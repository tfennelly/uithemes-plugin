/*
 * Copyright (C) 2014 CloudBees Inc.
 *
 * All rights reserved.
 */

var jqProxy = require('../../../jQuery');
var mvc = require('../../../mvc');
var timeout = require('../../../util/timeout');

var timeoutHandler = timeout.newTimeoutHandler("popovers");

exports.newPopover = function(name, popover, onElement, options) {
    return new Popover(name, popover, onElement, options);
}

exports.hide = function(popover) {
    if (!popover) {
        return;
    }

    var $ = jqProxy.getJQuery();

    if ($.isArray(popover)) {
        // The 'popover' arg is actually an array of popover names
        // that should be hidden.

        var activePopovers = $('.uit-popover');
        activePopovers.each(function () {
            var thePopover = $(this);
            var popoverName = thePopover.attr('popover-name');
            if (popover.indexOf(popoverName) !== -1) {
                exports.hide(thePopover);
            }
        });
        return;
    } else if (typeof popover === 'string') {
        exports.hide(popover.split(","));
        return;
    }

    if (popover.escPressListener) {
        $('body').unbind('keydown', popover.escPressListener);
        delete popover.escPressListener;
    }
    popover.remove();
}

function Popover(name, popover, onElement, options) {
    if (!name) {
        throw "Popover must be supplied with a 'name' param.";
    }
    if (options === undefined) {
        options = {
            placement: 'top'
        };
    }

    popover.attr('popover-name', name);
    popover.addClass(name);

    this.popover = popover;
    this.onElement = onElement;
    this.options = options;
}

Popover.prototype.show = function() {
    var $ = jqProxy.getJQuery();
    var theWindow = jqProxy.getWindow();
    var thisPopover = this;

    var notIf = thisPopover.options.notIf;
    if (notIf && isPopupVisible(notIf)) {
        // Don't show.
        return;
    }
    var hidePopups = thisPopover.options.hide;
    if (hidePopups) {
        exports.hide(hidePopups);
    }

    thisPopover.popover.addClass('uit-widget');
    thisPopover.popover.addClass('uit-popover');
    thisPopover.popover.addClass('placement-'+this.options.placement);
    
    $('body').append(thisPopover.popover);
    thisPopover.applyPlacement();

    // Reapply controllers...
    mvc.applyControllers(thisPopover.popover, true);

    // Handle removal of the popover
    if (this.popover.escPressListener) {
        $(theWindow).unbind('keydown', thisPopover.popover.escPressListener);
    }
    thisPopover.popover.escPressListener = function (keyPressEvent) {
        if(keyPressEvent.which === 27){
            thisPopover.hide();
        }
    }
    $('body').keydown(thisPopover.popover.escPressListener);
    $('.remove', thisPopover.popover).click(function() {
        thisPopover.hide();
    });

    if (thisPopover.options.onshow) {
        thisPopover.options.onshow();
        thisPopover.applyPlacement();
    }
}
Popover.prototype.hide = function() {
    exports.hide(this.popover);
}

Popover.prototype.click = function() {
    var $ = jqProxy.getJQuery();
    var docBody = $('body');
    var thisPopover = this;

    var eventName = 'click';
    if (thisPopover.options.namespace) {
        eventName += '.' + thisPopover.options.namespace;
        thisPopover.onElement.off(eventName);
    }

    thisPopover.onElement.on(eventName, function() {
        thisPopover.show();

        var boxCoords = jqProxy.getElementsEnclosingBoxCoords([thisPopover.onElement, thisPopover.popover]);
        function unbindListeners() {
            // We're done... stop listening for mouse move and keypress events and remove the popover.
            docBody.unbind('click', isOverElementListener);
            docBody.unbind('keydown', escPressListener);
            thisPopover.hide();
        }

        var isOverElementChecker = function (mouseX, mouseY) {
            if (!jqProxy.isCoordInBox(mouseX, mouseY, boxCoords)) {
                unbindListeners();
                return false;
            } else {
                return true;
            }
        };

        var isOverElementListener = function (moveEvent) {
            isOverElementChecker(moveEvent.pageX, moveEvent.pageY);
        }
        var escPressListener = function (keyPressEvent) {
            if(keyPressEvent.which === 27){
                unbindListeners();
            }
        }

        docBody.click(isOverElementListener);
        docBody.keydown(escPressListener);
        $('.remove', thisPopover.popover).click(function() {
            unbindListeners();
        });
    });
}

Popover.prototype.hover = function() {
    var $ = jqProxy.getJQuery();
    var mouseTracker = jqProxy.getMouseTracker();
    var thisPopover = this;

    var inoutDelay = thisPopover.options.inoutDelay;
    if (inoutDelay === undefined) {
        inoutDelay = 500;
    }

    // You can use the hover or mouse enter and leave events, but behavior gets interesting
    // when you have multiple popovers on top of each other, with mouse moves entering the "top"
    // one triggering movemove out events on lower ones etc.  Also, interesting things happen
    // just at the element boundaries.  Therefore, we use mouseenter and then mousemove.
    thisPopover.onElement.mouseenter(function() {
        // Don't start listening for mousemove events immediately.  This way, we "skip"
        // events that happen just at the boundary as the mouse is crossing it.
        timeoutHandler.setTimeout(function() {
            var onElementCoords = jqProxy.getElementBoxCoords(thisPopover.onElement);
            var docBody = $('body');

            // If the mouse is no longer in the box then we just return (i.e. ignore).
            if (!jqProxy.isCoordInBox(mouseTracker.x, mouseTracker.y, onElementCoords)) {
                // mouse not in the box... ignore...
                return;
            }

            // Show the popover now.
            thisPopover.show();

            var hoverBoxes = [onElementCoords];
            if (thisPopover.options.hoverBoth) {
                var popoverCoords = jqProxy.getElementBoxCoords(thisPopover.popover);

                hoverBoxes.push(popoverCoords);

                // We need to stretch the onElementCoords so the boxes touch.  Direction in which we stretch depends on the
                // placement of the popover..
                if (thisPopover.options.placement === 'left') {
                    // popover is to the left of the onElement => stretch onElement left to meet the right.x of the popover
                    jqProxy.stretchBoxCoords(onElementCoords, 'left', popoverCoords.topRight.x);
                } else if (thisPopover.options.placement === 'right') {
                    // popover is to the right of the onElement => stretch onElement right to meet the left.x of the popover
                    jqProxy.stretchBoxCoords(onElementCoords, 'right', popoverCoords.topLeft.x);
                } else if (thisPopover.options.placement === 'bottom') {
                    // popover is to the bottom of the onElement => stretch onElement down to meet the top.y of the popover
                    jqProxy.stretchBoxCoords(onElementCoords, 'down', popoverCoords.topLeft.y);
                } else {
                    // popover is to the top of the onElement => stretch onElement up to meet the bottom.y of the popover
                    jqProxy.stretchBoxCoords(onElementCoords, 'up', popoverCoords.bottomLeft.y);
                }
            }

            function remove() {
                docBody.unbind('mousemove', mouseMoveListener);
                thisPopover.hide();
            }

            function isMouseInHoverArea() {
                for (var i = 0; i < hoverBoxes.length; i++) {
                    if (jqProxy.isCoordInBox(mouseTracker.x, mouseTracker.y, hoverBoxes[i])) {
                        return true;
                    }
                }
                return false;
            }

            var removePopoverChecker = function (recheck) {
                if (!isMouseInHoverArea()) {
                    if (recheck) {
                        // Perform a recheck before removing the popover i.e. set a timeout
                        // to check are we still outside the hover area.  If at that time we still are
                        // outside, only then remove it. This allows the user to momentarily exit and
                        // re-enter the hover area without the hover disappearing.
                        timeoutHandler.setTimeout(function() {
                            removePopoverChecker(false);
                        }, Math.floor(inoutDelay * 0.8));
                        return;
                    }

                    // We're done... stop listening for mouse move events and remove the popover.
                    remove();
                }
            };

            var mouseMoveListener = function () {
                removePopoverChecker(true);
            }

            // If the mouse is still over the element (might not if the mouse just swiped over),
            // then track mouse moves as a way of deciding whether or not to hide the popover.
            if (isMouseInHoverArea()) {
                docBody.mousemove(mouseMoveListener);
                $('.remove', thisPopover.popover).click(function() {
                    remove();
                });
            }
        }, inoutDelay);
    });
}

Popover.prototype.applyPlacement = function() {
    var $ = jqProxy.getJQuery();
    var theWindow = jqProxy.getWindow();
    var thisPopover = this;
    var placement = thisPopover.options.placement;

    // If there's no anchor element or placement specified, we default
    // the placement to the center of the window.
    if (!thisPopover.onElement) {
        placement = 'window-center';
    } else if (!placement) {
        placement = 'top';
    }

    if (placement === 'right') {
        var onElementOffset = thisPopover.onElement.offset();
        thisPopover.popover.css({
            'top': onElementOffset.top,
            'left': onElementOffset.left + thisPopover.onElement.width() + 5
        });
    } else if (placement === 'left') {
        var onElementOffset = thisPopover.onElement.offset();
        thisPopover.popover.css({
            'top': onElementOffset.top,
            'left': onElementOffset.left - (thisPopover.popover.width() + 5)
        });
    } else if (placement === 'right-inside') {
        var onElementOffset = thisPopover.onElement.offset();
        thisPopover.popover.css({
            'top': onElementOffset.top + Math.round(parseFloat(thisPopover.onElement.css("border-top-width"))),
            'left': onElementOffset.left + thisPopover.onElement.width() - (thisPopover.popover.width() - Math.round(parseFloat(thisPopover.onElement.css("border-left-width"))))
        });
    } else if (placement === 'window-center') {
        var winWidth = $(theWindow).width();
        var winHeight = $(theWindow).height();
        var popoverWidth = thisPopover.popover.width();
        var popoverHeight = thisPopover.popover.height();

        var leftPlacement = ((winWidth - popoverWidth) / 2);
        var topPlacement = ((winHeight - popoverHeight) / 2);

        // try not have the top of the dialog further down from the
        // top thn 1/4 the window height.
        topPlacement = Math.min(topPlacement, (winWidth / 4));

        thisPopover.popover.css({
            'top': topPlacement,
            'left': leftPlacement
        });
    } else {
        // default is top
        var onElementOffset = thisPopover.onElement.offset();
        thisPopover.popover.css({
            'top': onElementOffset.top - (thisPopover.popover.height() + 5),
            'left': onElementOffset.left
        });
    }
}

function isPopupVisible(popupNames) {
    var $ = jqProxy.getJQuery();

    if ($.isArray(popupNames)) {
        var activePopovers = $('.uit-popover');
        var popoverVisible = false;
        activePopovers.each(function () {
            var thePopover = $(this);
            var popoverName = thePopover.attr('popover-name');
            if (popupNames.indexOf(popoverName) !== -1) {
                popoverVisible = true;
                return false; // terminate the loop
            }
        });
        return popoverVisible;
    } else if (typeof popupNames === 'string') {
        return isPopupVisible(popupNames.split(","));
    }

    return false;
}

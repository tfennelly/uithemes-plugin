/*
 * Copyright (C) 2014 CloudBees Inc.
 *
 * All rights reserved.
 */

var jqProxy = require('../jQuery');

/**
 * Lightweight MVC.
 */

var controllers = {};

/**
 * Register a controller.
 * @param controller The controller to be registered.
 */
exports.register = function (controller) {
    var controllerName = controller.getName();

    if (exports.isRegistered(controllerName)) {
        throw "A controller named '" + controllerName + "' is already registered.";
    }
    controllers[controllerName] = controller;
}

/**
 * Is the named controller already registered.
 * @param controllerName The name of the controller.
 * @return true if the controller is registered, otherwise false.
 */
exports.isRegistered = function (controllerName) {
    return controllers.hasOwnProperty(controllerName);
}

/**
 * Apply controllers.
 */
exports.applyControllers = function (onElement, allowReapply) {
    var targetEls = jqProxy.getWidgets(onElement);

    jqProxy.forEachElement(targetEls, function(targetEl) {
        if (allowReapply || !targetEl.hasClass('uit-controller-applied')) {
            var controllerName = targetEl.attr('uit-controller');

            if (controllerName) {
                var controller = controllers[controllerName];

                if (controller) {
                    var model = controller.getModel();
                    var view = controller.getView();
                    var mvcContext = new MVCContext(controllerName, targetEl);

                    targetEl.addClass('uit-widget');
                    targetEl.addClass('uit-controller-applied');
                    targetEl.addClass(controllerName);

                    model.getModelData.call(mvcContext, function (modelData) {
                        view.render.call(mvcContext, modelData, targetEl);
                    });
                } else {
                    console.error("No controller named '" + controllerName + "'.");
                }
            } else {
                console.error("'widget-element' must define 'controller'.");
            }
        }
    });
}

exports.newContext = function (controllerName, targetEl) {
    return new MVCContext(controllerName, targetEl);
}

function MVCContext(controllerName, targetEl) {
    if (!controllerName) {
        throw "No 'controllerName' supplied to MVCContext.";
    }
    if (!targetEl || targetEl.size() === 0) {
        throw "No 'targetEl' name supplied to MVCContext.";
    }

    this.targetEl = targetEl;
    this.controllerName = controllerName;
}

MVCContext.prototype.getControllerName = function() {
    return this.controllerName;
}

MVCContext.prototype.getTargetElement = function() {
    return this.targetEl;
}

MVCContext.prototype.attr = function(attributeName, defaultVal) {
    var attrVal = this.targetEl.attr(attributeName);
    if (attrVal) {
        return attrVal;
    }  else {
        return defaultVal;
    }
}

MVCContext.prototype.requiredAttr = function(attributeName) {
    var attrVal = this.targetEl.attr(attributeName);
    if (!attrVal) {
        throw "Required attribute '" +  attributeName + "' not defined on MVC controller '" + this.controllerName + "' element.";
    }
    return attrVal;
}

MVCContext.prototype.onModelChange = function(callback) {
    this.targetEl.on("cbwfModelChange", callback);
}

MVCContext.prototype.modelChange = function(modelData) {
    this.targetEl.trigger({type: "cbwfModelChange", modelData: modelData});
}

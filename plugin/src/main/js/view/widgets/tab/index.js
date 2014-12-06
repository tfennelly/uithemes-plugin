/*
 * Copyright (C) 2014 CloudBees Inc.
 *
 * All rights reserved.
 */

var jqProxy = require('../../../jQuery');

exports.activate = function(container) {
    var $ = jqProxy.getJQuery();
    var tabBar = $('.tab-bar', container);
    var tabContentFrame = $('.tab-content-frame', container);
    var tabs = $('.tab', tabBar);
    var tabContentBlocks = $('.tab-content', tabContentFrame);

    if (tabs.size() > 0) {
        function activate(tab) {
            var tabId = tab.attr('tab-id');

            tabs.removeClass('active');
            tabContentBlocks.removeClass('active');
            var tabIdSelector = '[tab-id="' + tabId + '"]';
            $('.tab' + tabIdSelector, tabBar).addClass('active');
            $('.tab-content' + tabIdSelector, tabContentFrame).addClass('active');
        }
        activate($(tabs.get(0)));

        tabs.click(function() {
            activate($(this));
        });
    } else {
        alert("Cannot activate a tab section. Failed to find a set of tabs.");
    }
}
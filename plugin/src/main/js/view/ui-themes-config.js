/*
 * Copyright (C) 2013 CloudBees Inc.
 *
 * All rights reserved.
 */

var templates = require('./templates');
var tab = require('./widgets/tab');
var jqProxy = require('../jQuery');

exports.render = function (modelData, onElement) {
    var $ = jqProxy.getJQuery();

    function _render(theModel) {
        //console.log(theModel.themes[0]);
        var uiThemesConfig = templates.apply('ui-themes-config', theModel);

        onElement.empty().append(uiThemesConfig);
        tab.activate(onElement);
        $('.selection', onElement).click(function () {
            var implSelector = $(this);
            var themeName = implSelector.attr('name');
            var themeImplName = implSelector.val();
            theModel.updateImplSelection(themeName, themeImplName);
        });
    }

    _render(modelData);
    this.onModelChange(function(event) {
        var activeTabId = tab.getActiveTabId(onElement);
        _render(event.modelData);
        tab.activateTab(activeTabId, onElement);
    });
}

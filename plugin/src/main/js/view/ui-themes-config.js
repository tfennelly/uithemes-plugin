/*
 * Copyright (C) 2013 CloudBees Inc.
 *
 * All rights reserved.
 */

var templates = require('./templates');
var tab = require('./widgets/tab');
var jqProxy = require('../jQuery');

exports.render = function (modelData, onElement) {
    console.log(modelData.themes[0]);
    var uiThemesConfig = templates.apply('ui-themes-config', modelData);
    onElement.empty().append(uiThemesConfig);

    tab.activate(onElement);

    var $ = jqProxy.getJQuery();
    $('.selection', onElement).click(function() {
        var implSelector = $(this);
        var themeName = implSelector.attr('name');
        var themeImplName = implSelector.val();

        modelData.updateImplSelection(themeName, themeImplName);
    });
}

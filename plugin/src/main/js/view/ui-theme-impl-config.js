/*
 * Copyright (C) 2013 CloudBees Inc.
 *
 * All rights reserved.
 */

var templates = require('./templates');
var jqProxy = require('../jQuery');
var colorPicker = require('./widgets/color-picker');

exports.render = function (modelData, onElement) {
    var $ = jqProxy.getJQuery();
    var uiThemeImplConfig = templates.apply('ui-theme-impl-config', modelData);

    onElement.empty().append(uiThemeImplConfig);

    // Add a color picker to all COLOR inputs.
    colorPicker.addColorPicker($('input.COLOR', uiThemeImplConfig));

    // Scrap all properties and save on pressing of the Save button.
    $('.save', uiThemeImplConfig).click(function() {
        var userConfig = {};
        $('.impl-config-value', uiThemeImplConfig).each(function() {
            var input = $(this);
            var name = input.attr('name');
            var value = input.val();
            userConfig[name] = value;
        });
        modelData.updateImplConfig(userConfig);
    });
}
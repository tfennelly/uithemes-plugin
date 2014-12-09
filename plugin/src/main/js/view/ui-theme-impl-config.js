/*
 * Copyright (C) 2013 CloudBees Inc.
 *
 * All rights reserved.
 */

var templates = require('./templates');
var jqProxy = require('../jQuery');

exports.render = function (modelData, onElement) {
    var uiThemeImplConfig = templates.apply('ui-theme-impl-config', modelData);
    onElement.empty().append(uiThemeImplConfig);

    var $ = jqProxy.getJQuery();
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
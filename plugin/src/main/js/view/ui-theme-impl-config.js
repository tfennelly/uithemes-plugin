/*
 * Copyright (C) 2013 CloudBees Inc.
 *
 * All rights reserved.
 */

var templates = require('./templates');
var jqProxy = require('../jQuery');

exports.render = function (modelData, onElement) {
    var uiThemeImplConfig = templates.apply('ui-theme-impl-config', modelData);
    //debugger;
    onElement.empty().append(uiThemeImplConfig);
}
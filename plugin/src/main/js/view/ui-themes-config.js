/*
 * Copyright (C) 2013 CloudBees Inc.
 *
 * All rights reserved.
 */

var templates = require('./templates');

exports.render = function (modelData, onElement) {
    console.log(modelData);
    var uiThemesConfig = templates.apply('ui-themes-config', modelData);
    onElement.empty().append(uiThemesConfig);
}

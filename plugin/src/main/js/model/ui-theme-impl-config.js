/*
 * Copyright (C) 2014 CloudBees Inc.
 *
 * All rights reserved.
 */

var restApi = require('./rest-api');
var jqProxy = require('../jQuery');

exports.getModelData = function (callback) {
    var mvcContext = this;
    var themesConfigMVCContext = mvcContext.getContext('ui-themes-config');

    if (!themesConfigMVCContext) {
        console.error("Failed to get MVCContext for 'ui-themes-config'.");
        return;
    }

    var themeName = mvcContext.requiredAttr('theme');
    themesConfigMVCContext.onModelChange(function(e) {
        mashupDataModel(themeName, e.modelData, function(mashedupModel) {
            mvcContext.modelChange(mashedupModel);
        });
    });

    mashupDataModel(themeName, themesConfigMVCContext.getModelData(), function(mashedupModel) {
        callback(mashedupModel);
    });
}

function getTheme(themeName, themesConfig) {
    var themes = themesConfig.themes;
    for (var i = 0; i < themes.length; i++) {
        var theme = themes[i];
        if (theme.name === themeName) {
            return theme;
        }
    }
}

function getSelection(theme) {
    for (var i = 0; i < theme.implementations.length; i++) {
        var implementation = theme.implementations[i];
        if (implementation.name === theme.implSelection) {
            return implementation;
        }
    }
    return undefined;
}

function mashupDataModel(themeName, themesConfig, callback) {
    var theme = getTheme(themeName, themesConfig);

    if (theme) {
        var selection = getSelection(theme);
        if (selection && selection.isConfigurable) {
            function getThemeImplConfig() {
                restApi.getThemeImplConfig("../", function (themeImplConfig) {
                    if (themeImplConfig.status === 'OK') {
                        var $ = jqProxy.getJQuery();

                        // clone the impl spec and use it as the user's impl config, mapping in
                        // the user configured values.
                        var userConfigSetObj = $.extend({}, selection.spec);
                        var userConfigSetArray = [];
                        for (var configName in userConfigSetObj) {
                            if (userConfigSetObj.hasOwnProperty(configName)) {
                                var userConfig = userConfigSetObj[configName];
                                var userConfigVal = themeImplConfig.data[configName];

                                userConfig.name = configName;
                                if (userConfigVal) {
                                    userConfig.value = userConfigVal;
                                } else {
                                    userConfig.value = userConfig.defaultValue;
                                }

                                if (!userConfig.title) {
                                    userConfig.title = userConfig.name;
                                }

                                userConfigSetArray.push(userConfig);
                            }
                        }

                        callback({
                            theme: theme,
                            selection: selection,
                            userConfig: userConfigSetArray,
                            isConfigurable: true
                        });
                    }
                }, theme.name, theme.implSelection);
            }

            if (selection.spec) {
                getThemeImplConfig();
            } else {
                restApi.getThemeImplSpec("../", function(themeImplSpec) {
                    if (themeImplSpec.status === 'OK') {
                        selection.spec = themeImplSpec.data.properties;
                        getThemeImplConfig();
                    }
                }, theme.name, theme.implSelection);
            }
        } else {
            callback({
                theme: theme,
                selection: selection,
                isConfigurable: false
            });
        }
    }
}
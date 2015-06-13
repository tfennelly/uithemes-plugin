/*
 * Copyright (C) 2013 CloudBees Inc.
 *
 * All rights reserved.
 */

var ajax = require('jenkins-js-util/ajax');

/**
 * UI Themes REST API
 */

var URL_BASE = 'uithemes-rest';

exports.getUserThemesConfig = function(userUrl, success) {
    ajax.invoker().asyncGET([userUrl, URL_BASE, "config"], success);
}

exports.putUserThemesConfig = function(userUrl, themesConfig, success) {
    ajax.invoker().syncPUT([userUrl, URL_BASE, "config"], themesConfig, success);
}

exports.getUserAvailableThemes = function(userUrl, success) {
    ajax.invoker().asyncGET([userUrl, URL_BASE, "themes"], success);
}

exports.getThemeImplSpec = function(userUrl, success, themeName, themeImplName) {
    ajax.invoker().asyncGET([userUrl, URL_BASE, "implspec"], success, {
        'theme-name': themeName,
        'theme-impl-name': themeImplName
    });
}

exports.getThemeImplConfig = function(userUrl, success, themeName, themeImplName) {
    ajax.invoker().asyncGET([userUrl, URL_BASE, "implconfig"], success, {
        'theme-name': themeName,
        'theme-impl-name': themeImplName
    });
}

exports.putThemeImplConfig = function(userUrl, themeImplConfig, success, themeName, themeImplName) {
    ajax.invoker().syncPUT([userUrl, URL_BASE, "implconfig"], themeImplConfig, success, {
        'theme-name': themeName,
        'theme-impl-name': themeImplName
    });
}

/*
 * Copyright (C) 2014 CloudBees Inc.
 *
 * All rights reserved.
 */

var restApi = require('./rest-api');

exports.getModelData = function (callback) {
    var mvcContext = this;
    var userUrl = mvcContext.requiredAttr("objectUrl");

    restApi.getUserThemesConfig(userUrl, function(userThemesConfig) {
        if (userThemesConfig.status === "OK") {
            restApi.getUserAvailableThemes(userUrl, function(availableThemes) {
                if (availableThemes.status === "OK") {
                    var model = mashupUserThemeData(availableThemes.data.themes, userThemesConfig.data.userThemes);
                    callback(availableThemes.data.themes);
                }
            });
        }
    });
}

function mashupUserThemeData(availableThemes, selections) {

}

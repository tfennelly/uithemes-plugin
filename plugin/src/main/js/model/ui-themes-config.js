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
                    var model = mashupUserThemeData(availableThemes.data.themes, userThemesConfig.data.userThemes, function() {
                        mvcContext.modelChange(model);
                    });
                    callback(model);
                }
            });
        }
    });
}

function mashupUserThemeData(availableThemes, selections, modelChange) {
    function getImplSelection(theme) {
        for (var i = 0; i < selections.length; i++) {
            if (selections[i].themeName === theme.name) {
                return selections[i];
            }
        }
        return undefined;
    }

    function mashupTheme(theme) {
        var selection = getImplSelection(theme);
        if (selection) {
            theme.implSelection = selection.implName;
        } else {
            theme.implSelection = theme.defaultImpl;
        }
        theme.updateImplSelection = function(newSelection) {
            theme.implSelection = newSelection;
            getImplSelection(theme).implName = newSelection;

            // TODO: save

            modelChange();
        }
        theme.getImplConfig = function() {

        };
    }

    for (var i = 0; i < availableThemes.length; i++) {
        mashupTheme(availableThemes[i]);
    }

    return availableThemes;
}

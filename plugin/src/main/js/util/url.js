/*
 * Copyright (C) 2013 CloudBees Inc.
 *
 * All rights reserved.
 */

exports.concatPathTokens = function (tokens) {
    if (typeof tokens === 'string') {
        return tokens;
    } else {
        var concatedString = '';
        for (var index = 0; index < tokens.length; index++) {
            if (index === 0) {
                concatedString += exports.trimTrailingSlashes(tokens[index]);
            } else if (index === tokens.length - 1) {
                concatedString += '/' + exports.trimLeadingSlashes(tokens[index]);
            } else {
                concatedString += '/' + exports.trimSlashes(tokens[index]);
            }
        }
        return concatedString;
    }
}

exports.trimLeadingSlashes = function (string) {
    return string.replace(/^\/+/g, '');
}
exports.trimTrailingSlashes = function (string) {
    return string.replace(/\/+$/g, '');
}
exports.trimSlashes = function (string) {
    return exports.trimLeadingSlashes(exports.trimTrailingSlashes(string));
}
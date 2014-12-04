/*
 * Copyright (C) 2014 CloudBees Inc.
 *
 * All rights reserved.
 */

var url = require('./url');
var jqProxy = require('../jQuery');

exports.execAsyncGET = function (resPathTokens, success, params) {
    var $ = jqProxy.getJQuery();

    $.ajax({
        url: url.concatPathTokens(resPathTokens),
        type: 'get',
        dataType: 'json',
        data: params,
        success: success
    });
}

exports.jenkinsAjaxGET = function (path, success) {
    new Ajax.Request(path, {
        method : 'get',
        onSuccess: success
    });
}

exports.jenkinsAjaxPOST = function (path, success) {
    new Ajax.Request(path, {
        method : 'post',
        onSuccess: success
    });
}
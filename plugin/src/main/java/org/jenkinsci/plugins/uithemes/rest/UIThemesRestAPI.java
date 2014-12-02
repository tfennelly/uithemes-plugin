/*
 * The MIT License
 *
 * Copyright (c) 2013-2014, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.uithemes.rest;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.TransientUserActionFactory;
import hudson.model.User;
import org.jenkinsci.plugins.uithemes.UIThemesPlugin;
import org.jenkinsci.plugins.uithemes.UIThemesProcessor;
import org.jenkinsci.plugins.uithemes.model.UIThemeSet;
import org.jenkinsci.plugins.uithemes.model.UserUIThemeConfiguration;
import org.jenkinsci.plugins.uithemes.rest.model.StatusResponse;
import org.jenkinsci.plugins.uithemes.rest.model.UIThemeList;
import org.jenkinsci.plugins.uithemes.util.JSONReadWrite;
import org.jenkinsci.plugins.uithemes.util.JenkinsUtil;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@Extension
public class UIThemesRestAPI extends TransientUserActionFactory implements Action {

    public static final String URL_BASE = "uithemes-rest";

    private User user;
    private File userHome;
    private UIThemesPlugin pluginInstance;

    public String getUrlName() {
        return URL_BASE;
    }

    public final HttpResponse doConfig(StaplerRequest req) {
        String method = req.getMethod().toUpperCase();

        try {
            if (method.equals("GET")) {
                UserUIThemeConfiguration themeConfiguration = UserUIThemeConfiguration.fromUserHome(user);
                if (themeConfiguration == null) {
                    themeConfiguration = new UserUIThemeConfiguration();
                }
                return new JSONResponse(themeConfiguration);
            } else if (method.equals("PUT")) {
                UserUIThemeConfiguration themeConfiguration = JSONReadWrite.fromRequest(req, UserUIThemeConfiguration.class);
                UserUIThemeConfiguration.toUserHome(user, themeConfiguration);
                return new JSONResponse(StatusResponse.OK());
            } else {
                return new JSONResponse(StatusResponse.ERROR(String.format("Unsupported '%s' operation.", method)));
            }
        } catch (Exception e) {
            return new JSONResponse(StatusResponse.ERROR(e));
        }
    }

    public final HttpResponse doThemes(StaplerRequest req) {
        String method = req.getMethod().toUpperCase();

        try {
            if (method.equals("GET")) {
                UIThemesPlugin plugin = getPlugin();
                UIThemesProcessor themeProcessor = plugin.getThemesProcessor();
                UIThemeSet themeSet = themeProcessor.getUiThemeSet(userHome);

                return new JSONResponse(UIThemeList.fromInternal(themeSet));
            } else {
                return new JSONResponse(StatusResponse.ERROR(String.format("Unsupported '%s' operation.", method)));
            }
        } catch (Exception e) {
            return new JSONResponse(StatusResponse.ERROR(e));
        }
    }

    private UIThemesPlugin getPlugin() {
        if (pluginInstance == null) {
            pluginInstance = UIThemesPlugin.getInstance();
        }
        return pluginInstance;
    }

    @Override
    public Collection<? extends Action> createFor(User target) {
        user = target;
        userHome = JenkinsUtil.getJenkinsUserHome(user);
        return Collections.singletonList(this);
    }

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return null;
    }
}
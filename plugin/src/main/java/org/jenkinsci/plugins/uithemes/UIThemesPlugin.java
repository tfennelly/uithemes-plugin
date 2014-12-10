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
package org.jenkinsci.plugins.uithemes;

import hudson.Plugin;
import hudson.PluginManager;
import hudson.PluginWrapper;
import hudson.model.User;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.uithemes.model.UIThemeSet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UIThemesPlugin extends Plugin implements UIThemesProcessor {

    private static final Logger LOGGER = Logger.getLogger(UIThemesPlugin.class.getName());

    protected UIThemesProcessor themesProcessor;

    @Override
    public void postInitialize() throws Exception {
        themesProcessor = newProcessor();
        addPluginContributors();
    }

    protected UIThemesProcessor newProcessor() {
        return new UIThemesProcessorImpl();
    }

    public UIThemesProcessor getThemesProcessor() {
        return themesProcessor;
    }

    public PluginManager getPluginManager() {
        return getWrapper().parent;
    }

    private void addPluginContributors() throws IOException {
        List<PluginWrapper> plugins = getPluginManager().getPlugins();

        for (PluginWrapper pluginWrapper : plugins) {
            Plugin plugin = pluginWrapper.getPlugin();
            if (plugin instanceof UIThemeContributor) {
                addContributor((UIThemeContributor) plugin);
            }
        }

        themesProcessor.deleteAllUserThemes();
    }

    @Override
    public UIThemesProcessor addContributor(UIThemeContributor themeContributor) {
        return themesProcessor.addContributor(themeContributor);
    }

    @Override
    public UIThemesProcessor removeContributor(UIThemeContributor themeContributor) {
        return themesProcessor.removeContributor(themeContributor);
    }

    @Override
    public File getUserThemesCSS(User user) throws IOException {
        return themesProcessor.getUserThemesCSS(user);
    }

    @Override
    public void deleteAllUserThemes() throws IOException {
        themesProcessor.deleteAllUserThemes();
    }

    @Override
    public UIThemeSet getUiThemeSet() {
        return themesProcessor.getUiThemeSet();
    }

    public static UIThemesPlugin getInstance() {
        PluginManager pluginManager = Jenkins.getInstance().getPluginManager();
        PluginWrapper uiThemesPlugin = pluginManager.getPlugin("uithemes");
        if (uiThemesPlugin != null) {
            return (UIThemesPlugin) uiThemesPlugin.getPlugin();
        } else {
            LOGGER.log(Level.WARNING, "Error rebuilding plugin styles.  Failed to find a plugin named 'uithemes'.");
            return null;
        }
    }
}

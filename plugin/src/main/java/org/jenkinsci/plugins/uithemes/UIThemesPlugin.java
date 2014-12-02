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
import jenkins.model.Jenkins;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UIThemesPlugin extends Plugin {

    private static final Logger LOGGER = Logger.getLogger(UIThemesPlugin.class.getName());

    protected UIThemesProcessor themesProcessor;

    @Override
    public void postInitialize() throws Exception {
        themesProcessor = newProcessor();
        addThemeContributors();
    }

    protected UIThemesProcessor newProcessor() {
        return new UIThemesProcessor();
    }

    public UIThemesProcessor getThemesProcessor() {
        return themesProcessor;
    }

    public PluginManager getPluginManager() {
        return getWrapper().parent;
    }

    private void addThemeContributors() throws IOException {
        List<PluginWrapper> plugins = getPluginManager().getPlugins();

        for (PluginWrapper pluginWrapper : plugins) {
            Plugin plugin = pluginWrapper.getPlugin();
            if (plugin instanceof UIThemeContributor) {
                addThemeContributor((UIThemeContributor) plugin);
            }
        }

        themesProcessor.deleteAllUserThemes();
    }

    public void addThemeContributor(UIThemeContributor themeContributor) {
        themesProcessor.addContributor(themeContributor);
    }

    public void removeThemeContributor(UIThemeContributor themeContributor) {
        themesProcessor.removeContributor(themeContributor);
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

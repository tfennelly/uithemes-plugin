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
import org.jenkins.ui.icon.Icon;
import org.jenkins.ui.icon.IconSet;
import org.jenkinsci.plugins.uithemes.model.UIThemeContribution;
import org.jenkinsci.plugins.uithemes.model.UIThemeSet;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UIThemesPlugin extends Plugin implements UIThemeContributor {

    protected UIThemesProcessor themesProcessor;

    @Override
    public void postInitialize() throws Exception {
        // Register the icon definition to the IconSet
        IconSet.icons.addIcon(new Icon("icon-uithemes icon-md", Icon.ICON_MEDIUM_STYLE));

        themesProcessor = UIThemesProcessor.getInstance();
        if (themesProcessor != null) {
            addPluginContributors();
        }
    }

    public PluginManager getPluginManager() {
        return getWrapper().parent;
    }

    private void addPluginContributors() throws IOException {
        PluginManager pluginManager = getPluginManager();
        List<PluginWrapper> plugins = pluginManager.getPlugins();

        for (PluginWrapper pluginWrapper : plugins) {
            Plugin plugin = pluginWrapper.getPlugin();
            if (plugin instanceof UIThemeContributor) {
                addContributor((UIThemeContributor) plugin);
            }
        }

        themesProcessor.deleteAllUserThemes();
    }

    public UIThemesProcessor addContributor(UIThemeContributor themeContributor) {
        return themesProcessor.addContributor(themeContributor);
    }

    public UIThemesProcessor removeContributor(UIThemeContributor themeContributor) {
        return themesProcessor.removeContributor(themeContributor);
    }

    @Override
    public void contribute(UIThemeSet themeSet) {
        // See src/main/resources/jenkins-themes/icons/default/default-icons-uithemes/theme-template.less
        themeSet.contribute(new UIThemeContribution("default-icons-uithemes", "icons", "default", UIThemesPlugin.class));
    }
}

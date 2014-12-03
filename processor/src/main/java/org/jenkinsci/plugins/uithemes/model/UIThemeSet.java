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
package org.jenkinsci.plugins.uithemes.model;

import org.jenkinsci.plugins.uithemes.UIThemesProcessor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UI Theme Set.
 * <p/>
 * See <a href="README.md">README.md</a>.
 * <p/>
 * A set of themes that "could" be applied for a user. The {@link UIThemesProcessor} uses this set to
 * select the actual theme implementations to be applied, based on the users theme selection config.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UIThemeSet {

    private static final Logger LOGGER = Logger.getLogger(UIThemeSet.class.getName());

    private Map<String, UITheme> themes = new LinkedHashMap<String, UITheme>();

    public UITheme registerTheme(String name, String description) {
        UITheme theme = getTheme(name);
        if (theme != null) {
            return theme;
        }
        theme = new UITheme(name, description);
        themes.put(name, theme);
        return theme;
    }

    public Set<String> getThemeNames() {
        return themes.keySet();
    }

    public Set<String> getThemeImplNames(String themeName) {
        UITheme theme = getTheme(themeName);
        if (theme != null) {
            return theme.getThemeImplNames();
        } else {
            return Collections.emptySet();
        }
    }

    public UITheme getTheme(String name) {
        return themes.get(name);
    }

    public UIThemeImplementation registerThemeImpl(String themeName, String themeImplName, String themeImplDescription) {
        UITheme theme = getTheme(themeName);
        if (theme == null) {
            LOGGER.log(Level.WARNING, "Theme ''{0}'' is not registered. Cannot register implementation ''{1}''.", new String [] {themeName, themeImplName});
        }
        UIThemeImplementation impl = new UIThemeImplementation(themeName, themeImplName, themeImplDescription);
        theme.registerImpl(impl);
        return impl;
    }

    public boolean contribute(UIThemeContribution contribution) {
        UITheme theme = getTheme(contribution.getThemeName());
        if (theme != null) {
            LOGGER.log(Level.FINE, "Theme ''{0}'' has received contribution to implementation ''{1}''.", new String [] {contribution.getThemeName(), contribution.getThemeImplName()});
            return theme.contribute(contribution);
        } else {
            LOGGER.log(Level.WARNING, "Theme ''{0}'' is not registered. Cannot contribute to implementation ''{1}''.", new String [] {contribution.getThemeName(), contribution.getThemeImplName()});
            return false;
        }
    }

    public List<UIThemeContribution> getThemeImplContributions(String themeName, String themeImplName) {
        UITheme theme = getTheme(themeName);
        if (theme != null) {
            return theme.getThemeImplContributions(themeImplName);
        }
        return Collections.emptyList();
    }
}

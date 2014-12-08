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
package org.jenkinsci.plugins.uithemestest;

import hudson.Plugin;
import org.jenkinsci.plugins.uithemes.UIThemeContributor;
import org.jenkinsci.plugins.uithemes.model.UIThemeImplSpec;
import org.jenkinsci.plugins.uithemes.model.UIThemeImplSpecProperty;
import org.jenkinsci.plugins.uithemes.model.UIThemeSet;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class PluginImpl extends Plugin implements UIThemeContributor {

    @Override
    public void contribute(UIThemeSet themeSet) {
        // What we do in this code here would normally be spread out across multiple contributors and Jenkins core itself.

        // Register a few themes.
        themeSet.registerTheme("icon", "Icons", "The set of Icons used by Jenkins");
        themeSet.registerTheme("status-balls", "Status Balls/Orbs", "The set of Status Balls/Orbs used by Jenkins");
        themeSet.registerTheme("header", "Page Header", "The Jenkins page header styles");
        themeSet.registerTheme("console", "Console", "The Console/Terminal styles");

        // register some theme implementations
        themeSet.registerThemeImpl("icon",          "default", "Default", "Classic Jenkins Icon Set");
        themeSet.registerThemeImpl("icon",          "font-awesome", "Font Awesome", "<a href='http://fortawesome.github.io/Font-Awesome/'>Font Awesome</a> vector Icons");

        themeSet.registerThemeImpl("status-balls",  "default", "Default")
                .setThemeImplSpec(
                        new UIThemeImplSpec()
                                .addProperty("size", new UIThemeImplSpecProperty().setType(UIThemeImplSpecProperty.Type.NUMBER).setDefaultValue("24"))
                                .addProperty("bgColor", new UIThemeImplSpecProperty().setType(UIThemeImplSpecProperty.Type.COLOR).setDefaultValue("#CCC"))
                );
        themeSet.registerThemeImpl("status-balls",  "css3-animated", "CSS3 Animated")
                .setThemeImplSpec(
                        new UIThemeImplSpec()
                                .addProperty("size", new UIThemeImplSpecProperty().setType(UIThemeImplSpecProperty.Type.NUMBER).setDefaultValue("24"))
                                .addProperty("bgColor", new UIThemeImplSpecProperty().setType(UIThemeImplSpecProperty.Type.COLOR).setDefaultValue("#CCC"))
                );

        themeSet.registerThemeImpl("header",        "default", "Default")
                .setThemeImplSpec(
                        new UIThemeImplSpec()
                                .addProperty("bgColor", new UIThemeImplSpecProperty().setType(UIThemeImplSpecProperty.Type.COLOR).setDefaultValue("#BBB"))
                                .addProperty("logo", new UIThemeImplSpecProperty().setDefaultValue("images/butler.png"))
                );
        themeSet.registerThemeImpl("console",       "default", "Default")
                .setThemeImplSpec(
                        new UIThemeImplSpec()
                                .addProperty("bgColor", new UIThemeImplSpecProperty().setType(UIThemeImplSpecProperty.Type.COLOR).setDefaultValue("#AAA"))
                );
    }
}

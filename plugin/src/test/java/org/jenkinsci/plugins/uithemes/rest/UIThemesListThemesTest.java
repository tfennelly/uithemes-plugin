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

import hudson.model.User;
import org.jenkinsci.plugins.uithemes.TestUtil;
import org.jenkinsci.plugins.uithemes.UIThemeContributor;
import org.jenkinsci.plugins.uithemes.UIThemesPlugin;
import org.jenkinsci.plugins.uithemes.less.URLResource;
import org.jenkinsci.plugins.uithemes.model.UIThemeContribution;
import org.jenkinsci.plugins.uithemes.model.UIThemeImplSpec;
import org.jenkinsci.plugins.uithemes.model.UIThemeImplSpecProperty;
import org.jenkinsci.plugins.uithemes.model.UIThemeImplementation;
import org.jenkinsci.plugins.uithemes.model.UIThemeSet;
import org.jenkinsci.plugins.uithemes.rest.model.StatusResponse;
import org.jenkinsci.plugins.uithemes.rest.model.UIThemeList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UIThemesListThemesTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    private static URLResource EMPTY_RES = new URLResource("/less/empty.less");
    private MockUIThemeContributor icon_default;
    private MockUIThemeContributor icon_font_awesome;
    private MockUIThemeContributor status_balls_default;
    private MockUIThemeContributor status_balls_doony;
    private MockUIThemeContributor status_balls_css3;
    private MockUIThemeContributor header_default;
    private MockUIThemeContributor header_lite;

    @Before
    public void before() throws NoSuchMethodException, IOException {
        icon_default = new MockUIThemeContributor("icon", "default", EMPTY_RES);
        icon_font_awesome = new MockUIThemeContributor("icon", "font-awesome", EMPTY_RES);
        status_balls_default = new MockUIThemeContributor("status-balls", "default", EMPTY_RES);
        status_balls_doony = new MockUIThemeContributor("status-balls", "doony-balls", EMPTY_RES);
        status_balls_css3 = new MockUIThemeContributor("status-balls", "css3-animated", EMPTY_RES);
        header_default = new MockUIThemeContributor("header", "default", EMPTY_RES);
        header_lite = new MockUIThemeContributor("header", "lite", EMPTY_RES);

        header_lite.setThemeImplSpec(new UIThemeImplSpec());
        UIThemeImplSpecProperty backgroundColor = new UIThemeImplSpecProperty();
        backgroundColor.type = UIThemeImplSpecProperty.Type.COLOR;
        backgroundColor.defaultValue = "#FFF";
        header_lite.themeImplSpec.properties.put("backgroundColor", backgroundColor);
    }

    @Test
    public void test_GET_themes() throws Exception {
        // create a user
        User.get("tfennelly", true, Collections.emptyMap());

        // No themes registered...
        StatusResponse response = TestUtil.getJSON("user/tfennelly/uithemes-rest/themes", StatusResponse.class, jenkinsRule);
        Assert.assertEquals("OK", response.status);
        UIThemeList uiThemes = response.dataTo(UIThemeList.class);
        Assert.assertEquals(0, uiThemes.themes.size());
        Assert.assertEquals(0, uiThemes.themes.size());

        // register a few theme contributors + implementations
        UIThemesPlugin plugin = UIThemesPlugin.getInstance();
        plugin.addThemeContributor(icon_default);
        plugin.addThemeContributor(icon_font_awesome);
        plugin.addThemeContributor(status_balls_default);
        plugin.addThemeContributor(status_balls_doony);
        plugin.addThemeContributor(status_balls_css3);
        plugin.addThemeContributor(header_default);
        plugin.addThemeContributor(header_lite);

        // fetch them again...
        response = TestUtil.getJSON("user/tfennelly/uithemes-rest/themes", StatusResponse.class, jenkinsRule);
        Assert.assertEquals("OK", response.status);
        uiThemes = response.dataTo(UIThemeList.class);

        // should be 3 themes
        Assert.assertEquals(3, uiThemes.themes.size());
        Assert.assertEquals("icon", uiThemes.themes.get(0).name);
        Assert.assertEquals("Icons", uiThemes.themes.get(0).description);
        Assert.assertEquals("default", uiThemes.themes.get(0).defaultImpl);
        Assert.assertEquals("[default, font-awesome]", uiThemes.themes.get(0).implementations.toString());
        Assert.assertEquals("status-balls", uiThemes.themes.get(1).name);
        Assert.assertEquals("Status Balls", uiThemes.themes.get(1).description);
        Assert.assertEquals("default", uiThemes.themes.get(1).defaultImpl);
        Assert.assertEquals("[default, doony-balls, css3-animated]", uiThemes.themes.get(1).implementations.toString());
        Assert.assertEquals("header", uiThemes.themes.get(2).name);
        Assert.assertEquals("Page Header", uiThemes.themes.get(2).description);
        Assert.assertEquals("default", uiThemes.themes.get(2).defaultImpl);
        Assert.assertEquals("[default, lite]", uiThemes.themes.get(2).implementations.toString());

        // The "lite" header theme should be configurable. See @Before method above.
        Assert.assertEquals(2, uiThemes.themes.get(2).implementations.size());
        Assert.assertEquals(false, uiThemes.themes.get(2).implementations.get(0).isConfigurable);
        Assert.assertEquals(true, uiThemes.themes.get(2).implementations.get(1).isConfigurable);
    }

    @Test
    public void test_GET_theme_impl_spec_bad_params() throws Exception {
        // create a user
        User.get("tfennelly", true, Collections.emptyMap());

        UIThemesPlugin plugin = UIThemesPlugin.getInstance();
        plugin.addThemeContributor(icon_default);
        plugin.addThemeContributor(icon_font_awesome);
        plugin.addThemeContributor(status_balls_default);
        plugin.addThemeContributor(status_balls_doony);
        plugin.addThemeContributor(status_balls_css3);
        plugin.addThemeContributor(header_default);
        plugin.addThemeContributor(header_lite);

        // Missing parameters should result in errors
        StatusResponse response = TestUtil.getJSON("user/tfennelly/uithemes-rest/implspec", StatusResponse.class, jenkinsRule);
        Assert.assertEquals("ERROR", response.status);
        Assert.assertEquals("Request parameter 'theme-name' is required.", response.message);
        response = TestUtil.getJSON("user/tfennelly/uithemes-rest/implspec?theme-name=header", StatusResponse.class, jenkinsRule);
        Assert.assertEquals("ERROR", response.status);
        Assert.assertEquals("Request parameter 'theme-impl-name' is required.", response.message);

        // unknown theme name should cause an error
        response = TestUtil.getJSON("user/tfennelly/uithemes-rest/implspec?theme-name=XXX&theme-impl-name=lite", StatusResponse.class, jenkinsRule);
        Assert.assertEquals("ERROR", response.status);
        Assert.assertEquals("Unknown theme 'XXX'.", response.message);

        // unknown theme impl name should cause an error
        response = TestUtil.getJSON("user/tfennelly/uithemes-rest/implspec?theme-name=header&theme-impl-name=XXX", StatusResponse.class, jenkinsRule);
        Assert.assertEquals("ERROR", response.status);
        Assert.assertEquals("Unknown theme implementation 'XXX' on theme named 'header'.", response.message);

        // A theme impl that does not specify a spec should cause an error
        response = TestUtil.getJSON("user/tfennelly/uithemes-rest/implspec?theme-name=header&theme-impl-name=default", StatusResponse.class, jenkinsRule);
        Assert.assertEquals("ERROR", response.status);
        Assert.assertEquals("Theme implementation 'default:header' does not specify an implementation spec i.e. it is not configurable.", response.message);
    }

    @Test
    public void test_GET_theme_impl_spec() throws Exception {
        // create a user
        User.get("tfennelly", true, Collections.emptyMap());

        UIThemesPlugin plugin = UIThemesPlugin.getInstance();
        plugin.addThemeContributor(icon_default);
        plugin.addThemeContributor(icon_font_awesome);
        plugin.addThemeContributor(status_balls_default);
        plugin.addThemeContributor(status_balls_doony);
        plugin.addThemeContributor(status_balls_css3);
        plugin.addThemeContributor(header_default);
        plugin.addThemeContributor(header_lite);

        StatusResponse response = TestUtil.getJSON("user/tfennelly/uithemes-rest/implspec?theme-name=header&theme-impl-name=lite", StatusResponse.class, jenkinsRule);
        Assert.assertEquals("OK", response.status);
        UIThemeImplSpec implSpec = response.dataTo(UIThemeImplSpec.class);
        UIThemeImplSpecProperty bgColor = implSpec.properties.get("backgroundColor");
        Assert.assertNotNull(bgColor);
        Assert.assertEquals(UIThemeImplSpecProperty.Type.COLOR, bgColor.type);
        Assert.assertEquals("#FFF", bgColor.defaultValue);
    }

    private class MockUIThemeContributor implements UIThemeContributor {
        private UIThemeContribution contribution;
        private UIThemeImplSpec themeImplSpec;

        private MockUIThemeContributor(String themeName, String themeImplName, URLResource lessResource) {
            this.contribution = new UIThemeContribution(themeName, themeImplName, lessResource);
        }
        private MockUIThemeContributor setThemeImplSpec(UIThemeImplSpec themeImplSpec) {
            this.themeImplSpec = themeImplSpec;
            return this;
        }
        @Override
        public void contribute(UIThemeSet themeSet, File userDir) {
            if (themeSet.getThemeNames().isEmpty()) {
                themeSet.registerTheme(icon_default.contribution.getThemeName(), "Icons");
                themeSet.registerTheme(status_balls_default.contribution.getThemeName(), "Status Balls");
                themeSet.registerTheme(header_default.contribution.getThemeName(), "Page Header");
            }
            UIThemeImplementation impl = themeSet.registerThemeImpl(contribution.getThemeName(), contribution.getThemeImplName(), contribution.getThemeImplName());
            impl.setThemeImplSpec(themeImplSpec);
            themeSet.contribute(contribution);
        }
    }
}

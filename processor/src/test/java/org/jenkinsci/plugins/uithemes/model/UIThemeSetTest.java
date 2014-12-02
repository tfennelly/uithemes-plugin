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

import org.jenkinsci.plugins.uithemes.less.URLResource;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UIThemeSetTest {

    @Test
    public void test() {
        UIThemeSet uiThemeSet = new UIThemeSet();
        UIThemeContribution classicIcons = new UIThemeContribution("icon", "classic", new URLResource("./icons/classic-base.less"));

        // Should return false if the theme is not registered.
        Assert.assertFalse(uiThemeSet.contribute(classicIcons));

        uiThemeSet.registerTheme("icon", "Jenkins Icon Theme");
        Assert.assertEquals("[icon]", uiThemeSet.getThemeNames().toString());

        // Should still return false because the theme implementation is not registered.
        Assert.assertFalse(uiThemeSet.contribute(classicIcons));

        // Register the "classic" implementation of the icons theme
        uiThemeSet.registerThemeImpl("icon", "classic", "Classic Jenkins Icons");
        Assert.assertEquals("[classic]", uiThemeSet.getThemeImplNames("icon").toString());

        // Should now register the classic icon contribution.
        Assert.assertTrue(uiThemeSet.contribute(classicIcons));

        // make another contribution to the classic icons
        Assert.assertTrue(uiThemeSet.contribute(new UIThemeContribution("icon", "classic", new URLResource("./icons/classic-some-other-styles.less"))));

        Assert.assertEquals("[./icons/classic-base.less, ./icons/classic-some-other-styles.less]", uiThemeSet.getThemeImplContributions("icon", "classic").toString());

        // Add another implementation for the icons theme...

        uiThemeSet.registerThemeImpl("icon", "font-awesome", "FontAwesome Jenkins Icons");
        Assert.assertEquals("[classic, font-awesome]", uiThemeSet.getThemeImplNames("icon").toString());
        Assert.assertEquals("[]", uiThemeSet.getThemeImplContributions("icon", "font-awesome").toString());

        uiThemeSet.contribute(new UIThemeContribution("icon", "font-awesome", new URLResource("./icons/font-awesome.less")));
        Assert.assertEquals("[./icons/font-awesome.less]", uiThemeSet.getThemeImplContributions("icon", "font-awesome").toString());

        Assert.assertTrue(uiThemeSet.contribute(classicIcons));
    }
}

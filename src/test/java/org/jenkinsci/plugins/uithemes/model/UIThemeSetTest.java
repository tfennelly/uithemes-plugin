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
        UIThemeSet registry = new UIThemeSet();
        UIThemeContribution classicIcons = new UIThemeContribution("icon", "classic", new URLResource("./icons/classic-base.less"));

        // Should return false if the theme is not registered.
        Assert.assertFalse(registry.contribute(classicIcons));

        registry.registerTheme("icon", "Jenkins Icon Theme");
        Assert.assertEquals("[icon]", registry.getThemeNames().toString());

        // Should still return false because the theme implementation is not registered.
        Assert.assertFalse(registry.contribute(classicIcons));

        // Register the "classic" implementation of the icons theme
        registry.registerThemeImpl("icon", "classic", "Classic Jenkins Icons");
        Assert.assertEquals("[classic]", registry.getThemeImplNames("icon").toString());

        // Should now register the classic icon contribution.
        Assert.assertTrue(registry.contribute(classicIcons));

        // make another contribution to the classic icons
        Assert.assertTrue(registry.contribute(new UIThemeContribution("icon", "classic", new URLResource("./icons/classic-some-other-styles.less"))));

        Assert.assertEquals("[./icons/classic-base.less, ./icons/classic-some-other-styles.less]", registry.getThemeImplContributions("icon", "classic").toString());

        // Add another implementation for the icons theme...

        registry.registerThemeImpl("icon", "font-awesome", "FontAwesome Jenkins Icons");
        Assert.assertEquals("[classic, font-awesome]", registry.getThemeImplNames("icon").toString());
        Assert.assertEquals("[]", registry.getThemeImplContributions("icon", "font-awesome").toString());

        registry.contribute(new UIThemeContribution("icon", "font-awesome", new URLResource("./icons/font-awesome.less")));
        Assert.assertEquals("[./icons/font-awesome.less]", registry.getThemeImplContributions("icon", "font-awesome").toString());

        Assert.assertTrue(registry.contribute(classicIcons));
    }
}

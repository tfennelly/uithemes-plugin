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

import org.jenkinsci.plugins.uithemes.model.UIThemeImplSpec;
import org.jenkinsci.plugins.uithemes.model.UIThemeImplSpecProperty;
import org.jenkinsci.plugins.uithemes.util.JSONReadWrite;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UIThemeImplSpecTest {

    @Test
    public void test() throws IOException {
        UIThemeImplSpec specIn = new UIThemeImplSpec();

        UIThemeImplSpecProperty backgroundColor = new UIThemeImplSpecProperty();
        backgroundColor.type = UIThemeImplSpecProperty.Type.COLOR;
        specIn.properties.put("backgroundColor", backgroundColor);

        UIThemeImplSpecProperty visibility = new UIThemeImplSpecProperty();
        visibility.permittedValues = new String[] {"visible", "hidden"};
        specIn.properties.put("visibility", visibility);

        String serlialized = JSONReadWrite.toString(specIn);
        // System.out.println(serlialized);

        UIThemeImplSpec specOut = JSONReadWrite.fromString(serlialized, UIThemeImplSpec.class);
        // System.out.println(JSONReadWrite.toString(specOut));
        Assert.assertEquals(2, specOut.properties.size());
        Assert.assertEquals(UIThemeImplSpecProperty.Type.COLOR, specOut.properties.get("backgroundColor").type);
        Assert.assertEquals(UIThemeImplSpecProperty.Type.STRING, specOut.properties.get("visibility").type);
        Assert.assertEquals("[visible, hidden]", Arrays.asList(specOut.properties.get("visibility").permittedValues).toString());
    }
}

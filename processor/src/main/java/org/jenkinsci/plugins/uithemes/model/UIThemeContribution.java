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

import javax.xml.namespace.QName;

/**
 * UI Theme Contribution.
 * <p/>
 * See <a href="README.md">README.md</a>.
 * <p/>
 * A given theme "implementation" ({@link UIThemeImplementation}) can have multiple resources
 * "contributed" to it by Jenkins core (and/or 0+ plugins). If you think about the "icon" theme
 * and it's "classic" implementation, Jenkins core makes a theme resource contribution that defines
 * the core set of "classic" icons, but each of the plugins can also contribute to that same
 * theme implementation. The same obviously applies for other "icon" themes might be created
 * e.g. a "font-awesome" theme.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UIThemeContribution {

    private String themeName;
    private String themeImplName;
    private URLResource lessResource;

    // TODO: Maybe support Javascript contributions?

    public UIThemeContribution(String themeName, String themeImplName, URLResource lessResource) {
        this.themeName = themeName;
        this.themeImplName = themeImplName;
        this.lessResource = lessResource;
    }

    public String getThemeName() {
        return themeName;
    }

    public String getThemeImplName() {
        return themeImplName;
    }

    public URLResource getLessResource() {
        return lessResource;
    }

    public QName getQName() {
        return new QName(themeName + ":" + themeImplName, lessResource.toString());
    }

    @Override
    public String toString() {
        return lessResource.toString();
    }
}

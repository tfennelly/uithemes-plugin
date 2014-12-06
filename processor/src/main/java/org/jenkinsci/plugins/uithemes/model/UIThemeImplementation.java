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

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * UI Theme Implementation.
 * <p/>
 * See <a href="README.md">README.md</a>.
 * <p/>
 * This class represents a single implementation of a ui theme e.g. a "classic" implementation
 * of an "icon" theme. An implementation can have multiple resources {@link UIThemeContribution contributed} to it.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UIThemeImplementation {

    private String name;
    private String themeName;
    private String description;
    private boolean isDefault = false;
    private UIThemeImplSpec themeImplSpec;
    private List<UIThemeContribution> contributions = new ArrayList<UIThemeContribution>();

    public UIThemeImplementation(String themeName, String implName, String description) {
        this.name = implName;
        this.themeName = themeName;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getThemeName() {
        return themeName;
    }

    public QName getQName() {
        return new QName(themeName, name);
    }

    public String getDescription() {
        return description;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public UIThemeImplSpec getThemeImplSpec() {
        return themeImplSpec;
    }

    public void setThemeImplSpec(UIThemeImplSpec themeImplSpec) {
        this.themeImplSpec = themeImplSpec;
    }

    public List<UIThemeContribution> getContributions() {
        return contributions;
    }

    public UIThemeImplementation add(UIThemeContribution contribution) {
        contributions.add(contribution);
        return this;
    }
}

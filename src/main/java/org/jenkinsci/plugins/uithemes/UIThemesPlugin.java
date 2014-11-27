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

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UIThemesPlugin extends Plugin {

    protected UIThemesProcessor themesProcessor;

    @Override
    public void postInitialize() throws Exception {
        // NB: This method can be called multiple times for this plugin.  The PluginManager calls it to
        // re-initialise the styles in the event of a plugin being added or removed.  Not a good way imo
        // but could not see another way (is there one?).  Would be nice if the plugin could listen for
        // an event that gets emitted by the PluginManager.

        if (themesProcessor == null) {
            themesProcessor = newProcessor();
        }

        themesProcessor.assembleCoreStyles();
        themesProcessor.assemblePluginStyles(getPluginManager());
    }

    protected UIThemesProcessor newProcessor() {
        return new UIThemesProcessor();
    }

    public PluginManager getPluginManager() {
        return getWrapper().parent;
    }
}

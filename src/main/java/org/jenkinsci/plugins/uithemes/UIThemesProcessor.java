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

import hudson.PluginManager;
import hudson.PluginWrapper;
import hudson.util.PluginServletFilter;
import org.jenkinsci.plugins.uithemes.less.LESSProcessor;
import org.jenkinsci.plugins.uithemes.less.URLResource;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class UIThemesProcessor {

    private static final Logger LOGGER = Logger.getLogger(UIThemesProcessor.class.getName());

    private final LESSProcessor lessProcessor = new LESSProcessor();

    private byte[] coreCSSBytes = new byte[0];
    private byte[] pluginCSSBytes = new byte[0];
    private Properties themeVariables = new Properties();

    protected StylesFilter filter;

    public UIThemesProcessor() {
        // TODO: Get the theme variables from the UI i.e. allow the user to configure themes
        themeVariables.setProperty("theme-icons", System.getProperty("theme-icons", "classic"));

        try {
            filter = new StylesFilter();
            registerStylesFilter(filter);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to install StylesFilter.", e);
        }
    }

    public void assembleCoreStyles() {
        if (filter.coreStyleResource != null) {
            try {
                StringBuilder stylesBuilder = new StringBuilder();

                addGenerationTime(stylesBuilder);
                stylesBuilder.append(lessProcessor.process(filter.coreStyleResource));
                coreCSSBytes = stylesBuilder.toString().getBytes(Charset.forName("UTF-8"));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, String.format("Error processing core styles (%s).", filter.coreStyleResource.getName()), e);
            }
        } else {
            LOGGER.log(Level.INFO, "UI Themes plugin installed on Jenkins that does not define core LESS style resources.");
        }
    }

    public void assemblePluginStyles(PluginManager pluginManager) {
        List<PluginWrapper> plugins = pluginManager.getPlugins();
        StringBuilder stylesBuilder = new StringBuilder();

        addGenerationTime(stylesBuilder);

        for (PluginWrapper plugin : plugins) {
            try {
                URL pluginLessURL = getPluginLESSStyleResourceURL(plugin);
                URLResource pluginLessURLResource = new URLResource(pluginLessURL);

                if (pluginLessURL != null && pluginLessURLResource.exists(Level.FINEST)) {
                    stylesBuilder.append("/* ------------------------------------------------------------------------------\n");
                    stylesBuilder.append("   Styles for plugin: ").append(plugin.getDisplayName()).append("\n");
                    stylesBuilder.append("   ------------------------------------------------------------------------------ */\n");

                    pluginLessURLResource.setThemesProcessor(this);
                    pluginLessURLResource.setCoreVariables(filter.coreVariablesResource);
                    stylesBuilder.append(lessProcessor.process(pluginLessURLResource));
                    stylesBuilder.append("\n");
                    LOGGER.log(Level.FINE, String.format("LESS styles processed and added for plugin '%s'.", plugin.getDisplayName()));
                } else {
                    LOGGER.log(Level.FINE, String.format("No LESS styles defined for plugin '%s'.", plugin.getDisplayName()));
                }
            } catch (Exception e) {
                stylesBuilder.append("       /* Error processing plugin styles - corrupt LESS definition. */\n\n");
                LOGGER.log(Level.WARNING, String.format("Error processing LESS styles for plugin '%s'.", plugin.getDisplayName()), e);
            }
        }

        if (stylesBuilder.length() > 0) {
            try {
                pluginCSSBytes = stylesBuilder.toString().getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                LOGGER.log(Level.WARNING, "Unexpected UTF-8 encoding error.", e);
            }
        }
    }

    public Properties getThemeVariables() {
        return themeVariables;
    }

    private void addGenerationTime(StringBuilder stylesBuilder) {
        stylesBuilder.append("/*\n");
        stylesBuilder.append(String.format("      Styles generated %s\n", (new Date()).toString()));
        stylesBuilder.append("*/\n");
    }

    protected void registerStylesFilter(StylesFilter filter) throws ServletException {
        PluginServletFilter.addFilter(filter);
    }

    protected void writeCSSResponse(byte[] cssBytes, HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setContentType("text/css;charset=UTF-8");
        httpServletResponse.setContentLength(cssBytes.length);
        httpServletResponse.getOutputStream().write(cssBytes);
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
    }

    protected class StylesFilter implements Filter {

        private URLResource coreStyleResource;
        private URLResource coreVariablesResource;

        public void init(FilterConfig config) throws ServletException {
            coreVariablesResource = getAppResource("/css/variables.less", config);
            if (coreVariablesResource == null) {
                coreVariablesResource = getClasspathResource("/less/fallback-core-variables.less");
            }

            coreStyleResource = getAppResource("/css/style.less", config);
            if (coreStyleResource != null) {
                coreStyleResource.setCoreVariables(coreVariablesResource);
            }
        }

        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String pathInfo = httpServletRequest.getPathInfo();

            if (pathInfo.equals("/styles/core.css")) {
                writeCSSResponse(coreCSSBytes, (HttpServletResponse) response);
            } else if (pathInfo.equals("/styles/plugins.css")) {
                writeCSSResponse(pluginCSSBytes, (HttpServletResponse) response);
            } else {
                chain.doFilter(request, response);
            }
        }

        public void destroy() {
        }

        public URLResource getAppResource(String resPath, FilterConfig config) throws ServletException {
            try {
                URL url = config.getServletContext().getResource(resPath);
                if (url != null) {
                    return new URLResource(url);
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, String.format("Error getting resource URL for '%s'.", resPath), e);
            }
            return null;
        }

        public URLResource getClasspathResource(String resPath) {
            try {
                URL url = UIThemesProcessor.class.getResource(resPath);
                if (url != null) {
                    return new URLResource(url);
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, String.format("Error getting resource URL for '%s'.", resPath), e);
            }
            return null;
        }
    }


    public URL getPluginLESSStyleResourceURL(PluginWrapper plugin) throws MalformedURLException {
        return URLResource.getResourceURL("less/style.less", plugin.baseResourceURL);
    }
}

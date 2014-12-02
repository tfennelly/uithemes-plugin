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

import hudson.util.PluginServletFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
* @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
*/
public class UIThemesCSSFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String pathInfo = httpServletRequest.getPathInfo();

        if (pathInfo.equals("/styles/core.css")) {
//            writeCSSResponse(coreCSSBytes, (HttpServletResponse) response);
        } else if (pathInfo.equals("/styles/plugins.css")) {
//            writeCSSResponse(pluginCSSBytes, (HttpServletResponse) response);
        } else {
            chain.doFilter(request, response);
        }
    }

    public void register() throws ServletException {
        PluginServletFilter.addFilter(this);
    }

    protected void writeCSSResponse(byte[] cssBytes, HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setContentType("text/css;charset=UTF-8");
        httpServletResponse.setContentLength(cssBytes.length);
        httpServletResponse.getOutputStream().write(cssBytes);
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
    }

    public void destroy() {
    }
}

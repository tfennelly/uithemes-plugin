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
package org.jenkinsci.plugins.uithemes.util;

import hudson.model.User;
import jenkins.model.Jenkins;

import java.io.File;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class JenkinsUtil {

    private static final Logger LOGGER = Logger.getLogger(JenkinsUtil.class.getName());

    public static File JENKINS_USER_HOME;
    protected static Method idStrategy;

    static {
        try {
            JenkinsUtil.idStrategy = User.class.getMethod("idStrategy");
        } catch (NoSuchMethodException e) {
            LOGGER.log(Level.INFO, "Installed Jenkins version is pre 1.566. Cannot support user configurable UI Themes. All users will receive default theme implementations.");
        }
        try {
            JENKINS_USER_HOME = new File(Jenkins.getInstance().getRootDir(), "users");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to get Jenkins instance. Must be manually set if this is a test.");
        }
    }

    public static File getJenkinsUserHome(User user) {
        if (user == null || idStrategy == null) {
            return null;
        }

        return new File(JENKINS_USER_HOME, getUserHomeFilename(user));
    }

    public static String getUserHomeFilename(User user) {
        if (user == null || idStrategy == null) {
            return null;
        }

        try {
            Object userIdStrategy = idStrategy.invoke(null);

            if (userIdStrategy == null) {
                LOGGER.log(Level.FINE, "No IdStrategy. Cannot resolve the Jenkins user home dir name for user ID '{0}'.", user.getId());
                return null;
            }

            Method filenameOf = userIdStrategy.getClass().getMethod("filenameOf", String.class);

            return (String) filenameOf.invoke(userIdStrategy, user.getId());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, String.format("Unexpected error while attempting to resolve the Jenkins user home dir name for user ID '%s'.", user.getId()), e);
            return null;
        }
    }
}

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
import jenkins.model.IdStrategy;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;

import java.io.File;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class JenkinsUtilTestSetup {

    public static void setup() throws NoSuchMethodException {
        JenkinsUtil.JENKINS_USER_HOME = new File("./target/jenkins-home/users");
        FileUtils.deleteQuietly(JenkinsUtil.JENKINS_USER_HOME);
        JenkinsUtil.JENKINS_USER_HOME.mkdirs();
        Assert.assertNotNull("Test env running against a version of Jenkins older than version 1.566?", JenkinsUtil.idStrategy);
        JenkinsUtil.idStrategy = JenkinsUtilTestSetup.class.getMethod("mockIdStrategyMethod");
    }

    public static File mkUserDir(User user) {
        File userDir = new File(JenkinsUtil.JENKINS_USER_HOME, user.getId().toLowerCase());
        userDir.mkdirs();
        return userDir;
    }

    public static IdStrategy mockIdStrategyMethod() {
        return IdStrategy.CASE_INSENSITIVE;
    }
}

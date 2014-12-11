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

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import hudson.Util;
import org.apache.commons.io.FileUtils;
import org.jenkinsci.plugins.uithemes.UIThemesProcessorImpl;
import org.jenkinsci.plugins.uithemes.UIThemesProcessorImpl;
import org.lesscss.FileResource;
import org.lesscss.Resource;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static final Logger LOGGER = Logger.getLogger(UIThemesProcessorImpl.class.getName());

    private final String contributionName;
    private final Class<?> contributor;
    private String themeName;
    private String themeImplName;
    private Template lessTemplate;

    // TODO: Maybe support Javascript contributions?

    public UIThemeContribution(String contributionName, String themeName, String themeImplName, Class<?> contributor) {
        assert contributionName != null;
        assert themeName != null;
        assert themeImplName != null;
        assert contributor != null;

        assertNameComponentOkay(contributionName);
        assertNameComponentOkay(themeName);
        assertNameComponentOkay(themeImplName);

        this.contributionName = contributionName;
        this.themeName = themeName;
        this.themeImplName = themeImplName;
        this.contributor = contributor;
        this.lessTemplate = createLESSTemplate();
    }

    public String getContributionName() {
        return contributionName;
    }

    public Class<?> getContributor() {
        return contributor;
    }

    public String getThemeName() {
        return themeName;
    }

    public String getThemeImplName() {
        return themeImplName;
    }

    public Resource createUserLessResource(File userHome, UIThemeImplementation implementation) throws IOException {
        if (lessTemplate == null) {
            return null;
        }

        Map<String, String> userConfig = getUserThemeImplConfig(userHome);
        if (userConfig.isEmpty() && implementation != null) {
            UIThemeImplSpec themeImplSpec = implementation.getThemeImplSpec();
            if (themeImplSpec != null) {
                userConfig = themeImplSpec.getDefaultConfig();
            }
        }

        File lessFile = UIThemesProcessorImpl.getUserThemeImplLESSFile(themeName, themeImplName, userHome);
        StringWriter writer = new StringWriter();

        try {
            lessTemplate.process(userConfig, writer);
            FileUtils.write(lessFile, writer.toString(), "UTF-8");
            return new FileResource(lessFile);
        } catch (TemplateException e) {
            throw new IOException(
                    String.format("Error applying user theme impl configuration to LESS resource template. UserHome '%s', ThemeImpl '%s'.\n" +
                                  "   > There seems to be an issue/mismatch between the variables used in the template and those provided in the theme implementation configuration.\n" +
                                  "   > Check for mismatches/omissions between the template variables and the theme configuration variables:\n" +
                                  "       > Template Contributor: %s\n" +
                                  "       > Template: %s\n" +
                                  "       > Template Error Expression: ${%s} (Line %d, Column %d)\n" +
                                  "       > Theme Implementation Config: %s\n",
                            userHome.getAbsolutePath(),
                            getQName().toString(),
                            contributor.getName(),
                            getTemplatePath(),
                            e.getBlamedExpressionString(),
                            e.getLineNumber(),
                            e.getColumnNumber(),
                            (userConfig.isEmpty() ? "{} !!EMPTY!!" : userConfig.toString())
                    ),
                    e);
        } finally {
            writer.close();
        }
    }

    public QName getQName() {
        return new QName(themeName + ":" + themeImplName, contributionName);
    }

    @Override
    public String toString() {
        return getQName().toString();
    }

    protected Map<String, String> getUserThemeImplConfig(File userHome) throws IOException {
        return UIThemesProcessorImpl.getUserThemeImplConfig(themeName, themeImplName, userHome);
    }

    private Template createLESSTemplate() {
        String templateText = loadLESSTemplateText();

        if (templateText != null) {
            Reader templateReader = new StringReader(templateText);

            try {
                try {
                    Configuration config = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
                    config.setNumberFormat("#.####");
                    return new Template(getQName().toString(), templateReader, config);
                } finally {
                    templateReader.close();
                }
            } catch (IOException e) {
                throw new IllegalStateException("Exception creating FreeMarker Template instance for template:\n\n[" + templateText + "]\n\n", e);
            }
        } else {
            return null;
        }
    }

    protected String loadLESSTemplateText() {
        String templatePath = getTemplatePath();
        InputStream templateResStream = contributor.getResourceAsStream(templatePath);

        if (templateResStream != null) {
            try {
                Reader templateResStreamReader = new InputStreamReader(templateResStream, "UTF-8");
                StringWriter writer = new StringWriter();
                Util.copyStreamAndClose(templateResStreamReader, writer);
                return writer.toString();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, String.format("Error reading LESS resource template file '%s'.", templatePath), e);
            }

        } else {
            LOGGER.log(Level.INFO, "No UI Theme Contribution LESS template found at ''{0}'' on the classpath.", templatePath);
        }

        return null;
    }

    private String getTemplatePath() {
        return String.format("/jenkins-themes/%s/%s/%s/theme-template.less", themeName, themeImplName, contributionName);
    }

    private void assertNameComponentOkay(String string) {
        if (!Util.rawEncode(string).equals(string)) {
            throw new IllegalArgumentException(String.format("'%s' cannot be used as UIThemeContribution name component as " +
                    "it contains characters that cannot be used in a file path.", string));
        }
    }
}

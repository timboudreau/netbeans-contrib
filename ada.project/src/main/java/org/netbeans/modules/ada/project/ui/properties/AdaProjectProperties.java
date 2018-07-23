/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.ada.project.ui.properties;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.queries.FileEncodingQuery;
import org.netbeans.modules.ada.project.AdaProject;
import org.netbeans.modules.ada.project.AdaProjectUtil;
import org.netbeans.modules.ada.project.Pair;
import org.netbeans.modules.ada.project.SourceRoots;
import org.netbeans.modules.ada.project.options.AdaOptions;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Mutex;
import org.openide.util.MutexException;

/**
 *
 * @author Andrea Lucarelli
 */
public class AdaProjectProperties {

    public static final String SRC_DIR = "src.dir"; //NOI18N
    public static final String BUILD_DIR = "build.dir"; // NOI18N
    public static final String DIST_DIR = "dist.dir"; // NOI18N
    public static final String MAIN_FILE = "main.file"; //NOI18N
    public static final String APPLICATION_ARGS = "application.args";   //NOI18N
    public static final String ACTIVE_PLATFORM = "platform.active"; //NOI18N
    public static final String ADA_LIB_PATH = "ada.lib.path"; //NOI18N
    public static final String SOURCE_ENCODING = "source.encoding"; //NOI18N

    private final AdaProject project;
    private final PropertyEvaluator eval;

    private volatile String encoding;
    private volatile List<Pair<File, String>> sourceRoots;
    private volatile List<Pair<File, String>> testRoots;
    private volatile String mainModule;
    private volatile String appArgs;
    private volatile ArrayList<String> librariesPath;

    private volatile String activePlatformId;
    private volatile String adaDialects;
    private volatile String adaRestrictions;
    private volatile String pkgSpecPrefix;
    private volatile String pkgBodyPrefix;
    private volatile String separatePrefix;
    private volatile String pkgSpecPostfix;
    private volatile String pkgBodyPostfix;
    private volatile String separatePostfix;
    private volatile String pkgSpecExt;
    private volatile String pkgBodyExt;
    private volatile String separateExt;

    private static final String ADA_PATH_SEP = "|";

    public AdaProjectProperties(final AdaProject project) {
        assert project != null;
        this.project = project;
        this.eval = project.getEvaluator();
    }

    public AdaProject getProject() {
        return this.project;
    }

    public FileObject getProjectDirectory() {
        return this.project.getProjectDirectory();
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        if (this.encoding == null) {
            this.encoding = eval.getProperty(SOURCE_ENCODING);
        }
        return this.encoding;
    }

    public List<Pair<File, String>> getSourceRoots() {
        if (sourceRoots == null) {
            final SourceRoots tmpSourceRoots = project.getSourceRoots();
            final String[] rootLabels = tmpSourceRoots.getRootNames();
            final String[] rootProps = tmpSourceRoots.getRootProperties();
            final URL[] rootURLs = tmpSourceRoots.getRootURLs();
            final List<Pair<File, String>> data = new LinkedList<Pair<File, String>>();
            for (int i = 0; i < rootURLs.length; i++) {
                final File f = new File(URI.create(rootURLs[i].toExternalForm()));
                final String s = tmpSourceRoots.getRootDisplayName(rootLabels[i], rootProps[i]);
                data.add(Pair.of(f, s));
            }
            this.sourceRoots = data;
        }
        return this.sourceRoots;
    }

    public void setSourceRoots(final List<Pair<File, String>> sourceRoots) {
        assert sourceRoots != null;
        this.sourceRoots = sourceRoots;
    }

    public List<Pair<File, String>> getTestRoots() {
        if (testRoots == null) {
            final SourceRoots tmpTestRoots = project.getTestRoots();
            final String[] rootLabels = tmpTestRoots.getRootNames();
            final String[] rootProps = tmpTestRoots.getRootProperties();
            final URL[] rootURLs = tmpTestRoots.getRootURLs();
            final List<Pair<File, String>> data = new LinkedList<Pair<File, String>>();
            for (int i = 0; i < rootURLs.length; i++) {
                final File f = new File(URI.create(rootURLs[i].toExternalForm()));
                final String s = tmpTestRoots.getRootDisplayName(rootLabels[i], rootProps[i]);
                data.add(Pair.of(f, s));
            }
            this.testRoots = data;
        }
        return this.testRoots;
    }

    public void setTestRoots(final List<Pair<File, String>> testRoots) {
        assert testRoots != null;
        this.sourceRoots = testRoots;
    }

    public String getMainModule() {
        if (mainModule == null) {
            mainModule = eval.getProperty(MAIN_FILE);
        }
        return mainModule;
    }

    public void setMainModule(final String module) {
        this.mainModule = module;
    }

    public String getApplicationArgs() {
        if (appArgs == null) {
            appArgs = eval.getProperty(APPLICATION_ARGS);
        }
        return appArgs;
    }

    public void setApplicationArgs(final String args) {
        this.appArgs = args;
    }

    public ArrayList<String> getLibrariesPath() {
        if (librariesPath == null) {
            librariesPath = buildPathList(eval.getProperty(ADA_LIB_PATH));
        }
        return librariesPath;
    }

    public void setLibrariesPath(ArrayList<String> librariesPath) {
        assert librariesPath != null;
        this.librariesPath = librariesPath;
    }

    public String getActivePlatformId() {
        if (activePlatformId == null) {
            activePlatformId = eval.getProperty(ACTIVE_PLATFORM);
        }
        return activePlatformId;
    }

    public void setActivePlatformId(String activePlatformId) {
        this.activePlatformId = activePlatformId;
    }

    // Storing
    void save() {
        try {
            if (this.sourceRoots != null) {
                final SourceRoots sr = this.project.getSourceRoots();
                sr.putRoots(this.sourceRoots);
            }
            if (this.testRoots != null) {
                final SourceRoots sr = this.project.getTestRoots();
                sr.putRoots(this.testRoots);
            }
            // store properties
            ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Void>() {

                public Void run() throws IOException {
                    saveProperties();
                    return null;
                }
            });
            ProjectManager.getDefault().saveProject(project);
        } catch (MutexException e) {
            Exceptions.printStackTrace((IOException) e.getException());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void saveProperties() throws IOException {

        final AntProjectHelper helper = AdaProjectUtil.getProjectHelper(project);
        // get properties
        final EditableProperties projectProperties = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        final EditableProperties privateProperties = helper.getProperties(AntProjectHelper.PRIVATE_PROPERTIES_PATH);

        if (mainModule != null) {
            projectProperties.put(MAIN_FILE, mainModule);
        }

        if (encoding != null) {
            projectProperties.put(SOURCE_ENCODING, encoding);
        }

        if (appArgs != null) {
            privateProperties.put(APPLICATION_ARGS, appArgs);
        }

        if (librariesPath != null) {
            projectProperties.put(ADA_LIB_PATH, buildPathString(librariesPath));
        }

        if (activePlatformId != null) {
            projectProperties.put(ACTIVE_PLATFORM, activePlatformId);
        }

        if (adaDialects != null) {
            projectProperties.put(AdaOptions.ADA_DIALECTS, adaDialects);
        }
        if (adaRestrictions != null) {
            projectProperties.put(AdaOptions.ADA_RESTRICTIONS, adaRestrictions);
        }
        if (pkgSpecPrefix != null) {
            projectProperties.put(AdaOptions.PKG_SPEC_PREFIX, pkgSpecPrefix);
        }
        if (pkgBodyPrefix != null) {
            projectProperties.put(AdaOptions.PKG_BODY_PREFIX, pkgBodyPrefix);
        }
        if (separatePrefix != null) {
            projectProperties.put(AdaOptions.SEPARATE_PREFIX, separatePrefix);
        }
        if (pkgSpecPostfix != null) {
            projectProperties.put(AdaOptions.PKG_SPEC_POSTFIX, pkgSpecPostfix);
        }
        if (pkgBodyPostfix != null) {
            projectProperties.put(AdaOptions.PKG_BODY_POSTFIX, pkgBodyPostfix);
        }
        if (separatePostfix != null) {
            projectProperties.put(AdaOptions.SEPARATE_POSTFIX, separatePostfix);
        }
        if (pkgSpecExt != null) {
            projectProperties.put(AdaOptions.PKG_SPEC_EXT, pkgSpecExt);
        }
        if (pkgBodyExt != null) {
            projectProperties.put(AdaOptions.PKG_BODY_EXT, pkgBodyExt);
        }
        if (separateExt != null) {
            projectProperties.put(AdaOptions.SEPARATE_EXT, separateExt);
        }

        // store all the properties        
        helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, projectProperties);
        helper.putProperties(AntProjectHelper.PRIVATE_PROPERTIES_PATH, privateProperties);

        // additional changes
        // encoding
        if (encoding != null) {
            try {
                FileEncodingQuery.setDefaultEncoding(Charset.forName(encoding));
            } catch (UnsupportedCharsetException e) {
                //When the encoding is not supported by JVM do not set it as default
            }
        }
    }

    /**
     *Build a path string from arraylist
     * @param path
     * @return
     */
    private static String buildPathString(ArrayList<String> path) {
        StringBuilder pathString = new StringBuilder();
        int count = 0;
        for (String pathEle : path) {
            pathString.append(pathEle);
            if (count++ < path.size()) {
                pathString.append(ADA_PATH_SEP);
            }
        }
        return pathString.toString();
    }

    /**
     *
     * @param pathString
     * @return
     */
    private static ArrayList<String> buildPathList(String pathString) {
        ArrayList<String> pathList = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(pathString, ADA_PATH_SEP);
        while (tokenizer.hasMoreTokens()) {
            pathList.add(tokenizer.nextToken());
        }
        return pathList;
    }

    public String getAdaDialects() {
        if (adaDialects == null) {
            adaDialects = eval.getProperty(AdaOptions.ADA_DIALECTS);
        }
        return adaDialects;
    }

    public void setAdaDialects(String adaDialects) {
        this.adaDialects = adaDialects;
    }

    public String getAdaRestrictions() {
        if (adaRestrictions == null) {
            adaRestrictions = eval.getProperty(AdaOptions.ADA_RESTRICTIONS);
        }
        return adaRestrictions;
    }

    public void setAdaRestrictions(String adaRestrictions) {
        this.adaRestrictions = adaRestrictions;
    }

    public String getPkgBodyExt() {
        if (pkgBodyExt == null) {
            pkgBodyExt = eval.getProperty(AdaOptions.PKG_BODY_EXT);
        }
        return pkgBodyExt;
    }

    public void setPkgBodyExt(String pkgBodyExt) {
        this.pkgBodyExt = pkgBodyExt;
    }

    public String getPkgBodyPostfix() {
        if (pkgBodyPostfix == null) {
            pkgBodyPostfix = eval.getProperty(AdaOptions.PKG_BODY_POSTFIX);
        }
        return pkgBodyPostfix;
    }

    public void setPkgBodyPostfix(String pkgBodyPostfix) {
        this.pkgBodyPostfix = pkgBodyPostfix;
    }

    public String getPkgBodyPrefix() {
        if (pkgBodyPrefix == null) {
            pkgBodyPrefix = eval.getProperty(AdaOptions.PKG_BODY_PREFIX);
        }
        return pkgBodyPrefix;
    }

    public void setPkgBodyPrefix(String pkgBodyPrefix) {
        this.pkgBodyPrefix = pkgBodyPrefix;
    }

    public String getPkgSpecExt() {
        if (pkgSpecExt == null) {
            pkgSpecExt = eval.getProperty(AdaOptions.PKG_SPEC_EXT);
        }
        return pkgSpecExt;
    }

    public void setPkgSpecExt(String pkgSpecExt) {
        this.pkgSpecExt = pkgSpecExt;
    }

    public String getPkgSpecPostfix() {
        if (pkgSpecPostfix == null) {
            pkgSpecPostfix = eval.getProperty(AdaOptions.PKG_SPEC_POSTFIX);
        }
        return pkgSpecPostfix;
    }

    public void setPkgSpecPostfix(String pkgSpecPostfix) {
        this.pkgSpecPostfix = pkgSpecPostfix;
    }

    public String getPkgSpecPrefix() {
        if (pkgSpecPrefix == null) {
            pkgSpecPrefix = eval.getProperty(AdaOptions.PKG_SPEC_PREFIX);
        }
        return pkgSpecPrefix;
    }

    public void setPkgSpecPrefix(String pkgSpecPrefix) {
        this.pkgSpecPrefix = pkgSpecPrefix;
    }

    public String getSeparateExt() {
        if (separateExt == null) {
            separateExt = eval.getProperty(AdaOptions.SEPARATE_EXT);
        }
        return separateExt;
    }

    public void setSeparateExt(String separateExt) {
        this.separateExt = separateExt;
    }

    public String getSeparatePostfix() {
        if (separatePostfix == null) {
            separatePostfix = eval.getProperty(AdaOptions.SEPARATE_POSTFIX);
        }
        return separatePostfix;
    }

    public void setSeparatePostfix(String separatePostfix) {
        this.separatePostfix = separatePostfix;
    }

    public String getSeparatePrefix() {
        if (separatePrefix == null) {
            separatePrefix = eval.getProperty(AdaOptions.SEPARATE_PREFIX);
        }
        return separatePrefix;
    }

    public void setSeparatePrefix(String separatePrefix) {
        this.separatePrefix = separatePrefix;
    }
}

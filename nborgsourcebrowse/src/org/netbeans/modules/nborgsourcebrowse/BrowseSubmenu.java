/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.nborgsourcebrowse;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.openide.awt.DynamicMenuContent;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

/**
 * Submenu active for nb.org files permitting you to browse to their sources, Javadoc, etc.
 * XXX handle specific revisions rather than tip...
 * @author Jesse Glick
 */
public class BrowseSubmenu implements Presenter.Menu {

    /** Tuples of display label, URL format; use @REPO@, @FILEPATH@, @JAVABASENAME@, @CNBDASHES@ */
    private static final String[][] LINKS_FILE = {
        {"Source (Hg)", "@REPO@/raw-file/tip/@FILEPATH@"},
        {"Source (Hudson trunk)", "http://deadlock.netbeans.org/hudson/job/trunk/ws/@FILEPATH@"},
        {"Javadoc (official)", "http://bits.netbeans.org/dev/javadoc/@CNBDASHES@/@JAVABASENAME@.html"},
        {"Javadoc (Hudson nbms-and-javadoc)", "http://deadlock.netbeans.org/hudson/job/nbms-and-javadoc/javadoc/@CNBDASHES@/@JAVABASENAME@.html"},
    };
    private static final String[][] LINKS_PRJ = {
        {"Javadoc (official)", "http://www.netbeans.org/download/dev/javadoc/@CNBDASHES@/"},
        {"Javadoc (Hudson nbms-and-javadoc)", "http://deadlock.netbeans.org/hudson/job/nbms-and-javadoc/javadoc/@CNBDASHES@/"},
    };

    /** Default constructor for layer */
    public BrowseSubmenu() {}

    public JMenuItem getMenuPresenter() {
        return new Menu();
    }

    private static final class Menu extends JMenu implements DynamicMenuContent {

        public JComponent[] getMenuPresenters() {
            List<String[]> labelsAndUrls = new ArrayList<String[]>();
            DataObject d = Utilities.actionsGlobalContext().lookup(DataObject.class);
            if (d != null) {
                FileObject f = d.getPrimaryFile();
                FileObject dir = f.isFolder() ? f : f.getParent();
                String[] repoAndPath = findRepoAndPath(dir, "");
                if (repoAndPath != null) {
                    for (String[] data : LINKS_FILE) {
                        String label = data[0];
                        String url = data[1];
                        if (url.contains("@REPO@")) {
                            if (repoAndPath[0] != null) {
                                url = url.replace("@REPO@", repoAndPath[0]);
                            } else {
                                continue;
                            }
                        }
                        if (url.contains("@FILEPATH@")) {
                            if (f.isData()) {
                                url = url.replace("@FILEPATH@", repoAndPath[1] + f.getNameExt());
                            } else if (f.isFolder()) {
                                url = url.replace("@FILEPATH@", repoAndPath[1]);
                            }
                        }
                        if (url.contains("@JAVABASENAME@")) {
                            Matcher m = Pattern.compile(".*/src/(.+)\\.java").matcher(repoAndPath[1] + f.getNameExt());
                            if (m.matches()) {
                                url = url.replace("@JAVABASENAME@", m.group(1));
                            } else {
                                continue;
                            }
                        }
                        if (url.contains("@CNBDASHES@")) {
                            Project p = FileOwnerQuery.getOwner(f);
                            if (p == null) {
                                continue;
                            } else {
                                url = url.replace("@CNBDASHES@", ProjectUtils.getInformation(p).getName().replace('.', '-'));
                            }
                        }
                        labelsAndUrls.add(new String[] {label, url});
                    }
                }
            } else {
                Project p = Utilities.actionsGlobalContext().lookup(Project.class);
                if (p != null) {
                    for (String[] data : LINKS_PRJ) {
                        String label = data[0];
                        String url = data[1];
                        labelsAndUrls.add(new String[] {label, url.replace("@CNBDASHES@", ProjectUtils.getInformation(p).getName().replace('.', '-'))});
                    }
                }
            }
            if (labelsAndUrls.isEmpty()) {
                return new JComponent[0];
            } else {
                JMenu menu = new JMenu("Browse hg.netbeans.org");
                for (String[] labelAndUrl : labelsAndUrls) {
                    JMenuItem mi = new JMenuItem(labelAndUrl[0]);
                    final URL u;
                    try {
                        u = new URL(labelAndUrl[1]);
                    } catch (MalformedURLException x) {
                        throw new AssertionError(x);
                    }
                    mi.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent ev) {
                            HtmlBrowser.URLDisplayer.getDefault().showURL(u);
                        }
                    });
                    menu.add(mi);
                }
                return new JComponent[] {menu};
            }
        }

        private static String[] findRepoAndPath(FileObject dir, String path) {
            FileObject dotHg = dir.getFileObject(".hg");
            if (dotHg != null && dotHg.isFolder()) {
                String repo = null;
                FileObject hgrc = dotHg.getFileObject("hgrc");
                if (hgrc != null && hgrc.isData()) {
                    try {
                        InputStream is = hgrc.getInputStream();
                        try {
                            BufferedReader r = new BufferedReader(new InputStreamReader(is));
                            String line;
                            while ((line = r.readLine()) != null) {
                                Matcher m = Pattern.compile("default *= *(https?://[^:]+[^/])/?").matcher(line);
                                if (m.matches()) {
                                    repo = m.group(1);
                                    break;
                                }
                            }
                        } finally {
                            is.close();
                        }
                    } catch (IOException x) {
                        Exceptions.printStackTrace(x);
                    }
                }
                return new String[] {repo, path};
            } else {
                FileObject parent = dir.getParent();
                if (parent == null) {
                    return null;
                } else {
                    return findRepoAndPath(parent, dir.getNameExt() + "/" + path);
                }
            }
        }

        public JComponent[] synchMenuPresenters(JComponent[] items) {
            return getMenuPresenters();
        }

    }

}

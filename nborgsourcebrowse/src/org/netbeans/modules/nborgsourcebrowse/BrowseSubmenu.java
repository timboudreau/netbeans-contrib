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
 * XXX handle branches, tags, specific revision numbers...
 * @author Jesse Glick
 */
public class BrowseSubmenu implements Presenter.Menu {

    /** Tuples of display label, URL format; use @FILEPATH@, @DIRPATH@, @JAVABASENAME@, @CNBDASHES@ */
    private static final String[][] LINKS_FILE = {
        {"Source (ViewCVS)", "http://www.netbeans.org/nonav/source/browse/~checkout~/@FILEPATH@?content-type=text/plain"},
        {"Source (ViewCVS)", "http://www.netbeans.org/nonav/source/browse/@DIRPATH@/"},
        {"Source (OpenGrok)", "http://deadlock.netbeans.org/opengrok/xref/@FILEPATH@"},
        {"Source (OpenGrok)", "http://deadlock.netbeans.org/opengrok/xref/@DIRPATH@/"},
        {"Source (Fisheye)", "http://deadlock.netbeans.org/fisheye/browse/~raw,r=HEAD/netbeans/@FILEPATH@"},
        {"Source (Fisheye)", "http://deadlock.netbeans.org/fisheye/browse/netbeans/@DIRPATH@/"},
        {"Source (Hudson trunk)", "http://deadlock.netbeans.org/hudson/job/trunk/ws/@FILEPATH@"},
        {"Source (Hudson trunk)", "http://deadlock.netbeans.org/hudson/job/trunk/ws/@DIRPATH@/"},
        {"Javadoc (official)", "http://www.netbeans.org/download/dev/javadoc/@CNBDASHES@/@JAVABASENAME@.html"},
        {"Javadoc (Hudson javadoc-nbms)", "http://deadlock.netbeans.org/hudson/job/javadoc-nbms/javadoc/@CNBDASHES@/@JAVABASENAME@.html"},
    };
    private static final String[][] LINKS_PRJ = {
        {"Javadoc (official)", "http://www.netbeans.org/download/dev/javadoc/@CNBDASHES@/"},
        {"Javadoc (Hudson javadoc-nbms)", "http://deadlock.netbeans.org/hudson/job/javadoc-nbms/javadoc/@CNBDASHES@/"},
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
                String root = slurp(dir.getFileObject("CVS/Root"));
                if (root != null && root.contains("netbeans.org")) {
                    String repos = slurp(dir.getFileObject("CVS/Repository"));
                    if (repos != null) {
                        /* XXX branch handling not yet implemented... most of these viewers will not handle it well anyway
                        String tag = slurp(dir.getFileObject("CVS/Tag"));
                        String branch = (tag != null && tag.startsWith("T")) ? tag.substring(1) : null;
                         */
                        for (String[] data : LINKS_FILE) {
                            String label = data[0];
                            String url = data[1];
                            if (url.contains("@FILEPATH@")) {
                                if (f.isData()) {
                                    url = url.replace("@FILEPATH@", repos + "/" + f.getNameExt());
                                } else {
                                    continue;
                                }
                            }
                            if (url.contains("@DIRPATH@")) {
                                if (f.isFolder()) {
                                    url = url.replace("@DIRPATH@", repos);
                                } else {
                                    continue;
                                }
                            }
                            if (url.contains("@JAVABASENAME@")) {
                                Matcher m = Pattern.compile(".*/src/(.+)\\.java").matcher(repos + "/" + f.getNameExt());
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
                }
            } else {
                Project p = Utilities.actionsGlobalContext().lookup(Project.class);
                if (p != null) {
                    for (String[] data : LINKS_PRJ) {
                        String label = data[0];
                        String url = data[1];
                        labelsAndUrls.add(new String[] {data[0], data[1].replace("@CNBDASHES@", ProjectUtils.getInformation(p).getName().replace('.', '-'))});
                    }
                }
            }
            if (labelsAndUrls.isEmpty()) {
                return new JComponent[0];
            } else {
                JMenu menu = new JMenu("Browse cvs.netbeans.org");
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

        private static String slurp(FileObject f) {
            if (f != null) {
                try {
                    InputStream is = f.getInputStream();
                    try {
                        BufferedReader r = new BufferedReader(new InputStreamReader(is));
                        return r.readLine();
                    } finally {
                        is.close();
                    }
                } catch (IOException x) {
                    Exceptions.printStackTrace(x);
                }
            }
            return null;
        }

        public JComponent[] synchMenuPresenters(JComponent[] items) {
            return getMenuPresenters();
        }

    }

}

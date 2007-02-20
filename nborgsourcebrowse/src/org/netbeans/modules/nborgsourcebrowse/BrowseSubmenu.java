/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
    private static final String[][] LINKS = {
        {"Source (ViewCVS)", "http://www.netbeans.org/nonav/source/browse/~checkout~/@FILEPATH@?content-type=text/plain"},
        {"Source (ViewCVS)", "http://www.netbeans.org/nonav/source/browse/@DIRPATH@/"},
        {"Source (OpenGrok)", "http://deadlock.nbextras.org/opengrok/xref/@FILEPATH@"},
        {"Source (OpenGrok)", "http://deadlock.nbextras.org/opengrok/xref/@DIRPATH@/"},
        {"Source (Fisheye)", "http://deadlock.nbextras.org/fisheye/browse/~raw,r=HEAD/netbeans/@FILEPATH@"},
        {"Source (Fisheye)", "http://deadlock.nbextras.org/fisheye/browse/netbeans/@DIRPATH@/"},
        {"Source (Hudson trunk)", "http://deadlock.nbextras.org/hudson/job/trunk/ws/@FILEPATH@"},
        {"Source (Hudson trunk)", "http://deadlock.nbextras.org/hudson/job/trunk/ws/@DIRPATH@/"},
        {"Javadoc (official)", "http://www.netbeans.org/download/dev/javadoc/@CNBDASHES@/@JAVABASENAME@.html"},
        {"Javadoc (Hudson javadoc-nbms)", "http://deadlock.nbextras.org/hudson/job/javadoc-nbms/javadoc/@CNBDASHES@/@JAVABASENAME@.html"},
    };

    /** Default constructor for layer */
    public BrowseSubmenu() {}

    public JMenuItem getMenuPresenter() {
        return new Menu();
    }

    private static final class Menu extends JMenu implements DynamicMenuContent {

        public JComponent[] getMenuPresenters() {
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
                        JMenu menu = new JMenu("Browse cvs.netbeans.org");
                        for (String[] data : LINKS) {
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
                            JMenuItem mi = new JMenuItem(label);
                            final URL u;
                            try {
                                u = new URL(url);
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
            }
            return new JComponent[0];
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

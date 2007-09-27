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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.buildmonitor;

import java.awt.EventQueue;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeEvent;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.Preferences;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import org.openide.util.WeakListeners;

/**
 * Displays one or more BuildStatus objects in the status bar.
 *
 * @author Tom Ball, Jesse Glick
 */
public class BuildMonitorPanel extends JPanel implements NodeChangeListener {
    
    static final Preferences ROOT = NbPreferences.forModule(BuildMonitor.class);
    
    private static BuildMonitor createDefaultMonitor() {
        Preferences node = ROOT.node("nbdev");
        BuildMonitor m;
        try {
            m = BuildMonitor.create(node);
            m.setURL(new URL("http://deadlock.netbeans.org/hudson/job/trunk/rssAll"));
        } catch (MalformedURLException x) {
            throw new AssertionError(x);
        }
        m.setName("NB trunk");
        return m;
    }
    
    private static final BuildMonitorPanel instance = new BuildMonitorPanel();

    public static BuildMonitorPanel getInstance() {
        return instance;
    }

    private BuildMonitorPanel() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        ROOT.addNodeChangeListener(WeakListeners.create(NodeChangeListener.class, this, ROOT));
        buildPanel();
    }

    private void buildPanel() {
        try {
            String[] ids = ROOT.childrenNames();
            if (ids.length == 0) {
                add(new BuildStatus(createDefaultMonitor()));
            } else {
                for (int i = 0; i < ids.length; i++) {
                    try {
                        add(new BuildStatus(BuildMonitor.create(ROOT.node(ids[i]))));
                        if (i + 1 < ids.length) {
                            add(Box.createHorizontalStrut(10));
                        }
                    } catch (MalformedURLException x) {
                        Exceptions.printStackTrace(x);
                    }
                }
            }
        } catch (BackingStoreException x) {
            Exceptions.printStackTrace(x);
        }

        revalidate();
        repaint();
    }

    public void rebuildPanel() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                removeAll();
                buildPanel();
            }
        });
    }
    
    public void childAdded(NodeChangeEvent ev) {
        rebuildPanel();
    }
    
    public void childRemoved(NodeChangeEvent ev) {
        rebuildPanel();
    }
    
}

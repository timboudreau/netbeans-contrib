/*
DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.


The contents of this file are subject to the terms of either the GNU
General Public License Version 2 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://www.netbeans.org/cddl-gplv2.html
or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License file at
nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
particular file as subject to the "Classpath" exception as provided
by Sun in the GPL Version 2 section of the License file that
accompanied this code. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

Contributor(s): */
package org.netbeans.modules.htmlproject;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import org.netbeans.api.project.Project;
import org.openide.ErrorManager;
import org.openide.actions.FileSystemAction;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.cookies.EditCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.actions.Presenter;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Tim Boudreau
 */
final class HtmlLogicalView extends AbstractNode implements PropertyChangeListener {
    private HtmlProject proj;
    private static final String ICON = 
        "org/netbeans/modules/htmlproject/htmlProject.png";
    /** Creates a new instance of HtmlLogicalView */
    public HtmlLogicalView(HtmlProject proj, Lookup toProxy) {
        super (new Kids(proj, null), new ProxyLookup (new Lookup[] {
            Lookups.fixed(new Object[] { proj, new Locator() }), toProxy,
        }));
        Locator l = (Locator) getLookup().lookup (Locator.class);
        l.view = this;
        this.proj = proj;
        setDisplayName (proj.getDisplayName());
        setIconBaseWithExtension(ICON); // NOI18N
        proj.addPropertyChangeListener(WeakListeners.propertyChange(this, proj));
    }
    
    void closed() {
        ((Kids) getChildren()).closed();
    }
    
    static class Locator {
        HtmlLogicalView view;
        public Node locate (Node root, Object o) {
            if (o instanceof FileObject) {
                o = FileUtil.toFile((FileObject)o);
            }
            return traverse (root, o.getClass(), o);
        }

        private Node traverse (Node n, Class clazz, Object o) {
            if (o.equals(n.getLookup().lookup(clazz))) {
                return n;
            } else {
                Node[] more = n.getChildren().getNodes(true);
                for (int i = 0; i < more.length; i++) {
                    Node result = traverse (more[i], clazz, o);
                    if (result != null) {
                        return result;
                    }
                }
            }
            return null;
        }
    }

    public Action[] getActions(boolean popup) {
        Action fsAction = findFsAction (proj);
        List l = new ArrayList (11);

        int[] order = new int[] {
            MutableAction.VIEW, MutableAction.ZIP, MutableAction.CLEAN,
            -1, MutableAction.CLOSE, MutableAction.DELETE,
        };

        for (int i = 0; i < order.length; i++) {
            if (order[i] == -1) {
                l.add (null);
            } else {
                l.add (new MutableAction (order[i], proj));
            }
        }
        l.add (null);
        if (fsAction != null) {
            l.add (fsAction);
            l.add (null);
        }
        boolean currSort = ((Kids) getChildren()).isSortByFolder();
        l.add (new SortAction (false, currSort));
        l.add (new SortAction (true, currSort));
        l.add (null);
        l.add (new MutableAction (MutableAction.PROPS, proj));

        Action[] result = (Action[]) l.toArray(new Action[l.size()]);
        return result;
    }

    private class SortAction extends AbstractAction implements Presenter.Popup {
        private boolean byFilename;
        private boolean currentlySortedByFilename;
        SortAction (boolean filename, boolean currIsFilename) {
            this.byFilename = filename;
            this.currentlySortedByFilename = currIsFilename;
            String folderSort = NbBundle.getMessage (
                    HtmlLogicalView.class, "LBL_FolderSort");
            String filenameSort = NbBundle.getMessage (
                    HtmlLogicalView.class, "LBL_FilenameSort");
            putValue (NAME, filename ? folderSort : filenameSort);
        }

        public void actionPerformed(ActionEvent e) {
            Kids k = (Kids) getChildren();
            k.setSortByFilename (byFilename);
        }

        public JMenuItem getPopupPresenter() {
            JCheckBoxMenuItem result = new JCheckBoxMenuItem (this);
            if (byFilename == currentlySortedByFilename) {
                result.setSelected(true);
            }
            return result;
        }
    }

    private void upd() {
        if (((Kids) getChildren()).showing) {
            ((Kids) getChildren()).launch();
        }
    }

    private Action findFsAction (Project p) {
        FileObject fob = p.getProjectDirectory();
        Node n;
        try {
            n = DataObject.find(fob).getNodeDelegate();
        } catch (DataObjectNotFoundException ex) {
            return null;
        }
        Action[] a = n.getActions(true);
        for (int i = 0; i < a.length; i++) {
            if (a[i] instanceof FileSystemAction) {
                return a[i];
            }
        }
        return null;
    }


    public void propertyChange(PropertyChangeEvent evt) {
        setDisplayName (proj.getDisplayName());
    }

}

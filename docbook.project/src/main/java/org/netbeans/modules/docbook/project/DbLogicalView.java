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
package org.netbeans.modules.docbook.project;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.ErrorManager;
import org.openide.actions.FileSystemAction;
import org.openide.actions.NewTemplateAction;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeAdapter;
import org.openide.nodes.NodeEvent;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tim Boudreau
 */
public class DbLogicalView extends AbstractNode {
    public DbLogicalView(DbProject proj) throws DataObjectNotFoundException {
        super (Children.<FileObject>create(new DbLogicalViewChildren(proj), true),
                Lookups.fixed(proj, DataObject.find (proj.getProjectDirectory())));
        setDisplayName (proj.getProjectDirectory().getName());
        setIconBaseWithExtension(
                "org/netbeans/modules/docbook/project/docbook.png"); //NOI18N
        notifyMainNameChanged();
    }

    private DbFileFilterNode lnode = null;
    void notifyMainNameChanged() {
        DbProject proj = getLookup().lookup (DbProject.class);
        FileObject fob = proj.getMainFile();
        if (lnode != null) {
            lnode.removeNodeListener (nl);
            lnode = null;
        }
        if (fob != null) {
            try {
                DataObject dob = DataObject.find (fob);
                Node n = dob.getNodeDelegate();
                lnode = new DbFileFilterNode(n, proj, fob.getParent());
                lnode.addNodeListener(nl);
                nl.propertyChange(new PropertyChangeEvent (lnode,
                        Node.PROP_DISPLAY_NAME, null, null));
            } catch (Exception e) {
                ErrorManager.getDefault().notify(e);
            }
        }
    }

    private NL nl = new NL();
    private class NL extends NodeAdapter {
        public void nodeDestroyed(NodeEvent ev) {
            ev.getNode().removeNodeListener (this);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (Node.PROP_DISPLAY_NAME.equals(evt.getPropertyName())) {
                setDisplayName (((Node) evt.getSource()).getDisplayName());
            }
        }
    }

    public Action[] getActions (boolean context) {
        DbProject proj = getLookup().lookup (DbProject.class);
        Action[] a = proj.getActions();
        List <Action> l = new ArrayList <Action>(Arrays.<Action>asList(a));
        l.add (0, SystemAction.get(NewTemplateAction.class));
        l.add (1, null);
        l.add (2, SystemAction.get (org.openide.actions.FindAction.class));
        l.add (null);
        l.add (new SetMainProjectAction());
        l.add (new CloseProjectAction());
        l.add (null);
        l.add (SystemAction.get(FileSystemAction.class));

        a = l.toArray (a);
        return a;
    }

    private class SetMainProjectAction extends AbstractAction {
        public SetMainProjectAction() {
            putValue (NAME, "Set Main Project");
        }

        public void actionPerformed(ActionEvent e) {
            OpenProjects.getDefault().setMainProject(getLookup().lookup(Project.class));
        }
    }

    private class CloseProjectAction extends AbstractAction {
        public CloseProjectAction() {
            putValue (NAME, "Close Project");
        }

        public void actionPerformed(ActionEvent e) {
            OpenProjects.getDefault().close(new Project[] {
                    getLookup().lookup(Project.class)
            });
        }
    }

}

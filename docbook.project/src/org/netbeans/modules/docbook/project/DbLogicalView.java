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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
import org.openide.actions.NewTemplateAction;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeAdapter;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tim Boudreau
 */
public class DbLogicalView extends AbstractNode {
    public DbLogicalView(DbProject proj) throws DataObjectNotFoundException {
        super (new DbLogicalViewChildren(proj),
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
        }
        if (fob != null) {
            try {
                DataObject dob = DataObject.find (fob);
                Node n = dob.getNodeDelegate();
                lnode = new DbFileFilterNode(n, proj);
                lnode.addNodeListener(nl);
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
        List l = new ArrayList (Arrays.asList(a));
        l.add (0, SystemAction.get(NewTemplateAction.class));
        l.add (1, null);
        l.add (new SetMainProjectAction());
        l.add (new CloseProjectAction());
        a = (Action[]) l.toArray (a);
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

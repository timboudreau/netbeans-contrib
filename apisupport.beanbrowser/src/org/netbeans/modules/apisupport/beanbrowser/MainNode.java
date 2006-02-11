/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.apisupport.beanbrowser;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Action;
import org.openide.ErrorManager;
import org.openide.actions.OpenLocalExplorerAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFilter;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.RepositoryNodeFactory;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.SystemAction;

/** Node to browse various important stuff. */
public class MainNode extends AbstractNode {
    
    public MainNode() {
        super(new MainChildren());
        setName("BeanBrowserMainNode");
        setDisplayName("Bean Browser");
        setIconBaseWithExtension("org/netbeans/modules/apisupport/beanbrowser/BeanBrowserIcon.gif");
    }
    
    public Action[] getActions(boolean context) {
        return new Action[] {
            SystemAction.get(OpenLocalExplorerAction.class),
        };
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx("org.netbeans.modules.apisupport.beanbrowser");
    }
    
    public Node.Handle getHandle() {
        return new MainNodeHandle();
    }
    private static final class MainNodeHandle implements Node.Handle {
        private static final long serialVersionUID = 1L;
        public Node getNode() throws IOException {
            return new MainNode();
        }
    }
    
    // Key class: DataObject (for a folder to show), or LOOKUP_NODE, or REPOSITORY
    private static class MainChildren extends Children.Keys {
        
        private static final Object LOOKUP_NODE = "lookupNode"; // NOI18N
        private static final Object REPOSITORY = "repository"; // NOI18N
        
        protected void addNotify() {
            refreshKeys();
        }
        
        private void refreshKeys() {
            List l = new LinkedList();
            l.add(LOOKUP_NODE);
            l.add(REPOSITORY);
            String[] folders = {
                "UI/Services", // NOI18N
                "UI/Runtime", // NOI18N
                "", // NOI18N
            };
            for (int i = 0; i < folders.length; i++) {
                FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource(folders[i]);
                if (fo != null) {
                    try {
                        l.add(DataObject.find(fo));
                    } catch (DataObjectNotFoundException e) {
                        ErrorManager.getDefault().notify(e);
                    }
                }
            }
            setKeys(l);
        }
        
        protected void removeNotify() {
            setKeys(Collections.EMPTY_SET);
        }
        
        protected Node[] createNodes(Object key) {
            if (key == LOOKUP_NODE) {
                return new Node[] {new LookupNode()};
            } else if (key == REPOSITORY) {
                return new Node[] {Wrapper.make(RepositoryNodeFactory.getDefault().repository(DataFilter.ALL))};
            } else {
                DataObject d = (DataObject)key;
                Node n = d.getNodeDelegate();
                final String title;
                if (d.getPrimaryFile().isRoot()) {
                    title = "Root of system filesystem";
                } else {
                    title = d.getPrimaryFile().getPath() + " in system filesystem";
                }
                Node n2 = new FilterNode(n) {
                    public String getDisplayName() {
                        return title;
                    }
                };
                return new Node[] {Wrapper.make(n2)};
            }
        }
        
    }
    
}

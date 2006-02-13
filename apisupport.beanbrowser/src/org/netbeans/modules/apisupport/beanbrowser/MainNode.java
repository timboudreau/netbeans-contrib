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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Action;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.windows.TopComponent;

/** Node to browse various important stuff. */
public class MainNode extends AbstractNode {
    
    public MainNode() {
        super(new MainChildren());
        setName("BeanBrowserMainNode");
        setDisplayName("NetBeans Runtime");
        setIconBaseWithExtension("org/netbeans/modules/apisupport/beanbrowser/BeanBrowserIcon.gif");
    }
    
    public Action[] getActions(boolean context) {
        return new Action[0];
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
        
        protected void addNotify() {
            refreshKeys();
        }
        
        private void refreshKeys() {
            List l = new LinkedList();
            l.add(LOOKUP_NODE);
            l.add(Repository.getDefault().getDefaultFileSystem());
            File[] roots = File.listRoots();
            if (roots != null) {
                for (int i = 0; i < roots.length; i++) {
                    FileObject f = FileUtil.toFileObject(roots[i]);
                    if (f != null) {
                        l.add(f);
                    }
                }
            }
            l.add(TopComponent.getRegistry());
            setKeys(l);
        }
        
        protected void removeNotify() {
            setKeys(Collections.EMPTY_SET);
        }
        
        protected Node[] createNodes(Object key) {
            if (key == LOOKUP_NODE) {
                return new Node[] {new LookupNode()};
            } else if (key instanceof FileSystem) {
                Node orig;
                try {
                    orig = DataObject.find(((FileSystem) key).getRoot()).getNodeDelegate();
                } catch (DataObjectNotFoundException e) {
                    throw new AssertionError(e);
                }
                return new Node[] {Wrapper.make(new FilterNode(orig) {
                    public String getDisplayName() {
                        return "System FS (All Layers)";
                    }
                })};
            } else if (key instanceof FileObject) {
                final FileObject f = (FileObject) key;
                Node orig;
                try {
                    orig = DataObject.find(f).getNodeDelegate();
                } catch (DataObjectNotFoundException e) {
                    throw new AssertionError(e);
                }
                return new Node[] {Wrapper.make(new FilterNode(orig) {
                    public String getDisplayName() {
                        return FileUtil.getFileDisplayName(f);
                    }
                })};
            } else if (key instanceof TopComponent.Registry) {
                return new Node[] {new TopComponentsNode((TopComponent.Registry) key)};
            } else {
                throw new AssertionError(key);
            }
        }
        
    }
    
}

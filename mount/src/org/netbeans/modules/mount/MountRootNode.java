/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.mount;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.queries.VisibilityQuery;
import org.openide.ErrorManager;
import org.openide.actions.CutAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.RenameAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.ChangeableDataFilter;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.WeakListeners;
import org.openide.util.actions.SystemAction;


/**
 * Shows mounts.
 * @author Jesse Glick
 */
final class MountRootNode extends AbstractNode {
    
    public MountRootNode() {
        super(new MountChildren());
        // XXX add Index cookie so user can reorder mounts
        // XXX add DummyProject.instance to its lookup, just for fun
    }

    public String getName() {
        return "mount"; // NOI18N
    }

    public String getDisplayName() {
        return "Filesystems";
    }

    //XXX: public Image getIcon(int type) {}

    public boolean canRename() {
        return false;
    }

    public boolean canDestroy() {
        return false;
    }

    public boolean canCut() {
        return false;
    }

    public boolean canCopy() {
        return false;
    }
    
    public Action[] getActions(boolean context) {
        return new Action[] {
            new AddMountAction(false),
            new AddMountAction(true),
            // XXX add various other context actions to build etc.
        };
    }

    public Action getPreferredAction() {
        return null;
    }

    public Node.Handle getHandle() {
        return new MountHandle();
    }
    
    private static final class MountHandle implements Node.Handle {
        private static final long serialVersionUID = 1L;
        public MountHandle() {}
        public Node getNode() throws IOException {
            return new MountRootNode();
        }
    }

    public Node cloneNode() {
        return new MountRootNode();
    }
    
    /**
     * Holds a list of mount roots and creates one node for each.
     */
    private static final class MountChildren extends Children.Keys/*<FileObject>*/ implements ChangeListener {
        
        public MountChildren() {}

        protected Node[] createNodes(Object key) {
            FileObject fo = (FileObject) key;
            DataFolder folder = DataFolder.findFolder(fo);
            Children ch = folder.createNodeChildren(MountFilter.DEFAULT);
            return new Node[] {new MountNode(folder.new FolderNode(ch))};
        }
        
        private void refreshKeys() {
            setKeys(MountList.DEFAULT.getMounts());
        }

        protected void addNotify() {
            super.addNotify();
            MountList.DEFAULT.addChangeListener(this);
            refreshKeys();
        }

        protected void removeNotify() {
            MountList.DEFAULT.removeChangeListener(this);
            super.removeNotify();
        }

        public void stateChanged(ChangeEvent e) {
            refreshKeys();
        }
        
    }
    
    /**
     * Represents the root node of one mount point.
     */
    private static final class MountNode extends FilterNode {
        
        public MountNode(Node orig) {
            super(orig);
        }
        
        private FileObject root() {
            return ((DataObject) getCookie(DataObject.class)).getPrimaryFile();
        }
        
        public String getName() {
            try {
                return root().getURL().toExternalForm();
            } catch (FileStateInvalidException e) {
                ErrorManager.getDefault().notify(e);
                return ""; // NOI18N
            }
        }
        
        public String getDisplayName() {
            return FileUtil.getFileDisplayName(root());
        }

        public Action[] getActions(boolean context) {
            List/*<Action>*/ actions = new ArrayList(Arrays.asList(super.getActions(context)));
            actions.remove(SystemAction.get(CutAction.class));
            actions.remove(SystemAction.get(RenameAction.class));
            actions.remove(SystemAction.get(DeleteAction.class));
            actions.add(0, null);
            actions.add(0, new UnmountAction(root()));
            return (Action[]) actions.toArray(new Action[actions.size()]);
        }

        public Action getPreferredAction() {
            return null;
        }

        public boolean canRename() {
            return false;
        }

        public boolean canDestroy() {
            return false;
        }

        public boolean canCut() {
            return false;
        }

    }

    /**
     * Filter used to exclude *.class and specially masked files.
     */
    private static final class MountFilter implements ChangeableDataFilter, ChangeListener {
        
        public static final MountFilter DEFAULT = new MountFilter();
        
        private final List/*<ChangeListener>*/ listeners = new ArrayList();
        
        private MountFilter() {
            VisibilityQuery.getDefault().addChangeListener(WeakListeners.change(this, VisibilityQuery.getDefault()));
        }

        public boolean acceptDataObject(DataObject obj) {
            FileObject fo = obj.getPrimaryFile();
            if (fo.hasExt("class") && FileUtil.toFile(fo) != null) { // NOI18N
                // Hide *.class on disk. Show them inside JARs.
                return false;
            }
            return VisibilityQuery.getDefault().isVisible(fo);
        }
        
        public synchronized void addChangeListener(ChangeListener l) {
            listeners.add(l);
        }
        
        public synchronized void removeChangeListener(ChangeListener l) {
            listeners.remove(l);
        }
        
        private void fireChange() {
            ChangeListener[] ls;
            synchronized (this) {
                if (listeners.isEmpty()) {
                    return;
                }
                ls = (ChangeListener[]) listeners.toArray(new ChangeListener[listeners.size()]);
            }
            ChangeEvent ev = new ChangeEvent(this);
            for (int i = 0; i < ls.length; i++) {
                ls[i].stateChanged(ev);
            }
        }
        
        public void stateChanged(ChangeEvent ev) {
            fireChange();
        }
        
    }

}

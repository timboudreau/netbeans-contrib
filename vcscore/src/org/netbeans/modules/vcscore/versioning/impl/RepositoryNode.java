/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.versioning.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.Collections;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

import org.netbeans.modules.vcscore.versioning.VersioningRepository;
import org.netbeans.modules.vcscore.versioning.VersioningRepositoryEvent;
import org.netbeans.modules.vcscore.versioning.VersioningRepositoryListener;
import org.netbeans.modules.vcscore.versioning.VersioningFileSystem;
import org.openide.filesystems.FileObject;

/**
 * Node that encapsulated the logical structure of more
 * versioning systems - versioning repository.
 *
 * @author  Martin Entlicher
 * @author  Petr Kuzel (encapsulated kids)
 */
final class RepositoryNode extends AbstractNode {

    private static RepositoryNode def;
    
    /** Creates new VersioningDataSystem */
    private RepositoryNode() {
        super(new RepositoryChildren());
        setName(NbBundle.getMessage(RepositoryNode.class, "versioningSystemName"));
        setShortDescription(NbBundle.getMessage(RepositoryNode.class, "VersioningDataSystem.Description"));
        setIconBase("org/netbeans/modules/vcscore/versioning/impl/versioningExplorer");
    }
    
    public static synchronized RepositoryNode getExplorerNode() {
        if (def == null) {
            def = new RepositoryNode();
        }
        return def;
    }

    protected SystemAction[] createActions() {
        return new SystemAction[] {
            //SystemAction.get(org.netbeans.modules.vcscore.actions.VcsMountFromTemplateAction.class)
        };
    }
    
    public Node.Handle getHandle() {
        return new DataSystemHandler();
    }
    
    private static class DataSystemHandler extends Object implements Node.Handle {
        static final long serialVersionUID =-360330468336250300L;

        DataSystemHandler() {
        }

        public Node getNode() {
            return RepositoryNode.getExplorerNode();
        }
        
    }

    /**
     * Maps VersioningRepository's VersioningFileSystems to RootFolderNodes.
     * Listens on repository membership events and on all
     * members intances for "root" property.
     */
    private static class RepositoryChildren extends Children.Keys implements PropertyChangeListener, VersioningRepositoryListener {

        protected void addNotify() {
            setKeys(computeKeys());
            VersioningRepository.getRepository().addRepositoryListener(this);
            List systems = VersioningRepository.getRepository().getVersioningFileSystems();
            for (Iterator it = systems.iterator(); it.hasNext(); ) {
                VersioningFileSystem vs = (VersioningFileSystem) it.next();
                vs.addPropertyChangeListener(this);
            }
        }

        protected void removeNotify() {
            List systems = VersioningRepository.getRepository().getVersioningFileSystems();
            for (Iterator it = systems.iterator(); it.hasNext(); ) {
                VersioningFileSystem vs = (VersioningFileSystem) it.next();
                vs.removePropertyChangeListener(this);
            }
            VersioningRepository.getRepository().removeRepositoryListener(this);
            setKeys(Collections.EMPTY_SET);
        }

        public void propertyChange (PropertyChangeEvent ev) {
            //System.out.println ("Property change"); // NOI18N
            RepositoryNode ds = (RepositoryNode) getNode();
            if (ds == null) return;

            assert "root".equals(ev.getPropertyName()) == false : "VersioningFileSystem does not support root relocation! At least all setting will be lost.";  // NOI18N
            if (ev.getPropertyName().equals("root")) {
                VersioningFileSystem vs = (VersioningFileSystem) ev.getSource ();
                refreshKey(vs);
            }
        }

        protected Node[] createNodes(Object key) {
            FileObject fo = ((VersioningFileSystem) key).getRoot();
            Node n = new FileSystemNode(fo);
            return new Node[] { n };
        }
        
        public void versioningSystemAdded(VersioningRepositoryEvent re) {
            re.getVersioningFileSystem().addPropertyChangeListener(this);
            setKeys(computeKeys());
        }

        public void versioningSystemRemoved(VersioningRepositoryEvent re) {
            re.getVersioningFileSystem().removePropertyChangeListener(this);
            setKeys(computeKeys());
        }

        private Collection computeKeys() {
            return VersioningRepository.getRepository().getVersioningFileSystems();
        }
    }
    
}

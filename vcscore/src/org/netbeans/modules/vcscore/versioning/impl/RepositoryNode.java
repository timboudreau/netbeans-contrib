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

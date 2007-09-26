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
        getCookieSet().add(new LookupNode.BbMarker());
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
                return new Node[] {LookupNode.globalLookupNode(), LookupNode.actionsGlobalContextLookupNode()};
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

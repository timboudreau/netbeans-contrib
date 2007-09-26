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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

/*
 * ServicesExplorerPanel.java
 *
 * Created on February 16, 2007, 3:46 PM
 */

package org.netbeans.modules.servicestab;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ActionMap;
import javax.swing.JPanel;
import javax.swing.text.DefaultEditorKit;
import org.openide.ErrorManager;
import org.openide.cookies.InstanceCookie;
import org.openide.cookies.InstanceCookie;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.FolderInstance;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 * The Services Tab is a place where some of the services
 * such as Database Connections, WebServices & Deployment servers will
 * be hosted.
 * @author Winston Prakash
 */
public final class ServicesExplorerPanel extends JPanel implements ExplorerManager.Provider, Lookup.Provider{
    
    private static final boolean DEBUG = ErrorManager.getDefault()
            .getInstance(ServicesExplorerPanel.class.getName()).isLoggable(ErrorManager.INFORMATIONAL);
    
    private final ExplorerManager manager = new ExplorerManager();
    private final Lookup lookup;
    
    private final BeanTreeView treeView = new BeanTreeView();
    
    private final ServicesChildren servicesChildren = new ServicesChildren();
    
    /** Creates a new instance of ServicesExplorerPanel */
    public ServicesExplorerPanel() {
         
        ActionMap map = getActionMap();
        map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(manager));
        map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(manager));
        map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(manager));
        map.put("delete", ExplorerUtils.actionDelete(manager, true)); // or false // NOI18N
                 
        lookup = ExplorerUtils.createLookup(manager, map);
        
        treeView.setRootVisible(false);
        
        setLayout(new BorderLayout());
        add(treeView, BorderLayout.CENTER);
        
        //manager.addPropertyChangeListener(servicesManagerListener);
        
        Node rootNode = new AbstractNode(servicesChildren);
        rootNode.setName("Hidden Services Root Node"); // NOI18N
        manager.setRootContext(rootNode);
    }
    
    public Lookup getLookup() {
        return lookup;
    }
    
    public ExplorerManager getExplorerManager() {
        return manager;
    }
    
    /**
     * Initialize the Service Tab after creating the root nodes as 
     * defined in the layer file  
     */
    public void initialize(){
        FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource("UI/ServiceTab");
        if(fo != null){
            DataFolder folder = DataFolder.findFolder(fo);
            final ServicesFolder servicesFolder = new ServicesFolder(folder);
            servicesFolder.recreate();
            servicesFolder.instanceFinished();
            fo.addFileChangeListener(new org.openide.filesystems.FileChangeAdapter() {
                public void fileDeleted(org.openide.filesystems.FileEvent evt) {
                    servicesFolder.recreate();
                }
                
                public void fileDataCreated(org.openide.filesystems.FileEvent evt) {
                    servicesFolder.recreate();
                }
            });
        }
    }
    
    /**
     * Activate the Services Tab explorer actions when explorer panel is added
     */
    public void addNotify() {
        super.addNotify();
        ExplorerUtils.activateActions(manager, true);
    }
    
    /**
     * DeActivate the Services Tab explorer actions when explorer panel is added
     */
    public void removeNotify() {
        ExplorerUtils.activateActions(manager, false);
        super.removeNotify();
    }
    
    private class ServicesChildren extends org.openide.nodes.Children.Keys {
        protected Node[] createNodes(Object key) {
            if(key instanceof Node) {
                final Node node = (Node)key;
                
                // XXX Trick to expand the node
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if(treeView != null) {
                            treeView.expandNode(node);
                        }
                    }
                });
                
                return new Node[] {node};
            }
            
            return new Node[0];
        }
        
        protected void removeNotify() {
            setKeys(java.util.Collections.EMPTY_SET);
        }
        
        public void updateKeys(java.util.Collection keys) {
            setKeys(keys);
        }
    }
    
    /** This class is used to add the nodes specified in the layer file's
     * <code>ServicesTab</code> folder.
     */
    private final class ServicesFolder extends FolderInstance {
        
        public ServicesFolder(final DataFolder folder) {
            super(folder);
        }
        
        /** Updates the <code>Services Tab</code> with nodes specified in the children of
         *  ServicesTab folder in the layer file.
         */
        protected Object createInstance(InstanceCookie[] cookies) throws IOException, ClassNotFoundException {
            final List<Node> nodes = new ArrayList<Node>();
            for(int i=0; i< cookies.length; i++){
                try {
                    Object obj = cookies[i].instanceCreate();
                    if (obj instanceof Node) {
                        Node node =  (Node) obj;
                        nodes.add(node);
                        treeView.expandNode(node);
                    }
                } catch (ClassNotFoundException ex) {
                    ErrorManager.getDefault().notify(ex);
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
            
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ServicesExplorerPanel.this.servicesChildren.updateKeys(nodes);
                }
            });
            return nodes;
        }
        
    }
    
}

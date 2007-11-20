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

package org.netbeans.modules.j2ee.oc4j.nodes;

import java.awt.Image;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.status.ProgressObject;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport.ServerIcon;
import org.netbeans.modules.j2ee.oc4j.OC4JDeploymentManager;
import org.netbeans.modules.j2ee.oc4j.config.ds.OC4JDatasourceManager;
import org.netbeans.modules.j2ee.oc4j.nodes.actions.OpenURLAction;
import org.netbeans.modules.j2ee.oc4j.nodes.actions.OpenURLActionCookie;
import org.netbeans.modules.j2ee.oc4j.nodes.actions.RefreshModulesAction;
import org.netbeans.modules.j2ee.oc4j.nodes.actions.RefreshModulesCookie;
import org.netbeans.modules.j2ee.oc4j.nodes.actions.Refreshable;
import org.netbeans.modules.j2ee.oc4j.nodes.actions.UndeployModuleAction;
import org.netbeans.modules.j2ee.oc4j.nodes.actions.UndeployModuleCookie;
import org.netbeans.modules.j2ee.oc4j.util.OC4JPluginProperties;
import org.netbeans.modules.j2ee.oc4j.util.OC4JPluginUtils;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;

/**
 * Universal extensible node.
 *
 * @author Michal Mocnak
 */
public class OC4JItemNode extends AbstractNode {
    
    private ItemType type;
    
    private static final String HTTP_HEADER = "http://";
    
    private static final int TIMEOUT = 30000;
    
    private OC4JItemNode(Children children, final TargetModuleID module, final Lookup lookup, final ItemType type) {
        super(children);
        this.type = type;
        
        if(type.equals(ItemType.J2EE_APPLICATION_FOLDER) ||
                type.equals(ItemType.REFRESHABLE_FOLDER)) {
            getCookieSet().add(new RefreshModulesCookie() {
                public void refresh() {
                    Children children = getChildren();
                    if(children instanceof Refreshable)
                        ((Refreshable)children).updateKeys();
                }
            });
        } else if(type.equals(ItemType.J2EE_APPLICATION)) {
            getCookieSet().add(new OpenURLActionCookie() {
                public String getWebURL() {
                    if(module == null || lookup == null)
                        return null;
                    
                    try {
                        OC4JDeploymentManager dm = lookup.lookup(OC4JDeploymentManager.class);
                        String app = null;
                        
                        if(module.getWebURL() != null)
                            app = module.getWebURL();
                        
                        for(TargetModuleID id:module.getChildTargetModuleID()) {
                            if(id.getWebURL() != null) {
                                app = id.getWebURL();
                                break;
                            }
                        }
                        
                        InstanceProperties ip = dm.getInstanceProperties();
                        
                        String host = ip.getProperty(OC4JPluginProperties.PROPERTY_HOST);
                        String httpPort = ip.getProperty(InstanceProperties.HTTP_PORT_NUMBER);
                        
                        if(app == null || host == null || httpPort == null)
                            return null;
                        
                        return HTTP_HEADER + host + ":" + httpPort + app;
                    } catch (Throwable t) {
                        return null;
                    }
                }
            });
            getCookieSet().add(new UndeployModuleCookie() {
                private boolean isRunning = false;
                
                public Task undeploy() {
                    final OC4JDeploymentManager dm = lookup.lookup(OC4JDeploymentManager.class);
                    final ProgressHandle handle = ProgressHandleFactory.createHandle(NbBundle.getMessage(OC4JItemNode.class,
                            "LBL_UndeployProgress", OC4JPluginUtils.getName(module)));
                    
                    Runnable r = new Runnable() {
                        public void run() {
                            isRunning = true;
                            
                            // Save the current time so that we can deduct that the undeploy
                            // failed due to timeout
                            long start = System.currentTimeMillis();
                            
                            ProgressObject o = dm.undeploy(new TargetModuleID[] {module});
                            
                            while(!o.getDeploymentStatus().isCompleted() && System.currentTimeMillis() - start < TIMEOUT) {
                                try {
                                    Thread.sleep(2000);
                                } catch(InterruptedException ex) {
                                    // Nothing to do
                                }
                            }
                            
                            handle.finish();
                            isRunning = false;
                        }
                    };
                    
                    handle.start();
                    return RequestProcessor.getDefault().post(r);
                }
                
                public synchronized boolean isRunning() {
                    return isRunning;
                }
            });
        } else if (type.equals(ItemType.JDBC_NATIVE_DATASOURCES) ||
                type.equals(ItemType.JDBC_MANAGED_DATASOURCES) ||
                type.equals(ItemType.CONNECTION_POOLS)) {
            getCookieSet().add(new UndeployModuleCookie() {
                private boolean isRunning = false;
                
                public Task undeploy() {
                    final OC4JDeploymentManager dm = lookup.lookup(OC4JDeploymentManager.class);
                    final ProgressHandle handle = ProgressHandleFactory.createHandle(NbBundle.getMessage(OC4JItemNode.class,
                            "LBL_UndeployProgress", getDisplayName()));
                    
                    Runnable r = new Runnable() {
                        public void run() {
                            isRunning = true;
                            
                            OC4JDatasourceManager dsManager = new OC4JDatasourceManager(dm);
                            
                            // Undeploying
                            if(type.equals(ItemType.JDBC_NATIVE_DATASOURCES)) {
                                dsManager.undeployNativeDataSource(getDisplayName());
                            } else if (type.equals(ItemType.JDBC_MANAGED_DATASOURCES)) {
                                dsManager.undeployManagedDataSource(getDisplayName());
                            } else if (type.equals(ItemType.CONNECTION_POOLS)) {
                                dsManager.undeployConnectionPool(getDisplayName());
                            }
                            
                            handle.finish();
                            isRunning = false;
                        }
                    };
                    
                    handle.start();
                    return RequestProcessor.getDefault().post(r);
                }
                
                public synchronized boolean isRunning() {
                    return isRunning;
                }
            });
        }
    }
    
    public OC4JItemNode(Lookup lookup, TargetModuleID module, ItemType type) {
        this(Children.LEAF, module, lookup, type);
        setDisplayName(OC4JPluginUtils.getName(module));
    }
    
    public OC4JItemNode(Lookup lookup, Children children, String name, ItemType type) {
        this(children, null, lookup, type);
        setDisplayName(name);
    }
    
    public Image getIcon(int type) {
        if(this.type.equals(ItemType.J2EE_APPLICATION_FOLDER)) {
            return UISupport.getIcon(ServerIcon.EAR_FOLDER);
        } else if(this.type.equals(ItemType.J2EE_APPLICATION) ||
                this.type.equals(ItemType.J2EE_APPLICATION_SYSTEM)) {
            return UISupport.getIcon(ServerIcon.EAR_ARCHIVE);
        } else if(this.type.equals(ItemType.JDBC_MANAGED_DATASOURCES) ||
                this.type.equals(ItemType.JDBC_NATIVE_DATASOURCES)) {
            return Utilities.loadImage(JDBC_RESOURCE_ICON);
        } else if(this.type.equals(ItemType.CONNECTION_POOLS)) {
            return Utilities.loadImage(CONNECTION_POOL_ICON);
        } else {
            return getIconDelegate().getIcon(type);
        }
    }
    
    public Image getOpenedIcon(int type) {
        if(this.type.equals(ItemType.J2EE_APPLICATION_FOLDER)) {
            return UISupport.getIcon(ServerIcon.EAR_OPENED_FOLDER);
        } else if(this.type.equals(ItemType.J2EE_APPLICATION) ||
                this.type.equals(ItemType.J2EE_APPLICATION_SYSTEM) ||
                this.type.equals(ItemType.JDBC_MANAGED_DATASOURCES) ||
                this.type.equals(ItemType.JDBC_NATIVE_DATASOURCES) ||
                this.type.equals(ItemType.CONNECTION_POOLS)) {
            return getIcon(type);
        } else {
            return getIconDelegate().getOpenedIcon(type);
        }
    }
    
    private Node getIconDelegate() {
        return DataFolder.findFolder(Repository.getDefault().getDefaultFileSystem().getRoot()).getNodeDelegate();
    }
    
    public javax.swing.Action[] getActions(boolean context) {
        SystemAction[] actions = new SystemAction[] {};
        
        if(type.equals(ItemType.J2EE_APPLICATION_FOLDER)
                || type.equals(ItemType.REFRESHABLE_FOLDER)) {
            actions = new SystemAction[] {
                SystemAction.get(RefreshModulesAction.class)
            };
        } else if(type.equals(ItemType.J2EE_APPLICATION)) {
            actions = new SystemAction[] {
                SystemAction.get(OpenURLAction.class),
                SystemAction.get(UndeployModuleAction.class)
            };
        } else if(type.equals(ItemType.JDBC_NATIVE_DATASOURCES) ||
                type.equals(ItemType.JDBC_MANAGED_DATASOURCES) ||
                type.equals(ItemType.CONNECTION_POOLS)) {
            actions = new SystemAction[] {
                SystemAction.get(UndeployModuleAction.class)
            };
        }
        
        return actions;
    }
    
    /* Creates and returns the instance of the node
     * representing the status 'WAIT' of the node.
     * It is used when it spent more time to create elements hierarchy.
     * @return the wait node.
     */
    public static Node createWaitNode() {
        AbstractNode n = new AbstractNode(Children.LEAF);
        n.setName(NbBundle.getMessage(OC4JItemNode.class, "LBL_WaitNode_DisplayName")); //NOI18N
        n.setIconBaseWithExtension("org/openide/src/resources/wait.gif"); // NOI18N
        return n;
    }
    
    private static final String JDBC_RESOURCE_ICON = "org/netbeans/modules/j2ee/oc4j/resources/JDBCResource.gif";
    private static final String CONNECTION_POOL_ICON = "org/netbeans/modules/j2ee/oc4j/resources/ConnectionPool.gif";
    
    public static class ItemType {
        
        public static final ItemType J2EE_APPLICATION_FOLDER = new ItemType();
        public static final ItemType J2EE_APPLICATION = new ItemType();
        public static final ItemType J2EE_APPLICATION_SYSTEM = new ItemType();
        public static final ItemType REFRESHABLE_FOLDER = new ItemType();
        public static final ItemType JDBC_MANAGED_DATASOURCES = new ItemType();
        public static final ItemType JDBC_NATIVE_DATASOURCES = new ItemType();
        public static final ItemType CONNECTION_POOLS = new ItemType();
        
        private ItemType() {}
    }
}
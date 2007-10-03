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

/*
 * WS70WebModulesChildren.java
 *
 */

package org.netbeans.modules.j2ee.sun.ws7.nodes;

import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;

import javax.enterprise.deploy.shared.ModuleType;

import org.openide.util.Lookup;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

import java.util.TreeSet;


/**
 *
 * @author Administrator
 */
public class WS70WebModulesChildren extends Children.Keys{
    private static final String WAIT_NODE = "wait_node"; //NOI18N
    private Lookup lookup;
    /** Creates a new instance of WS70WebModulesChildren */
    public WS70WebModulesChildren(Lookup lookup) {
        this.lookup = lookup;
    }
    
    protected Node[] createNodes(Object key){
        if (key instanceof WS70WebModule){
            WS70WebModule module = (WS70WebModule)key;
            WS70WebModuleNode node = new WS70WebModuleNode(module);            
            return new Node[]{node};
        }
        if (key instanceof String && key.equals(WAIT_NODE)){
            return new Node[]{createWaitNode ()};
        }
        return null;
    }
    
    public void updateKeys(){        
        Target target = (Target)lookup.lookup(Target.class);
        DeploymentManager dm = (DeploymentManager)lookup.lookup(DeploymentManager.class);        
        TreeSet ts = new TreeSet();
        ts.add(WAIT_NODE);       
        setKeys(ts); 
        RequestProcessor.getDefault().post(new Runnable() {
            public void run () {
                DeploymentManager manager = (DeploymentManager)lookup.lookup(DeploymentManager.class);
                Target target = (Target)lookup.lookup(Target.class);                        
                TreeSet list = new TreeSet(new WS70WmComparator()); 
                if (target != null){

                    try{
                        TargetModuleID[] modules = manager.getAvailableModules(ModuleType.WAR, new Target[]{target}); 
                         for (int i = 0; i < modules.length; i ++){
                             list.add(new WS70WebModule(manager, modules[i]));
                         }
                       }
                    
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }       
               setKeys(list);
       
            }
        }, 0);
    }
    
    protected void addNotify() {
        updateKeys();
    }
   
    protected void removeNotify() {
        setKeys(java.util.Collections.EMPTY_SET);
    }    
    private Node createWaitNode () {
        AbstractNode n = new AbstractNode(Children.LEAF);
        n.setName(NbBundle.getMessage(WS70WebModulesChildren.class, "LBL_WaitNode_DisplayName")); //NOI18N
        n.setIconBaseWithExtension("org/openide/src/resources/wait.gif"); // NOI18N
        return n;
    } 
    public static class WS70WmComparator implements java.util.Comparator{
        
       public int compare(Object o1, Object o2) {
            WS70WebModule wm1 = (WS70WebModule) o1;
            WS70WebModule wm2 = (WS70WebModule) o2;            
            return wm1.getName().compareTo(wm2.getName());
        }            
        
    }    

}


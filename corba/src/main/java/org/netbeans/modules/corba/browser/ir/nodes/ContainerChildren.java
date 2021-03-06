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

package org.netbeans.modules.corba.browser.ir.nodes;

import org.omg.CORBA.*;
import java.util.Vector;
import java.io.*;
import org.openide.nodes.Node;
import org.openide.*;
import org.netbeans.modules.corba.*;
import org.netbeans.modules.corba.browser.ir.*;
import org.netbeans.modules.corba.browser.ir.nodes.keys.IRContainedKey;



public class ContainerChildren extends Children {


    private Container container;

    public static final boolean DEBUG = false;
    //public static final boolean DEBUG = true;




    public ContainerChildren (Container contain) {
        super ();
        this.container = contain;
    }
    
    public void addNotify () {
        if (DEBUG)
            System.out.println ("addNotify ()");
        synchronized (this) {
            if (this.state == SYNCHRONOUS) {
                this.createKeys();
                this.state = INITIALIZED;
            }
            else {
                this.state = TRANSIENT;
                this.waitNode = new WaitNode ();
                this.add ( new Node[] { this.waitNode});
                org.netbeans.modules.corba.browser.ir.IRRootNode.getDefault().performAsync (this);
            }
        }
    }
    
    
    public void createKeys () {
        try {
            Contained[] contained = container.contents (DefinitionKind.dk_all, true);
            java.lang.Object[] keys = new java.lang.Object [contained.length];
            for (int i=0; i<keys.length; i++)
                keys[i] = new IRContainedKey ( contained[i]);
            setKeys (keys);
        }catch (final SystemException e) {
            if (DEBUG)
                e.printStackTrace();
            setKeys ( new java.lang.Object[0]);
            java.awt.EventQueue.invokeLater ( new Runnable () {
                public void run () {
                    TopManager.getDefault().notify ( new NotifyDescriptor.Message (e.toString(),NotifyDescriptor.Message.ERROR_MESSAGE));
                }});
        }
    }
    
    /*
      public void setRootNode (IRRootNode node) {
      _root_node = node;
      }
     
      public IRRootNode getRootNode () {
      return _root_node;
      }
     */
    
    public org.openide.nodes.Node[] createNodes (java.lang.Object key) {
        DefinitionKind dk = null;
        if (key != null){
            if (key instanceof IRContainedKey) {
                try{
                    Node[] nodes = new Node[1];
                    Contained contained = ((IRContainedKey)key).contained;
		    // Workaround to allow operation on JDK where Sun's implementation
		    // is not removed from rt.jar or third party ORB is not in boot classpath.
		    // In this situations the Sun's DefinitionKind is taken and it is out of date
		    // which causes RuntimeException durring run!!!!!!!!
		    if (contained._is_a("IDL:omg.org/CORBA/AbstractInterfaceDef:1.0") || contained._is_a("IDL:omg.org/CORBA/AbstractInterfaceDef:2.3")) {
		      nodes[0] = new IRInterfaceDefNode (ContainerHelper.narrow (contained), true);
		      return nodes;
		    }
		    dk = contained.def_kind();
                    if (dk == DefinitionKind.dk_Module) {
                        Container container = ContainerHelper.narrow (contained);
                        nodes[0] = new IRModuleDefNode (container);
                        return nodes;
                    }
                    else if (dk == DefinitionKind.dk_Interface) {
                        Container container = ContainerHelper.narrow (contained);
                        nodes[0] = new IRInterfaceDefNode (container);
                        return nodes;
                    }
                    else if (dk == DefinitionKind.dk_Exception){
                        nodes[0] = new IRExceptionDefNode (contained);
                        return nodes;
                    }
                    else if (dk == DefinitionKind.dk_Struct){
                        nodes[0] = new IRStructDefNode (contained);
                        return nodes;
                    }
                    else if (dk == DefinitionKind.dk_Union){
                        nodes[0] = new IRUnionDefNode (contained);
                        return nodes;
                    }
                    else if (dk == DefinitionKind.dk_Constant) {
                        nodes[0] = new IRConstantDefNode (contained);
                        return nodes;
                    }
                    else if (dk == DefinitionKind.dk_Attribute){
                        nodes[0] = new IRAttributeDefNode(contained);
                        return nodes;
                    }
                    else if (dk == DefinitionKind.dk_Operation){
                        nodes[0] = new IROperationDefNode(contained);
                        return nodes;
                    }
                    else if (dk == DefinitionKind.dk_Alias){
                        nodes[0] = new IRAliasDefNode(contained);
                        return nodes;
                    }
                    else if (dk== DefinitionKind.dk_Enum){
                        nodes[0] = new IREnumDefNode(contained);
                        return nodes;
                    }
                    else if (dk == DefinitionKind.dk_ValueBox) {
                        nodes[0] = new IRValueBoxDefNode(contained);
                        return nodes;
                    }
                    else if (dk == DefinitionKind.dk_Value) {
                        nodes[0] = new IRValueDefNode (contained);
                        return nodes;
                    }
                    else if (dk == DefinitionKind.dk_Native){
                        nodes[0] = new IRNativeDefNode(contained);
                        return nodes;
                    }
                }catch(final Exception t){
                    if (DEBUG)
                        t.printStackTrace();
                    java.awt.EventQueue.invokeLater ( new Runnable () {
                        public void run () {
                            TopManager.getDefault().notify ( new NotifyDescriptor.Message (t.toString(),NotifyDescriptor.Message.ERROR_MESSAGE));
                        }});
                        return new Node[0];
                }
            }
        }
        return new Node[]{new IRUnknownTypeNode()};
    }
    
    public void setContainer (Container c) {
        container = c;
    }
    
}


/*
 * $Log
 * $
 */

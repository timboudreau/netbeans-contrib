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

package org.netbeans.modules.rmi.registry;

import org.openide.actions.PropertiesAction;
import org.openide.actions.ToolsAction;
import org.openide.nodes.*;
import org.openide.src.*;
import org.openide.src.nodes.*;
import org.openide.util.actions.*;
import org.openide.util.HelpCtx;

/**
 *
 * @author  mryzl
 */

public class InterfaceNode extends ClassElementNode implements Node.Cookie {

    private static final boolean DEBUG = false;

    Class cl;

    /** Creates new InterfaceNode. */
    public InterfaceNode(Class cl, ClassElement ce) {
        super(ce, new ClassChildren(new ENFactory(), ce), false);
        this.cl = cl;

        systemActions = new SystemAction[] {
                            SystemAction.get(org.netbeans.modules.rmi.registry.CreateClientAction.class),
                            SystemAction.get(org.netbeans.modules.rmi.registry.SaveInterfaceAction.class),
                            null,
                            SystemAction.get(org.openide.actions.ToolsAction.class),
                            SystemAction.get(org.openide.actions.PropertiesAction.class),
                        };
        getCookieSet().add(this);
    }

    public Class getInterface() {
        return cl;
    }

    public String getURLString() {
        // potrebuju registry item a service item
        try {
            Node snode = getParentNode(); // service item node
            if (DEBUG) System.err.println("InterfaceNode.getURLString(): snode = " + snode); // NOI18B
            Node rnode = snode.getParentNode(); // registry item node
            if (DEBUG) System.err.println("InterfaceNode.getURLString(): rnode = " + rnode); // NOI18B
            ServiceItem sitem = (ServiceItem) snode.getCookie(ServiceItem.class);
            if (DEBUG) System.err.println("InterfaceNode.getURLString(): sitem = " + sitem); // NOI18B
            RegistryItem ritem = (RegistryItem) rnode.getCookie(RegistryItem.class);
            if (DEBUG) System.err.println("InterfaceNode.getURLString(): ritem = " + ritem); // NOI18B
            return ritem.getURLString() + sitem.getName();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public HelpCtx getHelpCtx () {
        return new HelpCtx (InterfaceNode.class.getName());
    }
    
    /** Test for equality.
     * @return <code>true</code> if the represented {@link Element}s are equal
     */
    public boolean equals(Object o) {
        return (super.equals(o)) && (o instanceof InterfaceNode) && (((InterfaceNode)o).getURLString().equals(getURLString()));
    }
    
    static final class ENFactory extends DefaultFactory {
        
        /** Array of the actions for element nodes */
        private static SystemAction[] defaultActions;
        
        public ENFactory() {
            super(false);
        }
        
        /** Make a node representing a class.
         * @param element the class
         * @return a class node instance
         */
        public Node createClassNode(ClassElement element) {
            ClassElementNode n = new ClassElementNode(element, new ClassChildren(new ENFactory(), element), false);
            n.setActions(getDefaultActions());
            return n;
        }
        
        /** Make a node representing a constructor.
         * @param element the constructor
         * @return a constructor node instance
         */
        public Node createConstructorNode(ConstructorElement element) {
            ConstructorElementNode n = new ConstructorElementNode(element, false);
            n.setDefaultAction(SystemAction.get(PropertiesAction.class));
            n.setActions(getDefaultActions());
            return n;
        }
        
        /** Make a node representing a method.
         * @param element the method
         * @return a method node instance
         */
        public Node createMethodNode(MethodElement element) {
            MethodElementNode n = new MethodElementNode(element, false);
            n.setDefaultAction(SystemAction.get(PropertiesAction.class));
            n.setActions(getDefaultActions());
            return n;
        }
        
        /** Make a node representing a field.
         * @param element the field
         * @return a field node instance
         */
        public Node createFieldNode(FieldElement element) {
            FieldElementNode n = new FieldElementNode(element, false);
            n.setDefaultAction(SystemAction.get(PropertiesAction.class));
            n.setActions(getDefaultActions());
            return n;
        }
        
        /** Convenience method for obtaining default actions of nodes */
        SystemAction[] getDefaultActions () {
            if (defaultActions == null) {
                defaultActions = new SystemAction[] {
                                     SystemAction.get(ToolsAction.class),
                                     SystemAction.get(PropertiesAction.class),
                                 };
            }
            return defaultActions;
        }
        
    }
}


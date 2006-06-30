/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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


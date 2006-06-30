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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.structural;

import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
import javax.swing.Action;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Children.Keys;
import org.openide.util.actions.SystemAction;


/**
 *
 * @author Jan Lahoda
 */
public class StructuralNode extends BeanNode implements PropertyChangeListener {
    
    public StructuralNode(StructuralElement el) throws IntrospectionException {
        this(el, el.getSubElements().size() == 0 ? Children.LEAF : new StructuralNodeChildren(el));
    }
    
    public StructuralNode(StructuralElement el, Children children) throws IntrospectionException {
        super(el, children);
        
        getCookieSet().add(new PositionCookie() {
            public SourcePosition getPosition() {
                return getOpeningPosition();
            }
        });
        
        el.addPropertyChangeListener(this);
    }
    
    public SystemAction[] createActions() {
        return new SystemAction[] {
            SystemAction.get(GoToSourceAction.class),
            null,
            SystemAction.get(PropertiesAction.class),
        };
    }
    
    public Action getPreferredAction() {
        Action[] actions = getActions(false);
        
        if (actions.length > 0 && actions[0] != null)
            return actions[0];
        
        return super.getPreferredAction();
    }
    
    public SourcePosition getOpeningPosition() {
        return null;
    }
    
//    protected boolean isOpeningPositionSupported
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (StructuralElement.SUB_ELEMENTS.equals(evt.getPropertyName())) {
            StructuralElement el = (StructuralElement) getBean();
            
            if (getChildren() == Children.LEAF) {
                if (el.getSubElements().size() != 0) {
                    setChildren(new StructuralNodeChildren(el));
                }
            } else {
                if (el.getSubElements().size() == 0) {
                    setChildren(Children.LEAF);
                }
            }
        }
    }
    
    public static class StructuralNodeChildren extends Keys implements PropertyChangeListener {
        
        private StructuralElement el;
        
        public StructuralNodeChildren (StructuralElement el) {
            this.el = el;
            
            el.addPropertyChangeListener(this); //!!!WARNING: POSSIBLE MEMORY LEAK!!!!!!
        }
        
        public void addNotify() {
            doSetKeys();
        }
        
        protected void doSetKeys() {
            setKeys(getElement().getSubElements());
        }
        
        public void removeNotify() {
            setKeys(Collections.EMPTY_LIST);
        }
        
        protected StructuralElement getElement() {
            return el;
        }
        
        /** Create nodes for a given key.
         * @param key the key
         * @return child nodes for this key or null if there should be no
         *    nodes for this key
         */
        protected Node[] createNodes(Object key) {
            return new Node[] {StructuralNodeFactory.createNode((StructuralElement) key)};
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (StructuralElement.SUB_ELEMENTS.equals(evt.getPropertyName()) && isInitialized())
                doSetKeys();
        }
        
    }
    
}

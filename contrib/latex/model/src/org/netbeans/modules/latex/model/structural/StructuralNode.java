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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
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
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.structural;

import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
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
public class StructuralNode<T extends StructuralElement> extends BeanNode<T> implements PropertyChangeListener {
    
    public StructuralNode(T el) throws IntrospectionException {
        this(el, el.getSubElements().size() == 0 ? Children.LEAF : new StructuralNodeChildren<T>(el));
    }
    
    public StructuralNode(T el, Children children) throws IntrospectionException {
        super(el, children);
        
        getCookieSet().add(new PositionCookie() {
            public SourcePosition getPosition() {
                return getOpeningPosition();
            }
        });
        
        el.addPropertyChangeListener(this);
    }
    
    @Override
    public Action[] getActions(boolean context) {
        return new Action[] {
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
            T el = getBean();
            
            if (getChildren() == Children.LEAF) {
                if (el.getSubElements().size() != 0) {
                    setChildren(new StructuralNodeChildren<T>(el));
                }
            } else {
                if (el.getSubElements().size() == 0) {
                    setChildren(Children.LEAF);
                }
            }
        }
    }
    
    public static class StructuralNodeChildren<T extends StructuralElement> extends Keys<StructuralElement> implements PropertyChangeListener {
        
        private T el;
        
        public StructuralNodeChildren (T el) {
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
            setKeys(Collections.<StructuralElement>emptyList());
        }
        
        protected T getElement() {
            return el;
        }
        
        /** Create nodes for a given key.
         * @param key the key
         * @return child nodes for this key or null if there should be no
         *    nodes for this key
         */
        protected Node[] createNodes(StructuralElement key) {
            return new Node[] {StructuralNodeFactory.createNode(key)};
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (StructuralElement.SUB_ELEMENTS.equals(evt.getPropertyName()) && isInitialized())
                doSetKeys();
        }
        
    }
    
}

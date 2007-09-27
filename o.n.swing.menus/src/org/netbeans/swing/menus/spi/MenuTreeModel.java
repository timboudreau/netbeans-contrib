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
 * MenuTreeModel.java
 *
 * Created on May 21, 2004, 5:12 PM
 */

package org.netbeans.swing.menus.spi;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.tree.TreeModel;
import org.netbeans.swing.menus.impl.TreeNodeMenu;

/**
 * A tree model representing the elements of a menu bar and its associated
 * submenus.  Nodes in the tree may be of any class; model instances are
 * expected to provide an implementation of ComponentProvider which will
 * create components to represent the tree nodes.  A default implementation is
 * provided which expects Action instances as tree nodes, and returns
 * JMenuItems.
 *
 * @author  Tim Boudreau
 */
public interface MenuTreeModel extends TreeModel {
    public ComponentProvider getComponentProvider();
    
    public static abstract class ComponentProvider {
        private final MenuTreeModel mdl;
        public ComponentProvider (MenuTreeModel mdl) {
            this.mdl = mdl;
        }
        
        protected final MenuTreeModel getModel() {
            return mdl;
        }
        
        /**
         * Create a component to represent this node.  
         */
        public abstract JComponent createItemFor (Object node);
        /**
         * Synchronize the state of a component when it is about to be
         * displayed, setting, for instance, enabled/disabled state,
         * mnemonics, etc.  This method may return a different component
         * than it is passed; if it does so, the GUI container should 
         * replace the old component with the returned one.
         */
        public abstract JComponent syncStateOf (Object node, JComponent proxy);
        /**
         * Called when a component has been removed from the component 
         * hierarchy, either because it has been replaced or destroyed.
         */
        public abstract void dispose (JComponent comp, Object node);
    }
}

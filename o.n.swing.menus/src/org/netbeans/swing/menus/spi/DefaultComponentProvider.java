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
 * DefaultComponentProvider.java
 *
 * Created on May 21, 2004, 8:04 PM
 */

package org.netbeans.swing.menus.spi;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import org.netbeans.swing.menus.impl.TreeNodeMenu;

/**
 * Simple ComponentProvider implementation - provides JMenuItems for
 * nodes which are Action instances, and menus for everything else.
 * <p>
 * This class is final; implementations of MenuTreeModel which need additional
 * functionality should create an instanceand proxy to it where appropriate.
 *
 * @author  Tim Boudreau
 */
public final class DefaultComponentProvider extends MenuTreeModel.ComponentProvider {
    public DefaultComponentProvider (MenuTreeModel mdl) {
        super (mdl);
    }

    /**
     * Create a component (such as a JMenuItem) for the passed object, which
     * represents one element from a MenuTreeModel.  This method should always
     * return a new component (or one known not to be in use).
     * <p>
     * This implementation returns JMenuItems if the passed node implements
     * Action, and JMenus for everything else.
     */
    public JComponent createItemFor(Object node) {
        if (node instanceof Action) {
            return new JMenuItem ((Action) node);
        } else {
            TreeNodeMenu result = new TreeNodeMenu (getModel(), node);
            result.setText (node.toString());
            return result;
        }
    }

    /**
     * Synchronize the state of a component representing a tree node with
     * the tree node it represents.  This method may return a replacement
     * component if necessary (for example, if a node which was formerly a
     * menu item now should be represented by a menu).
     * <p>
     * This method should update icons, mnemonics, display names, etc. as
     * appropriate so the component accurately reflects the state of the 
     * action it represents.
     */
    public JComponent syncStateOf(Object node, JComponent proxy) {
        if (node instanceof Action) {
            JMenuItem jm = (JMenuItem) proxy;
            Action act = (Action) node;
            jm.setText ((String) act.getValue(Action.NAME));
            jm.setIcon ((Icon) act.getValue(Action.SMALL_ICON));
        } else if (proxy instanceof TreeNodeMenu) {
            ((TreeNodeMenu) proxy).setText (node.toString());
        }
        return proxy;
    }

    /**
     * Dispose of a component, clearing any references or listeners it may
     * hold.
     */
    public void dispose(JComponent comp, Object node) {
        if (comp instanceof JMenuItem) {
            ((JMenuItem) comp).setAction(null);
        }
    }    
    
}

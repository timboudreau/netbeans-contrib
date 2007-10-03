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

package org.netbeans.modules.vcscore.actions;


import org.openide.nodes.*;
import javax.swing.Icon;

/**
 * TODO Is not it unused?
 * <p>
 * ClusteringAction expects to have the popup menu to be defined
 * consisting of items implementing this interface. The menu is defined on the default filesystem
 * by the module's layer.
 *
 *
 * @author  Milos Kleint
 */
public interface ClusterItemVisualizer {

    /**
     * Display name of the item.
     */
    String getName();

    /**
     * Icon for the item. Is used only if the menu is constructed within the 
     * main menu structure. If it's defined as popup menu, icon is ignored.
     */
    Icon getIcon();
    
    /**
     * Indicates wheather the item enables a forced dialog display.
     * is valid only if the ClusteringAction returns true in isSwitchingEnabled().
     * In such case, items returning true here will be appended "3 dots" when 
     * user presses CTRL and is expected to display some kind of dialog when performed.
     */
    boolean isSwitchable();
    
    boolean isItemEnabled(ClusteringAction action);
    
    void performAction(Node[] nodes, ClusteringAction action);
    
    void setSwitched(boolean userHoldsCtrl);
}


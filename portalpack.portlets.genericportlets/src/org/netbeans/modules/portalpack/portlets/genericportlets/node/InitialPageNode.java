/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.portlets.genericportlets.node;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Action;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.actions.OpenInitialPageAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author satyaranjan
 */
public class InitialPageNode extends AbstractNode {

    private String page;
    public static String VIEW_MODE = "view";
    public static String EDIT_MODE = "edit";
    public static String HELP_MODE = "help";
    private static final String EDIT_IMAGE_ICON_BASE = "org/netbeans/modules/portalpack/portlets/genericportlets/resources/initialeditpage.png"; //NOI18N

    private static final String HELP_IMAGE_ICON_BASE = "org/netbeans/modules/portalpack/portlets/genericportlets/resources/initialhelppage.png"; //NOI18N

    private static final String VIEW_IMAGE_ICON_BASE = "org/netbeans/modules/portalpack/portlets/genericportlets/resources/initialviewpage.png"; //NOI18N


    public InitialPageNode(String jsp, String mode) {
        super(Children.LEAF);
        this.page = jsp;
        if (mode.equals(VIEW_MODE)) {
            setIconBaseWithExtension(VIEW_IMAGE_ICON_BASE);
        }
        if (mode.equals(EDIT_MODE)) {
            setIconBaseWithExtension(EDIT_IMAGE_ICON_BASE);
        }
        if (mode.equals(HELP_MODE)) {
            setIconBaseWithExtension(HELP_IMAGE_ICON_BASE);
        }
    }

    @Override
    public String getDisplayName() {
        return page;
    }

    @Override
    public String getName() {
        return page;
    }

    @Override
    public Action[] getActions(boolean context) {

     //   javax.swing.Action[] actions = super.getActions(context);
       /* javax.swing.Action[] finalActions = new javax.swing.Action[actions.length + 1];
        List actionList = new ArrayList();
        actionList.add(SystemAction.get(OpenInitialPageAction.class));
        for (int i = 0; i < actions.length; i++) {
            actionList.add(actions[i]);
        }
        
        return (javax.swing.Action[]) actionList.toArray(new javax.swing.Action[0]);*/
        
        javax.swing.Action[] actions = {SystemAction.get(OpenInitialPageAction.class)};
        return actions;
    }
    
}

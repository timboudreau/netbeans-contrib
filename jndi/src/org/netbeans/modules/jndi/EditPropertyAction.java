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

package org.netbeans.modules.jndi;

import org.openide.util.actions.NodeAction;
import org.openide.util.HelpCtx;
import org.openide.nodes.Node;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import org.netbeans.modules.jndi.utils.AttributeManager;
/**
 *
 * @author  tzezula
 * @version
 */
public class EditPropertyAction extends NodeAction {

    /** Creates new CreatePropertyAction */
    public EditPropertyAction() {
        super();
    }


    public void performAction (Node[] nodes){
        if (enable(nodes)){
            ((AttributeManager)nodes[0].getCookie(JndiNode.class)).editAttribute();
        }
    }


    public boolean enable (Node[] nodes){
        if  (nodes == null || nodes.length!=1)
            return false;
        JndiNode node = (JndiNode)nodes[0].getCookie(JndiNode.class);
        if (node == null) return false;
        Context ctx = node.getContext();
        if (ctx == null || !(ctx instanceof DirContext))
            return false;
        return true;
    }


    public String getName(){
        return JndiRootNode.getLocalizedString("CTL_EditProperty");
    }


    public HelpCtx getHelpCtx(){
        return HelpCtx.DEFAULT_HELP;
    }

}
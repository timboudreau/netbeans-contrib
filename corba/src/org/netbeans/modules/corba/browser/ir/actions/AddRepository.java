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

package org.netbeans.modules.corba.browser.ir.actions;

import java.util.Vector;
import org.openide.nodes.*;
import org.openide.util.actions.*;
import org.openide.util.*;
import org.openide.*;
import org.netbeans.modules.corba.browser.ir.IRRootNode;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.gui.AddRepositoryPanel;


import org.netbeans.modules.corba.*;

/*
 * @author Karel Gardas
 */

public class AddRepository extends NodeAction {

    public static final boolean DEBUG = false;
    //public static final boolean DEBUG = true;

    public AddRepository () {
        super ();
    }

    protected boolean enable (org.openide.nodes.Node[] nodes) {                        
        if (nodes != null)
            for (int i = 0; i < nodes.length; i ++)
                if (nodes[i].getCookie (IRRootNode.class) == null)
                    return false;
        return true;
    }

    public String getName() {
        return Util.getLocalizedString ("CTL_AddRepository");
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP; // [PENDING]
    }

    protected void performAction (final Node[] activatedNodes) {
        if (DEBUG)
            System.out.println ("AddRepository.java");
        Vector names = new Vector ();
        Node tmp_node = activatedNodes[0];
        IRRootNode node = (IRRootNode)tmp_node.getCookie (IRRootNode.class);
        AddRepositoryPanel p = new AddRepositoryPanel ();
        DialogDescriptor dd = new DialogDescriptor
            (p, Util.getLocalizedString ("TITLE_CORBAPanel"), true, DialogDescriptor.OK_CANCEL_OPTION, DialogDescriptor.OK_OPTION,
             DialogDescriptor.BOTTOM_ALIGN, null, null);
        TopManager.getDefault ().createDialog (dd).show ();
        if (dd.getValue () == DialogDescriptor.OK_OPTION) {
            if (DEBUG) {
                System.out.println (":OK");
                System.out.println (p.getName ());
                //System.out.println (p.getKind ());
                System.out.println (p.getUrl ());
                System.out.println (p.getIOR ());
            }
            if (enable (activatedNodes)) {
                try {
                    ((IRRootNode) activatedNodes[0].getCookie(IRRootNode.class)).addRepository
                        (p.getName (), p.getUrl (), p.getIOR ());
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace ();
                    TopManager.getDefault ().notify (new NotifyDescriptor.Message (e.toString(),NotifyDescriptor.Message.ERROR_MESSAGE));
                }
            }
        }

    }


}

/*
 * $Log
 * $
 */




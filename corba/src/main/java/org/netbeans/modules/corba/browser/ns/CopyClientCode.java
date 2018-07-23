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

package org.netbeans.modules.corba.browser.ns;

import java.util.Vector;
import java.awt.datatransfer.StringSelection;

import org.openide.nodes.*;
import org.openide.util.actions.*;
import org.openide.util.*;
import org.openide.*;


import org.netbeans.modules.corba.*;

/*
 * @author Karel Gardas
 */

public class CopyClientCode extends NodeAction {

    public static final boolean DEBUG = false;
    //public static final boolean DEBUG = true;

    static final long serialVersionUID =981986841072137161L;
    public CopyClientCode () {
        super ();
    }

    protected boolean enable (org.openide.nodes.Node[] nodes) {
        if (nodes == null || nodes.length != 1)
            return false;
        return (nodes[0].getCookie (ObjectNode.class) != null);
    }

    public String getName() {
        return NbBundle.getBundle (ContextNode.class).getString ("CTL_CopyClientCode");
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP; // [PENDING]
    }

    protected void performAction (final Node[] activatedNodes) {
        if (DEBUG)
            System.out.println ("CopyClientCode.java");
        TopManager.getDefault().setStatusText(NbBundle.getBundle(CopyClientCode.class).getString("TXT_GeneratingCode"));
        RequestProcessor.postRequest ( new Runnable () {
            public void run () {
                Vector names = new Vector ();
                Node tmp_node = activatedNodes[0];
                ObjectNode on = (ObjectNode)tmp_node.getCookie (ObjectNode.class);
                names.add (on.getName ());
                names.add (on.getKind ());
                tmp_node = on.getParentNode ();
                while (tmp_node.getParentNode () != null) {
                    ContextNode cn = (ContextNode)tmp_node.getCookie (ContextNode.class);
                    tmp_node = tmp_node.getParentNode ();
                    names.add (cn.getName ());
                    names.add (cn.getKind ());
                }
                String paste = new String ("      String[] client_name_hierarchy = new String [] {");
                for (int i=names.size () - 6; i>=0; i=i-2) {
                    paste = paste + "\"" + GenerateSupport.correctCode((String)names.elementAt (i)) + "\"" + ", ";
                    paste = paste + "\"" + GenerateSupport.correctCode((String)names.elementAt (i+1)) + "\"" + ", ";
                }
                if (paste.substring (paste.length () - 2, paste.length ()).equals (", "))
                    paste = paste.substring (0, paste.length () - 2);
                paste = paste + "};\n";
                if (DEBUG)
                    System.out.println ("names: " + paste);
                StringSelection ss = new StringSelection (paste);
                TopManager.getDefault().getClipboard().setContents(ss, null);
                TopManager.getDefault().setStatusText (NbBundle.getBundle(CopyClientCode.class).getString("TXT_CodeGenerated"));
            }
        });

    }

}


/*
 * $Log
 * $
 */

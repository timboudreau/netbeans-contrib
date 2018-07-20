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
 * RefreshAction.java
 *
 * Created on November 13, 2000, 12:21 PM
 */

package org.netbeans.modules.corba.ioranalyzer;

import org.openide.*;
import org.openide.nodes.*;
import org.openide.util.*;
import org.openide.util.actions.*;
/**
 *
 * @author  tzezula
 * @version
 */
public class RefreshAction extends CookieAction {

    /** Creates new RefreshAction */
    public RefreshAction() {
    }


    public Class[] cookieClasses() {
        return new Class[] {IORNode.class};
    }

    public String getName () {
        return NbBundle.getBundle(RefreshAction.class).getString("TXT_Refresh");
    }
    
    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public boolean enable (Node[] nodes) {
        if (nodes.length == 0)
            return false;
        for (int i=0; i< nodes.length; i++) {
            IORNode node = (IORNode) nodes[i].getCookie (IORNode.class);
            if (node == null)
                return false;
        }
        
        return true;
    }
    
    protected int mode () {
        return org.openide.util.actions.CookieAction.MODE_ALL;
    }
    
    public void performAction (Node[] nodes) {
        for (int i=0; i< nodes.length; i++) {
            IORNode node = (IORNode) nodes[i].getCookie (IORNode.class);
            if (node != null)
                ((ProfileChildren)node.getChildren()).update();
        }
    }

}

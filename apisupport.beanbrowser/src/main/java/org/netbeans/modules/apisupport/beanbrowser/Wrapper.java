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

package org.netbeans.modules.apisupport.beanbrowser;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.FilterNode;
import org.openide.nodes.FilterNode.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;

/** The basic class--a wrapper for a node. */
public class Wrapper extends FilterNode {

    private Wrapper(Node orig) {
        super(orig, new WrapperKids(orig));
    }

    /** Create a wrapper node from an original.
     * Specially prevents recursion (creating a wrapper of a wrapper).
     */
    public static Node make(Node orig) {
        if (orig instanceof Wrapper) {
            // FQN to avoid interpretation as FilterNode.Children:
            org.openide.nodes.Children kids = new Children.Array();
            kids.add(new Node[] { orig.cloneNode() });
            AbstractNode toret = new AbstractNode(kids) {
                public HelpCtx getHelpCtx() {
                    return new HelpCtx("org.netbeans.modules.apisupport.beanbrowser");
                }
            };
            toret.setName("Already a wrapper node...");
            toret.setIconBaseWithExtension("org/netbeans/modules/apisupport/beanbrowser/BeanBrowserIcon.gif");
            return toret;
        } else {
            return new Wrapper(orig);
        }
    }
    
    public Node cloneNode() {
        return new Wrapper(getOriginal());
    }
    
    /*
    // Override to include special node-exploration action.
    public SystemAction[] getActions () {
        SystemAction[] orig = super.getActions ();
        if (orig == null) orig = new SystemAction[0];
        boolean includeSep = orig.length > 0 && orig[0] != null;
        SystemAction[] nue = new SystemAction[orig.length + (includeSep ? 2 : 1)];
        nue[0] = SystemAction.get (NodeExploreAction.class);
        if (includeSep) nue[1] = null;
        for (int i = 0; i < orig.length; i++)
            nue[i + (includeSep ? 2 : 1)] = orig[i];
        return nue;
    }
     
    // For access by NodeExploreAction:
    public Node getOriginal () {
        return super.getOriginal ();
    }
     */
    
}

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

package org.netbeans.modules.vcscore.ui.fsmanager;

import org.netbeans.modules.vcscore.registry.*;

import java.awt.Dialog;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ResourceBundle;

import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.CookieSet;
import org.openide.util.datatransfer.NewType;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.actions.NewAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.ToolsAction;
import org.openide.NotifyDescriptor;
import org.openide.DialogDescriptor;


/** 
 * Root node of Vcs FileSystems
 * @author Richard Gregor
 */
public class VcsNode extends AbstractNode {
    
    /** Array of the actions of the java methods, constructors and fields. */
    private static final SystemAction[] DEFAULT_ACTIONS = new SystemAction[0];
    
    public static final String ICON_BASE =
    "org/netbeans/modules/vcscore/actions/VcsManagerActionIcon"; // NOI18N       
    
    public VcsNode() {        
        super(new VcsChildren());                     
        setName( NbBundle.getBundle(VcsNode.class).getString("CTL_VcsNodeName"));
        setIconBase(ICON_BASE);
        setActions(DEFAULT_ACTIONS);
    }
    
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx(VcsNode.class);
    }
    
    /** Set all actions for this node.
     * @param actions new list of actions
     */
    public void setActions(SystemAction[] actions) {
        systemActions = actions;
    }
    
    /** Serialization */
    public Node.Handle getHandle() {
        return new VcsHandle();
    }
    
    /** Handle for this node, it is serialized instead of node */
    static final class VcsHandle implements Node.Handle {
        static final long serialVersionUID =-3856331604791683300L;
        public Node getNode() {
            return new VcsNode();
        }
    }
    
}


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

import java.util.HashMap;
import java.util.Iterator;
import java.io.IOException;
import javax.naming.Context;
import org.openide.util.actions.SystemAction;
import org.openide.actions.NewAction;
import org.openide.util.datatransfer.NewType;
import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node.Cookie;
import org.openide.nodes.Children;
import org.openide.nodes.DefaultHandle;
import org.openide.nodes.Sheet;
import org.openide.util.HelpCtx;
import org.netbeans.modules.jndi.utils.Refreshable;
import org.netbeans.modules.jndi.settings.JndiSystemOption;

/** This class represents the branch with providers (factories)
 *
 *  @author Tomas Zezula
 */
public class JndiProvidersNode extends AbstractNode implements Cookie,Refreshable {

    /** Name for JndiIcons*/
    public static final String DRIVERS = "TITLE_DRIVERS";

    /** System actions*/
    SystemAction[] jndiactions = null;

    /** Creates new JndiProviderNode, installs providers if they are not installed
     *  and reads them to hashtable
     */
    public JndiProvidersNode() {
        super ( new Children.Array ());
        this.getCookieSet().add(this);
        setName (JndiRootNode.getLocalizedString(JndiProvidersNode.DRIVERS));
        setIconBase (JndiIcons.ICON_BASE + JndiIcons.getIconName(JndiProvidersNode.DRIVERS));
        this.installProperties(false);
    }

    /** Returns name of object
     *  @return Object name of node
     */
    public Object getValue(){
        return this.getName();
    }


    /** Sets the name of node
     *  @param Object name of node
     */
    public void setValue (Object name) {
        if (name instanceof String) {
            this.setName ((String) name);
        }
    }

    /** Returns how the node feels about destroying
     *  @return boolean can / can not destroy
     */
    public boolean canDestroy () {
        return false;
    }

    /** Returns true if the node can be copy
     *  @return boolean can / can not copy
     */
    public boolean canCopy () {
        return false;
    }

    /** Returns true if the node can be cut
     *  @return boolean can / can not cut
     */
    public boolean canCut () {
        return false;
    }

    /** Returns true if the node can be removed
     *  @return boolean can / can notr rename
     */
    public boolean canRename () {
        return false;
    }

    /** Returns default system action of this node
     *  @return SystemAction 
     */
    public SystemAction getDefaultAction () {
        return null;
    }

    /** Returns system actions of this node
     *  @return SystemAction[] actions 
     */
    public SystemAction[] getActions () {
        if (this.jndiactions == null) {
            this.jndiactions = this.createActions ();
        }
        return this.jndiactions;
    }

    /** Initialization of the SystemActions
     *  @return SystemAction[] actions
     */
    public SystemAction[] createActions () {
        return new SystemAction[] {
                   SystemAction.get(NewAction.class),
                   null,
                   SystemAction.get(RefreshAction.class)
               };
    }

    /** Returns New Type of this node
     *  @return NewType[] types
     */
    public NewType[] getNewTypes () {
        return new NewType[] {new ProviderDataType(this)};
    }

    /** Returns Handle of this Node
     *  @return Handle handle
     */
    public Handle gethandle () {
        return DefaultHandle.createHandle(this);
    }

    /** Creates ProviderNode as a child of this node */
    private void installProperties (boolean reload) {
        JndiSystemOption settings = (JndiSystemOption) JndiSystemOption.findObject (JndiSystemOption.class, true);
        if (settings != null) {
            HashMap providers = (HashMap) settings.getProviders(reload).clone();
            int size = providers.size ();
            if (size > 0) {
                Iterator it = providers.keySet().iterator();
                Node nodes[] = new Node[size];
                for (int i=0; it.hasNext(); i++) {
                    String key = (String) it.next();
                    nodes[i] = new ProviderNode(key);
                }
                this.getChildren().add(nodes);
            }
        }
    }

    /** Refresh the providers tree
     */
    public void refresh() {
        this.getChildren().remove ( this.getChildren().getNodes());
        this.installProperties (true);
    }
    
    
    /** Returns help context for providers node,
     *  the parent node for provider nodes
     */
    public HelpCtx getHelpCtx () {
        return new HelpCtx (JndiProvidersNode.class.getName());
    }
}

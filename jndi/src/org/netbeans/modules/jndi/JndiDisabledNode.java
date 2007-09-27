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

import java.util.Hashtable;
import java.util.Iterator;
import javax.naming.NamingException;
import javax.naming.Context;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;
import org.openide.util.actions.SystemAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.PropertiesAction;
import org.openide.util.HelpCtx;
import org.netbeans.modules.jndi.utils.Refreshable;
import org.netbeans.modules.jndi.utils.JndiPropertyMutator;

/** This class represents a mounted Context which is from some
 *  reason, e.g. the naming service is not running, not in progress.
 */
public class JndiDisabledNode extends JndiAbstractNode implements Refreshable, Node.Cookie, JndiPropertyMutator {

    /** Icon name*/
    public static final String DISABLED_CONTEXT_ICON = "DISABLED_CONTEXT_ICON";

    /** Initial properties for externalization*/
    private Hashtable properties;
    
    /** Unique index of node */
    private int index;

    /** Creates new JndiDisabledNode
     *  @param Hashtable the properties that represents the root of naming system
     */
    public JndiDisabledNode(Hashtable properties, int index) {
        super (Children.LEAF);
        this.index = index;
        this.getCookieSet().add(this);
        this.setName((String)properties.get(JndiRootNode.NB_LABEL));
        this.setIconBase(JndiIcons.ICON_BASE + JndiIcons.getIconName(DISABLED_CONTEXT_ICON));
        this.properties = properties;
    }

    /** Returns the properties of InitialDirContext
     *  @return Hashtable properties;
     */
    public Hashtable getInitialDirContextProperties() throws NamingException {
        return this.properties;
    }


    /** Can the node be destroyed
     *  @return boolean, true if the node can be destroyed
     */
    public boolean canDestroy() {
        return true;
    }

    /** Creates SystemActions of this node
     *  @return SystemAction[] the actions
     */
    public SystemAction[] createActions() {
        return new SystemAction[] {
                   SystemAction.get(RefreshAction.class),
                   null,
                   SystemAction.get(DeleteAction.class),
                   null,
                   SystemAction.get(PropertiesAction.class)
               };
    }

    /** Refreshs the node
     *  If the node is failed, and the preconditions required by the context
     *  of this node are satisfied, than change the node to JndiNode
     */
    public void refresh() {
        try {
            JndiRootNode root = JndiRootNode.getDefault();
            this.destroy();
            root.addContext(this.properties, this.index);
        }catch (java.io.IOException ioException) {
            // Should never happen
            JndiRootNode.notifyForeignException (ioException);
        }
    }
    
    
    public void destroy () throws java.io.IOException {
        ((JndiRootNodeChildren)this.getParentNode().getChildren()).remove (this.index);
        super.destroy();
    }
    
    public Sheet createSheet () {
        Sheet sheet = Sheet.createDefault ();
        Sheet.Set properties = sheet.get (Sheet.PROPERTIES);
        properties.put ( new PropertySupport.ReadOnly ("NAME",String.class,JndiRootNode.getLocalizedString("TXT_Name"),JndiRootNode.getLocalizedString("TIP_Name")) {
            public Object getValue () {
                return JndiDisabledNode.this.getName();
            }
        });
        Iterator kIt = this.properties.keySet().iterator();
        Iterator vIt = this.properties.values().iterator();
        while (kIt.hasNext()) {
            String key = (String) kIt.next();
            String value = vIt.next().toString();
            properties.put ( new JndiProperty(key, String.class, key, key, value, this, true));
        }
        return sheet;
    }
    
    public boolean changeJndiPropertyValue(String name, Object value) {
        this.properties.put (name, value);
        this.refresh();
        return true;
    }
    
    /** Returns the help context for the root Context
     *  which could not be restored after the start of
     *  the IDE, e.g. because of the service is not started.
     */
    public HelpCtx getHelpCtx () {
        return new HelpCtx (JndiDisabledNode.class.getName());
    }
    
}

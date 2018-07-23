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

package org.netbeans.modules.rmi.registry;

import java.beans.*;
import java.io.*;
import java.text.MessageFormat;
import org.netbeans.modules.rmi.registry.settings.RMIRegistrySettings;

import org.openide.nodes.*;
import org.openide.util.*;
import org.openide.util.actions.*;

/** Node for RegistryItem.
 *
 * @author  mryzl
 */

public class RegistryItemNode extends AbstractNode implements Node.Cookie, RefreshCookie {

    /** Message format for valid node. */
    static final MessageFormat FMT_VALID = new MessageFormat(getBundle("FMT_ValidItem")); // NOI18N

    /** Message format for valid node. */
    static final MessageFormat FMT_INVALID = new MessageFormat(getBundle("FMT_InvalidItem")); // NOI18N

    /** Icon for valid node. */
    static final String SERVER_ICON_BASE = "org/netbeans/modules/rmi/registry/resources/rmiServer"; // NOI18N

    /** Icon for invalid node. */
    static final String SERVEROFF_ICON_BASE = "org/netbeans/modules/rmi/registry/resources/rmiServerOff"; // NOI18N

    /** Creates new RegistryItemNode.
    * @param item a RegistryItem
    * @param children children of the node
    */
    public RegistryItemNode(RegistryItem item, Children children) {
        super(children);
        systemActions = new SystemAction[] {
                            SystemAction.get(org.netbeans.modules.rmi.registry.RMIRegistryRefreshAction.class),
                            null,
                            SystemAction.get(org.openide.actions.DeleteAction.class),
                            null,
                            SystemAction.get(org.openide.actions.ToolsAction.class),
                            SystemAction.get(org.openide.actions.PropertiesAction.class),
                        };
        CookieSet cookies = getCookieSet();
        cookies.add(this);
        cookies.add(item);
    }

    /** Create property sheet.
    */
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        try {
            Sheet.Set prop = sheet.get(Sheet.PROPERTIES);

            if (prop == null) {
                prop = sheet.createPropertiesSet();
                sheet.put(prop);
            }

            Object item = getCookie(RegistryItem.class);

            PropertySupport.Reflection p = new PropertySupport.Reflection(item, String.class, "getHostName", null); // NOI18N
            p.setName("PROP_HostName"); // NOI18N
            p.setDisplayName(getBundle("PROP_HostName")); // NOI18N
            p.setShortDescription(getBundle("HINT_HostName")); // NOI18N
            prop.put(p);

            p = new PropertySupport.Reflection(item, Integer.TYPE, "getPort", null); // NOI18N
            p.setName("PROP_Port"); // NOI18N
            p.setDisplayName(getBundle("PROP_Port")); // NOI18N
            p.setShortDescription(getBundle("HINT_Port")); // NOI18N
            prop.put(p);

        } catch (NoSuchMethodException ex) {
            org.openide.ErrorManager.getDefault().notify(org.openide.ErrorManager.INFORMATIONAL, ex);
        }
        return sheet;
    }

    /** Destroy the node and the remove the RegistryItem.
    */
    public void destroy() throws IOException {
        super.destroy();
        RegistryItem item = (RegistryItem)getCookie(RegistryItem.class);
        RMIRegistrySettings.getInstance().removeRegistryItem((RegistryItem)getCookie(RegistryItem.class));
    }

    /**
    * @return true if the node can be destroyed.
    */
    public boolean canDestroy() {
        return true;
    }

    /** Causes refresh of the node/item.
    */
    public void refresh() {
        RMIRegistryChildren.updateItem((RegistryItem)getCookie(RegistryItem.class));
    }
    
    public HelpCtx getHelpCtx () {
        return new HelpCtx (RegistryItemNode.class.getName());
    }

    // -- Inner classes. --

    /** Class representing a valid node. */
    public static class ValidNode extends RegistryItemNode {
        public ValidNode(RegistryItem item, Children children) {
            super(item, children);
            setName(FMT_VALID.format(item.getItemObjects()));
            setIconBase(SERVER_ICON_BASE);
        }
    }

    /** Class representing an invalid node. */
    public static class InvalidNode extends RegistryItemNode implements PropertyChangeListener {
        RegistryItem item;

        public InvalidNode(RegistryItem item) {
            super(item, Children.LEAF);
            this.item = item;

            // weak listener, it is not necessary to unregister
            PropertyChangeListener pcl = WeakListeners.propertyChange(this, item);
            item.addPropertyChangeListener(pcl);
            //      item.addPropertyChangeListener(new WeakListener.PropertyChange(this));

            setName(FMT_INVALID.format(item.getItemObjects()));
            setIconBase(SERVEROFF_ICON_BASE);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getNewValue() != null) {
                // initiate refresh of the key
                item.removePropertyChangeListener(this);
                RMIRegistryNode parent = (RMIRegistryNode) getParentNode().getCookie(RMIRegistryNode.class);
                parent.refreshItem(item);
            }
        }

        protected void finalize() throws Throwable {
            item.removePropertyChangeListener(this);
            super.finalize();
        }
    }

    private static String getBundle( String key ) {
        return NbBundle.getMessage( RegistryItemNode.class, key );
    }
}

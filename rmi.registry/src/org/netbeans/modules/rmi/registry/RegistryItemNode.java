/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
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

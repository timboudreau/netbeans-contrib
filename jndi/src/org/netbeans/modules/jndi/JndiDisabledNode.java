/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
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
                   SystemAction.get(DeleteAction.class)
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
    
}
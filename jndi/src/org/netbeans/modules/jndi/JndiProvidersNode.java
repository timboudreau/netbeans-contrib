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
        return HelpCtx.DEFAULT_HELP;
    }
}
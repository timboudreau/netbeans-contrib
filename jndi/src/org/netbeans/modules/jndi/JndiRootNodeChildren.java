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
import java.util.ArrayList;
import java.util.Iterator;
import javax.naming.NamingException;
import javax.naming.Context;
import org.openide.nodes.*;
import org.netbeans.modules.jndi.settings.JndiSystemOption;
/**
 *
 * @author  Tomas Zezula
 */
public class JndiRootNodeChildren extends Children.Keys {
    
    private ArrayList contexts;
    private JndiSystemOption settings;
    private JndiProvidersNode providersNode;

    /** Creates new JndiRootNodeChildren */
    public JndiRootNodeChildren() {
    }
    
    
    /** Called by IDE when the children are needed
     */
    public void addNotify () {
        this.update ();
    }
    
    /** Called by IDE when the children are disposing
     */
    public void removeNotify () {
        this.setKeys (new Object[0]);
    }
    
    /** Adds the node to children list
     *  @param Hashtable environment of the JNDIContext
     */
    public void add (Hashtable contextProperties) {
        if (this.settings == null)
            this.init ();
        this.contexts.add (contextProperties);
        this.settings.setInitialContexts (this.contexts);
        this.update();
    }
    
    /** Adds the node to the specified position
     *  @param Hashtable environment of the JndiContext
     *  @param int index
     */
    public void add (Hashtable contextProperties, int index) {
        if (this.settings == null)
            this.init ();
        this.contexts.add (index, contextProperties);
        this.settings.setInitialContexts (this.contexts);
        this.update();
    }
    
    /** Removes node from children list
     *  @param int index
     */
    public void remove (int index) {
        if (index <0 || index >= this.contexts.size())
            return;
        if (this.settings == null)
            this.init ();
        this.contexts.remove (index);
        this.settings.setInitialContexts (this.contexts);
        this.update();
    }
    
    /** Rebuilds the nodes according to contexts
     */
    public void update () {
        if (this.settings == null)
            this.init ();
        ArrayList keys = new ArrayList ();
        keys.add ( new ProvidersKey());
        Iterator it = this.contexts.iterator();
        for (int index=0; it.hasNext(); index++) {
            Hashtable env = (Hashtable) it.next();
            JndiRootCtxKey key = new JndiRootCtxKey (env,index);
            keys.add (key);
        }
        this.setKeys (keys);
    }
    
    public void updateKey (Object key) {
        this.refreshKey (key);
    }
    
    
    public JndiProvidersNode getProvidersNode () {
        if (this.providersNode == null)
            this.providersNode = new JndiProvidersNode ();
        return this.providersNode;
    }

    /** Create nodes for a given key.
     * @param key the key
     * @return child nodes for this key or null if there should be no
     *   nodes for this key
     */
    protected Node[] createNodes(Object key) {
        if (key instanceof ProvidersKey) {
            return new Node[] {this.getProvidersNode()};
        }
        else if (key instanceof JndiRootCtxKey) {
            JndiRootCtxKey jndiRootCtxKey = (JndiRootCtxKey) key;
            try {
                Context ctx = new JndiDirContext (jndiRootCtxKey.getEnvironment());
                String root = (String)jndiRootCtxKey.getEnvironment().get(JndiRootNode.NB_ROOT);
                if (root != null) {
                    ctx = (Context) ctx.lookup(root);
                }
                else {
                    ((JndiDirContext)ctx).checkContext();
                }
                JndiNode jndiNode = new JndiNode (ctx, jndiRootCtxKey.getIndex());
                return new Node[] {jndiNode};
            }catch (NamingException e) {
                return new Node[] {new JndiDisabledNode (jndiRootCtxKey.getEnvironment(), jndiRootCtxKey.getIndex())};
            }
        }
        else {
            return new Node[0];
        }
    }
    
    
    /** Initializes binding to settings
     */
    private void init () {
        this.settings = (JndiSystemOption) JndiSystemOption.findObject (JndiSystemOption.class, true);
        this.contexts = this.settings.getInitialContexts();
    }
    
}

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

import java.util.Iterator;
import java.util.Hashtable;
/**
 *
 * @author  Tomas Zezula
 */
public class JndiRootCtxKey extends Object {
    
    private Hashtable env;
    private int index;

    /** Creates new JndiRootCtxKey */
    public JndiRootCtxKey(Hashtable env, int index) {
        this.env = env;
        this.index = index;
    }
    
    
    public boolean equals (Object other) {
        if (other == null)
            return false;
        if (!(other instanceof JndiRootCtxKey))
            return false;
        if (this.env == null && ((JndiRootCtxKey)other).getName() == null)
            return true;
        if (this.env == null && ((JndiRootCtxKey)other).getName() != null)
            return false;
        return (this.getName().equals (((JndiRootCtxKey)other).getName()));
    }
    
    public int hashCode () {
        return this.env != null ? this.getName().hashCode() : 0;
    }
    
    public String getName () {
        return (String) (this.env == null ? null : this.env.get (JndiRootNode.NB_LABEL));
    }
    
    public Hashtable getEnvironment () {
        return this.env;
    }
    
    public int getIndex () {
        return this.index;
    }
    
    public String toString () {
        if (env != null) {
            Iterator kIt = env.keySet().iterator();
            Iterator vIt = env.values().iterator();
            StringBuffer result = new StringBuffer ();
            while (kIt.hasNext()) {
                result.append (kIt.next()+"="+vIt.next()+"\n"); // No I18N
            }
            return result.toString();
        }
        else {
            return null;
        }
    }

}

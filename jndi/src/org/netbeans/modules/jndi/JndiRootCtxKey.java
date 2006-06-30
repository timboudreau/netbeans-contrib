/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
        if (!(other instanceof JndiRootCtxKey))
            return false;
        if (this.env == null && ((JndiRootCtxKey)other).getName() == null)
            return true;
        if (this.env == null && ((JndiRootCtxKey)other).getName() != null)
            return false;
        return (this.getName().equals (((JndiRootCtxKey)other).getName()) && ((JndiRootCtxKey)other).getIndex() == this.index);
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

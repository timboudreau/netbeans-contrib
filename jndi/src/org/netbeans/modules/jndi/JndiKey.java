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

import javax.naming.Binding;

/**
 * This class represents the key for Children.Keys used by this module
 * @author  tzezula
 * @version 1.0
 * @see JndiChildren
 */
public final class JndiKey extends Object {

    /* Failed this node while listing*/
    public boolean failed;
    /* The name class pair*/
    public Binding name;


    /** Constructor used for Keys representing remote objects
     *  @param NameClassPair name, name and class of remote object
     */
    public JndiKey (Binding name) {
        this.name = name;
        this.failed = false;
    }

    /** Constructor used for Keys representing remote objects
     *  @param NameClassPair name, name and class of remote object
     *  @param boolean failed, if the node is failed
     */
    public JndiKey (Binding name, boolean failed){
        this.name = name;
        this.failed = failed;
    }

    /** Comparator
     *  @param Object obj, object to compare with
     *  @return boolean, true if equals
     */
    public boolean equals(Object obj){
        if (! (obj instanceof JndiKey)){
            return false;
        }
        JndiKey key = (JndiKey) obj;
        if (!this.name.getName().equals(key.name.getName()))
            return false;
        else if (this.name.getClassName() == null) {
            return key.name.getClassName() == null;
        }
        else return this.name.getClassName().equals(key.name.getClassName());
    }

    /** Hash code of object
     *  @return int hash code of object
     */
    public int hashCode(){
        return this.name.getName().hashCode();
    }

    /** Returns the name of key
     *  return String name
     */
    public String toString () {
        return name.toString();
    }

}

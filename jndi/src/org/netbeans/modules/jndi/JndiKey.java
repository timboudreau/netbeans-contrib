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

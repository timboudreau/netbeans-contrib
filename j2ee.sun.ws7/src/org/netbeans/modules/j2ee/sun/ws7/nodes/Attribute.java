/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.sun.ws7.nodes;

public class Attribute {
    private Object value;
    private String name;


    public Attribute(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    
    /**
     * Gets the value of value
     *
     * @return the value of value
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * Sets the value of value
     *
     * @param argValue Value to assign to this.value
     */
    public void setValue(Object argValue){
        this.value = argValue;
    }

    /**
     * Get the Name value.
     * @return the Name value.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the Name value.
     * @param newName The new Name value.
     */
    public void setName(String newName) {
        this.name = newName;
    }
}

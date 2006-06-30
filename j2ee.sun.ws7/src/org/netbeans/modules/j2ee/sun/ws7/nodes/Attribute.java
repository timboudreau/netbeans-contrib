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

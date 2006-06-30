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

public class AttributeInfo {
    private String name;
    private String type;
    private String description;
    private boolean readable;
    private boolean writable;
    private boolean bool;

    public AttributeInfo(String name, String type, String description,
                         boolean readable, boolean writable,
                         boolean bool) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.readable = readable;
        this.writable = writable;
        this.bool = bool;
    }
    
    /**
     * Gets the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the value of name
     *
     * @param argName Value to assign to this.name
     */
    public void setName(String argName){
        this.name = argName;
    }

    /**
     * Gets the value of type
     *
     * @return the value of type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Sets the value of type
     *
     * @param argType Value to assign to this.type
     */
    public void setType(String argType){
        this.type = argType;
    }

    /**
     * Gets the value of description
     *
     * @return the value of description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the value of description
     *
     * @param argDescription Value to assign to this.description
     */
    public void setDescription(String argDescription){
        this.description = argDescription;
    }

    /**
     * Gets the value of readable
     *
     * @return the value of readable
     */
    public boolean isReadable() {
        return this.readable;
    }

    /**
     * Sets the value of readable
     *
     * @param argReadable Value to assign to this.readable
     */
    public void setReadable(boolean argReadable){
        this.readable = argReadable;
    }

    /**
     * Gets the value of writable
     *
     * @return the value of writable
     */
    public boolean isWritable() {
        return this.writable;
    }

    /**
     * Sets the value of writable
     *
     * @param argWritable Value to assign to this.writable
     */
    public void setWritable(boolean argWritable){
        this.writable = argWritable;
    }

    /**
     * Gets the value of bool
     *
     * @return the value of bool
     */
    public boolean isBool() {
        return this.bool;
    }

    /**
     * Sets the value of bool
     *
     * @param argBool Value to assign to this.bool
     */
    public void setBool(boolean argBool){
        this.bool = argBool;
    }
}

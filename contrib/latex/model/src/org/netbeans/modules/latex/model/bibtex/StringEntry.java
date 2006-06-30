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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.bibtex;

/**
 *
 * @author Jan Lahoda
 */
public class StringEntry extends Entry {

    private String key;
    private String value;

    /** Creates a new instance of StringEntry */
    public StringEntry() {
    }

    /**
     * Getter for property key.
     * @return Value of property key.
     */
    public java.lang.String getKey() {
        return key;
    }

    /**
     * Setter for property key.
     * @param key New value of property key.
     */
    public void setKey(java.lang.String key) {
        this.key = key;
    }    
    
    /**
     * Getter for property value.
     * @return Value of property value.
     */
    public java.lang.String getValue() {
        return value;
    }
    
    /**
     * Setter for property value.
     * @param value New value of property value.
     */
    public void setValue(java.lang.String value) {
        this.value = value;
    }
    
    public String writeOut() {
        return "";
    }
    
    public void update(Entry entry) {
        assert getClass().equals(entry.getClass());
        
        StringEntry sEntry = (StringEntry) entry;
        
        setKey(sEntry.getKey());
        setValue(sEntry.getValue());
    }
    
}

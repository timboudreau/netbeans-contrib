/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
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

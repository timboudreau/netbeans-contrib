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
public class FreeFormEntry extends Entry {
    
    private String type;
    private String content;
    
    /** Creates a new instance of CommentEntry */
    public FreeFormEntry() {
    }
    
    /**
     * Getter for property content.
     * @return Value of property content.
     */
    public java.lang.String getContent() {
        return content;
    }
    
    /**
     * Setter for property content.
     * @param content New value of property content.
     */
    public void setContent(java.lang.String content) {
        this.content = content;
    }
    
    /**
     * Getter for property type.
     * @return Value of property type.
     */
    public java.lang.String getType() {
        return type;
    }
    
    /**
     * Setter for property type.
     * @param type New value of property type.
     */
    public void setType(java.lang.String type) {
        this.type = type;
    }
    
    public String writeOut() {
        return "";
    }
    
    public void update(Entry entry) {
        assert getClass().equals(entry.getClass());
        
        FreeFormEntry fEntry = (FreeFormEntry) entry;
        
        setType(fEntry.getType());
        setContent(fEntry.getContent());
    }
    
}

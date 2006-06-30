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

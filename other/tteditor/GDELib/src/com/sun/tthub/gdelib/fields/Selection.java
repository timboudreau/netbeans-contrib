
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved
 *
 */


package com.sun.tthub.gdelib.fields;

/**
 *
 * @author Hareesh Ravindran
 */
public class Selection {

    private boolean isDefault;
    private String displayString;
    private String parseableString;
    
    /** Creates a new instance of Selection */
    public Selection() {}    
    public Selection(String displayString, 
                String parseableString, boolean isDefault) {
        this.displayString = displayString;
        this.parseableString = parseableString; 
        this.isDefault = isDefault;
    }
    
    public boolean getIsDefault() { return isDefault; }
    public void setIsDefault(boolean isDefault) { this.isDefault = isDefault; }
    
    public String getDisplayString() { return displayString; }
    public void setDisplayString(String displayString) {
        this.displayString = displayString;
    }

    public String getParseableString() { return parseableString; }
    public void setParseableString(String parseableString) {
        this.parseableString = parseableString;
    }
    
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(!(obj instanceof Selection))
            return false;
        Selection sel = (Selection) obj;
        return (sel.getIsDefault() == isDefault) && isStringsSame(
                displayString, sel.getDisplayString()) && isStringsSame(
                parseableString, sel.getParseableString());        
    }
    
    public Object clone() throws CloneNotSupportedException {
        return new Selection(displayString, parseableString, isDefault);
    }
    
    public boolean isStringsSame(String str1, String str2) {
        if(str1 == null)
            return (str2 == null);
        return str1.equals(str2);
    }
    
    public String toString() {
        return "DisplayString: '" + displayString + "', Parseable String: '" +
                parseableString + "', IsDefault: '" + isDefault + "'";
    }

}


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


package com.sun.tthub.gdelib.logic;

import com.sun.tthub.gdelib.InvalidArgumentException;
import com.sun.tthub.gdelib.fields.FieldInfo;
import com.sun.tthub.gdelib.fields.FieldMetaData;
import com.sun.tthub.gdelib.fields.FieldDisplayInfo;

/**
 * This class holds the information to format and display a single field in the
 * extended TroubleTicketValue interface.
 *
 * @author Hareesh Ravindran
 */
public class TTValueFieldInfo extends FieldInfo {
    
    protected boolean isRequired;
    protected boolean isSearchable;
    protected boolean includeInSearchResults;
    
    /**
     * Creates a new instance of TTValueFieldInfo
     */
    public TTValueFieldInfo() {}
    
    public TTValueFieldInfo(FieldInfo info,
            boolean isRequired, boolean isSearchable,
            boolean includeInSearchResults) {
        super(info.getFieldMetaData(), info.getFieldDisplayInfo());
        this.isRequired = isRequired;
        this.isSearchable = isSearchable;
        this.includeInSearchResults = includeInSearchResults;
    }
    
    public TTValueFieldInfo(FieldMetaData fieldMetaData,
            boolean isRequired, boolean isSearchable,
            boolean includeInSearchResults,  FieldDisplayInfo fieldDisplayInfo) {
        super(fieldMetaData, fieldDisplayInfo);
        this.isRequired = isRequired;
        this.isSearchable = isSearchable;
        this.includeInSearchResults = includeInSearchResults;
    }
    
    public boolean getIsRequired() { return this.isRequired; }
    public void setIsRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }
    
    public boolean getIsSearchable() { return this.isSearchable; }
    public void setIsSearchable(boolean isSearchable) {
        this.isSearchable = isSearchable;
    }
    
    public boolean getIncludeInSearchResults() {
        return this.includeInSearchResults;
    }
    public void setIncludeInSearchResults(boolean includeInSearchResults) {
        this.includeInSearchResults = includeInSearchResults;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return new TTValueFieldInfo(this.fieldMetaData,
                this.isRequired, this.isSearchable,
                this.includeInSearchResults, this.fieldDisplayInfo);
    }
    
    public boolean equals(Object obj) {
        
        if(!(obj instanceof TTValueFieldInfo))
            return false;
        TTValueFieldInfo info = (TTValueFieldInfo) obj;
        
        if(isRequired != info.getIsRequired())
            return false;
        if(isSearchable != info.getIsSearchable())
            return false;
        if(includeInSearchResults != info.getIncludeInSearchResults())
            return false;
        
        return true;
    }
    /*
    // String representation of this class.
    public String toString() {
     
        StringBuffer strBuf = new StringBuffer();
        strBuf.append(super.toString());
        strBuf.append(", Required?: ");
        strBuf.append(this.isRequired ? "Yes" : "No");
        strBuf.append(", Searchable?: ");
        strBuf.append(this.isSearchable ? "Yes" : "No");
        strBuf.append(", Include In Search Results?: ");
        strBuf.append(this.includeInSearchResults ? "Yes" : "No");
        return strBuf.toString();
    }
     */
    public String toString(){
        StringBuffer strBuf = new StringBuffer();
        
        //<field name="field-name" type="normal|ttvalue">
        strBuf.append("<field name=");
        strBuf.append(fieldMetaData.getFieldName());
        strBuf.append(" type=");
        strBuf.append(fieldMetaData.getFieldDataTypeNature());
        strBuf.append(">\r\n");
        
        strBuf.append("<portlet-attr>\r\n");
        strBuf.append("<include-in-create-tt>");
        strBuf.append(this.isRequired);
        strBuf.append("</include-in-create-tt>\r\n");
        strBuf.append("<is-search-attribute>");
        strBuf.append(this.isSearchable);
        strBuf.append("</is-search-attribute>\r\n");
        strBuf.append("<include-in-search-result>");
        strBuf.append(this.includeInSearchResults);
        strBuf.append("</include-in-search-result>\r\n");
        strBuf.append("</portlet-attr>\r\n");
        strBuf.append(this.fieldMetaData);
        strBuf.append(this.fieldDisplayInfo);
        strBuf.append("</field>\r\n");
            
        return strBuf.toString();
    }
}


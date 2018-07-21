
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder. *
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


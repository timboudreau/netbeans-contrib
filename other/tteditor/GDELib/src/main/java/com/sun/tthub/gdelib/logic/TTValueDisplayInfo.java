
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

/**
 * This class represents the details of the display information that is to be
 * passed to the portlet page generator. This encapsulates all the details
 * required by the page generator.
 *
 * It uses a hash map to hold the details of each field in the extended
 * TroubleTicketValue interface.  It also holds the names of the extened
 * TroubleTicketValue interface and its implementation.
 *
 * @author Hareesh Ravindran
 *
 */

public class TTValueDisplayInfo implements java.io.Serializable{
    
    protected String extTTValueInterface;
    protected String extTTValueImplClass;
    protected String extTTValueSchema;
    protected Map extFieldInfoMap = new HashMap();
    protected Map baseFieldInfoMap = new HashMap();
    
    /**
     * Creates a new instance of TTValueDisplayInfo
     */
    public TTValueDisplayInfo() {}
    
    public TTValueDisplayInfo(String extTTValueInterface,
            String extTTValueImplClass) {
        this.extTTValueInterface = extTTValueInterface;
        this.extTTValueImplClass = extTTValueImplClass;
    }
    
    public String getExtTTValueInterface() { return this.extTTValueInterface; }
    public void setExtTTValueInterface(String extTTValueInterface) {
        this.extTTValueInterface = extTTValueInterface;
    }
    
    public String getExtTTValueImplClass() { return this.extTTValueImplClass; }
    public void setExtTTValueImplClass(String extTTValueImplClass) {
        this.extTTValueImplClass = extTTValueImplClass;
    }
    public String getExtTTValueSchema() { return this.extTTValueSchema; }
    public void setExtTTValueSchema(String extTTValueSchema) {
        this.extTTValueSchema = extTTValueSchema;
    }
    /**
     * Returns the list of FieldMetaData objects for all the fields stored in
     * this object.
     */
    public Collection getFieldList() {
        Collection coll = extFieldInfoMap.entrySet();
        Collection retColl = new ArrayList(coll.size());
        for(Iterator it = coll.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            TTValueFieldInfo info =
                    (TTValueFieldInfo) entry.getValue();
            retColl.add(info.getFieldMetaData());
        }
        return retColl;
    }
    
    public void setExtFieldInfoMap(Map fieldDisplayInfoMap) {
        this.extFieldInfoMap = fieldDisplayInfoMap;
    }
    public Map getExtFieldInfoMap() { return this.extFieldInfoMap; }
    
    public void setBaseFieldInfoMap(Map baseFieldsMap) {
        this.baseFieldInfoMap = baseFieldsMap;
    }
    public Map getBaseFieldInfoMap() { return this.baseFieldInfoMap; }
    
    public TTValueFieldInfo getFieldInfo(String fieldName) {
        return (TTValueFieldInfo) this.extFieldInfoMap.get(fieldName);
    }
    /*
    public String toString() {
        return "TTValue Interface: '" + this.extTTValueInterface +
                "', TTValue Impl Class: '" + this.extTTValueImplClass +
                "', TTValue Schema: '" + this.extTTValueSchema +
                "', DisplayParams Map: [" + this.extFieldInfoMap + "], " +
                " BaseFields Map: [" + this.baseFieldInfoMap + "]";
    }
     */
    
    public String toString(){
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("<ttvalue-info interface=");
        strBuf.append(this.getExtTTValueInterface());
        strBuf.append(" impl-class=");
        strBuf.append(this.getExtTTValueImplClass());
        strBuf.append(">\r\n");
        
        strBuf.append("<fields>\r\n");
        strBuf.append(this.extFieldInfoMap);
        strBuf.append(this.baseFieldInfoMap);
        strBuf.append("</fields>\r\n");
        strBuf.append("</ttvalue-info>");
        return strBuf.toString();
        
    }
    /*
    public String toXML() {
        
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("<ttvalue-info interface=");
        strBuf.append(this.getExtTTValueInterface());
        strBuf.append(" impl-class=");
        strBuf.append(this.getExtTTValueImplClass());
        strBuf.append(">\r\n");
        
        strBuf.append("<fields>\r\n");
        
        for(Iterator it = this.extFieldInfoMap.entrySet().iterator(); it.hasNext(); ) {
            
            Map.Entry entry = (Map.Entry) it.next();
            TTValueFieldInfo fieldInfo =
                    (TTValueFieldInfo) entry.getValue();
            FieldMetaData metaData= fieldInfo.getFieldMetaData();
            
            
            //<field name="field-name" type="normal|ttvalue">
            strBuf.append("<field name=");
            strBuf.append(metaData.getFieldName());
            strBuf.append(" type=");
            strBuf.append(metaData.getFieldDataTypeNature());
            strBuf.append(">\r\n");
            
            strBuf.append("<portlet-attr>\r\n");
            strBuf.append("<include-in-create-tt>");
            strBuf.append(fieldInfo.getIsRequired());
            strBuf.append("</include-in-create-tt>\r\n");
            strBuf.append("<is-search-attribute>");
            strBuf.append(fieldInfo.getIsSearchable());
            strBuf.append("</is-search-attribute>\r\n");
            strBuf.append("<include-in-search-result>");
            strBuf.append(fieldInfo.getIncludeInSearchResults());
            strBuf.append("</include-in-search-result>\r\n");
            strBuf.append("</portlet-attr>\r\n");
            
            strBuf.append("<meta-data>\r\n");
            strBuf.append("<field-name>");
            strBuf.append(metaData.getFieldName());
            strBuf.append("</field-name>\r\n");
            strBuf.append("<data-type>");
            strBuf.append(metaData.getFieldDataType());
            strBuf.append("</data-type>\r\n");
            strBuf.append("<nesting-level>");
            strBuf.append(metaData.getNestingLevel());
            strBuf.append("</nesting-level>\r\n");
            strBuf.append("<data-type-nature>");
            strBuf.append((metaData.getFieldDataTypeNature()==DataTypeNature.NATURE_SIMPLE)?"simple":"complex");
            strBuf.append("</data-type-nature\r\n");
            strBuf.append("</meta-data>\r\n");
            
            
            FieldDisplayInfo displayInfo=fieldInfo.getFieldDisplayInfo();
            
            strBuf.append("<display-info>\r\n");
            strBuf.append("<display-name>");
            strBuf.append(displayInfo.getFieldDisplayName());
            strBuf.append("</display-name>\r\n");
            
            //<display-type>entry|single-select|multi-select</display-type>
            strBuf.append("<display-type>");
            strBuf.append(displayInfo.getFieldDataEntryNature());
            strBuf.append("</display-type>\r\n");
            strBuf.append("<ui-component>");
            //component singleton class static reference
            strBuf.append(displayInfo.getUIComponentType().toString());
            strBuf.append("</ui-component>\r\n");
            
            
            if(displayInfo instanceof SelectionFieldDisplayInfo) {
                strBuf.append("<selection-info>\r\n");
                
                SelectionFieldDisplayInfo selectFieldInfo= (SelectionFieldDisplayInfo)fieldInfo.getFieldDisplayInfo();
                Object[] selRange = selectFieldInfo.getSelectionRange();
                Object[] defList = selectFieldInfo.getDefaultSelection();
                for(int i = 0; i < selRange.length; ++i) {
                    if(selRange[i] == null)
                        continue;
                    strBuf.append("<option display-str=");
                    strBuf.append(selRange[i].toString() );
                    strBuf.append(" value=");
                    strBuf.append(selRange[i].toString());
                    strBuf.append(" default-sel=");
                    strBuf.append(isInDefValueList(selRange[i], defList) ?
                        "true" : "false");
                    strBuf.append("/>\r\n");
                    
                }
                
                strBuf.append("</selection-info>\r\n");
            }
            
            
            // else  if(displayInfo instanceof ComplexEntryFieldDisplayInfo) {
            strBuf.append("</display-info>\r\n");
            
            strBuf.append("</field>\r\n");
            
            strBuf.append("</fields>\r\n");
            strBuf.append("</ttvalue-info>");
            
            
            
        }
        return strBuf.toString();
    }
    */
    public static TTValueDisplayInfo readDisplayXML(){
        return null;
    }
    private boolean isInDefValueList(Object obj, Object[] defList) {
        for(int i = 0; i < defList.length; ++i) {
            if(obj.equals(defList[i]))
                return true;
        }
        return false;
    }
    
}
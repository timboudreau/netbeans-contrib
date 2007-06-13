
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

public class TTValueDisplayInfo {
    
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
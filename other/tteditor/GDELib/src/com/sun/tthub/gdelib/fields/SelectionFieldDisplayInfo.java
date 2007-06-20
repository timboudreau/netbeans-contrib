

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
public class SelectionFieldDisplayInfo extends FieldDisplayInfo {
    
    // this array reference will be used where ever an empty array is
    // required in this class.
    private static final Object[] EMPTY_SELECTION_ARR = new Selection[0];
    
    protected boolean isMultiSelect = false;
    protected Object[] selectionRange = EMPTY_SELECTION_ARR;
    protected boolean isRequired;
    protected Object[] defaultSelection = EMPTY_SELECTION_ARR;
    
    /**
     * Creates a new instance of SelectionFieldDisplayInfo
     */
    public SelectionFieldDisplayInfo() {}
    public SelectionFieldDisplayInfo(String fieldDisplayName,
            UIComponentType componentType, boolean isMultiSelect,
            Object[] selectionRange, boolean isRequired) {
        super(fieldDisplayName, componentType);
        this.isMultiSelect = isMultiSelect;
        this.selectionRange = (selectionRange != null) ?
            selectionRange : EMPTY_SELECTION_ARR;
        this.isRequired = isRequired;
    }
    
    public boolean getIsMultiSelect() {
        return isMultiSelect;
    }
    
    public void setIsMultiSelect(boolean isMultiSelect) {
        this.isMultiSelect = isMultiSelect;
    }
    
    public int getFieldDataEntryNature() {
        return (isMultiSelect ? FieldDataEntryNature.TYPE_MULTI_SELECT :
            FieldDataEntryNature.TYPE_SINGLE_SELECT);
    }
    
    public Object[] getSelectionRange() { return this.selectionRange; }
    public void setSelectionRange(Object[] selectionRange) {
        this.selectionRange = (selectionRange != null) ?
            selectionRange : EMPTY_SELECTION_ARR;
    }
    /* Choon Yin 14 Nov 2006 Added missing method*/
    
    
    public Object[] getDefaultSelection() { return this.defaultSelection; }
    public void setDefaultSelection(Object[] defaultSelection) {
        this.defaultSelection = (defaultSelection != null) ?
            defaultSelection : EMPTY_SELECTION_ARR;
    }
    public boolean getIsRequired() { return this.isRequired; }
    public void setIsRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }
    
    /**
     * Performs a deep copy of the selection range array. It will create a
     * new array and copy the entire contents of the existing array to the
     * new array.
     */
    public Object clone() throws CloneNotSupportedException {
        
        Object[] selRangeClone =null;
        if(selectionRange.length <= 0){
            selRangeClone= EMPTY_SELECTION_ARR;
        }else{
            selRangeClone = new Object[selectionRange.length];
            for(int i = 0; i < selectionRange.length; ++i) {
                //selRangeClone[i] = (Selection)selectionRange[i].clone();
                selRangeClone[i] =selectionRange[i];
            }
        }
        return new SelectionFieldDisplayInfo(fieldDisplayName,displayUIComponentType,
                isMultiSelect, selRangeClone, isRequired);
                
    }
    
    public boolean equals(Object obj) {
        if(!super.equals(obj))
            return false;
        if(!(obj instanceof SelectionFieldDisplayInfo))
            return false;
        SelectionFieldDisplayInfo attr = (SelectionFieldDisplayInfo) obj;
        boolean isSelectionRangeSame =
                selectionRange.equals(attr.getSelectionRange());
        return (isSelectionRangeSame && isRequired);
    }
    
    // String representation of this class.
    public String toString() {
        return getDisplayInfoStr();
    }
/*
    public String getDisplayInfoStr() {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("Field Display Name: '");
        strBuf.append(this.fieldDisplayName);
        strBuf.append(", UI Control: '");
        strBuf.append(this.displayUIComponentType);
        strBuf.append("', Sel. Type: '");
        strBuf.append(this.isMultiSelect ? "Multi Select" : "Single Select");
        strBuf.append("', Is Req: '");
        strBuf.append(this.isRequired ? "Yes" : "No");
        strBuf.append("', Sel. Range: '");
        strBuf.append(this.selectionRange);
        strBuf.append("'");
        return strBuf.toString();
    }
 */
    public String getDisplayInfoStr(){
        
        StringBuffer strBuf=new StringBuffer();
        strBuf.append("<display-info>\r\n");
        strBuf.append("<display-name>");
        strBuf.append(this.fieldDisplayName);
        strBuf.append("</display-name>\r\n");
        
        //<display-type>entry|single-select|multi-select</display-type>
        strBuf.append("<display-type>");
        strBuf.append(this.isMultiSelect?"multi-select":"single-select");
        strBuf.append("</display-type>\r\n");
        strBuf.append("<ui-component>");
        //component singleton class static reference
        strBuf.append(this.displayUIComponentType.toString());
        strBuf.append("</ui-component>\r\n");
        
        strBuf.append("<selection-info>\r\n");
        
        for(int i = 0; i < this.selectionRange.length; ++i) {
            if(selectionRange[i] == null)
                continue;
            strBuf.append("<option display-str=");
            strBuf.append(selectionRange[i].toString() );
            strBuf.append(" value=");
            strBuf.append(selectionRange[i].toString());
            strBuf.append(" default-sel=");
            strBuf.append(isInDefValueList(selectionRange[i], this.defaultSelection) ?
                "true" : "false");
            strBuf.append("/>\r\n");
        }
        
        strBuf.append("</selection-info>\r\n");
        
        strBuf.append("</display-info>\r\n");
        return strBuf.toString();
    }
    
    private boolean isInDefValueList(Object obj, Object[] defList) {
        for(int i = 0; i < defList.length; ++i) {
            if(obj.equals(defList[i]))
                return true;
        }
        return false;
    }
    
    
}

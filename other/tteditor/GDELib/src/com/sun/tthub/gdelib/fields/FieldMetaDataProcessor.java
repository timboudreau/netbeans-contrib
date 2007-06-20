
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

import com.sun.tthub.gdelib.GDEException;
import com.sun.tthub.gdelib.GDERuntimeException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class contains utility functions that can operate on a FieldMetaData.
 *
 * @author Hareesh Ravindran
 */
public final class FieldMetaDataProcessor {
    
    private FieldMetaData metaData;
    private ClassLoader classLoader;
    
    // Define arrays of controls for different purposes.
    private static final UIComponentType[] SIMPLE_BOOLEAN_CONTROLS = 
    {UIComponentType.CONTROL_BOOLEAN_CHECKBOX, 
             UIComponentType.CONTROL_BOOLEAN_RADIOBUTTONS,
             UIComponentType.CONTROL_BOOLEAN_COMBO,
             UIComponentType.CONTROL_TEXTBOX};

    private static final UIComponentType[] SINGLE_SELECT_CONTROLS = 
    {UIComponentType.CONTROL_COMBOBOX, UIComponentType.CONTROL_SINGLESELECT,
             UIComponentType.CONTROL_RADIOBUTTON_SET};

    private static final UIComponentType[] MULTI_SELECT_CONTROLS = 
    {UIComponentType.CONTROL_CHECKBOX_SET, UIComponentType.CONTROL_MULTISELECT };

    private static final UIComponentType[] SIMPLE_TEXT_ENTRY_CONTROLS = 
    {UIComponentType.CONTROL_TEXTBOX };

    private static final UIComponentType[] COMPLEX_ENTRY_CONTROLS = 
    {UIComponentType.CONTROL_COMPLEX_ENTRY};   
    // Define arrays of controls for different purposes.    
    
    /**
     * Creates a new instance of FieldMetaDataProcessor
     */
    public FieldMetaDataProcessor(FieldMetaData metaData, 
                        ClassLoader classLoader) throws GDEException {        
        if(metaData == null) {
            throw new GDEException("The MetaData Object cannot be " +
                    "null in the FieldMetaDataProcessor.");
        } else if(classLoader == null) {
            throw new GDEException("The ClassLoader Object cannot be " +
                    "null in the FieldMetaDataProcessor.");            
        }
        this.metaData = metaData;
        this.classLoader = classLoader;
    }
    
    public SelectionFieldDisplayInfo 
            createDefaultSelFieldDisplayInfo() throws GDEException {
        if(metaData.getFieldDataTypeNature() == DataTypeNature.NATURE_COMPLEX) {
            throw new GDEException("Currently, the SelectionFieldDisplay is " +
                    "not available for complex types");
        }
      /*Choon yin 14 Nov 2006 - update latest constructor signature.
        return new SelectionFieldDisplayInfo(metaData.getFieldName(),
                 UIComponentType.CONTROL_COMBOBOX, false, null, false, null);
        */
        return new SelectionFieldDisplayInfo(metaData.getFieldName(),
                 UIComponentType.CONTROL_COMBOBOX, false, null, false);
        
       }
    
    public SimpleEntryFieldDisplayInfo 
            createDefaultSimpleEntryFieldDisplayInfo () throws GDEException {        
            UIComponentType uiCompType = (metaData.getFieldDataType() == 
                    SimpleDataTypes.TYPE_BOOLEAN || 
                    metaData.getFieldDataType() == 
                    SimpleDataTypes.TYPE_BOOLEAN_OBJ) ? 
                UIComponentType.CONTROL_BOOLEAN_CHECKBOX : 
                        UIComponentType.CONTROL_TEXTBOX;
        return new SimpleEntryFieldDisplayInfo(
                            metaData.getFieldName(), uiCompType);
    }
    
    public ComplexEntryFieldDisplayInfo 
            createDefaultComplexEntryFieldDisplayInfo() throws GDEException {
        return new ComplexEntryFieldDisplayInfo(metaData.getFieldName(),
                UIComponentType.CONTROL_COMPLEX_ENTRY, 
                false, null, getDefaultFieldInfoMap());
    }
    
    /**
     * This function returns FieldMetaData objects representing all the getter 
     * methods within the class. The 'get' prefix of the method is eliminated 
     * while retrieving the methods. 
     *
     * @return The Collection of FieldMetaData objects representing the 
     *      object
     */
    public Collection getProperties() throws GDEException {        
        return GenFieldUtil.getProperties(metaData, classLoader);
    }    
    
    /**
     * This is a recursive function that generates the FieldInfo map for the
     * field meta data represented by this class. The method will check if the 
     * data type of the field is of 'SIMPLE' nature, if so, it creates a default
     * SimpleEntryFieldDisplayInfo object using the 
     * createDefaultSimpleFieldDisplayInfo() method. If it finds that the field 
     * is a complex one, it will iterate through each field of the complex data 
     * structure and will use the createDefaultComplexEntryFieldInfo() method on
     * each fieldMeta data encountered, to create a ComplexEntryFieldDisplayInfo.
     *
     * The special cases of a complex data type containing a reference to itself,
     * is not handled in this fucntion, currently.
     *
     * @return The map containing the field names of the complex data object as
     *      keys and the FieldInfo objects for the corresponding fields as 
     *      values.
     * @throws GDEException if this method is invoked with a FieldMeta data for
     *      a simple data type.
     */    
    public Map getDefaultFieldInfoMap() throws GDEException {                
        return GenFieldUtil.getDefaultFieldInfoMap(metaData, classLoader);
    }
    
    public UIComponentType[] getControlList(int fieldDataEntryNature) {
        String fieldDataType = metaData.getFieldDataType();
        int fieldDataTypeNature = metaData.getFieldDataTypeNature();
        
        // Check if the field data type nature is complex, if so, return the 
        // array of complex entry controls.
        if(fieldDataTypeNature == DataTypeNature.NATURE_COMPLEX)
            return COMPLEX_ENTRY_CONTROLS;
        // Check if the field data type nature is simple. if so, return the 
        // array of simple entry controls based on different cases.
        if(fieldDataTypeNature == DataTypeNature.NATURE_SIMPLE) {
            if(fieldDataType == SimpleDataTypes.TYPE_BOOLEAN || 
                    fieldDataType == SimpleDataTypes.TYPE_BOOLEAN_OBJ) {
                return SIMPLE_BOOLEAN_CONTROLS;
            } 
            if(fieldDataEntryNature == FieldDataEntryNature.TYPE_SINGLE_SELECT) {
                return SINGLE_SELECT_CONTROLS;
            }
            if(fieldDataEntryNature == FieldDataEntryNature.TYPE_MULTI_SELECT) {
                return MULTI_SELECT_CONTROLS;
            }
            // If field DataType nature is 'Entry' then return a text box in
            // the collection.
            return SIMPLE_TEXT_ENTRY_CONTROLS;
        }
        // If the data type nature is neither simple, nor complex, throw a
        // GDERuntimeException.
        throw new GDERuntimeException("Not Able to resolve the list of " +
                "controls. Please check the field data type entry and the " +
                "field data type nature.");
    }
}

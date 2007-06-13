
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
public final class UIComponentType {
    
    private int componentType;
    private String componentTypeName;
    private String componentPrefix;
    
    private static final int TYPE_TEXTBOX = 0;
    private static final int TYPE_COMBOBOX = 1;
    private static final int TYPE_SINGLESELECT = 2;
    private static final int TYPE_MULTISELECT = 3;
    private static final int TYPE_RADIOBUTTON_SET = 4;
    private static final int TYPE_CHECKBOX_SET = 5;
    private static final int TYPE_BOOLEAN_CHECKBOX = 6;    
    private static final int TYPE_BOOLEAN_RADIOBUTTONS = 7;
    private static final int TYPE_BOOLEAN_COMBO = 8;
    private static final int TYPE_COMPLEX_ENTRY = 9;
    
    public UIComponentType(int compType, 
                    String compTypeName, String compPrefix) {
        this.componentType = compType;
        this.componentTypeName = compTypeName;
        this.componentPrefix = compPrefix;
    }       
    
    public static final UIComponentType CONTROL_TEXTBOX = 
                new UIComponentType(TYPE_TEXTBOX, "Text Box", "txt");
    public static final UIComponentType CONTROL_COMBOBOX =
                new UIComponentType(TYPE_COMBOBOX, "Combo Box", "cmb");
    public static final UIComponentType CONTROL_SINGLESELECT = 
                new UIComponentType(TYPE_SINGLESELECT, 
                        "Single Select List", "lstSingle");
    public static final UIComponentType CONTROL_MULTISELECT = 
                new UIComponentType(TYPE_MULTISELECT, 
                        "Multi Select List", "lstMulti");
    public static final UIComponentType CONTROL_RADIOBUTTON_SET = 
                new UIComponentType(TYPE_RADIOBUTTON_SET, 
                "Radio Button Set", "optLst");
    public static final UIComponentType CONTROL_CHECKBOX_SET = 
                new UIComponentType(TYPE_CHECKBOX_SET, "Check Box Set", "chkLst");
    // Complex entry control represents a text area with a button to bring up
    // the wizard formatted/custom poupup window. The popup window is responsible
    // for capturing all the details of the complex object and returning the
    // filled complex object instance.     
    public static final UIComponentType CONTROL_COMPLEX_ENTRY = 
                new UIComponentType(TYPE_COMPLEX_ENTRY, 
                "Complex Entry Control", "cmplxCtrl");
    
    // These controls are specifically for displaying boolean types. Currently
    // boolean values can be displayed using a check box, a textbox (with true
    // or false value), combo box (with true/false values) or radio buttons with
    // true/false options.
    public static final UIComponentType CONTROL_BOOLEAN_CHECKBOX = 
                new UIComponentType(TYPE_BOOLEAN_CHECKBOX, "Check Box", "chkBool");
    public static final UIComponentType CONTROL_BOOLEAN_RADIOBUTTONS = 
                new UIComponentType(TYPE_BOOLEAN_RADIOBUTTONS, 
                "Boolean Radio Buttons", "optBool");
    public static final UIComponentType CONTROL_BOOLEAN_COMBO = 
                new UIComponentType(TYPE_BOOLEAN_COMBO, "Boolean Combo", "cmbBool");
            
    
    public int getComponentType() { return this.componentType; }
    public String getComponentTypeName() { return this.componentTypeName; }
    public String getComponentPrefix() { return this.componentPrefix; }
    
       public void setComponentType(int componentType) { this.componentType=componentType; }
    public void setComponentTypeName(String componentTypeName) { this.componentTypeName=componentTypeName; }
    public void setComponentPrefix(String componentPrefix) { this.componentPrefix=componentPrefix; }
 
    /**
     * Override the toString() method. This is required, as the type is to be
     * displayed in the combobox. The value of the toString() method will be 
     * displayed in the combo box.
     */
    public String toString() { return this.componentTypeName; }
}


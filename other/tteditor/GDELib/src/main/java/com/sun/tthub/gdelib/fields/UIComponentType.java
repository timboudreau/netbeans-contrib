
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


package com.sun.tthub.gdelib.fields;

/**
 *
 * @author Hareesh Ravindran
 */
public final class UIComponentType implements java.io.Serializable {

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


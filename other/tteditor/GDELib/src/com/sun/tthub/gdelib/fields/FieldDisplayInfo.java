
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

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.tthub.gdelib.GDERuntimeException;
import com.sun.tthub.gdelib.InvalidArgumentException;

/**
 *
 * @author Hareesh Ravindran
 */
public abstract class FieldDisplayInfo implements Cloneable ,java.io.Serializable{
    
    protected String fieldDisplayName;
    protected UIComponentType displayUIComponentType;
    
    /**
     * Creates a new instance of FieldDisplayInfo
     */
    public FieldDisplayInfo() {}
    
    public FieldDisplayInfo(String fieldDisplayName, 
                                UIComponentType compType) {
        this.fieldDisplayName = fieldDisplayName;
        this.displayUIComponentType = compType;
    }
   
    public String getFieldDisplayName() { return this.fieldDisplayName; }
    public void setFieldDisplayName(String fieldDisplayName) {
        this.fieldDisplayName = fieldDisplayName;
    }
    
    /**
     * This abstract function is only to include the public clone function in
     * the FieldDisplayInfo class. The base class has a  protected clone method
     */
    public abstract Object clone() throws CloneNotSupportedException;
    
    public UIComponentType getUIComponentType() {
        return this.displayUIComponentType; 
    }
    public void setUIComponentType(UIComponentType compType) {
        this.displayUIComponentType = compType;
    }
    
    public abstract int getFieldDataEntryNature();
    public abstract String getDisplayInfoStr();
    
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(!(obj instanceof FieldDisplayInfo)) 
            return false;
        FieldDisplayInfo attr = (FieldDisplayInfo) obj;
        
        boolean displayNameIsEqual = (fieldDisplayName == null) ?
            (attr.getFieldDisplayName() == null) :
            fieldDisplayName.equals(attr.getFieldDisplayName());
        return (displayNameIsEqual && 
                (displayUIComponentType == attr.getUIComponentType()));
    }    
    /*
    public String toString() {
        return "Field Display Name: '" + this.fieldDisplayName + "', " +
                "Component Type: '" + this.displayUIComponentType + "'";
    }
    */
    public String toString(){
        
            StringBuffer strBuf= new StringBuffer();
            strBuf.append("<display-info>\r\n");
            strBuf.append("<display-name>");
            strBuf.append(this.fieldDisplayName);
            strBuf.append("</display-name>\r\n");
            
            strBuf.append("<ui-component>");
            //component singleton class static reference
            strBuf.append(this.displayUIComponentType.toString());
            strBuf.append("</ui-component>\r\n");
            strBuf.append("</display-info>\r\n");
            return strBuf.toString();
    }
}

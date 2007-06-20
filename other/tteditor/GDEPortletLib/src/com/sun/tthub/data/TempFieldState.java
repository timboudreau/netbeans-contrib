
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


package com.sun.tthub.data;
import com.sun.tthub.gdelib.fields.UIComponentType;

/**
 *
 * @author choonyin
 */
public abstract class TempFieldState implements java.io.Serializable{
    
    /** Creates a new instance of TempFieldState */
    protected UIComponentType componentType=null;
    protected boolean isValueChangedFlag=false; 
    
    public TempFieldState(UIComponentType componentType) {
        this.componentType=componentType;
    }
    public UIComponentType  getUIComponentType() {
        return this.componentType;
    }
    public boolean getIsValueChanged(){
        return this.isValueChangedFlag;
    }    
    public void setIsValueChanged(boolean isValueChangedFlag){
        this.isValueChangedFlag=isValueChangedFlag;
    }
    
}

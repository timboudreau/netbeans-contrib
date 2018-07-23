
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

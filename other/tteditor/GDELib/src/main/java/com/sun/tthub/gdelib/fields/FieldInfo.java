
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

import com.sun.tthub.gdelib.InvalidArgumentException;

/**
 *
 * @author Hareesh Ravindran
 */
public class FieldInfo implements Cloneable,java.io.Serializable {

    protected FieldMetaData fieldMetaData;
    protected FieldDisplayInfo fieldDisplayInfo;
    
    /**
     * Creates a new instance of FieldInfo
     */
    public FieldInfo() {}
    
    public FieldInfo(FieldMetaData fieldMetaData, 
                            FieldDisplayInfo fieldDisplayInfo) {
        if(fieldMetaData == null || fieldDisplayInfo == null) {
            throw new InvalidArgumentException("The field meta data and the " +
                    "display control attribute cannot be null.");
        }
        this.fieldMetaData = fieldMetaData;
        this.fieldDisplayInfo = fieldDisplayInfo;
    }    
    
    public FieldMetaData getFieldMetaData() { return this.fieldMetaData; }
    public void setFieldMetaData(FieldMetaData fieldInfo) {
        if(fieldInfo == null) {
            throw new InvalidArgumentException("The field meta data for the " +
                    "TTValue field cannot be null.");
        }
        this.fieldMetaData = fieldInfo;
    }
    
    public FieldDisplayInfo getFieldDisplayInfo() { 
        return this.fieldDisplayInfo;
    } 
    public void setFieldDisplayInfo(FieldDisplayInfo attr) {
        if(attr == null) {
            throw new InvalidArgumentException("The display control attribute " +
                    "for the TTValue field cannot be null.");
        }
        this.fieldDisplayInfo = attr;
    }
        
    public Object clone() throws CloneNotSupportedException {
        FieldMetaData data = (fieldMetaData == null) ? null : 
                (FieldMetaData) fieldMetaData.clone();
        FieldDisplayInfo attr = (this.fieldDisplayInfo ==  null) ?
                null : (FieldDisplayInfo) fieldDisplayInfo.clone();
        return new FieldInfo(data, attr);
    }
    
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(!(obj instanceof FieldInfo))
            return false;
        FieldInfo info = (FieldInfo) obj;
        boolean isMetaDataEqual = (fieldMetaData == null) ? 
            (info.getFieldMetaData() == null) : 
            fieldMetaData.equals(info.getFieldMetaData());
        if(!isMetaDataEqual)
            return false;        
        boolean isDisplayControlAttrEqual = (fieldDisplayInfo == null) ?
            (info.getFieldDisplayInfo() == null) : 
            fieldDisplayInfo.equals(info.getFieldDisplayInfo());        
        return isDisplayControlAttrEqual;
    }    
    
    public String toString() {
        return "FieldMetaData: [" + fieldMetaData + "], " +
                "FieldDisplayInfo: [" + fieldDisplayInfo + "]";
    }
}

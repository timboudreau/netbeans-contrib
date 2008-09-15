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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.portalpack.portlets.spring.handlers.form;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author Satyaranjan
 */
public class InputParam {

    private String name;
    private String label;
    private String[] values;
    private String componentType;
    private DataType dataType;
    private String getterName;
    private String setterName;

    public InputParam(String name, String label, String[] values, DataType type,String componentType) {
        this.name = name;
       // this.desc = desc;
        this.dataType = type;
        this.componentType = componentType;
        this.label = label;
        this.values = values;
       /// this.desc = getParsedDesc(description);
    }
    
    private String[] getParsedValues(String d) {
        
        
        if(componentType.equals(TypesHelper.CHECKBOX_COMP)
                || componentType.equals(TypesHelper.SELECT_COMP)
                || componentType.equals(TypesHelper.RADIO_COMP)) {
            
             StringTokenizer st = new StringTokenizer(d,",");
             List list = new ArrayList();
             while(st.hasMoreTokens()) {
                 String token = st.nextToken();
                 if(token != null && !token.trim().equals(""))
                    list.add(token);
             }
             
             return (String [])list.toArray(new String[0]);
        }
        
        return new String[]{d};
        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType type) {
        this.dataType = type;
    }

    public String[] getValues() {
        ///return desc;
        ///return getParsedValues(encodedValues);
        return values;
    }
    
    public String getEncodedValues() {
        StringBuffer sb = new StringBuffer();
        for(String value:values) {
            sb.append(value);
            sb.append(",");
        }
        return sb.toString();
    }

    public void setValues(String[] values) {
        this.values = values;
        ///this.desc = getParsedDesc(description);
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }
    
    public String getGetterName() {
        
        if(getterName != null) return getterName;
        
        char ch = name.toCharArray()[0];
        if(Character.isLowerCase(ch))
             getterName = name.replaceFirst(String.valueOf(ch), String.valueOf(Character.toUpperCase(ch)));
        
        getterName = "get" + getterName;
        return getterName;
    }
    
    public String getSetterName() {
        
        if(setterName != null) return setterName;
        
        char ch = name.toCharArray()[0];
        if(Character.isLowerCase(ch))
            setterName = name.replaceFirst(String.valueOf(ch), String.valueOf(Character.toUpperCase(ch)));
        
        setterName = "set" + setterName;
        return setterName;
    }

    public String toString() {
        return name + ":   " + dataType;
    }
    
}
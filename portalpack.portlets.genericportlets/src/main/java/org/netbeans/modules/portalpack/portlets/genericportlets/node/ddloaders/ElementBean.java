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

package org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author Satyaranjan
 */
public class ElementBean implements DataBean {
    private String name;
    private String value = "";
    private boolean isArrayBean;
    private char separator = ',';

    public ElementBean(String name,Object[] values,boolean isIndexBean)
    {
        this.name = name;
        for(int i=0;i<values.length;i++)
        {
            if(values[i] == null)continue;
            if(i != values.length - 1)
                value += values[i].toString() + separator;
            else
                value += values[i].toString();
        }
        isArrayBean = isIndexBean;
    }
    
    public ElementBean(String name,String value)
    {
        this.name = name;
        this.value = value;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    public boolean isArrayBean()
    {
        return isArrayBean;
    }
    
    public void setValues(String[] values)
    {
        value = "";
        for(int i=0;i<values.length;i++)
        {
            if(values[i] == null)continue;
            if(i != values.length - 1)
                value += values[i].toString() + separator;
            else
                value += values[i].toString();
        }
        isArrayBean = true;
    }
    
    public String[] getValues()
    {
        StringTokenizer st = new StringTokenizer(value,separator +"");
        List list = new ArrayList();
        while(st.hasMoreTokens())
        {
            list.add(st.nextToken());
        }
        return (String [])list.toArray(new String[0]);
    }
    
}

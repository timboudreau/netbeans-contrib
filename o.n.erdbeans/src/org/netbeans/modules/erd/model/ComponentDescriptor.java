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


package org.netbeans.modules.erd.model;

import java.util.*;
import org.netbeans.modules.erd.graphics.ERDScene;

public abstract class ComponentDescriptor implements Cloneable{

    private HashMap <Enum,String> properties=new HashMap<Enum,String>();
    private String componentId;

    public abstract String getType();
    
    
    public void setProperty(Enum type,String value){
        properties.put(type,value);
    }
  
    public String getProperty(Enum type){
        return properties.get(type);
    }
   
    
    public void setId(String componentId){
        this.componentId=componentId;
    }
    
    public String getId(){
        return componentId;
    }
    
    abstract  public void presentComponent(ERDScene scene);
        
    

    public String toString(){
        return properties.toString();
                
    }

}

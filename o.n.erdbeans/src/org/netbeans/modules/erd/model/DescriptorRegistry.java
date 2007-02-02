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
import org.netbeans.modules.erd.model.component.ColumnDescriptor;
import org.netbeans.modules.erd.model.component.ConnectionDescriptor;
import org.netbeans.modules.erd.model.component.TableDescriptor;


public final class DescriptorRegistry {

    private static HashMap<String, Class> descriptors ;  

    static {
        descriptors= new HashMap<String, Class> ();
        descriptors.put(ColumnDescriptor.NAME, ColumnDescriptor.class);
        descriptors.put(ConnectionDescriptor.NAME, ConnectionDescriptor.class);
        descriptors.put(TableDescriptor.NAME, TableDescriptor.class);
    }
    
    
    
    
   
    
    public static ComponentDescriptor getComponentDescriptor (String type) {
        synchronized (descriptors) {
            
           try {  
            return (ComponentDescriptor)descriptors.get(type).newInstance();
           } catch(Exception e){
              return null; 
           } 
        }
    }

    
    

    
}

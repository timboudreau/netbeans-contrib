/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.assistant;

import java.util.*;
/*
 * AssistantContext.java
 * 
 * Created on October 11, 2002, 1:52 PM
 *
 * @author  Richard Gregor
 */
public class AssistantContext {
    private String name;    
    private Vector ids;    
    
    public AssistantContext(){     
        this((AssistantID)null);
    }
    
    public AssistantContext(AssistantID id){
        ids = new Vector();
        if(id != null)
            addID(id);
    }
    
   public void addID(AssistantID id){
        ids.addElement(id);
    }
    public Enumeration getIDs(){
        Enumeration enum ;
        if(ids != null)
            enum = ids.elements();
        else
            enum = null;
        return enum;
    }
    
    public void addID(AssistantID[] id){
        if(id == null)
            return;
        for(int i = 0 ; i < id.length; i++){
            addID(id[i]);
        }
    }
}

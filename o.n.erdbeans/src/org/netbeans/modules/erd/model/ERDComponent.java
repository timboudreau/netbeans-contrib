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

public final class ERDComponent {

    private ERDDocument document;
    private String componentID;
    private String type;
    
    
    
    

    private ComponentDescriptor componentDescriptor;
    
    

    ERDComponent (ERDDocument document, String componentID, ComponentDescriptor componentDescriptor) {
        
        this.document = document;
        this.componentID = componentID;
        this.type = componentDescriptor.getType();
        
        
    
       
        this.componentDescriptor=componentDescriptor;
        this.componentDescriptor.setId(componentID);
    }

    
    public ComponentDescriptor getComponentDescriptor () {
        assert document.getTransactionManager ().isAccess ();
        return componentDescriptor;
    }

    
    public ERDDocument getDocument () {
        return document;
    }

    /**
     * Returns a component id.
     * @return the component id
     */
    public String getComponentID () {
        return componentID;
    }

    /**
     * Returns a component type id
     * @return the component type id
     */
    public String getType () {
        return type;
    }
    
    
    public void writePropertyWithoutAffectingUndoRedo(Enum propertyName,String propertyValue){
        writePropertyInDescriptor(propertyName, propertyValue);
    }
    
    
    
    public void writeProperty(Enum propertyName,String propertyValue){
        String oldValue=writePropertyInDescriptor(propertyName,propertyValue);
        if(oldValue==null)
            return;
        document.getTransactionManager ().writePropertyHappened (this, propertyName, oldValue, propertyValue);
    }
    
 
    private String writePropertyInDescriptor(Enum propertyName,String propertyValue){
        assert document.getTransactionManager ().isWriteAccess ();
        
        assert componentDescriptor != null;

        String oldValue = componentDescriptor.getProperty(propertyName);
      
        if (oldValue == propertyValue)
            return null;

        

        componentDescriptor.setProperty(propertyName, propertyValue);
        return oldValue;

    }
    
    

    /**
     * Returns a property value of a specified property
     * @param propertyName the property name
     * @return the property value
     */
    public String readProperty (Enum propertyName) {
        assert document.getTransactionManager ().isAccess ();
        String value = componentDescriptor.getProperty(propertyName);
        assert value != null;
        return value;
    }

   

    
    public void presentComponent(ERDScene scene){
        componentDescriptor.presentComponent(scene);
    }
    

    
    public String toString () {
        return componentID + ":" + type;
    }

    

}

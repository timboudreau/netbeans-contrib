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

import java.util.HashMap;
import java.util.Collections;
import java.util.Collection;

public final class ERDDocument {


    private final DocumentInterfaceImpl documentInterface;
   // private final DescriptorRegistry descriptorRegistry;
    private final ListenerManager listenerManager;
    private final TransactionManager transactionManager;
    private String dbschema;
    private boolean isDefaultLayout;
    

    private String selectionSourceID;
  
    private HashMap<String,ERDComponent> components;
    /**
     * Creates an instance of document.
     * @param documentInterface the documentInterface interface
     */
    public ERDDocument (DocumentInterfaceImpl documentInterface) {
        this.documentInterface=documentInterface;
        listenerManager=new ListenerManager(this);
   
        transactionManager = new TransactionManager (this,listenerManager);

        components = new HashMap<String, ERDComponent> (100);
        
        

        
    }
    
    
    
    public void markAllComponentsAsAffected(){
        assert getTransactionManager ().isWriteAccess();
        for(ERDComponent component : components.values()){
            getTransactionManager ().notifyComponentCreated(component);
        }
    }
    
    
    public void setIsDefaultLayout(boolean isDefaultLayout){
        this.isDefaultLayout=isDefaultLayout;
    }
    
    public boolean getIsDefaultLayout(){
        return isDefaultLayout;
    }
    
    public void setDBSchema(String dbschema){
        this.dbschema=dbschema;
    }
    
    public String getDBSchema(){
        return dbschema;
    }

    public ListenerManager getListenerManager(){
        return listenerManager;
    }
    
    /**
     * Returns a transaction manager of the document.
     * @return the transaction manager
     */
    public TransactionManager getTransactionManager () {
        return transactionManager;
    }


    public ERDComponent createComponent (String componentId,String componentType) {
        
        assert transactionManager.isWriteAccess ();

        ComponentDescriptor componentDescriptor = DescriptorRegistry.getComponentDescriptor (componentType);
        assert componentDescriptor != null : "Missing component descriptor for " + componentType;
       

        ERDComponent component = new ERDComponent (this, componentId, componentDescriptor);

        components.put (component.getComponentID (), component);
        //getListenerManager ().notifyComponentCreated (component);

        return component;
    }

    
   

    
    /**
     * Returns a component with specified component id.
     * @param componentID the component id
     * @return the component
     */
    public ERDComponent getComponentByID (String componentID) {
        assert transactionManager.isAccess ();
        ERDComponent component = components.get (componentID);
        return component;
    }

    
    public Collection<ERDComponent> getAllComponents(){
        return components.values();
    }
    
    public DocumentInterfaceImpl getDocumentInterface(){
        return documentInterface;
    }

    

   

}

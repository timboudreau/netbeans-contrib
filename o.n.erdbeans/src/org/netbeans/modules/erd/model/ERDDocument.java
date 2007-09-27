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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 * made subject to such option by the copyright holder.
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

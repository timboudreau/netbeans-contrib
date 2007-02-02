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


import org.openide.ErrorManager;

import java.util.*;



public final class ListenerManager {

  

    private static final boolean INVOKE_VALIDATORS = true;

    private long eventID = 0;
    private final ERDDocument document;

    private Set<ERDComponent> components=new HashSet<ERDComponent>();
   
    
    ERDController controller;

    ListenerManager (ERDDocument document) {
        this.document = document;
        this.controller=new ERDController(document);
        
    }

  
   

   public ERDController getController(){
       return  controller;
   }
    

    void addAffectedERDComponent (ERDComponent component) {
        
        components.add (component);
        
    }

   

    void notifyComponentCreated (ERDComponent component) {
        
        components.add (component);
    }

   
    long getEventID () {
        
        return eventID;
    }

    ERDEvent fireEvent () {
        

        if (components.isEmpty ())
            return null;

        
        Set<ERDComponent> fullyComponentsUm = Collections.unmodifiableSet (components);
        
        final ERDEvent event = new ERDEvent (++ eventID,fullyComponentsUm);

        fireEventInWriteAccess (event);
        components=new HashSet<ERDComponent>();
        return event;
    }

    private void fireEventInWriteAccess (ERDEvent event) {
        

       
            try {
                controller.notifyEventFired (event);
            } catch (ThreadDeath td) {
                throw td;
            } catch (Throwable th) {
                ErrorManager.getDefault ().notify (th);
            }
      
    }

   

    

    
}

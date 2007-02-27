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



import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.erd.graphics.ERDScene;
import org.netbeans.modules.erd.model.component.ColumnDescriptor;
import org.netbeans.modules.erd.model.component.TableDescriptor;
import org.netbeans.modules.erd.model.ERDComponent;

/**
 * @author David Kaspar
 */
public class ERDController  {

    private ERDDocument document;
    private ERDScene scene;
    private static final float ZOOM_FACTOR = 1.2f;

    
    private int zoom = 0;
    

    private volatile long eventID = 0;
    
    
    public ERDController (ERDDocument document) {
        this.document = document;
        scene = new ERDScene (document);
        
    }

    public void writeAccess (Runnable runnable) {
        runnable.run ();
    }
    
    public void paint(Graphics2D graphics2D){
        scene.paint(graphics2D);
    }
    
    public void zoomIn () {
        scene.setZoomFactor((float) Math.pow (ZOOM_FACTOR, ++zoom));
        scene.validate();
        
    }

    public void zoomOut () {
        scene.setZoomFactor((float) Math.pow (ZOOM_FACTOR, --zoom));
        scene.validate();
    }
    
    public void invokeLayout(){
        scene.invokeLayout();
    }

    public void notifyEventFired (final ERDEvent event) {
        scene.layoutScene();
        if (eventID < event.getEventID ())
            eventID = event.getEventID ();
        SwingUtilities.invokeLater (new Runnable() {
            public void run () {
                document.getTransactionManager ().readAccess (new Runnable() {
                    public void run () {
                        
                        
                        Set<ERDComponent> components=event.geAffectedComponents();
                        List<ERDComponent> sortedComponents=sort(components);
                        for(ERDComponent component: sortedComponents){
                        
                              component.presentComponent(scene);
                        
                            
                        }
                        
                        scene.validate ();
                    }
                });
            }
        });
    }

    private List<ERDComponent> sort(Set<ERDComponent> components){
        
        ArrayList<ERDComponent> table=new ArrayList<ERDComponent>();
        ArrayList<ERDComponent> pk=new ArrayList<ERDComponent>();
        ArrayList<ERDComponent> fk=new ArrayList<ERDComponent>();
        ArrayList<ERDComponent> other=new ArrayList<ERDComponent>();
        
        for(ERDComponent component: components){
            if(component.getType().equals(TableDescriptor.NAME))
                table.add(component);
            else
              if(component.getType().equals(ColumnDescriptor.NAME) && component.readProperty(ColumnDescriptor.PROPERTY.IS_PK).equals("true")){
                pk.add(component);  
              }
              else 
                  if(component.getType().equals(ColumnDescriptor.NAME) && component.readProperty(ColumnDescriptor.PROPERTY.IS_FK).equals("true")){
                    fk.add(component);
                  }
                   else{
                    other.add(component);
                   }     
        }
        table.addAll(pk);
        table.addAll(fk);
        table.addAll(other);
        return table;
        
    } 

    public ERDScene getScene () {
        return scene;
    }

    public JComponent createView () {
        return scene.createView ();
    }

}

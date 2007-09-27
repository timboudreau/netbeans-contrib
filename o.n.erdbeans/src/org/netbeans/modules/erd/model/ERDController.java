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

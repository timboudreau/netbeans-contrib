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

package org.netbeans.modules.erd.model.component;

import java.awt.Point;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.widget.SwingScrollWidget;
import org.netbeans.modules.erd.graphics.ERDScene;
import org.netbeans.modules.erd.graphics.TableWidget;
import org.netbeans.modules.erd.graphics.TableWidget;
import org.netbeans.modules.erd.model.ComponentDescriptor;



public class TableDescriptor extends ComponentDescriptor {
    
    public final static String NAME="TABLE#COMPONENT";
    
    public enum PROPERTY {X,Y};
    
    
    public TableDescriptor() {
       
    
    }
    
    
   
    public void presentComponent(ERDScene scene){
        String nodeID=getId();
        SwingScrollWidget widget =(SwingScrollWidget)scene.findWidget(nodeID);
        if(widget==null){
           widget = (SwingScrollWidget)scene.addNode(nodeID);
           ((TableWidget)widget.getView()).setNodeProperties (null, nodeID, null, null);
           scene.addPin (nodeID, nodeID + ERDScene.PIN_ID_DEFAULT_SUFFIX);
        } 
        String X=getProperty(TableDescriptor.PROPERTY.X);
        String Y=getProperty(TableDescriptor.PROPERTY.Y);
        if(X ==null | Y==null)
            return;
        int x=Integer.parseInt(X);
        int y=Integer.parseInt(Y);
        widget.setPreferredLocation (new Point (x, y));
        
        
    }
    @Override
    public String getType(){
        return NAME;
    }
}

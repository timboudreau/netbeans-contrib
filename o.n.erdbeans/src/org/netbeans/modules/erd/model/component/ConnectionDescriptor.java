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
import java.util.HashMap;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.modules.erd.graphics.ERDScene;
import org.netbeans.modules.erd.graphics.OneManyAnchor;
import org.netbeans.modules.erd.graphics.OneOneAnchor;
import org.netbeans.modules.erd.graphics.ZeroManyAnchor;
import org.netbeans.modules.erd.model.ComponentDescriptor;

public class ConnectionDescriptor extends ComponentDescriptor{
    public final static String NAME="CONNECTION#COMPONENT";
    public final static String ONE_MANY="ONE_MANY";
    public final static String ONE_ONE="ONE_ONE";
    public final static String ZERO_MANY="ZERO_MANY";
    public enum PROPERTY {SOURCE,TARGET,RELATION};
    
    
    
    private static HashMap<String, AnchorShape> anchors ;  

    static {
         anchors= new HashMap<String, AnchorShape> ();
         anchors.put(ONE_MANY, new OneManyAnchor());
         anchors.put(ONE_ONE, new OneOneAnchor());
         anchors.put(ZERO_MANY, new ZeroManyAnchor());
    }
    
    
    public ConnectionDescriptor() {
      
    }
    
   
    
    public void presentComponent(ERDScene scene){
        String edgeId=getId();
        String source=getProperty(PROPERTY.SOURCE);
        String target=getProperty(PROPERTY.TARGET);
        String relation=getProperty(PROPERTY.RELATION);
        ConnectionWidget widget=(ConnectionWidget)scene.addEdge(edgeId);
        widget.setSourceAnchorShape(anchors.get(relation));
        //widget.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        scene.setEdgeSource(edgeId,source+ERDScene.PIN_ID_DEFAULT_SUFFIX);
        scene.setEdgeTarget(edgeId,target+ERDScene.PIN_ID_DEFAULT_SUFFIX);
        
    }
    
    public String getType(){
        return NAME;
    }
 
    
    
}

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

package org.netbeans.modules.erd.model.component;
import java.awt.Image;
import java.util.HashMap;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.anchor.AnchorShapeFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.modules.erd.graphics.ERDScene;
import org.netbeans.modules.erd.graphics.OneManyAnchor;
import org.netbeans.modules.erd.graphics.OneOneAnchor;
import org.netbeans.modules.erd.graphics.ZeroManyAnchor;
import org.netbeans.modules.erd.model.ComponentDescriptor;
import org.openide.util.Utilities;

public class ConnectionDescriptor extends ComponentDescriptor{
    public final static String NAME="CONNECTION#COMPONENT";
    public final static String ONE_MANY="ONE_MANY";
    public final static String ONE_ONE="ONE_ONE";
    public final static String ZERO_MANY="ZERO_MANY";
    public enum PROPERTY {SOURCE,TARGET,RELATION};
    private static Image oneToMany=Utilities.loadImage("org/netbeans/modules/erd/resources/erd_icon.png");
    
    
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

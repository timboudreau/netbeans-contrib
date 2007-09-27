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

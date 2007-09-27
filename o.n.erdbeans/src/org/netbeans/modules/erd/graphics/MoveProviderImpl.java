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

package org.netbeans.modules.erd.graphics;
import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import org.netbeans.api.visual.action.MoveProvider;
import org.netbeans.api.visual.graph.layout.GraphLayoutListener;
import org.netbeans.api.visual.graph.layout.UniversalGraph;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.erd.graphics.MoveProviderImpl.WritePropertyCommand;
import org.netbeans.modules.erd.model.ERDComponent;
import org.netbeans.modules.erd.model.ERDDocument;
import org.netbeans.modules.erd.model.component.TableDescriptor;

/**
 *
 * @author luke
 */
public class MoveProviderImpl implements MoveProvider,GraphLayoutListener<String,String> {
    
    private ERDDocument document;
    private ERDScene scene;
    private static WritePropertyWithoutUndoRedo withoutUndoRedo=new WritePropertyWithoutUndoRedo();
    private static WriteWithUndoRedo withUndoRedo=new WriteWithUndoRedo();
    
    private HashMap<String,Point> affectedWidgets=new HashMap<String,Point>();
    public MoveProviderImpl(ERDDocument document,ERDScene scene) {
        this.document=document;
        this.scene=scene;
    }
    
    
    
    public void movementStarted(Widget widget) {
        affectedWidgets.clear();
    }
    
    public void movementFinished(final Widget widget) {
       updateDocument(withUndoRedo);
        
        
    }
    
    public Point getOriginalLocation(Widget widget) {
        return widget.getPreferredLocation();
    }
    
    public void setNewLocation(Widget widget, Point location) {
       
        widget.setPreferredLocation(location);
        String componentId=(String)scene.findObject(widget)  ;
        affectedWidgets.put(componentId, location);
    }
    
    public void graphLayoutStarted(UniversalGraph<String, String> graph){
        affectedWidgets.clear();
    }
    
    
    public void graphLayoutFinished(UniversalGraph<String, String> graph){
       updateDocument(withoutUndoRedo); 
    }
    
    public void nodeLocationChanged(UniversalGraph<String,String> graph, String node, Point previousPreferredLocation, Point newPreferredLocation){
       affectedWidgets.put(node, newPreferredLocation);
    }
    
    private void updateDocument(final WritePropertyCommand command){
        document.getTransactionManager().writeAccess(new Runnable() {
            public void run() {
                for(String componentId:affectedWidgets.keySet()){
                    ERDComponent component=document.getComponentByID(componentId);
                    Point point=affectedWidgets.get(componentId);
                    String x=Integer.toString((int)point.getX());
                    String y=Integer.toString((int)point.getY());
                    command.writeProperty(component,TableDescriptor.PROPERTY.X, x);
                    command.writeProperty(component,TableDescriptor.PROPERTY.Y, y);
                }
            }
        });
    }
    
    static interface WritePropertyCommand {
        public void writeProperty(ERDComponent component,Enum propertyName,String propertyValue);
    }
    
    static class WritePropertyWithoutUndoRedo implements WritePropertyCommand{
          public void writeProperty(ERDComponent component,Enum propertyName,String propertyValue){
                component.writePropertyWithoutAffectingUndoRedo(propertyName, propertyValue);
          }
    }
    
    static class WriteWithUndoRedo implements WritePropertyCommand{
          public void writeProperty(ERDComponent component,Enum propertyName,String propertyValue){
                component.writeProperty(propertyName, propertyValue);
          }
    }
    
}

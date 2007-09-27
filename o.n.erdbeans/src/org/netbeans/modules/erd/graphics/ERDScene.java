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




import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.anchor.*;
import org.netbeans.api.visual.graph.GraphPinScene;
import org.netbeans.api.visual.router.CollisionsCollector;
import org.netbeans.api.visual.router.CollisionsCollector;
import javax.swing.*;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.MoveProvider;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.action.ResizeStrategy;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.modules.erd.model.ERDDocument;
import org.netbeans.api.visual.anchor.*;
import org.netbeans.api.visual.graph.layout.GraphLayout;
import org.netbeans.api.visual.graph.layout.GridGraphLayout;
import org.netbeans.api.visual.layout.SceneLayout;
import org.netbeans.modules.erd.model.ERDComponent;
import org.netbeans.modules.erd.model.component.TableDescriptor;
import org.netbeans.api.visual.graph.layout.GraphLayoutListener;
import org.netbeans.api.visual.graph.layout.UniversalGraph;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.SwingScrollWidget;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.modules.erd.actions.PngAction;
import org.netbeans.modules.erd.graphics.ResizeStrategyImpl;
import org.netbeans.modules.erd.graphics.TableWidget;
import org.netbeans.modules.erd.graphics.TableWidget;
import org.netbeans.modules.erd.graphics.TableWidget;
import org.netbeans.modules.erd.model.ERDDocumentAwareness;
import org.openide.actions.RedoAction;
import org.openide.actions.UndoAction;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;








public class ERDScene extends GraphPinScene<String, String, String>  implements ERDDocumentAwareness{
    
    
    public static final String PIN_ID_DEFAULT_SUFFIX = "#default"; // NOI18N
    
    private LayerWidget backgroundLayer = new LayerWidget(this);
    private LayerWidget mainLayer = new LayerWidget(this);
    private LayerWidget connectionLayer = new LayerWidget(this);
    private LayerWidget upperLayer = new LayerWidget(this);
    
   // private CollisionsCollector collisionsCollector;
    private ERDDocument document;
    
    private GridGraphLayout<String,String> graphLayout;
    private SceneLayout sceneLayout;
    private WidgetAction moveAction ;
    private MoveProviderImpl moveHelper;
    private ResizeStrategy resizeStrategy=new ResizeStrategyImpl();
    private LayerWidget[] layers;
    private WidgetAction popupMenuAction;
    
    
    public ERDScene(ERDDocument document) {
        
        setOpaque (true);
        setBackground (PAINT_BACKGROUND);
        this.document=document;
        
        this.moveHelper=new MoveProviderImpl(document,this);
        addChild(backgroundLayer);
        addChild(mainLayer);
        addChild(connectionLayer);
        addChild(upperLayer);
        moveAction= ActionFactory.createMoveAction(null,moveHelper);
         popupMenuAction = ActionFactory.createPopupMenuAction (new ERDPopupMenuProvider ());
        getActions ().addAction (popupMenuAction);
       
        layers=new LayerWidget[2];
        layers[0]=mainLayer;
        layers[1]=connectionLayer;
        graphLayout = new GridGraphLayout<String, String> ().setChecker(true);
        graphLayout.addGraphLayoutListener(moveHelper);
        
        sceneLayout = new SceneLayout(this) {
            protected void performLayout() {
                graphLayout.layoutGraph(ERDScene.this);
            }
        };
        
       
        
        
    }
    
    
     public void setERDDocument (ERDDocument designDocument){
        layoutScene();  
     }
    
    public void layoutScene() {
        if(document.getIsDefaultLayout())
            sceneLayout.invokeLayout();
        document.setIsDefaultLayout(false);
    }
    
    public void invokeLayout(){
       sceneLayout.invokeLayout();
    }
    
    protected Widget attachNodeWidget(String node) {
        
        
        TableWidget table = new TableWidget(this);
        SwingScrollWidget widget=new SwingScrollWidget(this,table);
       // widget.setBorder(BorderFactory.createResizeBorder(8, Color.BLUE, false));
        widget.setCheckClipping(true);
        
        // widget.setPreferredBounds(clientArea);
        // widget.setPreferredLocation (new Point (50, 50));
        // widget.setMinimumBounds (new Rectangle (100, 200));
        // widget.setMaximumBounds (new Rectangle (500, 500));
        
        widget.getView().setLayout(LayoutFactory.createVerticalLayout());;
        addSceneListener((SceneListener)table.getView());
        mainLayer.addChild(widget);
        
        //widget.getActions ().addAction (createObjectHoverAction ());
        //widget.getHeader ().getActions ().addAction (createObjectHoverAction ());
        widget.getView().getActions().addAction(createObjectHoverAction());
        widget.getActions().addAction(createSelectAction());
        widget.getActions().addAction(ActionFactory.createResizeAction(resizeStrategy,null));
        widget.getActions().addAction(moveAction);
        
        return widget;
    }
    
    protected Widget attachPinWidget(String node, String pin) {
        if (pin.endsWith(PIN_ID_DEFAULT_SUFFIX))
            return null;
        
        ColumnWidget widget = new ColumnWidget(this);
        TableWidget table=(TableWidget)((SwingScrollWidget) findWidget(node)).getView();
        table.attachPinWidget(widget);
        widget.getActions().addAction(createObjectHoverAction());
        widget.getActions().addAction(createSelectAction());
        
        return widget;
    }
    
    protected Widget attachEdgeWidget(String edge) {
        ConnectionWidget connectionWidget = new ConnectionWidget(this);
        connectionWidget.setRouter( RouterFactory.createOrthogonalSearchRouter(layers));
       // connectionWidget.setSourceAnchorShape(new OneManyAnchor(12,true,true));
       // connectionWidget.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        connectionWidget.setControlPointShape(PointShape.SQUARE_FILLED_SMALL);
        connectionWidget.setEndPointShape(PointShape.SQUARE_FILLED_BIG);
        connectionLayer.addChild(connectionWidget);
        
        connectionWidget.getActions().addAction(createObjectHoverAction());
        connectionWidget.getActions().addAction(createSelectAction());
        // connectionWidget.getActions ().addAction (moveControlPointAction);
        
        return connectionWidget;
    }
    
    protected void attachEdgeSourceAnchor(String edge, String oldSourcePin, String sourcePin) {
        ((ConnectionWidget) findWidget(edge)).setSourceAnchor(getPinAnchor(sourcePin));
    }
    
    protected void attachEdgeTargetAnchor(String edge, String oldTargetPin, String targetPin) {
        ((ConnectionWidget) findWidget(edge)).setTargetAnchor(getPinAnchor(targetPin));
    }
    
    
    private Anchor getPinAnchor(String pin) {
        SwingScrollWidget nodeWidget = (SwingScrollWidget) findWidget(getPinNode(pin));
        Widget pinMainWidget = findWidget(pin);
        Anchor anchor;
        if (pinMainWidget != null) {
            anchor = AnchorFactory.createDirectionalAnchor(pinMainWidget, AnchorFactory.DirectionalAnchorKind.HORIZONTAL,3) ;
            anchor = ((TableWidget)nodeWidget.getView()).createAnchorPin(anchor);
        } else
            anchor =((TableWidget)nodeWidget.getView()).getNodeAnchor();
        return anchor;
    }
    
    
    private class ERDPopupMenuProvider implements PopupMenuProvider {

        public JPopupMenu getPopupMenu (Widget widget, Point localLocation) {
            JComponent component = ERDScene.this.getView ();

            
                return Utilities.actionsToPopup (new Action[]{
                    SystemAction.get (PngAction.class)
                    
                }, component);

           
           
            
        }
    }
    
     private static Paint PAINT_BACKGROUND;
     static {
        Image sourceImage = Utilities.loadImage ("org/netbeans/modules/vmd/flow/resources/paper_grid.png"); // NOI18N
        int width = sourceImage.getWidth (null);
        int height = sourceImage.getHeight (null);
        BufferedImage image = new BufferedImage (width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics ();
        graphics.drawImage (sourceImage, 0, 0, null);
        graphics.dispose ();
        PAINT_BACKGROUND = new TexturePaint (image, new Rectangle (0, 0, width, height));
//        PAINT_BACKGROUND = Color.WHITE;
    }
    
    
    
    
}

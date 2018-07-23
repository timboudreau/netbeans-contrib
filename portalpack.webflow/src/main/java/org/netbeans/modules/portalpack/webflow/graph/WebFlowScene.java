/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.webflow.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import org.netbeans.api.visual.action.AcceptProvider;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.action.TextFieldInplaceEditor;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.action.WidgetAction.Chain;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.graph.GraphPinScene;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.layout.SceneLayout;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.vmd.VMDColorScheme;
import org.netbeans.api.visual.vmd.VMDConnectionWidget;
import org.netbeans.api.visual.vmd.VMDFactory;
import org.netbeans.api.visual.vmd.VMDGraphScene;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.EventProcessingType;
import org.netbeans.api.visual.widget.ImageWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.portalpack.webflow.graph.actions.LinkCreateProvider;
import org.netbeans.modules.portalpack.webflow.graph.layout.ConnectionWrapperLayout;
import org.netbeans.modules.portalpack.webflow.palette.Shape;
import org.netbeans.modules.portalpack.webflow.palette.ShapeNode;
import org.openide.util.Utilities;

/**
 *
 * @author satyaranjan
 */
public class WebFlowScene extends GraphPinScene<Shape,WebFlowEdge,WebFlowPin> {

    public static final String PIN_ID_DEFAULT_SUFFIX = "#default"; // NOI18N
    private static final VMDColorScheme scheme = VMDFactory.getNetBeans60Scheme();

    protected LayerWidget backgroundLayer = new LayerWidget (this);
    protected LayerWidget mainLayer = new LayerWidget (this);
    protected LayerWidget connectionLayer = new LayerWidget (this);
    protected LayerWidget upperLayer = new LayerWidget (this);

    private Router router;

    private WidgetAction moveControlPointAction = ActionFactory.createOrthogonalMoveControlPointAction ();
    private WidgetAction moveAction = ActionFactory.createMoveAction ();
    //private final WidgetAction selectAction = ActionFactory.createSelectAction(new PageFlowSelectProvider());
      private final WidgetAction connectAction = ActionFactory.createConnectAction(connectionLayer, new LinkCreateProvider(this));

    private SceneLayout sceneLayout;
    
    private int nodeKey = 0;
    private int edgeKey = 0;
    private int pinKey = 0;

    public WebFlowScene() {
        super();
         setKeyEventProcessingType (EventProcessingType.FOCUSED_WIDGET_AND_ITS_PARENTS);

        addChild (backgroundLayer);
        addChild (mainLayer);
        addChild (connectionLayer);
        addChild (upperLayer);

        router = RouterFactory.createOrthogonalSearchRouter (mainLayer, connectionLayer);

        getActions ().addAction (ActionFactory.createZoomAction ());
        getActions ().addAction (ActionFactory.createPanAction ());
       // getActions ().addAction (ActionFactory.createRectangularSelectAction (this, backgroundLayer));

      //  sceneLayout = LayoutFactory.createSceneGraphLayout (this, new GridGraphLayout<String, String> ().setChecker (true));
        initialize();
    }

    private void initialize() {
        getActions().addAction(ActionFactory.createZoomAction());
        getActions().addAction(ActionFactory.createPanAction());
        getActions().addAction(ActionFactory.createRectangularSelectAction(this, backgroundLayer));
        getActions().addAction(ActionFactory.createAcceptAction(new AcceptProvider() {

            public ConnectorState isAcceptable(Widget widget, Point point, Transferable transferable) {
                Object obj = null;
                try {

                    obj = transferable.getTransferData(new DataFlavor("application/x-java-openide-nodednd; class=org.openide.nodes.Node", "application/x-java-openide-nodednd")); //NOI18N

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (obj == null) {

                    return ConnectorState.REJECT;
                } else if (obj instanceof ShapeNode) {
                    return ConnectorState.ACCEPT;
                }
                System.out.println("************class:::::::::" + obj.getClass());
                return ConnectorState.ACCEPT;
            }

            public void accept(Widget widget, Point point, Transferable transferable) {
                try {
                    Object obj = transferable.getTransferData(new DataFlavor("application/x-java-openide-nodednd; class=org.openide.nodes.Node", "application/x-java-openide-nodednd")); //NOI18N

                    WebFlowSceneUtility.addShapeToScene((WebFlowScene)widget.getScene(), (ShapeNode) obj, point);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    @Override
    protected Widget attachNodeWidget(Shape shape) {
       
        nodeKey ++;
        shape.setID(nodeKey);
        Widget widget = new Widget(this);
        widget.setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.CENTER, 1));    
        ImageWidget nodeWidget = new ImageWidget(this,Utilities.loadImage(shape.getImage()));
        mainLayer.addChild(widget);
    
        nodeWidget.setLayout(LayoutFactory.createAbsoluteLayout());
        ImageWidget imageWidget = new DefaultAnchorWidget(this, Utilities.loadImage("org/netbeans/modules/visual/resources/vmd-pin.png"));
        imageWidget.getActions().addAction(connectAction);
        imageWidget.getActions().addAction(createWidgetHoverAction());
        
        widget.addChild(nodeWidget);
        widget.addChild(imageWidget);
        widget.getActions().addAction(moveAction);
        
        widget.bringToFront();
        
      //  Border border = BorderFactory.createBevelBorder(BevelBorder.RAISED);
     //   widget.setBorder(border);
        //nodeWidget.getActions().addAction(selectAction);
        //nodeWidget.getActions().addAction(moveAction);
        //nodeWidget.getActions().addAction(connectAction);
        return widget;
    }

    @Override
    protected Widget attachEdgeWidget(WebFlowEdge edge) {
               assert edge != null;

        VMDConnectionWidget connectionWidget =  new VMDConnectionWidget(this, router);

        LabelWidget label = new LabelWidget(this, "edge");
        label.setOpaque(true);
        label.getActions().addAction(ActionFactory.createInplaceEditorAction(new WebFlowScene.CaseNodeTextFieldInplaceEditor()));

        connectionLayer.addChild(connectionWidget);

        connectionWidget.getActions().addAction(createObjectHoverAction());
      
        connectionWidget.getActions().addAction(moveControlPointAction);

       //// connectionWidget.setLayout(new ConnectionWrapperLayout(connectionWidget, label));
       //// connectionWidget.setConstraint(label, LayoutFactory.ConnectionWidgetLayoutAlignment.TOP_RIGHT, 10);
       ///// connectionWidget.addChild(label);

        return connectionWidget;
    }

    @Override
    protected Widget attachPinWidget(Shape pageNode, WebFlowPin pinNode) {
        assert pinNode != null;

     /*   if (pinNode.isDefault()) {
            return null;
        }*/

        VMDPinWidget widget = new VMDPinWidget(this, scheme);
        
        VMDNodeWidget nodeWidget = (VMDNodeWidget) findWidget(pageNode);
        if (nodeWidget != null) {
            nodeWidget.attachPinWidget(widget);
           //// widget.setProperties(pinNode.getName(), Arrays.asList(pinNode.getIcon(0)));
           pageNode.setPin(pinNode);

            Chain actions = widget.getActions();
            actions.addAction(createObjectHoverAction());
            actions.addAction(createSelectAction());
            actions.addAction(connectAction);
           // actions.addAction(doubleClickAction);
        } else {
            System.err.println("Node widget should not be null.");
        }

        return widget;
    }

    @Override
    protected void attachEdgeSourceAnchor(WebFlowEdge edge, WebFlowPin oldSourcePin, WebFlowPin sourcePin) {
       ((ConnectionWidget) findWidget (edge)).setSourceAnchor (getPinAnchor (sourcePin));
    }

    @Override
    protected void attachEdgeTargetAnchor(WebFlowEdge edge, WebFlowPin oldTargetPin, WebFlowPin targetPin) {
        ((ConnectionWidget) findWidget(edge)).setTargetAnchor(getPinAnchor(targetPin));
    }
    
      /*
     * Returns the Anchor for a given pin
     * @param pin The Pin
     * @return Anchor the anchor location
     */
    private Anchor getPinAnchor(WebFlowPin pin) {
        if (pin == null) {
            return null;
        }
        VMDNodeWidget nodeWidget = (VMDNodeWidget) findWidget(getPinNode(pin));
        Widget pinMainWidget = findWidget(pin);
        Anchor anchor;
        if (pinMainWidget != null) {
            anchor = AnchorFactory.createDirectionalAnchor(pinMainWidget, AnchorFactory.DirectionalAnchorKind.HORIZONTAL, 8);
            anchor = nodeWidget.createAnchorPin(anchor);
        } else {
            anchor = nodeWidget.getNodeAnchor();
        }
        return anchor;
    }

    public final class CaseNodeTextFieldInplaceEditor implements TextFieldInplaceEditor {

        public boolean isEnabled(Widget widget) {
            return true;
           /* NavigationCaseEdge caseNode = (NavigationCaseEdge) findObject(widget.getParentWidget());
            return caseNode.isModifiable();*/
        }

        public String getText(Widget widget) {
            return "edge";
           /* NavigationCaseEdge caseNode = (NavigationCaseEdge) findObject(widget.getParentWidget());
            return ((LabelWidget) widget).getLabel();*/
        }

        public void setText(Widget widget, String newName) {
         /*   if (newName.equals("")) {
                return;
            }*/

           /* NavigationCaseEdge caseNode = (NavigationCaseEdge) findObject(widget.getParentWidget());
            String oldName = caseNode.getName();

            if (caseNode.canRename()) {
                //Pin pin = getEdgeSource(caseNode);
                //caseNode.setName(pin, newName);
                caseNode.setName(newName);
            }

            ((LabelWidget) widget).setLabel(newName);
        }*/
    }
    }
    
     /**
     *
     * @param pageNode
     * @return
     */
    public WebFlowPin getDefaultPin(Shape shape) {
        Collection<WebFlowPin> pins = getNodePins(shape);
        if (pins == null) {
            System.err.println("Node is null?: " + shape);
        }
        for (WebFlowPin pin : pins) {
//            if (pin.isDefault()) {
//                return pin;
            return pin;
        }
        
        //System.err.println("Some reason this node: " + pageNode + " does not have a pin.");
        return null;
    }
    
    private static class DefaultAnchorWidget extends ImageWidget {

        public DefaultAnchorWidget(WebFlowScene scene, Image image) {
            super(scene, image);
        }

        @Override
        protected void notifyStateChanged(ObjectState previousState, ObjectState state) {
            Border BORDER_HOVERED = javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK);
            Border BORDER = BorderFactory.createEmptyBorder();
            if (previousState.isHovered() == state.isHovered()) {
                return;
            }
            setBorder(state.isHovered() ? BORDER_HOVERED : BORDER);
        }
    }

   }

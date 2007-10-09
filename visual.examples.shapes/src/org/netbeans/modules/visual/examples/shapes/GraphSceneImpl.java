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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.visual.examples.shapes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.netbeans.api.visual.action.AcceptProvider;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.action.ReconnectProvider;
import org.netbeans.api.visual.action.TextFieldInplaceEditor;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.model.ObjectScene;
import org.netbeans.api.visual.widget.ComponentWidget;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.general.IconNodeWidget;
import org.netbeans.api.visual.widget.ImageWidget;
import org.netbeans.modules.visual.examples.shapes.assistant.AssistantModel;
import org.netbeans.modules.visual.examples.shapes.assistant.ModelHelper;
import org.netbeans.modules.visual.examples.shapes.dataobject.MyItemData;
import org.netbeans.modules.visual.examples.shapes.palette.ShapeTopComponent;
import org.netbeans.modules.visual.examples.shapes.palette.Utils;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Utilities;

/**
 * Action which shows Shape component.
 */
public class GraphSceneImpl extends GraphScene.StringGraph implements ActionListener  {
    
    //These are the images that we will display:
    private static final Image SHAPE1 = Utilities.loadImage("org/netbeans/modules/visual/examples/shapes/resources/shape1-32.png"); // NOI18N
    private static final Image SHAPE2 = Utilities.loadImage("org/netbeans/modules/visual/examples/shapes/resources/shape2-32.png"); // NOI18N
    private static final Image SHAPE3 = Utilities.loadImage("org/netbeans/modules/visual/examples/shapes/resources/shape3-32.png"); // NOI18N
    private static final Image SHAPE4 = Utilities.loadImage("org/netbeans/modules/visual/examples/shapes/resources/shape4-32.png"); // NOI18N
    private static final Image ORANGE = Utilities.loadImage("org/netbeans/modules/visual/examples/shapes/resources/orange.png"); // NOI18N
    private static final Image GREEN = Utilities.loadImage("org/netbeans/modules/visual/examples/shapes/resources/green.png"); // NOI18N
    private static final Image BLUE = Utilities.loadImage("org/netbeans/modules/visual/examples/shapes/resources/blue.png"); // NOI18N
    private static final Image YELLOW = Utilities.loadImage("org/netbeans/modules/visual/examples/shapes/resources/yellow.png"); // NOI18N
    
    private Map displayers = new HashMap();
    
    private long nodeCounter = 0;
    private long edgeCounter = 0;
    private int pos = 0;
    
    //Here are our layers, to which the nodes will be added:
    private LayerWidget backgroundLayer = new LayerWidget(this);
    private LayerWidget mainLayer= new LayerWidget(this);
    private LayerWidget connectionLayer= new LayerWidget(this);
    private LayerWidget interactionLayer= new LayerWidget(this);
    
    //Here we define the actions that the widgets will be able to perform:
    private WidgetAction connectAction = ActionFactory.createConnectAction(interactionLayer, new SceneConnectProvider());
    private WidgetAction reconnectAction = ActionFactory.createReconnectAction(new SceneReconnectProvider());
    private WidgetAction popup = ActionFactory.createPopupMenuAction(new MyPopupProvider());
    private WidgetAction popupMenuAction = ActionFactory.createPopupMenuAction(new MyWidgetPopupProvider());
    private WidgetAction editorAction = ActionFactory.createInplaceEditorAction(new LabelTextFieldEditor());
    private WidgetAction moveAction;
    private WidgetAction showMapAction;
    private WidgetAction normalMoveAction = ActionFactory.createMoveAction();
    
    private static final String ACTION_CONNECT = "connect"; // NOI18N
    private static final String ACTION_SELECT = "selection"; // NOI18N
    private static final String ACTION_SHOW = "show";
    
    JComponent comp = this.createView();
    
    public AssistantModel model = ModelHelper.returnAssistantModel();
    
    ShapeTopComponent shapeTopComponent;
    
    private JComboBox colorChoices;
    
    private ActionListener listener;
    
    private String replacementNode;
    
    private boolean dropped = false;
    private boolean removed = false;
    
    private static final Hashtable<String,Image> mapping;
    private static final Hashtable<String,Image> coloring;
    
    private ImageWidget backgroundImageWidget;
    
    
    static {
        mapping = new Hashtable<String,Image>();
        mapping.put("Whirl",SHAPE1);
        mapping.put("Heart",SHAPE2);
        mapping.put("Cross",SHAPE3);
        mapping.put("Star",SHAPE4);
    }
    
    static {
        coloring = new Hashtable<String,Image>();
        coloring.put("Orange",ORANGE);
        coloring.put("Blue",BLUE);
        coloring.put("Yellow",YELLOW);
        coloring.put("Green",GREEN);
    }
    
    //Here the Constructor begins:
    public GraphSceneImpl() {
        
        // Set the default active tool and inform the user:
        setActiveTool(ACTION_SELECT);
        StatusDisplayer.getDefault().setStatusText("Selection mode");
        
        // Here we add our layers to the scene:
        addChild(backgroundLayer);
        addChild(mainLayer);
        addChild(connectionLayer);
        addChild(interactionLayer);
        
        //Here we add the color to the background:
        backgroundImageWidget= new ImageWidget(this);
        backgroundLayer.addChild(backgroundImageWidget);
        
        //Here we add the color background combo box, making it movable:
        colorChoices = new JComboBox(new String[] { "Orange", "Blue", "Yellow", "Green", "White"});
        colorChoices.setSelectedItem("White");
        colorChoices.addActionListener(this);
        mainLayer.addChild(createMoveableComponent(colorChoices));
        
        //After the layers are added to the scene,
        //we define the move action, which makes use of those layers:
        moveAction = ActionFactory.createAlignWithMoveAction(mainLayer, interactionLayer, null);
        
        // Here we set all the actions that we want to be available
        // on the scene:
        getActions().addAction(popup);
        getActions().addAction(ActionFactory.createZoomAction());
        getActions().addAction(ActionFactory.createPanAction());
        getActions().addAction(ActionFactory.createAcceptAction(new AcceptProvider() {
            
            public ConnectorState isAcceptable(Widget widget, Point point, Transferable transferable) {
                
                MyItemData data = null;
                
                try {
                    data = (MyItemData) transferable.getTransferData(Utils.MY_DATA_FLAVOR);
                } catch (UnsupportedFlavorException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                
                //If the current transferable is NOT a node yet (and has not been removed),
                //add drag image and return true, so that the "accept" method
                //can add the node:
                if (!isNode(data.getComment())) {
                    StatusDisplayer.getDefault().setStatusText(data.getComment() + " is" +
                            " not yet a node.");
                    Image dragImage = data.getBigImage();
                    Graphics2D g2 = (Graphics2D) comp.getGraphics();
                    Rectangle visRect = comp.getVisibleRect();
                    comp.paintImmediately(visRect.x, visRect.y, visRect.width, visRect.height);
                    g2.drawImage(dragImage,
                            AffineTransform.getTranslateInstance(point.getLocation().getX(),
                            point.getLocation().getY()),
                            null);
                    return ConnectorState.ACCEPT;
                    
                } else   {
                    StatusDisplayer.getDefault().setStatusText(data.getComment() + " is a node.");
                    return ConnectorState.REJECT_AND_STOP;
                }
                
            }
            
            public void accept(Widget widget, Point point, Transferable transferable) {
                
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                
                MyItemData data = null;
                try {
                    
                    data = (MyItemData) transferable.getTransferData(Utils.MY_DATA_FLAVOR);
                    String id = data.getComment();
                    
                    if (mapping.containsKey (id)) {
                        addNode(id).setPreferredLocation(widget.convertLocalToScene(point));
                    }
                    
                    repaint();
                    revalidate();
                    
                    model.setContext("dropped");
                    
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (UnsupportedFlavorException ex) {
                    ex.printStackTrace();
                }
                
            }
            
        }));
        
        model.setContext("started");
    }
    
    private Widget createShapeWidget(String label, Image image) {
        IconNodeWidget character = new IconNodeWidget(this);
        character.setImage(image);
        character.setLabel(label);
        mainLayer.addChild(character);
        character.getActions().addAction(createSelectAction());
        character.getActions().addAction(createObjectHoverAction());
        character.getActions().addAction(popupMenuAction);
        character.createActions(ACTION_SELECT).addAction(moveAction);
        character.createActions(ACTION_CONNECT).addAction(connectAction);
        character.getLabelWidget().getActions().addAction(editorAction);
        return character;
    }
    
    private Widget createColorWidget(String label, Image image) {
        IconNodeWidget character = new IconNodeWidget(this);
        character.setImage(image);
        backgroundLayer.addChild(character);
        return character;
    }
    
    
    //This method is triggered by addNode(). When a drop
    //is done, in the accept() method addNode() is invoked,
    //which triggers the following method:
    protected Widget attachNodeWidget(String node) {
        Image image = (Image) coloring.get(node);
        if (image != null) {
            return createColorWidget(node,image);
        }

        image = (Image) mapping.get(node);
        if (image != null) {
            return createShapeWidget(node,image);
        }

        throw new IllegalArgumentException(node);
    }
    
    protected Widget attachEdgeWidget(String edge) {
        ConnectionWidget connection = new ConnectionWidget(this);
        connection.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        connection.setEndPointShape(PointShape.SQUARE_FILLED_BIG);
        connection.getActions().addAction(createObjectHoverAction());
        connection.getActions().addAction(createSelectAction());
        connection.getActions().addAction(reconnectAction);
        connectionLayer.addChild(connection);
        return connection;
    }
    
    protected void attachEdgeSourceAnchor(String edge, String oldSourceNode, String sourceNode) {
        Widget w = sourceNode != null ? findWidget(sourceNode) : null;
        ((ConnectionWidget) findWidget(edge)).setSourceAnchor(AnchorFactory.createRectangularAnchor(w));
    }
    
    protected void attachEdgeTargetAnchor(String edge, String oldTargetNode, String targetNode) {
        Widget w = targetNode != null ? findWidget(targetNode) : null;
        ((ConnectionWidget) findWidget(edge)).setTargetAnchor(AnchorFactory.createRectangularAnchor(w));
    }
    
    private Widget createMoveableComponent(Component component) {
        Widget widget = new Widget(this);
        widget.setLayout(LayoutFactory.createVerticalLayout());
        widget.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        widget.getActions().addAction(normalMoveAction);
        
        LabelWidget label = new LabelWidget(this, "Color Choice:");
        label.setOpaque(true);
        label.setForeground(Color.WHITE);
        label.setBackground(Color.BLACK);
        widget.addChild(label);
        
        widget.addChild(new ComponentWidget(this, component));
        
        pos += 100;
        widget.setPreferredLocation(new Point(pos, pos));
        return widget;
    }
    
    public void actionPerformed(ActionEvent e) {
        colorChoices = (JComboBox) e.getSource();
        String newItem = (String) colorChoices.getSelectedItem();

        if (coloring.containsKey(newItem)  &&  ! isNode (newItem)) {
            for (String color : coloring.keySet()) {
                if (isNode (color))
                    removeNode (color);
            }
            addNode (newItem);
            model.setContext(newItem.toLowerCase());
            validate();
        }
    }
    
    private class SceneConnectProvider implements ConnectProvider {
        
        private String source = null;
        private String target = null;
        
        public boolean isSourceWidget(Widget sourceWidget) {
            Object object = findObject(sourceWidget);
            source = object != null  &&  mapping.containsKey(object) ? (String) object : null;
            return source != null;
        }
        
        public boolean hasCustomTargetWidgetResolver(Scene scene) {
            return false;
        }
        
        public Widget resolveTargetWidget(Scene scene, Point sceneLocation) {
            return null;
        }
        
        public void createConnection(Widget sourceWidget, Widget targetWidget) {
            String edge = "edge" + edgeCounter ++;
            addEdge(edge);
            setEdgeSource(edge, source);
            setEdgeTarget(edge, target);
        }
        
        public ConnectorState isTargetWidget(Widget sourceWidget, Widget targetWidget) {
            Object object = findObject(targetWidget);
            target = object != null  &&  mapping.containsKey(object) ? (String) object : null;
            if (target != null)
                return ! source.equals(target) ? ConnectorState.ACCEPT : ConnectorState.REJECT_AND_STOP;
            return object != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;
        }
    }
    
    private class SceneReconnectProvider implements ReconnectProvider {
        
        String edge;
        String originalNode;
        String replacementNode;
        
        public void reconnectingStarted(ConnectionWidget connectionWidget, boolean reconnectingSource) {
        }
        
        public void reconnectingFinished(ConnectionWidget connectionWidget, boolean reconnectingSource) {
        }
        
        public boolean isSourceReconnectable(ConnectionWidget connectionWidget) {
            Object object = findObject(connectionWidget);
            edge = isEdge(object) ? (String) object : null;
            originalNode = edge != null ? getEdgeSource(edge) : null;
            return originalNode != null;
        }
        
        public boolean isTargetReconnectable(ConnectionWidget connectionWidget) {
            Object object = findObject(connectionWidget);
            edge = isEdge(object) ? (String) object : null;
            originalNode = edge != null ? getEdgeTarget(edge) : null;
            return originalNode != null;
        }
        
        public boolean hasCustomReplacementWidgetResolver(Scene scene) {
            return false;
        }
        
        public Widget resolveReplacementWidget(Scene scene, Point sceneLocation) {
            return null;
        }
        
        public void reconnect(ConnectionWidget connectionWidget, Widget replacementWidget, boolean reconnectingSource) {
            if (replacementWidget == null)
                removeEdge(edge);
            else if (reconnectingSource)
                setEdgeSource(edge, replacementNode);
            else
                setEdgeTarget(edge, replacementNode);
        }
        
        public ConnectorState isReplacementWidget(ConnectionWidget connectionWidget, Widget replacementWidget, boolean b) {
            Object object = findObject(replacementWidget);
            replacementNode = object != null  &&  mapping.containsKey(object) ? (String) object : null;
            if (replacementNode != null)
                return ConnectorState.ACCEPT;
            return object != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;
        }
    }
    
    private class LabelTextFieldEditor implements TextFieldInplaceEditor {
        
        public boolean isEnabled(Widget widget) {
            return true;
        }
        
        public String getText(Widget widget) {
            return ((LabelWidget) widget).getLabel();
        }
        
        public void setText(Widget widget, String text) {
            ((LabelWidget) widget).setLabel(text);
        }
        
    }
    
    public final class MyWidgetPopupProvider implements PopupMenuProvider, ActionListener {
        
        Scene localScene = new Scene();
        
        private JPopupMenu menu;
        String target = null;
        ObjectScene myObjectScene;
        private String source = null;
        
        public MyWidgetPopupProvider() {
            menu = new JPopupMenu("Popup menu");
            JMenuItem item;
            
            item = new JMenuItem("Delete");
            item.addActionListener(this);
            menu.add(item);
        }
        
        public void actionPerformed(ActionEvent e) {
            StatusDisplayer.getDefault().setStatusText("Deleted!");
        }
        
        public JPopupMenu getPopupMenu(Widget widget, Point point) {
            
            Object o = findObject(widget);
            String target = (String) o;
            if (isNode(target)){
                removeNode(target);
            }
            return null;
        }
        
    }
    
    private final class MyPopupProvider implements PopupMenuProvider, ActionListener {
        
        private JPopupMenu menu;
        
        public MyPopupProvider() {
            
            menu = new JPopupMenu("Popup menu");
            JMenuItem item;
            
            item = new JMenuItem("Connection Mode");
            item.setActionCommand(ACTION_CONNECT);
            item.addActionListener(this);
            
            menu.add(item);
            
            item = new JMenuItem("Selection Mode");
            item.setActionCommand(ACTION_SELECT);
            item.addActionListener(this);
            menu.add(item);
        }
        
        public void actionPerformed(ActionEvent e) {
            
            setActiveTool(e.getActionCommand());
            if (e.getActionCommand().equals(ACTION_SELECT)){
                StatusDisplayer.getDefault().setStatusText("Selection mode");
                model.setContext("select");
            } else if (e.getActionCommand().equals(ACTION_CONNECT)){
                StatusDisplayer.getDefault().setStatusText("Connection mode");
                model.setContext("connect");
            }
        }
        
        public JPopupMenu getPopupMenu(Widget widget, Point point) {
            return menu;
        }
        
    }
    
}

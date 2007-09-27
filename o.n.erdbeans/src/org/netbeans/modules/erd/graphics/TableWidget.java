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

import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.model.StateModel;
import org.netbeans.api.visual.widget.*;
import org.openide.util.Utilities;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import org.netbeans.api.visual.vmd.VMDGlyphSetWidget;
import org.netbeans.api.visual.vmd.VMDNodeAnchor;
import org.netbeans.api.visual.widget.Scene.SceneListener;


public class TableWidget extends SwingScrollWidget implements StateModel.Listener {
    
    private static final Image IMAGE_EXPAND = Utilities.loadImage("org/netbeans/modules/visual/resources/vmd-expand.png"); // NOI18N
    private static final Image IMAGE_COLLAPSE = Utilities.loadImage("org/netbeans/modules/visual/resources/vmd-collapse.png"); // NOI18N
    
    private static final Border BORDER_NODE = new VMDNodeBorder();
    private static final Color BORDER_CATEGORY_BACKGROUND = new Color(0xCDDDF8);
    private static final Border BORDER_MINIMIZE = BorderFactory.createRoundedBorder(2, 2, null, VMDNodeBorder.COLOR_BORDER);
    static final Color COLOR_SELECTED = new Color(0x748CC0);
    static final Border BORDER = BorderFactory.createOpaqueBorder(2, 8, 2, 8);
    static final Border BORDER_HOVERED = BorderFactory.createLineBorder(2, 8, 2, 8, Color.BLACK);
    
    
    //private SwingScrollWodget
    private Widget header;
    private ImageWidget minimizeWidget;
    private ImageWidget imageWidget;
    private LabelWidget nameWidget;
    private LabelWidget typeWidget;
    private VMDGlyphSetWidget glyphSetWidget;
    
    private SeparatorWidget pinsSeparator;
    
    private HashMap<String, Widget> pinCategoryWidgets = new HashMap<String, Widget> ();
    private Font fontPinCategory = getScene().getFont().deriveFont(10.0f);
    
    private StateModel stateModel = new StateModel(2);
    private Anchor nodeAnchor = new VMDNodeAnchor(this);
    
    
    /**
     * Creates a node widget.
     * @param scene the scene
     */
    public TableWidget(Scene scene) {
        super(scene);
        
        
        setView(new MyView(scene));
        getView().setOpaque(false);
        getView().setBorder(BORDER_NODE);
        getView().setLayout(LayoutFactory.createVerticalLayout());
        //setMinimumBounds (new Rectangle (0, 0, 128, 0));
        
        header = new Widget(scene);
        header.setBorder(BORDER);
        header.setBackground(COLOR_SELECTED);
        header.setOpaque(false);
        header.setLayout(LayoutFactory.createHorizontalLayout(LayoutFactory.SerialAlignment.CENTER, 8));
        //addChild (header);
        getView().addChild(header);
        
        minimizeWidget = new ImageWidget(scene, IMAGE_COLLAPSE);
        minimizeWidget.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        minimizeWidget.setBorder(BORDER_MINIMIZE);
        minimizeWidget.getActions().addAction(new ToggleMinimizedAction());
        header.addChild(minimizeWidget);
        
        imageWidget = new ImageWidget(scene);
        header.addChild(imageWidget);
        
        nameWidget = new LabelWidget(scene);
        nameWidget.setFont(scene.getDefaultFont().deriveFont(Font.BOLD));
        header.addChild(nameWidget);
        
        typeWidget = new LabelWidget(scene);
        typeWidget.setForeground(Color.BLACK);
        header.addChild(typeWidget);
        
        glyphSetWidget = new VMDGlyphSetWidget(scene);
        header.addChild(glyphSetWidget);
        
        pinsSeparator = new SeparatorWidget(scene, SeparatorWidget.Orientation.HORIZONTAL);
        pinsSeparator.setForeground(BORDER_CATEGORY_BACKGROUND);
        //addChild (pinsSeparator);
        getView().addChild(pinsSeparator);
        //Widget topLayer = new Widget(scene);
        //addChild (topLayer);
        //view.addChild(topLayer);
        stateModel = new StateModel();
        stateModel.addListener(this);
        
        notifyStateChanged(ObjectState.createNormal(), ObjectState.createNormal());
    }
    
    
    
    
    /**
     * Check the minimized state.
     * @return true, if minimized
     */
    public boolean isMinimized() {
        return stateModel.getBooleanState();
    }
    
    /**
     * Set the minimized state. This method will show/hide child widgets of this Widget and switches anchors between
     * node and pin widgets.
     * @param minimized if true, then the widget is going to be minimized
     */
    public void setMinimized(boolean minimized) {
        stateModel.setBooleanState(minimized);
    }
    
    /**
     * Toggles the minimized state. This method will show/hide child widgets of this Widget and switches anchors between
     * node and pin widgets.
     */
    public void toggleMinimized() {
        stateModel.toggleBooleanState();
    }
    
    /**
     * Called when a minimized state is changed. This method will show/hide child widgets of this Widget and switches anchors between
     * node and pin widgets.
     */
    public void stateChanged() {
        boolean minimized = stateModel.getBooleanState();
        
        Rectangle rectangle = minimized ? new Rectangle() : null;
        for (Widget widget : getView().getChildren())
            if (widget != header  &&  widget != pinsSeparator)
                getScene().getSceneAnimator().animatePreferredBounds(widget, rectangle);
        minimizeWidget.setImage(minimized ? IMAGE_EXPAND : IMAGE_COLLAPSE);
        if(minimized){
            collapseWidget();
        } else
            expandWidget();
        
    }
    
    private Rectangle currentSize;
    public void collapseWidget(){
        
        
        //  verticalWidget.setPreferredBounds(new Rectangle());
        //  horizontalWidget.setPreferredBounds(new Rectangle());
        //currentSize=getPreferredBounds();
        //setPreferredBounds(new Rectangle());
        // addChild(header);
        
    }
    
    public void expandWidget(){
        // verticalWidget.setPreferredBounds(null);
        // horizontalWidget.setPreferredBounds(null);
        
    }
    
    /**
     * Called to notify about the change of the widget state.
     * @param previousState the previous state
     * @param state the new state
     */
    protected void notifyStateChanged(ObjectState previousState, ObjectState state) {
        if (! previousState.isSelected()  &&  state.isSelected()){
            collapseWidget();
            bringToFront();
            
        } else if (! previousState.isHovered()  &&  state.isHovered()){
            expandWidget();
            bringToFront();
            
        }
        
        
        header.setOpaque(state.isSelected());
        header.setBorder(state.isFocused() || state.isHovered() ? TableWidget.BORDER_HOVERED : TableWidget.BORDER);
    }
    
    /**
     * Sets a node image.
     * @param image the image
     */
    public void setNodeImage(Image image) {
        imageWidget.setImage(image);
        revalidate();
    }
    
    /**
     * Returns a node name.
     * @return the node name
     */
    public String getNodeName() {
        return nameWidget.getLabel();
    }
    
    /**
     * Sets a node name.
     * @param nodeName the node name
     */
    public void setNodeName(String nodeName) {
        nameWidget.setLabel(nodeName);
    }
    
    /**
     * Sets a node type (secondary name).
     * @param nodeType the node type
     */
    public void setNodeType(String nodeType) {
        typeWidget.setLabel(nodeType != null ? "[" + nodeType + "]" : null);
    }
    
    /**
     * Attaches a pin widget to the node widget.
     * @param widget the pin widget
     */
    public void attachPinWidget(Widget widget) {
        
        widget.setCheckClipping(true);
        getView().addChild(widget);
        if (stateModel.getBooleanState())
            widget.setPreferredBounds(new Rectangle());
    }
    
    /**
     * Sets node glyphs.
     * @param glyphs the list of images
     */
    public void setGlyphs(List<Image> glyphs) {
        glyphSetWidget.setGlyphs(glyphs);
    }
    
    /**
     * Sets all node properties at once.
     * @param image the node image
     * @param nodeName the node name
     * @param nodeType the node type (secondary name)
     * @param glyphs the node glyphs
     */
    public void setNodeProperties(Image image, String nodeName, String nodeType, List<Image> glyphs) {
        setNodeImage(image);
        setNodeName(nodeName);
        setNodeType(nodeType);
        setGlyphs(glyphs);
    }
    
    /**
     * Returns a node name widget.
     * @return the node name widget
     */
    public LabelWidget getNodeNameWidget() {
        return nameWidget;
    }
    
    /**
     * Returns a node anchor.
     * @return the node anchor
     */
    public Anchor getNodeAnchor() {
        return nodeAnchor;
    }
    
    /**
     * Creates an extended pin anchor with an ability of reconnecting to the node anchor when the node is minimized.
     * @param anchor the original pin anchor from which the extended anchor is created
     * @return the extended pin anchor
     */
    public Anchor createAnchorPin(Anchor anchor) {
        return AnchorFactory.createProxyAnchor(stateModel, anchor, nodeAnchor);
    }
    
    /**
     * Returns a list of pin widgets attached to the node.
     * @return the list of pin widgets
     */
    private List<Widget> getColumnWidgets() {
        ArrayList<Widget> pins = new ArrayList<Widget> (getView().getChildren());
        pins.remove(header);
        pins.remove(pinsSeparator);
        return pins;
    }
    
    
    
   /* @Override
    public void paintWidget(){
     if(doItOnce){
            sort();
            doItOnce=false;
        }    
        
    }*/
    
    public void sort(){
        List<Widget> columns=getColumnWidgets();
        LinkedList sortedColumns=new LinkedList();
        for(Widget column:columns){
            ColumnWidget columnWidget=(ColumnWidget)column;
            
            ColumnWidget.CONSTRAINT_TYPE columnType=columnWidget.getColumnType();
            switch(columnType){
                case PRIMARY_KEY :
                    
                    sortedColumns.addFirst(column);
                    break;
                case FP_KEY:
                    sortedColumns.addFirst(column);
                    break;
                case FOREIGN_KEY:
                    sortedColumns.add(column);
                    break;
                case ORDINARY:
                    sortedColumns.addLast(column);
                    
            }
        }
        
        getView().removeChildren(columns);
        getView().addChildren(sortedColumns);
    }
    
    
    /**
     * Returns a header widget.
     * @return the header widget
     */
    public Widget getHeader() {
        return header;
    }
    
    private final class ToggleMinimizedAction extends WidgetAction.Adapter {
        
        public State mousePressed(Widget widget, WidgetMouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1 || event.getButton() == MouseEvent.BUTTON2) {
                stateModel.toggleBooleanState();
                return State.CONSUMED;
            }
            return State.REJECTED;
        }
    }
    
    
    private class MyView extends Widget implements SceneListener{
        private boolean doItOnce=true;
        public MyView(Scene scene){
            super(scene);
        }
        
        public void sceneRepaint (){
            
        }
        
        public void sceneValidating (){
            if(doItOnce){
              sort();
              doItOnce=!doItOnce;
            }
        }

        
        public void sceneValidated (){
            
        }
        
        
    }
    
    
    
}
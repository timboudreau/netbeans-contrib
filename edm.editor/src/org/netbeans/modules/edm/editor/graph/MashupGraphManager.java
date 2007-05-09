/*
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License (the License). You may not use this
 * file except in compliance with the License.  You can obtain a copy of the
 *  License at http://www.netbeans.org/cddl.html
 *
 * When distributing Covered Code, include this CDDL Header Notice in each
 * file and include the License. If applicable, add the following below the
 * CDDL Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2006 Sun Microsystems, Inc. All Rights Reserved
 *
 */

package org.netbeans.modules.edm.editor.graph;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.Rectangle;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.widget.Widget;

import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;
import org.netbeans.modules.edm.editor.graph.actions.RuntimeModelPopupProvider;
import org.netbeans.modules.edm.editor.graph.actions.TablePopupProvider;
import org.netbeans.modules.edm.editor.widgets.EDMNodeWidget;
import org.netbeans.modules.edm.editor.widgets.EDMPinWidget;
import org.netbeans.modules.edm.editor.widgets.EDMGraphScene;
import org.netbeans.modules.edm.editor.graph.actions.SceneAcceptProvider;
import org.netbeans.modules.edm.editor.graph.actions.ScenePopupProvider;
import org.netbeans.modules.edm.editor.graph.components.DebugPanel;
import org.netbeans.modules.edm.editor.graph.components.MashupTopPanel;
import org.netbeans.modules.edm.editor.utils.MashupGraphUtil;
import org.netbeans.modules.edm.editor.graph.actions.JoinPopupProvider;

import org.netbeans.modules.sql.framework.model.SQLConstants;
import org.netbeans.modules.sql.framework.model.SQLDBColumn;
import org.netbeans.modules.sql.framework.model.SQLDBTable;
import org.netbeans.modules.sql.framework.model.SQLDefinition;
import org.netbeans.modules.sql.framework.model.SQLInputObject;
import org.netbeans.modules.sql.framework.model.SQLJoinOperator;
import org.netbeans.modules.sql.framework.model.SQLJoinTable;
import org.netbeans.modules.sql.framework.model.SQLJoinView;
import org.netbeans.modules.sql.framework.model.SQLObject;
import org.netbeans.modules.sql.framework.model.RuntimeDatabaseModel;
import org.netbeans.modules.sql.framework.model.RuntimeInput;
import com.sun.sql.framework.utils.RuntimeAttribute;
import org.netbeans.modules.sql.framework.model.SQLCondition;
import org.netbeans.modules.sql.framework.model.SourceTable;

/**
 *
 * @author karthikeyan s
 */
public class MashupGraphManager {
    
    private MashupDataObject mObj;
    
    private EDMGraphScene scene;
    
    private DebugPanel output;
    
    private MashupTopPanel panel;
    
    private JScrollPane pane;
    
    private long edgeCounter = 1;
    
    private long nodeCounter = 1;
    
    private long pinCounter = 1;
    
    private Map<String, Widget> sqlIdtoWidgetMap = new HashMap<String, Widget>();
    
    private Map<String, String> edgeMap = new HashMap<String, String>();
    
    private List<Widget> widgets = new ArrayList<Widget>();
    
    public MashupGraphManager() {
        scene = new EDMGraphScene();
        
        output = new DebugPanel();
        panel = new MashupTopPanel();
        
        scene.getActions().addAction(ActionFactory.createZoomAction());
        scene.getActions().addAction(ActionFactory.createPanAction());
        scene.getActions().addAction(ActionFactory.createMoveAction());
    }
    
    /**
     * Creates a Mashup graph scene.
     * @param dObj MashupDataObject
     */
    public MashupGraphManager(MashupDataObject dObj) {
        this();
        mObj = dObj;
        mObj.setMashupGraphManager(this);
        scene.getActions().addAction(ActionFactory.createAcceptAction(
                new SceneAcceptProvider(mObj, this)));
        scene.getActions().addAction(ActionFactory.createPopupMenuAction(
                new ScenePopupProvider(mObj, scene)));
    }
    
    public void refreshGraph() {
        generateGraph(this.mObj.getModel().getSQLDefinition());
        scene.layoutScene();
    }
    
    public JComponent getSatelliteView() {
        return scene.createSatelliteView();
    }
    
    public EDMGraphScene getScene() {
        return this.scene;
    }
    
    public void fitToPage() {        
        Rectangle rectangle = new Rectangle(0, 0, 1, 1);
        for (Widget widget : scene.getChildren())
            rectangle = rectangle.union(widget.convertLocalToScene(widget.getBounds()));
        Dimension dim = rectangle.getSize();
        Dimension viewDim = pane.getViewportBorderBounds().getSize();
        scene.getSceneAnimator().animateZoomFactor(
                Math.min((float) viewDim.width / dim.width, 
                (float) viewDim.height / dim.height));
        scene.revalidate();
    }
    
    public void expandAll() {
        Iterator<Widget> it = widgets.iterator();
        while(it.hasNext()) {
            Widget wd = it.next();
            if(wd instanceof EDMNodeWidget) {
                ((EDMNodeWidget)wd).expandWidget();
                wd.revalidate();
            }
        }
    }
    
    public void collapseAll() {
        Iterator<Widget> it = widgets.iterator();
        while(it.hasNext()) {
            Widget wd = it.next();
            if(wd instanceof EDMNodeWidget) {
                ((EDMNodeWidget)wd).collapseWidget();
                wd.revalidate();
            }
        }        
    }   
    
    public void zoomGraph(double zoomFactor) {
        scene.getSceneAnimator().animateZoomFactor(zoomFactor);
        scene.revalidate();
    }
    
    public void zoomIn() {
        scene.getSceneAnimator().animateZoomFactor(scene.getZoomFactor() * 1.1);
        scene.revalidate();
    }
    
    public void zoomOut() {
        scene.getSceneAnimator().animateZoomFactor(scene.getZoomFactor() * 0.9);
        scene.revalidate();
    }    
    
    public void generateGraph(SQLDefinition sqlDefinition) {
        removeAllChildren();
        try {
            SQLJoinView[] joinViews = (SQLJoinView[])sqlDefinition.getObjectsOfType(
                    SQLConstants.JOIN_VIEW).toArray(new SQLJoinView[0]);
            if(joinViews != null && joinViews.length != 0) {
                SQLJoinView joinView = joinViews[0];
                SQLJoinOperator joinOperator = joinView.getRootJoin();
                String join = createGraphNode(joinOperator);
                recursivelyAddNodes(joinOperator, join);
                
                // Add tables which are not a part of the join.
                SQLDBTable[] dbTables = (SQLDBTable[]) sqlDefinition.
                        getJoinSources().toArray(new SQLDBTable[0]);
                for(SQLDBTable dbTable : dbTables) {
                    createGraphNode(dbTable);
                }
            } else {
                SQLDBTable[] dbTables = (SQLDBTable[]) sqlDefinition.getSourceTables().
                        toArray(new SQLDBTable[0]);
                for(SQLDBTable dbTable : dbTables) {
                    createGraphNode(dbTable);
                }
            }
            RuntimeDatabaseModel rtModel = sqlDefinition.getRuntimeDbModel();
            if(rtModel != null) {
                RuntimeInput rtInput = rtModel.getRuntimeInput();
                if(rtInput != null) {
                    if(rtInput.getRuntimeAttributeMap().size() != 0) {
                        createGraphNode(rtInput);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void setDataObject(MashupDataObject mashupDataObject) {
        this.mObj = mashupDataObject;
        generateGraph(mObj.getModel().getSQLDefinition());
        scene.validate();
        scene.layoutScene();
    }
    
    public JComponent getTopPanel() {
        JComponent comp = scene.getView();
        comp = (comp == null) ? scene.createView() : scene.getView();
        pane = new JScrollPane(comp);
        panel.setTopComponent(pane);
        panel.setBottomComponent(output);
        return panel;
    }
    
    public void setLog(String text) {
        output.setLog(text);
    }
    
    public void showOutput(SQLObject object, SQLDefinition sqlDefn) {
        output.showOutput(object, sqlDefn);
    }
    
    public void updateColumnSelection(SQLDBTable table) {
        EDMNodeWidget widget = (EDMNodeWidget) sqlIdtoWidgetMap.get(table.getId());
        
        List<Widget> usedCol = new ArrayList<Widget>();
        List<Widget> unusedCol = new ArrayList<Widget>();
        HashMap<String, List<Widget>> categories = new HashMap<String, List<Widget>>();
        
        SQLDBColumn[] columns = (SQLDBColumn[]) table.getColumnList().
                toArray(new SQLDBColumn[0]);
        for(SQLDBColumn column : columns) {
            EDMPinWidget pin = (EDMPinWidget)sqlIdtoWidgetMap.get(column.getId());
            if(column.isVisible()) {
                usedCol.add(pin);
            } else {
                unusedCol.add(pin);
            }
        }
        
        if(usedCol.size() != 0) {
            categories.put("Used Columns", usedCol);
        }
        if(unusedCol.size() != 0) {
            categories.put("Unused Columns", unusedCol);
        }
        widget.sortPins(categories);
        widget.revalidate();
    }
    
    private void createGraphEdge(String sourcePinID, String targetNodeID) {
        String edgeID = "edge" + this.edgeCounter ++;
        Widget widget = scene.addEdge(edgeID);
        widgets.add(widget);
        scene.setEdgeSource(edgeID, sourcePinID + EDMGraphScene.PIN_ID_DEFAULT_SUFFIX);
        scene.setEdgeTarget(edgeID, targetNodeID + EDMGraphScene.PIN_ID_DEFAULT_SUFFIX);
        edgeMap.put(edgeID, sourcePinID + "#" + targetNodeID);
        scene.validate();
    }
    
    private String createGraphNode(SQLObject model) {
        String nodeID = model.getId() + "#" + this.nodeCounter ++;
        EDMNodeWidget widget = (EDMNodeWidget)scene.addNode(nodeID);
        widgets.add(widget);
        scene.validate();
        if(model instanceof SQLJoinOperator) {
            SQLJoinOperator joinOp = (SQLJoinOperator) model;
            String nodeName = "";
            if(joinOp.isRoot()) {
                nodeName = "ROOT JOIN";
            } else {
                nodeName = "JOIN";
            }
            String joinType = "";
            switch(joinOp.getJoinType()) {
            case SQLConstants.INNER_JOIN:
                joinType += "INNER JOIN";
                break;
            case SQLConstants.RIGHT_OUTER_JOIN:
                joinType +=  "RIGHT OUTER JOIN";
                break;
            case SQLConstants.LEFT_OUTER_JOIN:
                joinType += "LEFT OUTER JOIN";
                break;
            case SQLConstants.FULL_OUTER_JOIN:
                joinType += "FULL OUTER JOIN";
            }
            widget.setNodeName(nodeName);
            
            EDMPinWidget joinTypePin = ((EDMPinWidget) scene.addPin(nodeID, "nodeID" + "#pin" + pinCounter++));
            scene.validate();
            joinTypePin.setPinName(joinType);
            List<Image> typeImage = new ArrayList<Image>();
            typeImage.add(MashupGraphUtil.getPropertiesImage());
            joinTypePin.setGlyphs(typeImage);
            widgets.add(joinTypePin);
            SQLCondition cond = joinOp.getJoinCondition();
            String condition = "";
            if(cond != null) {
                condition = cond.getConditionText();
                if(condition == null) {
                    condition = "";
                }
            }
            condition = condition.equals("") ? "NULL" : condition;
            EDMPinWidget conditionPin = ((EDMPinWidget) scene.addPin(nodeID, "nodeID" + "#pin" + pinCounter++));
            scene.validate();
            conditionPin.setPinName("CONDITION");
            conditionPin.setToolTipText("Join Condition: " + condition);
            List<Image> image = new ArrayList<Image>();
            image.add(MashupGraphUtil.getConditionImage());
            conditionPin.setGlyphs(image);
            widgets.add(conditionPin);
            
            // add popup for join widget.
            widget.getHeader().getActions().addAction(
                    ActionFactory.createPopupMenuAction(new JoinPopupProvider(joinOp, mObj)));
            sqlIdtoWidgetMap.put(joinOp.getId(), widget);
        } else if(model instanceof RuntimeInput) {
            widget.setNodeName("Runtime Input");
            RuntimeInput rtInput = (RuntimeInput)model;
            Iterator it = rtInput.getRuntimeAttributeMap().keySet().iterator();
            while(it.hasNext()) {
                String name = (String)it.next();
                EDMPinWidget columnPin = ((EDMPinWidget)
                        scene.addPin(nodeID, "nodeID" + "#pin" + pinCounter++));
                scene.validate();
                columnPin.setPinName(name);
                List<Image> image = new ArrayList<Image>();
                image.add(MashupGraphUtil.getRuntimeAttributeImage());
                columnPin.setGlyphs(image);
                RuntimeAttribute rtAttr = (RuntimeAttribute) rtInput.getRuntimeAttributeMap().get(name);
                columnPin.setToolTipText("Value: " + rtAttr.getAttributeValue());
                widgets.add(columnPin);
            }
            
            // add popup for runtime inputs.
            widget.getHeader().getActions().addAction(
                    ActionFactory.createPopupMenuAction(new RuntimeModelPopupProvider(rtInput, mObj)));
            sqlIdtoWidgetMap.put(rtInput.getId(), widget);
        } else if (model instanceof SQLDBTable) {
            SQLDBTable tbl = (SQLDBTable) model;
            String tooltip = "URL: " + tbl.getParent().getConnectionDefinition().getConnectionURL();
            widget.setNodeName(model.getDisplayName());
            widget.getHeader().getActions().addAction(
                    ActionFactory.createPopupMenuAction(new TablePopupProvider(tbl, mObj)));
            
            String condition = ((SourceTable)tbl).getExtractionCondition().getConditionText();
            if(condition != null && !condition.equals("")) {
                List<Image> image = new ArrayList<Image>();
                image.add(MashupGraphUtil.getFilterImage());
                widget.setGlyphs(image);
                tooltip = tooltip + ";  Extraction Condition: " + condition;
            }
            
            // now add columns.
            SQLDBColumn[] columns = (SQLDBColumn[]) tbl.getColumnList().toArray(new SQLDBColumn[0]);
            List<Widget> usedCol = new ArrayList<Widget>();
            List<Widget> unusedCol = new ArrayList<Widget>();
            HashMap<String, List<Widget>> categories = new HashMap<String, List<Widget>>();
            for(SQLDBColumn column : columns) {
                String pinTooltip = "Scale: " + column.getScale() +
                        ";  Precision: " + column.getPrecision() +
                        ";  Type: " + column.getJdbcTypeString();
                EDMPinWidget columnPin = ((EDMPinWidget)
                        scene.addPin(nodeID, "nodeID" + "#pin" + pinCounter++));
                columnPin.setPinName(column.getDisplayName());
                List<Image> image = new ArrayList<Image>();
                if(column.isVisible()) {
                    if(column.isPrimaryKey()) {
                        image.add(MashupGraphUtil.getPrimaryKeyColumnImage());
                        pinTooltip = pinTooltip + "; PRIMARY KEY ";
                    } else if (column.isForeignKey()) {
                        image.add(MashupGraphUtil.getForeignKeyColumnImage());
                        image.add(MashupGraphUtil.getForeignKeyImage());
                        pinTooltip = pinTooltip + "; FOREIGN KEY ";
                    } else {
                        image.add(MashupGraphUtil.getColumnImage());
                    }
                    usedCol.add(columnPin);
                } else {
                    image.add(MashupGraphUtil.getColumnImage());
                    unusedCol.add(columnPin);
                }
                columnPin.setGlyphs(image);
                columnPin.setToolTipText(pinTooltip);
                widgets.add(columnPin);
                sqlIdtoWidgetMap.put(column.getId(), columnPin);
            }
            widget.setToolTipText(tooltip);
            
            if(usedCol.size() != 0) {
                categories.put("Used Columns", usedCol);
            }
            if(unusedCol.size() != 0) {
                categories.put("Unused Columns", unusedCol);
            }
            widget.sortPins(categories);
            
            sqlIdtoWidgetMap.put(tbl.getId(), widget);
        } else if (model instanceof SQLJoinTable) {
            SQLJoinTable joinTbl = (SQLJoinTable) model;
            String tooltip = "URL: " + joinTbl.getSourceTable().
                    getParent().getConnectionDefinition().getConnectionURL();
            widget.setNodeName(joinTbl.getSourceTable().getDisplayName());
            widget.getHeader().getActions().addAction(
                    ActionFactory.createPopupMenuAction(new TablePopupProvider(
                    joinTbl.getSourceTable(), mObj)));
            
            String condition = joinTbl.getSourceTable().getExtractionCondition().getConditionText();
            if(condition != null && !condition.equals("")) {
                List<Image> image = new ArrayList<Image>();
                image.add(MashupGraphUtil.getFilterImage());
                widget.setGlyphs(image);
                tooltip = tooltip + ";  Extraction Condition: " + condition;
            }
            
            // now add columns.
            SQLDBColumn[] columns = (SQLDBColumn[]) joinTbl.getSourceTable().
                    getColumnList().toArray(new SQLDBColumn[0]);
            List<Widget> usedCol = new ArrayList<Widget>();
            List<Widget> unusedCol = new ArrayList<Widget>();
            HashMap<String, List<Widget>> categories = new HashMap<String, List<Widget>>();
            for(SQLDBColumn column : columns) {
                String pinTooltip = "Scale: " + column.getScale() +
                        ";  Precision: " + column.getPrecision() +
                        ";  Type: " + column.getJdbcTypeString();
                EDMPinWidget columnPin = ((EDMPinWidget)
                        scene.addPin(nodeID, "nodeID" + "#pin" + pinCounter++));
                columnPin.setPinName(column.getDisplayName());
                List<Image> image = new ArrayList<Image>();
                if(column.isVisible()) {
                    if(column.isPrimaryKey()) {
                        image.add(MashupGraphUtil.getPrimaryKeyColumnImage());
                        pinTooltip = pinTooltip + "; PRIMARY KEY ";
                    } else if (column.isForeignKey()) {
                        image.add(MashupGraphUtil.getForeignKeyColumnImage());
                        image.add(MashupGraphUtil.getForeignKeyImage());
                        pinTooltip = pinTooltip + "; FOREIGN KEY ";
                    } else {
                        image.add(MashupGraphUtil.getColumnImage());
                    }
                    usedCol.add(columnPin);
                } else {
                    image.add(MashupGraphUtil.getColumnImage());
                    unusedCol.add(columnPin);
                }
                columnPin.setGlyphs(image);
                columnPin.setToolTipText(pinTooltip);
                widgets.add(columnPin);
                sqlIdtoWidgetMap.put(column.getId(), columnPin);
            }
            widget.setToolTipText(tooltip);
            if(usedCol.size() != 0) {
                categories.put("Used Columns", usedCol);
            }
            if(unusedCol.size() != 0) {
                categories.put("Unused Columns", unusedCol);
            }
            widget.sortPins(categories);
            sqlIdtoWidgetMap.put(joinTbl.getSourceTable().getId(), widget);
        }
        widget.getActions().addAction(scene.createWidgetHoverAction());
        widget.setNodeImage(MashupGraphUtil.getImageForObject(model.getObjectType()));
        scene.addPin(nodeID, nodeID + EDMGraphScene.PIN_ID_DEFAULT_SUFFIX);
        return nodeID;
    }
    
    private void recursivelyAddNodes(SQLJoinOperator rootJoin, String join) {
        SQLInputObject leftIn = rootJoin.getInput(SQLJoinOperator.LEFT);
        SQLInputObject rightIn = rootJoin.getInput(SQLJoinOperator.RIGHT);
        
        // left side traversal
        while(true) {
            String left = createGraphNode(leftIn.getSQLObject());
            createGraphEdge(left, join);
            if(leftIn.getSQLObject().getObjectType() == SQLConstants.JOIN_TABLE) {
                break;
            }
            recursivelyAddNodes((SQLJoinOperator)leftIn.getSQLObject(), left);
            break;
        }
        
        // right side traversal
        while(true) {
            String right = createGraphNode(rightIn.getSQLObject());
            createGraphEdge(right, join);
            if(rightIn.getSQLObject().getObjectType() == SQLConstants.JOIN_TABLE) {
                break;
            }
            recursivelyAddNodes((SQLJoinOperator)rightIn.getSQLObject(), right);
            break;
        }
    }
    
    private void removeAllChildren() {
        Iterator it = widgets.iterator();
        while(it.hasNext()) {
            Widget wd = (Widget) it.next();
            wd.removeFromParent();
            scene.validate();
        }
        
        sqlIdtoWidgetMap.clear();
        edgeMap.clear();
        widgets.clear();
    }
}
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
import java.awt.Point;
import java.util.WeakHashMap;
import java.util.LinkedHashMap;
import java.awt.Dialog;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.widget.Widget;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.windows.WindowManager;

import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;
import org.netbeans.modules.edm.editor.graph.actions.RuntimeModelPopupProvider;
import org.netbeans.modules.edm.editor.graph.actions.TablePopupProvider;
import org.netbeans.modules.edm.editor.widgets.EDMNodeWidget;
import org.netbeans.modules.edm.editor.widgets.EDMPinWidget;
import org.netbeans.modules.edm.editor.widgets.EDMGraphScene;
import org.netbeans.modules.edm.editor.graph.actions.SceneAcceptProvider;
import org.netbeans.modules.edm.editor.graph.actions.ScenePopupProvider;
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
import java.util.List;
import java.util.List;
import java.util.List;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.modules.edm.editor.graph.actions.GroupByPopupProvider;
import org.netbeans.modules.edm.editor.graph.components.EDMOutputTopComponent;
import org.netbeans.modules.edm.editor.graph.components.TableChooserPanel;
import org.netbeans.modules.edm.editor.utils.ImageConstants;
import org.netbeans.modules.edm.editor.widgets.property.GroupByNode;
import org.netbeans.modules.edm.editor.widgets.property.JoinNode;
import org.netbeans.modules.edm.editor.widgets.property.TableNode;
import org.netbeans.modules.edm.editor.widgets.property.editor.ColumnSelectionEditor;
import org.netbeans.modules.sql.framework.model.SQLCondition;
import org.netbeans.modules.sql.framework.model.SQLGroupBy;
import org.netbeans.modules.sql.framework.model.SourceTable;
import org.netbeans.modules.sql.framework.model.impl.SQLGroupByImpl;
import org.netbeans.modules.sql.framework.ui.utils.UIUtil;
import org.netbeans.modules.sql.framework.model.DBColumn;
import org.netbeans.modules.sql.framework.model.DBTable;

/**
 *
 * @author karthikeyan s
 */
public class MashupGraphManager {
    
    private MashupDataObject mObj;
    
    private EDMGraphScene scene;
    
    private JScrollPane pane;
    
    private JComponent satelliteView;
    
    private long edgeCounter = 1;
    
    private long nodeCounter = 1;
    
    private long pinCounter = 1;
    
    private Map<SQLObject, Widget> sqltoWidgetMap = new HashMap<SQLObject, Widget>();
    
    private WeakHashMap<Widget, SQLObject> widgetToObjectMap =
            new WeakHashMap<Widget, SQLObject>();
    
    private Map<String, String> edgeMap = new HashMap<String, String>();
    
    private List<Widget> widgets = new ArrayList<Widget>();
    
    private WidgetAction columnSelectionEditor;
    
    public MashupGraphManager() {
        scene = new EDMGraphScene();
        pane = new JScrollPane();

        JComponent view = scene.createView();
        pane.setViewportView(view);
        satelliteView = scene.createSatelliteView();

        scene.getActions().addAction(ActionFactory.createZoomAction());
        scene.getActions().addAction(ActionFactory.createPanAction());
        scene.getActions().addAction(ActionFactory.createMoveAction());

        // vlv: print
        view.putClientProperty(java.awt.print.Printable.class, ""); // NOI18N
    }
    
    /**
     * Creates a Mashup graph scene.
     * @param dObj MashupDataObject
     */
    public MashupGraphManager(MashupDataObject dObj) {
        this();
        mObj = dObj;
        scene.getActions().addAction(ActionFactory.createAcceptAction(
                new SceneAcceptProvider(mObj, this)));
        scene.getActions().addAction(ActionFactory.createPopupMenuAction(
                new ScenePopupProvider(mObj, this)));
        columnSelectionEditor = ActionFactory.createInplaceEditorAction(
                new ColumnSelectionEditor(mObj));
    }
    
    public void refreshGraph() {
        generateGraph(this.mObj.getModel().getSQLDefinition());
        scene.validate();
        if(this.mObj.getModel().getSQLDefinition().getObjectsOfType(
                SQLConstants.JOIN_VIEW).size() == 0) {
            scene.layoutScene(true);
        } else {
            scene.layoutScene(false);
        }
    }
    
    public JComponent getSatelliteView() throws Exception {
        return satelliteView;
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
        scene.validate();
    }
    
    public void fitToWidth(){
        Rectangle rectangle = new Rectangle(0, 0, 1, 1);
        for (Widget widget : scene.getChildren())
            rectangle = rectangle.union(widget.convertLocalToScene(widget.getBounds()));
        Dimension dim = rectangle.getSize();
        Dimension viewDim = pane.getViewportBorderBounds().getSize();
        scene.getSceneAnimator().animateZoomFactor(
                (float) viewDim.width / dim.width);
        scene.validate();
    }
    
    public void fitToHeight(){
        Rectangle rectangle = new Rectangle(0, 0, 1, 1);
        for (Widget widget : scene.getChildren())
            rectangle = rectangle.union(widget.convertLocalToScene(widget.getBounds()));
        Dimension dim = rectangle.getSize();
        Dimension viewDim = pane.getViewportBorderBounds().getSize();
        scene.getSceneAnimator().animateZoomFactor(
                (float) viewDim.height / dim.height);
        scene.validate();
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
        scene.validate();
    }
    
    public void zoomIn() {
        scene.getSceneAnimator().animateZoomFactor(scene.getZoomFactor() * 1.1);
        scene.validate();
    }
    
    public void zoomOut() {
        scene.getSceneAnimator().animateZoomFactor(scene.getZoomFactor() * 0.9);
        scene.validate();
    }
    
    public boolean addGroupby(Point point) {
        boolean status = false;
        try {
            SQLGroupBy groupby = new SQLGroupByImpl();
            SQLJoinView[] joinViews = getJoinViews();
            if(joinViews.length != 0) {
               List<DBColumn> columns = new ArrayList<DBColumn>();
                SQLDBTable[] tables = (SQLDBTable[]) joinViews[0].getSourceTables().
                        toArray(new SQLDBTable[0]);
                for(SQLDBTable table : tables) {
                    columns.addAll(table.getColumnList());
                }
                groupby.setColumns(columns);
                joinViews[0].setSQLGroupBy(groupby);
                groupby.setParentObject(joinViews[0]);
                status = true;
            } else {
                DialogDescriptor dlgDesc = null;
                              List<SQLDBTable> srcTbls = new ArrayList<SQLDBTable>();
                for(DBTable tbl : mObj.getModel().getSQLDefinition().getSourceTables()) {
                    srcTbls.add((SQLDBTable)tbl);
                }
                TableChooserPanel panel = new TableChooserPanel(srcTbls);
                dlgDesc = new DialogDescriptor(panel, "Select Table", true,
                        NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.OK_OPTION,
                        DialogDescriptor.DEFAULT_ALIGN, null, null);
                Dialog dlg = DialogDisplayer.getDefault().createDialog(dlgDesc);
                dlg.setVisible(true);
                if (NotifyDescriptor.OK_OPTION == dlgDesc.getValue()) {
                    SQLDBTable table = panel.getSelectedTable();
                    if(table == null) {
                        NotifyDescriptor d =
                                new NotifyDescriptor.Message("Group by discarded.",
                                NotifyDescriptor.WARNING_MESSAGE);
                        DialogDisplayer.getDefault().notify(d);
                    } else {
                        groupby.setColumns(table.getColumnList());
                        groupby.setParentObject(table);
                        ((SourceTable)table).setSQLGroupBy(groupby);
                        status = true;
                    }
                }
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return status;
    }
    
    public void generateGraph(SQLDefinition sqlDefinition) {
        UIUtil.startProgressDialog(mObj.getName(), "Generating graph...");
        removeAllChildren();
        try {
            SQLJoinView[] joinViews = getJoinViews();
            if(joinViews != null && joinViews.length != 0) {
                addJoinsAndTables(sqlDefinition, joinViews[0]);
            } else {
                addTablesOnly(sqlDefinition);
            }
            
            // Add runtime models
            addRuntimeModel(sqlDefinition);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        UIUtil.stopProgressDialog();
    }
    
    public void setDataObject(MashupDataObject mashupDataObject) {
        this.mObj = mashupDataObject;
    }
    
    public JScrollPane getPanel() {
        if(pane == null) {
            pane = new JScrollPane();
            pane.setViewportView(scene.createView());
        }
        return pane;
    }
    
    public void setLog(String text) {
        EDMOutputTopComponent win = EDMOutputTopComponent.findInstance();
        win.setLog(text);
    }
    
    public void showOutput(SQLObject object, SQLDefinition sqlDefn) {
        EDMOutputTopComponent win = EDMOutputTopComponent.findInstance();
        win.generateOutput(object, sqlDefn);
        if(!win.isOpened()) {
            win.open();
        }
        win.setVisible(true);
    }
    
    public void showSql(SQLObject object) {
        EDMOutputTopComponent win = EDMOutputTopComponent.findInstance();
        win.showSql(object, mObj);
        if(!win.isOpened()) {
            win.open();
        }
        win.setVisible(true);
    }
    
    public void updateColumnSelection(SQLDBTable table) {
        EDMNodeWidget widget = (EDMNodeWidget) sqltoWidgetMap.get(table);
        List<Widget> usedCol = new ArrayList<Widget>();
        List<Widget> unusedCol = new ArrayList<Widget>();
        HashMap<String, List<Widget>> categories = new HashMap<String, List<Widget>>();
        SQLDBColumn[] columns = (SQLDBColumn[])table.getColumnList().
                toArray(new SQLDBColumn[0]);
        for(SQLDBColumn column : columns) {
            Widget[] children = widget.getChildren().toArray(new Widget[0]);
            EDMPinWidget pin = null;
            for(Widget child : children) {
                if(child instanceof EDMPinWidget &&
                        ((EDMPinWidget)child).getPinName().equals(column.getDisplayName())) {
                    pin = (EDMPinWidget)child;
                    break;
                }
            }
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
    
    public void setSelectedNode(Widget wd) {
        SQLObject obj = widgetToObjectMap.get(wd);
        if(obj != null) {
            if(obj instanceof SQLJoinOperator) {
                WindowManager.getDefault().getRegistry().getActivated().
                        setActivatedNodes(new Node[]{new JoinNode((SQLJoinOperator)obj, mObj)});
            } else if (obj instanceof SQLDBTable) {
                WindowManager.getDefault().getRegistry().getActivated().
                        setActivatedNodes(new Node[]{new TableNode((SourceTable)obj)});
            } else if (obj instanceof SQLJoinTable) {
                SQLJoinTable joinTbl = (SQLJoinTable)obj;
                WindowManager.getDefault().getRegistry().getActivated().
                        setActivatedNodes(new Node[]{new TableNode(joinTbl.getSourceTable())});
            } else if(obj instanceof SQLGroupByImpl) {
                SQLGroupByImpl grpby = (SQLGroupByImpl)obj;
                WindowManager.getDefault().getRegistry().getActivated().
                        setActivatedNodes(new Node[]{new GroupByNode(grpby, mObj)});
            }
        }
    }
    
    public SQLObject mapWidgetToObject(Widget widget) {
        return widgetToObjectMap.get(widget);
    }
    
    public void validateScene() {
        scene.validate();
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
        widgetToObjectMap.put(widget, model);
        scene.validate();
        widget.setNodeImage(MashupGraphUtil.getImageForObject(model.getObjectType()));
        scene.validate();
        if(model instanceof SQLJoinOperator) {
            addJoinOperatorNode((SQLJoinOperator)model, widget, nodeID);
        } else if(model instanceof RuntimeInput) {
            addRuntimeNode((RuntimeInput)model, widget, nodeID);
        } else if (model instanceof SQLDBTable) {
            addTableNode((SQLDBTable)model, widget, nodeID);
        } else if (model instanceof SQLJoinTable) {
            addTableNode((SQLDBTable)((SQLJoinTable)model).getSourceTable(),
                    widget, nodeID);
        } else if(model instanceof SQLGroupByImpl) {
            addGroupbyNode((SQLGroupByImpl)model, widget, nodeID);
            widget.setNodeImage(MashupGraphUtil.getImage(ImageConstants.GROUPBY));
            scene.validate();
        }
        widget.getActions().addAction(scene.createWidgetHoverAction());
        scene.addPin(nodeID, nodeID + EDMGraphScene.PIN_ID_DEFAULT_SUFFIX);
        scene.validate();
        return nodeID;
    }
    
    private void recursivelyAddNodes(SQLJoinOperator rootJoin, String join) {
        SQLInputObject leftIn = rootJoin.getInput(SQLJoinOperator.LEFT);
        SQLInputObject rightIn = rootJoin.getInput(SQLJoinOperator.RIGHT);
        
        // left side traversal
        while(true) {
            String left = createGraphNode(leftIn.getSQLObject());
            
            // check for groupby operator.
            if(leftIn.getSQLObject().getObjectType() == SQLConstants.JOIN_TABLE) {
                SQLGroupBy groupby = ((SQLJoinTable)leftIn.getSQLObject()).
                        getSourceTable().getSQLGroupBy();
                if(groupby != null) {
                    String grpbyNode = createGraphNode((SQLGroupByImpl)groupby);
                    createGraphEdge(left, grpbyNode);
                    left = grpbyNode;
                }
            }
            
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
            
            // check for groupby operator.
            if(rightIn.getSQLObject().getObjectType() == SQLConstants.JOIN_TABLE) {
                SQLGroupBy groupby = ((SQLJoinTable)rightIn.getSQLObject()).
                        getSourceTable().getSQLGroupBy();
                if(groupby != null) {
                    String grpbyNode = createGraphNode((SQLGroupByImpl)groupby);
                    createGraphEdge(right, grpbyNode);
                    right = grpbyNode;
                }
            }
            createGraphEdge(right, join);
            if(rightIn.getSQLObject().getObjectType() == SQLConstants.JOIN_TABLE) {
                break;
            }
            recursivelyAddNodes((SQLJoinOperator)rightIn.getSQLObject(), right);
            break;
        }
    }
    
    
    private void addJoinOperatorNode(SQLJoinOperator joinOp, EDMNodeWidget widget, String nodeID) {
        sqltoWidgetMap.put(joinOp, widget);
        String nodeName = "";
        if(joinOp.isRoot()) {
            nodeName = "ROOT JOIN";
        } else {
            nodeName = "JOIN";
        }
        String joinType = "<html><table border=0 cellspacing=0 cellpadding=4>" +
                "<tr><td><b>Join Type</b></td><td>";
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
        joinType += "</td></tr></table></html>";
        widget.setNodeName(nodeName);
        
        EDMPinWidget joinTypePin = ((EDMPinWidget) scene.addPin(
                nodeID, "nodeID" + "#pin" + pinCounter++));
        scene.validate();
        joinTypePin.setPinName("JOIN TYPE");
        joinTypePin.setToolTipText(joinType);
        scene.validate();
        List<Image> typeImage = new ArrayList<Image>();
        typeImage.add(MashupGraphUtil.getImage(ImageConstants.PROPERTIES));
        joinTypePin.setGlyphs(typeImage);
        scene.validate();
        widgets.add(joinTypePin);
        SQLCondition cond = joinOp.getJoinCondition();
        String condition = "";
        if(cond != null) {
            condition = cond.getConditionText();
            if(condition == null) {
                condition = "";
            }
        }
        condition = condition.equals("") ? "<NO CONDITION DEFINED>" : condition;
        EDMPinWidget conditionPin = ((EDMPinWidget) scene.addPin(
                nodeID, "nodeID" + "#pin" + pinCounter++));
        scene.validate();
        conditionPin.setPinName("CONDITION");
        conditionPin.setToolTipText("<html> <table border=0 cellspacing=0 cellpadding=4>" +
                "<tr><td><b>Join Condition</b></td><td>" +
                condition + "</td></tr></table></html>");
        List<Image> image = new ArrayList<Image>();
        image.add(MashupGraphUtil.getImage(ImageConstants.CONDITION));
        conditionPin.setGlyphs(image);
        scene.validate();
        widgets.add(conditionPin);
     
        // add popup for join widget.
        widget.getActions().addAction(
                ActionFactory.createPopupMenuAction(new JoinPopupProvider(
                joinOp, mObj)));
        scene.validate();
    }
    
    private void addRuntimeNode(RuntimeInput rtInput, EDMNodeWidget widget,
            String nodeID) {
        sqltoWidgetMap.put(rtInput, widget);
        widget.setNodeName("Runtime Input");
        Iterator it = rtInput.getRuntimeAttributeMap().keySet().iterator();
        while(it.hasNext()) {
            String name = (String)it.next();
            EDMPinWidget columnPin = ((EDMPinWidget)
                    scene.addPin(nodeID, "nodeID" + "#pin" + pinCounter++));
            scene.validate();
            columnPin.setPinName(name);
            List<Image> image = new ArrayList<Image>();
            image.add(MashupGraphUtil.getImage(ImageConstants.RUNTIMEATTR));
            columnPin.setGlyphs(image);
            scene.validate();
            RuntimeAttribute rtAttr = (RuntimeAttribute) rtInput.
                    getRuntimeAttributeMap().get(name);
            columnPin.setToolTipText("<html><table border=0 cellspacing=0 cellpadding=4 >" +
                    "<tr><td><b>Value</b></td><td>" + rtAttr.getAttributeValue() +
                    "</td></tr></table></html>");
            widgets.add(columnPin);
        }
        
        // add popup for runtime inputs.
        widget.getActions().addAction(
                ActionFactory.createPopupMenuAction(
                new RuntimeModelPopupProvider(rtInput, mObj)));
        scene.validate();
    }
    
    private void addTableNode(SQLDBTable tbl, EDMNodeWidget widget, String nodeID) {
        sqltoWidgetMap.put(tbl, widget);
        String tooltip = "<html><table border=0 cellspacing=0 cellpadding=4><tr><td><b>URL</b></td>" +
                "<td>" + tbl.getParent().getConnectionDefinition().getConnectionURL() +
                "</td></tr><tr><td><b>Database</b></td><td>"+ tbl.getParent().getConnectionDefinition().getDBType() +
                "</td></tr>";
        widget.setNodeName(tbl.getDisplayName());
        scene.validate();
        widget.getActions().addAction(
                ActionFactory.createPopupMenuAction(new TablePopupProvider(
                tbl, mObj)));
        scene.validate();
        String condition = ((SourceTable)tbl).getExtractionCondition().getConditionText();
        if(condition != null && !condition.equals("")) {
            List<Image> image = new ArrayList<Image>();
            image.add(MashupGraphUtil.getImage(ImageConstants.FILTER));
            widget.setGlyphs(image);
            scene.validate();
            tooltip = tooltip + "<tr><td><b>Extraction Condition</b></td><td>" +
                    condition + "</td></tr></table></html>";
        }
        
        // now add columns.
        SQLDBColumn[] columns = (SQLDBColumn[]) tbl.getColumnList().
                toArray(new SQLDBColumn[0]);
        List<Widget> usedCol = new ArrayList<Widget>();
        List<Widget> unusedCol = new ArrayList<Widget>();
        Map<String, List<Widget>> categories = new LinkedHashMap<String, List<Widget>>();
        for(SQLDBColumn column : columns) {
            String pinTooltip = "<html> <table border=0 cellspacing=0 cellpadding=4><tr><td>" +
                    "<b>Scale</b></td><td>" + column.getScale() + "</td></tr>" +
                    "<tr><td><b>Precision</b></td><td>" + column.getPrecision() +
                    "</td></tr><tr><td><b>Type</b></td><td>" +
                    column.getJdbcTypeString() + "</td></tr>";
            EDMPinWidget columnPin = ((EDMPinWidget)
                    scene.addPin(nodeID, "nodeID" + "#pin" + pinCounter++));
            scene.validate();
            columnPin.setPinName(column.getDisplayName());
            columnPin.getActions().addAction(columnSelectionEditor);
            scene.validate();
            List<Image> image = new ArrayList<Image>();
            if(column.isVisible()) {
                if(column.isPrimaryKey()) {
                    image.add(MashupGraphUtil.getImage(ImageConstants.PRIMARYKEYCOL));
                    pinTooltip = pinTooltip + "<tr><td colspan=2><b>PRIMARY KEY</b></td></tr>";
                } else if (column.isForeignKey()) {
                    image.add(MashupGraphUtil.getImage(ImageConstants.FOREIGNKEYCOL));
                    image.add(MashupGraphUtil.getImage(ImageConstants.FOREIGNKEY));
                    pinTooltip = pinTooltip + "<tr><td colspan=2><b>FOREIGN KEY</b></td></tr>";
                } else {
                    image.add(MashupGraphUtil.getImage(ImageConstants.COLUMN));
                }
                usedCol.add(columnPin);
                
            } else {
                image.add(MashupGraphUtil.getImage(ImageConstants.COLUMN));
                unusedCol.add(columnPin);
                
            }
            pinTooltip = pinTooltip + "</table></html>";
            columnPin.setGlyphs(image);
            scene.validate();
            columnPin.setToolTipText(pinTooltip);
            scene.validate();
            widgets.add(columnPin);
            sqltoWidgetMap.put(column, columnPin);
            widgetToObjectMap.put(columnPin, column);
        }
        widget.setToolTipText(tooltip);
        if(usedCol.size() != 0) {
            categories.put("Used Columns", usedCol);
        }
        if(unusedCol.size() != 0) {
            categories.put("Unused Columns", unusedCol);
        }
        widget.sortPins(categories);
    }
    
    private void addGroupbyNode(SQLGroupByImpl groupby,
            EDMNodeWidget widget, String nodeID) {
        sqltoWidgetMap.put(groupby, widget);
        widget.setNodeName("Group By");
        SQLCondition condition = groupby.getHavingCondition();
        String conditionText = "<NO CONDITION>";
        if(condition != null) {
            conditionText = condition.getConditionText();
        }
        EDMPinWidget havingPin = ((EDMPinWidget)
                scene.addPin(nodeID, "nodeID" + "#pin" + pinCounter++));
        List<Image> image = new ArrayList<Image>();
        image.add(MashupGraphUtil.getImage(ImageConstants.FILTER));
        havingPin.setGlyphs(image);
        havingPin.setPinName("HAVING CLAUSE");
        havingPin.setToolTipText(conditionText);
        scene.validate();
        widget.getActions().addAction(
                ActionFactory.createPopupMenuAction(new GroupByPopupProvider(groupby, mObj)));
        widgets.add(havingPin);
    }
    
    private void addJoinsAndTables(SQLDefinition sqlDefinition, SQLJoinView joinView) {
        SQLJoinOperator joinOperator = joinView.getRootJoin();
        String join = createGraphNode(joinOperator);
        recursivelyAddNodes(joinOperator, join);
        
        // Add tables which are not a part of the join.
        SQLDBTable[] dbTables = (SQLDBTable[]) sqlDefinition.
                getJoinSources().toArray(new SQLDBTable[0]);
        for(SQLDBTable dbTable : dbTables) {
            createGraphNode(dbTable);
        }
        
        // Add groupby operator.
        SQLGroupBy grpBy = joinView.getSQLGroupBy();
        if(grpBy != null) {
            String grpbyId = createGraphNode((SQLGroupByImpl)grpBy);
            createGraphEdge(join, grpbyId);
        }
    }
    
    private void addTablesOnly(SQLDefinition sqlDefinition) {
        SQLDBTable[] dbTables = (SQLDBTable[]) sqlDefinition.getSourceTables().
                toArray(new SQLDBTable[0]);
        for(SQLDBTable dbTable : dbTables) {
            String nodeId = createGraphNode(dbTable);
            SQLGroupBy groupBy = ((SourceTable)dbTable).getSQLGroupBy();
            if(groupBy != null) {
                String grpbyId = createGraphNode((SQLGroupByImpl)groupBy);
                createGraphEdge(nodeId, grpbyId);
            }
        }
    }
    
    private void addRuntimeModel(SQLDefinition sqlDefinition) {
        RuntimeDatabaseModel rtModel = sqlDefinition.getRuntimeDbModel();
        if(rtModel != null) {
            RuntimeInput rtInput = rtModel.getRuntimeInput();
            if(rtInput != null) {
                if(rtInput.getRuntimeAttributeMap().size() != 0) {
                    createGraphNode(rtInput);
                }
            }
        }
    }
    
    private SQLJoinView[] getJoinViews() {
        SQLJoinView[] joinViews = (SQLJoinView[])mObj.getModel().getSQLDefinition()
                .getObjectsOfType(SQLConstants.JOIN_VIEW).toArray(new SQLJoinView[0]);
        return joinViews;
    }
    
    private void removeAllChildren() {
        Iterator it = widgets.iterator();
        while(it.hasNext()) {
            Widget wd = (Widget) it.next();
            wd.removeFromParent();
            scene.validate();
        }
        
        // clear all data structures.
        sqltoWidgetMap.clear();
        edgeMap.clear();
        widgets.clear();
        widgetToObjectMap.clear();
    }
    private Object image;
    private Object pin;
    private Object sqlIdtoWidgetMap;
    private Object srcCols;
}

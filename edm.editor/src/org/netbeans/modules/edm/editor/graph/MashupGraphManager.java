/*
 * The contents of this file are subject to the terms of the Common
 * Development
The contents of this file are subject to the terms of either the GNU
General Public License Version 2 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://www.netbeans.org/cddl-gplv2.html
or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License file at
nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
particular file as subject to the "Classpath" exception as provided
by Sun in the GPL Version 2 section of the License file that
accompanied this code. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

Contributor(s):
 *
 * Copyright 2006 Sun Microsystems, Inc. All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
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

import java.awt.event.MouseEvent;
import java.util.Collection;

import java.util.Collection;

import java.util.List;
import java.util.List;
import java.util.List;
import java.util.logging.Logger;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.modules.edm.editor.graph.actions.GroupByPopupProvider;
import org.netbeans.modules.edm.editor.graph.components.EDMOutputTopComponent;
import org.netbeans.modules.edm.editor.graph.components.TableChooserPanel;
import org.netbeans.modules.edm.editor.utils.ImageConstants;
import org.netbeans.modules.edm.editor.widgets.property.GroupByNode;
import org.netbeans.modules.edm.editor.widgets.property.JoinNode;

import org.netbeans.modules.edm.editor.widgets.property.PropertyNode;
import org.netbeans.modules.edm.editor.widgets.property.SourceTableNode;
import org.netbeans.modules.edm.editor.widgets.property.TargetTableNode;

import org.netbeans.modules.edm.editor.widgets.property.SourceTableNode;
import org.netbeans.modules.edm.editor.widgets.property.TargetTableNode;

import org.netbeans.modules.edm.editor.widgets.property.editor.ColumnSelectionEditor;
import org.netbeans.modules.sql.framework.model.SQLCondition;
import org.netbeans.modules.sql.framework.model.SQLGroupBy;
import org.netbeans.modules.sql.framework.model.SourceTable;
import org.netbeans.modules.sql.framework.model.TargetTable;
import org.netbeans.modules.sql.framework.model.impl.SQLGroupByImpl;
import org.netbeans.modules.sql.framework.ui.utils.UIUtil;

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
    private WeakHashMap<Widget, SQLObject> widgetToObjectMap = new WeakHashMap<Widget, SQLObject>();
    private Map<String, String> edgeMap = new HashMap<String, String>();
    private List<Widget> widgets = new ArrayList<Widget>();
    private WidgetAction columnSelectionEditor;
    
    public MashupGraphManager() {
        scene = new EDMGraphScene();
        pane = new JScrollPane();
        pane.setViewportView(scene.createView());
        satelliteView = scene.createSatelliteView();
        
        scene.getActions().addAction(ActionFactory.createZoomAction());
        scene.getActions().addAction(ActionFactory.createPanAction());
        scene.getActions().addAction(ActionFactory.createMoveAction());
    }
    
    /**
     * Creates a Mashup graph scene.
     * @param dObj MashupDataObject
     */
    @SuppressWarnings("unchecked")
    public MashupGraphManager(MashupDataObject dObj) {
        this();
        mObj = dObj;
        scene.getActions().addAction(ActionFactory.createAcceptAction(new SceneAcceptProvider(mObj, this)));
        scene.getActions().addAction(ActionFactory.createPopupMenuAction(new ScenePopupProvider(mObj, this)));
        scene.getActions().addAction(new MouseStateAction());
        columnSelectionEditor = ActionFactory.createInplaceEditorAction(new ColumnSelectionEditor(mObj));
    }
    
    public void refreshGraph() {
        generateGraph(this.mObj.getModel().getSQLDefinition());
        scene.validate();
        if (this.mObj.getModel().getSQLDefinition().getObjectsOfType(SQLConstants.JOIN_VIEW).size() == 0) {
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
        for (Widget widget : scene.getChildren()) {
            rectangle = rectangle.union(widget.convertLocalToScene(widget.getBounds()));
        }
        Dimension dim = rectangle.getSize();
        Dimension viewDim = pane.getViewportBorderBounds().getSize();
        scene.getSceneAnimator().animateZoomFactor(Math.min((float) viewDim.width / dim.width, (float) viewDim.height / dim.height));
        scene.validate();
    }
    
    public void fitToWidth() {
        Rectangle rectangle = new Rectangle(0, 0, 1, 1);
        for (Widget widget : scene.getChildren()) {
            rectangle = rectangle.union(widget.convertLocalToScene(widget.getBounds()));
        }
        Dimension dim = rectangle.getSize();
        Dimension viewDim = pane.getViewportBorderBounds().getSize();
        scene.getSceneAnimator().animateZoomFactor((float) viewDim.width / dim.width);
        scene.validate();
    }
    
    public void fitToHeight() {
        Rectangle rectangle = new Rectangle(0, 0, 1, 1);
        for (Widget widget : scene.getChildren()) {
            rectangle = rectangle.union(widget.convertLocalToScene(widget.getBounds()));
        }
        Dimension dim = rectangle.getSize();
        Dimension viewDim = pane.getViewportBorderBounds().getSize();
        scene.getSceneAnimator().animateZoomFactor((float) viewDim.height / dim.height);
        scene.validate();
    }
    
    public void expandAll() {
        Iterator<Widget> it = widgets.iterator();
        while (it.hasNext()) {
            Widget wd = it.next();
            if (wd instanceof EDMNodeWidget) {
                ((EDMNodeWidget) wd).expandWidget();
                wd.revalidate();
            }
        }
    }
    
    public void collapseAll() {
        Iterator<Widget> it = widgets.iterator();
        while (it.hasNext()) {
            Widget wd = it.next();
            if (wd instanceof EDMNodeWidget) {
                ((EDMNodeWidget) wd).collapseWidget();
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
            if (joinViews.length != 0) {
                List<SQLDBColumn> columns = new ArrayList<SQLDBColumn>();
                SQLDBTable[] tables = (SQLDBTable[]) joinViews[0].getSourceTables().
                        toArray(new SQLDBTable[0]);
                for (SQLDBTable table : tables) {
                    columns.addAll(table.getColumnList());
                }
                groupby.setColumns(columns);
                joinViews[0].setSQLGroupBy(groupby);
                groupby.setParentObject(joinViews[0]);
                status = true;
            } else {
                DialogDescriptor dlgDesc = null;
                TableChooserPanel panel = new TableChooserPanel(mObj.getModel().getSQLDefinition().getSourceTables());
                dlgDesc = new DialogDescriptor(panel, "Select Table", true, NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.OK_OPTION, DialogDescriptor.DEFAULT_ALIGN, null, null);
                Dialog dlg = DialogDisplayer.getDefault().createDialog(dlgDesc);
                dlg.setVisible(true);
                if (NotifyDescriptor.OK_OPTION == dlgDesc.getValue()) {
                    SQLDBTable table = panel.getSelectedTable();
                    if (table == null) {
                        NotifyDescriptor d = new NotifyDescriptor.Message("Group by discarded.", NotifyDescriptor.WARNING_MESSAGE);
                        DialogDisplayer.getDefault().notify(d);
                    } else {
                        groupby.setColumns(table.getColumnList());
                        groupby.setParentObject(table);
                        ((SourceTable) table).setSQLGroupBy(groupby);
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
            if (joinViews != null && joinViews.length != 0) {
                /*for(SQLJoinView joinView : joinViews )
                addJoinsAndTables(sqlDefinition, joinView);*/
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
        if (pane == null) {
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
        if (!win.isOpened()) {
            win.open();
        }
        win.setVisible(true);
    }
    
    public void showSql(SQLObject object) {
        EDMOutputTopComponent win = EDMOutputTopComponent.findInstance();
        win.showSql(object, mObj);
        if (!win.isOpened()) {
            win.open();
        }
        win.setVisible(true);
    }
    
    public void updateColumnSelection(SQLDBTable table) {
        EDMNodeWidget widget = (EDMNodeWidget) sqltoWidgetMap.get(table);
        List<Widget> usedCol = new ArrayList<Widget>();
        List<Widget> unusedCol = new ArrayList<Widget>();
        HashMap<String, List<Widget>> categories = new HashMap<String, List<Widget>>();
        SQLDBColumn[] columns = (SQLDBColumn[]) table.getColumnList().
                toArray(new SQLDBColumn[0]);
        for (SQLDBColumn column : columns) {
            Widget[] children = widget.getChildren().toArray(new Widget[0]);
            EDMPinWidget pin = null;
            for (Widget child : children) {
                if (child instanceof EDMPinWidget && ((EDMPinWidget) child).getPinName().equals(column.getDisplayName())) {
                    pin = (EDMPinWidget) child;
                    break;
                }
            }
            if (column.isVisible()) {
                usedCol.add(pin);
            } else {
                unusedCol.add(pin);
            }
        }
        if (usedCol.size() != 0) {
            categories.put("Used Columns", usedCol);
        }
        if (unusedCol.size() != 0) {
            categories.put("Unused Columns", unusedCol);
        }
        widget.sortPins(categories);
        widget.revalidate();
    }
    
    public void setSelectedNode(Widget wd) {
        SQLObject obj = widgetToObjectMap.get(wd);         
        if (obj != null) {
            if (obj instanceof SQLJoinOperator) {
                WindowManager.getDefault().getRegistry().getActivated().setActivatedNodes(new Node[]{new JoinNode((SQLJoinOperator) obj, mObj)});
            } else if (obj instanceof SourceTable) {
                WindowManager.getDefault().getRegistry().getActivated().setActivatedNodes(new Node[]{new SourceTableNode((SourceTable) obj)});
            } else if (obj instanceof TargetTable) {
                WindowManager.getDefault().getRegistry().getActivated().setActivatedNodes(new Node[]{new TargetTableNode((TargetTable) obj, mObj)});
            } else if (obj instanceof SQLJoinTable) {
                SQLJoinTable joinTbl = (SQLJoinTable) obj;
                WindowManager.getDefault().getRegistry().getActivated().setActivatedNodes(new Node[]{new SourceTableNode(joinTbl.getSourceTable())});
            } else if (obj instanceof SQLGroupByImpl) {
                SQLGroupByImpl grpby = (SQLGroupByImpl) obj;
                WindowManager.getDefault().getRegistry().getActivated().setActivatedNodes(new Node[]{new GroupByNode(grpby, mObj)});
            }
        } else {
            WindowManager.getDefault().getRegistry().getActivated().setActivatedNodes(new Node[]{new PropertyNode(mObj)});
        }
        wd = null;
    }
    
    public SQLObject mapWidgetToObject(Widget widget) {
        return widgetToObjectMap.get(widget);
    }
    
    public void validateScene() {
        scene.validate();
    }
    

    public EDMGraphScene getScene() {
        return scene;
    }

    public void createPinEdge(String sourcePinID, String targetPinID) {
        /*String edgeID = "edge" + this.edgeCounter++;
        Widget widget = scene.addEdge(edgeID);
        widgets.add(widget);
        scene.setEdgeSource(edgeID, sourcePinID);
        scene.setEdgeTarget(edgeID, targetPinID);
        edgeMap.put(edgeID, sourcePinID + "#" + targetPinID);
        scene.validate();*/

        //Logger.getLogger(MashupGraphManager.class.getName()).info("getPinNode -- " + scene.getPinNode(targetPinID));
        Collection nodes = scene.getNodes();
        Iterator it2 = nodes.iterator();
        while (it2.hasNext()) {
            //Logger.getLogger(MashupGraphManager.class.getName()).info("nodes " + it2.next().toString());
        }
        Collection pins = scene.getPins();
        Iterator it1 = pins.iterator();
        while (it1.hasNext()) {
            //Logger.getLogger(MashupGraphManager.class.getName()).info("Pins "+it1.next().toString());
        }

        Collection edges = scene.getEdges();
        Iterator it = edges.iterator();
        //Logger.getLogger(MashupGraphManager.class.getName()).info("edges.size() " + edges.size());
        scene.validate();
        //Logger.getLogger(MashupGraphManager.class.getName()).info("getPinNode --- "+scene.getPinNode(pinId));
        /* scene.setEdgeSource(pinId, sourcePinID);
        scene.setEdgeTarget(pinId, targetPinID);
        Logger.getLogger(MashupGraphManager.class.getName()).info("sourcePinID _________ " + sourcePinID + " targetNodeID => " + targetPinID + " pinId -->" + pinId);
        pinMap.put(pinId, sourcePinID + "#" + targetPinID);
        scene.validate(); */
    }
  

    public void createGraphEdge(String sourcePinID, String targetNodeID) {
        String edgeID = "edge" + this.edgeCounter++;        
        Widget widget = scene.addEdge(edgeID);
        widgets.add(widget);
        scene.setEdgeSource(edgeID, sourcePinID + EDMGraphScene.PIN_ID_DEFAULT_SUFFIX);
        scene.setEdgeTarget(edgeID, targetNodeID + EDMGraphScene.PIN_ID_DEFAULT_SUFFIX);
        edgeMap.put(edgeID, sourcePinID + "#" + targetNodeID);
        scene.validate();
    }
    
    private String createGraphNode(SQLObject model) {
        String nodeID = model.getId() + "#" + this.nodeCounter++;
        EDMNodeWidget widget = (EDMNodeWidget) scene.addNode(nodeID);
        widgets.add(widget);
        widgetToObjectMap.put(widget, model);
        scene.validate();
        widget.setNodeImage(MashupGraphUtil.getImageForObject(model.getObjectType()));
        scene.validate();
        if (model instanceof SQLJoinOperator) {
            addJoinOperatorNode((SQLJoinOperator) model, widget, nodeID);
        } else if (model instanceof RuntimeInput) {
            addRuntimeNode((RuntimeInput) model, widget, nodeID);
        } else if (model instanceof SourceTable) {
            addTableNode((SQLDBTable) model, widget, nodeID);
        } else if (model instanceof TargetTable) {
            addTargetTableNode((SQLDBTable) model, widget, nodeID);
        } else if (model instanceof SQLJoinTable) {
            addTableNode((SQLDBTable) ((SQLJoinTable)model).getSourceTable(), widget, nodeID);
        } else if (model instanceof SQLGroupByImpl) {
            addGroupbyNode((SQLGroupByImpl) model, widget, nodeID);
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
        while (true) {
            String left = createGraphNode(leftIn.getSQLObject());
            
            // check for groupby operator.
            if (leftIn.getSQLObject().getObjectType() == SQLConstants.JOIN_TABLE) {
                SQLGroupBy groupby = ((SQLJoinTable) leftIn.getSQLObject()).getSourceTable().getSQLGroupBy();
                if (groupby != null) {
                    String grpbyNode = createGraphNode((SQLGroupByImpl) groupby);
                    createGraphEdge(left, grpbyNode);
                    left = grpbyNode;
                }
            }
            
            createGraphEdge(left, join);
            if (leftIn.getSQLObject().getObjectType() == SQLConstants.JOIN_TABLE) {
                break;
            }
            recursivelyAddNodes((SQLJoinOperator) leftIn.getSQLObject(), left);
            break;
        }
        
        // right side traversal
        while (true) {
            String right = createGraphNode(rightIn.getSQLObject());
            
            // check for groupby operator.
            if (rightIn.getSQLObject().getObjectType() == SQLConstants.JOIN_TABLE) {
                SQLGroupBy groupby = ((SQLJoinTable) rightIn.getSQLObject()).getSourceTable().getSQLGroupBy();
                if (groupby != null) {
                    String grpbyNode = createGraphNode((SQLGroupByImpl) groupby);
                    createGraphEdge(right, grpbyNode);
                    right = grpbyNode;
                }
            }
            createGraphEdge(right, join);
            if (rightIn.getSQLObject().getObjectType() == SQLConstants.JOIN_TABLE) {
                break;
            }
            recursivelyAddNodes((SQLJoinOperator) rightIn.getSQLObject(), right);
            break;
        }
    }
    
    private void addJoinOperatorNode(SQLJoinOperator joinOp, EDMNodeWidget widget, String nodeID) {
        sqltoWidgetMap.put(joinOp, widget);
        String nodeName = "";
        if (joinOp.isRoot()) {
            nodeName = "ROOT JOIN";
        } else {
            nodeName = "JOIN";
        }
        String joinType = "<html><table border=0 cellspacing=0 cellpadding=4>" + "<tr><td><b>Join Type</b></td><td>";
        switch (joinOp.getJoinType()) {
        case SQLConstants.INNER_JOIN:
            joinType += "INNER JOIN";
            break;
        case SQLConstants.RIGHT_OUTER_JOIN:
                joinType += "RIGHT OUTER JOIN";
            break;
        case SQLConstants.LEFT_OUTER_JOIN:
            joinType += "LEFT OUTER JOIN";
            break;
        case SQLConstants.FULL_OUTER_JOIN:
            joinType += "FULL OUTER JOIN";
        }
        joinType += "</td></tr></table></html>";
        widget.setNodeName(nodeName);
        
        EDMPinWidget joinTypePin = (EDMPinWidget) scene.addPin(
                nodeID, "nodeID" + "#pin" + pinCounter++);
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
        if (cond != null) {
            condition = cond.getConditionText();
            if (condition == null) {
                condition = "";
            }
        }
        condition = condition.equals("") ? "<NO CONDITION DEFINED>" : condition;
        EDMPinWidget conditionPin = (EDMPinWidget) scene.addPin(
                nodeID, "nodeID" + "#pin" + pinCounter++);
        scene.validate();
        conditionPin.setPinName("CONDITION");
        conditionPin.setToolTipText("<html> <table border=0 cellspacing=0 cellpadding=4>" + "<tr><td><b>Join Condition</b></td><td>" + condition + "</td></tr></table></html>");
        List<Image> image = new ArrayList<Image>();
        image.add(MashupGraphUtil.getImage(ImageConstants.CONDITION));
        conditionPin.setGlyphs(image);
        scene.validate();
        widgets.add(conditionPin);
     
        //add source and target columns
        /*SQLDBTable[] tbl = (SQLDBTable[]) joinOp.getAllSourceTables()
                .toArray(new SQLDBTable[0]);
        List<Widget> srcCols1 = new ArrayList<Widget>();
        HashMap<String, List<Widget>> categories = new HashMap<String, List<Widget>>();
        for (SQLDBTable table : tbl) {
            SQLDBColumn[] columns = (SQLDBColumn[]) table.getColumnList().
                    toArray(new SQLDBColumn[0]);
            List<Widget> srcCols = new ArrayList<Widget>();
            for (SQLDBColumn column : columns) {
                String pinTooltip = "<html> <table border=0 cellspacing=0 cellpadding=4><tr><td>" + "<b>Scale</b></td><td>" + column.getScale() + "</td></tr>" + "<tr><td><b>Precision</b></td><td>" + column.getPrecision() + "</td></tr><tr><td><b>Type</b></td><td>" + column.getJdbcTypeString() + "</td></tr>";
                EDMPinWidget srcColumnPin = (EDMPinWidget) scene.addPin(nodeID, "nodeID" + "#pin" + pinCounter++);
                scene.validate();
                srcColumnPin.setPinName(column.getDisplayName());
                scene.validate();
                List<Image> tblimage = new ArrayList<Image>();
                tblimage.add(MashupGraphUtil.getImage(ImageConstants.COLUMN));
                srcColumnPin.setGlyphs(tblimage);
                scene.validate();
                widgets.add(srcColumnPin);
                sqltoWidgetMap.put(column, srcColumnPin);
                if (column.isVisible()) {
                    srcCols.add(srcColumnPin);
                } else {
                    srcCols1.add(srcColumnPin);
                }
               
            }
            if (srcCols.size() != 0) {
                categories.put(table.getDisplayName(), srcCols);
            }
            if (srcCols1.size() != 0) {
                categories.put(table.getDisplayName(), srcCols1);
            }
            widget.sortPins(categories);
            sqltoWidgetMap.put(joinOp, widget);
            //widget.getActions().addAction(ActionFactory.createPopupMenuAction(new TablePopupProvider(table, mObj)));
            // manager.updateColumnSelection(table);
            widget.revalidate();
        }*/

              //---------------------
                /*if (column.isVisible()) {
                if (column.isPrimaryKey()) {
                tblimage.add(MashupGraphUtil.getImage(ImageConstants.PRIMARYKEYCOL));
                pinTooltip = pinTooltip + "<tr><td colspan=2><b>PRIMARY KEY</b></td></tr>";
                } else if (column.isForeignKey()) {
                tblimage.add(MashupGraphUtil.getImage(ImageConstants.FOREIGNKEYCOL));
                tblimage.add(MashupGraphUtil.getImage(ImageConstants.FOREIGNKEY));
                pinTooltip = pinTooltip + "<tr><td colspan=2><b>FOREIGN KEY</b></td></tr>";
                } else {
                tblimage.add(MashupGraphUtil.getImage(ImageConstants.COLUMN));
                }
                srcCols.add(srcColumnPin);
                } else {
                tblimage.add(MashupGraphUtil.getImage(ImageConstants.COLUMN));
                srcCols1.add(srcColumnPin);
                }
                tblimage.add(MashupGraphUtil.getImage(ImageConstants.COLUMN));
                srcColumnPin.setGlyphs(tblimage);
                scene.validate();
                widgets.add(srcColumnPin);
                //sqltoWidgetMap.put(column, srcColumnPin);*/
                //---------------------
        
        // add popup for join widget.
        widget.getActions().addAction(ActionFactory.createPopupMenuAction(new JoinPopupProvider(joinOp, mObj)));
        scene.validate();
    }
    
    private void addRuntimeNode(RuntimeInput rtInput, EDMNodeWidget widget, String nodeID) {
        sqltoWidgetMap.put(rtInput, widget);
        widget.setNodeName("Runtime Input");
        Iterator it = rtInput.getRuntimeAttributeMap().keySet().iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            EDMPinWidget columnPin = (EDMPinWidget) scene.addPin(nodeID, "nodeID" + "#pin" + pinCounter++);
            scene.validate();
            columnPin.setPinName(name);
            List<Image> image = new ArrayList<Image>();
            image.add(MashupGraphUtil.getImage(ImageConstants.RUNTIMEATTR));
            columnPin.setGlyphs(image);
            scene.validate();
            RuntimeAttribute rtAttr = (RuntimeAttribute) rtInput.
                    getRuntimeAttributeMap().get(name);
            columnPin.setToolTipText("<html><table border=0 cellspacing=0 cellpadding=4 >" + "<tr><td><b>Value</b></td><td>" + rtAttr.getAttributeValue() + "</td></tr></table></html>");
            widgets.add(columnPin);
        }
        
        // add popup for runtime inputs.
        widget.getActions().addAction(ActionFactory.createPopupMenuAction(new RuntimeModelPopupProvider(rtInput, mObj)));
        scene.validate();
    }

    //add TargetTable Node
    private void addTargetTableNode(SQLDBTable tbl, EDMNodeWidget widget, String nodeId) {
        sqltoWidgetMap.put(tbl, widget);
        String tooltip = "<html><table border=0 cellspacing=0 cellpadding=4><tr><td><b>URL</b></td>" + "<td>" + tbl.getParent().getConnectionDefinition().getConnectionURL() + "</td></tr><tr><td><b>Database</b></td><td>" + tbl.getParent().getConnectionDefinition().getDBType() + "</td></tr>";
        widget.setNodeName(tbl.getDisplayName());
        scene.validate();
        widget.getActions().addAction(ActionFactory.createPopupMenuAction(new TablePopupProvider(tbl, mObj)));

        scene.validate();
        String condition = ((TargetTable) tbl).getFilterCondition().getConditionText();
        if (condition != null && !condition.equals("")) {
            List<Image> targetimage = new ArrayList<Image>();
            targetimage.add(MashupGraphUtil.getImage(ImageConstants.FILTER));
            widget.setGlyphs(targetimage);
            scene.validate();
            tooltip = tooltip + "<tr><td><b>Extraction Condition</b></td><td>" + condition + "</td></tr></table></html>";
        }
        //now add columns
        SQLDBColumn[] columns = (SQLDBColumn[]) tbl.getColumnList().
                toArray(new SQLDBColumn[0]);
        List<Widget> usedCol = new ArrayList<Widget>();
        List<Widget> unusedCol = new ArrayList<Widget>();
        Map<String, List<Widget>> categories = new LinkedHashMap<String, List<Widget>>();
        for (SQLDBColumn column : columns) {
            String pinTooltip = "<html> <table border=0 cellspacing=0 cellpadding=4><tr><td>" + "<b>Scale</b></td><td>" + column.getScale() + "</td></tr>" + "<tr><td><b>Precision</b></td><td>" + column.getPrecision() + "</td></tr><tr><td><b>Type</b></td><td>" + column.getJdbcTypeString() + "</td></tr>";
            EDMPinWidget columnPin = (EDMPinWidget) scene.addPin(nodeId, "nodeID" + "#pin" + pinCounter++);
            scene.validate();
            columnPin.setPinName(column.getDisplayName());
            columnPin.getActions().addAction(columnSelectionEditor);
            scene.validate();
            List<Image> image = new ArrayList<Image>();
            if (column.isVisible()) {
                if (column.isPrimaryKey()) {
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
        if (usedCol.size() != 0) {
            categories.put("Used Columns", usedCol);
        }
        if (unusedCol.size() != 0) {
            categories.put("Unused Columns", unusedCol);
        }
        widget.sortPins(categories);
    }

    Map<String, List<Widget>> categories = null;


    private void addTableNode(SQLDBTable tbl, EDMNodeWidget widget, String nodeID) {
        sqltoWidgetMap.put(tbl, widget);
        String tooltip = "<html><table border=0 cellspacing=0 cellpadding=4><tr><td><b>URL</b></td>" + "<td>" + tbl.getParent().getConnectionDefinition().getConnectionURL() + "</td></tr><tr><td><b>Database</b></td><td>" + tbl.getParent().getConnectionDefinition().getDBType() + "</td></tr>";
        widget.setNodeName(tbl.getDisplayName());
        scene.validate();
        widget.getActions().addAction(ActionFactory.createPopupMenuAction(new TablePopupProvider(tbl, mObj)));
        scene.validate();
        String condition = ((SourceTable) tbl).getExtractionCondition().getConditionText();
        if (condition != null && !condition.equals("")) {
            List<Image> image = new ArrayList<Image>();
            image.add(MashupGraphUtil.getImage(ImageConstants.FILTER));
            widget.setGlyphs(image);
            scene.validate();
            tooltip = tooltip + "<tr><td><b>Extraction Condition</b></td><td>" + condition + "</td></tr></table></html>";
        }
        
        // now add columns.
        SQLDBColumn[] columns = (SQLDBColumn[]) tbl.getColumnList().
                toArray(new SQLDBColumn[0]);
        List<Widget> srcUsedCol = new ArrayList<Widget>();
        List<Widget> unusedCol = new ArrayList<Widget>();
        Map<String, List<Widget>> categories = new LinkedHashMap<String, List<Widget>>();
        for (SQLDBColumn column : columns) {
            String pinTooltip = "<html> <table border=0 cellspacing=0 cellpadding=4><tr><td>" + "<b>Scale</b></td><td>" + column.getScale() + "</td></tr>" + "<tr><td><b>Precision</b></td><td>" + column.getPrecision() + "</td></tr><tr><td><b>Type</b></td><td>" + column.getJdbcTypeString() + "</td></tr>";
            EDMPinWidget columnPin = (EDMPinWidget) scene.addPin(nodeID, "nodeID" + "#pin" + pinCounter++);
            scene.validate();
            columnPin.setPinName(column.getDisplayName());
            columnPin.getActions().addAction(columnSelectionEditor);
            scene.validate();
            List<Image> image = new ArrayList<Image>();
            if (column.isVisible()) {
                if (column.isPrimaryKey()) {
                    image.add(MashupGraphUtil.getImage(ImageConstants.PRIMARYKEYCOL));
                    pinTooltip = pinTooltip + "<tr><td colspan=2><b>PRIMARY KEY</b></td></tr>";
                } else if (column.isForeignKey()) {
                    image.add(MashupGraphUtil.getImage(ImageConstants.FOREIGNKEYCOL));
                    image.add(MashupGraphUtil.getImage(ImageConstants.FOREIGNKEY));
                    pinTooltip = pinTooltip + "<tr><td colspan=2><b>FOREIGN KEY</b></td></tr>";
                } else {
                    image.add(MashupGraphUtil.getImage(ImageConstants.COLUMN));
                }
                srcUsedCol.add(columnPin);
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
        if (srcUsedCol.size() != 0) {
            categories.put("Used Columns", srcUsedCol);
        }
        if (unusedCol.size() != 0) {
            categories.put("Unused Columns", unusedCol);
        }
        widget.sortPins(categories);
    }
    
    private void addGroupbyNode(SQLGroupByImpl groupby, EDMNodeWidget widget, String nodeID) {
        sqltoWidgetMap.put(groupby, widget);
        widget.setNodeName("Group By");
        SQLCondition condition = groupby.getHavingCondition();
        String conditionText = "<NO CONDITION>";
        if (condition != null) {
            conditionText = condition.getConditionText();
        }
        EDMPinWidget havingPin = (EDMPinWidget) scene.addPin(nodeID, "nodeID" + "#pin" + pinCounter++);
        List<Image> image = new ArrayList<Image>();
        image.add(MashupGraphUtil.getImage(ImageConstants.FILTER));
        havingPin.setGlyphs(image);
        havingPin.setPinName("HAVING CLAUSE");
        havingPin.setToolTipText(conditionText);
        scene.validate();
        widget.getActions().addAction(ActionFactory.createPopupMenuAction(new GroupByPopupProvider(groupby, mObj)));
        widgets.add(havingPin);
    }
    
    private String addJoinsAndTables(SQLDefinition sqlDefinition, SQLJoinView joinView) {
        SQLJoinOperator joinOperator = joinView.getRootJoin();
        String join = createGraphNode(joinOperator);

        recursivelyAddNodes(joinOperator, join);


        // Add tables which are not a part of the join.
        SQLDBTable[] dbTables = (SQLDBTable[]) sqlDefinition.
                getJoinSources().toArray(new SQLDBTable[0]);
        for (SQLDBTable dbTable : dbTables) {
            createGraphNode(dbTable);
        }
        
        // Add tables which are not a part of the join.
        TargetTable[] tgtTables = (TargetTable[]) sqlDefinition.
                getTargetTables().toArray(new TargetTable[0]);
        for (TargetTable tgtTable : tgtTables) {
            createGraphNode(tgtTable);
        }
        // Add groupby operator.
        SQLGroupBy grpBy = joinView.getSQLGroupBy();
        if (grpBy != null) {
            String grpbyId = createGraphNode((SQLGroupByImpl) grpBy);
            createGraphEdge(join, grpbyId);
        }
        return join;
    }
    
    private void addTablesOnly(SQLDefinition sqlDefinition) {
        SQLDBTable[] dbTables = (SQLDBTable[]) sqlDefinition.getSourceTables().
                toArray(new SQLDBTable[0]);
        TargetTable[] tgtTables = (TargetTable[]) sqlDefinition.getTargetTables().
                toArray(new TargetTable[0]);
        for (SQLDBTable dbTable : dbTables) {
            String nodeId = createGraphNode(dbTable);
            SQLGroupBy groupBy = ((SourceTable) dbTable).getSQLGroupBy();
            if (groupBy != null) {
                String grpbyId = createGraphNode((SQLGroupByImpl) groupBy);
                createGraphEdge(nodeId, grpbyId);
            }
        }
        for (TargetTable tgtTable : tgtTables) {
            String tgtId = createGraphNode(tgtTable);
            // ----------- COMMENTED FOR TESTING -------------
            /*SQLGroupBy groupBy = ((TargetTable) tgtTable).getSQLGroupBy();
            Logger.getLogger(MashupGraphManager.class.getName()).info("In addTablesOnly-- target___groupBy  "+groupBy);
            if (groupBy != null) {
            String grpbyId = createGraphNode((SQLGroupByImpl) groupBy);
            createGraphEdge(tgtId, grpbyId);
            }*/
            //String joinId = addJoinsAndTables(sqlDefinition, tgtTable.getJoinView());
            //createGraphEdge(tgtId, joinId);
            // ----------- COMMENTED FOR TESTING -------------
    }
    }
    
    private void addRuntimeModel(SQLDefinition sqlDefinition) {
        RuntimeDatabaseModel rtModel = sqlDefinition.getRuntimeDbModel();
        if (rtModel != null) {
            RuntimeInput rtInput = rtModel.getRuntimeInput();
            if (rtInput != null) {
                if (rtInput.getRuntimeAttributeMap().size() != 0) {
                    createGraphNode(rtInput);
                }
            }
        }
    }
    
    private SQLJoinView[] getJoinViews() {
        SQLJoinView[] joinViews = (SQLJoinView[]) mObj.getModel().getSQLDefinition()
                .getObjectsOfType(SQLConstants.JOIN_VIEW).toArray(new SQLJoinView[0]);
        return joinViews;
    }
    
    //-----------ADDED FOR TESTING --------------
    public SQLJoinView getJoinView(SourceTable sTable) {
        Collection joinViews = mObj.getModel().getSQLDefinition().getObjectsOfType(SQLConstants.JOIN_VIEW);
        Iterator it = joinViews.iterator();

        while (it.hasNext()) {
            SQLJoinView joinView = (SQLJoinView) it.next();
            if (joinView.containsSourceTable(sTable)) {
                return joinView;
            }
        }
        return null;
    }

    //-----------ADDED FOR TESTING --------------
    private void removeAllChildren() {
        Iterator it = widgets.iterator();
        while (it.hasNext()) {
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
    
    public class MouseStateAction extends WidgetAction.Adapter {

        @Override
        public State mouseClicked(Widget widget, WidgetMouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                MashupDataObject dObj = WindowManager.getDefault().getRegistry().getActivated().getLookup().lookup(MashupDataObject.class);
                if (dObj != null) {
                    setSelectedNode(widget.getScene().getFocusedWidget());}
                    widget.getScene().setFocusedWidget(null);
                    return State.CONSUMED;
            }
            return State.REJECTED;
        }
    }
}
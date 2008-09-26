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

package org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view;

import org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view.widgets.*;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.model.ObjectScene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Column;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view.ui.AddColumnUI;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.helper.ServiceBuilderHelper;
import org.openide.windows.WindowManager;

/**
 *
 * @author satyaranjan
 */
public class ColumnWidget extends AbstractTitledWidget implements TabWidget,ListSelectionListener{

    private Widget componentWidget;
    
    private transient Widget buttons;
    private transient ButtonWidget addButton;
    private transient ImageLabelWidget headerLabelWidget;

    private transient ColumnsTableModel model;
    private transient TableWidget parameterTable;
    private transient TableWidget entryTable;
    
    private transient Widget tabComponent;
    private transient Entity entity;
    private transient ServiceBuilderHelper helper;
    
    private ObjectScene scene;
    public ColumnWidget(ObjectScene scene,TableWidget entryTable,ServiceBuilderHelper helper) {
        super(scene,0,RADIUS,0,BORDER_COLOR);
        this.scene = scene;
        this.helper = helper;
        this.entryTable = entryTable;
        createContent();
        this.entryTable.addSelectionChangeListener(this);
    }
    
    private void createContent() {
        model = new ColumnsTableModel(null);
        populateContentWidget(getContentWidget());
        getContentWidget().setBorder(BorderFactory.createEmptyBorder(0,1,1,1));
        headerLabelWidget = new ImageLabelWidget(getScene(), getIcon(), getTitle(), 
                "("+0+")");
        headerLabelWidget.setLabelFont(getScene().getFont().deriveFont(Font.BOLD));
        getHeaderWidget().addChild(new Widget(getScene()),5);
        getHeaderWidget().addChild(headerLabelWidget);
        getHeaderWidget().addChild(new Widget(getScene()),4);

        addButton = new ButtonWidget(getScene(), "+");
        getHeaderWidget().addChild(addButton);
        addButton.setAction(new AddAction());
        
        buttons = new Widget(getScene());
        buttons.setLayout(LayoutFactory.createHorizontalFlowLayout(
                LayoutFactory.SerialAlignment.CENTER, 8));

        buttons.addChild(getExpanderWidget());
        buttons.setOpaque(true);
        buttons.setBackground(TITLE_COLOR_BRIGHT);

        getHeaderWidget().addChild(buttons);

    }

    private void populateContentWidget(Widget parentWidget) {
      //  if(model.getRowCount()>0) {
            parameterTable = new TableWidget(getScene(),model);
            parentWidget.addChild(parameterTable);
       /* } else {
            LabelWidget noParamsWidget = new LabelWidget(getScene(),
                    NbBundle.getMessage(OperationWidget.class, "LBL_InputNone"));
            noParamsWidget.setAlignment(LabelWidget.Alignment.CENTER);
            parentWidget.addChild(noParamsWidget);
        }*/
            
    }
    
    public String getTitle() {
        return "Columns";
    }

    public Image getIcon() {
        return null;
    }
    
    public void setComponentWidget(Widget componentWidget) {
        this.componentWidget = componentWidget;
    }

    public Widget getComponentWidget() {
        if(tabComponent == null){
            //Widget content = createContentWidget();
            
            populateContentWidget(getContentWidget());
            tabComponent = this;
        }
        return tabComponent;
    }

    public Object hashKey() {
        return super.hashKey();
    }
    
    private void updateTable() {
        List<Column> cols = getColumns(entity);
        
        System.out.println("Update table for entity: "+entity.getName());
        model.setColumns(cols);
    //    populateContentWidget(tabComponent);
    ///    tabComponent.revalidate();
        parameterTable.refreshTable();
        //entryTable.refreshTable();
        tabComponent.revalidate();
        ///colTableWidget.revalidate();
        ////colTableWidget = new TableWidget(scene, colTableModel);

        ///columnsWidget.addChild(colTableWidget);
    }

    private List<Column> getColumns(Entity entity) {

        if (entity == null) {
            return new ArrayList();
        }
        String[] name = entity.getColumnName();
        List<Column> cols = new ArrayList();
        for (int i = 0; i < name.length; i++) {
            Column col = new Column();
            col.setName(name[i]);
            col.setDbName(entity.getColumnDbName(i));
            col.setType(entity.getColumnType(i));
            col.setPrimaryKey(entity.getColumnPrimary(i));
            cols.add(col);
        }
        return cols;
    }
    
    private Entity getEntity(String name) {
        
        Entity[] ens = helper.getEntity();
        for(Entity en:ens) {
            
            if(en.getName().equals(name))
                return en;
        }
        return null;
    }

    public void valueChanged(ListSelectionEvent e) {

        int i = e.getFirstIndex();
        if (i == -1) {
            return;
        }
        
        
       /// columnsWidget.removeChildren();
        entity = (Entity)((Entity) entryTable.getSelectedObject()).clone();
        
        //REMOVE THIS LINE LATER
            Widget w = scene.findWidget(helper.getEntity()[0]);
            System.out.println("ValueChanged...........Widget for entry(0)::::::: "+w);
       // createTable(entity);
       // columnsWidget.revalidate();
        updateTable();
    }

     private class AddAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {

             //REMOVE THIS LINE LATER
            Widget w = scene.findWidget(helper.getEntity()[0]);
            System.out.println("AddAction.........Widget for entry(0)::::::: "+w);
            
            if (entity == null) {
                return;
            }
            AddColumnUI addColumnUI =
                    new AddColumnUI(WindowManager.getDefault().getMainWindow());
            String name = addColumnUI.getName();
            if (name == null || name.trim().length() == 0) {
                return;
            }
            
            
            String dbName = addColumnUI.getDbName();
            boolean primaryKey = addColumnUI.isPrimaryKey();
            String type = addColumnUI.getType();
            
            //REMOVE THIS LINE LATER
            Widget w3 = scene.findWidget(helper.getEntity()[0]);
            System.out.println("After UI close..###.........Widget for entry(0)::::::: "+w3);
            
            String enName = entity.getName();
            Entity en = getEntity(enName);
            
            Widget w2 = scene.findWidget(helper.getEntity()[0]);
            System.out.println("befporeeeer SAVE AddAction.........Widget for entry(0)::::::: "+w2+"  "+en.hashCode());
            
            en.addColumn("");
            en.addColumnName(name);
            en.addColumnDbName(dbName);
            en.addColumnType(type);
            en.addColumnPrimary(Boolean.toString(primaryKey));

            
            Widget w5 = scene.findWidget(helper.getEntity()[0]);
            System.out.println("Jusssssssssssst ...befporeeeer SAVE AddAction.........Widget for entry(0)::::::: "+w5);
            
            helper.save();

            Entity enn = getEntity(enName);
            Widget w1 = scene.findWidget(helper.getEntity()[0]);
            System.out.println("After SAVE AddAction.........Widget for entry(0)::::::: "+w1+"   "+enn.hashCode());
            
            entity = (Entity) en.clone();
            System.out.println("Selected index is........................." + enName);
            /*
            Column c = new Column();
            c.setName(name);
            c.setDbName(dbName);
            c.setType(type);
            c.setPrimaryKey(Boolean.toString(primaryKey));

            colTableModel.addRow(c);
            colTableWidget.addRow();*/

            //columnsWidget.removeChildren();
            //TODO entity = (Entity) entityTable.getSelectedObject();
            
            
            
            updateTable();
            
           
            
           //// entityTable.addRow();
            //columnsWidget.revalidate();
        }
    }

}


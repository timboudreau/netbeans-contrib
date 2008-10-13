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
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Column;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view.ui.AddColumnUI;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.helper.ServiceBuilderHelper;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 *
 * @author satyaranjan
 */
public class ColumnWidget extends AbstractTitledWidget implements TabWidget,ListSelectionListener{

    private Widget componentWidget;
    
    private transient Widget buttons;
    private transient ButtonWidget addButton;
    private transient ButtonWidget deleteButton;
    private transient ButtonWidget updateButton;
    private transient ImageLabelWidget headerLabelWidget;
    private transient LabelWidget entityNameLabelWidget;

    private transient ColumnsTableModel model;
    private transient TableWidget columnTable;
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
        //setBorder(new RoundedBorder3D(this,radius, depth, 0, 0, borderColor));
    }
    
    private void createContent() {
        model = new ColumnsTableModel(null);
        populateContentWidget(getContentWidget());
       // getContentWidget().setBorder(BorderFactory.createEmptyBorder(0,1,1,1));
        headerLabelWidget = new ImageLabelWidget(getScene(), getIcon(), "(" +getTitle() +")");
        headerLabelWidget.setLabelFont(getScene().getFont().deriveFont(Font.BOLD));
        entityNameLabelWidget = new LabelWidget(getScene(),"");
        entityNameLabelWidget.setFont(getScene().getFont().deriveFont(Font.BOLD));
        
        getHeaderWidget().addChild(entityNameLabelWidget);
        //getHeaderWidget().addChild(new Widget(getScene()),5);
        getHeaderWidget().addChild(headerLabelWidget);
        
        getHeaderWidget().addChild(new Widget(getScene()),4);

        addButton = new ButtonWidget(getScene(),
                NbBundle.getMessage(ColumnWidget.class, "LBL_ADD"));
        addButton.setOpaque(true);
        addButton.setRoundedBorder(addButton.BORDER_RADIUS, 4, 0, null);
        getHeaderWidget().addChild(addButton);
        addButton.setAction(new AddAction());
        
        updateButton = new ButtonWidget(getScene(),
                NbBundle.getMessage(ColumnWidget.class, "LBL_UPDATE"));
        updateButton.setOpaque(true);
        updateButton.setRoundedBorder(updateButton.BORDER_RADIUS, 4, 0, null);
        getHeaderWidget().addChild(updateButton);
        updateButton.setAction(new UpdateAction());
        
        deleteButton = new ButtonWidget(getScene(),
                NbBundle.getMessage(ColumnWidget.class, "LBL_REMOVE"));
        deleteButton.setOpaque(true);
        deleteButton.setRoundedBorder(deleteButton.BORDER_RADIUS, 4, 0, null);
        getHeaderWidget().addChild(deleteButton);
        deleteButton.setAction(new DeleteAction());
        
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
            columnTable = new TableWidget(getScene(),model);
            parentWidget.addChild(columnTable);
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
            
            //TODOOOO populateContentWidget(getContentWidget());
            tabComponent = this;
        }
        return tabComponent;
    }

    public Object hashKey() {
        return super.hashKey();
    }
    
    private void updateTable() {
        List<Column> cols = helper.getColumns(entity);
        
        System.out.println("Update table for entity: "+entity.getName());
        model.setColumns(cols);
    //    populateContentWidget(tabComponent);
    ///    tabComponent.revalidate();
        columnTable.refreshTable();
        //entryTable.refreshTable();
        tabComponent.revalidate();
        
        ///colTableWidget.revalidate();
        ////colTableWidget = new TableWidget(scene, colTableModel);

        ///columnsWidget.addChild(colTableWidget);
    }
    

    public void valueChanged(ListSelectionEvent e) {

        int i = e.getFirstIndex();
        if (i == -1) {
            return;
        }
        
        entity = (Entity)((Entity) entryTable.getSelectedObject()).clone();
        //entity = (Entity)((Entity) entryTable.getSelectedObject());
        if(entity != null) {
            entityNameLabelWidget.setLabel(entity.getName());
        }
        updateTable();
    }

     private class AddAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            
            if (entity == null) {
                return;
            }
            AddColumnUI addColumnUI =
                    new AddColumnUI(WindowManager.getDefault().getMainWindow());
            addColumnUI.setVisible(true);
            String name = addColumnUI.getName();
            if (name == null || name.trim().length() == 0) {
                return;
            }
            
            String dbName = addColumnUI.getDbName();
            boolean primaryKey = addColumnUI.isPrimaryKey();
            String type = addColumnUI.getType();
            
            String enName = entity.getName();
            Entity en = helper.getEntity(enName);
            
            Column newCol = en.newColumn();
            newCol.setName(name);
            if(dbName != null && dbName.trim().length() != 0)
                newCol.setDbName(name);
            newCol.setType(type);
            
            if(primaryKey)
                newCol.setPrimary(Boolean.toString(primaryKey));
            else
                newCol.setPrimary(null);
            en.addColumn(newCol);

            if(!helper.save())
                helper.forceReload();

            Entity enn = helper.getEntity(enName);       
            entity = (Entity) enn.clone();
            //entity = enn;
     
            updateTable();
           
        }
    }
     
    private class DeleteAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {

            Column col = (Column) columnTable.getSelectedObject();
            if(col == null)
                return;
            Entity en = helper.getEntity(entity.getName());
            Column c = helper.getColumn(en,col.getName());
            
            if(c != null) {
                helper.removeColumn(en, c);
                if(!helper.save())
                    helper.forceReload();
            }
            
            Entity newEn = helper.getEntity(en.getName());
            entity =(Entity) newEn.clone();
            //entity = newEn;
            updateTable();
        }
        
    }
    
    private class UpdateAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
                
            if (entity == null) {
                return;
            }
            
            Object selectedObj = columnTable.getSelectedObject();
            int index = columnTable.getSelectedIndex();
            if(selectedObj == null)
                return;
            Column col = (Column)selectedObj;
            AddColumnUI addColumnUI =
                    new AddColumnUI(WindowManager.getDefault().getMainWindow(),col);
            addColumnUI.setVisible(true);
            
            String name = addColumnUI.getName();
            if (name == null || name.trim().length() == 0) {
                return;
            }
            
            String dbName = addColumnUI.getDbName();
            boolean primaryKey = addColumnUI.isPrimaryKey();
            String type = addColumnUI.getType();
            
            Entity en = helper.getEntity(entity.getName());
            Column actualCol = helper.getColumn(en, col.getName());
            if(actualCol == null)
                return;
            
            if(dbName != null && dbName.trim().length() != 0)
                actualCol.setDbName(dbName);
            actualCol.setType(type);
            
            if(primaryKey)
                actualCol.setPrimary(Boolean.toString(primaryKey));
            else
                actualCol.setPrimary(null);
            
            if(!helper.save())
                helper.forceReload();
            
            Entity newEn = helper.getEntity(en.getName());
            entity =(Entity) newEn.clone();
     
            updateTable();
        }
        
    }

}


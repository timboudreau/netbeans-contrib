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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.ScrollWidget;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Column;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view.ui.AddColumnUI;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view.widgets.ButtonWidget;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view.widgets.ColumnsTableModel;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view.widgets.TableModel;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view.widgets.TableWidget;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.helper.ServiceBuilderHelper;
import org.openide.windows.WindowManager;

/**
 *
 * @author satyaranjan
 */
public class ColumnsView implements ListSelectionListener {

    private Scene scene;
    private Widget parent;
    private TableWidget entityTable;
    private Widget columnsWidget;
    private ButtonWidget addButtonWidget;
    private ButtonWidget deleteButtonWidget;
    private ButtonWidget detailButtonWidget;
    private Widget buttonsWidget;
    private ServiceBuilderHelper helper;
    private Entity entity;
    private TableWidget colTableWidget;
    private ScrollWidget scrollWidget;
    private ColumnsTableModel colTableModel;

    public ColumnsView(Scene scene, Widget parent, TableWidget entityTable, ServiceBuilderHelper helper) {

        this.parent = parent;
        this.scene = scene;
        this.entityTable = entityTable;
        this.helper = helper;

        columnsWidget = new Widget(scene);
        columnsWidget.setLayout(LayoutFactory.createHorizontalFlowLayout(
                LayoutFactory.SerialAlignment.JUSTIFY, 16));
        createTable(null);

        buttonsWidget = new Widget(scene);
        buttonsWidget.setLayout(LayoutFactory.createVerticalFlowLayout(
                LayoutFactory.SerialAlignment.JUSTIFY, 16));
        addButtonWidget = new ButtonWidget(scene, "Add");
        detailButtonWidget = new ButtonWidget(scene, "Detail");
        deleteButtonWidget = new ButtonWidget(scene, "Delete");

        addButtonWidget.setAction(new AddAction());
        deleteButtonWidget.setAction(new DeleteAction());
        detailButtonWidget.setAction(new DetailAction());

        buttonsWidget.addChild(addButtonWidget);
        buttonsWidget.addChild(detailButtonWidget);
        buttonsWidget.addChild(deleteButtonWidget);

        entityTable.addSelectionChangeListener(this);


        //  scrollWidget = new ScrollWidget(scene, columnsWidget);
        //  scrollWidget.setPreferredBounds(new Rectangle(250,250));

        parent.addChild(columnsWidget);
        parent.addChild(buttonsWidget);

    }

    public void createTable(Entity entity) {

        List<Column> cols = getColumns(entity);
        colTableModel = new ColumnsTableModel(cols);
        colTableWidget = new TableWidget(scene, colTableModel);

        columnsWidget.addChild(colTableWidget);
        
    }
    
    private void updateTable() {
        List<Column> cols = getColumns(entity);
        
        colTableModel.setColumns(cols);
        colTableWidget.refreshTable();
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
        
        Entity[] ens = helper.getServiceBuilder().getEntity();
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
        entity = (Entity)((Entity) entityTable.getSelectedObject()).clone();
        
       // createTable(entity);
       // columnsWidget.revalidate();
        updateTable();
    }

    private class AddAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {

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
            
            String enName = entity.getName();
            Entity en = getEntity(enName);
            
            en.addColumn("");
            en.addColumnName(name);
            en.addColumnDbName(dbName);
            en.addColumnType(type);
            en.addColumnPrimary(Boolean.toString(primaryKey));

            helper.save();

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
            entityTable.addRow();
            //columnsWidget.revalidate();
        }
    }

    private class DetailAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
        }
    }

    private class DeleteAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
        }
    }
}

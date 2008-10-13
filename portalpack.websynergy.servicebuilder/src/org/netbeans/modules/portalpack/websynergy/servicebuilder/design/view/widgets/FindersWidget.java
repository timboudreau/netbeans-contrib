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
package org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view.widgets;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JToolBar;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.model.ObjectScene;
import org.netbeans.api.visual.model.ObjectSceneEvent;
import org.netbeans.api.visual.model.ObjectSceneEventType;
import org.netbeans.api.visual.model.ObjectSceneListener;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.FinderColumn;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view.ui.AddFinderUI;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view.ui.FinderColumnData;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.helper.ServiceBuilderHelper;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 *
 * @author satyaranjan
 */
public class FindersWidget extends AbstractTitledWidget {

    private transient Entity entity;
    private transient AddFinderAction addAction;
    private transient UpdateFinderAction updateAction;
    private transient RemoveAction removeAction;
    private transient Widget buttons;
    private transient ButtonWidget addButton;
    private transient ButtonWidget removeButton;
    private transient ButtonWidget updateButton;
    private transient ImageLabelWidget headerLabelWidget;
    private transient LabelWidget entityNameWidget;
    private ObjectScene scene;
    private ServiceBuilderHelper helper;
    private ObjectSceneListener operationSelectionListener;

    public FindersWidget(ObjectScene scene, final Entity entity, ServiceBuilderHelper helper) {
        super(scene, RADIUS, BORDER_COLOR);
        this.scene = scene;
        this.entity = entity;
        this.helper = helper;
        createContent();
    }

    private void createContent() {
        ///   if (serviceModel==null) return;
        entityNameWidget = new LabelWidget(getScene(), "");
        entityNameWidget.setFont(getScene().getFont().deriveFont(Font.BOLD));

        headerLabelWidget = new ImageLabelWidget(getScene(), null, " ( " +
                NbBundle.getMessage(OperationsWidget.class, "LBL_Finders") + " ) ");
        headerLabelWidget.setLabelFont(getScene().getFont().deriveFont(Font.BOLD));
        getHeaderWidget().addChild(entityNameWidget);
        getHeaderWidget().addChild(headerLabelWidget);
        getHeaderWidget().addChild(new Widget(getScene()), 1);
        updateHeaderLabel();

        buttons = new Widget(getScene());
        buttons.setLayout(LayoutFactory.createHorizontalFlowLayout(
                LayoutFactory.SerialAlignment.CENTER, 8));

        addButton = new ButtonWidget(getScene(), "Add Finder");
        addButton.setOpaque(true);
        addButton.setRoundedBorder(addButton.BORDER_RADIUS, 4, 0, null);

        removeButton = new ButtonWidget(getScene(), "Remove");
        removeButton.setOpaque(true);
        removeButton.setRoundedBorder(removeButton.BORDER_RADIUS, 4, 0, null);
        
        updateButton = new ButtonWidget(getScene(), "Update");
        updateButton.setOpaque(true);
        updateButton.setRoundedBorder(updateButton.BORDER_RADIUS, 4, 0, null);

        //goToSourceButton.setAction(new GoToSourceAction());
        addAction = new AddFinderAction();
        addButton.setAction(addAction);

        removeAction = new RemoveAction();
        removeButton.setAction(removeAction);
        
        updateAction = new UpdateFinderAction();
        updateButton.setAction(updateAction);

        buttons.addChild(addButton);
        buttons.addChild(updateButton);
        buttons.addChild(removeButton);
        buttons.addChild(getExpanderWidget());

        getHeaderWidget().addChild(buttons);

        getContentWidget().setBorder(BorderFactory.createEmptyBorder(RADIUS));

        if (entity != null) {
            Finder[] finders = entity.getFinder();
            //operationsPanelWidget = new Widget(getScene());
            if (finders != null) {
                for (Finder finder : finders) {
                    FinderWidget finderWidget = new FinderWidget(scene, finder);
                    getContentWidget().addChild(finderWidget);
                }
            } else {
                LabelWidget msgWidget = new LabelWidget(getScene(), "No finder method found.");
                getContentWidget().addChild(msgWidget);
            }
        }

    //getContentWidget().addChild(operationsPanelWidget);
    }

    private void updateHeaderLabel() {
        //int noOfOperations = serviceModel == null || serviceModel.getOperations()==null?0:serviceModel.getOperations().size();
        // headerLabelWidget.setComment("(" + noOfOperations + ")");
    }

    public void reload(Entity ent) {

        this.entity = ent;
        getContentWidget().removeChildren();
        getContentWidget().revalidate();

        if (entity != null) {
            Finder[] finders = entity.getFinder();
            headerLabelWidget.setLabel(entity.getName() + " ( " +
                    NbBundle.getMessage(OperationsWidget.class, "LBL_Finders") + " )");
            //operationsPanelWidget = new Widget(getScene());
            if (finders != null) {
                for (Finder finder : finders) {
                    FinderWidget finderWidget = new FinderWidget(scene, finder);
                    getContentWidget().addChild(finderWidget);
                }
            } else {
                LabelWidget msgWidget = new LabelWidget(getScene(), "No finder methods found.");
                getContentWidget().addChild(msgWidget);
            }
        }
        getContentWidget().revalidate();
    }

    /* public Object hashKey() {
    return entity;
    }*/
    protected void notifyAdded() {
        super.notifyAdded();
        operationSelectionListener = new ObjectSceneAdapter() {

            public void selectionChanged(ObjectSceneEvent event,
                    Set<Object> previousSelection, Set<Object> newSelection) {
                Set<Finder> finders = new HashSet<Finder>();
                if (newSelection != null) {
                    for (Object obj : newSelection) {
                        if (obj instanceof Finder) {
                            finders.add((Finder) obj);
                        }
                    }
                }
                removeAction.setWorkingSet(finders);
                updateAction.setWorkingSet(finders);
            }
        };
        getObjectScene().addObjectSceneListener(operationSelectionListener,
                ObjectSceneEventType.OBJECT_SELECTION_CHANGED);
    }

    protected void notifyRemoved() {
        super.notifyRemoved();
        if (operationSelectionListener != null) {
            getObjectScene().removeObjectSceneListener(operationSelectionListener,
                    ObjectSceneEventType.OBJECT_SELECTION_CHANGED);
            operationSelectionListener = null;
        }
    }

    /**
     * Adds the widget actions to the given toolbar (no separators are
     * added to either the beginning or end).
     *
     * @param  toolbar  to which the actions are added.
     */
    public void addToolbarActions(JToolBar toolbar) {
        toolbar.add(addAction);
        toolbar.add(removeAction);
    }

    public class AddFinderAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {

            AddFinderUI ui = new AddFinderUI(WindowManager.getDefault().getMainWindow(), entity);
            ui.setVisible(true);
            if (!ui.isOK()) {
                return;
            }
            String name = ui.getFinderName();
            //String comparator = ui.getComparator();

            String returnType = ui.getReturnType();
            List cols = ui.getFinderColumns();

            Finder finder = entity.newFinder();
            finder.setName(name);
            finder.setReturnType(returnType);

            for (Object col : cols) {

                FinderColumn column = finder.newFinderColumn();
                FinderColumnData data = (FinderColumnData) col;
                column.setName(data.getName());
                if (data.isCaseSensitive()) {
                    column.setCaseSensitive("true");
                }
                if (data.getComparator() != null && data.getComparator().trim().length() != 0) {
                    column.setComparator(data.getComparator());
                }
                finder.addFinderColumn(column);
            }
            String entityName = entity.getName();
            Entity en = helper.getEntity(entityName);
            en.addFinder(finder);

            if (!helper.save()) {

                helper.forceReload();
                Entity enn = helper.getEntity(entityName);
                reload((Entity)enn.clone());
                return;
            }

            reload((Entity)en.clone());

        }
    }

    public class RemoveAction extends AbstractAction {

        private Set<Finder> finders;

        public void setWorkingSet(Set<Finder> finders) {
            this.finders = finders;
            setEnabled(finders != null && !finders.isEmpty());
        }

        public void actionPerformed(ActionEvent e) {

            if (finders.size() < 1) {
                return;
            }
            boolean singleSelection = finders.size() == 1;
            String finderName = singleSelection ? finders.iterator().next().getName() : "" + finders.size();
            NotifyDescriptor desc = new NotifyDescriptor.Confirmation(NbBundle.getMessage(RemoveAction.class,
                    (singleSelection ? "MSG_OPERATION_DELETE" : "MSG_OPERATIONS_DELETE"), finderName));
            Object retVal = DialogDisplayer.getDefault().notify(desc);
            if (retVal == NotifyDescriptor.YES_OPTION) {

                String[] finderNames = new String[finders.size()];
                Iterator<Finder> it = finders.iterator();
                int i = 0;
                while(it.hasNext()) {
                    finderNames[i] = it.next().getName();
                    i++;
                }
                
                Entity en = helper.getEntity(entity.getName());
                helper.removeFinders(en, finderNames);
                String enName = en.getName();
                
                if(!helper.save()) {
                    helper.forceReload();
                    Entity enn = helper.getEntity(enName);
                    reload((Entity)enn.clone());
                    return;
                }
                reload((Entity)en.clone());
            }
        }
        
        @Override
        public void setEnabled(boolean newValue) {
            super.setEnabled(newValue);
            removeButton.setButtonEnabled(newValue);
        }
    }
    
    public class UpdateFinderAction extends AbstractAction {

        private Set<Finder> finders;
        public void setWorkingSet(Set<Finder> finders) {
            this.finders = finders;
            setEnabled(finders != null && !finders.isEmpty() && finders.size() == 1);
        }
        public void actionPerformed(ActionEvent e) {
            
            if (finders.size() != 1) {
                return;
            }
            
            Finder finder = finders.iterator().next();
            AddFinderUI ui = new AddFinderUI(WindowManager.getDefault().getMainWindow(), entity,finder);
            
            ui.setVisible(true);
            if (!ui.isOK()) {
                return;
            }
            String name = ui.getFinderName();
            
            String entityName = entity.getName();
            Entity en = helper.getEntity(entityName);
            Finder orgFinder = helper.getFinder(en, name);
            
            //String comparator = ui.getComparator();

            String returnType = ui.getReturnType();
            List cols = ui.getFinderColumns();

            //orgFinder.setName(name);
            orgFinder.setReturnType(returnType);
            

            FinderColumn[] fcs = orgFinder.getFinderColumn();
            
            for (Object col : cols) {
              
                for(FinderColumn column:fcs) {
                    
                    if(!column.getName().equals(((FinderColumnData)col).getName()))
                            continue;
                    
                    FinderColumnData data = (FinderColumnData) col;
                   // column.setName(data.getName());
                    if (data.isCaseSensitive()) {
                        column.setCaseSensitive("true");
                    }
                    if (data.getComparator() != null && data.getComparator().trim().length() != 0) {
                        column.setComparator(data.getComparator());
                    }
                }           
            }
            
            //orgFinder.setFinderColumn(newFinderCols);
          
            if (!helper.save()) {

                helper.forceReload();
                Entity enn = helper.getEntity(entityName);
                reload((Entity)enn.clone());
                return;
            }

            reload((Entity)en.clone());
        
        }

        @Override
        public void setEnabled(boolean newValue) {
            super.setEnabled(newValue);
            updateButton.setButtonEnabled(newValue);
        }
        
    }
}

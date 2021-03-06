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
package org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view.widgets;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.HashSet;
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
import org.openide.util.NbBundle;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.design.javamodel.*;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

/**
 *
 * @author Satyaranjan
 */
public class OperationsWidget extends AbstractTitledWidget {

    private transient ServiceModel serviceModel;
    private transient Action addAction;
    ///  private transient RemoveOperationAction removeAction;
    private transient Widget buttons;
    private transient ImageLabelWidget headerLabelWidget;
    private transient Widget operationsPanelWidget;
    private ObjectSceneListener operationSelectionListener;
    private transient LabelWidget entityNameWidget;

    /**
     * Creates a new instance of OperationWidget
     * @param scene
     * @param service
     * @param serviceModel
     */
    public OperationsWidget(ObjectScene scene, final ServiceModel serviceModel) {
        super(scene, RADIUS, BORDER_COLOR);
        createContent();
    }

    private void createContent() {
        ///   if (serviceModel==null) return;
        entityNameWidget = new LabelWidget(getScene(),"");
        entityNameWidget.setFont(getScene().getFont().deriveFont(Font.BOLD));
        
        headerLabelWidget = new ImageLabelWidget(getScene(), null,"("+
                NbBundle.getMessage(OperationsWidget.class, "LBL_Operations")+")");
        headerLabelWidget.setLabelFont(getScene().getFont().deriveFont(Font.BOLD));
        getHeaderWidget().addChild(entityNameWidget);
        getHeaderWidget().addChild(headerLabelWidget);
        getHeaderWidget().addChild(new Widget(getScene()), 1);
        updateHeaderLabel();

        buttons = new Widget(getScene());
        buttons.setLayout(LayoutFactory.createHorizontalFlowLayout(
                LayoutFactory.SerialAlignment.CENTER, 8));

        addAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                //do nothing;
            }
        };

        /*    ButtonWidget addButton = new ButtonWidget(getScene(), addAction);
        addButton.setOpaque(true);
        addButton.setRoundedBorder(addButton.BORDER_RADIUS, 4, 0, null);
        
        //removeAction = new RemoveOperationAction(service);
        ButtonWidget removeButton = new ButtonWidget(getScene(),"r"); //removeAction);
        removeButton.setOpaque(true);
        removeButton.setRoundedBorder(removeButton.BORDER_RADIUS, 4, 0, null);*/

        ButtonWidget goToSourceButton = new ButtonWidget(getScene(), "Go To Source");
        goToSourceButton.setOpaque(true);
        goToSourceButton.setRoundedBorder(goToSourceButton.BORDER_RADIUS, 4, 0, null);
        goToSourceButton.setAction(new GoToSourceAction());

        buttons.addChild(goToSourceButton);
        //    buttons.addChild(addButton);
        //    buttons.addChild(removeButton);
        buttons.addChild(getExpanderWidget());

        getHeaderWidget().addChild(buttons);

        getContentWidget().setBorder(BorderFactory.createEmptyBorder(RADIUS));

        //operationsPanelWidget = new Widget(getScene());
        if (serviceModel != null && serviceModel.getImplementationClass() != null 
                                 && serviceModel.getOperations() != null) {
            for (MethodModel operation : serviceModel.getOperations()) {
                //  LabelWidget d = new LabelWidget(getScene(),"testdeeeee");
                // getContentWidget().addChild(d);

                OperationWidget operationWidget = new OperationWidget(getObjectScene(), operation);
                getContentWidget().addChild(operationWidget);
            //operationsPanelWidget.addChild(operationWidget);
            }
        } else {
            LabelWidget msgWidget = new LabelWidget(getScene(),"No Java Source Found. Please regenerate services first.");
            getContentWidget().addChild(msgWidget);
        }

    //getContentWidget().addChild(operationsPanelWidget);
    }

    private void updateHeaderLabel() {
        int noOfOperations = serviceModel == null || serviceModel.getOperations()==null?0:serviceModel.getOperations().size();
        
        headerLabelWidget.setComment("(" + noOfOperations + ")");
    }

    public void reload(ServiceModel model) {

        serviceModel = model;
        getContentWidget().removeChildren();
        getContentWidget().revalidate();

        if (serviceModel != null && serviceModel.getImplementationClass() != null 
                                 && serviceModel.getOperations() != null) {
            
           
            entityNameWidget.setLabel(serviceModel.getImplementationClass().getNameExt());
            
            for (MethodModel operation : serviceModel.getOperations()) {
                //  LabelWidget d = new LabelWidget(getScene(),"testdeeeee");
                // getContentWidget().addChild(d);

                OperationWidget operationWidget = new OperationWidget(getObjectScene(), operation);
                getContentWidget().addChild(operationWidget);
            // operationsPanelWidget.addChild(operationWidget);
            }
        } else {
            
            entityNameWidget.setLabel("");
            LabelWidget msgWidget = new LabelWidget(getScene(),"No Java Source Found. Please regenerate services first.");
            getContentWidget().addChild(msgWidget);
        }

        getContentWidget().revalidate();

    }

    public Object hashKey() {
        return serviceModel;
    }

    protected void notifyAdded() {
        super.notifyAdded();
        operationSelectionListener = new ObjectSceneAdapter() {

            public void selectionChanged(ObjectSceneEvent event,
                    Set<Object> previousSelection, Set<Object> newSelection) {
                Set<MethodModel> methods = new HashSet<MethodModel>();
                if (newSelection != null) {
                    for (Object obj : newSelection) {
                        if (obj instanceof MethodModel) {
                            methods.add((MethodModel) obj);
                        }
                    }
                }
//                removeAction.setWorkingSet(methods);
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
    }

    private class GoToSourceAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (serviceModel == null) {
                return;
            }
            FileObject fob = serviceModel.getImplementationClass();
            if (fob == null) {
                return;
            }
            try {
                DataObject dobj = DataObject.find(fob);
                OpenCookie open = (OpenCookie) dobj.getCookie(OpenCookie.class);
                if (open != null) {
                    open.open();
                }
            } catch (DataObjectNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }
}

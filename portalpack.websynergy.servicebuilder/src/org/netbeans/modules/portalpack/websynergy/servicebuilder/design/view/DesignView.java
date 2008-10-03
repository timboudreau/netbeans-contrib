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
package org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.TextFieldInplaceEditor;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.model.ObjectScene;
import org.netbeans.api.visual.widget.ComponentWidget;
import org.netbeans.api.visual.widget.EventProcessingType;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.SeparatorWidget;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.api.ServiceBuilderEditorContext;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity;
import org.openide.filesystems.FileObject;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view.widgets.*;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.design.javamodel.*;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view.ui.AddServiceUI;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.helper.GenerateServiceHelper;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.helper.ServiceBuilderHelper;
import org.openide.windows.WindowManager;
import sun.awt.HorizBagLayout;

/**
 * Service Builder Designer
 *
 * @author Satyaranjan
 */
public class DesignView extends JPanel {

    public static final Object messageLayerKey = new Object();
    private static final Color BG_COLOR = new Color(217, 235, 255);
    private FileObject implementationClass;
    private ServiceModel serviceModel;
    /** Manages the state of the widgets and corresponding objects. */
    private ObjectScene scene;
    /** Manages the zoom level. */
    private ZoomManager zoomer;
    private Widget mainLayer;
    private Widget messageWidget;
//    private LabelWidget headerWidget;
    private Widget contentWidget;
    private Widget mainWidget;
    private Widget separatorWidget;
    private OperationsWidget operationsWidget;
    private ButtonWidget addServiceWidget;
    private ButtonWidget removeServiceWidget;
    private ServiceBuilderHelper helper;
    private ServicesTableModel st;
    private TableWidget tableWidget;
    private ServiceBuilderEditorContext context;
    private JScrollPane panel;

    /**
     * Creates a new instance of GraphView.
     * @param service
     * @param implementationClass
     */
    public DesignView(ServiceBuilderEditorContext context) {
        super(new BorderLayout());

        this.context = context;
        this.implementationClass = implementationClass;
        ///this.serviceModel = ServiceModel.getServiceModel(implementationClass);
        helper = context.getDataObject().getServiceBuilderHelper();
        scene = new ObjectScene();
        
        final JComponent sceneView = scene.createView();
        zoomer = new ZoomManager(scene);

        scene.getActions().addAction(ActionFactory.createCycleObjectSceneFocusAction());
        scene.setKeyEventProcessingType(EventProcessingType.FOCUSED_WIDGET_AND_ITS_PARENTS);
        
        panel = new JScrollPane(sceneView);
        panel.getVerticalScrollBar().setUnitIncrement(16);
        panel.getHorizontalScrollBar().setUnitIncrement(16);
        panel.setBorder(null);
        add(panel);

        init();
    }
    
    private void init() {
        /*{
        @Override
        
        public Comparable<DesignerWidgetIdentityCode> getIdentityCode(Object object) {
        return new DesignerWidgetIdentityCode(scene,object);
        }
        };*/
      
        mainLayer = new LayerWidget(scene);
        mainLayer.setPreferredLocation(new Point(0, 0));
        mainLayer.setLayout(LayoutFactory.createVerticalFlowLayout(
                LayoutFactory.SerialAlignment.JUSTIFY, 12));
        scene.addChild(mainLayer);

        mainWidget = new Widget(scene);
        mainWidget.setLayout(LayoutFactory.createVerticalFlowLayout(
                LayoutFactory.SerialAlignment.JUSTIFY, 12));

        Widget headerPanelWidget = createHeaderPanel();
        mainWidget.addChild(headerPanelWidget);
        

        separatorWidget = new SeparatorWidget(scene,
                SeparatorWidget.Orientation.HORIZONTAL);
        separatorWidget.setForeground(Color.ORANGE);
        mainWidget.addChild(separatorWidget);


        contentWidget = new Widget(scene);

        // RoundedBorder3D border = 
        //       new RoundedBorder3D(contentWidget, 5, 1, 1, 1, Color.gray);
        //contentWidget.setBorder(border);
        contentWidget.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        // contentWidget.setBorder(BorderFactory.createBevelBorder(true));
        contentWidget.setLayout(LayoutFactory.createVerticalFlowLayout(
                LayoutFactory.SerialAlignment.JUSTIFY, 16));

        Widget entryContentWidget = new Widget(scene);
        entryContentWidget.setLayout(LayoutFactory.createHorizontalFlowLayout(
                LayoutFactory.SerialAlignment.JUSTIFY, 16));
        entryContentWidget.setBorder(BorderFactory.createEmptyBorder(0, 1, 1, 1));


        Widget buttonContentWidget = new Widget(scene);
        /// buttonContentWidget.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        buttonContentWidget.setLayout(LayoutFactory.createVerticalFlowLayout(
                LayoutFactory.SerialAlignment.JUSTIFY, 16));

        contentWidget.addChild(entryContentWidget);
        mainWidget.addChild(contentWidget);

        //add operations widget
        //// operationsWidget = new OperationsWidget(scene,serviceModel);
        ///contentWidget.addChild(operationsWidget);


        Entity[] entity = helper.getEntity();

        List<Entity> l = new ArrayList();

        for (int i = 0; i < entity.length; i++) {
            l.add(entity[i]);
        }

        st = new ServicesTableModel(l);
        tableWidget = new TableWidget(scene, st, true);

        //  JScrollPane jsp = new JScrollPane(tableWidget);
        //   ComponentWidget scrollPaneWidget = new ComponentWidget(scene,);
        entryContentWidget.addChild(tableWidget);
        entryContentWidget.addChild(buttonContentWidget);


        addServiceWidget = new ButtonWidget(scene, "Add");
        addServiceWidget.setOpaque(true);
        addServiceWidget.setRoundedBorder(3, 4, 0, null);

        removeServiceWidget = new ButtonWidget(scene, "Delete");
        removeServiceWidget.setOpaque(true);
        removeServiceWidget.setRoundedBorder(3, 4, 0, null);

        buttonContentWidget.addChild(addServiceWidget);
        buttonContentWidget.addChild(removeServiceWidget);

        addServiceWidget.setAction(new AddEntityAction());
        removeServiceWidget.setAction(new RemoveEntityAction());

        
        Widget globalParamWidget = new Widget(scene);
        globalParamWidget.setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.JUSTIFY, 8));
        //Package-path widget
        Widget packagePathWidget = new Widget(scene);
        packagePathWidget.setLayout(LayoutFactory.createHorizontalFlowLayout(LayoutFactory.SerialAlignment.JUSTIFY, 16));
        LabelWidget packagePathLabelWidget = new LabelWidget(scene, "Package Path :");
        packagePathLabelWidget.setFont(scene.getFont().deriveFont(Font.BOLD));
        packagePathLabelWidget.setForeground(Color.BLUE);
        
        LabelWidget packagePathTfWidget =
                new LabelWidget(scene, helper.getPackagePath());
        //packagePathTfWidget.setMaximumSize(new Dimension(80,10));
        packagePathTfWidget.setBorder(BorderFactory.createLineBorder());
        
        packagePathTfWidget.getActions().
                addAction(ActionFactory.createInplaceEditorAction(new PackagePathInPlaceEditor()));
        
        packagePathTfWidget.setAlignment(LabelWidget.Alignment.CENTER);
        
        packagePathWidget.addChild(packagePathLabelWidget);
        packagePathWidget.addChild(packagePathTfWidget);
        //package-path end
        //namespace widget
        Widget namespaceWidget = new Widget(scene);
        namespaceWidget.setLayout(LayoutFactory.createHorizontalFlowLayout(LayoutFactory.SerialAlignment.JUSTIFY, 16));
        LabelWidget namespaceLabelWidget = new LabelWidget(scene, "Namespace    :");
        namespaceLabelWidget.setFont(scene.getFont().deriveFont(Font.BOLD));
        namespaceLabelWidget.setForeground(Color.BLUE);
        
        LabelWidget namespaceTfWidget =
                new LabelWidget(scene, helper.getNamespace());
        
        namespaceTfWidget.setBorder(BorderFactory.createLineBorder());
        
        namespaceTfWidget.getActions().
                addAction(ActionFactory.createInplaceEditorAction(new NamespaceInPlaceEditor()));
        
        namespaceTfWidget.setAlignment(LabelWidget.Alignment.CENTER);
        
        namespaceWidget.addChild(namespaceLabelWidget);
        namespaceWidget.addChild(namespaceTfWidget);
        //namespacewidget end
        globalParamWidget.addChild(packagePathWidget);
        globalParamWidget.addChild(namespaceWidget);
        
        entryContentWidget.addChild(globalParamWidget);


        TabbedPaneWidget tabPaneWidget = new TabbedPaneWidget(scene);

        Widget columnWidgetParent = new Widget(scene);
        columnWidgetParent.setLayout(LayoutFactory.createHorizontalFlowLayout(
                LayoutFactory.SerialAlignment.JUSTIFY, 16));
        //add column widget
        //TODO columnsView = new ColumnsView(scene, columnWidgetParent, tableWidget,helper);
        ColumnWidget colWidget = new ColumnWidget(scene, tableWidget, helper);
        ///todo colWidget.setComponentWidget(columnWidgetParent);

        tabPaneWidget.addTab(colWidget);

        //add operations widget
        //original code
        // operationsWidget = new OperationsWidget(scene,serviceModel);
        //contentWidget.addChild(operationsWidget);

        //new code

        FinderMethodsWidget finderMethodsWidget = new FinderMethodsWidget(scene);
        LocalMethodsWidget localMethodsWidget = new LocalMethodsWidget(scene);

        tabPaneWidget.addTab(finderMethodsWidget);
        tabPaneWidget.addTab(localMethodsWidget);

        contentWidget.addChild(tabPaneWidget);


        // add wsit widget
        // WsitWidget wsitWidget = new WsitWidget(scene,service, implementationClass);
        // contentWidget.addChild(wsitWidget);

        ///// sceneView.removeMouseWheelListener((MouseWheelListener)sceneView);
        /***final JScrollPane panel = new JScrollPane(sceneView);
        panel.getVerticalScrollBar().setUnitIncrement(16);
        panel.getHorizontalScrollBar().setUnitIncrement(16);
        panel.setBorder(null);
        add(panel);***/
        mainLayer.addChild(mainWidget);

        messageWidget = new Widget(scene);
        messageWidget.setLayout(LayoutFactory.createVerticalFlowLayout(
                LayoutFactory.SerialAlignment.JUSTIFY, 4));
        mainLayer.addChild(messageWidget);
        scene.addObject(messageLayerKey, messageWidget);

        scene.addSceneListener(new ObjectScene.SceneListener() {

            public void sceneRepaint() {
            }

            public void sceneValidating() {
            }

            public void sceneValidated() {
                int width = panel.getViewport().getWidth();
                if (width <= scene.getBounds().width) {
                    mainWidget.setMinimumSize(new Dimension((int) (width * 1.0), 0));
                }
            }
        });

    // vlv: print
    /////  getContent().putClientProperty("print.printable", Boolean.TRUE); // NOI18N
    }
    
    private Widget createHeaderPanel() {
        Widget headerPanelWidget = new Widget(scene);
        headerPanelWidget.setLayout(
                LayoutFactory.createHorizontalFlowLayout(LayoutFactory.SerialAlignment.CENTER, 16));
        LabelWidget headerWidget = new LabelWidget(scene, "service.xml");
        headerWidget.setFont(scene.getFont().deriveFont(Font.BOLD));
        headerWidget.setForeground(Color.GRAY);
        headerPanelWidget.addChild(headerWidget);

        headerWidget.setBorder(BorderFactory.createEmptyBorder(6, 28, 0, 0));
        ButtonWidget generateServiceButton = new ButtonWidget(scene, "Genrate Services");
        generateServiceButton.setOpaque(true);
        generateServiceButton.setRoundedBorder(3, 4, 0, null);
        
        headerPanelWidget.addChild(generateServiceButton);
        ButtonWidget reloadButton = new ButtonWidget(scene, "Reload");
        reloadButton.setOpaque(true);
        reloadButton.setRoundedBorder(3, 4, 0, null);
        reloadButton.setAction(new ReloadAction());
        
        generateServiceButton.setAction(new GenerateAction(context.getServiceBuilderFile()));
        headerPanelWidget.addChild(reloadButton);
        return headerPanelWidget;
    }

    /**
     * Adds the graph actions to the given toolbar.
     *
     * @param  toolbar  to which the actions are added.
     */
    public void addToolbarActions(JToolBar toolbar) {
        toolbar.addSeparator();
        zoomer.addToolbarActions(toolbar);
        toolbar.addSeparator();
        operationsWidget.addToolbarActions(toolbar);
    }
    
    public void reloadIfDirty() {
        if(helper.isDirty()) {
          //  scene.removeChildren();
          //  init();
          //  scene.revalidate();
        }
    }

    /**
     * Return the view content, suitable for printing (i.e. without a
     * scroll pane, which would result in the scroll bars being printed).
     *
     * @return  the view content, sans scroll pane.
     */
    public JComponent getContent() {
        return scene.getView();
    }

    public void requestFocus() {
        super.requestFocus();
        // Ensure the graph widgets have the focus.
        scene.getView().requestFocus();
    }

    public boolean requestFocusInWindow() {
        super.requestFocusInWindow();
        // Ensure the graph widgets have the focus.
        return scene.getView().requestFocusInWindow();
    }

    private String getServiceName() {
        String serviceName = serviceModel.getServiceName();

        return serviceName;
    }

    private class AddEntityAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {

            AddServiceUI addSrvUI = new AddServiceUI(WindowManager.getDefault().getMainWindow());

            if (addSrvUI.getServiceName() != null && addSrvUI.getName().trim().length() != 0) {
                Entity entity = helper.newEntity();
                entity.setName(addSrvUI.getServiceName());
                if(addSrvUI.isRemoteService())
                    entity.setRemoteService(Boolean.toString(addSrvUI.isRemoteService()));
                entity.setLocalService(Boolean.toString(addSrvUI.isLocalService()));

                helper.addEntity(entity);
                st.addRow(entity);
                //tableWidget.addRow();
                tableWidget.refreshTable();
                scene.revalidate();

            }
        }
    }

    private class RemoveEntityAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {

            Object object = tableWidget.getSelectedObject();
            if (object == null || !(object instanceof Entity)) {
                return;
            }

            helper.removeEntity((Entity) object);
            st.removeRow(tableWidget.getSelectedIndex());
            tableWidget.refreshTable();
            scene.revalidate();
        }
    }
    
    private class ReloadAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            scene.removeChildren();
            init();
            scene.revalidate();
        }
        
    }

    private class PackagePathInPlaceEditor implements TextFieldInplaceEditor {

        public boolean isEnabled(Widget widget) {
            return true;
        }

        public String getText(Widget widget) {
            if(widget instanceof LabelWidget) {
                return ((LabelWidget)widget).getLabel();
            }
            return null;
        }

        public void setText(Widget widget, String text) {
            
            if(!(widget instanceof LabelWidget))
                return;
            ((LabelWidget)widget).setLabel(text);
            helper.setPackagePath(text);
            helper.save();
        }
    }
    
    private class NamespaceInPlaceEditor implements TextFieldInplaceEditor {

        public boolean isEnabled(Widget widget) {
            return true;
        }

        public String getText(Widget widget) {
            if(widget instanceof LabelWidget) {
                return ((LabelWidget)widget).getLabel();
            }
            return null;
        }

        public void setText(Widget widget, String text) {
            
            if(!(widget instanceof LabelWidget))
                return;
            ((LabelWidget)widget).setLabel(text);
            helper.setNamespace(text);
            helper.save();
        }
    }
    
    private class GenerateAction extends AbstractAction {
        
        private FileObject serviceXmlFileObject;
        public GenerateAction(FileObject serviceXmlFileObject) {
            this.serviceXmlFileObject = serviceXmlFileObject;
        }

        public void actionPerformed(ActionEvent e) {
            GenerateServiceHelper.getInstance().generateService(serviceXmlFileObject);
        }
    }
}

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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.workflow.editor.view.design;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import org.netbeans.api.visual.action.AcceptProvider;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.model.ObjectScene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.workflow.editor.palette.WorkflowActiveEditorDrop;
import org.openide.nodes.Node;

/**
 *
 * @author radval
 */
public class MainScene extends ObjectScene {

    public MainScene() {
        WidgetAction acceptAction = ActionFactory.createAcceptAction(new SceneAcceptProvider());
        this.getActions().addAction(acceptAction);
    }
    
    private class SceneAcceptProvider implements AcceptProvider {

        public ConnectorState isAcceptable(Widget widget, Point point, Transferable transferable) {
            return ConnectorState.ACCEPT;
        }

        public void accept(Widget widget, Point point, Transferable transferable) {
            if(widget instanceof MainScene) {
                if (transferable != null) {
                    for (DataFlavor flavor : transferable.getTransferDataFlavors()) {
                        Class repClass = flavor.getRepresentationClass();
                        if (WorkflowActiveEditorDrop.class.isAssignableFrom(repClass)) {
                            try {
                                WorkflowActiveEditorDrop drop = (WorkflowActiveEditorDrop) transferable.getTransferData(flavor);
                                String componentName = drop.getComponentName();
                                if(componentName != null) {
                                    WidgetFactory factory = WidgetFactory.getDefault(MainScene.this);
                                    Widget newWidget = factory.createWidget(componentName);
                                    if(newWidget != null) {
                                        newWidget.setPreferredLocation(point);
                                        widget.addChild(newWidget);
                                        widget.getScene().validate();
                                        break;
                                    }
                                }
                            } catch(Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
                /*
                Widget child = new StartEventWidget(MainScene.this, null);
                child.setPreferredLocation(point);
                widget.addChild(child);*/
            }
        }
        
    }
    
    
}

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
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JToolBar;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.model.ObjectScene;
import org.netbeans.api.visual.model.ObjectSceneListener;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder;
import org.openide.util.NbBundle;
/**
 *
 * @author satyaranjan
 */
public class FinderColumnsWidget extends AbstractTitledWidget implements TabWidget {

        private transient Finder finder;
        private transient Widget buttons;
        private transient ImageLabelWidget headerLabelWidget;
        private transient TableModel model;
        private transient TableWidget finderColumnTable;
        private transient Widget tabComponent;

        /** 
         * Creates a new instance of OperationWidget 
         * @param scene 
         * @param method 
         */
        public FinderColumnsWidget(ObjectScene scene, Finder finder) {
            super(scene, 0, RADIUS, 0, BORDER_COLOR);
            this.finder = finder;
            createContent();
        }

        protected Paint getTitlePaint(Rectangle bounds) {
            return TITLE_COLOR_PARAMETER;
        }

        private void createContent() {
            model = new FinderColumnTableModel(finder);
            populateContentWidget(getContentWidget());
            getContentWidget().setBorder(BorderFactory.createEmptyBorder(0, 1, 1, 1));
            headerLabelWidget = new ImageLabelWidget(getScene(), getIcon(), getTitle(),
                    "(" + ")");
            headerLabelWidget.setLabelFont(getScene().getFont().deriveFont(Font.BOLD));
            getHeaderWidget().addChild(new Widget(getScene()), 5);
            getHeaderWidget().addChild(headerLabelWidget);
            getHeaderWidget().addChild(new Widget(getScene()), 4);

            buttons = new Widget(getScene());
            buttons.setLayout(LayoutFactory.createHorizontalFlowLayout(
                    LayoutFactory.SerialAlignment.CENTER, 8));

            buttons.addChild(getExpanderWidget());
            buttons.setOpaque(true);
            buttons.setBackground(TITLE_COLOR_BRIGHT);

            getHeaderWidget().addChild(buttons);

        }

        private void populateContentWidget(Widget parentWidget) {
            if (model.getRowCount() > 0) {
                finderColumnTable = new TableWidget(getScene(), model);
                parentWidget.addChild(finderColumnTable);
            } else {
                LabelWidget noParamsWidget = new LabelWidget(getScene(),
                        NbBundle.getMessage(OperationWidget.class, "LBL_ColumnsNone"));
                noParamsWidget.setAlignment(LabelWidget.Alignment.CENTER);
                parentWidget.addChild(noParamsWidget);
            }
        }

        public Object hashKey() {
            return model;
        }

        public String getTitle() {
            return NbBundle.getMessage(OperationWidget.class, "LBL_Columns");
        }

        public Image getIcon() {
            return null;
//        return Utilities.loadImage
//            ("org/netbeans/modules/websvc/design/view/resources/input.png");
        }

        public Widget getComponentWidget() {
            if (tabComponent == null) {
                tabComponent = createContentWidget();
                populateContentWidget(tabComponent);
            }
            return tabComponent;
        }
    }

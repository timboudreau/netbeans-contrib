/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package test.alignwith;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import test.SceneSupport;

import java.awt.*;
import javax.swing.JComboBox;
import javax.swing.JList;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.ComponentWidget;

/**
 * @author Tomas Holy
 */

/** Test for issue #157951 */
public class AlignCompositeWidgetTest extends Scene {

    private LayerWidget mainLayer;
    private WidgetAction moveAction;
    private WidgetAction resizeAction;

    public AlignCompositeWidgetTest() {
        setBackground(Color.WHITE);

        mainLayer = new LayerWidget(this);
        addChild(mainLayer);

        LayerWidget interractionLayer = new LayerWidget(this);
        addChild(interractionLayer);

        resizeAction = ActionFactory.createAlignWithResizeAction(mainLayer, interractionLayer, null, false);
        moveAction = ActionFactory.createAlignWithMoveAction(mainLayer, interractionLayer, null, false);

        createCompositeWidget();
    }
    Widget panel;

    void createCompositeWidget() {
        panel = new Widget(this);
        panel.setOpaque(true);
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setPreferredBounds(new Rectangle(50, 50, 400, 300));
        panel.setBorder(BorderFactory.createResizeBorder(4));
        panel.getActions().addAction(resizeAction);
        panel.getActions().addAction(moveAction);
        mainLayer.addChild(panel);

        panel.addChild(createMoveableComponent(new JComboBox(new String[]{"First", "Second", "Third"}), 150, 150));
        panel.addChild(createMoveableComponent(new JList(new String[]{"First", "Second", "Third"}), 150, 200));
    }

    private Widget createMoveableComponent(Component component, int x, int y) {
        Widget widget = new Widget(this);
        widget.setLayout(LayoutFactory.createVerticalFlowLayout());
        widget.setBorder(BorderFactory.createLineBorder());
        widget.getActions().addAction(moveAction);

        LabelWidget label = new LabelWidget(this, "Drag this to move widget");
        label.setOpaque(true);
        label.setBackground(Color.LIGHT_GRAY);
        widget.addChild(label);

        ComponentWidget componentWidget = new ComponentWidget(this, component);
        widget.addChild(componentWidget);
        widget.setPreferredLocation(new Point(x, y));
        widget.getActions().addAction(resizeAction);
        widget.getActions().addAction(moveAction);

        return widget;
    }

    public static void main(String[] args) {
        SceneSupport.show(new AlignCompositeWidgetTest());
    }
}

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

package test.border;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JFrame;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author vogler-florian
 */
public class LineBorderTest {

    public static void main(String[] args) {

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

        Container contentPane = frame.getContentPane();
        final Scene scene = new Scene();
        contentPane.add(scene.createView());
        frame.setVisible(true);
        buildScene(scene);
    }

    private static void buildScene(Scene scene) {
        scene.getPriorActions().addAction(ActionFactory.createZoomAction());
        scene.setZoomFactor(4.0);
        WidgetAction moveAction = ActionFactory.createMoveAction();

        LayerWidget layerWidget = new LayerWidget(scene);
        scene.addChild(layerWidget);
        StateWidget stateWidget = new StateWidget(scene);
        stateWidget.getActions().addAction(moveAction);
        stateWidget.setPreferredLocation(new Point(25, 25));

        layerWidget.addChild(stateWidget);

        scene.validate();
    }

    private static class StateWidget extends Widget {

        private final Border stateBorder;

        public StateWidget(Scene scene) {
            super(scene);
            stateBorder = BorderFactory.createLineBorder(1, Color.RED);
            setPreferredSize(new Dimension(60, 50));
            setBorder(BorderFactory.createLineBorder(5, Color.BLUE));
            setOpaque(true);
            setBackground(Color.lightGray);
            setCheckClipping(true);
        }

        @Override
        protected void paintBackground() {
            super.paintBackground();
            Rectangle clientArea = getClientArea();
            if (stateBorder.isOpaque()) {
                stateBorder.paint(getGraphics(), new Rectangle(clientArea));
            }
        }

        @Override
        protected void paintWidget() {
            super.paintWidget();
            Graphics2D graphics = getGraphics();
            Rectangle clientArea = getClientArea();
            graphics.drawLine(clientArea.x, clientArea.y, clientArea.x + clientArea.width, clientArea.y + clientArea.height);
            graphics.drawLine(clientArea.x + clientArea.width, clientArea.y, clientArea.x, clientArea.y + clientArea.height);
        }
    }
}

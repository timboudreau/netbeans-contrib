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

package test.multiview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.ComponentWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;


public class SatelliteViewTest {
 private final WidgetAction moveAction = ActionFactory.createMoveAction();
    private Scene scene;

    public SatelliteViewTest() {
        scene = new Scene();
        scene.getActions().addAction(ActionFactory.createZoomAction());

        LayerWidget layer = new LayerWidget(scene);
        scene.addChild(layer);

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200, 200));
        panel.setBackground(Color.blue);

        JPanel inner = new JPanel();
        inner.setPreferredSize(new Dimension(50, 50));
        inner.setBackground(Color.red);

        panel.add(inner);
        layer.addChild(createMoveableComponent(panel));

        JComponent sceneView = scene.getView();
        if (sceneView == null)
            sceneView = scene.createView();
        showFrame(sceneView, scene.createSatelliteView());
    }

    private Widget createMoveableComponent(Component component) {
        ComponentWidget componentWidget = new ComponentWidget(scene, component);
        componentWidget.getActions().addAction(moveAction);
        componentWidget.setPreferredLocation(new Point(100, 100));
        return componentWidget;
    }

    private void showFrame(JComponent view, JComponent satelliteView) {
        JScrollPane panel = new JScrollPane(view);
        int width = 800, height = 600;
        JFrame frame = new JFrame();
        frame.add(satelliteView, BorderLayout.WEST);
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds((screenSize.width - width) / 2, (screenSize.height - height) / 2, width, height);
        frame.setVisible(true);
    }


    public static void main(String[] args) {
        new SatelliteViewTest();
    }
}

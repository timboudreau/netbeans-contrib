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
package test.graphlayout;

import java.awt.Color;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.EditProvider;
import org.netbeans.api.visual.action.TwoStateHoverProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.graph.layout.GraphLayout;
import org.netbeans.api.visual.graph.layout.GraphLayoutFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.layout.SceneLayout;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import test.SceneSupport;

/**
 * @author Tomas Holy
 */
public class OrthogonalGraphLayoutTest extends GraphScene.StringGraph {

    LayerWidget mainLayer;
    LayerWidget connectionLayer;
    WidgetAction moveAction = ActionFactory.createMoveAction();
    WidgetAction mouseHoverAction = ActionFactory.createHoverAction(new MyHoverProvider());

    public OrthogonalGraphLayoutTest() {
        mainLayer = new LayerWidget(this);
        connectionLayer = new LayerWidget(this);
        addChild(mainLayer);
        addChild(connectionLayer);

        getActions().addAction(mouseHoverAction);
        GraphLayout<String, String> graphLayout = GraphLayoutFactory.createOrthogonalGraphLayout(this, true);
        final SceneLayout sceneGraphLayout = LayoutFactory.createSceneGraphLayout(this, graphLayout);
        getActions().addAction(ActionFactory.createEditAction(new EditProvider() {

            public void edit(Widget widget) {
                sceneGraphLayout.invokeLayoutImmediately();
            }
        }));
    }

    protected Widget attachNodeWidget(String node) {
        LabelWidget widget = new LabelWidget(this, node);
        mainLayer.addChild(widget);
        widget.getActions().addAction(moveAction);
        widget.getActions().addAction(mouseHoverAction);
        return widget;
    }

    protected Widget attachEdgeWidget(String edge) {
        ConnectionWidget connectionWidget = new ConnectionWidget(this);
        connectionLayer.addChild(connectionWidget);
        return connectionWidget;
    }

    protected void attachEdgeSourceAnchor(String edge, String oldSourceNode, String sourceNode) {
        ((ConnectionWidget) findWidget(edge)).setSourceAnchor(AnchorFactory.createRectangularAnchor(findWidget(sourceNode)));
    }

    protected void attachEdgeTargetAnchor(String edge, String oldTargetNode, String targetNode) {
        ((ConnectionWidget) findWidget(edge)).setTargetAnchor(AnchorFactory.createRectangularAnchor(findWidget(targetNode)));
    }

    void addEdge(String from, String to) {
        String edge = from + "->" + to;
        addEdge(edge);
        setEdgeSource(edge, from);
        setEdgeTarget(edge, to);
    }

    private static class MyHoverProvider implements TwoStateHoverProvider {

        public void unsetHovering(Widget widget) {
            widget.setBackground(Color.WHITE);
        }

        public void setHovering(Widget widget) {
            widget.setBackground(Color.CYAN);
        }
    }

    public static void main(String[] args) {
        OrthogonalGraphLayoutTest scene = new OrthogonalGraphLayoutTest();

        for (int i = 42; i <= 54; i++) {
            scene.addNode("ABC" + i);
            if (i > 42) {
                scene.addEdge("ABC" + (i - 1), "ABC" + i);
            }
        }
        for (int i = 1; i <= 5; i++) {
            scene.addNode("DEFGHIJKLMN" + i);
            if (i > 1) {
                scene.addEdge("DEFGHIJKLMN" + (i - 1), "DEFGHIJKLMN" + i);
            }
        }
        scene.addEdge("ABC44", "DEFGHIJKLMN1");
        scene.addEdge("ABC49", "DEFGHIJKLMN3");
        scene.addEdge("ABC53", "DEFGHIJKLMN5");
        SceneSupport.show(scene);
    }
}

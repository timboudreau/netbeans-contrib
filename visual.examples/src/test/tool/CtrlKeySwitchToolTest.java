package test.tool;

import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import test.SceneSupport;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author David Kaspar
 */
public class CtrlKeySwitchToolTest {

    private static final String MOVE_TOOL = "moveTool";

    public static void main (String[] args) {
        final Scene scene = new Scene ();

        LayerWidget layer = new LayerWidget (scene);
        scene.addChild (layer);

        createLabel (layer, "You can move this widget only if the Ctrl key is pressed.", 10, 10);
        createLabel (layer, "The CtrlKeySwitchToolAction is switching an action tool of the scene based on the Ctrl key state.", 10, 30);
        createLabel (layer, "The action is assigned as a prior action to the scene and therefore it is executed before any other action.", 10, 50);

        scene.getPriorActions ().addAction (new CtrlKeySwitchToolAction ());

        SceneSupport.show (scene);
    }

    private static void createLabel (LayerWidget layer, String text, int x, int y) {
        LabelWidget label = new LabelWidget (layer.getScene (), text);
        label.setPreferredLocation (new Point (x, y));
        label.createActions (MOVE_TOOL).addAction (ActionFactory.createMoveAction ());
        layer.addChild (label);
    }

    private static final class CtrlKeySwitchToolAction extends WidgetAction.Adapter {

        public State keyPressed (Widget widget, WidgetKeyEvent event) {
            if (event.getKeyCode () == KeyEvent.VK_CONTROL)
                widget.getScene ().setActiveTool (MOVE_TOOL);
            return State.REJECTED;
        }

        public State keyReleased (Widget widget, WidgetKeyEvent event) {
            if (event.getKeyCode () == KeyEvent.VK_CONTROL)
                widget.getScene ().setActiveTool (null);
            return State.REJECTED;
        }
    }

}

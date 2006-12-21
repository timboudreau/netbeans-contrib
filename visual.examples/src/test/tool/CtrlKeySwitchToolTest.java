package test.tool;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import test.SceneSupport;

import java.awt.event.KeyEvent;
import java.awt.*;

/**
 * @author David Kaspar
 */
public class CtrlKeySwitchToolTest {

    private static final String MOVE_TOOL = "moveTool";

    private static final WidgetAction filteredMoveAction = new CtrlKeyFilterAction (ActionFactory.createMoveAction ());

    public static void main (String[] args) {
        final Scene scene = new Scene ();

        LayerWidget layer = new LayerWidget (scene);
        scene.addChild (layer);

        createLabel (layer, "You can move this widget only if the Ctrl key is pressed.", 10, 10);
        createLabel (layer, "The action assigned to the scene is switching action-tool.", 10, 30);
        createLabel (layer, "Unfortunately any action may locks event processing for itself.", 10, 50);
        createLabel (layer, "Therefore each locking-able action has to be filtered for ctrl-key state.", 10, 70);
        createLabel (layer, "CtrlKeyKeyAction is checking the ctrl-state and then forward events to the original action as if nothing happened.", 10, 90);

        scene.getActions ().addAction (new WidgetAction.Adapter () {
            public State keyPressed (Widget widget, WidgetKeyEvent event) {
                if (event.getKeyCode () == KeyEvent.VK_CONTROL) {
                    scene.setActiveTool (MOVE_TOOL);
                    return State.CONSUMED;
                }
                return State.REJECTED;
            }
            public State keyReleased (Widget widget, WidgetKeyEvent event) {
                if (event.getKeyCode () == KeyEvent.VK_CONTROL) {
                    scene.setActiveTool (null);
                    return State.CONSUMED;
                }
                return State.REJECTED;
            }
        });

        SceneSupport.show (scene);
    }

    private static void createLabel (LayerWidget layer, String text, int x, int y) {
        LabelWidget label = new LabelWidget (layer.getScene (), text);
        label.setPreferredLocation (new Point (x, y));
        label.createActions (MOVE_TOOL).addAction (filteredMoveAction);
        layer.addChild (label);
    }

    private static final class CtrlKeyFilterAction extends FilterAction {

        public CtrlKeyFilterAction (WidgetAction originalAction) {
            super (originalAction);
        }

        public State keyPressed (Widget widget, WidgetKeyEvent event) {
            if (event.getKeyCode () == KeyEvent.VK_CONTROL)
                widget.getScene ().setActiveTool (MOVE_TOOL);
            return processState (originalAction.keyPressed (widget, event));
        }

        public State keyReleased (Widget widget, WidgetKeyEvent event) {
            if (event.getKeyCode () == KeyEvent.VK_CONTROL)
                widget.getScene ().setActiveTool (null);
            return processState (originalAction.keyReleased (widget, event));
        }
    }

}

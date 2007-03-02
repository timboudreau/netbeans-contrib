package test.alignwith;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import test.SceneSupport;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class AlignWithMoveGuideLinesTest {

    public static void main (String[] args) {
        Scene scene = new Scene ();

        LayerWidget main = new LayerWidget (scene);
        scene.addChild (main);

        LayerWidget inter = new LayerWidget (scene);
        scene.addChild (inter);

        createWidget (main, "Alignment 1", 400, 0, 100, 100);
        createWidget (main, "Alignment 2", 0, 400, 100, 100);
        LabelWidget widget = createWidget (main, "Move this to top-left corner and align it", 10, 10, 200, 100);
        widget.getActions ().addAction (ActionFactory.createAlignWithMoveAction (main, inter, null));

        SceneSupport.show (scene);
    }

    private static LabelWidget createWidget (LayerWidget main, String label, int x, int y, int width, int height) {
        LabelWidget widget = new LabelWidget (main.getScene (), label);
        widget.setBorder (BorderFactory.createResizeBorder (8));
        widget.setPreferredLocation (new Point (x, y));
        widget.setPreferredSize (new Dimension (width, height));
        main.addChild (widget);
        return widget;
    }

}

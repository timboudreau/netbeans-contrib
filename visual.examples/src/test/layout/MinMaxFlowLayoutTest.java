package test.layout;

import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.EditProvider;
import test.SceneSupport;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class MinMaxFlowLayoutTest {

    public static void main (String[] args) {
        Scene scene = new Scene ();

        Widget w1 = new Widget (scene);
        w1.setBorder (BorderFactory.createLineBorder (1, Color.RED));
        w1.setLayout (LayoutFactory.createVerticalFlowLayout ());
//        w1.setPreferredSize (new Dimension (100, 100)); // uncommenting this forces the size and therefore the outer widget is smaller than widgets inside
        scene.addChild (w1);

        LabelWidget w2 = new LabelWidget (scene, "Double-click me to toggle visibility of widget below");
        w2.setBorder (BorderFactory.createLineBorder (1, Color.GREEN));
        w1.addChild (w2);

        final LabelWidget w3 = new LabelWidget (scene, "This is a big label with predefined minimal size");
        w3.setBorder (BorderFactory.createLineBorder (1, Color.BLUE));
        w3.setMinimumSize (new Dimension (400, 100));
        w1.addChild (w3);

        w2.getActions ().addAction (ActionFactory.createEditAction (new EditProvider() {
            public void edit (Widget widget) {
                w3.setVisible (! w3.isVisible ());
            }
        }));

        SceneSupport.show (scene);
    }

}

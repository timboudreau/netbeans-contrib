package test.constraint;

import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.layout.Layout;
import test.SceneSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.awt.*;

/**
 * @author David Kaspar
 */
public class ConstraintsTest {

    private static final String[] unsortedLabels = new String[] {
        "Item - This is the 2. line.",
        "Screen - This is the 4. line.",
        "Exit - This is the 1. line.",
        "Ok - This is the 3. line."
    };

    public static void main (String[] args) {
        Scene scene = new Scene ();
        for (String label : unsortedLabels)
            scene.addChild (new LabelWidget (scene, label), label);
        scene.setLayout (new AlphabeticalLayout ());
        SceneSupport.show (scene);
    }

    private static final class AlphabeticalLayout implements Layout {

        public void layout (final Widget widget) {
            ArrayList<Widget> widgets = new ArrayList<Widget> (widget.getChildren ());
            Collections.sort (widgets, new Comparator<Widget>() {
                public int compare (Widget o1, Widget o2) {
                    return ((String) widget.getChildConstraint (o1)).compareTo ((String) widget.getChildConstraint (o2));
                }
            });
            int y = 0;
            for (Widget child : widgets) {
                y += 20;
                child.resolveBounds (new Point (10, y), null);
            }
        }

        public boolean requiresJustification (Widget widget) {
            return false;
        }

        public void justify (Widget widget) {
        }
    }

}

package test.widget;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.EditProvider;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.general.IconNodeWidget;
import org.openide.util.Utilities;
import test.SceneSupport;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class IconNodeHeaderTest {

    public static void main (String[] args) {
        Scene scene = new Scene ();

        LayerWidget layer = new LayerWidget (scene);
        scene.addChild (layer);

        LayerWidget theWidgetWithHeader = new LayerWidget (scene);
        theWidgetWithHeader.setPreferredLocation (new Point (100, 100)); // the reference point of the whole structure
        layer.addChild (theWidgetWithHeader);

        final LabelWidget header = new LabelWidget (scene, "Visibility of this label should not affect the position of the top-left corner of the image in the scene.");
        header.setPreferredLocation (new Point (0, 0)); // the location of the header relatively to the reference point 
        theWidgetWithHeader.addChild (0, header);

        IconNodeWidget widget = new IconNodeWidget (scene, IconNodeWidget.TextOrientation.BOTTOM_CENTER);
        widget.setImage (Utilities.loadImage ("test/resources/displayable_64.png")); // NOI18N
        widget.setLabel ("Double-click me to change the visibility of the header");
        widget.setPreferredLocation (new Point (0, 0)); // the location of icon node widget relatively to the reference point
        theWidgetWithHeader.addChild (widget);

        widget.getActions ().addAction (ActionFactory.createEditAction (new EditProvider() {
            public void edit (Widget widget) {
                header.setVisible (! header.isVisible ());
            }
        }));

        SceneSupport.show (scene);
    }

}

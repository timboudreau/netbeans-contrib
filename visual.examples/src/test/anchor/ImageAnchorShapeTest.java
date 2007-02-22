package test.anchor;

import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShapeFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Scene;
import org.openide.util.Utilities;
import test.SceneSupport;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class ImageAnchorShapeTest {

    public static void main (String[] args) {
        Scene scene = new Scene ();

        ConnectionWidget child = new ConnectionWidget (scene);
        child.setSourceAnchor (AnchorFactory.createFixedAnchor (new Point (100, 100)));
        child.setTargetAnchor (AnchorFactory.createFixedAnchor (new Point (200, 200)));
        child.setSourceAnchorShape (AnchorShapeFactory.createImageAnchorShape (Utilities.loadImage ("test/resources/custom_displayable_32.png"), true)); // NOI18N
        child.setTargetAnchorShape (AnchorShapeFactory.createImageAnchorShape (Utilities.loadImage ("test/resources/custom_displayable_32.png"), true)); // NOI18N
        scene.addChild (child);

        SceneSupport.show (scene);
    }

}

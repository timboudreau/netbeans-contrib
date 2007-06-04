package test.widget;

import org.netbeans.api.visual.widget.ImageWidget;
import org.netbeans.api.visual.widget.Scene;
import test.SceneSupport;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class AnimatedImageTest {

    public static void main (String[] args) {
        Scene scene = new Scene ();
        ImageWidget widget = new ImageWidget (scene, Toolkit.getDefaultToolkit ().createImage (AnimatedImageTest.class.getClassLoader ().getResource ("test/resources/animated-image.gif"))); // NOI18N
        // HINT - do not use Utilities.loadImage for loading animated images
//        ImageWidget widget = new ImageWidget (scene, Utilities.loadImage ("test/widget/a.gif")); // NOI18N
        scene.addChild (widget);
        SceneSupport.show (scene);
    }

}

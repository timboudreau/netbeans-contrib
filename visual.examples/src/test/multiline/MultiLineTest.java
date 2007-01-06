package test.multiline;

import org.netbeans.api.visual.widget.ComponentWidget;
import org.netbeans.api.visual.widget.Scene;
import test.SceneSupport;

import javax.swing.*;

/**
 * @author David Kaspar
 */
public class MultiLineTest {

    public static void main (String[] args) {
        Scene scene = new Scene ();
        JLabel label = new JLabel ("<html>My long multi-line text.<br>Next line.");
        ComponentWidget widget = new ComponentWidget (scene, label);
        scene.addChild (widget);
        SceneSupport.show (scene);
    }

}

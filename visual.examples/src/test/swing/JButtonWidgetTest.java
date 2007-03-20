package test.swing;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import test.SceneSupport;

import javax.swing.*;
import java.awt.*;

/**
 * @author David Kaspar
 */
// TODO - the JButton does not receive mouse/key events and therefore it does not react on anything - JButton is just rendered
public class JButtonWidgetTest extends Widget {

    private JButton button = new JButton ();

    public JButtonWidgetTest (Scene scene) {
        super (scene);
    }

    public JButton getButton () {
        return button;
    }

    protected Rectangle calculateClientArea () {
        return new Rectangle (button.getPreferredSize ());
    }

    protected void paintWidget () {
        button.setSize (getBounds ().getSize ());
        button.paint (getGraphics ());
    }

    public static void main (String[] args) {
        Scene scene = new Scene ();
        scene.getActions ().addAction (ActionFactory.createZoomAction ());

        JButtonWidgetTest button = new JButtonWidgetTest (scene);
        button.getButton ().setText ("My Button");
        scene.addChild (button);

        SceneSupport.show (scene);
    }

}

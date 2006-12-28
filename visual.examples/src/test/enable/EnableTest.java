package test.enable;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.widget.ComponentWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import test.SceneSupport;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author David Kaspar
 */
public class EnableTest {

    public static void main (String[] args) {
        Scene scene = new Scene ();
        LayerWidget layer = new LayerWidget (scene);
        scene.addChild (layer);

        final LabelWidget label = new LabelWidget (scene, "You can move this label by dragging. To disable/enable the label, press the button.");
        label.setPreferredLocation (new Point (100, 50));
        label.getActions ().addAction (ActionFactory.createMoveAction ());
        layer.addChild (label);

        final JToggleButton button = new JToggleButton ("Press this button to disable/enable the label. When disabled, then it cannot be moved.", true);
        button.addActionListener (new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                boolean b = ! label.isEnabled ();
                label.setEnabled (b);
                button.setSelected (b);
            }
        });
        ComponentWidget componentWidget = new ComponentWidget (scene, button);
        componentWidget.setPreferredLocation (new Point (100, 100));
        layer.addChild (componentWidget);

        SceneSupport.show (scene);

    }

}

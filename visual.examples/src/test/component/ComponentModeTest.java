package test.component;

import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.ComponentWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.SeparatorWidget;
import test.SceneSupport;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author David Kaspar
 */
public class ComponentModeTest {

    public static void main (String[] args) {
        final Scene scene = new Scene ();
        scene.setBorder (BorderFactory.createEmptyBorder (10));
        scene.setLayout (LayoutFactory.createVerticalLayout (LayoutFactory.SerialAlignment.JUSTIFY, 10));


        JTextField textField = new JTextField ("Text for editing - try to edit me. When the JTextField component is hidden, then the Widget just renders it.");
        final ComponentWidget textFieldWidget = new ComponentWidget (scene, textField);

        JToggleButton button = new JToggleButton ("Click to hide/show JTextField component bellow. The ComponentWidget is still in the scene and rendered.");
        button.addActionListener (new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                textFieldWidget.setComponentVisible (! textFieldWidget.isComponentVisible ());
                scene.validate ();
            }
        });

        scene.addChild (new ComponentWidget (scene, button));
        scene.addChild (textFieldWidget);

        SeparatorWidget separator = new SeparatorWidget (scene, SeparatorWidget.Orientation.HORIZONTAL);
        scene.addChild (separator);

        JTextField textField2 = new JTextField ("Text for editing - try to edit me.");
        final ComponentWidget textFieldWidget2 = new ComponentWidget (scene, textField2);

        JToggleButton button2 = new JToggleButton ("Click to remove/add ComponentWidget from/to the scene.");
        button2.addActionListener (new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                if (textFieldWidget2.getParentWidget () != null)
                    scene.removeChild (textFieldWidget2);
                else
                    scene.addChild (textFieldWidget2);
                scene.validate ();
            }
        });

        scene.addChild (new ComponentWidget (scene, button2));
        scene.addChild (textFieldWidget2);

        SceneSupport.show (scene);
        // TODO - call detach method on all ComponentWidget to prevent memory leaks
    }

}

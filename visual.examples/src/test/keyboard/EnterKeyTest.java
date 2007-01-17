package test.keyboard;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.TextFieldInplaceEditor;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author David Kaspar
 */
public class EnterKeyTest {

    public static void main (String[] args) {
        Scene scene = new Scene ();

        LabelWidget label = new LabelWidget (scene, "This is a label");
        scene.addChild (label);
        label.getActions ().addAction (ActionFactory.createInplaceEditorAction (new TextFieldInplaceEditor() {
            public boolean isEnabled (Widget widget) {
                return true;
            }
            public String getText (Widget widget) {
                return ((LabelWidget) widget).getLabel ();
            }
            public void setText (Widget widget, String text) {
                ((LabelWidget) widget).setLabel (text);
            }
        }));

        JComponent view = scene.createView ();

        final JDialog dialog = new JDialog ();
        dialog.setSize (200, 100);
        dialog.setLayout (new BorderLayout ());
        dialog.add (view, BorderLayout.CENTER);

        JButton button = new JButton ("Close");
        button.setDefaultCapable (true);
        button.addActionListener (new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                dialog.setVisible (false);
                dialog.dispose ();
            }
        });
        dialog.add (button, BorderLayout.SOUTH);

        dialog.setVisible (true);
    }

}

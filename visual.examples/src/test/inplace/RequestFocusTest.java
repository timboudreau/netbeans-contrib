package test.inplace;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.TextFieldInplaceEditor;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import test.SceneSupport;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * @author David Kaspar
 */
public class RequestFocusTest {

    public static void main (String[] args) {
        JPanel panel = new JPanel ();
        panel.setLayout (new BorderLayout ());

        JToolBar toolbar = new JToolBar ();
        panel.add (toolbar, BorderLayout.NORTH);

        final JButton button = new JButton ("Press me while you are in in-place editing. The focus should be on the button.");
        button.addActionListener (new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                JOptionPane.showMessageDialog (button, "The actionPerformed method is called, so everything is ok.");
            }
        });
        toolbar.add (button);

        Scene scene = new Scene ();

        LabelWidget label = new LabelWidget (scene, "Double-click on this label to invoke edit, then click on the icon in toolbar to trace the focus behaviour.");
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
        scene.addChild (label);

        JComponent view = scene.createView ();
        panel.add (view, BorderLayout.CENTER);

        SceneSupport.showCore (panel);
    }

}

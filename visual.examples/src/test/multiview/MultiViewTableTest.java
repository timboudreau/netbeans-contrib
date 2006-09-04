package test.multiview;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.widget.ComponentWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * @author David Kaspar
 */
public class MultiViewTableTest {

    public static void main (String[] args) {
        Scene scene = new Scene ();

        LayerWidget layer = new LayerWidget (scene);
        scene.addChild (layer);

        JTable table = createTable ();
        ComponentWidget widget = new ComponentWidget (scene, new JScrollPane (table));
        widget.setBorder (BorderFactory.createResizeBorder (10));
        widget.setPreferredLocation (new Point (100, 100));
        widget.getActions ().addAction (ActionFactory.createResizeAction ());
        layer.addChild (widget);

        MultiViewTest.show (scene);
    }

    private static JTable createTable () {
        JTable table = new JTable ();
        table.setModel (new DefaultTableModel (new Object[][] {
                {"11", "12"},
                {"21", "22"}
        }, new Object[] {
                "First", "Second"
        }));
        return table;
    }

}

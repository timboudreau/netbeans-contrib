package test.visible;

import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.border.BorderFactory;
import test.SceneSupport;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class NotifyAddedRemovedTest extends Widget {

    private String label;

    public NotifyAddedRemovedTest (Scene scene, String label) {
        super (scene);
        this.label = label;
    }

    protected void notifyAdded () {
        System.out.println ("ADDED: " + label);
    }

    protected void notifyRemoved () {
        System.out.println ("REMOVED: " + label);
    }

    public static void main (String[] args) {
        Scene scene = new Scene ();

        final NotifyAddedRemovedTest w1 = new NotifyAddedRemovedTest (scene, "Level 1");
        scene.addChild (w1);

        final NotifyAddedRemovedTest w11 = new NotifyAddedRemovedTest (scene, "Level 1-1");
        w1.addChild (w11);

        final NotifyAddedRemovedTest w111 = new NotifyAddedRemovedTest (scene, "Level 1-1-1");
        w11.addChild (w111);

        final NotifyAddedRemovedTest w12 = new NotifyAddedRemovedTest (scene, "Level 1-2");
        w1.addChild (w12);

        final NotifyAddedRemovedTest w2 = new NotifyAddedRemovedTest (scene, "Level 2");
        scene.addChild (w2);

        Widget click = new LabelWidget (scene, "Click me to add/remove 'Level 1-1'");
        click.setBorder (BorderFactory.createLineBorder ());
        click.getActions ().addAction (ActionFactory.createSelectAction (new SelectProvider() {
            public boolean isAimingAllowed (Widget widget, Point localLocation, boolean invertSelection) {
                return false;
            }
            public boolean isSelectionAllowed (Widget widget, Point localLocation, boolean invertSelection) {
                return true;
            }
            public void select (Widget widget, Point localLocation, boolean invertSelection) {
                if (w11.getParentWidget () != null)
                    w11.removeFromParent ();
                else
                    w1.addChild (w11);
            }
        }));
        scene.addChild (click);

        SceneSupport.show (scene);
    }

}

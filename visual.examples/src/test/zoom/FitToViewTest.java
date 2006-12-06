package test.zoom;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.EditProvider;
import org.netbeans.api.visual.layout.SceneLayout;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.LabelWidget;
import test.SceneSupport;
import test.general.StringGraphPinScene;

import javax.swing.*;
import java.awt.*;

/**
 * @author dave
 */
public class FitToViewTest extends StringGraphPinScene {

    private SceneLayout layout;
    private JScrollPane panel;

    public FitToViewTest() {
        layout = new SceneLayout (this) {
            protected void performLayout () {
                Rectangle rectangle = new Rectangle (0, 0, 1, 1);
                for (Widget widget : getChildren ())
                    rectangle = rectangle.union (widget.convertLocalToScene (widget.getBounds ()));
                Dimension dim = rectangle.getSize ();
                Dimension viewDim = panel.getViewportBorderBounds ().getSize ();
                FitToViewTest.this.setZoomFactor (Math.min ((float) viewDim.width / dim.width, (float) viewDim.height / dim.height));
            }
        };

        getActions ().addAction (ActionFactory.createZoomAction ());
        getActions ().addAction (ActionFactory.createPanAction ());
        getActions ().addAction (ActionFactory.createEditAction (new EditProvider() {
            public void edit (Widget widget) {
                layout.invokeLayout ();
            }
        }));

        panel = new JScrollPane (createView ());

        for (int a = 0; a < 10; a ++)
            addNode ("node" + String.valueOf (a)).setPreferredLocation (new Point (SceneSupport.randInt (1000), SceneSupport.randInt (1000)));

        addChild (new LabelWidget (this, "Double-click on the background to zoom the scene to fit the view"));

        SceneSupport.showCore (panel);
    }

    public static void main(String[] args) {
        new FitToViewTest ();
    }

}

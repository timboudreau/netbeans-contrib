package test.expand;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.TwoStateHoverProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.LayerWidget;
import test.SceneSupport;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class MouseOverExpandTest extends Scene {

    private WidgetAction expandAction = ActionFactory.createHoverAction (new ExpandController ());

    public MouseOverExpandTest () {
        setBackground (Color.LIGHT_GRAY);
        LayerWidget layer = new LayerWidget (this);
        addChild (layer);
        getActions ().addAction (expandAction); // required by MouseHoverAction for reseting the hover state

        Widget w;

        w = new ExpandableWidget (this);
        w.setPreferredLocation (new Point (300, 100));
        w.getActions ().addAction (expandAction);
        w.getActions ().addAction (ActionFactory.createMoveAction ());
        layer.addChild (w);

        w = new ExpandableWidget (this);
        w.setPreferredLocation (new Point (100, 200));
        w.getActions ().addAction (expandAction);
        w.getActions ().addAction (ActionFactory.createMoveAction ());
        layer.addChild (w);

        w = new ExpandableWidget (this);
        w.setPreferredLocation (new Point (300, 300));
        w.getActions ().addAction (expandAction);
        w.getActions ().addAction (ActionFactory.createMoveAction ());
        layer.addChild (w);
    }

    public static void main (String[] args) {
        SceneSupport.show (new MouseOverExpandTest ());
    }

    private static class ExpandableWidget extends Widget {

        private boolean expanded = true;
        private Widget detailsWidget;

        public ExpandableWidget (Scene scene) {
            super (scene);
            setLayout (LayoutFactory.createVerticalLayout ());
            setOpaque (true);
            setBackground (Color.WHITE);
            setBorder (BorderFactory.createLineBorder (10));

            addChild (new LabelWidget (scene, "Move mouse cursor over the rectangle to EXPAND it."));

            detailsWidget = new Widget (scene);
            detailsWidget.setLayout (LayoutFactory.createVerticalLayout ());
            detailsWidget.addChild (new LabelWidget (scene, "Drag the rectangle to MOVE it."));
            detailsWidget.addChild (new LabelWidget (scene, "Move mouse cursor out of the rectangle to COLLAPSE it."));
            detailsWidget.setCheckClipping (true); // required to hide the content of details widget beyond its border
            addChild (detailsWidget);

            collapse ();
        }

        public void collapse () {
            if (! expanded)
                return;
            expanded = false;
            // animated, or set it directly using: widget.setPreferredBounds (new Rectangle ());
            getScene ().getSceneAnimator ().animatePreferredBounds (detailsWidget, new Rectangle ());
        }

        public void expand () {
            if (expanded)
                return;
            expanded = true;
            // animated, or set it directly using: widget.setPreferredBounds (null);
            getScene ().getSceneAnimator ().animatePreferredBounds (detailsWidget, null);
        }

    }

    private class ExpandController implements TwoStateHoverProvider {

        public void unsetHovering (Widget widget) {
            if (widget instanceof ExpandableWidget)
                ((ExpandableWidget) widget).collapse ();
        }

        public void setHovering (Widget widget) {
            if (widget instanceof ExpandableWidget)
                ((ExpandableWidget) widget).expand ();
       }

    }

}

package test.router;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import test.SceneSupport;

import java.awt.*;

/**
 * This test describes issue #96460. It should route the connection correctly. The bug happens when the connection is router as directly (not orthogonally).
 * 
 * @author David Kaspar
 */
public class OSRComputeControlPointsTest {

    public static void main (String[] args) {
        Scene scene = new Scene ();
        scene.getActions ().addAction (ActionFactory.createZoomAction (2.0, false));
        scene.getActions ().addAction (ActionFactory.createPanAction ());

        LayerWidget connectionLayer = new LayerWidget (scene);
        scene.addChild (connectionLayer);

        Router router = RouterFactory.createOrthogonalSearchRouter (connectionLayer);

        ConnectionWidget conn1 = new ConnectionWidget (scene);
        conn1.setForeground (new Color (0, 255, 0, 128));
        conn1.setSourceAnchor (new DirAnchor (new Point (100, 200), Anchor.Direction.LEFT));
        conn1.setTargetAnchor (new DirAnchor (new Point (400, 300), Anchor.Direction.RIGHT));
        conn1.setRouter (router);
        connectionLayer.addChild (conn1);

        ConnectionWidget conn3 = new ConnectionWidget (scene);
        conn3.setForeground (new Color (0, 0, 255, 128));
        conn3.setSourceAnchor (new DirAnchor (new Point (100, 300), Anchor.Direction.LEFT));
        conn3.setTargetAnchor (new DirAnchor (new Point (400, 200), Anchor.Direction.RIGHT));
        conn3.setRouter (router);
        connectionLayer.addChild (conn3);

        SceneSupport.show (scene);
    }

    static class DirAnchor extends Anchor {

        private Point location;
        private Direction direction;

        public DirAnchor (Point location, Direction direction) {
            super (null);
            this.location = location;
            this.direction = direction;
        }

        public Point getRelatedSceneLocation () {
            return location;
        }

        public Result compute (Entry entry) {
            return new Result (location, direction);
        }

    }

}

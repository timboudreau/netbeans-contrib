package test.connect;

import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import test.SceneSupport;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class ExtendedConnectTest {

    public static void main (String[] args) {
        Scene scene = new Scene ();
        LayerWidget layer = new LayerWidget (scene);
        scene.addChild (layer);

        final LabelWidget source = new LabelWidget (scene, "Hold Ctrl key and drag this label, then release Ctrl key and move with mouse, the connection must be moving too");
        source.setPreferredLocation (new Point (100, 100));
        layer.addChild (source);

        LayerWidget interractionLayer = new LayerWidget (scene);
        scene.addChild (interractionLayer);

        source.getActions ().addAction (ActionFactory.createExtendedConnectAction (interractionLayer, new ConnectProvider() {
            public boolean isSourceWidget (Widget sourceWidget) {
                return sourceWidget == source;
            }
            public ConnectorState isTargetWidget (Widget sourceWidget, Widget targetWidget) {
                return ConnectorState.REJECT_AND_STOP;
            }
            public boolean hasCustomTargetWidgetResolver (Scene scene) {
                return false;
            }
            public Widget resolveTargetWidget (Scene scene, Point sceneLocation) {
                return null;
            }
            public void createConnection (Widget sourceWidget, Widget targetWidget) {
            }
        }));

        SceneSupport.show (scene);
    }

}

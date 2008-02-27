package org.netbeans.modules.graphicclassview;

import java.awt.Point;
import java.awt.Rectangle;
import org.netbeans.api.visual.layout.Layout;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.graphicclassview.JavaScene.Conn;

public class OppositeSidesLayout
        implements Layout  {

    public OppositeSidesLayout() {
    }

    public void layout(Widget widget) {
        java.util.List kids = widget.getChildren();
        int max = kids.size();
        for (int i = 0; i < max; i++) {
            ((Widget) kids.get(i)).resolveBounds(null, null);
        }

        justify(widget);
    }

    public boolean requiresJustification(Widget widget) {
        return false;
    }

    public void justify(Widget widget) {
        Rectangle r = widget.getClientArea();
        if (r == null) {
            return;
        }
        java.util.List kids = widget.getChildren();
        int max = kids.size();
        for (int i = 0; i < max; i++) {
            if (i % 2 == 0) {
                ((Widget) kids.get(i)).resolveBounds(new Point(0, r.height / 2 - 3), new Rectangle(0, 0, 7, 7));
            } else {
                ((Widget) kids.get(i)).resolveBounds(new Point(r.width - 7, r.height / 2 - 3), new Rectangle(0, 0, 7, 7));
            }
        }

    }
}

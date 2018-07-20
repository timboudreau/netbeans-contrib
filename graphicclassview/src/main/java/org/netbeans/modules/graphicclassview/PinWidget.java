// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PinWidget.java
package org.netbeans.modules.graphicclassview;

import java.awt.*;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

public class PinWidget extends Widget {

    PinWidget(Scene s, String pinName) {
        super(s);
        setPreferredSize(new Dimension(7, 7));
        setToolTipText(pinName);
    }

    @Override
    protected boolean isRepaintRequiredForRevalidating() {
        return true;
    }

    @Override
    protected void paintWidget() {
        Graphics2D g = getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        Rectangle r = getBounds();
        int sz = 7;
        int x = r.width / 2 - sz / 2;
        int y = r.height / 2 - sz / 2;
        g.setColor(Color.WHITE);
        g.fillRect(x, y, sz, sz);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(x, y, sz, sz);
    }
}

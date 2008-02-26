package org.netbeans.modules.graphicclassview;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.CellRendererPane;
import javax.swing.JEditorPane;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.*;

class SharedTextWidget extends Widget {
    JEditorPane pane;
    CellRendererPane crp;
    ImageWidget image;
    Widget header;
    LabelWidget zoomIn;
    LabelWidget zoomOut;
    double zoom;

    SharedTextWidget(JavaScene scene) {
        super(scene);
        pane = new JEditorPane();
        crp = new CellRendererPane();
        zoom = 0.90D;
        setLayout(LayoutFactory.createVerticalFlowLayout());
        header = new Widget(scene);
        header.setLayout(LayoutFactory.createHorizontalFlowLayout());
        addChild(header);
        zoomIn = new LabelWidget(scene, " + ");
        zoomOut = new LabelWidget(scene, " - ");
        zoomIn.setFont(pane.getFont());
        zoomOut.setFont(pane.getFont());
        zoomIn.setBorder(BorderFactory.createCompositeBorder(new Border[]{
            BorderFactory.createEmptyBorder(0, 3, 0, 3), BorderFactory.createLineBorder(1, new Color(0, 165, 0))
        }));
        zoomOut.setBorder(BorderFactory.createLineBorder(1, new Color(0, 165, 0)));
        header.setBorder(BorderFactory.createOpaqueBorder(5, 0, 5, 0));
        ZoomAction a = new ZoomAction();
        zoomIn.getActions().addAction(a);
        zoomOut.getActions().addAction(a);
        header.addChild(zoomIn);
        header.addChild(zoomOut);
        image = new ImageWidget(scene);
        image.setBorder(BorderFactory.createLineBorder(0, 1, 1, 1, Color.DARK_GRAY));
        addChild(image);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                pane.setContentType("text/x-java");
            }
        });
        crp.add(pane);
    }

    void moveTo(Widget widget, String text) {
        boolean hadParent = getParentWidget() != null;
        if (hadParent) {
            Widget w = getParentWidget();
            getParentWidget().removeChild(this);
            if (w instanceof JavaNodeWidget) {
                ((JavaNodeWidget) w).setMinimized(true);
            }
            getScene().validate();
        }
        ((JavaScene) getScene()).notifyEditorViewOpen(widget != null);
        setText(text);
        if (widget != null) {
            widget.addChild(this);
            if (!hadParent) {
                getScene().validate();
            }
        }
    }

    void hide(Widget w) {
        if (getParentWidget() == w) {
            getParentWidget().removeChild(this);
            getScene().validate();
            ((JavaScene) getScene()).notifyEditorViewOpen(false);
        }
    }

    void setText(String text) {
        pane.setText(text);
        pane.validate();
        pane.doLayout();
        makeImage();
    }

    void setZoom(double zoom) {
        if (zoom != this.zoom) {
            this.zoom = zoom;
            makeImage();
        }
    }

    void makeImage() {
        String s = pane.getText();
        BufferedImage foo = new BufferedImage(1, 1, 1);
        Graphics2D gg = foo.createGraphics();
        Dimension d = computeSize(gg, s);
        gg.dispose();
        double w = d.width;
        double h = d.height;
        w *= zoom;
        h *= zoom;
        BufferedImage result = new BufferedImage((int) w, (int) h, 1);
        pane.setSize((int) w, (int) h);
        pane.doLayout();
        Graphics2D g = result.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setColor(pane.getBackground());
        g.fillRect(0, 0, (int) w, (int) h);
        g.setTransform(AffineTransform.getScaleInstance(zoom, zoom));
        pane.paint(g);
        g.dispose();
        image.setImage(result);
    }

    @Override
    protected void paintWidget() {
        Graphics2D g = getGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        super.paintWidget();
    }

    private Dimension computeSize(Graphics2D gg, String s) {
        gg.setFont(pane.getFont());
        char c[] = s.toCharArray();
        int lineCount = 1;
        int maxLineLength = 40;
        int lastReturn = 0;
        int x = 0;
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\n') {
                maxLineLength = Math.max(maxLineLength, i - lastReturn);
                lastReturn = i;
                lineCount++;
                x = 0;
            }
        }

        FontMetrics mx = gg.getFontMetrics();
        int wid = mx.charWidth('x') * maxLineLength;
        int ht = mx.getHeight() + mx.getMaxDescent();
        return new Dimension(wid, ht * lineCount);
    }
    
    private class ZoomAction extends WidgetAction.Adapter {
        @Override
        public WidgetAction.State mouseClicked(Widget widget, WidgetAction.WidgetMouseEvent event) {
            if (widget == zoomIn) {
                setZoom(zoom + 0.1D);
            } else {
                setZoom(Math.max(0.0D, zoom - 0.1D));
            }
            getScene().validate();
            return org.netbeans.api.visual.action.WidgetAction.State.CONSUMED;
        }

        private ZoomAction() {
            super();
        }
    }
}

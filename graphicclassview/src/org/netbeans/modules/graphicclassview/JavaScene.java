package org.netbeans.modules.graphicclassview;

import java.awt.*;
import java.util.*;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.anchor.*;
import org.netbeans.api.visual.graph.GraphPinScene;
import org.netbeans.api.visual.graph.layout.GraphLayout;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.layout.SceneLayout;
import org.netbeans.api.visual.model.*;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.widget.*;
import org.netbeans.modules.graphicclassview.JavaScene.Conn;
import org.netbeans.modules.graphicclassview.JavaScene.Pin;
import org.openide.loaders.DataObject;
import org.openide.util.RequestProcessor;

final class JavaScene extends GraphPinScene<SceneElement, Conn, Pin>
        implements ObjectSceneListener {
    private final LayerWidget mainLayer = new LayerWidget(this);
    private final LayerWidget connectionLayer = new LayerWidget(this);
    private final LayerWidget interractionLayer = new LayerWidget(this);
    private final LayerWidget selectionLayer = new LayerWidget(this);
    private org.openide.util.Lookup.Provider provider;
    private final Router router = RouterFactory.createDirectRouter();
    private SceneLayout sceneLayout;
    private final SharedTextWidget textWidget = new SharedTextWidget(this);
    org.openide.util.RequestProcessor.Task task;
    private JavaViewComponent.ShowKinds show;
    Map<SceneElement,Pin> outPins;
    Map<SceneElement,Pin> inPins;
    Set <SceneElement> all;
    private static final Color LINE_COLOR = new Color(128, 128, 128, 128);
    private Widget hoverWidget;

    JavaScene(org.openide.util.Lookup.Provider provider, JavaViewComponent.ShowKinds kinds) {
        show = JavaViewComponent.ShowKinds.METHODS_AND_FIELDS;
        outPins = new HashMap<SceneElement,Pin>();
        inPins = new HashMap<SceneElement,Pin>();
        hoverWidget = null;
        show = kinds;
        addChild(mainLayer);
        addChild(connectionLayer);
        addChild(interractionLayer);
        addChild(selectionLayer);
        selectionLayer.setLayout(LayoutFactory.createAbsoluteLayout());
        setZoomFactor(0.75D);
        this.provider = provider;
        GraphLayout<SceneElement, Conn> layout = new JavaSceneLayout();
        sceneLayout = LayoutFactory.createSceneGraphLayout(this, layout);
        sceneLayout.invokeLayout();
        addObjectSceneListener(this, new ObjectSceneEventType[]{
            ObjectSceneEventType.OBJECT_HOVER_CHANGED
        });
    }

    public void remove(SceneElement e) {
        super.removeNode(e);
    }

    SharedTextWidget getTextWidget() {
        return textWidget;
    }

    public void relayout() {
        sceneLayout.invokeLayoutImmediately();
    }

    @Override
    public boolean isEdge(Object object) {
        return object instanceof Conn;
    }

    @Override
    public boolean isNode(Object object) {
        return object instanceof SceneElement;
    }

    @Override
    public boolean isPin(Object object) {
        return object instanceof Pin;
    }

    synchronized void init(Notifier n) {
        if (task == null || task.isFinished()) {
            RequestProcessor.getDefault().post(new Initializer(n));
        }
    }

    void replaceLayout(GraphLayout<SceneElement, Conn> layout) {
        sceneLayout = LayoutFactory.createSceneGraphLayout(this, layout);
        sceneLayout.invokeLayout();
    }

    private Widget createWidget(SceneElement node) {
        JavaNodeWidget w = new JavaNodeWidget(this, node);
        w.setNodeImage(node.getImage());
        w.setNodeName(node.getName());
        String s[] = node.getType().split("\\)");
        String nodeArgs;
        String nodeType;
        if (s.length == 1) {
            nodeArgs = "";
            nodeType = node.getType();
        } else {
            nodeArgs = (new StringBuilder()).append(s[0]).append(")").toString();
            nodeType = s[1];
        }
        w.setNodeType(nodeType);
        w.setToolTipText((new StringBuilder()).append("<html>").append(nodeType).append(" <b>").append(node.getName()).append("</b> <i>").append(nodeArgs).toString());
        w.getActions().addAction(ActionFactory.createMoveAction());
        return w;
    }

    private void buildGraph(Set<SceneElement> elements) {
        if (all == null) {
            all = elements;
        }
        if (show != JavaViewComponent.ShowKinds.METHODS_AND_FIELDS) {
            Iterator i = elements.iterator();
            do {
                if (!i.hasNext()) {
                    break;
                }
                if (!show.match((SceneElement) i.next())) {
                    i.remove();
                }
            } while (true);
        }
        Map <SceneElement, Widget> m = new HashMap<SceneElement, Widget>();
        for (SceneElement e : elements) {
            if (!m.containsKey(e)) {
                Widget w = addNode(e);
                m.put(e, w);
                Pin inPin = new Pin("incoming");
                addPin(e, inPin);
                validate();
                inPins.put(e, inPin);
                if (e.getKind() == SceneObjectKind.METHOD) {
                    Pin outPin = new Pin("outgoing");
                    addPin(e, outPin);
                    outPins.put(e, outPin);
                }
            }
        }
        Set <Conn> connections = new HashSet <Conn> ();
        for (SceneElement e : elements) {
            Iterator itt = e.getOutboundReferences(SceneElement.class).iterator();
            while (itt.hasNext()) {
                SceneElement out = (SceneElement) itt.next();
                Conn conn = new Conn(e, out);
                if (!connections.contains(conn)) {
                    connections.add(conn);
                    Pin sourcePin = outPins.get(conn.src);
                    Pin destPin = inPins.get(conn.targ);
                    addEdge(conn);
                    validate();
                    setEdgeSource(conn, sourcePin);
                    setEdgeTarget(conn, destPin);
                }
            }
        }

        System.err.println((new StringBuilder()).append(connections.size()).append(" connections ").toString());
        sceneLayout.invokeLayoutImmediately();
    }

    public void notifyEditorViewOpen(boolean value) {
        connectionLayer.setVisible(!value);
        repaint();
    }

    @Override
    protected void paintChildren() {
        Object anti = getGraphics().getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        Object textAnti = getGraphics().getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
        getGraphics().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        getGraphics().setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        getGraphics().setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        getGraphics().setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        super.paintChildren();
    }

    protected Widget attachNodeWidget(SceneElement node) {
        Widget result = createWidget(node);
        result.getActions().addAction(createSelectAction());
        result.getActions().addAction(createObjectHoverAction());
        mainLayer.addChild(result);
        return result;
    }

    protected Widget attachEdgeWidget(Conn edge) {
        ConnectionWidget connection = new ConnectionWidget(this);
        connection.setRouter(router);
        connection.setTargetAnchorShape(AnchorShape.TRIANGLE_HOLLOW);
        connection.setPaintControlPoints(true);
        connection.setEndPointShape(PointShape.SQUARE_FILLED_SMALL);
        connection.setLineColor(LINE_COLOR);
        connection.getActions().addAction(createObjectHoverAction());
        SceneElement src = edge.src;
        SceneElement targ = edge.targ;
        Pin srcPin = outPins.get(src);
        Pin targPin = inPins.get(targ);
        connection.setSourceAnchor(AnchorFactory.createRectangularAnchor(findWidget(srcPin)));
        connection.setTargetAnchor(AnchorFactory.createRectangularAnchor(findWidget(targPin)));
        connectionLayer.addChild(connection);
        return connection;
    }

    protected Widget attachPinWidget(SceneElement node, Pin pin) {
        PinWidget widget = new PinWidget(this, pin.toString());
        ((JavaNodeWidget) findWidget(node)).attachPinWidget(widget);
        return widget;
    }

    protected void attachEdgeSourceAnchor(Conn edge, Pin oldSourcePin, Pin sourcePin) {
        ConnectionWidget edgeWidget = (ConnectionWidget) findWidget(edge);
        Widget sourceNodeWidget = findWidget(sourcePin);
        org.netbeans.api.visual.anchor.Anchor sourceAnchor = AnchorFactory.createRectangularAnchor(sourceNodeWidget);
        edgeWidget.setSourceAnchor(sourceAnchor);
        ((ConnectionWidget) findWidget(edge)).setSourceAnchor(AnchorFactory.createRectangularAnchor(findWidget(sourcePin)));
    }

    protected void attachEdgeTargetAnchor(Conn edge, Pin oldTargetPin, Pin targetPin) {
        ConnectionWidget edgeWidget = (ConnectionWidget) findWidget(edge);
        Widget targetNodeWidget = findWidget(targetPin);
        org.netbeans.api.visual.anchor.Anchor targetAnchor = AnchorFactory.createRectangularAnchor(targetNodeWidget);
        edgeWidget.setTargetAnchor(targetAnchor);
        ((ConnectionWidget) findWidget(edge)).setTargetAnchor(AnchorFactory.createRectangularAnchor(findWidget(targetPin)));
    }

    void setHoverWidget(Widget w) {
        if (hoverWidget != w) {
            if (hoverWidget != null) {
                changeHighlighting(hoverWidget, false);
            }
            hoverWidget = w;
            if (w != null) {
                changeHighlighting(w, true);
            }
        }
    }

    void changeHighlighting(Widget widget, boolean hl) {
        JavaNodeWidget w = (JavaNodeWidget) widget;
        SceneElement node = w.getNode();
        Iterator i$ = getEdges().iterator();
        do {
            if (!i$.hasNext()) {
                break;
            }
            Conn c = (Conn) i$.next();
            if (c.src == node) {
                ConnectionWidget cw = (ConnectionWidget) findWidget(c);
                cw.setLineColor(hl ? Color.BLUE : LINE_COLOR);
            } else if (c.targ == node) {
                ConnectionWidget cw = (ConnectionWidget) findWidget(c);
                cw.setLineColor(hl ? Color.RED : LINE_COLOR);
            }
        } while (true);
    }

    public void stateChanged(Object o) {
        if (o != null) {
            Widget w = findWidget(o);
            if (w instanceof JavaNodeWidget) {
                setHoverWidget(w);
            }
        } else {
            setHoverWidget(null);
        }
    }

    public void objectAdded(ObjectSceneEvent objectsceneevent, Object obj) {
    }

    public void objectRemoved(ObjectSceneEvent objectsceneevent, Object obj) {
    }

    public void objectStateChanged(ObjectSceneEvent objectsceneevent, Object obj, ObjectState objectstate, ObjectState objectstate1) {
    }

    public void selectionChanged(ObjectSceneEvent objectsceneevent, Set set, Set set1) {
    }

    public void highlightingChanged(ObjectSceneEvent objectsceneevent, Set set, Set set1) {
    }

    public void hoverChanged(ObjectSceneEvent event, Object previousHoveredObject, Object newHoveredObject) {
        stateChanged(newHoveredObject);
    }

    public void focusChanged(ObjectSceneEvent objectsceneevent, Object obj, Object obj1) {
    }
    
    private class CB
            implements ModelBuilder.Callback {

        public void done(Set<SceneElement> result) {
            System.err.println((new StringBuilder()).append("Callback done: ").append(result).toString());
            buildGraph(result);
            failed(null);
        }

        public void failed(final String message) {
            EventQueue.invokeLater(new Runnable() {

                public void run() {
                    n.done(message);
                    synchronized (JavaScene.this) {
                        task = null;
                    }
                }
            });
        }
        private final Notifier n;

        CB(Notifier n) {
            this.n = n;
        }
    }

    static final class Pin {
        private final String name;
        Pin(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        boolean isOutgoing() {
            return "outgoing".equals(name);
        }
    }

    static final class Conn {
        Conn(SceneElement src, SceneElement targ) {
            this.src = src;
            this.targ = targ;
        }

        public SceneElement getSrc() {
            return src;
        }

        public SceneElement getTarg() {
            return targ;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Conn other = (Conn) obj;
            if (targ != other.targ && (targ == null || !targ.equals(other.targ))) {
                return false;
            }
            return src == other.src || src != null && src.equals(other.src);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 97 * hash + (targ == null ? 0 : targ.hashCode());
            hash = 97 * hash + (src == null ? 0 : src.hashCode());
            return hash;
        }
        private final SceneElement targ;
        private final SceneElement src;
    }

    private class Initializer implements Runnable {

        Initializer(Notifier n) {
            this.n = n;
        }

        public void run() {
            DataObject dob = provider.getLookup().lookup(DataObject.class);
            if (dob != null && dob.isValid()) {
                (new ModelBuilder(dob.getPrimaryFile())).analyze(new CB(n));
            }
        }
        private final Notifier n;

    }

    static interface Notifier {
        public abstract void done(String s);
    }
}

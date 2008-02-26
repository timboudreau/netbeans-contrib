package org.netbeans.modules.graphicclassview;

import com.sun.source.util.*;
import java.awt.*;
import java.io.IOException;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import org.netbeans.api.java.source.*;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.model.StateModel;
import org.netbeans.api.visual.vmd.VMDMinimizeAbility;
import org.netbeans.api.visual.widget.*;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.util.*;
import org.openide.windows.TopComponent;

public class JavaNodeWidget extends Widget
        implements org.netbeans.api.visual.model.StateModel.Listener, VMDMinimizeAbility {

    private class CloseAction extends org.netbeans.api.visual.action.WidgetAction.Adapter {

        @Override
        public WidgetAction.State mouseClicked(Widget widget, WidgetAction.WidgetMouseEvent event) {
            ((JavaScene) getScene()).remove(getNode());
            getScene().validate();
            return org.netbeans.api.visual.action.WidgetAction.State.CONSUMED;
        }
    }

    private final class GoToSourceAction extends org.netbeans.api.visual.action.WidgetAction.Adapter {

        @Override
        public WidgetAction.State mouseClicked(Widget widget, org.netbeans.api.visual.action.WidgetAction.WidgetMouseEvent event) {
            if (event.getClickCount() >= 2) {
                final SceneElement el = getNode();
                javax.swing.JComponent jc = getScene().getView();
                TopComponent tc = (TopComponent) SwingUtilities.getAncestorOfClass(TopComponent.class, jc);
                DataObject ob = tc.getLookup().lookup(DataObject.class);
                if (ob != null) {
                    final EditorCookie ck = ob.getLookup().lookup(EditorCookie.class);
                    if (ck != null) {
                        ck.open();
                        class Opener implements Runnable, Task<CompilationController> {
                            long pos = -1L;
                            public void run() {
                                if (!EventQueue.isDispatchThread()) {
                                    try {
                                        javax.swing.text.Document d = ck.openDocument();
                                        JavaSource.forDocument(d).runWhenScanFinished(this, true);
                                    } catch (IOException ex) {
                                        Exceptions.printStackTrace(ex);
                                    }
                                } else {
                                    try {
                                        JEditorPane panes[] = ck.getOpenedPanes();
                                        if (pos != -1L && panes != null && panes.length > 0) {
                                            int p = (int) pos;
                                            java.awt.Rectangle r = panes[0].modelToView(p);
                                            panes[0].scrollRectToVisible(r);
                                            panes[0].setCaretPosition(p);
                                        }
                                    } catch (BadLocationException e) {
                                        Exceptions.printStackTrace(e);
                                    }
                                }
                            }

                            public void run(CompilationController compiler)
                                    throws Exception {
                                TreePathHandle h = el.getHandle();
                                TreePath path = h.resolve(compiler);
                                com.sun.source.tree.Tree tree = path.getLeaf();
                                pos = compiler.getTrees().getSourcePositions().getStartPosition(compiler.getCompilationUnit(), tree);
                                EventQueue.invokeLater(this);
                            }
                        }
                        RequestProcessor.getDefault().post(new Opener());
                    }
                }
                return org.netbeans.api.visual.action.WidgetAction.State.CONSUMED;
            } else {
                return org.netbeans.api.visual.action.WidgetAction.State.REJECTED;
            }
        }

        private GoToSourceAction() {
            super();
        }
    }

    private final class ToggleMinimizedAction extends WidgetAction.Adapter {

        @Override
        public WidgetAction.State mousePressed(Widget widget, WidgetAction.WidgetMouseEvent event) {
            if (event.getButton() == 1 || event.getButton() == 2) {
                stateModel.toggleBooleanState();
                return org.netbeans.api.visual.action.WidgetAction.State.CONSUMED;
            } else {
                return org.netbeans.api.visual.action.WidgetAction.State.REJECTED;
            }
        }
    }

    public JavaNodeWidget(Scene scene, SceneElement node) {
        super(scene);
        stateModel = new StateModel(2);
        this.node = node;
        setLayout(LayoutFactory.createOverlayLayout());
        content = new Widget(scene);
        addChild(content);
        content.setLayout(LayoutFactory.createVerticalFlowLayout());
        setMinimumSize(new Dimension(80, 8));
        headerContainer = new Widget(scene);
        headerContainer.setLayout(LayoutFactory.createHorizontalFlowLayout());
        content.addChild(headerContainer);
        header = new Widget(scene);
        content.setBackground(new GradientPaint(0.0F, 0.0F, new Color(255, 255, 187), 128F, 128F, new Color(187, 197, 255)));
        content.setOpaque(true);
        content.setBorder(BorderFactory.createCompositeBorder(new Border[]{
            BorderFactory.createRoundedBorder(7, 7, 0, 0, new Color(120, 120, 125, 0), new Color(80, 80, 90)), BorderFactory.createOpaqueBorder(12, 12, 12, 12)
        }));
        header.setLayout(LayoutFactory.createHorizontalFlowLayout(org.netbeans.api.visual.layout.LayoutFactory.SerialAlignment.CENTER, 8));
        headerContainer.addChild(header);
        boolean right = true;
        minimizeWidget = new ImageWidget(scene, Utilities.loadImage("org/netbeans/modules/graphicclassview/resources/minimize.png"));
        minimizeWidget.setCursor(Cursor.getPredefinedCursor(12));
        minimizeWidget.getActions().addAction(new ToggleMinimizedAction());
        if (!right) {
            header.addChild(minimizeWidget);
        }
        minimizeWidget.setBorder(BorderFactory.createEmptyBorder(3));
        imageWidget = new ImageWidget(scene);
        header.addChild(imageWidget);
        nameWidget = new LabelWidget(scene);
        nameWidget.setFont(scene.getDefaultFont().deriveFont(1));
        header.addChild(nameWidget);
        typeWidget = new LabelWidget(scene);
        typeWidget.setForeground(Color.BLACK);
        typeWidget.setVisible(false);
        header.addChild(typeWidget);
        Widget closeWidget = new LabelWidget(scene, "x");
        closeWidget.setCursor(Cursor.getPredefinedCursor(12));
        closeWidget.getActions().addAction(new CloseAction());
        if (right) {
            Widget widget = new Widget(scene);
            widget.setOpaque(false);
            header.addChild(widget, Integer.valueOf(1000));
            header.addChild(minimizeWidget);
            header.addChild(closeWidget, Integer.valueOf(1010));
        }
        topLayer = new Widget(scene);
        topLayer.setLayout(new OppositeSidesLayout());
        topLayer.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        addChild(topLayer);
        stateModel = new StateModel();
        stateModel.setBooleanState(true);
        stateModel.addListener(this);
        setBorder(BorderFactory.createEmptyBorder(7));
        notifyStateChanged(ObjectState.createNormal(), ObjectState.createNormal());
        WidgetAction gsa = new GoToSourceAction();
        getActions().addAction(gsa);
        nameWidget.getActions().addAction(gsa);
        typeWidget.getActions().addAction(gsa);
    }

    SceneElement getNode() {
        return node;
    }

    protected boolean isMinimizableWidget(Widget widget) {
        return !(widget instanceof PinWidget) && header != widget && headerContainer != widget;
    }

    public boolean isMinimized() {
        return stateModel.getBooleanState();
    }

    public void setMinimized(boolean minimized) {
        stateModel.setBooleanState(minimized);
    }

    public void toggleMinimized() {
        stateModel.toggleBooleanState();
    }

    public void stateChanged() {
        boolean minimized = stateModel.getBooleanState();
        String img = minimized ? "org/netbeans/modules/graphicclassview/resources/minimize.png" : 
            "org/netbeans/modules/graphicclassview/resources/maximize.png";
        Image image = Utilities.loadImage(img);
        minimizeWidget.setImage(image);
        JavaScene scene = (JavaScene) getScene();
        SharedTextWidget textView = scene.getTextWidget();
        if (minimized) {
            textView.hide(content);
        } else {
            bringToFront();
            textView.moveTo(content, node.getBody());
        }
        typeWidget.setVisible(!minimized);
    }

    public void setNodeImage(Image image) {
        imageWidget.setImage(image);
        revalidate();
    }

    public String getNodeName() {
        return nameWidget.getLabel();
    }

    public void setNodeName(String nodeName) {
        nameWidget.setLabel(nodeName);
    }

    public void setNodeType(String nodeType) {
        typeWidget.setLabel(nodeType == null ? null : (new StringBuilder()).append("[").append(nodeType).append("]").toString());
    }

    public void attachPinWidget(Widget widget) {
        widget.setCheckClipping(true);
        topLayer.addChild(widget);
    }

    public LabelWidget getNodeNameWidget() {
        return nameWidget;
    }

    public void collapseWidget() {
        stateModel.setBooleanState(true);
    }

    public void expandWidget() {
        stateModel.setBooleanState(false);
    }

    public Widget getHeader() {
        return header;
    }

    public Widget getMinimizeButton() {
        return minimizeWidget;
    }
    private final Widget header;
    private final ImageWidget minimizeWidget;
    private final ImageWidget imageWidget;
    private final LabelWidget nameWidget;
    private final LabelWidget typeWidget;
    private StateModel stateModel;
    private final Widget headerContainer;
    private final SceneElement node;
    private final Widget topLayer;
    private final Widget content;
}

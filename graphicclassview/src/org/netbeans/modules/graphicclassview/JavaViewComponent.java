package org.netbeans.modules.graphicclassview;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.nodes.NodeAdapter;
import org.openide.nodes.NodeEvent;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

public class JavaViewComponent extends TopComponent {
    private final DataObject ob;
    private JavaScene scene;
    private final JToolBar toolbar = createToolbar();
    private JComboBox show;
    private final NL nl = new NL();
    final N n = new N();

    public JavaViewComponent(DataObject ob) {
        scene = null;
        this.ob = ob;
        Node nd = ob.getNodeDelegate();
        setActivatedNodes(new Node[]{
            nd
        });
        setLayout(new BorderLayout());
        setDisplayName(nd.getDisplayName());
    }

    private JToolBar createToolbar() {
        JToolBar result = new JToolBar() {
            @Override
            public String getUIClassID() {
                if (UIManager.get("Nb.Toolbar.ui") != null) {
                    return "Nb.Toolbar.ui";
                } else {
                    return super.getUIClassID();
                }
            }
        };
        DefaultComboBoxModel mdl = new DefaultComboBoxModel(LayoutKinds.values());
        final JComboBox layoutBox = new JComboBox(mdl);
        result.setFloatable(false);
        final JButton refresh = new JButton(new ImageIcon(Utilities.loadImage("org/netbeans/modules/graphicclassview/resources/refresh.png")));
        refresh.setToolTipText(getString("BTN_REFRESH"));
        refresh.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                removeAll();
                scene = null;
                layoutBox.setSelectedItem(LayoutKinds.CONNECTED_CENTER);
                componentOpened();
            }
        });
        result.add(refresh);
        javax.swing.Icon icon = new ImageIcon(Utilities.loadImage("org/netbeans/modules/graphicclassview/resources/reflow.png"));
        JButton reflow = new JButton(icon);
        reflow.setToolTipText(getString("BTN_REFLOW"));
        reflow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (scene != null) {
                    scene.relayout();
                }
            }
        });
        result.add(reflow);
        icon = new ImageIcon(Utilities.loadImage("org/netbeans/modules/graphicclassview/resources/zoomIn.png"));
        final JButton zoomIn = new JButton(icon);
        zoomIn.setToolTipText(getString("BTN_ZOOMIN"));
        icon = new ImageIcon(Utilities.loadImage("org/netbeans/modules/graphicclassview/resources/zoomOut.png"));
        JButton zoomOut = new JButton(icon);
        zoomOut.setToolTipText(getString("BTN_ZOOMOUT"));
        ActionListener al = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (scene != null) {
                    double d = scene.getZoomFactor();
                    if (e.getSource() == zoomIn) {
                        scene.setZoomFactor(d + 0.1D);
                    } else {
                        scene.setZoomFactor(Math.max(0.0D, d - 0.1D));
                    }
                    scene.validate();
                    scene.repaint();
                }
            }
        };
        zoomIn.addActionListener(al);
        zoomOut.addActionListener(al);
        result.add(zoomIn);
        result.add(zoomOut);
        mdl = new DefaultComboBoxModel(ShowKinds.values());
        show = new JComboBox(mdl);
        JLabel lbl = new JLabel(getString("LBL_SHOW"));
        result.add(lbl);
        result.add(show);
        show.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                refresh.doClick();
            }
        });
        result.add(layoutBox);
        layoutBox.setSelectedItem(LayoutKinds.CONNECTED_CENTER);
        layoutBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (scene == null) {
                    return;
                } else {
                    scene.replaceLayout(layoutBox.getSelectedItem() != 
                            LayoutKinds.TOPOLOGICAL_LAYOUT ? 
                            new JavaSceneLayout() : new TopologicalLayout());
                    return;
                }
            }
        });
        result.add(new JPanel());
        return result;
    }

    private static String getString(String key) {
        return NbBundle.getMessage(JavaViewComponent.class, key);
    }

    ShowKinds getShowKinds() {
        return (ShowKinds) show.getSelectedItem();
    }

    private JComponent createWaitPanel() {
        JPanel pnl = new JPanel(new FlowLayout(3));
        pnl.add(new JLabel("Building graph..."));
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        pnl.add(bar);
        return pnl;
    }

    @Override
    public Lookup getLookup() {
        return ob.isValid() ? ob.getNodeDelegate().getLookup() : Lookup.EMPTY;
    }

    @Override
    protected void componentOpened() {
        n.active = true;
        if (ob.isValid()) {
            ob.getNodeDelegate().addNodeListener(nl);
            if (scene == null) {
                add(createWaitPanel(), "Center");
                scene = new JavaScene(this, getShowKinds());
                scene.init(n);
            }
        }
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_NEVER;
    }

    @Override
    protected void componentClosed() {
        n.active = false;
        ob.getNodeDelegate().removeNodeListener(nl);
        scene = null;
        removeAll();
    }
    
    private final class N
            implements JavaScene.Notifier {

        public void done(String failureMessage) {
            assert EventQueue.isDispatchThread();
            if (!active) {
                return;
            }
            removeAll();
            if (failureMessage == null) {
                JComponent jc = scene.getView();
                if (jc == null) {
                    jc = scene.createView();
                }
                add(new JScrollPane(jc), "Center");
                add(toolbar, "North");
            } else {
                add(new JLabel(failureMessage), "Center");
            }
            invalidate();
            revalidate();
            repaint();
        }
        volatile boolean active;
    }

    private final class NL extends NodeAdapter {

        @Override
        public void nodeDestroyed(NodeEvent ev) {
            close();
        }

        @Override
        public void propertyChange(PropertyChangeEvent ev) {
            if ("displayName".equals(ev.getPropertyName())) {
                setDisplayName(ev.getNewValue().toString());
            }
        }
    }

    public enum ShowKinds {
        METHODS_AND_FIELDS, METHODS, FIELDS;

        @Override
        public String toString() {
            return NbBundle.getMessage(ShowKinds.class, name());
        }

        public boolean match(SceneElement el) {
            switch (this) {
                case METHODS_AND_FIELDS :
                    return el.getKind() == SceneObjectKind.METHOD || el.getKind() == SceneObjectKind.FIELD;
                case METHODS :
                    return el.getKind() == SceneObjectKind.METHOD;
                case FIELDS :
                    return el.getKind() == SceneObjectKind.FIELD;
                default :
                    throw new AssertionError();
            }
        }
    }

    public enum LayoutKinds  {
        TOPOLOGICAL_LAYOUT, CONNECTED_CENTER;

        @Override
        public String toString() {
            return NbBundle.getMessage(LayoutKinds.class, name());
        }
    }
}

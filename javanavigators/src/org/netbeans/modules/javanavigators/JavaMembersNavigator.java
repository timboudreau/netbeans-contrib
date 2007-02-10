/*
 * JavaMembersNavigator.java
 *
 * Created on February 9, 2007, 10:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.javanavigators;

import java.awt.BorderLayout;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 *
 * @author Tim Boudreau
 */
public class JavaMembersNavigator implements NavigatorPanel {
    private Reference <JPanel> pnl = null;
    /** Creates a new instance of JavaMembersNavigator */
    public JavaMembersNavigator() {
    }
    
    public String getDisplayName() {
        return NbBundle.getMessage (JavaMembersNavigator.class,
                "LBL_MEMBERS_NAVIGATOR");
    }

    public String getDisplayHint() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public JComponent getComponent(boolean create) {
        JPanel result = pnl == null ? null : pnl.get();
        if (create && pnl == null) {
            result = createPanel();
            pnl = new SoftReference <JPanel> (result);
        }
        return result;
    }
    
    public JComponent getComponent() {
        return getComponent (true);
    }

    private Lookup ctx = Lookup.EMPTY;
    public void panelActivated(Lookup context) {
        System.err.println("panel activated");
        DataGatheringTaskFactory.getDefault().activate();
        ctx = context;
        JList list = getList(true);
        list.setModel (DataGatheringTaskFactory.getModel());
    }

    public void panelDeactivated() {
        System.err.println("panel deactivated");
        DataGatheringTaskFactory.getDefault().deactivate();
        ctx = Lookup.EMPTY;
        JList list = getList(false);
        if (list != null) {
            list.setModel (new DefaultListModel());
        }
    }

    public Lookup getLookup() {
        return ctx;
    }
    
    private JPanel createPanel() {
        JPanel result = new Pnl();
        Border empty = BorderFactory.createEmptyBorder();
        result.setLayout (new BorderLayout());
        JList list = new JList();
        JScrollPane scroll = new JScrollPane (list);
        list.setBorder (empty);
        scroll.setBorder (empty);
        scroll.setViewportBorder(empty);
        result.setBorder (empty);
        result.add (scroll, BorderLayout.CENTER);
        list.setModel (DataGatheringTaskFactory.getModel());
        list.getSelectionModel().addListSelectionListener(new ListListener(list));
        return result;
    }
    
    private JList getList(boolean val) {
        JPanel pnl = (JPanel) getComponent (val);
        if (pnl != null) {
            JScrollPane pane = (JScrollPane) pnl.getComponents()[0];
            return (JList) pane.getViewport().getView();
        }
        return null;
    }
    
    private static final class Pnl extends JPanel {
        Boolean prev;
        public void addNotify() {
            super.addNotify();
            TopComponent tc = (TopComponent) SwingUtilities.getAncestorOfClass (
                    TopComponent.class, this);
            if (tc != null) {
                prev = (Boolean) tc.getClientProperty ("dontActivate"); //NOI18N
                tc.putClientProperty ("dontActivate", Boolean.TRUE); //NOI18N
            }
        }
        
        public void removeNotify() {
            TopComponent tc = (TopComponent) SwingUtilities.getAncestorOfClass (
                    TopComponent.class, this);
            if (tc != null) {
                tc.putClientProperty ("dontActivate", prev); //NOI18N
            }
            super.removeNotify();
        }
    }
}

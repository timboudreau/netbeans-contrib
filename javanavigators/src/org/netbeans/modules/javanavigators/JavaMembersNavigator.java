/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.javanavigators;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Comparator;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.awt.HtmlRenderer;
import org.openide.awt.Mnemonics;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 *
 * @author Tim Boudreau
 */
public class JavaMembersNavigator implements NavigatorPanel {
    private Reference <JPanel> pnl = null;
    public JavaMembersNavigator() {
    }
    
    public String getDisplayName() {
        return NbBundle.getMessage (JavaMembersNavigator.class,
                "LBL_MEMBERS_NAVIGATOR"); //NOI18n
    }

    public String getDisplayHint() {
        return null; //FIXME
    }

    public JComponent getComponent(boolean create) {
        JPanel result = pnl == null ? null : pnl.get();
        if (create && result == null) {
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
        DataGatheringTaskFactory.getDefault().activate();
        ctx = context;
        JList list = getList(true);
        if (list != null) {
            list.setModel (DataGatheringTaskFactory.getModel());
        }
    }

    public void panelDeactivated() {
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
        JList list = new OffsetTooltipJList();
        JScrollPane scroll = new JScrollPane (list);
        list.setBorder (empty);
        scroll.setBorder (empty);
        scroll.setViewportBorder(empty);
        result.setBorder (empty);
        result.add (scroll, BorderLayout.CENTER);
        list.setModel (DataGatheringTaskFactory.getModel());
        list.getSelectionModel().addListSelectionListener(new ListListener(list));
        list.setCellRenderer (new CellRenderer());
        list.addMouseListener (new ML());
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
    
    private static class ML extends MouseAdapter implements ActionListener {
        private static final String ACTION_POSITION_SORT = "pos"; //NOI18N
        public static final String ACTION_ALPHA_SORT = "alpha"; //NOI18N
        private AsynchListModel <Description> m;
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3) {
                JList l = (JList) e.getSource();
                ListModel m = l.getModel();
                if (!(m instanceof AsynchListModel)) {
                    return;
                }
                this.m = (AsynchListModel <Description>) m;
                JPopupMenu menu = new JPopupMenu();
                JCheckBoxMenuItem pos = new JCheckBoxMenuItem ();
                Mnemonics.setLocalizedText(pos, NbBundle.getMessage (
                        JavaMembersNavigator.class,
                        "LBL_POS_SORT")); //NOI18N
                JCheckBoxMenuItem alpha = new JCheckBoxMenuItem ();
                alpha.setSelected (this.m.getComparator() == 
                        Description.ALPHA_COMPARATOR);
                pos.setSelected (!alpha.isSelected());
                Mnemonics.setLocalizedText(alpha, NbBundle.getMessage (
                        JavaMembersNavigator.class,
                        "LBL_ALPHA_SORT")); //NOI18N
                menu.add (alpha);
                menu.add (pos);
                pos.setActionCommand(ACTION_POSITION_SORT);
                alpha.setActionCommand (ACTION_ALPHA_SORT);
                pos.addActionListener (this);
                alpha.addActionListener (this);
                Point p = e.getPoint();
                menu.show(l, p.x, p.y);
            }
        }
    
        public void actionPerformed(ActionEvent e) {
            Comparator <Description> c = ACTION_ALPHA_SORT.equals(
                e.getActionCommand()) ?
                Description.ALPHA_COMPARATOR : Description.POSITION_COMPARATOR;
            m.setComparator (c);
        }
    }
    
    private static final class OffsetTooltipJList extends JList {
        public Point getToolTipLocation(MouseEvent e) {
            Point result = super.getToolTipLocation(e);
            if (result == null) {
                result = e.getPoint();
            }
            Rectangle r = getBounds();
            Container c = getTopLevelAncestor();
            if (c != null) {
                r = SwingUtilities.convertRectangle(this, r, c);
                int cw = c.getWidth();
                int availRight = r.x;
                int availLeft = cw - (r.x + r.width);
                if (availRight > availLeft) {
                    result.x += r.width - result.x;
                } else {
                    //FIXME: Really need to compute rectangle needed for
                    //tooltip here
                    result.x -= r.width;
                }
            }
            return result;
        }
    }
}

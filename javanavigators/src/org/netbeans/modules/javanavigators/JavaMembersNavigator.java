/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Comparator;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.awt.Mnemonics;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.windows.TopComponent;

/**
 *
 * @author Tim Boudreau
 */
public class JavaMembersNavigator implements NavigatorPanel {
    private Reference <JPanel> pnl = null;
    static final String KEY_SORT_POS = "sortMode";        
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
        new ListDragListener (list);
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
            boolean alphaSort = ACTION_ALPHA_SORT.equals(
                e.getActionCommand());
            Comparator <Description> c = alphaSort ?
                Description.ALPHA_COMPARATOR : Description.POSITION_COMPARATOR;
            m.setComparator (c);
            NbPreferences.forModule(JavaMembersNavigator.class).putBoolean(KEY_SORT_POS, alphaSort);                    
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

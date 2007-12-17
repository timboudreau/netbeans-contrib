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

/*
 * ToolbarStyleSwitchUI.java
 *
 * Created on September 8, 2007, 11:18 AM
 */
package org.netbeans.modules.perspective.ui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.netbeans.modules.perspective.utils.PerspectiveManagerImpl;
import org.netbeans.modules.perspective.actions.SwitchAction;
import org.netbeans.modules.perspective.views.PerspectiveImpl;
import org.openide.util.Utilities;

/**
 *
 * @author  Anuradha G
 */
public class ToolbarStyleSwitchUI extends JToolBar {

    private static ToolbarStyleSwitchUI instance;

    /** Creates new form BeanForm */
    public ToolbarStyleSwitchUI() {

        initComponents();
        btnSelected = new JToggleButton();
        btnNext = new JToggleButton();
        btnSelected.setFocusable(false);
        btnNext.setFocusable(false);
        buttonGroup.add(btnSelected);
        buttonGroup.add(btnNext);
        add(btnSelected);
        add(btnNext);
        btnList.setAction(new SwitchListAction());


    }

    public static synchronized ToolbarStyleSwitchUI getInstance() {
        if (instance == null) {
            instance = new ToolbarStyleSwitchUI();
        }
        return instance;
    }

    public Component getStatusLineElement() {
        return this;
    }

    public void showPerspectiveList() {
        JPopupMenu menu = new JPopupMenu();
        menu.addPopupMenuListener(new PopupMenuListener() {

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                if (btnList != null) {
                    btnList.setSelected(true);
                }
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                if (btnList != null) {
                    btnList.setSelected(false);
                }
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
                if (btnList != null) {
                    btnList.setSelected(false);
                }
            }
        });
        List<PerspectiveImpl> perspectives = PerspectiveManagerImpl.getInstance().getPerspectives();

        for (PerspectiveImpl perspective : perspectives) {
            if (perspective.equals(selected)) {
                continue;
            }
            menu.add(new SwitchAction(perspective, true));
        }

        Point point = btnList.getLocationOnScreen();

        menu.setInvoker(btnList);
        menu.setVisible(true);
        menu.setLocation(point.x, point.y - (menu.getHeight()));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnList = new javax.swing.JButton();

        setRollover(true);

        btnList.setFocusable(false);
        btnList.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnList.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        add(btnList);

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ToolbarStyleSwitchUI.class, "ToolbarStyleSwitchUI.AccessibleContext.accessibleName")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnList;
    // End of variables declaration//GEN-END:variables
    private JToggleButton btnSelected;
    private JToggleButton btnNext;
    private ButtonGroup buttonGroup = new ButtonGroup();
    private PerspectiveImpl selected;
    private PerspectiveImpl previous;

    public void setSelected(PerspectiveImpl selected) {
        previous = this.selected;
        this.selected = selected;
    }

    public void reset() {
        previous = null;
        selected = null;
    }

    public void loadQuickPerspectives() {

        if (selected == null) {
            btnSelected.setAction(null);
            btnNext.setAction(null);
            return;
        }

        PerspectiveImpl next = null;

        if (previous == null || selected.equals(previous)) {
            int index = selected.getIndex();
            List<PerspectiveImpl> perspectives = PerspectiveManagerImpl.getInstance().getPerspectives();
            if (index < (perspectives.size() - 1)) {
                next = perspectives.get(++index);
            } else if (perspectives.size() > 0) {
                next = perspectives.get(0);
            }
        } else {
            next = previous;
        }

        //add Actions
        SwitchAction selecedAction = new SwitchAction(selected);
        btnSelected.setAction(selecedAction);




        if (next != null && !selected.equals(next)) {
            SwitchAction nextAction = new SwitchAction(next);
            btnNext.setAction(nextAction);
        } else {
            btnNext.setAction(null);
        }

        buttonGroup.setSelected(btnSelected.getModel(), true);
    }

    private class SwitchListAction extends AbstractAction {

        public SwitchListAction() {
            putValue(SMALL_ICON, new javax.swing.ImageIcon(Utilities.loadImage("org/netbeans/modules/perspective/resources/perspective.png", true)));
        }

        public void actionPerformed(ActionEvent e) {
            showPerspectiveList();
        }
    }
}

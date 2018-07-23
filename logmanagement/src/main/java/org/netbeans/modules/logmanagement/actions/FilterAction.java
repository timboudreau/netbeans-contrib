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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.logmanagement.actions;

import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G
 */
public class FilterAction extends AbstractAction {

    private static final long serialVersionUID = 1l;
    private JPopupMenu menu = new JPopupMenu();
    private boolean wormup;

    public FilterAction() {

        putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(Utilities.loadImage("org/netbeans/modules/logmanagement/resources/filter.png", true)));
        putValue(Action.SHORT_DESCRIPTION, NbBundle.getMessage(FilterAction.class, "filter"));
        menu.add(new JCheckBoxMenuItem("TODO"));//NOI18N
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof JButton) {
            JButton button = (JButton) source;
            wormup(button);
            Point point = button.getLocationOnScreen();

            menu.setInvoker(button);
            menu.setVisible(true);
            menu.setLocation(point.x + (button.getWidth()), point.y);
        }
    }

    private synchronized void wormup(final JButton button) {
        if (!wormup) {
            wormup = true;
            button.setFocusPainted(false);
            menu.addPopupMenuListener(new PopupMenuListener() {

                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    if (button != null) {
                        button.setSelected(true);
                    }
                }

                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    if (button != null) {
                        button.setSelected(false);
                    }
                }

                public void popupMenuCanceled(PopupMenuEvent e) {
                    if (button != null) {
                        button.setSelected(false);
                    }
                }
            });
        }
    }
}
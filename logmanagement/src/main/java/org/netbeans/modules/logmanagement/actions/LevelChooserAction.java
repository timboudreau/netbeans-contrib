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
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.netbeans.modules.logmanagement.Logger;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * @author Anuradha G
 */
public class LevelChooserAction extends AbstractAction {

    private final Logger logger;

    public LevelChooserAction(Logger logger) {
        putValue(Action.SHORT_DESCRIPTION, NbBundle.getMessage(LevelChooserAction.class, "level"));
        putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(Utilities.loadImage("org/netbeans/modules/logmanagement/resources/level.png", true)));
        this.logger = logger;
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof JButton) {
            final JButton button = (JButton) source;
            button.setFocusPainted(false);
            JPopupMenu menu = new JPopupMenu();
            ButtonGroup group = new ButtonGroup();
            Level selected = Level.parse(logger.getLevel());
            for (Level lvl : new Level[] {Level.ALL, Level.CONFIG, Level.FINE, Level.FINER, Level.FINEST, Level.INFO, Level.OFF, Level.SEVERE, Level.WARNING}) {
                JRadioButtonMenuItem mi = new JRadioButtonMenuItem(new LevelAction(lvl));
                menu.add(mi);
                group.add(mi);
                if (lvl.equals(selected)) {
                    mi.setSelected(true);
                }
            }
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
            Point point = button.getLocationOnScreen();
            menu.setInvoker(button);
            menu.setVisible(true);
            menu.setLocation(point.x+ (button.getWidth()), point.y);
        }
    }

    private class LevelAction extends AbstractAction {

        private Level level;

        public LevelAction(Level level) {
            this.level = level;
            putValue(NAME, level.getLocalizedName());
        }

        public void actionPerformed(ActionEvent e) {
            logger.setLevel(level.getName());
        }
    }
}

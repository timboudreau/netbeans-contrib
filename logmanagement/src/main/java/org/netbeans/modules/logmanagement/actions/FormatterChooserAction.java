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
import java.util.logging.Formatter;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import java.util.logging.XMLFormatter;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G
 */
public class FormatterChooserAction extends AbstractAction {

    private static final long serialVersionUID = 1l;
    private StreamHandler customHandler;
    private JPopupMenu menu = new JPopupMenu();
    private ButtonGroup group = new ButtonGroup();
    private boolean wormup;

    public FormatterChooserAction(StreamHandler customHandler) {
        putValue(Action.SHORT_DESCRIPTION, NbBundle.getMessage(FormatterChooserAction.class, "formatter"));
        putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(Utilities.loadImage("org/netbeans/modules/logmanagement/resources/formatter.png", true)));
        this.customHandler = customHandler;
        SimpleFormatter simpleFormatter = new SimpleFormatter();
        JRadioButtonMenuItem simpale = new JRadioButtonMenuItem(new LevelAction(NbBundle.getMessage(FormatterChooserAction.class, "default_formatter"), simpleFormatter));
        menu.add(simpale);
        group.add(simpale);

        XMLFormatter xmlFormatter = new XMLFormatter();
        JRadioButtonMenuItem xmlItem = new JRadioButtonMenuItem(new LevelAction(NbBundle.getMessage(FormatterChooserAction.class, "xml_formatter"), xmlFormatter));
        menu.add(xmlItem);
        group.add(xmlItem);

        if (customHandler.getFormatter() instanceof SimpleFormatter) {
            simpale.setSelected(true);
        } else if( customHandler.getFormatter() instanceof XMLFormatter){
            xmlItem.setSelected(true);

        }


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

    private class LevelAction extends AbstractAction {

        private Formatter formatter;

        public LevelAction(String name, Formatter formatter) {
            this.formatter = formatter;
            putValue(NAME, name);
        }

        public void actionPerformed(ActionEvent e) {
            customHandler.setFormatter(formatter);

        }
    }
}
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.tanui;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import org.openide.modules.ModuleInstall;

/**
 * @author Tim Boudreau
 */
public class Installer extends ModuleInstall {
    static {
        if (UIManager.getLookAndFeel() instanceof MetalLookAndFeel) {
            MetalLookAndFeel.setCurrentTheme(new Theme());
        }
        adjustUi();
    }
    protected static final String TAB_ACTIVE_SELECTION_BACKGROUND = "TabRenderer.selectedActivatedBackground"; //NOI18N
    protected static final String TAB_ACTIVE_SELECTION_FOREGROUND = "TabRenderer.selectedActivatedForeground"; //NOI18N
    protected static final String TAB_SELECTION_FOREGROUND = "TabRenderer.selectedForeground"; //NOI18N
    protected static final String TAB_SELECTION_BACKGROUND = "TabRenderer.selectedBackground"; //NOI18N

    private static void adjustUi() {
        Color bg = new Color(236, 233, 216);
        Color darker = new Color(220, 218, 194);
        Set<Object> keys = new HashSet<Object>(UIManager.getLookAndFeelDefaults().keySet());
        for (Object o : keys) {
            Object val = UIManager.get(o);
            if (val instanceof Color) {
                Color c = (Color) val;
                if (isWhite(c)) {
                    UIManager.put(o, bg);
                } else if (isGray(c)) {
                    UIManager.put(o, darker);
                }
            }
        }
        UIManager.put("white", bg); //NOI18N
        if (UIManager.getLookAndFeel() instanceof MetalLookAndFeel) {
            UIManager.put("control", bg); //NOI18N
            UIManager.put("info", bg); //NOI18N
            UIManager.put("ToolBar.background", bg); //NOI18N
            UIManager.put("tab_unsel_fill", bg); //NOI18N
            UIManager.put("tab_unsel_fill", Color.BLUE); //NOI18N
            UIManager.put("controlShadow", new Color(220, 218, 194)); //NOI18N
            UIManager.put("controlHighlight", new Color(245, 240, 228)); //NOI18N
            UIManager.put("desktop", new Color(220, 218, 194)); //NOI18N
            UIManager.put("controlDkShadow", bg.darker()); //NOI18N
            UIManager.put("nb_workplace_fill", bg.darker()); //NOI18N
            UIManager.put(TAB_ACTIVE_SELECTION_BACKGROUND, new Color(216, 214, 190));
            UIManager.put(TAB_ACTIVE_SELECTION_FOREGROUND, new Color (255, 255, 232));
            UIManager.put(TAB_SELECTION_BACKGROUND, new Color(245, 240, 228));
            UIManager.put(TAB_SELECTION_FOREGROUND, new Color (40, 40, 150));
            UIManager.put("Tree.selectionBackground", new Color(216,214,190)); //NOI18N
            UIManager.put("Tree.selectionForeground", Color.WHITE); //NOI18N
        }
        UIManager.put("Tree.altbackground", darker); //NOI18N
        UIManager.put("Tree.dropLineColor", new Color(255,255,238)); //NOI18N
        UIManager.put("text", bg); //NOI18N
        UIManager.put("Tree.background", bg); //NOI18N
        UIManager.put("Table.background", bg); //NOI18N
        UIManager.put("List.background", bg); //NOI18N
        UIManager.put("TextField.background", bg); //NOI18N
        UIManager.put("ComboBox.background", bg); //NOI18N
        UIManager.put("ComboBox.listBackground", bg); //NOI18N
        UIManager.put("Table.dropLineColor", new Color (255, 255, 180)); //NOI18N
        UIManager.put("EditorPane.background", bg); //NOI18N
        UIManager.put("TabbedPane.background", bg); //NOI18N
        UIManager.put("Menu.selectionBackground", new Color(245, 236, 225)); //NOI18N
        UIManager.put("MenuItem.selectionBackground", new Color(248, 246, 232)); //NOI18N
        UIManager.put("Menu.selectionForeground", Color.BLACK); //NOI18N
        UIManager.put("MenuItem.selectionForeground", Color.BLACK); //NOI18N
        UIManager.put("ComboBox.border", BorderFactory.createLineBorder(bg.darker())); //NOI18N
    }
    private static final int LIMIT = 252;

    private static boolean isWhite(Color c) {
        return c.getRed() > LIMIT && c.getGreen() > LIMIT && c.getBlue() > LIMIT;
    }

    private static boolean isGray(Color c) {
        boolean equal = c.getRed() == c.getGreen() && c.getRed() == c.getBlue();
        if (equal && c.getRed() >= 128 && c.getRed() <= 180) {
            return true;
        }
        return false;
    }

}

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

package org.netbeans.modules.tasklist.usertasks.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.util.ResourceBundle;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * ListCellRenderer for priorities
 *
 * @author tl
 */
public class PriorityListCellRenderer extends DefaultListCellRenderer {
    private static final Image LOW = Utilities.loadImage(
        "org/netbeans/modules/tasklist/usertasks/renderers/low.gif"); // NOI18N
    private static final Image MEDIUM_LOW = Utilities.loadImage(
        "org/netbeans/modules/tasklist/usertasks/renderers/medium-low.gif"); // NOI18N
    private static final Image HIGH = Utilities.loadImage(
        "org/netbeans/modules/tasklist/usertasks/renderers/high.gif"); // NOI18N
    private static final Image MEDIUM_HIGH = Utilities.loadImage(
        "org/netbeans/modules/tasklist/usertasks/renderers/medium-high.gif"); // NOI18N
    private static final Image MEDIUM = Utilities.loadImage(
        "org/netbeans/modules/tasklist/usertasks/renderers/empty.gif"); // NOI18N
    

    private static final long serialVersionUID = 1;

    private static String[] TAGS;

    /** Keys for the Bundle.properties */
    private static final String[] PRIORITIES_KEYS = {
        "PriorityHigh",  // NOI18N
        "PriorityMediumHigh", // NOI18N
        "PriorityMedium", // NOI18N
        "PriorityMediumLow", // NOI18N
        "PriorityLow" // NOI18N
    };

    static {
        TAGS = new String[PRIORITIES_KEYS.length];
        ResourceBundle rb = NbBundle.getBundle(PriorityListCellRenderer.class);
        for (int i = 0; i < PRIORITIES_KEYS.length; i++) {
            TAGS[i] = rb.getString(PRIORITIES_KEYS[i]);
        }
    }

    /**
     * Default colors for diferent priorities
     * [0] - high, [1] - medium-high, ...
     */
    public static final Color[] COLORS = {
        new Color(221, 0, 0),
        new Color(255, 128, 0),
        Color.black,
        new Color(0, 187, 0),
        new Color(0, 128, 0)
    };

    private ImageIcon icon = new ImageIcon();
    
    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value != null) {
            int prio = ((Integer) value).intValue();
            setText(UserTask.getPriorityNames()[prio - 1]);
            if (!isSelected) {
                setForeground(PriorityListCellRenderer.COLORS[prio - 1]);
            }
            
            Image im;
            switch (prio) {
                case UserTask.HIGH:
                    im = HIGH;
                    break;
                case UserTask.LOW:
                    im = LOW;
                    break;
                case UserTask.MEDIUM_HIGH:
                    im = MEDIUM_HIGH;
                    break;
                case UserTask.MEDIUM_LOW:
                    im = MEDIUM_LOW;
                    break;
                default:
                    im = MEDIUM;
            }
            icon.setImage(im);
            setIcon(icon);
        } else {
            icon.setImage(MEDIUM);
            setIcon(icon);
        }
        return this;
    }
}

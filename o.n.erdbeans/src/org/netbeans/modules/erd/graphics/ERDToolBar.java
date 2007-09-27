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
 * DesignerToolBar.java
 *
 * Created on Jul 9, 2004
 */
package org.netbeans.modules.erd.graphics;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import org.netbeans.modules.erd.editor.ERDTopComponent;



public class ERDToolBar extends JToolBar {
    static ResourceBundle bundle = NbBundle.getBundle("org.netbeans.modules.erd.graphics.Bundle");
    public static final Image zoominImage = Utilities.loadImage ("org/netbeans/modules/erd/resources/zoomin.gif"); // NOI18N
    public static final Image zoomoutImage = Utilities.loadImage ("org/netbeans/modules/erd/resources/zoomout.gif"); // NOI18N
    public static final Image gridImage = Utilities.loadImage ("org/netbeans/modules/erd/resources/snap-to-grid.png"); // NOI18N
    public static final Image layoutImage = Utilities.loadImage ("org/netbeans/modules/erd/resources/realign-screens.png"); // NOI18N

   ERDTopComponent topComponent;

    public ERDToolBar (ERDTopComponent topComponent) {
        super ();
        this.topComponent = topComponent;
        initialize ();
    }

    private void initialize () {
        this.add (createToolBarSeparator ());

        JButton zoomin = new JButton (new ImageIcon (zoominImage));
        zoomin.setToolTipText (bundle.getString("HINT_ZoomIn")); // NOI18N
        initButton (zoomin);
        zoomin.addActionListener (new ActionListener () {
            public void actionPerformed (ActionEvent e) {
                topComponent.zoomIn ();
            }
        });
        this.add (zoomin);
        JButton zoomout = new JButton (new ImageIcon (zoomoutImage));
        zoomout.setToolTipText (bundle.getString("HINT_ZoomOut")); // NOI18N
        initButton (zoomout);
        zoomout.addActionListener (new ActionListener () {
            public void actionPerformed (ActionEvent e) {
               topComponent.zoomOut ();
            }
        });
        this.add (zoomout);

       

        this.add (createToolBarSeparator ());

        JButton layout = new JButton (new ImageIcon (layoutImage));
        layout.setToolTipText(bundle.getString("HINT_Realign")); // NOI18N
        initButton(layout);
        layout.addActionListener (new ActionListener () {
            public void actionPerformed (ActionEvent e) {
                topComponent.invokeLayout();
            }
        });
        this.add (layout);
    }

    private Separator createToolBarSeparator () {
        Separator toolBarSeparator = new Separator ();
        toolBarSeparator.setOrientation (JSeparator.VERTICAL);
        setFloatable(false);
        setRollover(true);
        setBorder(new EmptyBorder(0, 0, 0, 0));
        return toolBarSeparator;
    }

    //todo move to some helper class
    public static void initButton(AbstractButton button) {
        if (!("Windows".equals(UIManager.getLookAndFeel().getID()) // NOI18N
            && (button instanceof JToggleButton))) {
            button.setBorderPainted(false);
        }
        button.setOpaque(false);
        button.setFocusPainted(false);
    }
}

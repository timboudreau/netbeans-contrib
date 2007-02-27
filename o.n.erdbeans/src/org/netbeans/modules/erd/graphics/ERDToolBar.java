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

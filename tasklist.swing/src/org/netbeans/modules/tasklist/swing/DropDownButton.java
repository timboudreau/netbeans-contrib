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

package org.netbeans.modules.tasklist.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import org.openide.util.Utilities;

/**
 * JButton that shows a JPopupMenu when pressed.
 *
 * @author tl
 */
public class DropDownButton extends JButton {
    private JPopupMenu pm;
    
    /**
     * Constructor.
     * 
     * @param text text for the button 
     * @param pm a menu
     */
    public DropDownButton(String text, JPopupMenu pm) {
        super(text);
        this.pm = pm;
        setIcon(new ImageIcon(Utilities.loadImage(
                "org/netbeans/modules/tasklist/swing/dropdown.png")));
        super.setHorizontalTextPosition(JButton.LEFT);
        setVerticalTextPosition(JButton.BOTTOM);
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JPopupMenu m = createMenu();
                m.setVisible(true);
                m.show(DropDownButton.this, 
                        getWidth() - m.getWidth(), getHeight());
            }
        });
    }
    
    /**
     * Constructor.
     */
    public DropDownButton() {
        this("", new JPopupMenu());
    }
    
    /**
     * Creates a popup.
     * 
     * @return created menu 
     */
    public JPopupMenu createMenu() {
        return pm;
    }
}

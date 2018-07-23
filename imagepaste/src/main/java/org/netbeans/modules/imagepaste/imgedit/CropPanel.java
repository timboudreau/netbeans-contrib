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
 * CropPanel.java
 *
 * Created on October 15, 2006, 2:36 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.imagepaste.imgedit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Tim Boudreau
 */
public class CropPanel extends JPanel implements ActionListener, ChangeListener {
    CropComponent comp = new CropComponent();
    JLabel lbl = new JLabel ("Click and drag to crop, Ctrl-Z/Y undo/redo, ENTER to commit");
    JPanel pnl = new JPanel();
    JButton undo = new JButton("Undo");
    JButton redo = new JButton ("Redo");
    JButton done = new JButton ("Done");
    JButton border = new JButton ("Add Border");

    /** Creates a new instance of CropPanel */
    public CropPanel() {
        setLayout (new BorderLayout());
        add (pnl, BorderLayout.NORTH);
        pnl.setLayout (new FlowLayout (FlowLayout.LEADING));
        pnl.setBorder (BorderFactory.createEmptyBorder(5,5,5,5));
        pnl.add (undo);
        undo.setDisplayedMnemonicIndex(0);
        undo.setMnemonic('U');
        pnl.add (redo);
        redo.setDisplayedMnemonicIndex(0);
        redo.setMnemonic('R');
        pnl.add (done);
        pnl.add (border);
        border.setDisplayedMnemonicIndex(0);
        border.setMnemonic('B');
        undo.addActionListener(this);
        redo.addActionListener(this);
        border.addActionListener(this);
//        done.addActionListener(this);
        pnl.add (lbl);
        undo.setEnabled (false);
        redo.setEnabled (false);
        add (new JScrollPane(comp), BorderLayout.CENTER);
        comp.addChangeListener(this);
    }

    public void requestFocus() {
        comp.requestFocus();
    }

    public void addActionListener (ActionListener al) {
        comp.addActionListener (al);
    }
    public void removeActionListener (ActionListener al) {
        comp.removeActionListener (al);
    }

    public BufferedImage getImage() {
        return comp.getImage();
    }

    public void setImage (BufferedImage img) {
        comp.setImage (img);
    }

    public void actionPerformed(ActionEvent e) {
        Object b = e.getSource();
        if (b == undo) {
            comp.undo();
        } else if (b == redo) {
            comp.redo();
        } else if (b == done) {
            comp.fire();
        } else if (b == border) {
            Graphics2D g2d = getImage().createGraphics();
            int w = getImage().getWidth();
            int h = getImage().getHeight();
            g2d.setColor (Color.BLACK);
            g2d.drawRect(0, 0, w - 1, h - 1);
            g2d.dispose();
            repaint();
        }
    }

    public void stateChanged(ChangeEvent e) {
        undo.setEnabled (comp.canUndo());
        redo.setEnabled (comp.canRedo());
    }

}

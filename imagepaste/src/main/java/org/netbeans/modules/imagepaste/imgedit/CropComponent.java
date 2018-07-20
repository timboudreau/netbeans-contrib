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
 * CropComponent.java
 *
 * Created on October 15, 2006, 1:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.imagepaste.imgedit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Tim Boudreau
 */
public class CropComponent extends JComponent implements KeyListener {

    //XXX optimize repaint logic

    private BufferedImage image;
    private RectangleTool tool;
    /** Creates a new instance of CropComponent */
    public CropComponent() {
        tool = new RectangleTool();
        tool.attach(this);
        addKeyListener (this);
        setFocusable(true);
        setDoubleBuffered(false);
    }

    private List listeners = Collections.synchronizedList (new LinkedList());
    public void addActionListener (ActionListener al) {
        listeners.add (al);
    }

    public void removeActionListener (ActionListener al) {
        listeners.remove(al);
    }

    void fire () {
        ActionEvent ae = new ActionEvent (this,
                ActionEvent.ACTION_PERFORMED, "foo");
        ActionListener[] l = (ActionListener[])
                listeners.toArray (new ActionListener[0]);
        for (int i = 0; i < l.length; i++) {
            l[i].actionPerformed(ae);
        }
    }

    private List clisteners = Collections.synchronizedList (new LinkedList());
    public void addChangeListener (ChangeListener al) {
        clisteners.add (al);
    }

    public void removeChangeListener (ChangeListener al) {
        clisteners.remove(al);
    }

    void fireChange () {
        ChangeEvent ae = new ChangeEvent (this);
        ChangeListener[] l = (ChangeListener[])
                clisteners.toArray (new ChangeListener[0]);
        for (int i = 0; i < l.length; i++) {
            l[i].stateChanged(ae);
        }
    }

    public void addNotify(){
        super.addNotify();
        requestFocus();
    }

    private final Rectangle imageBounds = new Rectangle();
    public void setImage (BufferedImage image) {
        undo.clear();
        redo.clear();
        Dimension d = getPreferredSize();
        this.image = image;
        updateImageBounds();
        Dimension d2 = getPreferredSize();
        firePropertyChange ("preferredSize", d, d2);
        invalidate();
        revalidate();
        repaint();
    }

    public Dimension getPreferredSize() {
        Dimension result = new Dimension (300, 300);
        Insets ins = getInsets();
        if (image != null) {
            result.width = image.getWidth();
            result.height = image.getHeight();
        }
        result.width += ins.left + ins.right;
        result.height += ins.top + ins.bottom;
        return result;
    }

    public BufferedImage getImage() {
        return image;
    }

    public Insets getInsets() {
        return new Insets (0,0,0,0);
    }

    public void setBorder (Border b) {
        throw new UnsupportedOperationException();
    }
    public void paint (Graphics g) {
        g.setColor (Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paint(g);
    }

    private final Rectangle bds = new Rectangle();
    public void paintComponent (Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.getClipBounds(bds);
        g.setColor (Color.WHITE);
        g.fillRect(bds.x, bds.y, bds.width, bds.height);
        Insets ins = getInsets();
        if (imageBounds.contains (bds)) {
            g2d.drawImage(image,
                    bds.x, bds.y, bds.x + bds.width, bds.y + bds.height,
                    bds.x, bds.y, bds.x + bds.width, bds.y + bds.height,
                    this);
            tool.paint (g2d);
        } else {
            g2d.drawRenderedImage(image, AffineTransform.getTranslateInstance(0,0));
            tool.paint(g2d);
        }
    }

    private Stack undo = new Stack();
    private Stack redo = new Stack();

    void crop (MutableRectangle r) {
        redo.clear();
        undo.push (image);
        Rectangle rr = r.normalize();
        rr = imageBounds.intersection(rr);
        image = image.getSubimage(rr.x, rr.y, rr.width, rr.height);
        rr.translate(-rr.x, -rr.y);
        imageBounds.setBounds (rr);
        fireChange();
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_Z && (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {
            undo();
        } else if (e.getKeyCode() == KeyEvent.VK_Y && (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {
            redo();
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            fire();
        }
    }

    void undo() {
        if (!undo.isEmpty()) {
            redo.push(image);
            image = (BufferedImage) undo.pop();
            updateImageBounds();
            fireChange();
            invalidate();
            revalidate();
            repaint();
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    void redo() {
        if (!redo.isEmpty()) {
            undo.push(image);
            image = (BufferedImage) redo.pop();
            updateImageBounds();
            fireChange();
            invalidate();
            revalidate();
            repaint();
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    private void updateImageBounds() {
        imageBounds.x = 0;
        imageBounds.y = 0;
        imageBounds.width = image.getWidth();
        imageBounds.height = image.getHeight();
    }

    boolean canUndo() {
        return !undo.isEmpty();
    }

    boolean canRedo() {
        return !redo.isEmpty();
    }
}

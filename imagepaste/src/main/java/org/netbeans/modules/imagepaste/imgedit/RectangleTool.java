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
 * RectangleTool.java
 *
 * Created on September 28, 2006, 6:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.imagepaste.imgedit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 *
 * @author Tim Boudreau
 */
public class RectangleTool implements  MouseMotionListener, MouseListener, KeyListener {
    MutableRectangle rect;
    private Color color = Color.BLUE;
    private boolean fill;
    private int draggingCorner;
    private CropComponent c;

    /** Creates a new instance of RectangleTool */
    public RectangleTool() {
    }

    public void attach(CropComponent c) {
        assert this.c == null;
        this.c = c;
        c.addMouseListener (this);
        c.addMouseMotionListener (this);
        c.addKeyListener(this);
        c.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        change();
    }

    public void detach(CropComponent c) {
        assert this.c != null;
        assert this.c == c;
        assert c != null;
        this.c = null;
        rect = null;
        TEMPLATE_RECTANGLE.setBounds (0, 0, NO_ANCHOR_SIZE, NO_ANCHOR_SIZE);
        c.removeMouseListener(this);
        c.removeMouseMotionListener(this);
        c.removeKeyListener(this);
        c.setCursor (Cursor.getDefaultCursor());
    }

    BasicStroke stroke = new BasicStroke (1F);
    public void setThickness (float val) {
        if (getClass() == RectangleTool.class) {
            stroke = new BasicStroke (val);
        } else {
            stroke = new BasicStroke (val, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        }
        change();
    }

    public float getThickness() {
        return stroke.getLineWidth();
    }

    public void setColor (Color c) {
        this.color = c;
        change();
    }

    protected final void change() {
        if (rect != null && this.c != null) {
//            this.c.repaint(rect);
        }
    }

    public void setFill (boolean val) {
        this.fill = val;
        change();
    }

    public Rectangle getRectangle() {
        return rect == null ? null : new Rectangle (rect);
    }

    private void setDraggingCorner(int draggingCorner) {
        this.draggingCorner = draggingCorner;
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE && e.getModifiersEx() == 0) {
            clear();
        }
    }

    private void clear() {
        rect = null;
        draggingCorner = MutableRectangle.ANY;
        armed = false;
        if (c != null) {
            c.repaint();
        }
    }

    public String toString() {
        return "Rectangle";
    }

    private Rectangle paintedRect = new Rectangle();
    public void paint(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.setStroke(stroke);
        Rectangle toPaint = rect == null ? TEMPLATE_RECTANGLE : rect;
        paintedRect.setBounds (toPaint);
        if (armed || committing) {
            draw (toPaint, g2d, true);
        }
        merge = null;
    }

    protected void draw (Rectangle toPaint, Graphics2D g2d, boolean fill) {
        if (fill) {
            Color c = new Color (255, 200, 200, 100);
            g2d.setColor(c);
            g2d.fillRect(toPaint.x, toPaint.y, toPaint.width, toPaint.height);
            g2d.setColor (new Color (200, 127, 127, 180));
            g2d.drawRect(toPaint.x, toPaint.y, toPaint.width, toPaint.height);
        } else {
            g2d.drawRect(toPaint.x, toPaint.y, toPaint.width, toPaint.height);
        }
    }

    public void mouseDragged(MouseEvent e) {
       mouseMoved (e);
    }

    public void mouseMoved(MouseEvent e) {
        armed = true;
        Point p = e.getPoint();
        TEMPLATE_RECTANGLE.setLocation(p);
        if (rect != null) {
            repaintWithRect();
            dragged (e.getPoint(), e.getModifiersEx());
        } else {
            repaintWithRect();
        }
    }

    private void dragged (Point p, int modifiers) {
        int currCorner = draggingCorner;
        int corner = rect.setPoint(p, currCorner);

        if ((modifiers & java.awt.event.KeyEvent.SHIFT_DOWN_MASK) != 0) {
            rect.makeSquare(currCorner);
        }
        if (corner == -2 || (corner != currCorner && corner != -1)) {
            if (corner != -2) {
                setDraggingCorner(corner);
            }
        }
        repaintWithRect();
    }


    int NO_ANCHOR_SIZE = 18;
    private Rectangle TEMPLATE_RECTANGLE = new Rectangle (0, 0, NO_ANCHOR_SIZE, NO_ANCHOR_SIZE);
    private void repaintNoRect() {
        c.repaint();
    }

    private Rectangle merge = new Rectangle();
    private void repaintWithRect() {
        if (merge == null) {
            merge = new Rectangle();
            merge.setBounds (paintedRect);
        }
        if (rect != null) {
            merge = merge.union(rect);
        } else {
            merge = merge.union(TEMPLATE_RECTANGLE);
        }
        //+1 compensate for drawRect around it
        c.repaint (merge.x, merge.y, merge.width + 1, merge.height + 1);
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        c.requestFocus();
        Point p = e.getPoint();
        TEMPLATE_RECTANGLE.setLocation(p);
        if (rect == null) {
            p.x ++;
            p.y ++;
            rect = new MutableRectangle (e.getPoint(), p);
            draggingCorner = rect.nearestCorner(p);
        }
    }

    private static final int CLICK_DIST = 7;
    public void mouseReleased(MouseEvent e) {
        Point p = e.getPoint();
        boolean inBounds = c.contains(p);
        if (rect != null && inBounds) {
            int nearestCorner = rect.nearestCorner(p);
            if (p.distance(rect.getLocation()) < CLICK_DIST) {
                //do nothing
                setDraggingCorner (nearestCorner);
                rect.setLocation(p);
            } else {
                setDraggingCorner(nearestCorner);
                rect.setPoint(p, nearestCorner);
                armed = false;
                commit();
                clear();
            }
            change();
            repaintNoRect();
        }
    }

    boolean committing = false;
    private void commit() {
        committing = true;
        c.crop (rect);
        committing = false;
    }

    public void mouseEntered(MouseEvent e) {
    }

    boolean armed;
    public void mouseExited(MouseEvent e) {
        armed = false;
        repaintWithRect();
        TEMPLATE_RECTANGLE.setLocation(-100, -100);
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        if (rect == null) {
            return;
        }
        Point p = rect.getLocation();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DOWN :
                p.y ++;
                break;
            case KeyEvent.VK_UP :
                p.y--;
                break;
            case KeyEvent.VK_LEFT :
                p.x --;
                break;
            case KeyEvent.VK_RIGHT :
                p.x ++;
                break;
            case KeyEvent.VK_ENTER :
                commit();
                break;
        }
    }
}

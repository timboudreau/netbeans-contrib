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

package org.netbeans.lib.graphlayout;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.Iterator;

/**
 *
 * @author jarda
 */
final class Renderer extends javax.swing.JComponent
implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener, java.awt.event.ActionListener {
    private Graph graph;
    private Vertex dragging;
    private javax.swing.Timer timer;
    private java.util.HashMap colors = new java.util.HashMap ();
    private int colorindex = 0;
    static final Color[] basecolors = {
        Color.CYAN, Color.YELLOW, Color.BLUE, Color.RED, Color.MAGENTA, Color.ORANGE, Color.DARK_GRAY
    };
    private int zoom = 100;
    private AffineTransform lastTransform;
    
    private java.awt.Shape edge;

    Renderer (Graph g) {
        this.graph = g;
        
        // shape of the edge
        java.awt.Polygon p = new java.awt.Polygon ();
        p.addPoint (0, 0);
        p.addPoint (-5, 20);
        p.addPoint (-1, 20);
        p.addPoint (-2, 100);
        p.addPoint (2, 100);
        p.addPoint (1, 20);
        p.addPoint (5, 20);
        p.addPoint (0, 0);
        edge = p;
        
        addMouseListener(this);
        setPreferredSize (new java.awt.Dimension (600, 600));
        g.initialize (getPreferredSize ());
    }
    
    private Color findColor (Vertex v) {
        Color c = (Color)colors.get (v.info);
        if (c == null) {
            if (colorindex == basecolors.length) {
                colorindex = 0;
            }
            c = basecolors[colorindex++];
            colors.put (v.info, c);
        }
        return c;
    }
    
    private void paintNode (java.awt.Graphics g, Vertex v, java.awt.FontMetrics fm) {
        Rectangle r = v.getRectangle ();
        if (r == null || Math.abs (v.x - r.x - r.width / 2) > 2 || Math.abs (v.y - r.y - r.height / 2) > 2) {
            int x = (int)v.x;
            int y = (int)v.y;
            int w = fm.stringWidth (v.name) + 10;
            int h = fm.getHeight () + 4;
            
            r = new Rectangle (x - w/2, y - h / 2, w, h);
            v.setRectangle (r);
        }
        
        if (dragging == v) {
            g.setColor (Color.gray);
        } else {
            Color c = findColor (v);
            
            if (v.isFixed ()) {
                c = c.darker ();
            }
            
            g.setColor (c);
        }
        g.fillRect (r.x, r.y, r.width, r.height);
        
        g.setColor (Color.black);
        g.drawRect (r.x, r.y, r.width - 1, r.height - 1);
        g.drawString (v.name, r.x + 5, r.y + fm.getAscent ());
    }
    
    protected void paintComponent (java.awt.Graphics g) {
        java.awt.Graphics2D g2d = (java.awt.Graphics2D)g;
        
        java.awt.Dimension d = getSize ();
        g2d.translate (d.width / 2, d.height / 2);
        g2d.scale (zoom / 100.0, zoom / 100.0);
        
        for (Iterator it = graph.edges.iterator (); it.hasNext (); ) {
            Edge e = (Edge)it.next ();
            int x1 = e.getVertex1 ().getX ();
            int y1 = e.getVertex1 ().getY ();
            int x2 = e.getVertex2 ().getX ();
            int y2 = e.getVertex2 ().getY ();
            Color c = Color.red.brighter ();
            for (int i = e.strength; i > 0; i--) {
                c = c.darker ();
            }
            g.setColor (c) ;
            
            int dx = x1 - x2;
            int dy = y1 - y2;
            if (!e.getVertex1 ().isFixed () && !e.getVertex2 ().isFixed ()) {
                g2d.drawLine (x1, y1, x2, y2);
                continue;
            }
            
            AffineTransform af = new AffineTransform ();
            af.translate (x2, y2);
            if (dx == 0) {
                // no rotation, just resize
                af.scale (1, dy / 100.0);
            } else if (dy == 0) {
                af.rotate (Math.PI / 2);
                af.scale (dx / 100.0, 1);
            } else {
                double len = Math.sqrt (dx * dx + dy * dy);
                double theta = Math.atan (((double)dx) / ((double)-dy));
                
                if (dy < 0) {
                    theta = theta + Math.PI;
                }
                
                af.rotate (theta);
                af.scale (1.0, len / 100.0);
            }
            
            g2d.fill (af.createTransformedShape (this.edge));
        }
        
        java.awt.FontMetrics fm = g.getFontMetrics ();
        for (Iterator it = graph.vertexes.iterator (); it.hasNext (); ) {
            Vertex v = (Vertex)it.next ();
            paintNode (g, v, fm);
        }
        
        lastTransform = g2d.getTransform ();
    }
    
    public void setZoom (int zoom) {
        this.zoom = zoom;
        repaint ();
    }

    //
    // mouse operations
    //
    
    private void popup (java.awt.event.MouseEvent e) {
        java.awt.geom.Point2D at;
        try {
             at = lastTransform.inverseTransform (e.getPoint (), null);
        } catch (java.awt.geom.NoninvertibleTransformException ex) {
            ex.printStackTrace();
            return;
        }
        
        final Vertex v = graph.findVertex ((int)at.getX (), (int)at.getY (), 20, 20);
        if (v == null) {
            return;
        }
        
        javax.swing.JPopupMenu menu = new javax.swing.JPopupMenu (v.name);
        final javax.swing.JMenuItem remove = menu.add ("Remove Vertex");
        final javax.swing.JMenuItem removeGroup = menu.add ("Remove All " + v.info);
        menu.addSeparator ();
        final javax.swing.JMenuItem freeze = menu.add ("Freeze Vertex");
        freeze.setEnabled (!v.isFixed ());
        final javax.swing.JMenuItem release = menu.add ("Unfreeze Vertex");
        menu.addSeparator ();
        release.setEnabled (v.isFixed ());
        final javax.swing.JMenuItem random = menu.add ("Randomize Vertex");

        class Action implements java.awt.event.ActionListener {
            public void actionPerformed (java.awt.event.ActionEvent ev) {
                repaint ();
                if (ev.getSource () == remove) {
                    graph.removeVertex (v);
                    return;
                }
                if (ev.getSource () == removeGroup) {
                    graph.removeGroup (v.info);
                    return;
                }
                if (ev.getSource () == freeze) {
                    v.setFixed (true);
                    return;
                }
                if (ev.getSource () == release) {
                    v.setFixed (false);
                    return;
                }
                if (ev.getSource () == random) {
                    v.x = Math.random () * ((double)getSize ().width);
                    v.y = Math.random () * ((double)getSize ().height);
                    return;
                }
            }
        }
        Action a = new Action ();
        remove.addActionListener (a);
        removeGroup.addActionListener (a);
        freeze.addActionListener (a);
        release.addActionListener (a);
        random.addActionListener (a);
        
        menu.show (this, e.getX (), e.getY ());
    }
    
    
    public void mouseClicked (java.awt.event.MouseEvent e) {
        if (e.isPopupTrigger ()) {
            popup (e);
            return;
        }
        
        if (e.getClickCount () != 2) {
            return;
        }
        java.awt.geom.Point2D at;
        try {
             at = lastTransform.inverseTransform (e.getPoint (), null);
        } catch (java.awt.geom.NoninvertibleTransformException ex) {
            ex.printStackTrace();
            return;
        }
        
        Vertex v = graph.findVertex ((int)at.getX (), (int)at.getY (), 20, 20);
        if (v != null && v != dragging) {
            v.setFixed (false);
            repaint ();
            e.consume ();
        }
    }
    
    public void mousePressed (java.awt.event.MouseEvent e) {
        if (e.isPopupTrigger ()) {
            popup (e);
            return;
        }
        
        java.awt.geom.Point2D at;
        try {
             at = lastTransform.inverseTransform (e.getPoint (), null);
        } catch (java.awt.geom.NoninvertibleTransformException ex) {
            ex.printStackTrace();
            return;
        }
        
        Vertex v = graph.findVertex ((int)at.getX (), (int)at.getY (), 20, 20);
        if (v == null) {
            return;
        }
        
        addMouseMotionListener (this);

        dragging = v;
        v.setFixed (true);
        
        repaint ();
        e.consume ();
    }
    
    public void mouseReleased (java.awt.event.MouseEvent e) {
        if (e.isPopupTrigger ()) {
            popup (e);
            return;
        }
        
        if (dragging == null) {
            return;
        }
        removeMouseMotionListener (this);

        java.awt.geom.Point2D at;
        try {
             at = lastTransform.inverseTransform (e.getPoint (), null);
        } catch (java.awt.geom.NoninvertibleTransformException ex) {
            ex.printStackTrace();
            return;
        }

        dragging.x = (int)at.getX ();
        dragging.y = (int)at.getY ();
        dragging.setFixed (true);
        dragging = null;
        
        repaint ();
        e.consume ();
    }
    
    public void mouseEntered (java.awt.event.MouseEvent e) {
    }
    
    public void mouseExited (java.awt.event.MouseEvent e) {
    }
    
    public void mouseDragged (java.awt.event.MouseEvent e) {
        if (dragging == null) return;
        
        java.awt.geom.Point2D at;
        try {
             at = lastTransform.inverseTransform (e.getPoint (), null);
        } catch (java.awt.geom.NoninvertibleTransformException ex) {
            ex.printStackTrace();
            return;
        }

        dragging.x = (int)at.getX ();
        dragging.y = (int)at.getY ();
        dragging.setFixed (true);
        repaint ();
        e.consume ();
    }
    
    public void mouseMoved (java.awt.event.MouseEvent e) {
    }
    

    public void actionPerformed (java.awt.event.ActionEvent ev) {
        if (isShowing ()) {
            int sleep = graph.nextMove ();
            invalidate ();
            repaint ();
            timer.setDelay (sleep);
        } else {
            timer.setDelay (100);
        }
        
    }

    public void addNotify () {
        super.addNotify();
        timer = new javax.swing.Timer (50, this);
        timer.start ();
    }

    public void removeNotify () {
        timer.stop ();
        timer = null;
        super.removeNotify();
    }
}

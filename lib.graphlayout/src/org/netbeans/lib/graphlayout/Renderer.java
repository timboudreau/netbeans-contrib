/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.lib.graphlayout;

import java.awt.Color;
import java.awt.Rectangle;
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
    private static Color[] basecolors = {
        Color.CYAN, Color.YELLOW, Color.BLUE, Color.RED, Color.MAGENTA, Color.ORANGE, Color.BLACK
    };
    private int zoom = 100;

    Renderer (Graph g) {
        this.graph = g;
        addMouseListener(this);
        setPreferredSize (new java.awt.Dimension (600, 600));
        g.initialize (getPreferredSize ());
    }
    
    private Color findColor (Vertex v) {
        Color c = (Color)colors.get (v.info);
        if (c == null) {
            c = basecolors[colorindex++];
            colors.put (v.info, c);
        }
        return c;
    }
    
    private void paintNode (java.awt.Graphics g, Vertex v, java.awt.FontMetrics fm) {
        Rectangle r = v.getRectangle ();
        if (r == null || Math.abs (v.x - r.x - r.width / 2) > 1 || Math.abs (v.y - r.y - r.height / 2) > 1) {
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
        g.fillRect (zoom (r.x), zoom (r.y), r.width, r.height);
        
        g.setColor (Color.black);
        g.drawRect (zoom (r.x), zoom (r.y), r.width - 1, r.height - 1);
        g.drawString (v.name, zoom (r.x + 5), zoom (r.y) + fm.getAscent ());
    }
    
    protected void paintComponent (java.awt.Graphics g) {
        java.awt.Dimension d = computeSize ();
        setSize (d);
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
            g.drawLine (zoom (x1), zoom (y1), zoom (x2), zoom (y2));
        }
        
        java.awt.FontMetrics fm = g.getFontMetrics ();
        for (Iterator it = graph.vertexes.iterator (); it.hasNext (); ) {
            Vertex v = (Vertex)it.next ();
            paintNode (g, v, fm);
        }
    }
    
    public void setZoom (int zoom) {
        this.zoom = zoom;
        repaint ();
    }
    
    private int zoom (int value) {
        if (zoom == 100) return value;
        
        return value * zoom / 100;
    }

    private int unzoom (int value) {
        if (zoom == 100) return value;
        
        return value * 100 / zoom;
    }

    private java.awt.Dimension computeSize () {
        return new java.awt.Dimension (zoom ((int)graph.maxX), zoom ((int)graph.maxY));
    }
    /*
    public java.awt.Dimension getPreferredSize () {
        java.awt.Dimension d = computeSize ();
        return new java.awt.Dimension (Math.max (d.width, 300), Math.max (d.height, 300));
    }*/
    
    //
    // mouse operations
    //
    
    
    public void mouseClicked (java.awt.event.MouseEvent e) {
        if (e.getClickCount () != 2) {
            return;
        }
        
        Vertex v = graph.findVertex (unzoom (e.getX ()), unzoom (e.getY ()), 20, 20);
        if (v != null && v != dragging) {
            v.setFixed (false);
            repaint ();
            e.consume ();
        }
    }
    
    public void mousePressed (java.awt.event.MouseEvent e) {
        Vertex v = graph.findVertex (unzoom (e.getX ()), unzoom (e.getY ()), 20, 20);
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
        if (dragging == null) {
            return;
        }
        removeMouseMotionListener (this);

        dragging.x = unzoom (e.getX ());
        dragging.y = unzoom (e.getY ());
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
        
        dragging.x = unzoom (e.getX ());
        dragging.y = unzoom (e.getY ());
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

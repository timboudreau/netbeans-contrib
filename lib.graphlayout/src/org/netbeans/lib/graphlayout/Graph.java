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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/** Graph with vertexes and edges.
 *
 * @author jarda
 */
public final class Graph extends Object {
    final ArrayList vertexes = new ArrayList ();
    final ArrayList edges = new ArrayList ();
    
    private int middleSize = 100;
    private int gridSize = 300;
    private java.awt.Dimension size;
    double minX, maxX, minY, maxY;
    
    /** Creates a new instance of Graph */
    private Graph () {
    }
    
    public static Graph create () {
        return new Graph ();
    }
    
    public java.awt.Component createRenderer () {
        javax.swing.JPanel p = new javax.swing.JPanel ();
        p.setLayout (new java.awt.BorderLayout ());
        final Renderer r = new Renderer (this);
        final javax.swing.JScrollPane pane = new javax.swing.JScrollPane (r);
        p.add (java.awt.BorderLayout.CENTER, pane);
        
        javax.swing.JPanel controls = new javax.swing.JPanel ();
        p.add (java.awt.BorderLayout.SOUTH, controls);
        
        final javax.swing.JButton randomize = new javax.swing.JButton ("Randomize");
        controls.add (java.awt.BorderLayout.EAST, randomize);
        final javax.swing.JSlider slid = new javax.swing.JSlider (0, 200);
        slid.setValue (100);
        controls.add (java.awt.BorderLayout.CENTER, slid);
        final javax.swing.JButton singleton = new javax.swing.JButton ("Remove singletons");
        controls.add (java.awt.BorderLayout.WEST, singleton);
        
        class L implements java.awt.event.ActionListener, javax.swing.event.ChangeListener {
            public void actionPerformed (java.awt.event.ActionEvent ev) {
                if (ev.getSource () == randomize) {
                    slid.setValue (100);
                    initialize (pane.getSize ());
                    return;
                }
                
                if (ev.getSource () == singleton) {
                    Iterator it = vertexes.iterator ();
                    while (it.hasNext ()) {
                        Vertex v = (Vertex)it.next ();
                        if (v.isSingleton ()) {
                            it.remove ();
                        }
                    }
                    r.repaint ();
                }
            }

            public void stateChanged (javax.swing.event.ChangeEvent e) {
                r.setZoom (slid.getValue ());
            }
        }
        L l = new L ();
        randomize.addActionListener (l);
        singleton.addActionListener (l);
        slid.addChangeListener (l);
        
        return p;
    }
    
    public Vertex createVertex (String name, String description) {
        Iterator it = vertexes.iterator ();
        while (it.hasNext ()) {
            Vertex v = (Vertex)it.next ();
            if (name.equals (v.name)) {
                return v;
            }
        }
        
        Vertex v = new Vertex (name, description);
        vertexes.add (v);
        return v;
    }
    
    public Edge createEdge (Vertex v1, Vertex v2, int strength) {
        Edge e = new Edge (v1, v2, strength);
        edges.add (e);
        return e;
    }
    
    public Edge createEdge (String v1, String v2, int strength, boolean createVertexesIfNecessary) {
        Edge e = new Edge (
            findVertex (v1, createVertexesIfNecessary), 
            findVertex (v2, createVertexesIfNecessary), 
            strength
        );
        edges.add (e);
        return e;
    }
    
    private Vertex findVertex (String name, boolean createVertexesIfNecessary) {
        Iterator it = vertexes.iterator ();
        while (it.hasNext ()) {
            Vertex v = (Vertex)it.next ();
            if (name.equals (v.name)) {
                return v;
            }
        }
        if (!createVertexesIfNecessary) {
            throw new IllegalStateException ("No vertex with name " + name + " in " + vertexes); // NOI18N
        }
        
        return createVertex (name, name);
    }
    
    final Vertex findVertex (int x, int y, int deltaX, int deltaY) {
        if (vertexes.isEmpty ()) {
            return null;
        }
        
        java.util.ListIterator it = vertexes.listIterator (vertexes.size ());
        while (it.hasPrevious ()) {
            Vertex v = (Vertex)it.previous ();
            java.awt.Rectangle r = v.getRectangle ();
            if (r == null) {
                r = new java.awt.Rectangle ((int)v.x, (int)v.y, deltaX, deltaY);
            }

            if (r.contains (x, y)) {
                return v;
            }
        }
        return null;
    }
    
    //
    // forces computation
    //
    
    final void initialize (java.awt.Dimension dim) {
        // clean data in vertexes
        size = dim;
        for (Iterator it = vertexes.iterator (); it.hasNext (); ) {
            Vertex v = (Vertex)it.next ();
            if (v.isFixed ()) {
                continue;
            }
            v.x = Math.random () * ((double)dim.width);
            v.y = Math.random () * ((double)dim.height);
        }
    }
    
    /** Returns time in milis to wait before next move
     */
    final int nextMove () {
        for (Iterator it = edges.iterator (); it.hasNext (); ) {
            Edge e = (Edge)it.next ();
            Vertex v1 = e.getVertex1 ();
            Vertex v2 = e.getVertex2 ();

            v1.applyForce (v2.x, v2.y, e.strength, middleSize);
            v2.applyForce (v1.x, v1.y, e.strength, middleSize);
        }

        for (Iterator it = vertexes.iterator (); it.hasNext (); ) {
            Vertex v = (Vertex)it.next ();
            for (Iterator inner = vertexes.iterator (); inner.hasNext (); ) {
                Vertex n = (Vertex)inner.next ();
                if (v == n) continue;
                
                v.applyRepulsion (n.x, n.y, gridSize);
                n.applyRepulsion (v.x, v.y, gridSize);
            }
        }
        
        double minTime = 1.000;
        for (Iterator it = vertexes.iterator (); it.hasNext (); ) {
            Vertex v = (Vertex)it.next ();
            double opt = v.optimalTimeForForces ();
            if (opt < minTime) {
                minTime = opt;
            }
        }
        if (minTime < 0.005) {
            minTime = 0.005;
        }
        
        // clean data in vertexes
        for (Iterator it = vertexes.iterator (); it.hasNext (); ) {
            Vertex v = (Vertex)it.next ();
            v.applyAllForces (minTime);
            
            if (minX > v.x) minX = v.x;
            if (minY > v.y) minY = v.y;
            if (maxX < v.x) maxX = v.x;
            if (maxY < v.y) maxY = v.y;
        }
        return (int) (minTime * 1000);
    }
    
    private void log (String msg) {
        System.err.println("MSG: " + msg);
    }
}

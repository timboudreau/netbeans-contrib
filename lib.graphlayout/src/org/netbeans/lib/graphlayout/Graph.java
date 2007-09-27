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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/** Graph with vertexes and edges.
 *
 * @author jarda
 */
public final class Graph extends Object {
    final ArrayList vertexes = new ArrayList ();
    final ArrayList edges = new ArrayList ();
    
    private ArrayList listeners = new ArrayList();
    
    private int middleSize = 100;
    private int gridSize = 300;
    double minX, maxX, minY, maxY;
    
    /** Creates a new instance of Graph */
    private Graph () {
    }
    
    public static Graph create () {
        return new Graph ();
    }
    
    public javax.swing.JComponent createMatrix() {
        return new Matrix(this);
    }
    
    public javax.swing.JComponent createRenderer () {
        javax.swing.JPanel p = new javax.swing.JPanel () {
            public void setPreferredSize(Dimension d) {
                initialize(d);
                super.setPreferredSize(d);
            }
        };
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
                    fireChange();
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
        fireChange();
        return v;
    }
    
    public Edge createEdge (Vertex v1, Vertex v2, int strength) {
        Edge e = new Edge (v1, v2, strength);
        edges.add (e);
        fireChange();
        return e;
    }
    
    public Edge createEdge (String v1, String v2, int strength, boolean createVertexesIfNecessary) {
        Edge e = new Edge (
            findVertex (v1, createVertexesIfNecessary), 
            findVertex (v2, createVertexesIfNecessary), 
            strength
        );
        edges.add (e);
        fireChange();
        return e;
    }
    
    public void removeVertex (Vertex v) {
        if (vertexes.remove (v)) {
            Iterator it = edges.iterator ();
            while (it.hasNext ()) {
                Edge e = (Edge)it.next ();
                if (e.getVertex1 () == v) {
                    it.remove ();
                    e.getVertex2 ().removeEdge (e);
                    continue;
                }
                    
                if (e.getVertex2 () == v) {
                    it.remove ();
                    e.getVertex1 ().removeEdge (e);
                    continue;
                }
            }
        }
        fireChange();
    }
    
    public void removeGroup (String info) {
        Iterator ver = vertexes.iterator ();
        while (ver.hasNext ()) {
            Vertex v = (Vertex)ver.next ();
            if (info.equals (v.info)) {
                ver.remove ();
                Iterator it = edges.iterator ();
                while (it.hasNext ()) {
                    Edge e = (Edge)it.next ();
                    if (e.getVertex1 () == v) {
                        it.remove ();
                        e.getVertex2 ().removeEdge (e);
                        continue;
                    }

                    if (e.getVertex2 () == v) {
                        it.remove ();
                        e.getVertex1 ().removeEdge (e);
                        continue;
                    }
                }
            }
        }
        fireChange();
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
    // listeners
    //
    
    final synchronized void addChangeListener(ChangeListener l) {
        listeners.add(l);
    }

    final synchronized void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }
    
    final void fireChange() {
        ChangeEvent ev = new ChangeEvent(this);
        ChangeListener[] arr;
        synchronized (this) {
            arr = (ChangeListener[])listeners.toArray(new ChangeListener[0]);
        }
        for (int i = 0; i < arr.length; i++) {
            arr[i].stateChanged(ev);
        }
    }
    
    //
    // forces computation
    //
    
    /** Initializes the nodes randomly around the 0,0 point in both 
     * directions specified by dimension.
     */
    final void initialize (java.awt.Dimension dim) {
        // clean data in vertexes
        for (Iterator it = vertexes.iterator (); it.hasNext (); ) {
            Vertex v = (Vertex)it.next ();
            if (v.isFixed ()) {
                continue;
            }
            v.x = Math.random () * (2 * (double)dim.width) - dim.width;
            v.y = Math.random () * (2 * (double)dim.height) - dim.height;
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

    
    void topologicallySortVertexes() throws TopologicalSortException {
        HashMap all = new HashMap();
        
        Iterator it = edges.iterator();
        while (it.hasNext()) {
            Edge edge = (Edge)it.next();
            
            Collection to = (Collection)all.get(edge.v1);
            if (to == null) {
                HashSet s = new HashSet();
                all.put(edge.v1, s);
                to = s;
            }
            to.add(edge.v2);
        }
    
        ArrayList v = new ArrayList(vertexes);
        Collections.reverse(v);
        TopologicalSortException e = new TopologicalSortException(v, all);
        //e.reverse = true;
        List sort = e.partialSort();
        vertexes.clear();
        vertexes.addAll(sort);
        if (e.unsortableSets() != null && e.unsortableSets().length > 0) {
            throw e;
        }
    }
    
    void mergeVertexes(String name, String[] string) {
        HashSet toRemoveNames = new HashSet(Arrays.asList(string));
        HashSet toRemoveVertex = new HashSet();
        HashSet toRemoveEdges = new HashSet();
        
        HashMap toEdges = new HashMap();
        HashMap fromEdges = new HashMap();
        
        Iterator it = new ArrayList(edges).iterator();
        while(it.hasNext()) {
            Edge edge = (Edge)it.next();
            
            boolean f1 = toRemoveNames.contains(edge.v1.name);
            boolean f2 = toRemoveNames.contains(edge.v2.name);
            
            if (f1) {
                toRemoveVertex.add(edge.v1);
            }
            if (f2) {
                toRemoveVertex.add(edge.v2);
            }
            
            if (f1 && f2) {
                toRemoveEdges.add(edge);
                continue;
            }
            
            if (f1) {
                Edge from = (Edge)fromEdges.get(edge.v2.name);
                int strength;
                if (from != null) {
                    edges.remove(from);
                    strength = edge.strength + from.strength;
                } else {
                    strength = edge.strength;
                }
                from = createEdge(name, edge.v2.name, strength, true);
                fromEdges.put(edge.v2.name, from);
                toRemoveEdges.add(edge);
                continue;
            }
            if (f2) {
                Edge to = (Edge)toEdges.get(edge.v1.name);
                int strength;
                if (to != null) {
                    edges.remove(to);
                    strength = edge.strength + to.strength;
                } else {
                    strength = edge.strength;
                }
                to = createEdge(edge.v1.name, name, strength, true);
                fromEdges.put(edge.v1.name, to);
                toRemoveEdges.add(edge);
                continue;
            }
        }
        this.vertexes.removeAll(toRemoveVertex);
        this.edges.removeAll(toRemoveEdges);
    }

    void sortByGroups() {
        ListIterator it = vertexes.listIterator(vertexes.size());
        int cnt = Integer.MAX_VALUE;
        final HashMap indexes = new HashMap();
        while (it.hasPrevious()) {
            Vertex v = (Vertex)it.previous();
            Integer c = (Integer)indexes.get(v.info);
            if (c == null) {
                indexes.put(v.info, new Integer(cnt--));
            }
        }
        
        class Cmp implements Comparator {
            public int compare(Object o1, Object o2) {
                Vertex v1 = (Vertex)o1;
                Vertex v2 = (Vertex)o2;
                
                Integer i1 = (Integer)indexes.get(v1.info);
                Integer i2 = (Integer)indexes.get(v2.info);
                return i1.intValue() - i2.intValue();
            }
        }
        
        Collections.sort(vertexes, new Cmp());
    }

}

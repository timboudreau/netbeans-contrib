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

/** Represents a vertex in the graph.
 *
 * @author Jaroslav Tulach
 */
public final class Vertex {
    final String name;
    final String info;
    
    double x, y;
    
    double forceX, forceY;
    
    private java.awt.Rectangle data;
    private boolean fixed;
    private int edges;
    
    /** Creates a new instance of Node */
    Vertex (String name, String info) {
        this.name = name;
        this.info = info;
    }
    
    final void addEdge (Edge e) {
        edges++;
    }
    
    final boolean isSingleton () {
        return edges == 0;
    }
    
    /** Compares the x value with rectangle and uses the one that is more stable.
     */
    final int getX () {
        if (data == null || Math.abs (x - data.x - data.width / 2) > 1 || Math.abs (y - data.y - data.height / 2) > 1) {
            return (int)x;
        } else {
            return data.x + data.width / 2;
        }
    }
    final int getY () {
        if (data == null || Math.abs (x - data.x - data.width / 2) > 1 || Math.abs (y - data.y - data.height / 2) > 1) {
            return (int)y;
        } else {
            return data.y + data.height / 2;
        }
    }
    
    final boolean isFixed () {
        return fixed;
    }
    
    final void setFixed (boolean f) {
        fixed = f;
    }
    
    /** Time in seconds that should we wait for next update */
    final double optimalTimeForForces () {
        double maxForce = Math.abs (Math.max (forceX, forceY));
        if (maxForce < 5.0) {
            return 1.0;
        }
        return 1.0 / (int)maxForce;
    }

    /** Clears the accumulated results before computation */
    final void applyAllForces (double time) {
        if (!fixed) {
            x += forceX * time;
            y += forceY * time;
        }
        forceX = 0.0;
        forceY = 0.0;
    }
    
    /** Adds a force to the ones acting on this vertex.
     */
    final void applyForce (double toX, double toY, int k, int middleSize) {
        double deltaX = toX - x;
        double deltaY = toY - y;
        double distance = Math.sqrt (deltaX * deltaX + deltaY * deltaY);
        if (distance < 1e-5) {
            return;
        }
        
        double forceAdd = (distance - (double)middleSize / (double)k);
        double forceAddX = forceAdd / distance * deltaX;
        double forceAddY = forceAdd / distance * deltaY;
        
        forceX += forceAddX;
        forceY += forceAddY;
    }
    
    /** When two vertexes are close to each other they repulse them selves.
     */
    final void applyRepulsion (Vertex from, int minimalSize) {
        double deltaX, deltaY;
        if (data == null || from.data == null) {
            deltaX = x - from.x;
            deltaY = y - from.y;
        } else {
            // Calculate distance between edges of rectangles to avoid overlap.
            // Give a little grace space to avoid jumpiness.
            double grace = 3.0d;
            if (from.data.x > data.x + data.width) {
                deltaX = Math.min(-grace, data.x + data.width - from.data.x);
            } else if (data.x > from.data.x + from.data.width) {
                deltaX = Math.max(grace, data.x - from.data.x - from.data.width);
            } else {
                // Overlap; just use the minimum.
                deltaX = (x > from.x ? grace : -grace);
            }
            if (from.data.y > data.y + data.height) {
                deltaY = Math.min(-grace, data.y + data.height - from.data.y);
            } else if (data.y > from.data.y + from.data.height) {
                deltaY = Math.max(grace, data.y - from.data.y - from.data.height);
            } else {
                deltaY = (y > from.y ? grace : -grace);
            }
        }
        double distance = Math.sqrt (deltaX * deltaX + deltaY * deltaY);
        if (distance < 1e-5) {
            distance = .01;
        }
        
        
        double force = (minimalSize / distance);
        force = force * force;
        
        forceX += (double)deltaX / distance * force;
        forceY += (double)deltaY / distance * force;
        
    }
    
    /**
     * Vertices should avoid the left and top edges, so they do not go off screen.
     * (Zooming is always relative to the origin, so vertices going too far left
     * or up become invisible and cannot be retrieved.)
     */
    final void applyEdgeRepulsion(int minimalSize) {
        double rootXForce = minimalSize / (Math.max(1.0d, (data != null ? data.x : x)));
        forceX += rootXForce * rootXForce;
        double rootYForce = minimalSize / (Math.max(1.0d, (data != null ? data.y : y)));
        forceY += rootYForce * rootYForce;
    }
    
    //
    // Rendered custom data support
    //
    
    final java.awt.Rectangle getRectangle () {
        return data;
    }
    
    final void setRectangle (java.awt.Rectangle o) {
        data = o;
    }
}

/*
*                 Sun Public License Notice
*
* The contents of this file are subject to the Sun Public License
* Version 1.0 (the "License"). You may not use this file except in
* compliance with the License. A copy of the License is available at
* http://www.sun.com/
*
* The Original Code is NetBeans. The Initial Developer of the Original
* Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
* Microsystems, Inc. All Rights Reserved.
*/
/*
 * Grid.java
 *
 * Created on May 4, 2004, 8:16 PM
 */

package org.netbeans.swing.splitcontainer;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * A grid of logical lines which represents the dividers. Used to provide
 * snap-to-grid behavior
 *
 * @author  Tim Boudreau
 */
public final class Grid {
    private Dimension coordinateSpace;
    private int gap;
    private Line[] lines;
    
    /** Creates a new instance of Grid */
    public Grid(Dimension coordinateSpace, Rectangle[] r, int gap) {
        this.coordinateSpace = coordinateSpace;
        Rectangle[] rects = new Rectangle[r.length];
        int halfGap = gap / 2;
        for (int i=0; i < r.length; i++) {
            rects[i] = new Rectangle (r[i].x - halfGap, r[i].y - halfGap, 
               r[i].width + gap, r[i].height + gap);
        }
        buildGrid (rects);
    }
    
    public Rectangle snapToGrid (Rectangle r, int tolerance, int orientations) {
        Rectangle result = new Rectangle (r);
        Line[] l = lines (result);
        Line[] snapLines = new Line[4];
        
        boolean[] snapped = new boolean[4];
        int snapCount = 0;
        for (int j=0; j < l.length; j++) {
            for (int i=0; i < lines.length; i++) {
                if (lines[i].isHorizontal() == l[j].isHorizontal() && lines[i].isExtension(l[j], tolerance)) {
                    if (!snapped [j]) {
//                        System.err.println("SNAP TO " + lines[i] + " for " + l[j]);
                        snapLines[j] = lines[i];
                    }
                    snapped[j] = true;
                    snapCount++;
                    if (snapCount == 4) {
                        break;
                    }

                }
            }
        }

        int newX = r.x;
        int newY = r.y;
        int newWidth = r.width;
        int newHeight = r.height;
        
        if ((orientations & Interstice.HORIZONTAL) != 0 && snapLines[0] != null) {
            newY = snapLines[0].y + (tolerance / 2);
            if (newY != r.y) {
                newHeight -= r.y - newY;
            }
        }
        if ((orientations & Interstice.VERTICAL) != 0 && snapLines[1] != null) {
            newX = snapLines[1].x + (tolerance / 2);
            if (newX != r.x) {
                newWidth -= r.x - newX;
            }
        }
        if ((orientations & Interstice.HORIZONTAL) !=0 && snapLines[2] != null) {
            newHeight = (snapLines[2].y - (tolerance / 2)) - newY;
        }
        if ((orientations & Interstice.VERTICAL) != 0 && snapLines[3] != null) {
            newWidth = (snapLines[3].x - (tolerance / 2)) - newX;
        }
        result = new Rectangle (newX, newY, newWidth, newHeight);
        
        /*
        int[] adjustments = new int[4];
        for (int i=0; i < 4; i++) {
            switch (i) {
                case 3 :
                    //left edge
                    if (snapLines[i] != null && (orientations & Interstice.VERTICAL) != 0) {
                        int leftEdge = (snapLines[i].x + (tolerance / 2));
                        adjustments[i] = leftEdge - result.x;
                        result.x = leftEdge;
                    }
                    break;
                case 2 :
                    //bottom edge
                    if (snapLines[i] != null && (orientations & Interstice.HORIZONTAL) != 0) {
                        System.err.println("Bottom line " + snapLines[i]);
                        int rectBottom = result.y + result.height;
                        int bottomEdge = snapLines[i].y - (tolerance / 2);
                        System.err.println("RectBottom: " + rectBottom + " gridEdge: " + bottomEdge);
                        
                        adjustments[i] = bottomEdge - rectBottom;
                        System.err.println("Height adjusted by " + adjustments[i]);
                        result.height = adjustments[i];
                    }
                    break;
                case 1 :
                    //right edge
                    if (snapLines[i] != null && (orientations & Interstice.VERTICAL) != 0) {
                        
                    }
                    break;
                case 0 :
                    //top edge
                    if (snapLines[i] != null && (orientations & Interstice.HORIZONTAL) != 0) {
                        System.err.println("topLine: " + snapLines[i]);
                        int topEdge = snapLines[i].x + (tolerance / 2);
                        System.err.println("Rect top: " + r.x);
                        System.err.println("Top edge: " + topEdge);
                        
                        adjustments[i] = topEdge - result.x;
                        System.err.println("Adjusted top edge by " + adjustments[i]);
                        result.x = topEdge;
                        result.height += adjustments[i];
                    }
            }
        }
         */
        
        System.err.println("Try to snap " + r + " tolerance " + tolerance + ": " + result);
        return result;
    }
    
    private void buildGrid (Rectangle[] r) {
        //XXX this could be more efficient, n^2 iterations currently.  Some
        //process of eliminating shared corners might be a more efficient way
        //to do it
        HashSet al = new HashSet();
        for (int i=0; i < r.length; i++) {
//            System.err.println("[" + r[i].x + "," + r[i].y + "," + r[i].width + "," + r[i].height + "]");
            al.addAll (Arrays.asList(lines(r[i])));
        }
        
        int linecount = al.size();
        Line[] l = (Line[]) al.toArray (new Line[linecount]);
        boolean[] removed = new boolean[linecount];
        
        for (int i=0; i < linecount; i++) {
            Line curr = l[i];
            for (int j=0; j < l.length; j++) {
                if (curr != l[j]) {
                    if (curr.extend(l[j])) {
                        removed[j] = true;
                        al.remove (l[j]);
                    }
                }
            }
        }
        
        this.lines = (Line[]) al.toArray(new Line[0]);
/*         System.err.println("\n\nGrid built: " + lines.length + " lines");
        for (int i=0; i < lines.length; i++) {
            System.err.println(l[i]);
        }
 */
    }
    
    public Line[] getLines() {
        return lines;
    }
    
    public void show () {
        //For debugging
        new JF().show();
        
    }
    private class JF extends javax.swing.JFrame {
        public JF() {
            setBounds (100, 100, coordinateSpace.width, coordinateSpace.height);
        }
        public void paint (java.awt.Graphics g) {
            g.setColor (java.awt.Color.WHITE);
            g.fillRect (0, 0, getWidth(), getHeight());
            g.setColor (java.awt.Color.BLACK);
            for (int i=0; i < lines.length; i++) {
                g.drawLine (lines[i].x, lines[i].y, lines[i].x1, lines[i].y1);
            }
        }
    }
    
    private Line[] lines (Rectangle r) {
        Line[] result = new Line [4];
        result [0] = new Line (r.x, r.y, r.x + r.width, r.y);
        result [1] = new Line (r.x + r.width, r.y, r.x + r.width, r.y + r.height);
        result [2] = new Line (r.x, r.y + r.height, r.x + r.width, r.y + r.height);
        result [3] = new Line (r.x, r.y, r.x, r.y + r.height);
        return result;
    }
    
    private static final class Line {
        private int x;
        private int y;
        private int x1;
        private int y1;
        /**
         *
         */
        public Line (int x, int y, int x1, int y1) {
            this.x = x;
            this.y = y;
            this.x1 = x1;
            this.y1 = y1;
            
            if (x != x1 && y != y1) {
                throw new IllegalArgumentException("Line is not horizontal or vertical");
            }
            if (x > x1 || y > y1) {
                throw new IllegalArgumentException ("Line direction is wrong");
            }
            if (x == x1 && y == y1) {
                throw new IllegalArgumentException ("This is a point, not a line");
            }
        }
        
        public boolean extend (Line l) {
            if (l == this || l.isHorizontal() != isHorizontal()) return false;
            boolean result = false;
            
            //Check for subsegments and consume them
            if (l.isHorizontal()) {
                if (l.y == y) {
                    if (l.x >= x && l.x1 <= x1) {
                        System.err.println("PRUNING " + l + " from " + this);
                        return true;
                    }
                }
            } else {
                if (l.x == x) {
                    if (l.y >= y && l.y1 <= y1) {
                        return true;
                    }
                }
            }
            
            if (l.x1 == x && l.y1 == y) {
                x = l.x;
                y = l.y;
                result = true;
            } else if (l.y == y1 && l.x == x1) {
                y1 = l.y1;
                x1 = l.x1;
                result = true;
            }
            
            if (result) {
//                System.err.println(l + " extends " + this);
            } else {
//                System.err.println(l + " NO EXTENDS " + this);
            }
            return result;
        }
        
        private boolean isExtension (Line l, int tolerance) {
            tolerance *= 3;
            boolean result = l.isHorizontal() == isHorizontal();
            if (result) {
                if (isHorizontal()) {
                    result = Math.abs (l.y - y) <= tolerance;
                } else {
                    result = Math.abs (l.x - x) <= tolerance;
                }
            }
            return result;
        }
        
        public Point getPointA() {
            return new Point (x, y);
        }
        
        public Point getPointB() {
            return new Point (x1, y1);
        }
        
        public boolean isHorizontal() {
            return y == y1;
        }
        
        public String toString() {
            return (isHorizontal() ? "H" : "V") + "Line at " + (isHorizontal() ? Integer.toString(y) : 
                Integer.toString(x)) + " from " + (isHorizontal() ? Integer.toString(x) : Integer.toString(y)) + 
                " to " + (isHorizontal() ? Integer.toString (x1) : Integer.toString(y1));
        }
        
        public int hashCode() {
            return (x * 17) + (y * 37) + (x1 * 11) + (y1 * 17);
        }
        
        public boolean equals (Object o) {
            if (o instanceof Line) {
                Line l = (Line) o;
                return l.x == x && l.y == y && l.x1 == x1 && l.y1 == y1;
            }
            return false;
        }
    }
    
}

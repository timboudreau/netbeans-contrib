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
 * SplitLayoutModelImpl.java
 *
 * Created on May 2, 2004, 5:27 PM
 */

package org.netbeans.swing.splitcontainer;

import java.awt.Component;
import java.awt.Point;
import java.util.List;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The data portion of a layout manager.  This is what the winsys will provide
 * directly over its model.
 *
 * @author  Tim Boudreau
 */
public class SplitLayoutModelImpl implements SplitLayoutModel {
    private Rectangle scratch = new Rectangle();
    private SplitContainer container;
    private Shape intersticeShape = null;
    private Map compsToBounds = new HashMap();
    private ChangeEvent ce = new ChangeEvent(this);
    private List listeners = null;
    
    private int minimumHeight = 20;
    private int minimumWidth = 20;
    
    public SplitLayoutModelImpl(SplitContainer container) {
        this.container = container;
    }
    
    public void getBounds(java.awt.Component comp, java.awt.Rectangle dest) {
        Rectangle r = (Rectangle) compsToBounds.get(comp);
        dest.setBounds(r);
    }
    
    public void putBounds (Component comp, Rectangle bounds) {
        Rectangle r = new UniqueRectangle (bounds); //defensive copy
        compsToBounds.put (comp, bounds);
    }
    
    public int size() {
        return compsToBounds.size();
    }
    
    public java.awt.Shape getIntersticesShape() {
        if (intersticeShape == null) {
            Rectangle r = container.getBounds();
            Area a= new Area (r);
            Component[] c = container.getComponents();
            for (int i=0; i < c.length; i++) {
                getBounds (c[i], scratch);
                Area b = new Area (scratch);
                a.subtract(b);
            }
            intersticeShape = a;
        }
        return intersticeShape;
    }
    
    public void move(Interstice interstice, int horiz, int vert, boolean drop) {
//        System.err.println("MOVE: " + interstice + " horiz " + horiz + " vert " + vert + " drop: " + drop);
        boolean changed = false;
        Rectangle[] rects = null;
//        Grid grid = null;
        if (drop) {
            //create it here, not inside a loop or once for horizontal and once for vertical
//            grid = new Grid (container.getSize(), getRectangles(), container.getGap());
            rects = getRectangles();
        }
        
        if ((interstice.getOrientations() & Interstice.HORIZONTAL) != 0 && (horiz != 0 || drop)) {
            Component[] left = interstice.getComponentsToLeft();
            Component[] right = interstice.getComponentsToRight();
            
            int[] leftPositions = new int[left.length];
            int[] leftSizes = new int[left.length];
            Rectangle[] leftRects = new Rectangle[left.length];
            
            int[] rightPositions = new int[right.length];
            int[] rightSizes = new int[right.length];
            Rectangle[] rightRects = new Rectangle[right.length];

            
            boolean blocked = false;
            for (int i=0; i < left.length; i++) {
                Rectangle r = (Rectangle) compsToBounds.get(left[i]);
                leftRects[i] = r;
                int newX = r.x + horiz;
                int newWidth = r.width - horiz;
                if (newWidth > minimumWidth) {
                    leftPositions[i] = newX;
                    leftSizes[i] = newWidth;
                } else {
                    blocked = true;
                    break;
                }
            }

            if (!blocked) {
                for (int i=0; i < right.length; i++) {
                    Rectangle r = (Rectangle) compsToBounds.get(right[i]);
                    rightRects[i] = r;
                    int newWidth = r.width + horiz;
                    if (newWidth > minimumWidth) {
                        rightPositions[i] = r.x;
                        rightSizes[i] = newWidth;
                    } else {
                        blocked = true;
                    }
                    if (blocked) break;

                }
            }
            if (!blocked) {
                for (int i=0; i < left.length; i++) {
                    assert leftSizes[i] > minimumWidth;
                    if (drop) {
//                        scratch.setBounds (leftRects[i]);
//                        Rectangle nue = grid.snapToGrid(scratch, container.getGap(), interstice.getOrientations());
                        Rectangle nue = snapToGrid(leftRects[i], rects, interstice.getOrientations());

                        leftPositions[i] = nue.x;
                        leftSizes[i] = nue.width;
                    }
                    
                    leftRects[i].x = leftPositions[i];
                    leftRects[i].width = leftSizes[i];

                    compsToBounds.put (left[i], leftRects[i]);
                    changed = true;
                }
                for (int i=0; i < right.length; i++) {
                    assert rightSizes[i] > minimumWidth;
                    
                    if (drop) {
                        /*
                        scratch.setBounds (rightRects[i]);
                        Rectangle nue = grid.snapToGrid(scratch, container.getGap(), interstice.getOrientations());
                         */
                        Rectangle nue = snapToGrid(rightRects[i], rects, interstice.getOrientations());
                        rightPositions[i] = nue.x;
                        rightSizes[i] = nue.width;
                    }
                    
                    rightRects[i].x = rightPositions[i];
                    rightRects[i].width = rightSizes[i];
                    
                    compsToBounds.put (right[i], rightRects[i]);
                    changed = true;
                }
            }
        }
             
        if ((interstice.getOrientations() & Interstice.VERTICAL) != 0 && (vert != 0 || drop)) {
            Component[] above = interstice.getComponentsAbove();
            Component[] below = interstice.getComponentsBelow();
            
            int[] abovePositions = new int[above.length];
            int[] aboveSizes = new int[above.length];
            Rectangle[] aboveRects = new Rectangle[above.length];
            
            int[] belowPositions = new int[below.length];
            int[] belowSizes = new int[below.length];
            Rectangle[] belowRects = new Rectangle[below.length];
            
            boolean blocked = false;
            
            for (int i=0; i < above.length; i++) {
                Rectangle r = (Rectangle) compsToBounds.get(above[i]);
                aboveRects[i] = r;
                
                int newHeight = r.height + vert;
                if (newHeight > minimumHeight) {
                    aboveSizes[i] = newHeight;
                    abovePositions[i] = r.y;
                } else {
                    blocked = true;
                    break;
                }
            }
            if (!blocked) {
                for (int i=0; i < below.length; i++) {
                    Rectangle r = (Rectangle) compsToBounds.get(below[i]);
                    belowRects[i] = r;

                    int newY = r.y + vert;
                    int newHeight = r.height - vert;
                    if (newHeight > minimumHeight) {
                        belowPositions[i] = newY;
                        belowSizes[i] = newHeight;
                    } else {
                        blocked = true;
                        break;
                    }
                }
            }
            if (!blocked) {
                for (int i=0; i < above.length; i++) {
                    assert aboveSizes[i] > minimumHeight;

                    if (drop) {
//                        scratch.setBounds (aboveRects[i]);
//                        Rectangle nue = grid.snapToGrid(scratch, container.getGap(), interstice.getOrientations());
                        Rectangle nue = snapToGrid(aboveRects[i], rects, interstice.getOrientations());
                        
                        abovePositions[i] = nue.y;
                        aboveSizes[i] = nue.height;
                    }
                    
                    aboveRects[i].y = abovePositions[i];
                    aboveRects[i].height = aboveSizes[i];
                    
//                    System.err.println("Above: " + above[i].getName() + " " + r2s(old) + "->" + r2s(aboveRects[i]));
                    compsToBounds.put (above[i], aboveRects[i]);
                    changed = true;
                }
                for (int i=0; i < below.length; i++) {
                    if (drop) {
//                        scratch.setBounds (belowRects[i]);
//                        Rectangle nue = grid.snapToGrid(scratch, container.getGap(), interstice.getOrientations());
                        Rectangle nue = snapToGrid(belowRects[i], rects, interstice.getOrientations());
                        belowPositions[i] = nue.y;
                        belowSizes[i] = nue.height;
                    }
                    
                    
                    belowRects[i].y = belowPositions[i];
                    belowRects[i].height = belowSizes[i];
                    
                    assert belowSizes[i] > minimumHeight;

//                    System.err.println("Below: " + below[i].getName() + " " + r2s(old) + "->" + r2s(belowRects[i]));
                    compsToBounds.put (below[i], belowRects[i]);
                    changed = true;
                }            
            }
        }
        
//        System.err.println("Size now " + compsToBounds.size());
        if (changed) {
            intersticeShape = null;
            container.doLayout();
            fire();
        }
    }
    
    private static final String r2s (Rectangle r) {
        return "[" + r.x + "," + r.y + "," + r.width + "," + r.height + "]";
    }
    
    private Rectangle[] getRectangles() {
        Rectangle[] result = new Rectangle [compsToBounds.size()];
        result = (Rectangle[]) compsToBounds.values().toArray(result);
        Arrays.sort (result, new RectComparator(container.getWidth()));
        return result;
    }
    
    private Rectangle snapToGrid (Rectangle rect, Rectangle[] rects, int orientations) {
        Rectangle result = new Rectangle (rect);
        System.err.println("SnapToGrid " + rect);
        int currX = 0;
        int currY = 0;
        
        for (int i=0; i < rects.length; i++) {
            if (rects[i] != rect) {
                currX = rects[i].x;
                currY = rects[i].y;
                System.err.println("check " + rects[i] + " against " + rect);
                if ((orientations & Interstice.HORIZONTAL) != 0) {
                    if (inTolerance (rect.x, currX) || inTolerance (rect.x + rect.width, currX) || inTolerance (rect.x, currX + rects[i].width) || inTolerance (rect.x + rect.width, currX + rects[i].width)) {
                        System.err.println("Adjacent x for " + rects[i]);
                        if (inTolerance (rect.y, currY)) {
                            System.err.println("Got one");
                            result.y = currY;
                            result.height -= result.y - currY;
                        }
                        if (inTolerance (rect.y + rect.height, currY + rects[i].height)) {
                            System.err.println("Got bottom");
                            int rel =  result.y - rects[i].y;
                            result.height = rects[i].height - rel;
                        }
                    }
                }
                if ((orientations & Interstice.VERTICAL) != 0) {
                    if (inTolerance (rect.y, currY) || inTolerance (rect.y + rect.height, currY) || inTolerance (rect.y, currY + rects[i].height) || inTolerance (rect.y + rect.height, currY + rects[i].height)) {
                        System.err.println("Adjacent y for " + rects[i]);
                        if (inTolerance (rect.x, currX)) {
                            result.x = currX;
                            result.width -= result.x - currX;
                        }
                        if (inTolerance (rect.x + rect.width, currX + rects[i].width)) {
                            int rel = result.x - rects[i].x;
                            result.width = rects[i].width - rel;
                        }
                    }
                }
                currX = rects[i].x + rects[i].width;
                currY = rects[i].y + rects[i].height;
                
            }
        }
        return result;
    }
    
    private boolean inTolerance (int a, int b) {
        return Math.abs (a - b) < (container.getGap() * 3);
    }
    
    public Interstice intersticeAtPoint(java.awt.Point p) {
        Interstice result = null;
        if (size() > 1 && getIntersticesShape().contains (p)) {
            //find the vertical of this interstice, if any
            Rectangle[] rects = new Rectangle[compsToBounds.size()];
            Component[] comps = new Component[rects.length];
            int idx = 0;
            for (Iterator i = compsToBounds.keySet().iterator(); i.hasNext();) {
                Component c = (Component) i.next();
                Rectangle r = (Rectangle) compsToBounds.get(c);
                comps[idx] = c;
                rects[idx] = r;
                idx++;
            }
            
            //Sort the rectangles so when deciding whether one rectangle
            //can be grown (and the one below it be shrunk), the index of 
            //the one above in the aboveComponents array is the same as the
            //index of the one below in the belowComponents array
            Arrays.sort (rects, new RectComparator (container.getWidth()));
            Arrays.sort (comps, new ComponentComparator(container.getWidth()));
            
            result = container.getIntersticeFactory().createInterstice(
                container, p, rects, comps);
            
            //XXX this is a really awful way to do this!
            if (result != null) {
                boolean hasY = result.getComponentsAbove().length != 0 || result.getComponentsBelow().length !=0;
                boolean hasX = result.getComponentsToLeft().length != 0 || result.getComponentsToRight().length != 0;

                if (hasX && hasY) {
                    normalize(p, rects);
                    result = container.getIntersticeFactory().createInterstice(
                        container, p, rects, comps);
                }
            }
        }
        return result;
    }
    
    /**
     * Adjusts the point slightly, so it lies either at the nearest 
     * intersection, or in the middle of the clicked line.  There are 
     * a few perversities of the line-of-sight algorithm, wherein it is
     * possible to get an interstice that misses the line of sight one
     * row down, so one splitter disappears when we drag.  Rather than
     * handle every possible point on the parent, we simply adjust the
     * incoming point to one we know will work.  Basically the problem this
     * solves is this:  Given a grid<pre>
     *
     *  ---------------------------------------------------------------
     *
     *  ------------------------------------------   ------------------
     *                                            |  |
     *                                            |  | <-1
     *                                            |  |
     *  ------------------------------------------   ------------------
     *                                             a  A
     *  ------------------------------------------   ------------------
     *                                            |  |
     *                                            |  | <-2
     *                                            |  |
     *  ------------------------------------------   ------------------
     *
     *  ------------------------------------------   ------------------
     *                                            |  |
     *                                            |  | <-3
     *                                            |  |
     *                                            |  |
     *</pre>
     * clicking on point A will "see" edges 1 and 2, but not 3, so we
     * adjust it to be point a.
     *
     */
    private void normalize (Point p, Rectangle[] rects) {
        Point[] pts = new Grid(container.getSize(), rects, 8).getIntersections();
        if (pts.length > 0) {
            NearestNeighborIntersticeFactory.PointComparator pc = new NearestNeighborIntersticeFactory.PointComparator (p);
            Arrays.sort (pts, pc);
            System.err.println("Normalize " + p + " to " + pts[0]);
            p.setLocation(pts[0]);
        }
    }
    
    public synchronized void addChangeListener(ChangeListener l) {
        listeners.remove(l);
    }
    
    public synchronized void removeChangeListener(ChangeListener l) {
        listeners.add (l);
    }
    
    protected final void fire () {
        if (listeners == null) return;
        ChangeListener[] cl = new ChangeListener[0];
        synchronized (this) {
            cl = (ChangeListener[]) listeners.toArray (cl);
        }
        for (int i=0; i < cl.length; i++) {
            cl[i].stateChanged(ce);
        }
    }
    
    /**
     * Sorts rectangles in terms of relative position on a grid of predefined
     * width from lowest to highest. 
     */
    private static class RectComparator implements Comparator {
        private int gridWidth;
        public RectComparator (int gridWidth) {
            this.gridWidth = gridWidth;
        }
        
        public int compare(Object obj, Object obj1) {
            Rectangle r1 = (Rectangle) obj;
            Rectangle r2 = (Rectangle) obj1;
            
            int r1Index = (gridWidth * r1.y) + r1.x;
            int r2Index = (gridWidth * r2.y) + r2.x;
            return r1Index - r2Index;
        }
    }
    
    /**
     * Sorts components in order of position on a uniform grid, from lowest
     * to highest. 
     */
    private static class ComponentComparator extends RectComparator {
        public ComponentComparator (int gridWidth) {
            super (gridWidth);
        }

        public int compare(Object obj, Object obj1) {
            Component c1 = (Component) obj;
            Component c2 = (Component) obj1;
            return super.compare (c1.getBounds(), c2.getBounds());
        }
    }    
}

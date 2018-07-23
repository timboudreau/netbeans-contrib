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
 * NearestNeighborIntersticeFactory.java
 *
 * Created on May 4, 2004, 3:50 PM
 */

package org.netbeans.swing.splitcontainer;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Comparator;

/**
 * An IntersticeFactory which produces Interstice instances only for the two
 * rectangles closest to the point.
 *
 * @author  Tim Boudreau
 */
public class NearestNeighborIntersticeFactory extends IntersticeFactory {

    /** Creates a new instance of NearestNeighborIntersticeFactory */
    public NearestNeighborIntersticeFactory() {
    }
    
    public Interstice createInterstice(SplitContainer c, Point p, Rectangle[] rects, Component[] children) {
        Interstice result = null;
        
        Rectangle[] r = (Rectangle[]) rects.clone();
        Arrays.sort (r, new RectRelativePositionComparator (p));
        Rectangle a = r[0];
        Rectangle b = r[1];
        int idxA = Arrays.asList(rects).indexOf(a);
        int idxB = Arrays.asList(rects).indexOf(b);
        
        int relA = relationTo (p, rects[idxA]);
        int relB = relationTo (p, rects[idxB]);
        
        
        /*
        XXX This does not handle the case where there are two above and one
        below, i.e.
         -------------
         |        |   |
         -------X-----
         |    |       |
         -------------
         
         and similar permutations.  Maybe use Grid for detection here.
         */
        
        Component[] empty = new Component[0];
        if (relA == ABOVE && relB == BELOW) {
            result = new Interstice (empty, empty, new Component[] { children[idxA] }, new Component[] {
                children[idxB] }, false);
        } else if (relA == BELOW && relB == ABOVE) {
            result = new Interstice (empty, empty, new Component[] { children[idxB] }, new Component[] {
                children[idxA] }, false);
        } else if (relA == LEFT && relB == RIGHT) {
            result = new Interstice (new Component[] { children[idxA] }, new Component[] { children[idxB] },
                empty, empty, false);
        } else if (relA == RIGHT && relB == LEFT) {
            result = new Interstice (new Component[] { children[idxB] }, new Component[] { children[idxA] },
                empty, empty, false);
        }
        if (result != null) {
            System.err.println("Interstice: " + result);
        } else {
            System.err.println("Components: " + children[idxA].getName() + " " + r2s (relA) +
                " & " + children[idxB].getName() + " " + r2s(relB));
        }
        return result;
    }
    
    private static final String r2s (int i) {
        String result = "unknown";
        switch (i) {
            case ABOVE : result = "above";
            case BELOW : result = "below";
            case LEFT : result = "left";
            case RIGHT : result = "right";
        }
        return result;
    }
    
    private static final int ABOVE = 1;
    private static final int BELOW = 2;
    private static final int LEFT = 4;
    private static final int RIGHT = 8;
    private int relationTo (Point p, Rectangle r) {
        if (r.x + r.width <= p.x) {
            return RIGHT;
        }
        if (r.x > p.x) {
            return LEFT;
        }
        if (r.y > p.y) {
            return BELOW;
        }
        return ABOVE;
    }
    
    
    
    private static class RectRelativePositionComparator implements Comparator {
        private Point origin;
        public RectRelativePositionComparator (Point origin) {
            this.origin = origin;
        }
    
        private double distance (Point a, Point b) {
            int deltaX = a.x - b.x;
            int deltaY = a.y - b.y;
            return Math.sqrt (deltaX * deltaX + deltaY * deltaY);
        }
        
        public int compare(Object obj, Object obj1) {
            Rectangle a = (Rectangle) obj;
            Rectangle b = (Rectangle) obj1;
            
            Point aNearest = nearestCorner (origin, a);
            Point bNearest = nearestCorner (origin, b);
            
            double distA = Math.abs(distance (origin, aNearest));
            double distB = Math.abs(distance (origin, bNearest));
            return Math.round(Math.round((distA - distB) * 100));
        }
        
        private Point nearestCorner (Point p, Rectangle r) {
            return pointArrayFor (r, p) [0];
        }
        
        /** Gets an array of points belonging to a rectangle in order of 
         * proximity to an origin point */
        private Point[] pointArrayFor (Rectangle r, Point origin) {
            Point[] result = new Point[4];
            result[0] = new Point (r.x, r.y);
            result[1] = new Point (r.x + r.width, r.y);
            result[2] = new Point (r.x, r.y + r.height);
            result[3] = new Point (r.x + r.width, r.y + r.height);
            Arrays.sort (result, new PointComparator (origin));
            return result;
        }
        
    }  
    
     static class PointComparator implements Comparator {

        private Point origin;
        public PointComparator (Point origin) {
            this.origin = origin;
        }
    
        private double distance (Point a, Point b) {
            int deltaX = a.x - b.x;
            int deltaY = a.y - b.y;
            return Math.sqrt (deltaX * deltaX + deltaY * deltaY);
        }
        
        public int compare(Object obj, Object obj1) {
            return Math.round(Math.round((distance ((Point) obj, origin) - 
                distance ((Point) obj1, origin)) * 1000));
        }
    }    
    
}

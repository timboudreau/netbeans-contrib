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
 * Interstice.java
 *
 * Created on May 2, 2004, 4:46 PM
 */

package org.netbeans.swing.splitcontainer;

import java.awt.Component;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Immutable object representing a "splitter" (really just the surface of the
 * underlying container), in terms of what components will be affected by
 * calling SplitLayoutModel.move () on it.
 *
 * @author  Tim Boudreau
 */
public final class Interstice {
    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 2;
    public static final int BOTH = 3;
    
    private Component[] left;
    private Component[] right;
    private Component[] above;
    private Component[] below;
    
    private boolean uniform;
    
    /** Creates a new instance of Interstice */
    public Interstice(Component[] left, Component[] right, Component[] above, Component[] below, boolean uniform) {
        this.left = left;
        this.above = above;
        this.right = right;
        this.below = below;
        this.uniform = uniform;
        
        assert noDuplicates (above, below) : "Cannot have same component both "
            + "above and below a point :" + Arrays.asList (above) + " and " +
            Arrays.asList (below);
        
        assert noDuplicates (left, right) : "Cannot have same component both " +
            "to the left and to the right of a point :" 
            + Arrays.asList (left) + " and " +
            Arrays.asList (right);
    }
    
    private boolean noDuplicates (Component[] a, Component[] b) {
        Set set = new HashSet (Arrays.asList(a));
        boolean result = true;
        for (int i=0; i < b.length; i++) {
            result &= !set.contains (b[i]);
            if (result) {
                break;
            }
        }
        return result;
    }
    
    /**
     * True if this interstice spans the entire grid of components - i.e. no
     * snap-to-grid behavior is needed.
     */
    public boolean isUniform() {
        return uniform;
    }
    
    /**
     * Get the axes affected by moving this interstice - horizontal, vertical
     * or both.
     */
    public int getOrientations() {
        int result = 0;
        result |= left != null || right != null ? VERTICAL : 0;
        result |= above != null || below != null ? HORIZONTAL : 0;
        return result;
    }
    
    public Component[] getComponentsToLeft() {
        return left;
    }
    
    public Component[] getComponentsToRight() {
        return right;
    }
    
    public Component[] getComponentsAbove() {
        return above;
    }
    
    public Component[] getComponentsBelow() {
        return below;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append ("Interstice: " + o2s(getOrientations()) + " " + a2s(above) + " above " + a2s(below) +
            " below " + a2s(right) + " right " + a2s(left) + " left\n");
        if (above.length > 0) {
            sb.append ("ABOVE:\n");
            for (int i=0; i < above.length; i++) {
                sb.append ("  " + above[i].getName() + "\n");
            }
        }
        if (below.length > 0) {
            sb.append ("BELOW:\n");
            for (int i=0; i < below.length; i++) {
                sb.append ("  " + below[i].getName() + "\n");
            }
        }
        if (left.length > 0) {
            sb.append ("LEFT:\n");
            for (int i=0; i < left.length; i++) {
                sb.append ("  " + left[i].getName() + "\n");
            }
        }
        if (right.length > 0) {
            sb.append ("RIGHT:\n");
            for (int i=0; i < right.length; i++) {
                sb.append ("  " + right[i].getName() + "\n");
            }
        }
        return sb.toString();
    }
    
    private static final String a2s (Component[] c) {
        return c == null ? "0" : Integer.toString(c.length);
    }
    
    private static final String o2s (int o) {
        switch (o) {
            case HORIZONTAL : return "horizontal";
            case VERTICAL : return "vertical";
            case BOTH : return "both";
            default : return "unknown";
        }
    }
    
}

/*
* The contents of this file are subject to the terms of the Common Development
* and Distribution License (the License). You may not use this file except in
* compliance with the License.
*
* You can obtain a copy of the License at http://www.netbeans.org/cddl.html
* or http://www.netbeans.org/cddl.txt.
*
* When distributing Covered Code, include this CDDL Header Notice in each file
* and include the License file at http://www.netbeans.org/cddl.txt.
* If applicable, add the following below the CDDL Header, with the fields
* enclosed by brackets [] replaced by your own identifying information:
* "Portions Copyrighted [year] [name of copyright owner]"
*
* The Original Software is NetBeans. The Initial Developer of the Original
* Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
* Microsystems, Inc. All Rights Reserved.
*/
/*
 * IntersticeFactory.java
 *
 * Created on May 4, 2004, 3:13 PM
 */

package org.netbeans.swing.splitcontainer;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * A pluggable class which, given a point and a set of rectangles, can produce
 * an Interstice instance which indicates what rectangles are affected by
 * a drag at a given point.  It is possible to have multiple implementations:
 * perhaps, when the user is holding down CTRL, only the splitter being touched
 * should be moved, without affecting any others in a line from it.
 *
 * @author  Tim Boudreau
 */
public abstract class IntersticeFactory {
    /**
     *  Create an Interstice which can return the components above/below/to the
     *  left/to the right of the split at point p.  The following things are
     *  guaranteed about the values passed to this method:
     *  <ul>
     *  <li>Point p will not be inside any of the child rectangles</li>
     *  <li>The array of rectangles will be sorted in left-to-right, 
     *      top-to-bottom order</li>
     *  <li>The array of components will be sorted to match the array of 
     *      rectangles, such that rects[n].equals(children[n].getBounds())</li>
     *  </ul>
     */
    public abstract Interstice createInterstice (SplitContainer c, Point p, 
        Rectangle[] rects, Component[] children);
}

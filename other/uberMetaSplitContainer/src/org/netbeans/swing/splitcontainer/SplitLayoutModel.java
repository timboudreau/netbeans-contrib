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
 * SplitLayoutModel.java
 *
 * Created on May 2, 2004, 4:02 PM
 */

package org.netbeans.swing.splitcontainer;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.event.ChangeListener;

/**
 *
 * @author  Tim Boudreau
 */
public interface SplitLayoutModel {
    public void getBounds (Component comp, final Rectangle dest);
    public Shape getIntersticesShape();
    public void move (Interstice interstice, int horiz, int vert, boolean drop);
    public Interstice intersticeAtPoint (Point p);
    public void addChangeListener (ChangeListener l);
    public void removeChangeListener (ChangeListener l);
}

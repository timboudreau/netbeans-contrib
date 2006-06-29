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

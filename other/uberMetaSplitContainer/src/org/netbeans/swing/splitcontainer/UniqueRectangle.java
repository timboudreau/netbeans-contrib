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
 * UniqueRectangle.java
 *
 * Created on May 4, 2004, 1:24 AM
 */

package org.netbeans.swing.splitcontainer;

import java.awt.Rectangle;

/**
 * A Rectangle subclass which implements an identity equality and hash code.
 *
 * @author  Tim Boudreau
 */
public class UniqueRectangle extends Rectangle {
    
    /** Creates a new instance of UniqueRectangle */
    public UniqueRectangle(Rectangle r) {
        super (r);
    }
    
    public UniqueRectangle(int x, int y, int w, int h) {
        super (x, y, w, h);
    }
    
    public UniqueRectangle() {
        super();
    }
    
    public int hashCode() {
        return System.identityHashCode(this);
    }
    
    public boolean equals (Object o) {
        return o == this;
    }
    
}

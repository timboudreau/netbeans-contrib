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

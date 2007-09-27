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
 * Constraint.java
 *
 * Created on May 2, 2004, 4:56 PM
 */

package org.netbeans.swing.splitcontainer;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 * Constraint representing a position in a SplitContainer.  Provides for both
 * percentage bounds, to be used in the case of an unknown or unexpected
 * container size, and integer bounds, such that there is no risk of rounding
 * errors if a component is added to a container the same size as is expected.
 *
 * @author  Tim Boudreau
 */
public class Constraint{
    private Rectangle expectedBounds = null;
    private Dimension expectedSize = null;
    private Rectangle2D.Double percentageBounds = null;
    
    public static Constraint NORTH = new Constraint (null, null, null);
    public static Constraint SOUTH = new Constraint (null, null, null);
    public static Constraint EAST = new Constraint (null, null, null);
    public static Constraint WEST = new Constraint (null, null, null);
    public static Constraint NORTHEAST = new Constraint (null, null, null);
    public static Constraint SOUTHEAST = new Constraint (null, null, null);
    public static Constraint NORTHWEST = new Constraint (null, null, null);
    public static Constraint SOUTHWEST = new Constraint (null, null, null);
    public static Constraint CENTER = new Constraint (null, null, null);
    
    /** Creates a new instance of Constraint */
    public Constraint(Dimension expectedSize, Rectangle expectedBounds) {
        this.expectedSize = expectedSize;
        this.expectedBounds = expectedBounds;
    }
    
    public Constraint (Rectangle2D.Double percentageBounds, Dimension expectedSize, Rectangle expectedBounds) {
        this.expectedSize = expectedSize;
        this.expectedBounds = expectedBounds;
        this.percentageBounds = percentageBounds;
    }
    
    public Rectangle getBounds (Dimension containerSize) {
        if (this == NORTH || this == SOUTH || this == EAST || this == WEST ||
            this == NORTHEAST || this == SOUTHEAST || this == SOUTHWEST || this == NORTHWEST || 
            this == CENTER) {
                return constantBounds (containerSize);
        }
        if (containerSize.equals(expectedSize) && expectedBounds != null) {
            return expectedBounds;
        }
        Rectangle result = new Rectangle();
        Rectangle2D.Double pc = percentageBounds();
        
        result.x = Math.round(Math.round(pc.x * containerSize.width));
        result.y = Math.round(Math.round(pc.y * containerSize.height));
        result.width = Math.round(Math.round (pc.width * containerSize.width));
        result.height = Math.round(Math.round(pc.height * containerSize.height));
        
        return result;
    }
    
    private Rectangle2D.Double percentageBounds() {
        if (percentageBounds == null) {
            double x = expectedSize.width / expectedBounds.x;
            double y = expectedSize.height / expectedBounds.y;
            double w = expectedSize.width / expectedBounds.width;
            double h = expectedSize.height / expectedBounds.height;

            percentageBounds = new Rectangle2D.Double (x, y, w, h);
        }
        return percentageBounds;
    }
    
    private Rectangle constantBounds (Dimension size) {
        int w = size.width / 3;
        int h = size.height / 3;
        Rectangle result = new Rectangle(0, 0, w, h);
        
        if (this == NORTH) {
           result.x += w;
        } else if (this == SOUTH) {
            result.x +=w;
            result.y += h * 2;
        } else if (this == EAST) {
            result.x += w * 2;
            result.y += h;
        } else if (this == WEST) {
            result.y += h;
        } else if (this == NORTHEAST) {
            result.x += w * 2;
        } else if (this == SOUTHEAST) {
            result.x += w * 2;
            result.y += w * 2;
        } else if (this == SOUTHWEST) {
            result.y += w * 2;
        } else if (this == NORTHWEST) {
            //default position
        } else if (this == CENTER) {
            result.x += w;
            result.y += h;
        }
        return result;
    }
}

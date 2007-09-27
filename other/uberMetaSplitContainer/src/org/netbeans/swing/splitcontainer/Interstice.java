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

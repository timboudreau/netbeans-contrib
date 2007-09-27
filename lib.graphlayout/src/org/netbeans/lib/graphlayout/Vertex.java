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

package org.netbeans.lib.graphlayout;

/** Represents a vertex in the graph.
 *
 * @author Jaroslav Tulach
 */
public final class Vertex {
    final String name;
    final String info;

    double x, y;

    double forceX, forceY;

    private java.awt.Rectangle data;
    private boolean fixed;
    private int edges;

    /** Creates a new instance of Node */
    Vertex (String name, String info) {
        this.name = name;
        this.info = info;
    }

    public String toString() {
        return "Vertex[" + name + "]";
  }
    
    final void addEdge (Edge e) {
        edges++;
    }
    final void removeEdge (Edge e) {
        edges--;
    }
    
    final boolean isSingleton () {
        return edges == 0;
    }
    
    /** Compares the x value with rectangle and uses the one that is more stable.
     */
    final int getX () {
        if (data == null || Math.abs (x - data.x - data.width / 2) > 1 || Math.abs (y - data.y - data.height / 2) > 1) {
            return (int)x;
        } else {
            return data.x + data.width / 2;
        }
    }
    final int getY () {
        if (data == null || Math.abs (x - data.x - data.width / 2) > 1 || Math.abs (y - data.y - data.height / 2) > 1) {
            return (int)y;
        } else {
            return data.y + data.height / 2;
        }
    }
    
    final boolean isFixed () {
        return fixed;
    }
    
    final void setFixed (boolean f) {
        fixed = f;
    }
    
    /** Time in seconds that should we wait for next update */
    final double optimalTimeForForces () {
        double maxForce = Math.abs (Math.max (forceX, forceY));
        if (maxForce < 5.0) {
            return 1.0;
        }
        return 1.0 / (int)maxForce;
    }

    /** Clears the accumulated results before computation */
    final void applyAllForces (double time) {
        if (!fixed) {
            x += forceX * time;
            y += forceY * time;
        }
        forceX = 0.0;
        forceY = 0.0;
    }
    
    /** Adds a force to the ones acting on this vertex.
     */
    final void applyForce (double toX, double toY, int k, int middleSize) {
        double deltaX = toX - x;
        double deltaY = toY - y;
        double distance = Math.sqrt (deltaX * deltaX + deltaY * deltaY);
        if (distance < 1e-5) {
            return;
        }
        
        double forceAdd = (distance - (double)middleSize / (double)k);
        double forceAddX = forceAdd / distance * deltaX;
        double forceAddY = forceAdd / distance * deltaY;
        
        forceX += forceAddX;
        forceY += forceAddY;
    }
    
    /** When two vertexes are close to each other they repulse them selves.
     */
    final void applyRepulsion (double fromX, double fromY, int minimalSize) {
        double deltaX = x - fromX;
        double deltaY = y - fromY;
        double distance = Math.sqrt (deltaX * deltaX + deltaY * deltaY);
        if (distance < 1e-5) {
            distance = .01;
        }
        
        
        double force = (minimalSize / distance);
        force = force * force;
        
        forceX += (double)deltaX / distance * force;
        forceY += (double)deltaY / distance * force;
        
    }
    
    //
    // Rendered custom data support
    //
    
    final java.awt.Rectangle getRectangle () {
        return data;
    }
    
    final void setRectangle (java.awt.Rectangle o) {
        data = o;
    }
}

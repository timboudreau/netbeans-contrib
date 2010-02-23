/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;
import org.netbeans.modules.graphicclassview.grid.AffinityProvider;
import org.netbeans.modules.graphicclassview.grid.G;
import org.netbeans.modules.graphicclassview.grid.G.Split;

/**
 *
 * @author Tim Boudreau
 */
public class GTest extends TestCase {
    
    public GTest(String testName) {
        super(testName);
    }

    List<ND> l;
    ND[][] n;
    G<ND> g;
    protected void setUp() throws Exception {
        System.err.println("HEY!");
        int w = 20;
        int h = 20;
        n = new ND[w][h];
        l = new ArrayList<ND> ();
        int ct = 0;
        for (int y= 0; y < h; y++) {
            for (int x= 0; x < w; x++) {
                String name = "" + ct;
                ct++;
                n[x][y] = new ND(name);
                l.add (n[x][y]);
            }
        }
        g = G.create(l, w, h, new AP());
    }

    public void testSplit() throws Exception {
        System.err.println("ORIG\n");
        System.err.println(g);
        Thread.sleep(500);
        G<ND>[] gs = g.split(Split.VERTICAL);
        G<ND> g1 = gs[0];
        G<ND> g2 = gs[1];
        assertEquals (3, g1.getWidth());
        assertEquals (3, g2.getWidth());
        assertEquals (6, g1.getHeight());
        assertEquals (6, g2.getHeight());
        assertEquals (18, g1.size());
        assertEquals (18, g2.size());
        System.err.println(g1);
        Thread.sleep(500);
        System.err.println("******************************");
        System.err.println(g2);
        Thread.sleep(500);
        System.err.println("******************************");
        for (int y= 0; y < 18; y++) {
            for (int x= 0; x < 3; x++) {

            }
        }

        gs = g.split(Split.HORIZONTAL);
        g1 = gs[0];
        g2 = gs[1];
        assertEquals (3, g1.getHeight());
        assertEquals (3, g2.getHeight());
        assertEquals (6, g1.getWidth());
        assertEquals (6, g2.getWidth());
        assertEquals (18, g1.size());
        assertEquals (18, g2.size());
        System.err.println(g1);
        Thread.sleep(500);
        System.err.println("******************************");
        System.err.println(g2);
        Thread.sleep(500);
        System.err.println("******************************");

    }

    public void testOptimize() throws Exception {
        System.out.println("ORIG");
        System.out.println(g);
        n[2] [2].addConnections (n[1][1], n[5][5], n[5][4]);

        n[4][4].addConnections(n[0][0], n[3][0]);
        n[3][5].addConnections(n[0][0], n[2][5]);
        n[0][5].addConnections(n[5][0], n[5][1]);
        n[0][5].addConnections(n[1][2], n[2][1], n[3][1], n[5][5]);
        n[3][3].addConnections(n[3][4], n[5][1], n[3][4]);
        n[1][2].addConnections(n[18][19], n[17][18], n[3][15]);
        n[11][12].addConnections(n[18][19], n[17][18], n[3][15]);
        n[19][17].addConnections(n[1][1], n[17][18], n[0][10], n[1][10]);
        n[10][13].addConnections(n[1][1], n[17][18], n[16][10], n[16][10]);
        assertEquals (1, new AP().getAffinity(n[3][3], n[3][4]));
        assertEquals (1, new AP().getAffinity(n[3][3], n[5][1]));

        g.optimize();
        Thread.sleep(500);
        System.out.println("RESULT");
        System.out.println(g);
    }

    public final void testFoo() {
        System.out.println("FOO!!");
    }

/*
    public void testCommit() throws Exception {
        G<ND>[] gs = g.split(Split.VERTICAL);
        System.err.println(gs[1].transform(Transforms.FLIP_VERTICAL));
        Thread.sleep(200);
//        System.err.println("-------------------------------");
        G<ND> xformed = gs[1].transform(Transforms.FLIP_VERTICAL);
        Object[][] vals = xformed.values();
        xformed.commit();
//        System.out.println(g);
        Object[][] actual = gs[1].values();
        for (int y = 0; y < xformed.getHeight(); y++) {
            for (int x=0; x < xformed.getWidth(); x++) {
                assertEquals ("Mismatch at coordinates " + x + "," + y + ".  Value was not committed.", vals[x][y], actual[x][y]);
            }
        }
    }

    public void testSwap() throws Exception {
        G.Swap swap = new G.Swap (1, 2, 3, 4);
        G.Swap swap2 = new G.Swap (3, 4, 1, 2);
        assertEquals (swap.hashCode(), swap2.hashCode());
        assertEquals (swap, swap2);
        swap = new G.Swap (0,1,0,0);
        swap2 = new G.Swap (0,0,0,1);
        assertEquals (swap.hashCode(), swap2.hashCode());
        assertEquals (swap, swap2);
    }
 */

    private static final class AP implements AffinityProvider<ND> {
        public int getAffinity(ND a, ND b) {
            if (a == null || b == null) return 0;
            int result = a.connections.contains(b) ? 1 : 0;
            if (result == 0) {
                result = b.connections.contains(a) ? 1 : 0;
            }
//            System.err.println("Result for " + a + " and " + b + " is " + result);
            return result;
        }
    }

    private static final class ND {
        private final String name;
        private final Set<ND> connections = new HashSet<ND> ();
        public ND(String name) {
            this.name = name;
        }

        void addConnections (ND... nd) {
            Set<ND> set = new HashSet<ND>(Arrays.asList(nd));
            for (ND n : nd) {
                n.connections.add(this);
            }
            assertFalse (set.contains(this));
            connections.addAll(set);
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ND other = (ND) obj;
            if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 47 * hash + (this.name != null ? this.name.hashCode() : 0);
            return hash;
        }


    }

}

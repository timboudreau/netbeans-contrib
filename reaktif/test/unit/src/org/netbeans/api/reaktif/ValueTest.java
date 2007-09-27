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

package org.netbeans.api.reaktif;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.junit.NbTestCase;

// XXX test:
// add/remove notify timing
// mismatched locks
// null value
// exceptions (and errors)
// toString etc.
// getUncached
// cannot call recalculate from inside calculate, must call resolve from inside
// changes fired in batch, after all values recalc'd, outside lock
// "changes" in unknown values do not fire
/**
 * @author Jesse Glick
 */
public class ValueTest extends NbTestCase {
    public ValueTest(String name) {
        super(name);
    }
    public void testSetup() {
        boolean asserts = false;
        assert asserts = true;
        assertTrue(asserts);
    }
    public void testTrivial() {
        assertEquals(3, new Sum(new Fixed(1), new Fixed(2))._get());
    }
    public void testPropagatedChanges() {
        Variable v = new Variable();
        VI f = new Fixed(1);
        VI s1 = new Sum(v, f);
        VI s2 = new Sum(s1, f);
        VI s3 = new Sum(s1, s2);
        L l = new L();
        v.addValueListener(l);
        f.addValueListener(l);
        s1.addValueListener(l);
        s2.addValueListener(l);
        s3.addValueListener(l);
        assertEquals(3, s3._get());
        assertEquals(1, v.calculated);
        assertEquals(1, f.calculated);
        assertEquals(1, s1.calculated);
        assertEquals(1, s2.calculated);
        assertEquals(1, s3.calculated);
        l.assertChanged();
        v.incr();
        l.assertChanged(v, s1, s2, s3);
        assertEquals(5, s3._get());
        assertEquals(2, v.calculated);
        assertEquals(1, f.calculated);
        assertEquals(2, s1.calculated);
        assertEquals(2, s2.calculated);
        assertEquals(2, s3.calculated);
        s1._recalculate();
        l.assertChanged();
        assertEquals(5, s3._get());
        assertEquals(2, v.calculated);
        assertEquals(1, f.calculated);
        assertEquals(3, s1.calculated);
        assertEquals(2, s2.calculated);
        assertEquals(2, s3.calculated);
    }
    public void testGated() {
        Variable v1 = new Variable();
        Variable v2 = new Variable();
        Toggle t = new Toggle();
        VI g = new Gate(v1, v2, t);
        L l = new L();
        v1.addValueListener(l);
        v2.addValueListener(l);
        t.addValueListener(l);
        g.addValueListener(l);
        assertEquals(0, g._get());
        assertEquals(1, v1.calculated);
        assertEquals(0, v2.calculated);
        assertEquals(1, t.calculated);
        assertEquals(1, g.calculated);
        l.assertChanged();
        v1.incr();
        l.assertChanged(v1, g);
        assertEquals(1, g._get());
        assertEquals(2, v1.calculated);
        assertEquals(0, v2.calculated);
        assertEquals(1, t.calculated);
        assertEquals(2, g.calculated);
        v1.incr();
        l.assertChanged(v1, g);
        assertEquals(2, g._get());
        assertEquals(3, v1.calculated);
        assertEquals(0, v2.calculated);
        assertEquals(1, t.calculated);
        assertEquals(3, g.calculated);
        assertEquals(0, v2._get());
        assertEquals(1, v2.calculated);
        l.assertChanged();
        v2.incr();
        l.assertChanged(v2);
        assertEquals(2, g._get());
        assertEquals(3, v1.calculated);
        assertEquals(2, v2.calculated);
        assertEquals(1, t.calculated);
        assertEquals(3, g.calculated);
        t.flip();
        l.assertChanged(t, g);
        assertEquals(1, g._get());
        assertEquals(3, v1.calculated);
        assertEquals(2, v2.calculated);
        assertEquals(2, t.calculated);
        assertEquals(4, g.calculated);
        v1.incr();
        l.assertChanged(v1);
        assertEquals(1, g._get());
        assertEquals(4, v1.calculated);
        assertEquals(2, v2.calculated);
        assertEquals(2, t.calculated);
        assertEquals(4, g.calculated);
    }
    public void testGcDownstream() {
        VI f1 = new Fixed(3);
        VI f2 = new Fixed(4);
        VI sum = new Sum(f1, f2);
        assertEquals(7, sum._get());
        Reference<?> r = new WeakReference<Object>(sum);
        sum = null;
        assertGC("can collect downstream values while holding upstream", r, new HashSet<VI>(Arrays.asList(f1, f2)));
    }
    static class Fixed extends VI {
        private final int x;
        Fixed(int x) {this.x = x;}
        protected Integer _calculate() {
            return x;
        }
    }
    static class Variable extends VI {
        private int x;
        void incr() {
            x++;
            recalculate();
        }
        protected Integer _calculate() {
            return x;
        }
    }
    static class Sum extends VI {
        private VI[] summands;
        Sum(VI... summands) {
            this.summands = summands;
        }
        protected Integer _calculate() {
            int s = 0;
            for (VI f : summands) {
                s += resolve(f);
            }
            return s;
        }
    }
    static class Gate extends VI {
        private VI a, b;
        private Value<Boolean,Error> control;
        Gate(VI a, VI b, Value<Boolean,Error> control) {
            this.a = a;
            this.b = b;
            this.control = control;
        }
        protected Integer _calculate() {
            return resolve(control) ? resolve(a) : resolve(b);
        }
    }
    static class Toggle extends V<Boolean,Error> {
        private boolean state = true;
        void flip() {
            state = !state;
            recalculate();
        }
        protected Boolean _calculate() {
            return state;
        }
    }
    static abstract class VI extends V<Integer,Error> {
        int _get() {
            return get();
        }
    }
    static abstract class V<T,X extends Throwable> extends Value<T,X> {
        public void _recalculate() {
            recalculate();
        }
        int calculated;
        protected final T calculate() throws X {
            calculated++;
            return _calculate();
        }
        protected abstract T _calculate() throws X;
        int added; // 0 A=> 1 R=> -1 A=> 2 R=> -2 A=> ...
        @Override
        protected final void addNotify() {
            assertTrue(added <= 0);
            added = 1 - added;
            _addNotify();
        }
        protected void _addNotify() {}
        @Override
        protected final void removeNotify() {
            assertTrue(added > 0);
            added = -added;
            _removeNotify();
        }
        protected void _removeNotify() {}
    }
    static class L implements ValueListener {
        private final Set<Value<?,?>> changed = new HashSet<Value<?,?>>();
        public void valueChanged(ValueEvent ev) {
            changed.add(ev.getSource());
        }
        void assertChanged(Value<?,?>... values) {
            assertEquals(new HashSet<Value<?,?>>(Arrays.asList(values)), changed);
            changed.clear();
        }
    }
}

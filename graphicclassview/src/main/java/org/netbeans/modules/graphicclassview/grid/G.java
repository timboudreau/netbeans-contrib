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
package org.netbeans.modules.graphicclassview.grid;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openide.util.Exceptions;

/**
 *
 * @author Tim Boudreau
 */
public class G<T> {

    private final AbstractGridData<T> data;

    protected G(AbstractGridData<T> data) {
        this.data = data;
    }

    public static <T> G<T> create(List<T> data, int width, int height, AffinityProvider<T> a) {
        ListData<T> d = new ListData<T>(data, a);
        GridData<T> gridData = new GridData<T>(d, width, height);
        return new G<T>(gridData);
    }

    public static <T> G<T> create(List<T> data, int width, int height, AffinityProvider<T> a, int gap) {
        GapData<T> d = new GapData<T>(data, gap, a);
        GridData<T> gridData = new GridData<T>(d, width, height);
        return new G<T>(gridData);
    }

    public Object[][] values() {
        Object[][] result = new Object[getWidth()][getHeight()];
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                result[x][y] = data.get(x, y);
            }
        }
        return result;
    }

    public final int getWidth() {
        return data.getWidth();
    }

    public final int getHeight() {
        return data.getHeight();
    }

    protected int getAffinity(T a, T b) {
        return data.getAffinity(a, b);
    }

    public int getAffinity(G<T> other) {
        return data.getAffinity(other.data);
    }

    public G<T> commit() {
        data.commit();
        return this;
    }

    public T get(int x, int y) {
        return data.get(x, y);
    }

    public int size() {
        return data.size();
    }

    public G<T> transform(Transform transform) {
        TransformData<T> d = new TransformData<T>(data, transform);
        return new G(d);
    }

    public G<T> transform(Transforms transform) {
        return transform(transform.transform());
    }

    public G<T>[] split(Split split) {
        switch (split) {
            case HORIZONTAL:
                if (getWidth() <= 1 || getHeight() <= 1) {
                    return null;
                }
                int h = getHeight() / 2;
                AbstractGridData<T> topData = data.createSubData(0, 0, getWidth(), h);
                AbstractGridData<T> bottomData = data.createSubData(0, h, getWidth(), h);
                assert topData.width > 0;
                assert bottomData.width > 0;
                assert topData.height > 0;
                assert bottomData.height > 0;
                return new G[]{new G<T>(topData), new G<T>(bottomData)};
            case VERTICAL:
                if (getWidth() <= 1 || getHeight() <= 1) {
                    return null;
                }
                int w = getWidth() / 2;
                AbstractGridData<T> leftData = data.createSubData(0, 0, w, getHeight());
                AbstractGridData<T> rightData = data.createSubData(w, 0, w, getHeight());
                assert leftData.width > 0;
                assert rightData.width > 0;
                assert leftData.height > 0;
                assert rightData.height > 0;
                return new G[]{new G<T>(leftData), new G<T>(rightData)};
            default:
                throw new AssertionError();
        }
    }

    static final class TransformPair {

        final Transform a, b;

        public TransformPair(Transform a, Transform b) {
            this.a = a;
            this.b = b;
        }
    }

    public void optimize() {
        optimize(this);
    }

    static <T> void optimize(G<T> g) {
        optimize(g, Split.HORIZONTAL);
        optimize(g, Split.VERTICAL);
    }

    static <T> void optimize(G<T> g, Split split) {
        G<T>[] gs = g.split(split);
        if (gs == null) {
            return;
        }
        int currAffinity = 0;
        Transform best = null;
        for (Transforms t : Transforms.values()) {
            if (!split.isTransformUseful(t)) {
                continue;
            }
            Transform f = t.transform();
            G<T> xformed = gs[1].transform(f);
            G<T>[] topBottom = xformed.split(split);
            if (topBottom == null) {
                break;
            }
            G<T> nearSide = topBottom[0];
            int aff = gs[0].getAffinity(nearSide);
            if (aff > currAffinity) {
                currAffinity = aff;
                best = f;
            }
        }
        if (currAffinity > 0 && !(best instanceof None)) {
            gs[1].transform(best).commit();
        }
        best = null;
        currAffinity = 0;
        for (Transforms t : Transforms.values()) {
            if (!split.isTransformUseful(t)) {
                continue;
            }
            Transform f = t.transform();
            G<T> xformed = gs[0].transform(f);
            G<T>[] topBottom = xformed.split(split);
            if (topBottom == null) {
                break;
            }
            G<T> nearSide = topBottom[1];
            int aff = gs[1].getAffinity(nearSide);
            if (aff > currAffinity) {
                currAffinity = aff;
                best = f;
            }
        }
        if (currAffinity > 0 && !(best instanceof None)) {
            gs[0].transform(best).commit();
        }
        optimize(gs[0]);
        optimize(gs[1]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName() + "@" + System.identityHashCode(this) + "[" + data + "]");
        int maxLen = 0;
        for (int i = 0; i < data.size(); i++) {
            T t = data.get(i);
            if (t != null) {
                maxLen = Math.max(maxLen, t.toString().length());
            }
        }
        char[] dashes = new char[(maxLen * (getWidth() + 1)) + ((getWidth() - 1) * 3) + 2];
        Arrays.fill(dashes, '-');
        sb.append(new String(dashes)).append("|\n");
        maxLen += 2;
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                if (x == 0) {
                    sb.append("| ");
                } else {
                    sb.append(" | ");
                }
                T t = data.get(x, y);
                assert (t == null || !t.getClass().isArray());
                String str = t == null ? "" : t.toString();
                sb.append(str);
                if (str.length() < maxLen) {
                    char[] c = new char[maxLen - str.length()];
                    Arrays.fill(c, ' ');
                    sb.append(new String(c));
                }
            }
            sb.append("\n");
            sb.append(new String(dashes)).append("|\n");
        }
        return sb.toString();
    }

    public enum Transforms {

        NONE,
        FLIP_HORIZONTAL,
        //        REVERSE,
        FLIP_VERTICAL;

        static Transform merge(Transforms... xforms) {
            assert xforms.length > 0;
            if (xforms.length == 1) {
                return xforms[0].transform();
            }
            Transform result = xforms[0].transform();
            for (int i = 1; i < xforms.length; i++) {
                result = result.merge(xforms[i].transform());
            }
            return result;
        }

        Transform transform() {
            switch (this) {
                case NONE:
                    return new None();
                case FLIP_HORIZONTAL:
                    return new FlipHorizontal();
//                case REVERSE :
//                    return new Reverse();
                case FLIP_VERTICAL:
                    return new FlipVertical();
                default:
                    throw new AssertionError();
            }
        }

        static Transform merge(Transform... others) {
            assert others.length > 0;
            if (others.length == 1) {
                return others[0];
            }
            Transform result = new MergeTransform(others);
            return result;
        }
    }

    public static abstract class Transform {

        public abstract Point translate(int x, int y, int width, int height);

        public abstract Point reverse(int x, int y, int width, int height);

        Transform merge(Transform other) {
            return new MergeTransform(this, other);
        }

        @Override
        public int hashCode() {
            return getClass().hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return o != null && o.getClass() == getClass();
        }
    }

    public static final class None extends Transform {

        @Override
        public Point translate(int x, int y, int width, int height) {
            return new Point(x, y);
        }

        @Override
        public Point reverse(int x, int y, int width, int height) {
            return new Point(x, y);
        }
    }

    public static final class FlipVertical extends Transform {

        public Point translate(int x, int y, int width, int height) {
            Point result = new Point(x, y);
            result.x = width - (x + 1);
            return result;
        }

        public Point reverse(int x, int y, int width, int height) {
            return translate(x, y, width, height);
        }
    }

    private static final class FlipHorizontal extends Transform {

        public Point translate(int x, int y, int width, int height) {
            Point result = new Point(x, y);
            result.y = height - (y + 1);
            return result;
        }

        public Point reverse(int x, int y, int width, int height) {
            return translate(x, y, width, height);
        }
    }

    private static final class Reverse extends Transform {

//        public Point translate(int x, int y, int width, int height) {
//            Point result = new Point(x, y);
//            int total = width * height;
//            int ix = (x == 0 ? 0 : (width % x)) + (y * height);
//            int resultIx = total - ix;
//            result.x = width % resultIx;
//            result.y = height / resultIx;
////            result.x = width - (total % (x + 1));
////            result.y = height - (total / (y + 1));
//            return result;
//        }
        public Point translate(int x, int y, int width, int height) {
            x = width - x;
            y = height - y;
            return new Point(x, y);
        }

        public Point reverse(int x, int y, int width, int height) {
            return translate(x, y, width, height);
        }
    }

    private static final class MergeTransform extends Transform {

        private final Transform[] others;

        MergeTransform(Transform... others) {
            this.others = others;
        }

        @Override
        public Point translate(int x, int y, int width, int height) {
            Point p = new Point(x, y);
            for (int i = 0; i < others.length; i++) {
                Transform t = others[i];
                p = t.translate(p.x, p.y, width, height);
            }
            return p;
        }

        @Override
        public Point reverse(int x, int y, int width, int height) {
            Point p = new Point(x, y);
            for (int i = others.length - 1; i >= 0; i--) {
                Transform t = others[i];
                p = t.reverse(p.x, p.y, width, height);
            }
            return p;
        }
    }

    public enum Split {

        VERTICAL,
        HORIZONTAL;

        public boolean isTransformUseful(Transforms x) {
            switch (x) {
                case FLIP_HORIZONTAL:
                    return this == HORIZONTAL;
                case FLIP_VERTICAL:
                    return this == VERTICAL;
//                case REVERSE :
                case NONE:
                    return true;
                default:
                    throw new AssertionError();
            }
        }
    }

    public static abstract class Data<T> {

        public abstract int size();

        public abstract T get(int index);

        public abstract void swap(int ixA, int ixB);

        public abstract int getAffinity(T a, T b);

        public abstract void set(int ix, T t);

        public int getAffinity(Data<T> other) {
            int sz = size();
            int osz = other.size();
            int result = 0;
            for (int i = 0; i < sz; i++) {
                T t = get(i);
                for (int j = 0; j < osz; j++) {
                    T tt = other.get(j);
                    result += getAffinity(t, tt);
                }
            }
            return result;
        }

        public void commit() {
            //do nothing
        }
    }

    public static abstract class AbstractGridData<T> extends Data<T> {

        private final int width;
        private final int height;

        protected AbstractGridData(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public final int getHeight() {
            return height;
        }

        public final int getWidth() {
            return width;
        }

        public T get(int x, int y) {
            return get(indexOf(x, y));
        }

        public void swap(int aX, int aY, int bX, int bY) {
            swap(indexOf(aX, aY), indexOf(bX, bY));
        }

        public void set(int x, int y, T t) {
            set(indexOf(x, y), t);
        }

        protected int indexOf(int x, int y) {
            assert x >= 0 : "X < 0";
            assert y >= 0 : "Y < 0";
            int result = translateIndex(x + (y * width));
            assert result >= 0 : this + " translated " + x + ", " + y + " to " + result;
            return result;
        }

        protected int translateIndex(int index) {
            return index;
        }

        public boolean canRotate() {
            return getWidth() == getHeight();
        }

        protected AbstractGridData<T> createSubData(int x, int y, int width, int height) {
            return new SubData(x, y, width, height);
        }

        class SubData extends AbstractGridData<T> {

            private final int offY;
            private final int offX;

            SubData(int offX, int offY, int width, int height) {
                super(width, height);
                this.offX = offX;
                this.offY = offY;
            }

            @Override
            public T get(int x, int y) {
                return AbstractGridData.this.get(translateX(x), translateY(y));
            }

            @Override
            public int indexOf(int x, int y) {
                return AbstractGridData.this.indexOf(translateX(x), translateY(y));
            }

            int translateX(int x) {
                return offX + x;
            }

            int translateY(int y) {
                return offY + y;
            }

            @Override
            public void set(int x, int y, T t) {
                x = translateX(x);
                y = translateY(y);
                AbstractGridData.this.set(x, y, t);
            }

            @Override
            public void swap(int aX, int aY, int bX, int bY) {
                aX = translateX(aX);
                aY = translateY(aY);
                bX = translateX(bX);
                bY = translateY(bY);
                AbstractGridData.this.swap(aX, aY, bX, bY);
            }

            @Override
            public T get(int index) {
                int x = index % getWidth();
                int y = index / getWidth();
                return AbstractGridData.this.get(translateX(x), translateY(y));
            }

            public void set(int index, T t) {
                int x = index % getWidth();
                int y = index / getWidth();
                AbstractGridData.this.set(translateX(x), translateY(y), t);
            }

            @Override
            public void swap(int ixA, int ixB) {
                ixA = translateX(ixA);
                ixB = translateY(ixB);
                AbstractGridData.this.swap(ixA, ixB);
            }

            @Override
            public int getAffinity(T a, T b) {
                return AbstractGridData.this.getAffinity(a, b);
            }

            @Override
            public int size() {
                return getWidth() * getHeight();
            }

            @Override
            protected AbstractGridData<T> createSubData(int aX, int aY, int width, int height) {
                aX = translateX(aX);
                aY = translateY(aY);
                return AbstractGridData.this.createSubData(aX, aY, width, height);
            }
        }
    }

    public static final class GridData<T> extends AbstractGridData<T> {

        private final Data<T> data;

        GridData(Data<T> data, int width, int height) {
            super(width, height);
            this.data = data;
        }

        @Override
        public T get(int index) {
            return data.get(index);
        }

        @Override
        public void swap(int ixA, int ixB) {
            data.swap(ixA, ixB);
        }

        @Override
        public int getAffinity(T a, T b) {
            return data.getAffinity(a, b);
        }

        @Override
        public int size() {
            return data.size();
        }

        public void set(int ix, T t) {
            data.set(ix, t);
        }
    }

    public static class ListData<T> extends Data<T> {

        private final List<T> data;
        private final AffinityProvider<T> affinity;

        ListData(List<T> data, AffinityProvider<T> affinity) {
            this.data = data;
            this.affinity = affinity;
        }

        public T get(int index) {
            return index < data.size() ? data.get(index) : null;
        }

        public void swap(int ixA, int ixB) {
            try {
                T a = data.get(ixA);
                T b = data.get(ixB);
                data.set(ixA, b);
                data.set(ixB, a);
            } catch (IndexOutOfBoundsException e) {
                Exceptions.printStackTrace(e);
            }
        }

        @Override
        public final int getAffinity(T a, T b) {
            return affinity.getAffinity(a, b);
        }

        @Override
        public int size() {
            return data.size() + (data.size() % 4);
        }

        @Override
        public void set(int ix, T t) {
            data.set(ix, t);
        }
    }

    public static final class Swap {

        final int ax, ay, bx, by;

        public Swap(int ax, int ay, int bx, int by) {
            this.ax = ax;
            this.ay = ay;
            this.bx = bx;
            this.by = by;
        }

        @Override
        public int hashCode() {
            int aHash = ax ^ (ay * 31);
            int bHash = bx ^ (by * 31);
            return aHash + bHash;
        }

        @Override
        public boolean equals(Object o) {
            boolean result = o != null && o.getClass() == Swap.class;
            if (result) {
                Swap swap = (Swap) o;
                result = (ax == swap.ax && ay == swap.ay && bx == swap.bx && by == swap.by)
                        || (ax == swap.bx && ay == swap.by && bx == swap.ax && by == swap.ay);
            }
            return result;
        }
    }

    public static class SwapData<T> extends AbstractGridData<T> {

        private final AbstractGridData<T> real, proxy;

        SwapData(AbstractGridData<T> real, AbstractGridData<T> proxy) {
            super(real.getWidth(), real.getHeight());
            assert real.getWidth() == proxy.getWidth();
            assert real.getHeight() == proxy.getHeight();
            this.real = real;
            this.proxy = proxy;
        }

        @Override
        public int size() {
            return proxy.size();
        }

        @Override
        public T get(int index) {
            return proxy.get(index);
        }

        @Override
        public void swap(int ixA, int ixB) {
            proxy.swap(ixA, ixB);
        }

        @Override
        public int getAffinity(T a, T b) {
            return proxy.getAffinity(a, b);
        }

        @Override
        public void commit() {
            for (int y = 0; y < getHeight(); y++) {
                for (int x = 0; x < getWidth(); x++) {
                    T realVal = real.get(x, y);
                    T proxyVal = proxy.get(x, y);
                    real.set(x, y, proxyVal);
                    proxy.set(x, y, realVal);
                }
            }
        }

        @Override
        public void set(int ix, T t) {
            proxy.set(ix, t);
        }
    }

    public static class TransformData<T> extends AbstractGridData<T> {

        private Transform xform;
        private AbstractGridData<T> data;

        TransformData(AbstractGridData<T> data, Transform xform) {
            super(data.getWidth(), data.getHeight());
            this.data = data;
            this.xform = xform;
        }

        public TransformData<T> addTransform(Transform xform) {
            return new TransformData<T>(data, Transforms.merge(this.xform, xform));
        }

        public String toString() {
            return super.toString() + "[" + xform + "] for [" + data + "]";
        }

        @Override
        public void swap(int aX, int aY, int bX, int bY) {
            Point aPoint = xform.translate(aX, aY, getWidth(), getHeight());
            Point bPoint = xform.translate(bX, bY, getWidth(), getHeight());
            assert aPoint.x < getWidth() : aPoint.x + " > " + getWidth() + " on " + this;
            assert bPoint.x < getWidth() : aPoint.x + " > " + getWidth() + " on " + this;
            assert aPoint.y < getHeight() : aPoint.y + " > " + getHeight() + " on " + this;
            assert bPoint.y < getHeight() : bPoint.y + " > " + getHeight() + " on " + this;
            data.swap(aPoint.x, aPoint.y, bPoint.x, bPoint.y);
        }

        @Override
        public void commit() {
            if (xform instanceof None) {
                return;
            }
            Set<Swap> done = new HashSet<Swap>();
            for (int x = 0; x < getWidth(); x++) {
                for (int y = 0; y < getHeight(); y++) {
                    Point p = xform.reverse(x, y, getWidth(), getHeight());
                    Swap swap = new Swap(x, y, p.x, p.y);
                    if (!done.contains(swap)) {
//                        data.swap(x, y, p.x, p.y);
                        swap(x, y, p.x, p.y);
                        done.add(swap);
                    }
                }
            }
        }

        @Override
        public int size() {
            return data.size();
        }

        @Override
        public T get(int index) {
            int x = index % getWidth();
            int y = index / getWidth();
            Point p = xform.translate(x, y, getWidth(), getHeight());
            assert p.x >= 0 : xform + " translated " + x + ", " + y + " to " + p.x + ", " + p.y + " for index " + index + " width " + getWidth() + " height " + getHeight();
            assert p.y >= 0 : xform + " translated " + x + ", " + y + " to " + p.x + ", " + p.y + " for index " + index + " width " + getWidth() + " height " + getHeight();
            return data.get(p.x, p.y);
        }

        @Override
        public void set(int index, T t) {
            int x = index % getWidth();
            int y = index / getWidth();
            Point p = xform.translate(x, y, getWidth(), getHeight());
            assert p.x >= 0 : xform + " translated " + x + ", " + y + " to " + p.x + ", " + p.y + " for index " + index + " width " + getWidth() + " height " + getHeight();
            assert p.y >= 0 : xform + " translated " + x + ", " + y + " to " + p.x + ", " + p.y + " for index " + index + " width " + getWidth() + " height " + getHeight();
            data.set(p.x, p.y, t);
        }

        @Override
        public int getAffinity(T a, T b) {
            return data.getAffinity(a, b);
        }

        @Override
        public void swap(int ixA, int ixB) {
            data.swap(ixA, ixB);
        }
    }

    public static final class GapData<T> extends ListData<T> {
        public GapData(List<T> data, int gap, AffinityProvider<T> p) {
            super (gapList(data, gap), p);
        }

        private static <T> List<T> gapList(List<T> l, int gap) {
            List<T> r = new ArrayList<T>(l.size() + (l.size() * gap));
            for (int i = 0; i < l.size(); i++) {
                r.add (l.get(i));
                for (int j= 0; j < gap; j++) {
                    r.add(null);
                }
            }
            return r;
        }
    }
}

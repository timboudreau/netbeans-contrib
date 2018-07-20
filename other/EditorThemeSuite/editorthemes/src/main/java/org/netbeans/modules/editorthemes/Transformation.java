/*
 * Transformation.java
 *
 * Created on Jul 6, 2007, 8:08:10 PM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.editorthemes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * Class which can perform a transformation on all colors in a theme.
 *
 * @author Tim Boudreau
 */
public abstract class Transformation { //implements Comparable <Transformation> {
    private Transformation() {}

    public void process (ColorModel mdl, String profile) {
        Collection <MutableAttributeSet> attrs = convert(mdl.getAnnotations(profile));
        apply (attrs);
        mdl.setAnnotations(profile, convertBack(attrs));
        attrs = convert (mdl.getHighlightings(profile));
        apply (attrs);
        mdl.setHighlightings(profile, convertBack(attrs));
        for (String lang : mdl.getLanguages()) {
            System.err.println("Process language " + lang);
            attrs = convert (mdl.getCategories(profile, lang));
            apply (attrs);
            mdl.setCategories(profile, lang, convertBack(attrs));
        }
    }

    private static Collection <AttributeSet> convertBack(Collection <MutableAttributeSet> c) {
        //XXX why is this needed?
        List <AttributeSet> l = new ArrayList <AttributeSet> ();
        for (MutableAttributeSet m : c) {
            l.add (m.copyAttributes());
        }
        return l;
    }

    private static Collection <MutableAttributeSet> convert (Collection <AttributeSet> c) {
        List <MutableAttributeSet> result = new ArrayList <MutableAttributeSet> (c.size());
        for (AttributeSet a : c) {
            result.add (new SimpleAttributeSet(a));
        }
        return result;
    }

    public static Transformation createInverseTransformation () {
        return new Inverse();
    }

    public static Transformation createHueTransformation (float rotateBy) {
        return new HSBComponent(rotateBy, 0);
    }

    public static Transformation createSaturationTransformation (float by) {
        return new HSBComponent(by, 1);
    }

    public static Transformation createBrightnessTransformation (float by) {
        return new HSBComponent(by, 2);
    }

    public static Transformation createCompound (Transformation... xforms) {
        return new Compound (xforms);
    }

    final void apply(Iterable<MutableAttributeSet> attrs) {
        for (MutableAttributeSet a : attrs) {
            process (a);
        }
    }

//    public int compareTo (Transformation other) {
//
//    }

    private void process (MutableAttributeSet a) {
        Map <Object, Object> m = new HashMap <Object, Object> ();
        for (Enumeration en=a.getAttributeNames(); en.hasMoreElements();) {
            Object key = en.nextElement();
            Object val = a.getAttribute(key);
            Object nue = null;
            if (val instanceof Color) {
                nue = processColor ((Color)val);
            }
            if (nue != null && nue != val) {
                System.err.println(a.getAttribute(StyleConstants.NameAttribute) + ":" + val + " processed into " + nue);
                m.put (key, nue);
            }
        }
        for (Map.Entry<?,?> e : m.entrySet()) {
            a.removeAttribute(e.getKey());
            a.addAttribute(e.getKey(), e.getValue());
        }
    }

    abstract Color processColor (Color val);

    private static class Inverse extends Transformation {
        Color processColor(Color val) {
            int red = 255 - val.getRed();
            int green = 255 - val.getGreen();
            int blue = 255 - val.getBlue();
            return new Color (red, green, blue);
        }

        public XFormKind getKind() {
            return Transformation.XFormKind.INVERSE;
        }
    }

    public abstract XFormKind getKind();

    private static class HSBComponent extends Transformation {
        float by;
        int compIndex;
        HSBComponent (float by, int compIndex) {
            this.by = by;
            assert by < 1.0F;
            this.compIndex = compIndex;
        }

        Color processColor (Color c) {
            float[] f = new float[3];
            c.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), f);
            f[compIndex] = processComponent (f[compIndex]);
            return Color.getHSBColor(f[0], f[1], f[2]);
        }

        private float processComponent (float component) {
            float result = component + by;
            if (result > 1.0F) {
                result = 1.0F - result;
            }
            return result;
        }


        public XFormKind getKind() {
            switch (compIndex) {
            case 0 :
                return Transformation.XFormKind.HUE;
            case 1 :
                return XFormKind.SATURATION;
            case 2 :
                return XFormKind.BRIGHTNESS;
            default :
                throw new AssertionError ("" + compIndex);
            }
        }

    }

    private static final class Compound extends Transformation {
        private final Transformation[] xforms;
        Compound (Transformation[] xforms) {
            this.xforms = xforms;
        }

        Color processColor(Color val) {
            for (Transformation t : xforms) {
                val = t.processColor(val);
            }
            return val;
        }

        public XFormKind getKind() {
            return Transformation.XFormKind.COMPOUND;
        }
    }


    public enum XFormKind {
        HUE, SATURATION, INVERSE, BRIGHTNESS, COMPOUND,
    }
}

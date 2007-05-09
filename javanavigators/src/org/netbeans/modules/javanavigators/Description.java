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
package org.netbeans.modules.javanavigators;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.swing.Icon;
import org.netbeans.api.java.source.ElementHandle;
import org.openide.filesystems.FileObject;

public class Description {
    public static final Comparator<Description> ALPHA_COMPARATOR =
        new DescriptionComparator(true);
    public static final Comparator<Description> POSITION_COMPARATOR = 
        new DescriptionComparator(false);    

    FileObject fileObject; // For the root description

    String name;
    ElementHandle<? extends Element> elementHandle;
    ElementKind kind;
    Set<Modifier> modifiers;        
    List<Description> subs; 
    String htmlHeader;
    long pos;
    Description parent = null;
    Icon icon;
    String javadoc;
    boolean inner;

    Description( ) {

    }

    @Override
    public boolean equals(Object o) {

        if ( o == null ) {
            return false;
        }

        if ( !(o instanceof Description)) {
            return false;
        }

        Description d = (Description)o;

        if ( kind != d.kind ) {
            return false;
        }

        if (((name == null) != (d.name == null)) || (name != null && !name.equals(d.name)) ) {
            return false;
        }

        if ( ((elementHandle != null) != (d.elementHandle != null)) || (elementHandle != null && !elementHandle.signatureEquals(d.elementHandle)) ) {
            return false;
        }
        return true;
    }
    
    public boolean isInner() {
        return inner || (parent != null && parent.inner);
    }
    
    private String txt;
    public String toString() {
        if (txt == null) {
            StringBuilder sb = new StringBuilder("<html>"); //NOI18N            
            Description d = this;
            while (d != null && d.isInner() && d.parent != null) {
                if (d.parent.isInner()) {
                    //Don't show inner class name truncated on inner class constructors
                    if ((d.elementHandle.getKind() != ElementKind.CONSTRUCTOR || d != this)) {
                        sb.insert (6, '.');
                        sb.insert(6, truncate(d.parent.name));
                    }
                    d = d.parent;
                } else {
                    d = null;
                }
            }
            sb.append (htmlHeader);
            txt = sb.toString();
        }
        return txt;
    }

    private static String truncate (String s) {
        if (s == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder (s.length());
        char[] c = s.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (i == 0 || 
                Character.isUpperCase(c[i]) || 
                !Character.isLetter(c[i])) {
                    sb.append (c[i]);
            }
        }
        return sb.toString();
    }
    
    long getPosition() {
        return pos;
    }
    
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 29 * hash + (this.kind != null ? this.kind.hashCode() : 0);
        // hash = 29 * hash + (this.modifiers != null ? this.modifiers.hashCode() : 0);
        return hash;
    }

    private static class DescriptionComparator implements Comparator<Description> {

        boolean alpha;

        DescriptionComparator( boolean alpha ) {
            this.alpha = alpha;
        }

        public int compare(Description d1, Description d2) {

            if ( alpha ) {
                if ( k2i(d1.kind) != k2i(d2.kind) ) {
                    return k2i(d1.kind) - k2i(d2.kind);
                } 
                boolean i1 = d1.inner;
                boolean i2 = d2.inner;
                if (i1 != i2) {
                    return i1 && !i2 ? -1 : 1;
                } else {
                    int result = d1.name.compareTo(d2.name);
                    return result;
                }
            }
            else {
                long p1 = d1.getPosition();
                long p2 = d2.getPosition();
                int result = p1 == p2 ? 0 : p1 < p2 ? -1 : 1;
                return result;
//                return (int) (p1 - p2);
            }
        }

        int k2i( ElementKind kind ) {
            switch( kind ) {
                case CONSTRUCTOR:
                    return 3;
                case METHOD:
                    return 1;
                case FIELD:
                    return 2;
                case CLASS:
                case INTERFACE:
                case ENUM:
                case ANNOTATION_TYPE:                        
                    return 4;
                default:
                    return 100;
            }
        }
    }
}

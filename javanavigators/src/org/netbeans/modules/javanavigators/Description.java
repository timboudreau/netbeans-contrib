/*
 * Description.java
 *
 * Created on February 9, 2007, 11:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.javanavigators;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
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

    Description( ) {
        System.err.println("create a description");
    }

    @Override
    public boolean equals(Object o) {

        if ( o == null ) {
            //System.out.println("- f nul");
            return false;
        }

        if ( !(o instanceof Description)) {
            // System.out.println("- not a desc");
            return false;
        }

        Description d = (Description)o;

        if ( kind != d.kind ) {
            // System.out.println("- kind");
            return false;
        }

        if (((name == null) != (d.name == null)) || (name != null && !name.equals(d.name)) ) {
            // System.out.println("- name");
            return false;
        }

        if ( ((elementHandle != null) != (d.elementHandle != null)) || (elementHandle != null && !elementHandle.signatureEquals(d.elementHandle)) ) {
            return false;
        }

        /*
        if ( !modifiers.equals(d.modifiers)) {
            // E.println("- modifiers");
            return false;
        }
        */

        // System.out.println("Equals called");            
        return true;
    }
    
    public String toString() {
        return htmlHeader;
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

                return d1.name.compareTo(d2.name);
            }
            else {
                return d1.pos == d2.pos ? 0 : d1.pos < d2.pos ? -1 : 1;
            }
        }

        int k2i( ElementKind kind ) {
            switch( kind ) {
                case CONSTRUCTOR:
                    return 1;
                case METHOD:
                    return 2;
                case FIELD:
                    return 3;
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

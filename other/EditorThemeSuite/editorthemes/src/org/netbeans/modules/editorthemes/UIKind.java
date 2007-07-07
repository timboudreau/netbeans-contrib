/*
 * UIKind.java
 *
 * Created on Jul 2, 2007, 2:28:52 PM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.editorthemes;

import java.util.EnumSet;
import java.util.Set;
import org.openide.util.NbBundle;

/**
 *
 * @author Tim Boudreau
 */
public enum UIKind {
    SYNTAX, HIGHLIGHTINGS, ANNOTATIONS; //, DIFF;
    public Set <Controls> controls() {
        switch (this) {
        case SYNTAX :
            return EnumSet.of (Controls.BACKGROUND,
                    Controls.FOREGROUND, Controls.ITALIC,
                    Controls.BOLD, Controls.UNDERLINE,
                    Controls.STRIKETHROUGH, Controls.WAVE_UNDERLINE);
        case HIGHLIGHTINGS :
            return EnumSet.of(Controls.BACKGROUND, Controls.FOREGROUND);
        case ANNOTATIONS :
            return EnumSet.of(Controls.FOREGROUND, Controls.BACKGROUND,
                    Controls.WAVE_UNDERLINE);
//        case DIFF :
//            return EnumSet.of(Controls.BACKGROUND);
        default :
            throw new AssertionError();
        }
    }

    public String toString() {
        return NbBundle.getMessage (UIKind.class, name());
    }
}

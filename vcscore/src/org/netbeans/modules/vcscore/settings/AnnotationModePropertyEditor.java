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

package org.netbeans.modules.vcscore.settings;

import java.beans.PropertyEditorSupport;

import org.openide.util.NbBundle;

/**
 * Property editor for file annotation property of the global settings.
 *
 * @author Martin Entlicher
 */
public class AnnotationModePropertyEditor extends PropertyEditorSupport {

    /** localized string*/
    private final static String NONE = NbBundle.getMessage(RefreshModePropertyEditor.class, "AnnotationModePropertyEditor.noAnnotation");

    /** localized string*/
    private final static String FULL = NbBundle.getMessage(RefreshModePropertyEditor.class, "AnnotationModePropertyEditor.fullAnnotation");

    /** localized string*/
    private final static String SHORT = NbBundle.getMessage(RefreshModePropertyEditor.class, "AnnotationModePropertyEditor.shortAnnotation");

    /** localized string*/
    private final static String FULL_MODIFIED = NbBundle.getMessage(RefreshModePropertyEditor.class, "AnnotationModePropertyEditor.fullModifiedAnnotation");

    /** localized string*/
    //private final static String COLORED = NbBundle.getMessage(RefreshModePropertyEditor.class, "AnnotationModePropertyEditor.coloredAnnotation");

    /** array of annotation modes */
    private static final String[] modes = { NONE, FULL, SHORT, FULL_MODIFIED };

    /** @return names of the supported annotation modes */
    public String[] getTags() {
        return modes;
    }

    /** @return text for the current value */
    public String getAsText () {
        Integer mode = (Integer) getValue();
        int index = mode.intValue();
        if (index < 0 || index > 3) {
            return FULL;
        }
        return modes[index];
    }

    /** @param text A text for the current value. */
    public void setAsText (String text) {
        if (text.equals(NONE)) {
            setValue(new Integer(0));
        } else if (text.equals(FULL)) {
            setValue(new Integer(1));
        } else if (text.equals(SHORT)) {
            setValue(new Integer(2));
        } else if (text.equals(FULL_MODIFIED)) {
            setValue(new Integer(3));
        //} else if (text.equals(COLORED)) {
        //    setValue(new Integer(4));
        } else {
            throw new IllegalArgumentException (text);
        }
    }

    public void setValue(Object value) {
        super.setValue(value);
    }
}


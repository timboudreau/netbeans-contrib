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


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

package org.netbeans.modules.vcscore.grouping;

import java.beans.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openide.util.NbBundle;

/** Property editor for AutoAddition property of the Vgs Group Settings object
*
* @author Milos Kleint

*/
public class AutoAdditionPropertyEditor extends PropertyEditorSupport {

    /** localized string*/
    private final static String MANUAL = NbBundle.getMessage(AutoAdditionPropertyEditor.class, "AutoAddition.manual"); // NOI18N

    /** localized string*/
    private final static String TO_DEFAULT = NbBundle.getMessage(AutoAdditionPropertyEditor.class, "AutoAddition.toDefaultGroup"); // NOI18N

    /** localized string*/
    private final static String ASK = NbBundle.getMessage(AutoAdditionPropertyEditor.class, "AutoAddition.ask"); // NOI18N

    /** array of hosts */
    private static final String[] modes = {MANUAL, TO_DEFAULT};

    /** @return names of the supported LookAndFeels */
    public String[] getTags() {
        return modes;
    }

    /** @return text for the current value */
    public String getAsText () {
        Integer mode = (Integer) getValue();
        if (mode.intValue() == 0 ) {
            return MANUAL;
        } 
        else if (mode.intValue() == 2) {
            return ASK;
        }
        else {
            return TO_DEFAULT;
        }
    }

    /** @param text A text for the current value. */
    public void setAsText (String text) {
        if (text.equals(MANUAL)) {
            setValue(new Integer(0));
            return;
        }
        if (text.equals(TO_DEFAULT)) {
            setValue(new Integer(1));
            return;
        }
        if (text.equals(ASK)) {
            setValue(new Integer(2));
            return;
        }
        throw new IllegalArgumentException ();
    }

    public void setValue(Object value) {
        super.setValue(value);
    }
}


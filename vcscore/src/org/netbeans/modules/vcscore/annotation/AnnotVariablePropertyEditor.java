/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.annotation;

import java.beans.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openide.util.NbBundle;

/** Property editor for UiMode property of the JavaCvsFileSystem
*
* @author Milos Kleint

*/
public class AnnotVariablePropertyEditor extends PropertyEditorSupport {

    private static final java.util.ResourceBundle bundle = NbBundle.getBundle(AnnotVariablePropertyEditor.class);

    /** array of hosts */
    private  String[] modes;

    public AnnotVariablePropertyEditor(String[] array) {
        modes = array;
    }
    /** @return names of the supported LookAndFeels */
    public String[] getTags() {
        return modes;
    }

    /** @return text for the current value */
    public String getAsText () {
        String value = (String) getValue();
        return value;
    }

    /** @param text A text for the current value. */
    public void setAsText (String text) {
        setValue(text);
        return;
    }

    public void setValue(Object value) {
        super.setValue(value);
    }
}


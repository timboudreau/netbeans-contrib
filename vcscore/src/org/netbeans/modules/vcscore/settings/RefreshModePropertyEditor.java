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

package org.netbeans.modules.vcscore.settings;

import java.beans.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openide.util.NbBundle;

/** Property editor for refresh property of the VcsFileSystem
*
* @author Milos Kleint

*/
public class RefreshModePropertyEditor extends PropertyEditorSupport {

    private static final java.util.ResourceBundle bundle = NbBundle.getBundle(RefreshModePropertyEditor.class);

    /** localized string*/
    private final static String NO_REFRESH = bundle.getString("RefreshModePropertyEditor.noRefresh");

    /** localized string*/
    private final static String DIR_BASED = bundle.getString("RefreshModePropertyEditor.dirOnOpen");

    /** localized string*/
    private final static String MOUNT = bundle.getString("RefreshModePropertyEditor.recursOnMount");

    /** localized string*/
    private final static String RESTART = bundle.getString("RefreshModePropertyEditor.recursOnRestart");

    /** localized string*/
    private final static String MOUNT_AND_RESTART = bundle.getString("RefreshModePropertyEditor.recursOnMountAndRestart");

    /** array of hosts */
    private static final String[] modes = {NO_REFRESH, DIR_BASED, MOUNT, RESTART, MOUNT_AND_RESTART};

    /** @return names of the supported LookAndFeels */
    public String[] getTags() {
        return modes;
    }

    /** @return text for the current value */
    public String getAsText () {
        Integer mode = (Integer) getValue();
        int index = mode.intValue();
        if (index < 0 || index > 4) {
            return NO_REFRESH;
        }
        return modes[index];
    }

    /** @param text A text for the current value. */
    public void setAsText (String text) {
        if (text.equals(NO_REFRESH)) {
            setValue(new Integer(0));
            return;
        }
        if (text.equals(DIR_BASED)) {
            setValue(new Integer(1));
            return;
        }
        if (text.equals(MOUNT)) {
            setValue(new Integer(2));
            return;
        }
        if (text.equals(RESTART)) {
            setValue(new Integer(3));
            return;
        }
        if (text.equals(MOUNT_AND_RESTART)) {
            setValue(new Integer(4));
            return;
        }
        throw new IllegalArgumentException ();
    }

    public void setValue(Object value) {
        super.setValue(value);
    }
}


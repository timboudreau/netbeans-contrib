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

    /** localized string*/
    private final static String NO_REFRESH = NbBundle.getMessage(RefreshModePropertyEditor.class, "RefreshModePropertyEditor.noRefresh");

    /** localized string*/
    private final static String DIR_BASED = NbBundle.getMessage(RefreshModePropertyEditor.class, "RefreshModePropertyEditor.dirOnOpen");

    /** localized string*/
    private final static String MOUNT = NbBundle.getMessage(RefreshModePropertyEditor.class, "RefreshModePropertyEditor.recursOnMount");

    /** localized string*/
    private final static String RESTART = NbBundle.getMessage(RefreshModePropertyEditor.class, "RefreshModePropertyEditor.recursOnRestart");

    /** localized string*/
    private final static String MOUNT_AND_RESTART = NbBundle.getMessage(RefreshModePropertyEditor.class, "RefreshModePropertyEditor.recursOnMountAndRestart");

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


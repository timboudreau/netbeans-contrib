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

package org.netbeans.modules.corba.idl.editor.settings;

import org.openide.modules.ModuleInstall;
import org.netbeans.editor.Settings;

/**
 * @author Karel Gardas
 */
public class RestoreColoring extends ModuleInstall {
    private static final long serialVersionUID = 6847217344357938537L;

    public void restored () {
        Settings.addInitializer (new IDLEditorSettingsInitializer());
    }

    public void uninstalled () {
	super.uninstalled ();
        Settings.removeInitializer(IDLEditorSettingsInitializer.NAME);
    }
}
/*
 * <<Log>>
 *  3    Jaga      1.1.1.0     3/16/00  Miloslav Metelka patch
 *  2    Gandalf   1.1         2/8/00   Karel Gardas    
 *  1    Gandalf   1.0         11/9/99  Karel Gardas    
 * $
 */

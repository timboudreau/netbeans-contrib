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
package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.add;

import org.netbeans.lib.cvsclient.command.*;

/**
 * Describes add information for a file. This is the result of doing a
 * cvs add command. The fields in instances of this object are populated
 * by response handlers.
 * @author  Thomas Singer
 */
public class AddInformation extends DefaultFileInfoContainer {
    public static final String FILE_ADDED = "A"; //NOI18N
    public static final String FILE_RESURRECTED = "U"; //NOI18N

    public AddInformation() {
    }
}

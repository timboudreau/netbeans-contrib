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

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

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers;

import java.io.*;

/**
 * It's a base class for all containers that store information being processed
 * during execution of cvs commands.
 * Such info is then transmitted via the event mechanism (CVSEvent) to the
 * appropriate UI classes that use this information to display it to the user.
 *
 * @author  Milos Kleint
 */
public abstract class FileInfoContainer {
    public abstract File getFile();

    public String getToolTipText() {
        File file = getFile();
        if (file != null) {
            return file.getAbsolutePath();
        } else {
            return null;
        }
    }
    
}

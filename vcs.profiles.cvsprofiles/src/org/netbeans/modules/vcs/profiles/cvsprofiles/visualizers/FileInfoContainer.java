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
}

/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced;

import org.openide.filesystems.FileSystem;
import org.openide.filesystems.XMLFileSystem;

/**
 * Exposes additional aspecs of PropfilesFactory that is
 * needed by tests.
 *
 * @author Petr Kuzel
 */
public final class UnitProfilesFactory {

    public static void setRegistry(FileSystem fileSystem) {
        ProfilesFactory.setRegistry(fileSystem);
    }
}

/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.testprofiles;

import org.openide.filesystems.FileSystem;
import org.openide.filesystems.XMLFileSystem;
import org.xml.sax.SAXException;

import java.net.URL;

/**
 * Profile entry point.
 *
 * @author Petr Kuzel
 */
public final class TestProfile {

    private TestProfile() {
    }

    /**
     * Return filesystem containg registration of this profile.
     */
    public static final FileSystem getRegistration() throws SAXException {
        URL location = TestProfile.class.getResource("registration.xml");
        XMLFileSystem registration = new XMLFileSystem(location);
        return registration;
    }
}

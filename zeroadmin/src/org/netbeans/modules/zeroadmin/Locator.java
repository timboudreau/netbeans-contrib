/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.zeroadmin;

/**
 * When we run without the user and home directory the installed
 * file facility is severely limited.
 * @author David Strupl
 */
public class Locator extends org.openide.modules.InstalledFileLocator {
    
    /** Creates a new instance of Locator */
    public Locator() {
    }

    /**
     * There are no files installed on the client.
     */
    public java.io.File locate(String relativePath, String codeNameBase, boolean localized) {
        return null;
    }
}

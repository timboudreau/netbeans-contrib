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
 * Software is Nokia. Portions Copyright 2003 Nokia.
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

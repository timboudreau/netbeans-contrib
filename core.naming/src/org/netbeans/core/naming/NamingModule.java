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
 *
 */
package org.netbeans.core.naming;

import org.openide.modules.ModuleInstall;

/**
 * Manages a module's lifecycle.
 * @author David Strupl
 */
public class NamingModule extends ModuleInstall {

    public void restored() {
        System.setProperty(javax.naming.Context.URL_PKG_PREFIXES, "org.netbeans.core.naming");
    }
}

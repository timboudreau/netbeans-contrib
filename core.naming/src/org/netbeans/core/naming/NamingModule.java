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

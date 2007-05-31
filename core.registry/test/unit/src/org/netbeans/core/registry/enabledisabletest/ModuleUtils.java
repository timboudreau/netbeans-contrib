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

package org.netbeans.core.registry.enabledisabletest;

import java.net.URI;
import org.netbeans.Module;
import org.netbeans.core.startup.ModuleHistory;
import org.netbeans.ModuleManager;
import org.openide.util.Mutex;
import org.openide.util.MutexException;

import java.io.File;


/**
 *
 * @author  Jesse Glick, David Konecny
 */
public class ModuleUtils {

    public static ModuleUtils DEFAULT = new ModuleUtils();

    private ModuleManager mgr;
    private Module bookModule;
    private Module cdModule;

    private ModuleUtils() {
        mgr = org.netbeans.core.startup.Main.getModuleSystem().getManager();
    }

    public void install() throws Exception {
        try {
            mgr.mutex().writeAccess(new Mutex.ExceptionAction() {
                public Object run() throws Exception {
                    File jar1 = new File(URI.create(ModuleUtils.class.getResource("data/bookmodule.jar").toExternalForm()));
                    bookModule = mgr.create(jar1, new ModuleHistory(jar1.getAbsolutePath()), false, false, false);
                    if (!bookModule.getProblems().isEmpty()) throw new IllegalStateException("bookModule is uninstallable: " + bookModule.getProblems());
                    File jar2 = new File(URI.create(ModuleUtils.class.getResource("data/cdmodule.jar").toExternalForm()));
                    cdModule = mgr.create(jar2, new ModuleHistory(jar2.getAbsolutePath()), false, false, false);
                    return null;
                }
            });
        } catch (MutexException me) {
            throw me.getException();
        }
    }
    
    protected void uninstall() throws Exception {
        try {
            mgr.mutex().writeAccess(new Mutex.ExceptionAction() {
                public Object run() throws Exception {
                    if (bookModule.isEnabled()) mgr.disable(bookModule);
                    mgr.delete(bookModule);
                    if (cdModule.isEnabled()) mgr.disable(cdModule);
                    mgr.delete(cdModule);
                    return null;
                }
            });
        } catch (MutexException me) {
            throw me.getException();
        }
        bookModule = null;
        cdModule = null;
        mgr = null;
    }
    
    protected static final int TWIDDLE_ENABLE = 0;
    protected static final int TWIDDLE_DISABLE = 1;
    
    private void twiddle(final Module m, final int action) throws Exception {
        try {
            mgr.mutex().writeAccess(new Mutex.ExceptionAction() {
                public Object run() throws Exception {
                    switch (action) {
                    case TWIDDLE_ENABLE:
                        mgr.enable(m);
                        break;
                    case TWIDDLE_DISABLE:
                        mgr.disable(m);
                        break;
                    default:
                        throw new IllegalArgumentException("bad action: " + action);
                    }
                    return null;
                }
            });
        } catch (MutexException me) {
            throw me.getException();
        }
    }

    public Module getBookModule() {
        return bookModule;
    }
    
    public Module getCDModule() {
        return cdModule;
    }
    
    public void enableBookModule(boolean enable) throws Exception {
        twiddle(bookModule, enable ? TWIDDLE_ENABLE : TWIDDLE_DISABLE);
    }
    
    public void enableCDModule(boolean enable) throws Exception {
        twiddle(cdModule, enable ? TWIDDLE_ENABLE : TWIDDLE_DISABLE);
    }
    
}

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

package org.netbeans.api.convertor;

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
    private Module bookConvertorModule;
    private Module dvdConvertorModule;
    private Module shoppingCartConvertorModule;
    private Module storeConvertorModule;
    
    private ModuleUtils() {
        mgr = org.netbeans.core.startup.Main.getModuleSystem().getManager();
    }

    public void install() throws Exception {
        try {
            mgr.mutex().writeAccess(new Mutex.ExceptionAction() {
                public Object run() throws Exception {
                    File jar1 = new File(ModuleUtils.class.getResource("data/bookconvertor.jar").getPath());
                    bookConvertorModule = mgr.create(jar1, new ModuleHistory(jar1.getAbsolutePath()), false, false, false);
                    File jar2 = new File(ModuleUtils.class.getResource("data/dvdconvertor.jar").getPath());
                    dvdConvertorModule = mgr.create(jar2, new ModuleHistory(jar2.getAbsolutePath()), false, false, false);
                    File jar3 = new File(ModuleUtils.class.getResource("data/shoppingcartconvertor.jar").getPath());
                    shoppingCartConvertorModule = mgr.create(jar3, new ModuleHistory(jar3.getAbsolutePath()), false, false, false);
                    File jar4 = new File(ModuleUtils.class.getResource("data/storeconvertor.jar").getPath());
                    storeConvertorModule = mgr.create(jar4, new ModuleHistory(jar4.getAbsolutePath()), false, false, false);
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
                    if (bookConvertorModule.isEnabled()) mgr.disable(bookConvertorModule);
                    mgr.delete(bookConvertorModule);
                    if (dvdConvertorModule.isEnabled()) mgr.disable(dvdConvertorModule);
                    mgr.delete(dvdConvertorModule);
                    if (shoppingCartConvertorModule.isEnabled()) mgr.disable(shoppingCartConvertorModule);
                    mgr.delete(shoppingCartConvertorModule);
                    if (storeConvertorModule.isEnabled()) mgr.disable(storeConvertorModule);
                    mgr.delete(storeConvertorModule);
                    return null;
                }
            });
        } catch (MutexException me) {
            throw me.getException();
        }
        bookConvertorModule = null;
        dvdConvertorModule = null;
        shoppingCartConvertorModule = null;
        storeConvertorModule = null;
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
    
    public void enableBookModule(boolean enable) throws Exception {
        twiddle(bookConvertorModule, enable ? TWIDDLE_ENABLE : TWIDDLE_DISABLE);
    }
    
    public void enableDVDConvertorModule(boolean enable) throws Exception {
        twiddle(dvdConvertorModule, enable ? TWIDDLE_ENABLE : TWIDDLE_DISABLE);
    }
    
    public void enableShoppingCartConvertorModule(boolean enable) throws Exception {
        twiddle(shoppingCartConvertorModule, enable ? TWIDDLE_ENABLE : TWIDDLE_DISABLE);
    }
    
    public void enableStoreConvertorModule(boolean enable) throws Exception {
        twiddle(storeConvertorModule, enable ? TWIDDLE_ENABLE : TWIDDLE_DISABLE);
    }
    
}

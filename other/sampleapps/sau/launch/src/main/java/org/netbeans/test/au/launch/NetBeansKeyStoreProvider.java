/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.test.au.launch;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.spi.autoupdate.KeyStoreProvider;

/**
 *
 * @author Jiri Rechtacek
 */
public final class NetBeansKeyStoreProvider implements KeyStoreProvider {
    
    public static final String KS_FILE_PATH = "core" + System.getProperty ("file.separator") + "ide.ks";
    private static final String KS_DEFAULT_PASSWORD = "open4all";
    
    public KeyStore getKeyStore() {
        return getKeyStore (getPassword ());
    }
    /** Creates keystore and loads data from file.
    * @param filename - name of the keystore
    * @param password
    */
    private static KeyStore getKeyStore(String password) {
        KeyStore keyStore = null;
        InputStream is = null;
        
        try {

            is = NetBeansKeyStoreProvider.class.getResourceAsStream("sau.ks");

            keyStore = KeyStore.getInstance (KeyStore.getDefaultType ());
            keyStore.load (is, password.toCharArray ());
            
        } catch (Exception ex) {
            Logger.getLogger ("global").log (Level.INFO, ex.getMessage (), ex);
        } finally {
            try {
                if (is != null) is.close ();
            } catch (IOException ex) {
                assert false : ex;
            }
        }

        return keyStore;
    }
    
    private static String getPassword () {
        String password = KS_DEFAULT_PASSWORD;
        //XXX: read password from bundle
        return password;
    }

}

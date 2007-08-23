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

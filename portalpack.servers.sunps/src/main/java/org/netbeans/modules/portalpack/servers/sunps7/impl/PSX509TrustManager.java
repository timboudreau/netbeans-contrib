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

package org.netbeans.modules.portalpack.servers.sunps7.impl;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


/**
 * This class is a special trust manager that trusts the Cacao daemon
 * running on a host that has PS already installed.
 */
public class PSX509TrustManager implements X509TrustManager {
    static final char[] TRUSTSTORE_PASSWORD = "password".toCharArray();
    File truststorePath = null;
    KeyStore truststore = null;

    /*
     * The default X509TrustManager returned by SunX509.  We'll delegate
     * decisions to it, and fall back to the logic in this class if the
     * default X509TrustManager doesn't trust it.
     */
    X509TrustManager sunJSSEX509TrustManager = null;

    PSX509TrustManager(String psConfigDir)
        throws SecurityException, IOException {

        InputStream fis = null;
        
        if (psConfigDir != null) {
            truststorePath = new File(psConfigDir, "truststore");
        }    
        if(truststorePath.exists())     
        {
            try {
                fis = new FileInputStream(truststorePath);
            } catch (FileNotFoundException e) {
                // the truststore file doesn't exist yet
            }
        }else
        {
            fis = this.getClass().getClassLoader().getResourceAsStream("resources/truststore");
        }
        

        try {
            truststore = KeyStore.getInstance("JKS");
            truststore.load(fis, TRUSTSTORE_PASSWORD);
            initializeDefaultX509TrustManager();

            if (sunJSSEX509TrustManager == null) {
                throw new SecurityException("No default X509TrustManager");
            }
        } catch (Exception e) {
            SecurityException se
                = new SecurityException("Unable to create TrustManager");

            se.initCause(e);
            throw se;
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    // Documented in X509TrustManager
    public void checkClientTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {

        // this trust manager is dedicated to server authentication
        throw new CertificateException("Client authentication not supported");
    }

    // Documented in X509TrustManager
    public void checkServerTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {

        try {
            sunJSSEX509TrustManager.checkServerTrusted(chain, authType);
        } catch (CertificateException e) {
            if ((e instanceof CertificateEncodingException)
                || (e instanceof CertificateExpiredException)
                || (e instanceof CertificateNotYetValidException)
                || (e instanceof CertificateParsingException)) {

                throw e;
            }

            // The subject DN should have the form "CN=<hostname>_agent"
            String subjectDN = chain[0].getSubjectDN().getName();

            if (!subjectDN.startsWith("CN=") || !subjectDN.endsWith("_agent")) {
                // Most likely the server isn't a Cacao agent because
                // the subject DN doesn't have the right form.
                throw e;
            }

            String hostName = subjectDN.substring(3, subjectDN.length() - 6);

            // Trust the server cert chain if PS is installed on that
            // host, or in the case of the console, if that host is

            // Add the root CA cert to the truststore.
            addToTruststore(hostName, chain[chain.length - 1]);
        }
    }

    // Documented in X509TrustManager
    public X509Certificate[] getAcceptedIssuers() {
        return sunJSSEX509TrustManager.getAcceptedIssuers();
    }

    private void initializeDefaultX509TrustManager() throws Exception {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(truststore);
        TrustManager[] tms = tmf.getTrustManagers();

        /*
         * Iterate over the returned trustmanagers, look
         * for an instance of X509TrustManager.  If found,
         * use that as our "default" trust manager.
         */
        for (int i = 0; i < tms.length; i++) {
            if (tms[i] instanceof X509TrustManager) {
                sunJSSEX509TrustManager = (X509TrustManager)tms[i];
                return;
            }
        }
    }



    private void addToTruststore(String alias, X509Certificate cert) {
        FileOutputStream fos = null;

        try {
            truststore.setCertificateEntry(alias, cert);
            initializeDefaultX509TrustManager();

            if (truststorePath != null) {
                fos = new FileOutputStream(truststorePath);
                truststore.store(fos, TRUSTSTORE_PASSWORD);
            }
        } catch (Exception e) {
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                }
            }
        }
    }
}


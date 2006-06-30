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
package org.netbeans.modules.spellchecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.StringTokenizer;
import org.netbeans.modules.spellchecker.spi.LocaleQueryImplementation;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;

/**
 *
 * @author Jan Lahoda
 */
public class DefaultLocaleQueryImplementation implements LocaleQueryImplementation {
    
    /** Creates a new instance of DefaultLocaleQueryImplementation */
    public DefaultLocaleQueryImplementation() {
    }

    public Locale findLocale(FileObject file) {
        return getDefaultLocale();
    }
    
    private static final String FILE_NAME = "spellchecker-default-locale";
    
    private static FileObject getDefaultLocaleFile() {
        return Repository.getDefault().getDefaultFileSystem().findResource(FILE_NAME);
    }
    
    public static Locale getDefaultLocale() {
        FileObject file = getDefaultLocaleFile();
        
        if (file == null) {
            setDefaultLocale(Locale.getDefault());
            file = getDefaultLocaleFile();
            
            assert file != null;
        }
        
        Charset UTF8 = Charset.forName("UTF-8");
        
        BufferedReader r = null;
        
        try {
            r = new BufferedReader(new InputStreamReader(file.getInputStream(), UTF8));
            
            String localeLine = r.readLine();
            
            if (localeLine == null)
                return null;
            
            String language = "";
            String country = "";
            String variant = "";
            
            StringTokenizer stok = new StringTokenizer(localeLine, "_");
            
            language = stok.nextToken();
            
            if (stok.hasMoreTokens()) {
                country = stok.nextToken();
                
                if (stok.hasMoreTokens())
                    variant = stok.nextToken();
            }
            
            return new Locale(language, country, variant);
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        } finally {
            try {
                if (r != null)
                    r.close();
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            }
        }
        
        return null;
    }
    
    public static void setDefaultLocale(Locale locale) {
        FileObject file = getDefaultLocaleFile();
        Charset UTF8 = Charset.forName("UTF-8");
        FileLock lock = null;
        PrintWriter pw = null;
        
        try {
            if (file == null) {
                file = Repository.getDefault().getDefaultFileSystem().getRoot().createData(FILE_NAME);
            }
            
            lock = file.lock();
            pw = new PrintWriter(new OutputStreamWriter(file.getOutputStream(lock), UTF8));
            
            pw.println(locale.toString());
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        } finally {
            if (pw != null)
                pw.close();
            
            if (lock != null) {
                lock.releaseLock();
            }
        }
    }
}

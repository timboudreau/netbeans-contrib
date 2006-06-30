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

package org.netbeans.modules.apisupport.jackpotrules;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import junit.framework.Assert;

import org.netbeans.jackpot.engine.*;
import org.netbeans.jackpot.transform.Transformer;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;

/**
 *
 * @author Jaroslav Tulach
 */
public final class JackpotUtils {
    
    /** Creates a new instance of JackpotUtils */
    private JackpotUtils() {
    }

    public static void apply(File dir, URL rules) throws Exception {
        DefaultApplicationContext context = new DefaultApplicationContext();
        JackpotEngine eng = EngineFactory.createEngine(context);

        File rulesFile = extractResource(rules);

        int errors = eng.initialize(dir.getPath(), System.getProperty("java.class.path"), "1.4");
        Assert.assertEquals("No errors during compilation", 0, errors);
        eng.runScript("q", "t", rulesFile.getPath());

        Assert.assertTrue("There is something to commit", eng.needsCommit());
        Assert.assertTrue("commit ok", eng.commit());
    }

    final static String readFile (java.io.File f, boolean gzip) throws java.io.IOException {
        if (!gzip) {
            int s = (int)f.length ();
            byte[] data = new byte[s];
            Assert.assertEquals ("Read all data", s, new FileInputStream (f).read (data));

            return new String (data);
        } else {
            GZIPInputStream is = new GZIPInputStream(new FileInputStream(f));
            byte[] arr = new byte[256 * 256];
            int first = 0;
            for(;;) {
                int len = is.read(arr, first, arr.length - first);
                if (first + len < arr.length) {
                    return new String(arr, 0, first + len);
                }
            }
        }
    }
    
    final static File extractString(String res) throws Exception {
        File f = File.createTempFile("res", ".xml");
        f.deleteOnExit ();
        return extractString(f, res);
    }

    final static File extractString (File f, String res) throws Exception {
        FileOutputStream os = new FileOutputStream(f);
        InputStream is = new ByteArrayInputStream(res.getBytes("UTF-8"));
        for (;;) {
            int ch = is.read ();
            if (ch == -1) break;
            os.write (ch);
        }
        os.close ();
            
        return f;
    }
    
    final static File extractResource(String res) throws Exception {
        URL u = JackpotUtils.class.getResource(res);
        Assert.assertNotNull ("Resource should be found " + res, u);
        return extractResource(u);
    }

    final static File extractResource(URL u) throws Exception {
        Assert.assertNotNull ("Resource should be found " + u, u);
        
        File f = File.createTempFile("res", ".xml");
        f.deleteOnExit ();
        
        FileOutputStream os = new FileOutputStream(f);
        InputStream is = u.openStream();
        for (;;) {
            int ch = is.read ();
            if (ch == -1) break;
            os.write (ch);
        }
        os.close ();
            
        return f;
    }

}

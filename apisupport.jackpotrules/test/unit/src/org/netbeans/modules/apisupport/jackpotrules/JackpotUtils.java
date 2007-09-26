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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.apisupport.jackpotrules;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import junit.framework.Assert;
import org.netbeans.api.jackpot.test.TestUtilities;


/**
 *
 * @author Jaroslav Tulach
 */
public final class JackpotUtils {
    
    /** Creates a new instance of JackpotUtils */
    private JackpotUtils() {
    }

    public static void apply(File dir, URL rules) throws Exception {
        TestUtilities.applyRules(dir, rules, false);
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

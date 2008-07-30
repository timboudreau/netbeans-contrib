/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package dtrace;


import java.io.File;
import java.net.URI;
import java.net.URL;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This is a workaround for an issue related to installation of dtrace.jar.
 * This copies the /usr/share/lib/java/dtrace.jar file into the 
 * <cluster_directory>/modules/ext directory.
 * 
 * @author Nasser Nouri <nasser.nouri@sun.com>
 *
 */
public class PostInstall {
    public static void main(String[] args) {
        try {
            File dtraceJar = new File(File.separator + "usr" + File.separator +
                "share" + File.separator + "lib" +
                File.separator + "java" + File.separator +
                "dtrace.jar");

            URL url = PostInstall.class.getResource("PostInstall.class");
            URI uri = url.toURI();
            File currentDir = new File(uri);
            File dtracePackageDir = currentDir.getParentFile();
            File mainDir = dtracePackageDir.getParentFile();
            File updateDir = mainDir.getParentFile();
            File clusterDir = updateDir.getParentFile(); 
            File modulesExt = new File (clusterDir.toString() + File.separator +
                "modules" + File.separator + "ext" + File.separator +
                "dtrace.jar");

            copyFile(dtraceJar, modulesExt);

            File sourceZip = new File (clusterDir.toString() +
                File.separator + "modules" + File.separator + "ext" +
                File.separator + "DTraceScripts.zip");

            String userHome = System.getProperty("user.home");
            File destZip = new File (userHome + File.separator +
                "DTraceScripts.zip");

            copyFile(sourceZip, destZip);

            ZipFile zipFile = new ZipFile(destZip.toString());
            Enumeration entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry)entries.nextElement();

                if (entry.isDirectory()) {
                    (new File(userHome + File.separator + entry.getName())).mkdir();
                    continue;
                }

                copyInputStream(zipFile.getInputStream(entry),
                new BufferedOutputStream(new FileOutputStream(userHome + 
                        File.separator + entry.getName())));
            }
            zipFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        Process p = null;
        String command = "/bin/chmod -R 755 DTraceScripts";
        try {
            File userHomeDirFile = new File(System.getProperty("user.home"));
            p = Runtime.getRuntime().exec(command, null, userHomeDirFile);
        } catch(IOException ex) {
            ex.printStackTrace();
        }

        int retCode = 0;
        try {
            retCode = p.waitFor();                 
        } catch (InterruptedException ex) {
            // We've interupted the process. Kill it and wait for the process to finish.
            p.destroy();
            while (retCode < 0) {
                try {
                    retCode= p.waitFor();
                } catch (InterruptedException ex1) {
                    ex1.getStackTrace();
                }
            }
        }
    }

    /*
     * This method copies the /usr/share/lib/java/dtrace.jar file into the
     * modules/ext directory.
     */
    private static void copyFile(File from, File to) {
        try {
            BufferedInputStream is = new BufferedInputStream(new FileInputStream(from));
            BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(to));
            byte[] buf = new byte[4096];
            int cnt;
            while ((cnt = is.read(buf, 0, buf.length)) != -1) {
                os.write(buf, 0, cnt);
            }
            is.close();
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyInputStream(InputStream in, OutputStream out)
    throws IOException {
        byte[] buffer = new byte[1024];
        int len;

        while((len = in.read(buffer)) >= 0)
            out.write(buffer, 0, len);

        in.close();
        out.close();
    } 
}

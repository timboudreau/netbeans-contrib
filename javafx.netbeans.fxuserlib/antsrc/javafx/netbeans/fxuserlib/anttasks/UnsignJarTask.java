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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package javafx.netbeans.fxuserlib.anttasks;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 *
 * @author alex
 */
public class UnsignJarTask extends Task {

    /*
    private String src = null;

    public void setSrc(String src) {
        this.src = src;
    }
 */
    private File dest = null;

    public void setDest(File dest) {
        this.dest = dest;
    }
    private List<FileSet> filesets = new LinkedList<FileSet>();

    public void addFileSet(FileSet fs) {
        filesets.add(fs);
    }
    private ArrayList<String> files2jar = null;

    @Override
    public void execute() throws BuildException {
        List<File> files2unsign = new ArrayList<File>();

        Iterator it = filesets.iterator();
        while (it.hasNext()) {
            FileSet fs = (FileSet) it.next();
            File dir = fs.getDir(getProject());
            if (!dir.exists()) {
                continue;
            }
            System.out.println("Processing FileSet: " + fs);
            log("Processing FileSet: " + fs, Project.MSG_VERBOSE);
            DirectoryScanner ds = fs.getDirectoryScanner(getProject());
            File basedir = ds.getBasedir();
            String[] files = ds.getIncludedFiles();
            for (String f : files) {
                File fl = new File(basedir, f);
                files2unsign.add(fl);
            }
        }
        //System.out.println("Files to be packed: "  + files2pack.toString());
        log("Files to be packed: " + files2unsign.toString(), Project.MSG_VERBOSE);

        //Unsign list of JARs
        if (files2unsign.size() > 0) {

            //Unjar files in loop
            for (Iterator<File> iter = files2unsign.iterator(); iter.hasNext();) {
                File f = iter.next();
                System.out.println("Unsign File: " + f);
                log("Unsign file: " + f, Project.MSG_VERBOSE);
                try {
                    //TODO Remove hardcode
                    dest = new File(getProject().getBaseDir().getAbsolutePath()+File.separator+"dist"+File.separator+"tmp");
                    dest.mkdir();
                    unJar(new JarFile(f));
                    String fileName = f.getAbsolutePath();
                    f.delete();
                    jar(new File(fileName));
                    dest.delete();
                } catch (IOException ex) {
                    Logger.getLogger(UnsignJarTask.class.getName()).log(Level.SEVERE, null, ex);
                    throw new BuildException("Not a valid Jar file:" + ex.getMessage());
                }
            }//for

        }//if
    }

    private void unJar(JarFile jarFile) throws IOException {
        final int BUFFER = 2048;

        files2jar = new ArrayList<String>();
        Enumeration e = jarFile.entries();
        JarEntry entry;
        BufferedOutputStream destFile = null;
        FileInputStream fis = null;
        BufferedInputStream is = null;
        while (e.hasMoreElements()) {
            entry = (JarEntry) e.nextElement();
            //Filter for signature files
            if ((entry.getName().indexOf(".SF") != -1) ||
                    (entry.getName().indexOf(".DSA") != -1) ||
                    (entry.getName().indexOf(".RSA") != -1)) {
            } else {
                files2jar.add(entry.getName());
                if (entry.isDirectory()) {
                    new File(this.dest.getAbsolutePath() + File.separator + entry.getName()).mkdir();
                } else {
                    //Some jars has META-INF dir entry below its content
                    if ((entry.getName().indexOf("META-INF")!=-1)&&
                            !new File(dest.getAbsolutePath()+File.separator+"META-INF").exists()) {
                        new File(dest.getAbsolutePath()+File.separator+"META-INF").mkdir();
                    }
                    is = new BufferedInputStream(jarFile.getInputStream(entry));
                    int count;
                    byte data[] = new byte[BUFFER];
                    FileOutputStream fos = new FileOutputStream(this.dest.getAbsolutePath() + File.separator + entry.getName());
                    destFile = new BufferedOutputStream(fos, BUFFER);
                    while ((count = is.read(data, 0, BUFFER)) != -1) {
                        destFile.write(data, 0, count);
                    }
                    destFile.flush();
                    destFile.close();
                    is.close();
                }
            }
        }
    }

    private void jar(File jarFile) throws IOException {
        final int BUFFER = 2048;
        if (files2jar.size() > 0) {
            BufferedInputStream origin = null;
            FileOutputStream destFile = new FileOutputStream(jarFile);
            BufferedOutputStream checksum = new BufferedOutputStream(destFile);
            JarOutputStream out = new JarOutputStream(checksum);
            byte data[] = new byte[BUFFER];
            for (Iterator<String> it = files2jar.iterator(); it.hasNext();) {
                String jarEntry = it.next();
                File f = new File(dest.getAbsolutePath()+File.separator+jarEntry);
                FileInputStream fi;
                if (!f.isDirectory()) {
                    fi = new FileInputStream(f);
                    origin = new BufferedInputStream(fi, BUFFER);
                    JarEntry entry = new JarEntry(jarEntry);
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0,
                            BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }
                    origin.close();
                } else {
                    JarEntry entry = new JarEntry(jarEntry);
                    out.putNextEntry(entry);
                }
                f.delete();

            }
            out.close();
        }
    }

}

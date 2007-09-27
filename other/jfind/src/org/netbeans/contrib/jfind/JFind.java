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

package org.netbeans.contrib.jfind;

import java.io.*;
import java.util.Properties;
import java.util.jar.*;
import java.util.zip.*;
import java.util.StringTokenizer;

/**
 * Locate a specified class from a starting directory/jar file, searching
 * any jar files that are located.  Use the -p flag to specify searching
 * for a package instead of a class, and the -s flag to specify searching for
 * service instances.
 * It is possible to have a list of starting directories/jars. The elements of the list must be separated
 * with spaces, semicolons or colons (semicolons and colons together in one command are not allowed)
 * <h4>Examples</h4>
 * To find which jar provides <code>org.openide.ErrorManager</code> in a
 * NetBeans distribution:
 * <pre>
 *    java -jar jfind.jar org.openide.ErrorManager <i>dist</i>
 *</pre>
 * To find which jar provides the <code>javax.crypto</code> package in the
 * JDK:
 * <pre>
 *    java -jar jfind.jar -p javax.crypto <i>JDK-directory</i>
 * </pre>
 * To find which jars provide service instances for the NetBeans
 * <code>org.openide.awt.StatusLineElementProvider</code> interface in a
 * NetBeans distribution:
 * <pre>
 *    java -jar jfind.jar -s org.openide.awt.StatusLineElementProvider <i>dist</i>
 * </pre>
 *
 * @author Tom Ball
 * @author Michael Raschkowski
 *
 */
public class JFind implements Runnable {
    public enum QueryType {
        CLASS, PACKAGE, SERVICE
    }

    static void usage() {
	fatal("usage:  java -jar jfind.jar [-ps] classname [starting path | jar-name] \n" +
                "\t<space | semicolon | colon> [starting path 2]...");
    }

    private String classname;
    private File root;
    private QueryType type;

    public JFind(String classname, String path, QueryType type) {
        this.type = type;
        switch (type) {
            case CLASS:
                this.classname = classname.replace('.', '/') + ".class";
                break;
            case PACKAGE:
                this.classname = classname.replace('.', '/') + "/";
                break;
            case SERVICE:
                this.classname = "META-INF/services/" + classname;
                break;
        }
	root = new File(path);
	if (!root.exists())
	    fatal(root.getPath() + " doesn't exist");
    }

    public void run() {
	try {
	    find(root);
	} catch (IOException e) {
	    fatal(e.toString());
	}
    }

    private void find (String sfile) throws IOException
    {
      find (new File(sfile));
    }

    private void find(File f) throws IOException {
	if (f.isDirectory()) {
	    File classFile = new File(f, classname);
	    if (classFile.exists()) {
		System.out.print(f.getPath());
                if (type == QueryType.SERVICE) {
                    System.out.println(":");
                    printFile(new FileInputStream(classFile));
                } else
                    System.out.println();
		return;
	    }
	    String[] files = f.list();
	    for (int i = 0; i < files.length; i++)
		find(new File(f, files[i]));
	}
	else {
	    JarFile jarFile = null;
	    try {
		jarFile = new JarFile(f);
		ZipEntry entry = jarFile.getEntry(classname);
		if (entry != null) {
                    String name = getModuleName(jarFile);
                    if (name != null)
                        System.out.print(name + ": ");
		    System.out.print(f.getPath());
                    if (type == QueryType.SERVICE) {
                        System.out.println(":");
                        printFile(jarFile.getInputStream(entry));
                    } else
                        System.out.println();
		    return;
		}
	    } catch (ZipException e) {
		// not a jar file, so skip it
	    } finally {
		if (jarFile != null)
		    jarFile.close();
	    }
	}
    }

    private void printFile(InputStream in) throws IOException {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null)
                System.out.println("\t" + line);
        } finally {
            in.close();
        }
    }

    private static String getModuleName(JarFile jar) {
        try {
            final Manifest manifest = jar.getManifest();
            if (manifest == null) {
                return null;
            }
            Attributes attrs = manifest.getMainAttributes();
            String bundleName = attrs.getValue("OpenIDE-Module-Localizing-Bundle");
            if (bundleName == null)
                // check if old-style name spec was used; returns null if not
                return attrs.getValue("OpenIDE-Module-Name");
            ZipEntry entry = jar.getEntry(bundleName);
            if (entry == null)
                return null; // missing resource bundle or bad manifest specification
            Properties props = new Properties();
            props.load(jar.getInputStream(entry));
            return props.getProperty("OpenIDE-Module-Name");
        } catch (IOException ex) {
            return null;
        }
    }

    static void fatal(String msg) {
	System.err.println(msg);
	System.exit(1);
    }

    public static void main(String[] args) {
	if (args.length == 0)
	    usage();
        int nargs = args.length;
        int iarg = 0;
        boolean pkg = false;
        boolean svc = false;
        while (args[iarg].startsWith("-")) {
            if (args[iarg].equals("-p")) {
                if (svc)
                    usage();
                pkg = true;
                iarg++;
            }
            else if (args[iarg].equals("-s")) {
                if (pkg)
                    usage();
                svc = true;
                iarg++;
            }
            else
                usage();
        }
        QueryType type = pkg ? QueryType.PACKAGE : svc ? QueryType.SERVICE : QueryType.CLASS;
	String classname = args[iarg++];
	if (iarg == nargs)
	    new JFind(classname, ".", type).run();
	else 
            for (int i = iarg; i < nargs; i++) {
               String delim="";
               if (args[i].indexOf(':') >=0) delim = ":";
               if (args[i].indexOf(';') >=0) delim = ";";
               if ("".equals(delim)==false) {
                   StringTokenizer st= new StringTokenizer(args[i],delim);
                   while (st.hasMoreTokens())
                       new JFind(classname, st.nextToken(), type).run();
               }
               else 
                   new JFind(classname, args[i], type).run();
            }
    }
}

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

package org.netbeans.contrib.jfind;

import java.io.*;
import java.util.Properties;
import java.util.jar.*;
import java.util.zip.*;

/**
 * Locate a specified class from a starting directory/jar file, searching
 * any jar files that are located.  Use the -p flag to specify searching
 * for a package instead of a class, and the -s flag to specify searching for
 * service instances.
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
 */
public class JFind implements Runnable {
    enum QueryType {
        CLASS, PACKAGE, SERVICE
    }
    
    static void usage() {
	fatal("usage: java JFind [-ps] classname [starting path]");
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
            Attributes attrs = jar.getManifest().getMainAttributes();
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
	else for (int i = iarg; i < nargs; i++)
	    new JFind(classname, args[i], type).run();
    }
}

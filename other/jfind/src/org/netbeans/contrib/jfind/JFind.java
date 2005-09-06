/*
*                 Sun Public License Notice
*
* The contents of this file are subject to the Sun Public License
* Version 1.0 (the "License"). You may not use this file except in
* compliance with the License. A copy of the License is available at
* http://www.sun.com/
*
* The Original Code is NetBeans. The Initial Developer of the Original
* Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
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
 * for a package instead of a class.
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
 *
 * @author Tom Ball
 */
public class JFind implements Runnable {
    static void usage() {
	fatal("usage: java JFind [-p] classname [starting path]");
    }

    private String classname;
    private File root;

    public JFind(String classname, String path, boolean pkg) {
	this.classname = classname.replace('.', '/') + (pkg ? "/" : ".class");
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
		System.out.println(f.getPath());
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
		    System.out.println(f.getPath());
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
        if (args[iarg].equals("-p")) {
            pkg = true;
            iarg++;
        }
	String classname = args[iarg++];
	if (iarg == nargs)
	    new JFind(classname, ".", pkg).run();
	else for (int i = iarg; i < nargs; i++)
	    new JFind(classname, args[i], pkg).run();
    }
}

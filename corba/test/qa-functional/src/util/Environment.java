/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package util;

import java.io.File;
import java.util.Enumeration;
import org.netbeans.modules.corba.settings.CORBASupportSettings;
import org.netbeans.modules.corba.settings.ORBSettingsBundle;
import org.openide.TopManager;
import org.openide.execution.NbProcessDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.JarFileSystem;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.Repository;

public class Environment {
    
    public static class ORB {
        
        String shortcut;
        String displayName;

        String[] dirs;
        String[] jars;
        String[] jarsdirs;
        
        public ORB (String shortcut, String displayName) {
            this (shortcut, displayName, null, null, null);
        }
        
        public ORB (String shortcut, String displayName, String[] dirs, String[] jars, String[] jarsdirs) {
            this.shortcut = shortcut;
            this.displayName = displayName;
            this.dirs = dirs;
            this.jars = jars;
            this.jarsdirs = jarsdirs;
        }
        
        public String getShortcut () {
            return shortcut;
        }
        
        public void load () {
            if (dirs != null)
                for (int a = 0; a < dirs.length; a ++)
                    mountDir(dirs[a]);
            if (jars != null)
                for (int a = 0; a < jars.length; a ++)
                    mountJar(jars[a]);
            if (jarsdirs != null)
                for (int a = 0; a < jarsdirs.length; a ++)
                    mountJarsDir(jarsdirs[a]);
            css.setOrb(displayName);
        }
        
        public void unload () {
            if (dirs != null)
                for (int a = 0; a < dirs.length; a ++)
                    unmountDir(dirs[a]);
            if (jars != null)
                for (int a = 0; a < jars.length; a ++)
                    unmountJar(jars[a]);
            if (jarsdirs != null)
                for (int a = 0; a < jarsdirs.length; a ++)
                    unmountJarsDir(jarsdirs[a]);
        }
        
        public void setNSBinding () {
            css.getActiveSetting ().setServerBindingFromString (ORBSettingsBundle.SERVER_NS);
            css.getActiveSetting ().setClientBindingFromString (ORBSettingsBundle.CLIENT_NS);
        }

    }
    
    public static class Open1xORB extends ORB {
        
        public Open1xORB () {
            super ("OPEN1X", "OpenORB 1.x (unsupported)", null, null, (System.getProperty ("OPEN1X_DIR") != null) ? new String[] {System.getProperty ("OPEN1X_DIR"), System.getProperty ("netbeans.home") + "/lib/ext"} : new String[] {System.getProperty ("netbeans.home") + "/lib/ext"});
        }

        public void load () {
            super.load ();
            NbProcessDescriptor pd = css.getActiveSetting().getIdl();
            String args = pd.getArguments();
            int i = args.indexOf("{classpath}");
            if (i >= 0) {
                args = args.substring(0, i) + "{filesystems}" + args.substring(i + 11);
//                css.getActiveSetting().setIdl(new NbProcessDescriptor(System.getProperty ("java.home") + ((winOS) ? "/bin/java.exe" : "/bin/java"), args, pd.getInfo()));
                css.getActiveSetting().setIdl(new NbProcessDescriptor(pd.getProcessName(), args, pd.getInfo()));
            }
        }
        
    }
    
    public static final boolean winOS = System.getProperty("os.name").startsWith("Win");
    public static final CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject(CORBASupportSettings.class, true);
    public static final ORB[] orbs;
    
    static {
        orbs = new ORB[14];
        orbs[0] = new ORB ("J2EE", "J2EE ORB");
        orbs[1] = new ORB ("JDK13", "JDK 1.3 ORB");
        orbs[2] = new ORB ("JDK14", "JDK 1.4 ORB");
        orbs[3] = new ORB ("OB4X", winOS ? "ORBacus for Java 4.x for Windows" : "ORBacus for Java 4.x");
        orbs[4] = new ORB ("OW2000", "Orbix 2000 1.x for Java");
        orbs[5] = new ORB ("OW32", "OrbixWeb 3.2");
        orbs[6] = new ORB ("VB34", "VisiBroker 3.4 for Java");
        orbs[7] = new ORB ("VB4X", "VisiBroker 4.x for Java");
        orbs[8] = new ORB ("E1X", "eORB 1.x (unsupported)");
        orbs[9] = new ORB ("JAC13", "JacORB 1.3.x (unsupported)");
        orbs[10] = new ORB ("JAVA22", "JavaORB 2.2.x (unsupported)");
        orbs[11] = new ORB ("JDK12", "JDK 1.2 ORB (unsupported)");
        orbs[12] = new Open1xORB ();
        orbs[13] = new ORB ("OB3X", winOS ? "ORBacus for Java 3.x for Windows" : "ORBacus for Java 3.x");
    }
    
    public static void unloadAllORBEnvironment () {
        for (int a = 0; a < orbs.length; a ++)
            orbs[a].unload ();
    }
    public static ORB findORBByDisplayName (String shortcut) {
        for (int a = 0; a < orbs.length; a ++)
            if (orbs[a].getShortcut ().equals (shortcut))
                return orbs[a];
        return null;
    }
    
    public static ORB loadORBEnvironment (String shortcut) {
        unloadAllORBEnvironment ();
        ORB o = findORBByDisplayName (shortcut);
        o.load ();
        return o;
    }
    
    public static String mountDir(String name) {
        try {
            if (TopManager.getDefault().getRepository().findFileSystem(name) != null)
                return null;
            LocalFileSystem lfs = new LocalFileSystem();
            lfs.setRootDirectory(new File(name));
            TopManager.getDefault().getRepository().addFileSystem(lfs);
            return lfs.getDisplayName ();
        } catch (Exception e) {
            return null;
        }
    }
    
    public static void mountJar(String name) {
        try {
            if (TopManager.getDefault().getRepository().findFileSystem(name) != null)
                return;
            JarFileSystem jfs = new JarFileSystem();
            jfs.setJarFile(new File(name));
            TopManager.getDefault().getRepository().addFileSystem(jfs);
        } catch (Exception e) {
        }
    }
    
    public static void mountJarsDir(String name) {
        File[] jars = new File(name).listFiles();
        if (jars == null)
            return;
        for (int a = 0; a < jars.length; a ++) {
            String path = jars[a].getAbsolutePath ();
            if (path.endsWith(".jar")  ||  path.endsWith(".zip"))
                mountJar(path);
        }
    }
    
    public static void unmountDir(String name) {
        name = removeEndingSeparator (name);
        Enumeration e = TopManager.getDefault().getRepository().getFileSystems();
        while (e.hasMoreElements()) {
            FileSystem fs = (FileSystem) e.nextElement();
            if (compareFileSystemNames (fs.getSystemName(), name))
                TopManager.getDefault().getRepository().removeFileSystem (fs);
        }
    }
    
    public static void unmountJar(String name) {
        try {
            name = new File (name).getCanonicalPath();
        } catch (Exception e) {
            return;
        }
        Enumeration e = TopManager.getDefault().getRepository().getFileSystems();
        while (e.hasMoreElements()) {
            FileSystem fs = (FileSystem) e.nextElement();
            if (fs instanceof JarFileSystem) {
                JarFileSystem jfs = (JarFileSystem) fs;
                if (compareFileSystemNames (jfs.getJarFile().getAbsolutePath(), name))
                    TopManager.getDefault().getRepository().removeFileSystem (jfs);
            }
        }
    }
    
    public static void unmountJarsDir(String name) {
        File[] jars = new File(name).listFiles();
        if (jars == null)
            return;
        for (int a = 0; a < jars.length; a ++) {
            String path = jars[a].getAbsolutePath ();
            if (path.endsWith(".jar")  ||  path.endsWith(".zip"))
                unmountJar(path);
        }
    }
    
    public static String removeEndingSeparator (String path) {
        if (path.indexOf(':') >= 0  &&  path.length () <= 3)
            return path;
        if (path != null  &&  path.endsWith(File.separator))
            path = path.substring (0, path.length () - File.separator.length());
        else if (path != null  &&  path.endsWith("/"))
            path = path.substring (0, path.length () - 1);
        return path;
    }
    
    public static String replaceWinSeparator (String path) {
        return path.replace ('\\', '/');
    }
    
    public static String lowerFirstLetter (String path) {
        return (winOS) ? path.toLowerCase() : path;
    }
    
    public static String normalizeFileSystemName (String path) {
        return lowerFirstLetter (replaceWinSeparator (removeEndingSeparator (path)));
    }
    
    public static boolean compareFileSystemNames (String name1, String name2) {
        name1 = normalizeFileSystemName (name1);
        name2 = normalizeFileSystemName (name2);
        return name1.compareTo(name2) == 0;
    }
    
    public static FileObject findFileObject (String path) {
        return Repository.getDefault().findResource(path);
    }

}

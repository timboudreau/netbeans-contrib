/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import java.util.StringTokenizer;
import java.util.Vector;

import org.openide.compiler.ExternalCompilerGroup;
import org.openide.compiler.ExternalCompiler;
import org.openide.compiler.Compiler;
import org.openide.compiler.CompilerJob;

import org.openide.cookies.CompilerCookie;
import org.openide.execution.NbProcessDescriptor;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.EnvironmentNotSupportedException;

import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

import org.openide.TopManager;


import org.netbeans.modules.corba.settings.*;

import org.netbeans.modules.java.JavaDataObject;
import org.netbeans.modules.java.JavaCompilerType;
import org.netbeans.modules.java.settings.JavaSettings;
import org.netbeans.modules.java.JavaExternalCompilerType;
/** External Compiler Group
  * 
  *
  * @author Karel Gardas
  */
public class IDLExternalCompilerGroup extends ExternalCompilerGroup {

    /** Allows subclasses to provide their own format for parsing
     * the arguments of NbProcessDescriptor contained in the
     * ExternalCompiler.
     * <P>
     * This implementation creates new format Format with settings
     * from NbClassPath.createXXXX and executes them in the provided
     * process descriptor.
     *
     * @param desc description of program to start
     * @param files the argument to compiler list of files to compile (or reference
     *   to the file with @files)
     * @return format to use for changing the command line of the compiler
     * @exception IOException if exec fails
     */

    //public static final boolean DEBUG = true;
    public static final boolean DEBUG = false;


    protected Vector _files;
    protected Vector _file_objects;

    public IDLExternalCompilerGroup () {
        super ();
        if (DEBUG)
            System.out.println ("IDLExternalCompilerGroup ()");
        _files = new Vector ();
        _file_objects = new Vector ();
    }

    public void add (Compiler comp) {
        if (DEBUG)
            System.out.print ("add (" + comp + ");");
        super.add (comp);
        IDLExternalCompiler ec = (IDLExternalCompiler)comp;
        if (ec.getIDLFileObject () != null) {
            _file_objects.add (ec.getIDLFileObject ());
        }
    }

    protected Process createProcess (NbProcessDescriptor desc, String[] files)
    throws IOException {
        Thread.dumpStack ();
        FileObject fo = null;
        if (DEBUG) {
            System.out.println ("IDLExternalCompilerGroup::createProcess (" + desc + ", "
                                + files + ");");
        }
        if (DEBUG) {
            System.out.println("IDLExternalCompilerGroup:");
            System.out.print ("files(" + files.length + "): ");
            System.out.flush ();
        }
        //_files.add (type);
        for (int i=0; i<files.length; i++) {
            //_files.add (files[i]);
            if (DEBUG) {
                System.out.print (files[i] + ", ");
                System.out.flush ();
            }
        }

        //fo = findFileObject (files[0]);
        fo = (FileObject)_file_objects.elementAt (0);
        return desc.exec (new IDLFormat (files, fo));
        //throw new IOException("internal error");
    }

    /* Starts compilation. It should check which files realy needs to be
     * compiled and compile only those which really need to.
     * <P>
     * The compilation should fire info to status listeners and report
     * all errors to error listeners.
     *
     * @return true if successful, false otherwise
     */

    public boolean start () {
        boolean result = super.start ();
        if (DEBUG)
            System.out.println ("end of compilation");

        return result;
    }


    /**
     */
    public static class IDLFormat extends Format {

        static final long serialVersionUID =1779771962982570995L;

        public static final boolean DEBUG=true;

        //public PrintWriter out;

        public static final String TAG_RTCLASSPATH = "rtclasspath";
        public static final String TAG_PACKAGEROOT = "package_root";
        public static final String TAG_PARAMS = "params";
        public static final String TAG_PACKAGE_PARAM = "package_param";
        public static final String TAG_OUTPUTDIR_PARAM = "dir_param";
        public static final String TAG_PACKAGE = "package";


        private CORBASupportSettings css;

        public IDLFormat (String[] files, FileObject fo) {
            super (files);
            //try {
            //out = new PrintWriter (new FileOutputStream ("IDLFormat.debug-messages"));
            //} catch (Exception ex) {
            //ex.printStackTrace ();
            //}
            boolean is_in_root = false;
            if (fo.getParent ().isRoot ()) {
                System.out.println ("idl is in root of repository!!!");
                is_in_root = true;
            }

            css = (CORBASupportSettings) CORBASupportSettings.findObject
                  (CORBASupportSettings.class, true);
            String params = " ";
            if (css.getParams () != null)
                params += css.getParams ();
            if (css.isTie ())
                params += css.getTieParam ();

            java.util.Map map = getMap ();

            map.put (TAG_RTCLASSPATH, getRTClasspath ());
            map.put (TAG_PACKAGEROOT, getPackageRoot (fo));
            map.put (TAG_OUTPUTDIR_PARAM, css.getDirParam ());

            // workaround for compilation of file which is in root of repository
            if (is_in_root) {
                map.put (TAG_PACKAGE_PARAM, "");
                map.put (TAG_PACKAGE, "");
            }
            else {
                map.put (TAG_PACKAGE_PARAM, css.getPackageParam ());
                map.put (TAG_PACKAGE, getPackage (fo));
            }
            map.put (TAG_PARAMS, params);
            //map.put (TAG_FILES, getFile (fo));

            //FileSeparator

            /*
            String file = (String)map.get (TAG_FILES);
            String new_file = "";
            StringTokenizer st = new StringTokenizer (file, ".");
            while (st.hasMoreTokens ()) {
            //System.out.println (st.nextToken ());
            new_file += st.nextToken ();
        }
            */
            if (DEBUG) {
                System.out.println ("files: " + files);
                System.out.println ("map: " + map);
                System.out.println ("file: " + getFile (fo));
            }
            //throw new RuntimeException ("map:" + map);
        }

    } // class IDLFormat

    public static String getFile (FileObject fo) {
        if (DEBUG)
            System.out.println ("fo: " + fo.getName ());
        return fo.getName ();
    }

    public static String getRTClasspath() {
        String fileSeparator = System.getProperty("file.separator");
        String javaRuntimeRoot = System.getProperty("java.home") + fileSeparator;
        String javaRoot = javaRuntimeRoot + ".." + fileSeparator;
        return javaRuntimeRoot + "lib" + fileSeparator + "rt.jar";
    }

    public static String getPackage (FileObject fo) {
        CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
                                   (CORBASupportSettings.class, true);

        return fo.getParent ().getPackageName (css.delim ());
    }

    public static String getPackageRoot(FileObject fo) throws IllegalArgumentException {
        final StringBuffer pr = new StringBuffer(64);

        try {
            fo.getFileSystem().prepareEnvironment(new FileSystem.Environment() {
                                                      public void addClassPath(String element) {
                                                          pr.append(element);
                                                      }
                                                  });
        } catch (FileStateInvalidException ex) {
            throw new IllegalArgumentException();
        } catch (EnvironmentNotSupportedException ex) {
            // use current directory
            return ".";
        }
        // root must be directory ! test if it is not a jar file
        String root = pr.toString();
        File fr = new File(root);
        try {
            if (fr.isDirectory()) return root;
        } catch (Exception ex) {
        }
        return ".";

        /*
          if (DEBUG) {
          if (fo == null) 
          System.out.println ("fo is NULL!");
          System.out.println ("fo: " + fo.getName ());
          System.out.println ("package: " +  fo.getPackageName ('/') + " ");
          }
          return fo.getPackageName ('/') + " ";
        */
    }


}





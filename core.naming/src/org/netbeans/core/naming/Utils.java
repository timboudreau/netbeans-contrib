/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.core.naming;


import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.naming.Context;
import javax.naming.CompositeName;
import javax.naming.LinkRef;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import org.openide.ErrorManager;

import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;


/**
 *
 * @author Peter Zavadsky
 */
class Utils {
    
    /** Creates a new instance of Utils */
    private Utils() {
    }
    
    /** Finds file for name relatively to specified folder. */
    public static FileObject findFileForName(FileObject folder, Name name,
    boolean followLinks) throws NamingException {
        FileObject f = folder;
        Enumeration en = name.getAll ();
        while (en.hasMoreElements ()) {
            String n = (String)en.nextElement();
            FileObject next = null;
            
            if (".".equals (n)) {
                next = f;
            } else if ("..".equals (n)) {
                // XXX: check if not above root
                next = f.getParent ();
            } else {
                next = f.getFileObject(n);
            }
            
            if (next != null) {
                if (!en.hasMoreElements() && !next.isFolder()) {
                    throw new javax.naming.NotContextException (name.toString()); // NOI18N
                }
                // next folder
                f = next;
            } else {
                if (en.hasMoreElements()) {
                    // looking for folder, but not found
                    throw new javax.naming.NameNotFoundException (name.toString()); // NOI18N
                }
                
                f = findInstanceFile (f, n);
                if (f == null) {
                    throw new javax.naming.NameNotFoundException (name.toString ()); // NOI18N
                }
            }
            
        }
        
        return f;
    }


    /** Gets the object for file.
     * @throws NameNotFoundException When there is a problem to create instance.
     * @return context for folder or instance for "instance" file */
    public static Object getObjectForFile(Hashtable env, FileObject f,
    boolean followLinks) throws NamingException {
        if (f.isFolder ()) {
            return DOContext.find (env, f);
        } else {
            DataObject obj;
            try {
                obj = DataObject.find (f);
            } catch (DataObjectNotFoundException ex) {
                
                // #29576 - exception from underlaying datasystems layer
                // must be logged. This method is used from implementation of 
                // Context.lookup and its clients often ignore the Naming exception and interpret
                // it as "binding does not exist". But nobody can see the real cause.
                ErrorManager.getDefault().notify(ex);
                
                NamingException nex = new javax.naming.NameNotFoundException(ex.getMessage());
                nex.setRootCause(ex);
                throw nex;
            }
            return instanceCreate (obj, env, followLinks);
        }
    }
    
    public static Object instanceCreate (DataObject obj, Hashtable env,
    boolean followLinks) throws NamingException {
        InstanceCookie ic = (InstanceCookie)obj.getCookie (InstanceCookie.class);
        if (ic != null) {
            Context ctx = DOContext.find (env, obj.getPrimaryFile ().getParent ());
            Object o;
            try {
                o = ic.instanceCreate();
                o = NamingManager.getObjectInstance (o, new CompositeName (obj.getName ()), ctx, env);
            } catch (Exception ex) {
                
                // #29576 - exception from underlaying datasystems layer
                // must be logged. This method is used from implementation of 
                // Context.lookup and its clients often ignore the Naming exception and interpret
                // it as "binding does not exist". But nobody can see the real cause.
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
                
                NamingException nex = new javax.naming.NameNotFoundException(ex.getMessage());
                nex.setRootCause(ex);
                throw nex;
            }
            if ((o instanceof LinkRef) && followLinks) {
                o = ctx.lookup (((LinkRef) o).getLinkName ());
            }
            return o;
        }
        
        return null;
    }

    /** Which extensions are checked to contain extension of correct name.
     * @param f folder in which to search
     * @param n name of file (without extension) to seach for
     * @return file object or null if not found
     */
    public static FileObject findInstanceFile (FileObject f, String n) {
        for(Iterator it = getInstanceExtensions().iterator(); it.hasNext(); ) {
            String ext = (String)it.next();
            
            FileObject next = f.getFileObject(n, ext);
            if(next != null) {
                return next;
            }
            
            // XXX #27494 Settings files could be escaped.
            if("settings".equals(ext)) { // NOI18N
                String escaped = escapeAndCut(n);
                next = f.getFileObject(escaped, ext);
                if(next != null) {
                    return next;
                }
            }
        }

        return null;
//        FileObject next = f.getFileObject (n, "instance"); // NOI18N
//        if (next == null) {
//            next = f.getFileObject (n, "xml"); // NOI18N
//        } 
//
//        if (next == null) {
//            next = f.getFileObject (n, "settings"); // NOI18N
//        }
//        
//        if (next == null) {
//            next = f.getFileObject (n, "shadow" ); // NOI18N
//        }
//        
//        return next;
    }

    // XXX PENDING how to retrieve the instance extensions.
    /** */
    public static List getInstanceExtensions() {
        return Arrays.asList(new String[] {
                    "instance", "xml", "settings", "shadow"}); // NOI18N
    }

    public static boolean isInstanceFile(FileObject fo) {
        if(fo.isFolder()) {
            return true;
        }
        
        return getInstanceExtensions().contains(fo.getExt());
    }

    // XXX #27494
    private static final char OPEN = '[';
    private static final char CLOSE = ']';
    private static final int MAX_FILENAME_LENGTH = 50;
    // XXX #27494 Copied from InstanceDataObject.
    private static String escapeAndCut (String name) {
        int maxLen = MAX_FILENAME_LENGTH;
        
        String ename = escape(name);
        if (ename.length() <= maxLen)  return ename;
        String hash = Integer.toHexString(ename.hashCode());
        maxLen = (maxLen > hash.length()) ? (maxLen-hash.length()) / 2 :1;
        String start = ename.substring(0, maxLen);        
        String end = ename.substring(ename.length() - maxLen);                    

        return start + hash + end;
    }
    
    // XXX #27494
    private static String escape (String text) {
        boolean spacenasty = text.startsWith(" ") || text.endsWith(" ") || text.indexOf("  ") != -1; // NOI18N
        int len = text.length ();
        StringBuffer escaped = new StringBuffer (len);
        for (int i = 0; i < len; i++) {
            char c = text.charAt (i);
            // For some reason Windoze throws IOException if angle brackets in filename...
            if (c == '/' || c == ':' || c == '\\' || c == OPEN || c == CLOSE || c == '<' || c == '>' ||
                    // ...and also for some other chars (#16479):
                    c == '?' || c == '*' || c == '|' ||
                    (c == ' ' && spacenasty) ||
                    c == '.' || c == '"' || c < '\u0020' || c > '\u007E' || c == '#') {
                // Hex escape.
                escaped.append ('#');
                String hex = Integer.toString (c, 16).toUpperCase ();
                if (hex.length () < 4) escaped.append ('0');
                if (hex.length () < 3) escaped.append ('0');
                if (hex.length () < 2) escaped.append ('0');
                escaped.append (hex);
            } else {
                escaped.append (c);
            }
        }
        return escaped.toString ();
    }
    
}

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

import java.lang.reflect.*;
import java.util.*;

import javax.naming.*;


import org.openide.cookies.InstanceCookie;
import org.openide.loaders.DataObject;
import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.util.enum.AlterEnumeration;
import org.openide.util.enum.ArrayEnumeration;
import org.openide.loaders.DataFolder;
import org.openide.ErrorManager;
import org.openide.util.enum.EmptyEnumeration;
import org.openide.util.enum.SingletonEnumeration;

/** Enumeration of bindings.
 *
 * @author  Jaroslav Tulach
 */
final class DOEnum extends AlterEnumeration implements NamingEnumeration {
    /** empty NamingEnumeration */
    public static final NamingEnumeration EMPTY = new DOEnum (null, EmptyEnumeration.EMPTY);
    
    private Hashtable env;
    
    public DOEnum (Hashtable env, Enumeration en) {
        super (en);
        this.env = env;
    }
    
    /** Converts data objects to bindings.
     */
    protected Object alter (Object o) {
        if (o instanceof DataObject) {
            return new B (env, (DataObject)o);
        } else {
            return o;
        }
    }
    
    /** Creates an enumeration for DOContext.
     * @return enumeration of Bindings
     */
    public static NamingEnumeration create (Object c) throws NamingException {
        if (c instanceof DOContext) {
            return createDO ((DOContext)c);
        } else {
            Binding b = new Binding ("", c.getClass ().getName (), c);
            return new DOEnum (null, new SingletonEnumeration (b));
        }
    }
    
    /** Creates enumeration for DOContext.
     */
    private static NamingEnumeration createDO (DOContext c) throws NamingException {
        FileObject fo = c.folder;
        FileObject p = fo.getParent();
        DataObject obj = null;
        
        if (p != null) {
            p = Utils.findInstanceFile (p, fo.getName ());

            try {
                if (p != null) {
                    obj = DataObject.find (p);
                }
            } catch (IOException ex) {
                ErrorManager.getDefault ().notify (ex);
            }
            
        }
        
        Enumeration en;
        
        if (obj == null) {
            en = new ArrayEnumeration (DataFolder.findFolder (fo).getChildren ());
        } else {
            DataObject[] arr = DataFolder.findFolder (fo).getChildren ();
            ArrayList l = new ArrayList (arr.length + 1);
            l.add (obj);
            l.addAll (Arrays.asList (arr));
            en = Collections.enumeration (l);
        }
        
        return new DOEnum (c.env, en);
        
    }
    
    public Object next() throws javax.naming.NamingException {
        return nextElement ();
    }
    
    public boolean hasMore() throws javax.naming.NamingException {
        return hasMoreElements();
    }
    
    public void close() throws javax.naming.NamingException {
    }
    
    /** Binding for a data object.
     */
    private static final class B extends javax.naming.Binding {
        private DataObject obj;
        private Hashtable env;
        
        public B (Hashtable env, DataObject obj) {
            super (obj.getName (), null);
            this.obj = obj;
            this.env = env;
        }
        
        public String getClassName() {
            if (obj instanceof DataFolder) {
                return DOContext.class.getName ();
            } else {
                try {
                    InstanceCookie ic = (InstanceCookie)obj.getCookie (InstanceCookie.class);
                    if (ic != null) {
                        return ic.instanceClass().getName ();
                    }
                } catch (IOException ex) {
                    ErrorManager.getDefault ().notify (ex);
                } catch (ClassNotFoundException ex) {
                    ErrorManager.getDefault ().notify (ex);
                }
                
                return null;
            }
        }
        
        public Object getObject() {
            if (obj instanceof DataFolder) {
                try {
                    return DOContext.find (env, obj.getPrimaryFile ());
                } catch (NamingException ex) {
                    ErrorManager.getDefault ().notify (ex);
                    return null;
                }
            } else {
                try {
                    return Utils.instanceCreate (obj, env, false);
                } catch (NamingException ex) {
                    ErrorManager.getDefault ().notify (ex);
                }
                
                return null;
            }
        }
        
    }
    
}

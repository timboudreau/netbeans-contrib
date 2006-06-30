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

package org.netbeans.modules.rmi.activation;

import java.io.*;
import java.lang.reflect.*;
import java.rmi.MarshalledObject;
import java.security.AllPermission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.util.*;
import org.openide.util.Lookup;


/**
 *
 * @author  mryzl
 * @version
 */
public class MarshalledObjectSupport extends Object {

    /** Creates new MarshalledObjectSupport */
    public MarshalledObjectSupport() {
    }

    /** Creates a MarshalledObject which doesn't contain any nbfs:
     * annotations.
     * @param o - the object
     * @return MarshalledObject with no nbfs: annotation
     */
    public static MarshalledObject create(Object o) throws IOException {
        MarshalledObject obj = new MarshalledObject(o);
        try {
            resetAnnotation(obj, "nbfs:"); // NOI18N
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return obj;
    }

    /** Get annotations of given MarshalledObject.
     * @param mo - the object
     * @return annotations 
     */
    public static String[] getAnnotation(MarshalledObject mo) {
        byte[] b;
        ObjectInputStream ois = null;
        try {
            Field f = mo.getClass().getDeclaredField("locBytes");
            f.setAccessible(true);
            b = (byte[]) f.get(mo);
            if (b != null) {
                ois = new ObjectInputStream(new ByteArrayInputStream(b));
                Object o;
                LinkedList list = new LinkedList();
                while (true) {
                    try {
                        o = ois.readObject();
                        if (o instanceof String) list.add(o);
                        else list.add(o.toString());
                    } catch (EOFException ex) {
                        break;
                    }
                }
                return (String[]) list.toArray(new String[list.size()]);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (ois != null) try { ois.close(); } catch (IOException e) {}
        }
        return new String[0];
    }
    
    
    /** Change the annotation of a MarshalledObject
     * @param mo - the MarshalledObject
     * @param substr - all annotations containing this substring will be
     *                 replaced by null
     */
    public static void resetAnnotation(MarshalledObject mo, String substr) {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        byte[] b;
        try {
            Field f = mo.getClass().getDeclaredField("locBytes"); // NOI18N
            f.setAccessible(true);
            b = (byte[]) f.get(mo);
            if (b == null) {
                return;
            } else {
                ois = new ObjectInputStream(new ByteArrayInputStream(b));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                while (true) {
                    try {
                        Object o = ois.readObject();
                        if (o instanceof String) {
                            if (((String) o).indexOf(substr) != -1) o = null;
                        } 
                        oos.writeObject(o);
                    } catch (EOFException ex) {
                        break;
                    }
                    oos.flush();
                    f.set(mo, baos.toByteArray());
                }
            }
        } catch (Exception ex) {
            // ignore
        } finally {
            if (oos != null) try { oos.close(); } catch (IOException e) {}
        }
    }
    
    /** Replacement of get method which make it possible to use custom class loader
     * for deserialization.
     * @param mo - the Marshalled object
     * @param cl - a class loader used for deserialization
     * @return - deserialized object
     */
    public static Object get(MarshalledObject mo) throws java.io.IOException, 
    java.lang.ClassNotFoundException {
        byte[] b, b2;
        ObjectInputStream ois = null;
        try {
            Field f = mo.getClass().getDeclaredField("locBytes"); // NOI18N
            f.setAccessible(true);
            Field f2 = mo.getClass().getDeclaredField("objBytes"); // NOI18N
            f2.setAccessible(true);
            b = (byte[]) f.get(mo);
            b2 = (byte[]) f2.get(mo);
            if (b2 == null) return null;

            ByteArrayInputStream bin = new ByteArrayInputStream(b2);
            ByteArrayInputStream lin = (b == null) ? null : new ByteArrayInputStream(b);
            MarshalInputStream mis = new MarshalInputStream(bin, lin);
            return mis.readObject();
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } finally {
            if (ois != null) try { ois.close(); } catch (IOException e) {}
        }
        return mo.get();
    }

    /* To avoid calling deprecated code of TopManager.currentClassLoader
    */
    static ClassLoader currentClassLoader () {
        ClassLoader l = (ClassLoader) Lookup.getDefault().lookup(ClassLoader.class);
//        NbClassLoader l = new NbClassLoader();
//        l.setDefaultPermissions(getAllPermissions());
        return l;
    }
    private static PermissionCollection allPermission;
    private static synchronized PermissionCollection getAllPermissions() {
        if (allPermission == null) {
            allPermission = new Permissions();
            allPermission.add(new AllPermission());
        }
        return allPermission;
    }
    
    /** Class that handles deserialization. Its method resolveClass uses
     * TopManager.currentClassLoader to find correct class.
     */
    private static class MarshalInputStream extends ObjectInputStream {

        private ObjectInputStream locIn;

        public MarshalInputStream(InputStream objIn, InputStream locIn) throws IOException {
	    super(objIn);
	    this.locIn = (locIn == null ? null : new ObjectInputStream(locIn));
        }
        /**
         * resolveClass is extended to acquire (if present) the location
         * from which to load the specified class.
         * It will find, load, and return the class.
         */
        protected Class resolveClass(ObjectStreamClass classDesc)
        throws IOException, ClassNotFoundException
        {
            /*
             * Always read annotation written by MarshalOutputStream
             * describing where to load class from.
             */
            Object annotation = readLocation();
            
            /*
             * Unless we were told to skip this step, first try resolving the
             * class using default ObjectInputStream mechanism (using first
             * non-null class loader on the execution stack) to maximize
             * likelihood of type compatibility with calling code.  (This step
             * is skipped during server parameter unmarshalling using the 1.2
             * stub protocol, because there would never be a non-null class
             * loader on the stack in that situation anyway.)
             */
            try {
                return super.resolveClass(classDesc);
            } catch (ClassNotFoundException e) {
            }
            
            String className = classDesc.getName();

            /* Try RMIClassLoader
             */
            if ((annotation != null) && (annotation instanceof String)) {
                return java.rmi.server.RMIClassLoader.loadClass((String) annotation, className);
            }
            
            return currentClassLoader().loadClass(className);
        }
        
        
        /**
	 */
	protected Object readLocation() throws IOException, ClassNotFoundException
	{
	    return (locIn == null ? null : locIn.readObject());
	}
    }
}

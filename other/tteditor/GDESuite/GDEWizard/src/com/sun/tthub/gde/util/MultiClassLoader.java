
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved
 *
 */


package com.sun.tthub.gde.util;

/**
 * A simple java 1.2 style class loader which is capable of loading classes 
 * from multiple sources, such as local files or a URL. Must be subclassed and 
 * the abstract method loadClassBytes() implemented to provide the featuer of
 * loading from the required source (network, database etc). Since it is a 
 * jdk 1.2 style class loader, it delegates the of the Class object to the
 * java runtime system, rather than maintaining hash maps of Class objects within
 * the class loader. The linking of the class (i.e the resolve class invocation) 
 * is also taken care by the parent class loader.
 *
 * @author Hareesh Ravindran
 */
public abstract class MultiClassLoader extends ClassLoader {
    
    private char classNameReplacementChar = '/';

    public MultiClassLoader() {}
    
    public MultiClassLoader(ClassLoader parent) { super(parent); }
    
   /**
    * This is a simple version for external clients since they
    * will always want the class resolved before it is returned
    * to them.
    */
    public Class loadClass(String className) throws ClassNotFoundException {
        return (loadClass(className, true));
    }
    
   /**
    * This is the method where the task of class loading is delegated to the
    * custom loader. This method is invoked by the loadClass() method of the
    * java.lang.ClassLoader. It will take care of checking whether the class
    * is already loaded by the parent, and if the parent class loader is able
    * to load the class.
    *
    * @param  name the name of the class
    *
    * @return the resulting <code>Class</code> object
    *
    * @exception ClassNotFoundException if the class could not be found
    */
    protected Class findClass(String className) throws ClassNotFoundException {
        // use the loadClassBytes method to load the class bytes. This abstract
        // method will be overridden by all subclasses. For example, the Network
        // class loader will override this method to load the class from the
        // network resource. A JarClassLoader will override this to load the
        // class from a Jar File. This function returns null, if it is not able
        // to load the class. If the function returns null, throw a new 
        // ClassNotFoundException.
        byte[] classBytes = loadClassBytes(className);        
        if (classBytes == null) { 
            throw new ClassNotFoundException("Failed to load the class '" +
                    className + "' from the resource."); 
        }
        // If the loadClassBytes function returns a byte array, give the byte
        // array to the defineClass method of the java.lang.ClassLoader to
        // create a Class object from the btye array. The defineClass method
        // throws a ClassFormatError() if the byte array returned is not 
        // according to the java class file specification.
        Class loadedClass = defineClass(null, classBytes, 0, classBytes.length);
        return loadedClass;
        
    }
    
    /**
     * Every subclass of this class should override the loadClassBytes function
     * to load the class and return the array of bytes. The NetworkClassLoader
     * will load it from the network resource and the JarFileClassLoader will
     * load it from a jar file. The function returns null, if it fails to load
     * the class. This may happen due to several reasons like network failure,
     * trying to load an undefined class etc.
     */
    protected abstract byte[] loadClassBytes(String className);

    /**
     * This optional call allows a class name such as COM.test.Hello to be 
     * changed to COM_test_Hello, which is useful for storing classMap from 
     * different packages in the same retrival directory.
     */        
    public void setClassNameReplacementChar(char replacement) 
            { classNameReplacementChar = replacement; }    
    
    protected String formatClassName(String className) {
        if (classNameReplacementChar == '/') {                        
            // '/' is used to map the package to the path
            return className.replace('.', '/') + ".class";
        } else {
            // Replace '.' with custom char, such as '_'
            return className.replace('.', 
                                classNameReplacementChar) + ".class";
        }
    }
}

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

package basic.JNDITest;

import java.io.*;
import java.beans.*;

import org.openide.options.*;
import java.util.ArrayList;
import org.openide.util.Lookup;

public class SystemOptionTest {

    private PropertyChangeListener listener;
    
    private PrintStream out = null;

    /** Set output to script output stream. */
    public void setIO(PrintStream io) {
        out = io;
        out.println(toString());
    }

    public String toString() {
         return "SystemOptionTest version 1.0.1\nIT MUST BE LOADED BY MODULE CLASSLOADER\nOTHERWISE REFERENCED MODULE CLASSES COULD NOT BE LOADED!";

    }

    private void println(String s) {
        if (out == null) {
            println("({" + s + "})");
        } else {
            out.println(s);
        }
    }

    /** Get a system option by name.
     * @param className - name of the class
     * @return a system option
     */
    public boolean testSystemOption(String className) {
        
        boolean status = true;
        
        // introduction
        println("========================================");
        println("  systemOption = " + className);
        
        try {
            // get class
            Class clazz = ((ClassLoader) Lookup.getDefault().lookup(ClassLoader.class)).loadClass(className);
            println("  className = " + clazz.getName());

            // get option
            SystemOption so = (SystemOption) SystemOption.findObject(clazz, true);
            
            // serializability
            org.openide.util.io.NbMarshalledObject mo = new org.openide.util.io.NbMarshalledObject(so);
            mo.get();
            println("  serializable = true");
            
            // bean info
            println("bean info:");
            BeanInfo bi = Introspector.getBeanInfo(clazz);
            println("  class = " + bi.getClass().getName());
            
            // bean descriptor
            println("bean descriptor:");
            BeanDescriptor bd = bi.getBeanDescriptor();
            println("  name = " + bd.getName());
            println("  displayName = " + bd.getDisplayName());
            println("  shortDescription = " + bd.getShortDescription());
            println("  hidden = " + bd.isHidden());
            println("  customizer = " + getClassName(bd.getClass()));
            
            // properties
            PropertyDescriptor[] pds = bi.getPropertyDescriptors();
            ArrayList al = new ArrayList ();
            for(int i = 0; i < pds.length; i++)
                al.add (pds[i]);
            while (!al.isEmpty()) {
                PropertyDescriptor now = (PropertyDescriptor) al.get (0);
                for (int i = 1; i < al.size (); i ++)
                    if (now.getName ().compareTo(((PropertyDescriptor) al.get (i)).getName ()) > 0)
                        now = (PropertyDescriptor) al.get (i);
                println("property:");
                println("  name = " + now.getName());
                println("  type = " + getClassName(now.getPropertyType()));
                println("  getter = " + getMethodName(now.getReadMethod()));
                println("  setter = " + getMethodName(now.getWriteMethod()));
                println("  displayName = " + now.getDisplayName());
                println("  shortDescription = " + now.getShortDescription());
                println("  hidden = " + now.isHidden());
                println("  editor = " + getClassName(now.getPropertyEditorClass()));
                al.remove (now);
            }
            
            println("additional bean infos:");
            BeanInfo[] bis = bi.getAdditionalBeanInfo();
            if (bis != null) {
                for(int i = 0; i < bis.length; i++) {
                    println("  class = " + getClassName(bis[i].getClass()));
                }
            }
            
            println("========================================");
            
            if (so instanceof ContextSystemOption) {
                println("context:");
                SystemOption[] sos = ((ContextSystemOption) so).getOptions();
                for(int i = 0; i < sos.length; i++) {
                    testSystemOption(getClassName(sos[i].getClass()));
                }
            }
            
        
        } catch (Exception ex) {
            println("----------------------------------------");
            ex.printStackTrace();
            status = false;
        }
        
        return status;
    }    

    /** Get name of the class. 
     * @param clazz - class
     * @return name of the class or null
     */
    public String getClassName(Class clazz) {
        if (clazz != null) return clazz.getName();
        return null;
    }
    
    /** Get name of the method. 
     * @param clazz - class
     * @return name of the class or null
     */
    public String  getMethodName(java.lang.reflect.Method method) {
        if (method != null) return method.getName();
        return null;
    }
    
    public void attachListener(SystemOption option) {
        if (listener == null) {
            listener = new TestListener(this);
        }
        option.addPropertyChangeListener(listener);
    }
    
    public void detachListener(SystemOption option) {
        if (listener != null) option.removePropertyChangeListener(listener);
    }
    
    
    /** Listener for testing. 
    * !!! take care of async notification.
    */
    static class TestListener implements PropertyChangeListener {
        SystemOptionTest test;
        public TestListener(SystemOptionTest test) {
            this.test = test;
        }

        public void propertyChange(final java.beans.PropertyChangeEvent p1) {
            test.println("propertyChange:");
            test.println("sourceClass = " + p1.getSource().getClass().getName());
            test.println("name = " + p1.getPropertyName());
        }   
    }
    
}
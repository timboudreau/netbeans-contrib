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

package org.netbeans.spi.vcs.commands;

//import java.beans.BeanDescriptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Map;
import javax.swing.event.EventListenerList;

import org.openide.filesystems.FileObject;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

/**
 * The invocation handler for commands.
 * Basically it handles listeners add/remove stuff
 *
 * @author  Martin Entlicher
 */
class CommandInvocationHandler extends Object /*CommandSupport.Info*/ implements InvocationHandler, Cloneable {
    
    //private Command command;
    //private CommandTask commandTask;
    private CommandSupport support;
    private EventListenerList listenerList = new EventListenerList();
    private Map properties = new Hashtable();
    private FileObject[] files = null;
    private boolean guiMode = true;
    private boolean expertMode = false;
    
    /** Creates a new instance of CommandInvocationHandler */
    public CommandInvocationHandler(CommandSupport support) {
        this.support = support;
    }
    
    /**
     * Processes a method invocation on a proxy instance and returns
     * the result.  This method will be invoked on an invocation handler
     * when a method is invoked on a proxy instance that it is
     * associated with.
     *
     * @param	proxy the proxy instance that the method was invoked on
     *
     * @param	method the <code>Method</code> instance corresponding to
     * the interface method invoked on the proxy instance.  The declaring
     * class of the <code>Method</code> object will be the interface that
     * the method was declared in, which may be a superinterface of the
     * proxy interface that the proxy class inherits the method through.
     *
     * @param	args an array of objects containing the values of the
     * arguments passed in the method invocation on the proxy instance,
     * or <code>null</code> if interface method takes no arguments.
     * Arguments of primitive types are wrapped in instances of the
     * appropriate primitive wrapper class, such as
     * <code>java.lang.Integer</code> or <code>java.lang.Boolean</code>.
     *
     * @return	the value to return from the method invocation on the
     * proxy instance.  If the declared return type of the interface
     * method is a primitive type, then the value returned by
     * this method must be an instance of the corresponding primitive
     * wrapper class; otherwise, it must be a type assignable to the
     * declared return type.  If the value returned by this method is
     * <code>null</code> and the interface method's return type is
     * primitive, then a <code>NullPointerException</code> will be
     * thrown by the method invocation on the proxy instance.  If the
     * value returned by this method is otherwise not compatible with
     * the interface method's declared return type as described above,
     * a <code>ClassCastException</code> will be thrown by the method
     * invocation on the proxy instance.
     *
     * @throws	Throwable the exception to throw from the method
     * invocation on the proxy instance.  The exception's type must be
     * assignable either to any of the exception types declared in the
     * <code>throws</code> clause of the interface method or to the
     * unchecked exception types <code>java.lang.RuntimeException</code>
     * or <code>java.lang.Error</code>.  If a checked exception is
     * thrown by this method that is not assignable to any of the
     * exception types declared in the <code>throws</code> clause of
     * the interface method, then an
     * {@link UndeclaredThrowableException} containing the
     * exception that was thrown by this method will be thrown by the
     * method invocation on the proxy instance.
     *
     * @see	UndeclaredThrowableException
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        //System.out.println("CommandInvocationHandler: method = "+name);
        String propName; // The name of the property to get
        //Class propClass; // The class type of the property to get
        boolean get = false;
        boolean addListener = false;
        boolean removeListener = false;
        if (name.startsWith("set") && name.length() > "set".length()) {
            propName = Character.toLowerCase(name.charAt(3)) + name.substring(4);
            get = false;
        } else if ("getApplicableFiles".equals(name)) {
            if (args[0] instanceof FileObject[]) {
                return support.getApplicableFiles((FileObject[]) args[0]);
            } else {
                throw new IllegalArgumentException("getApplicableFiles("+args[0]+"): needs FileObject[] value.");
            }
        } else if (name.startsWith("get") && name.length() > "get".length()) {
            propName = Character.toLowerCase(name.charAt(3)) + name.substring(4);
            get = true;
        } else if (name.startsWith("is") && name.length() > "is".length()) {
            propName = Character.toLowerCase(name.charAt(2)) + name.substring(3);
            get = true;
        } else if (name.startsWith("add") && name.endsWith("Listener") && args.length == 1 && args[0] instanceof EventListener) {
            addListener = true;
            propName = name.substring("add".length(), name.length() - "Listener".length());
        } else if (name.startsWith("remove") && name.endsWith("Listener") && args.length == 1 && args[0] instanceof EventListener) {
            removeListener = true;
            propName = name.substring("remove".length(), name.length() - "Listener".length());
        } else if ("toString".equals(name)) {
            return support.toString();
        } else if ("equals".equals(name)) {
            try {
                return new Boolean((java.lang.reflect.Proxy.getInvocationHandler(args[0]).equals(this)));
            } catch (IllegalArgumentException iaex) {
                return Boolean.FALSE;
            }
        } else if ("hashCode".equals(name)) {
            return new Integer(this.hashCode());
        } else if ("execute".equals(name)) {
            CommandTask task = support.createTheTask();
            if (task == null) return null;
            task.run();
            return task;
        } else if ("run".equals(name)) {
            // Implementing PrivilegedAction - Customizer hack. Return the object,
            // that should be customized instead of me.
            if (support instanceof java.security.PrivilegedAction) {
                return ((java.security.PrivilegedAction) support).run();
            } else {
                return support;
            }
        } else throw new NoSuchMethodError(name);
        if (addListener) {
            listenerList.add(method.getParameterTypes()[0]/*findListenerClass(args[0], propName)*/, (EventListener) args[0]);
            return null;
        } else if (removeListener) {
            listenerList.remove(method.getParameterTypes()[0]/*findListenerClass(args[0], propName)*/, (EventListener) args[0]);
            return null;
        } else if (get) {
            if (args != null) throw new IllegalArgumentException(args.toString());
            if ("name".equals(propName)) {
                return support.getName();
            } else if ("displayName".equals(propName)) {
                return support.getDisplayName();
            //} else if ("beanDescriptor".equals(propName)) {
            //    return createBeanDescriptor();
            } else if ("files".equals(propName)) {
                return files;
            } else if ("gUIMode".equals(propName)) {
                return new Boolean(guiMode);
            } else if ("expertMode".equals(propName)) {
                return new Boolean(expertMode);
            } else if ("commandSupport".equals(propName)) {
                return support;
            } else {
                Object ret = properties.get(propName);
                if (ret == null) {
                    ret = getDefaultPropertyValue(method.getReturnType());
                }
                return ret;
            }
        } else {
            if (args == null || args.length != 1) {
                throw new IllegalArgumentException(name+"("+((args == null) ? "null" : args.toString())+")");
            }
            if ("name".equals(propName)) {
                throw new NoSuchMethodError(name);
            } else if ("displayName".equals(propName)) {
                throw new NoSuchMethodError(name);
            } else if ("gUIMode".equals(propName)) {
                if (args[0] instanceof Boolean) {
                    guiMode = ((Boolean) args[0]).booleanValue();
                } else {
                    throw new IllegalArgumentException("setGUIMode("+args[0]+"): needs boolean value.");
                }
            } else if ("expertMode".equals(propName)) {
                if (args[0] instanceof Boolean) {
                    expertMode = ((Boolean) args[0]).booleanValue();
                } else {
                    throw new IllegalArgumentException("setGUIMode("+args[0]+"): needs boolean value.");
                }
            } else if ("files".equals(propName)) {
                if (args[0] instanceof FileObject[]) {
                    files = support.getApplicableFiles((FileObject[]) args[0]);
                } else {
                    throw new IllegalArgumentException("setFiles("+args[0]+"): needs FileObject[] value.");
                }
            } else {
                if (args[0] == null) {
                    properties.remove(propName);
                } else {
                    properties.put(propName, args[0]);
                }
            }
            return null;
        }
    }
    
    /** Get the default value for the given class type. */
    private static Object getDefaultPropertyValue(Class clazz) {
        if (Boolean.TYPE.equals(clazz)) return Boolean.FALSE;
        if (Byte.TYPE.equals(clazz)) return new Byte((byte) 0);
        if (Character.TYPE.equals(clazz)) return new Character((char) 0);
        if (Short.TYPE.equals(clazz)) return new Short((short) 0);
        if (Integer.TYPE.equals(clazz)) return new Integer(0);
        if (Long.TYPE.equals(clazz)) return new Long(0L);
        if (Float.TYPE.equals(clazz)) return new Float(0);
        if (Double.TYPE.equals(clazz)) return new Double(0);
        return null;
    }
    
    /**
     * Find the class, that represents an event listener implemented by the
     * provided object. If more event listeners are implemented, it looks
     * for interface that match the provided name as a substring.
     * @param obj The object provided to get the listener it implements.
     * @param name The part of the name of the implemented listener that we search for.
     * @return The class representing an event listener implemented by the object
     *         and matched the required name as a substring if there are more
     *         implemented event listener. If the proper listener is not found
     *         the class of the object is returned.
     */
    private Class findListenerClass(Object obj, String name) {
        Class[] interfaces = obj.getClass().getInterfaces();
        if (interfaces.length == 0) {
            return obj.getClass();
        } else if (interfaces.length == 1) {
            return interfaces[0];
        } else {
            ArrayList eventListeners = new ArrayList();
            for (int i = 0; i < interfaces.length; i++) {
                if (interfaces[i].isAssignableFrom(EventListener.class)) {
                    eventListeners.add(interfaces[i]);
                }
            }
            int size = eventListeners.size();
            if (size == 0) {
                return obj.getClass();
            } else if (size == 1) {
                return (Class) eventListeners.get(0);
            } else {
                for (int i = 0; i < size; i++) {
                    Class interf = (Class) eventListeners.get(i);
                    if (interf.getName().indexOf(name) >= 0) {
                        return interf;
                    }
                }
                return obj.getClass();
            }
        }
    }
    
    protected Object clone() {
        CommandInvocationHandler clon = new CommandInvocationHandler(this.support);
        //clon.setCommand(this.command);
        clon.listenerList = new EventListenerList();
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length-2; i >= 0; i -= 2) {
            clon.listenerList.add((Class) listeners[i],
                                  (EventListener) listeners[i+1]);
        }
        clon.properties = new Hashtable(this.properties);
        clon.files = this.files;
        clon.guiMode = this.guiMode;
        clon.expertMode = this.expertMode;
        return clon;
    }
    
    /*
    private void setCommandTask(CommandTask commandTask) {
        this.commandTask = commandTask;
    }
    
    public CommandTask getCommandTask() {
        return commandTask;
    }
     */
    
    public EventListener[] getListeners(Class listenerType) {
        try {
            return listenerList.getListeners(listenerType);
        } catch (ClassCastException ccex) {
            System.err.println("Bad instance of listener: "+listenerType);
            ccex.printStackTrace(System.err);
            return new EventListener[0];
        }
    }
    
    FileObject[] getFiles() {
        return files;
    }
    
    /* private implementation */
    
    /*
    private BeanDescriptor createBeanDescriptor() {
        return new BeanDescriptor(support.getCommandClass(), support.getCustomizerClass());
    }
     */
    
}

/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.commands;

import java.util.*;
import java.beans.*;
import java.lang.reflect.*;

import org.openide.TopManager;
import org.openide.NotifyDescriptor;

/**
 *
 * @author  Martin Entlicher
 */
public class VcsCommandIO extends Object {

    /** Creates new VcsCommandIO */
    public VcsCommandIO() {
    }
    
    /**
     * Get the boolean value of the command property.
     * @return the boolean value of the command property or <code>false</code> when the property does not exist.
     */
    public static boolean getBooleanProperty(VcsCommand command, String propertyName) {
        Object value = command.getProperty(propertyName);
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        } else {
            return false;
        }
    }
    
    /**
     * Get the boolean value of the command property.
     * @return the boolean value of the command property or <code>true</code> when the property does not exist.
     */
    public static boolean getBooleanPropertyAssumeTrue(VcsCommand command, String propertyName) {
        Object value = command.getProperty(propertyName);
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        } else {
            return true;
        }
    }
    
    /**
     * Get the <code>int</code> value of the command property.
     * @return the <code>int</code> value of the command property or <code>0</code> when the property does not exist.
     */
    public static int getIntegerPropertyAssumeZero(VcsCommand command, String propertyName) {
        Object value = command.getProperty(propertyName);
        if (value instanceof Integer) {
            return ((Integer) value).intValue();
        } else {
            return 0;
        }
    }
    
    /**
     * Get the <code>int</code> value of the command property.
     * @return the <code>int</code> value of the command property or <code>-1</code> when the property does not exist.
     */
    public static int getIntegerPropertyAssumeNegative(VcsCommand command, String propertyName) {
        Object value = command.getProperty(propertyName);
        if (value instanceof Integer) {
            return ((Integer) value).intValue();
        } else {
            return -1;
        }
    }
    
    /**
     * Read commands from properies. All properties of the form <code>cmd.Command_Name.Command_Property</code>
     * are stored into commands. All properties of the form <code>cmdl.Command_Name.Command_Property</code>
     * are stored as "empty commands" with only label and orderArr properies. These are the labels for command subsets.
     * @param props the properties to read
     * @param clazz the class type of the command.
     * @return the Vector of commands
     *
    public static Vector readCommands(Properties props, Class clazz) throws InstantiationException, IllegalAccessException {
        Vector result=new Vector(20);

        for(Iterator iter = props.keySet().iterator(); iter.hasNext();){
            String key = (String) iter.next();
            String cmdStr = null;
            if (key.startsWith("cmd.") == true && // NOI18N
                key.endsWith(".label") == true) { // NOI18N
                    cmdStr = "cmd.";
            }
            if (key.startsWith("cmdl.") == true && // NOI18N
                key.endsWith(".label") == true) { // NOI18N
                    cmdStr = "cmdl.";
            }
            if (cmdStr != null) {
                int startIndex = cmdStr.length(); // NOI18N
                int endIndex = key.length() - ".label".length(); // NOI18N

                String name = key.substring(startIndex, endIndex);
                String label = (String) props.get(key);

                Object command = clazz.newInstance();
                if (!(command instanceof VcsCommand)) throw new InstantiationException("Bad class type. Not instance of VcsCommand.");
                VcsCommand vc = (VcsCommand) command;
                vc.setName(name);
                vc.setLabel(label);
                String orderStr = (String) props.get(cmdStr + name + ".order"); // NOI18N
                if (orderStr == null) orderStr = "-1";
                //D.deb("Parsing orderArr ("+uc.getName()+") = "+orderArr);
                int[] orderArr = VcsCommandOrder.parseOrder(orderStr);
                if (orderArr == null) {
                    orderArr = new int[1];
                    orderArr[0] = -1;
                }
                //D.deb("Setting orderArr = "+UserCommand.getorderArrString(orderArrArr));
                vc.setOrder(orderArr);
                if ("cmd.".equals(cmdStr)) {
                    //BeanInfo info = vc.createCommandInfo();
                    fillCommandProperties(name, vc, props);
                }
                result.addElement(vc);
            }
        }
        Object firstCommand = result.firstElement();
        if (firstCommand == null) return result;
        result = VcsCommandOrder.sortCommands(result);
        //D.deb("going to set the orderArr ..."); // NOI18N
        VcsCommandOrder.setOrder(result);
        Method finishMethod = null;
        try {
            finishMethod = clazz.getDeclaredMethod("readFinished", new Class[] { java.util.Vector.class });
        } catch (NoSuchMethodException exc) {
            TopManager.getDefault().notify(new NotifyDescriptor.Exception(exc));
        } catch (SecurityException sexc) {
            TopManager.getDefault().notify(new NotifyDescriptor.Exception(sexc));
        }
        try {
            if (finishMethod != null) finishMethod.invoke(firstCommand, new Object[] { result });
        } catch (IllegalArgumentException exc) {
            TopManager.getDefault().notify(new NotifyDescriptor.Exception(exc));
        } catch (InvocationTargetException itexc) {
            TopManager.getDefault().notify(new NotifyDescriptor.Exception(itexc));
        }
        return result;
    }
    
    /*
    private static void fillCommandInfo(String name, BeanInfo info, Properties props) {
        PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
        for(int j = 0; j < descriptors.length; j++) {
            Enumeration attrs = descriptors[j].attributeNames();
            while(attrs.hasMoreElements()) {
                String attrName = (String) attrs.nextElement();
                String attrValue = (String) props.get("cmd." + name + "." + attrName);
                Class propClazz = descriptors[j].getPropertyType();
                Object value = attrValue;
                if (propClazz.equals(Boolean.TYPE)) {
                    if (attrValue.equalsIgnoreCase("TRUE")) {
                        value = new Boolean(true);
                    } else if (attrValue.equalsIgnoreCase("FALSE")) {
                        value = new Boolean(false);
                    } else {
                        value = null;
                    }
                } else if (propClazz.equals(Integer.TYPE)) {
                    try {
                        int intValue = Integer.parseInt(attrValue);
                        value = new Integer(intValue);
                    } catch (NumberFormatException exc) {
                        TopManager.getDefault().notify(new NotifyDescriptor.Exception(exc));
                        value = null;
                    }
                } else if (propClazz.equals(Vector.class)) {
                    Vector vector = new Vector();
                    StringTokenizer tokenizer = new StringTokenizer(attrValue, ",");
                    while(tokenizer.hasMoreTokens()) {
                        String token = tokenizer.nextToken();
                        if (token.length() > 0 && token.charAt(0) == ']') {
                            token = token.substring(1);
                        }
                        if (token.endsWith("]")) {
                            token = token.substring(0, token.length() - 1);
                        }
                        vector.add(token);
                    }
                    value = vector;
                }
                descriptors[j].setValue(attrName, value);
            }
        }
    }
     */
    /*
    private static void fillCommandProperties(String name, VcsCommand vc, Properties props) {
        String[] propertyNames = vc.getPropertyNames();
        //PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
        for(int j = 0; j < propertyNames.length; j++) {
            String attrName = propertyNames[j];
            String attrValue = (String) props.get("cmd." + name + "." + attrName);
            //Class propClazz = descriptors[j].getPropertyType();
            Object value;
            Object oldValue = vc.getProperty(attrName);
            if (oldValue instanceof Boolean) {
                if (attrValue.equalsIgnoreCase("TRUE")) {
                    value = new Boolean(true);
                } else if (attrValue.equalsIgnoreCase("FALSE")) {
                    value = new Boolean(false);
                } else {
                    value = null;
                }
            } else if (oldValue instanceof Integer) {
                try {
                    int intValue = Integer.parseInt(attrValue);
                    value = new Integer(intValue);
                } catch (NumberFormatException exc) {
                    TopManager.getDefault().notify(new NotifyDescriptor.Exception(exc));
                    value = null;
                }
            } else {
                value = attrValue;
            }
            /*
            } else if (propClazz.equals(Vector.class)) {
                Vector vector = new Vector();
                StringTokenizer tokenizer = new StringTokenizer(attrValue, ",");
                while(tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();
                    if (token.length() > 0 && token.charAt(0) == ']') {
                        token = token.substring(1);
                    }
                    if (token.endsWith("]")) {
                        token = token.substring(0, token.length() - 1);
                    }
                    vector.add(token);
                }
                value = vector;
            }
             *
            vc.setProperty(attrName, value);
        }
    }
     */

    /**
     * Write the commands properties.
     *
    public static void writeConfiguration (Properties props, Vector cmds) {
        for(int i = 0; i < cmds.size(); i++) {
            VcsCommand vc = (VcsCommand) cmds.get (i);
            String name = vc.getName();
            //BeanInfo info = vc.getCommandInfo();
            String[] properties = vc.getPropertyNames();
            if (properties == null || properties.length == 0) { // Label command
                props.setProperty ("cmdl." + name + ".label", vc.getLabel ()); // NOI18N
                props.setProperty ("cmdl." + name + ".order", vc.getOrderString ()); // NOI18N
            } else {
                props.setProperty ("cmd." + name + ".label", vc.getLabel ()); // NOI18N
                props.setProperty ("cmd." + name + ".order", vc.getOrderString ()); // NOI18N
                //PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
                for(int j = 0; j < properties.length; j++) {
                    String attrName = properties[j];
                    props.setProperty("cmd." + name + "." + attrName, vc.getProperty(attrName).toString());
                }
            }
        }
    }
     */

}

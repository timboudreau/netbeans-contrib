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

import org.openide.NotifyDescriptor;

/**
 * This class contains static methods for getting and setting command properties.
 *
 * @author  Martin Entlicher
 */
public class VcsCommandIO extends Object {

    public static HashMap defaultPropertyValues = null;
    
    /** Creates new VcsCommandIO */
    public VcsCommandIO() {
    }
    
    private static void initDefaultPropertyValues() {
        defaultPropertyValues = new HashMap();
        defaultPropertyValues.put(VcsCommand.PROPERTY_CONCURRENT_EXECUTION, new Integer(VcsCommand.EXEC_CONCURRENT_ALL));
        defaultPropertyValues.put(VcsCommand.PROPERTY_ON_FILE, Boolean.TRUE);
        defaultPropertyValues.put(VcsCommand.PROPERTY_ON_DIR, Boolean.TRUE);
        defaultPropertyValues.put(VcsCommand.PROPERTY_ON_ROOT, Boolean.TRUE);
        defaultPropertyValues.put(VcsCommand.PROPERTY_REFRESH_CURRENT_FOLDER, Boolean.FALSE);
        defaultPropertyValues.put(VcsCommand.PROPERTY_REFRESH_PARENT_FOLDER, Boolean.FALSE);
        defaultPropertyValues.put(VcsCommand.PROPERTY_REFRESH_ON_FAIL, new Integer(0));
        defaultPropertyValues.put(VcsCommand.PROPERTY_DISPLAY_PLAIN_OUTPUT, Boolean.FALSE);
        defaultPropertyValues.put(VcsCommand.PROPERTY_PROCESS_ALL_FILES, Boolean.FALSE);
        defaultPropertyValues.put(VcsCommand.PROPERTY_RUN_ON_MULTIPLE_FILES, Boolean.FALSE);
        defaultPropertyValues.put(VcsCommand.PROPERTY_RUN_ON_MULTIPLE_FILES_IN_FOLDER, Boolean.FALSE);
        defaultPropertyValues.put(VcsCommand.PROPERTY_NEEDS_HIERARCHICAL_ORDER, Boolean.FALSE);
        defaultPropertyValues.put(VcsCommand.PROPERTY_CHECK_FOR_MODIFICATIONS, Boolean.FALSE);
        defaultPropertyValues.put(VcsCommand.PROPERTY_IGNORE_FAIL, Boolean.FALSE);
        defaultPropertyValues.put(VcsCommand.PROPERTY_NUM_REVISIONS, new Integer(0));
        defaultPropertyValues.put(VcsCommand.PROPERTY_CHANGING_NUM_REVISIONS, Boolean.FALSE);
        defaultPropertyValues.put(VcsCommand.PROPERTY_CHANGING_REVISION, Boolean.FALSE);
        defaultPropertyValues.put(VcsCommand.PROPERTY_DISTINGUISH_BINARY_FILES, Boolean.FALSE);
        defaultPropertyValues.put(VcsCommand.PROPERTY_SUPPORTS_ADVANCED_MODE, Boolean.FALSE);
        defaultPropertyValues.put(VcsCommand.PROPERTY_HIDDEN, Boolean.FALSE);
    }
    
    /**
     * Get the default value of a command property. When the property is not defined,
     * this value determines the default behavior.
     * @param propertyName the property name to get the default value for
     * @return the default value of the property
     */
    public static Object getDefaultPropertyValue(String propertyName) {
        if (defaultPropertyValues == null) {
            initDefaultPropertyValues();
        }
        return defaultPropertyValues.get(propertyName);
    }
    
    /**
     * Get the boolean value of the command property.
     * @param command the command to get the property of
     * @param propertyName the name of the property
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
     * @param command the command to get the property of
     * @param propertyName the name of the property
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
     * Get the boolean value of the command property, or its default value when
     * the property is not defined.
     * @param command the command to get the property of
     * @param propertyName the name of the property
     * @return the boolean value of the command property or default value when the property
     * does not exist or <code>false</code>, when the default value is not defined for this property.
     */
    public static boolean getBooleanPropertyAssumeDefault(VcsCommand command, String propertyName) {
        Object value = command.getProperty(propertyName);
        if (value == null) value = getDefaultPropertyValue(propertyName);
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        } else {
            return false;
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
    

}

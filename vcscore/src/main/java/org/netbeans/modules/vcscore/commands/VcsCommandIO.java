/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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

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

package org.netbeans.modules.metrics.options;

import java.beans.*;

/**
 * BeanInfo for the NTMetricSettings object.
 *
 * @author tball
 */
public class NTMetricSettingsBeanInfo extends SimpleBeanInfo {

    private static PropertyDescriptor[] props = {
        Util.makeProperty(NTMetricSettings.PROP_DEFAULT_WARNING, 
                          NTMetricSettings.class,
                          "getWarningLevel", 
                          "setWarningLevel",
                          "PROP_warningLevel",
                          "HINT_warningLevel"),
        Util.makeProperty(NTMetricSettings.PROP_DEFAULT_ERROR,
                          NTMetricSettings.class,
                          "getErrorLevel", 
                          "setErrorLevel",
                          "PROP_errorLevel",
                          "HINT_errorLevel"),
        Util.makeProperty(NTMetricSettings.PROP_DEFAULT_METHOD_WARNING, 
                          NTMetricSettings.class,
                          "getMethodWarningLevel", 
                          "setMethodWarningLevel",
                          "PROP_methodWarningLevel",
                          "HINT_methodWarningLevel"),
        Util.makeProperty(NTMetricSettings.PROP_DEFAULT_METHOD_ERROR,
                          NTMetricSettings.class,
                          "getMethodErrorLevel", 
                          "setMethodErrorLevel",
                          "PROP_methodErrorLevel",
                          "HINT_methodErrorLevel")
    };

    public PropertyDescriptor[] getPropertyDescriptors() {
	return props;
    }
}

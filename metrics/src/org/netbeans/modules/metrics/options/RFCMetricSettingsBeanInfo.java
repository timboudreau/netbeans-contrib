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
 * BeanInfo for the RFCMetricSettings object.
 *
 * @author tball
 */
public class RFCMetricSettingsBeanInfo extends SimpleBeanInfo {

    private static PropertyDescriptor[] props = {
        Util.makeProperty(RFCMetricSettings.PROP_DEFAULT_WARNING, 
                          RFCMetricSettings.class,
                          "getWarningLevel", 
                          "setWarningLevel",
                          "PROP_warningLevel",
                          "HINT_warningLevel"),
        Util.makeProperty(RFCMetricSettings.PROP_DEFAULT_ERROR,
                          RFCMetricSettings.class,
                          "getErrorLevel", 
                          "setErrorLevel",
                          "PROP_errorLevel",
                          "HINT_errorLevel"),
        Util.makeProperty(RFCMetricSettings.PROP_DEFAULT_METHOD_WARNING, 
                          RFCMetricSettings.class,
                          "getMethodWarningLevel", 
                          "setMethodWarningLevel",
                          "PROP_methodWarningLevel",
                          "HINT_methodWarningLevel"),
        Util.makeProperty(RFCMetricSettings.PROP_DEFAULT_METHOD_ERROR,
                          RFCMetricSettings.class,
                          "getMethodErrorLevel", 
                          "setMethodErrorLevel",
                          "PROP_methodErrorLevel",
                          "HINT_methodErrorLevel")
    };

    public PropertyDescriptor[] getPropertyDescriptors() {
	return props;
    }
}

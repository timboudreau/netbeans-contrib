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
 * BeanInfo for the CBOMetricSettings object.
 *
 * @author tball
 */
public class CBOMetricSettingsBeanInfo extends SimpleBeanInfo {

    private static PropertyDescriptor[] props = {
        Util.makeProperty(CBOMetricSettings.PROP_DEFAULT_WARNING, 
                          CBOMetricSettings.class,
                          "getWarningLevel", 
                          "setWarningLevel",
                          "PROP_warningLevel",
                          "HINT_warningLevel"),
        Util.makeProperty(CBOMetricSettings.PROP_DEFAULT_ERROR,
                          CBOMetricSettings.class,
                          "getErrorLevel", 
                          "setErrorLevel",
                          "PROP_errorLevel",
                          "HINT_errorLevel"),
        Util.makeProperty(CBOMetricSettings.PROP_INCLUDE_JDK_CLASSES,
                          CBOMetricSettings.class,
                          "includeJDKClasses", 
                          "setIncludeJDKClasses",
                          "PROP_includeJDKClasses",
                          "HINT_includeJDKClasses"),
        Util.makeProperty(CBOMetricSettings.PROP_INCLUDE_OPENIDE_CLASSES,
                          CBOMetricSettings.class,
                          "includeOpenIDEClasses", 
                          "setIncludeOpenIDEClasses",
                          "PROP_includeOpenIDEClasses",
                          "HINT_includeOpenIDEClasses")
    };

    public PropertyDescriptor[] getPropertyDescriptors() {
	return props;
    }
}

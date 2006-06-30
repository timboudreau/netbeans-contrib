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

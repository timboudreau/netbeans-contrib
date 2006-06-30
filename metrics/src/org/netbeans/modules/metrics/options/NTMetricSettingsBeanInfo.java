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

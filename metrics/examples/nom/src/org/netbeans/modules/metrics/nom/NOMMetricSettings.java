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

package org.netbeans.modules.metrics.nom;

import org.netbeans.modules.metrics.options.MetricSettings;
import org.openide.options.SystemOption;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/** Options for NOM metric.
 *
 * @author  tball
 */
public class NOMMetricSettings extends SystemOption implements MetricSettings {

    // private static final long serialVersionUID = -6942670443375581997L;

    public static final String PROP_DEFAULT_WARNING = "NOMMetric.default_warning_value";
    public static final String PROP_DEFAULT_ERROR = "NOMMetric.default_error_value";
    public static final String PROP_DEFAULT_METHOD_WARNING = "NOMMetric.default_method_warning_value";
    public static final String PROP_DEFAULT_METHOD_ERROR = "NOMMetric.default_method_error_value";

    // Class-level warning levels:  tweak as needed
    private final static int defaultWarningLevel = 20;
    private final static int defaultErrorLevel = 30;

    // Method-level warning levels are not used by this metric.
    private final static int defaultMethodWarningLevel = -1;
    private final static int defaultMethodErrorLevel = -1;

    /** Singleton instance */
    private static NOMMetricSettings singleton;

    protected void initialize () {
        super.initialize ();
        setWarningLevel(defaultWarningLevel);
        setErrorLevel(defaultErrorLevel);
	setMethodWarningLevel(defaultMethodWarningLevel);
	setMethodErrorLevel(defaultMethodErrorLevel);
    }

    public String displayName () {
        return NbBundle.getMessage(NOMMetricSettings.class, "LBL_NOMMetricSettings");
    }

    /** Default instance of this system option. */
    public static NOMMetricSettings getDefault() {
        if (singleton == null) {
            singleton = (NOMMetricSettings) 
                findObject(NOMMetricSettings.class, true);
        }
        return singleton;
    }

    public int getWarningLevel () {
        return ((Integer)getProperty(PROP_DEFAULT_WARNING)).intValue();
    }

    public void setWarningLevel (int value) {
        // Automatically fires property changes if needed etc.:
        putProperty (PROP_DEFAULT_WARNING, new Integer(value), true);
    }

    public int getErrorLevel () {
        return ((Integer)getProperty(PROP_DEFAULT_ERROR)).intValue();
    }

    public void setErrorLevel (int value) {
        putProperty (PROP_DEFAULT_ERROR, new Integer(value), true);
    }

    public int getMethodWarningLevel() {
	return ((Integer)getProperty(PROP_DEFAULT_METHOD_WARNING)).intValue();
    }

    public void setMethodWarningLevel(int value) {
        putProperty (PROP_DEFAULT_METHOD_WARNING, new Integer(value), true);
    }

    public int getMethodErrorLevel() {
	return ((Integer)getProperty(PROP_DEFAULT_METHOD_ERROR)).intValue();
    }

    public void setMethodErrorLevel(int value) {
        putProperty (PROP_DEFAULT_METHOD_ERROR, new Integer(value), true);
    }
}

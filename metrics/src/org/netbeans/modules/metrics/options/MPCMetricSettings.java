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

import org.openide.options.SystemOption;
import org.openide.util.NbBundle;

/** Options for MPC metric.
 *
 * @author  tball
 */
public class MPCMetricSettings extends SystemOption implements MetricSettings {

    private static final long serialVersionUID = 1550020669558465446L;

    public static final String PROP_DEFAULT_WARNING = "MPCMetric.default_warning_value";
    public static final String PROP_DEFAULT_ERROR = "MPCMetric.default_error_value";
    public static final String PROP_DEFAULT_METHOD_WARNING = "MPCMetric.default_method_warning_value";
    public static final String PROP_DEFAULT_METHOD_ERROR = "MPCMetric.default_method_error_value";

    // tweak as needed
    private final static int defaultWarningLevel = 25;
    private final static int defaultErrorLevel = 50;
    private final static int defaultMethodWarningLevel = 10;
    private final static int defaultMethodErrorLevel = 20;


    /** Singleton instance */
    private static MPCMetricSettings singleton;

    protected void initialize () {
        super.initialize ();
        setWarningLevel(defaultWarningLevel);
        setErrorLevel(defaultErrorLevel);
	setMethodWarningLevel(defaultMethodWarningLevel);
	setMethodErrorLevel(defaultMethodErrorLevel);
    }

    public String displayName () {
        return NbBundle.getMessage(MPCMetricSettings.class, "LBL_MPCMetricSettings");
    }

    /** Default instance of this system option. */
    public static MPCMetricSettings getDefault() {
        if (singleton == null) {
            singleton = (MPCMetricSettings) 
                findObject(MPCMetricSettings.class, true);
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
	Integer prop = (Integer)getProperty(PROP_DEFAULT_METHOD_WARNING);
	if (prop == null) {
	    // update older version metric system option
	    setMethodWarningLevel(defaultMethodWarningLevel);
	    return defaultMethodWarningLevel;
	}
        return prop.intValue();
    }

    public void setMethodWarningLevel(int value) {
        putProperty (PROP_DEFAULT_METHOD_WARNING, new Integer(value), true);
    }

    public int getMethodErrorLevel() {
	Integer prop = (Integer)getProperty(PROP_DEFAULT_METHOD_ERROR);
	if (prop == null) {
	    // update older version metric system option
	    setMethodWarningLevel(defaultMethodErrorLevel);
	    return defaultMethodErrorLevel;
	}
        return prop.intValue();
    }

    public void setMethodErrorLevel(int value) {
        putProperty (PROP_DEFAULT_METHOD_ERROR, new Integer(value), true);
    }
}

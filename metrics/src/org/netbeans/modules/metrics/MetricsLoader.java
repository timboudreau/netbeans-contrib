/*
 * MetricsLoader.java
 *
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
 *
 * Contributor(s): Thomas Ball
 *
 * Version: $Revision$
 */

package org.netbeans.modules.metrics;

import java.lang.reflect.Constructor;

/**
 * The utility class responsible for discovering, loading and managing 
 * the collection of installed metrics.
 *
 * @author  tball
 * @version
 */
public class MetricsLoader {

    private MetricsLoader() {
	// don't instantiate
    }

    private static Class[] metricClasses = {
        WMCMetric.class,
        CBOMetric.class,
        RFCMetric.class,
        MPCMetric.class,
        DITMetric.class,
        NOCMetric.class,
        NTMetric.class
    };
    private static int maxMetrics = metricClasses.length;

    public static Class[] getMetricClasses() {
	return metricClasses;
    }

    public static int getNumberOfMetricClasses() {
	return maxMetrics;
    }

    /**
     * Create a new set of metrics objects for a given ClassMetrics
     * client.  The returned set consists of one metric instance for
     * each installed Metric class, and is not in any specific order.
     *
     * If the metrics set is to only be used for introspection (such
     * as by the MetricsContextOption class, then use null for the
     * ClassMetrics object as that will suppress classfile loading
     * and node create.  NOTE:  this is dangerous for normal metric
     * use, as metric objects will throw exceptions such as NPE if
     * misused.  In other words, don't file any bugs against this
     * performance hack, or it will be removed.
     *
     * @param cm  the ClassMetrics object the created metrics will analyze.
     * @return  an array of Metric objects.
     */
    public static Metric[] createMetricsSet(ClassMetrics cm) {
	
	Metric[] metrics = new Metric[maxMetrics];
	for (int i = 0; i < maxMetrics; i++) {
	    Class cls = metricClasses[i];
	    try {
		Constructor c = cls.getConstructor(oneParamCls);
		oneParam[0] = cm;
		metrics[i] = (Metric)c.newInstance(oneParam);
	    } catch (Exception e) {
		System.err.println("couldn't create metric: " + 
				   cls.getName());
		e.printStackTrace();
	    }
	}
	return metrics;
    }
    private static Class[] oneParamCls = new Class[] {
	ClassMetrics.class
    };
    private static Object[] oneParam = new Object[1];
}

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
import java.util.Collection;
import java.util.Iterator;
import org.openide.ErrorManager;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;


/**
 * The utility class responsible for discovering, loading and managing 
 * the collection of installed metrics.
 *
 * @author  tball
 * @version
 */
public final class MetricsLoader {

    private MetricsLoader() {
	// don't instantiate
    }

    private static int maxMetrics;
    private static MetricFactory[] metricFactories;
    private static Class[] metricClasses;

    // Lookup installed metric classes.
    static {
	Lookup.Template template = new Lookup.Template(MetricFactory.class);
	final Lookup.Result result = Lookup.getDefault().lookup(template);
	result.addLookupListener(new LookupListener() {
		public void resultChanged(LookupEvent e) {
		    loadMetricFactories(result);
		}
	    });
	loadMetricFactories(result);
    }

    private static void loadMetricFactories(Lookup.Result result) {
	synchronized (MetricsLoader.class) {
	    try {
		Collection c = result.allInstances();
		maxMetrics = c.size();
		metricFactories = new MetricFactory[maxMetrics];
		metricClasses = new Class[maxMetrics];
		int n = 0;
		for (Iterator i = c.iterator(); i.hasNext(); ) {
		    metricFactories[n] = (MetricFactory)i.next();
		    metricClasses[n] = metricFactories[n].getClass();
		    n++;
		}
	    } catch (Exception e) {
		String msg = 
		    "metrics module exception loading factories: " + e;
		ErrorManager.getDefault().log(ErrorManager.ERROR, msg);
	    }
	}

	String msg = "metrics module: " + maxMetrics + 
	    " metric(s) were loaded";
	ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, msg);
    }

    public static Class[] getMetricClasses() {
	return metricClasses;
    }

    public static int getNumberOfMetrics() {
	return maxMetrics;
    }

    /**
     * Create a new set of metrics objects for a given ClassMetrics
     * client.  The returned set consists of one metric instance for
     * each installed Metric class, and is not in any specific order.
     *
     * @param cm  the ClassMetrics object the created metrics will analyze.
     * @return  an array of Metric objects.
     */
    public static Metric[] createMetricsSet(ClassMetrics cm) {
	Metric[] metrics = new Metric[maxMetrics];
	for (int i = 0; i < maxMetrics; i++)
	    metrics[i] = metricFactories[i].createMetric(cm);
	return metrics;
    }
}

/*
 * WMCMetric.java
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

import org.netbeans.modules.metrics.options.*;

import java.util.Iterator;

/**
 * Class which calculates the Weighted Methods per Class metric.
 * WMC is defined as the sum of the complexity of each method; 
 * unfortunately, there is no consensus as to the definition of
 * "complexity".  Most metrics tools just punt and assign a 
 * complexity value of one for each method, such that WMC equals
 * the number of methods.
 *
 * I think a more reasonable definition is to sum the code paths of
 * each method, which is approximately the McCabe Cyclomatic Complexity
 * value at the method level.  I made this more Java-centric by 
 * subtracting one code path from each private method, since they can 
 * only be an extension of a more public method.  This avoids penalizing 
 * developers who refactor their classes to use smaller methods, which 
 * aids source comprehension.
 *
 * NOTE:  Something needs to be done to better factor package-private
 * methods.  I'm pushing this off for now until I investigate what
 * sort of package metrics make sense. (TAB)
 *
 * @author  tball
 * @version 
 */
public class WMCMetric extends AbstractMetric {

    static final String displayName = 
        MetricsNode.bundle.getString ("LBL_WMCMetric");

    static final String shortDescription = 
	MetricsNode.bundle.getString ("HINT_WMCMetric");

    protected WMCMetric(ClassMetrics classMetrics) {
        super(classMetrics);
    }

    public String getName() {
        return "WMCMetric";
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public Integer getMetricValue() {
        if (metric == null) {
            createMetric();
        }
        return metric;
    }
    
    public String getDetails() {
        if (details == null) {
            createMetric();
        }
        return details;
    }

    public MetricSettings getSettings() {
	return WMCMetricSettings.getDefault();
    }

    public Integer getMetricValue(MethodMetrics mm) throws NoSuchMetricException {
	if (mm.isSynthetic())  // skip synthetic methods
	    return new Integer(0);
	int c = mm.getCodePathCount();
	if (mm.isPrivate())
	    c--;
	return new Integer(c);
    }

    private void createMetric() {
        int n = 0;
        StringBuffer buf = new StringBuffer();
        buf.append("WMC for ");
        buf.append(classMetrics.getName());
        buf.append('\n');

        Iterator iter = classMetrics.getMethods().iterator();
        while (iter.hasNext()) {
            MethodMetrics mm = (MethodMetrics)iter.next();
	    int c = 0;
	    try {
		c = getMetricValue(mm).intValue();
	    } catch (NoSuchMetricException e) {
		// assert(false);
	    }
            n += c;

            buf.append("   ");
            buf.append(c);
            buf.append("   ");
            buf.append(mm.getFullName());
            buf.append('\n');
        }
        metric = new Integer(n);
        details = buf.toString();
    }

    public boolean needsOtherClasses() {
        return false;
    }

    public boolean isMethodMetric() {
	return true;
    }

    /**
     * Actually a private class used by the MetricsLoader, but
     * must be public since its instance is created by the XML
     * filesystem.
     */
    public static class Factory implements MetricFactory {
	public Metric createMetric(ClassMetrics cm) {
	    return new WMCMetric(cm);
	}
	public Class getMetricClass() {
	    return WMCMetric.class;
	}
    }
}

/*
 * MPCMetric.java
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

import java.util.*;

/**
 *
 * @author  tball
 * @version 
 */
public class MPCMetric extends AbstractMetric {

    static final String displayName = 
        MetricsNode.bundle.getString ("LBL_MPCMetric");

    static final String shortDescription = 
	MetricsNode.bundle.getString ("HINT_MPCMetric");

    /** Creates new MPCMetric */
    public MPCMetric(ClassMetrics classMetrics) {
        super(classMetrics);
    }

    /**
     * Do not use this constructor!  It's only to be used by the Lookup
     * service when dynamically loading metric classes.
     */
    public MPCMetric() {
    }

    public String getName() {
        return "MPCMetric";
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public String getShortDescription() {
        return shortDescription;
    }
    
    public MetricSettings getSettings() {
	return MPCMetricSettings.getDefault();
    }

    private void buildMetric() {
        if (metric == null) {
            int messageSends = 0;
            StringBuffer sb = new StringBuffer();
            Iterator iter = classMetrics.getMethods().iterator();
            while (iter.hasNext()) {
                MethodMetrics mm = (MethodMetrics)iter.next();
                if (mm.isSynthetic())  // skip synthetic methods
                    continue;

                int msgs = mm.getMessageSendCount();
                messageSends += msgs;

                sb.append(mm.getName());
                sb.append(":  ");
                sb.append(msgs);
                sb.append('\n');
            }
            metric = new Integer(messageSends);
            details = sb.toString();
        }
    }
    
    public Integer getMetricValue() {
        buildMetric();
        return metric;
    }
    
    public String getDetails() {
        buildMetric();
        return details;
    }

    public boolean needsOtherClasses() {
        return false;
    }

    public boolean isMethodMetric() {
	return true;
    }

    public Integer getMetricValue(MethodMetrics mm) throws NoSuchMetricException {
	return new Integer(mm.getMessageSendCount());
    }
}

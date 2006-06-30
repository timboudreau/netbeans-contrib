/*
 * MPCMetric.java
 *
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
    protected MPCMetric(ClassMetrics classMetrics) {
        super(classMetrics);
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

    /**
     * Actually a private class used by the MetricsLoader, but
     * must be public since its instance is created by the XML
     * filesystem.
     */
    public static class Factory implements MetricFactory {
	public Metric createMetric(ClassMetrics cm) {
	    return new MPCMetric(cm);
	}
	public Class getMetricClass() {
	    return MPCMetric.class;
	}
    }
}

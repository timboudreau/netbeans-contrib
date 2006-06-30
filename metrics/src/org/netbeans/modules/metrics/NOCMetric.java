/*
 * NOCMetric.java
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

import java.util.Iterator;
import org.netbeans.modules.classfile.ClassName;

/**
 *
 * @author  tball
 * @version
 */
public class NOCMetric extends AbstractMetric {

    static final String displayName =
        MetricsNode.bundle.getString ("LBL_NOCMetric");

    static final String shortDescription =
	MetricsNode.bundle.getString ("HINT_NOCMetric");

    /** Creates new NOCMetric */
    protected NOCMetric(ClassMetrics classMetrics) {
        super(classMetrics);
    }

    public String getName() {
        return "NOCMetric";
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public MetricSettings getSettings() {
	return NOCMetricSettings.getDefault();
    }

    public Integer getMetricValue() {
        metric = new Integer(classMetrics.numberOfChildClasses());
        return metric;
    }

    public String getDetails() {
        StringBuffer sb = new StringBuffer();
        sb.append(MetricsNode.bundle.getString ("STR_ChildClasses"));
        Iterator iter = classMetrics.getChildClasses().iterator();
        while (iter.hasNext()) {
            sb.append("\n   ");
            sb.append(((ClassName)iter.next()).getExternalName());
        }
        details = sb.toString();
        return details;
    }

    public boolean needsOtherClasses() {
        return true;
    }

    /**
     * Actually a private class used by the MetricsLoader, but
     * must be public since its instance is created by the XML
     * filesystem.
     */
    public static class Factory implements MetricFactory {
	public Metric createMetric(ClassMetrics cm) {
	    return new NOCMetric(cm);
	}
	public Class getMetricClass() {
	    return NOCMetric.class;
	}
    }
}

/*
 * NOCMetric.java
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
    public NOCMetric(ClassMetrics classMetrics) {
        super(classMetrics);
    }

    /**
     * Do not use this constructor!  It's only to be used by the Lookup
     * service when dynamically loading metric classes.
     */
    public NOCMetric() {
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
}

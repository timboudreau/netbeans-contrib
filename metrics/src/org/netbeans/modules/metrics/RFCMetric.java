/*
 * RFCMetric.java
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
public class RFCMetric extends AbstractMetric {

    static final String displayName = 
        MetricsNode.bundle.getString ("LBL_RFCMetric");

    /** Creates new RFCMetric */
    public RFCMetric(ClassMetrics classMetrics) {
        super(classMetrics);
    }

    public String getName() {
        return "RFCMetric";
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public String getShortDescription() {
        return MetricsNode.bundle.getString ("HINT_RFCMetric");
    }
    
    public MetricSettings getSettings() {
	return RFCMetricSettings.getDefault();
    }

    private void buildMetric() {
        if (metric == null) {
            Set methods = new TreeSet();
            Set referencedMethods = new TreeSet();
            Iterator iter = classMetrics.getMethods().iterator();
            while (iter.hasNext()) {
                MethodMetrics mm = (MethodMetrics)iter.next();
                if (mm.isSynthetic())  // skip synthetic methods
                    continue;
                methods.add(mm.getFullName());
                Iterator iter2 = mm.getMethodReferences();
                while (iter2.hasNext()) {
                    String ref = (String)iter2.next();
                    referencedMethods.add(ref);
                }
            }
            metric = new Integer(methods.size() + referencedMethods.size());
            
            StringBuffer sb = new StringBuffer();
            sb.append(MetricsNode.bundle.getString ("STR_ClassMethods"));
            iter = methods.iterator();
            while (iter.hasNext()) {
                sb.append("\n   ");
                sb.append((String)iter.next());
            }
            sb.append("\n");
            sb.append(MetricsNode.bundle.getString ("STR_ReferencedMethods"));
            iter = referencedMethods.iterator();
            while (iter.hasNext()) {
                sb.append("\n   ");
                sb.append((String)iter.next());
            }
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
	return new Integer(mm.getMethodReferencesCount());
    }
}

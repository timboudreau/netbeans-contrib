/*
 * NTMetric.java
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

import java.text.MessageFormat;
import java.util.*;

/**
 *
 * @author  tball
 * @version 
 */
public class NTMetric extends AbstractMetric {

    static final String displayName = 
        MetricsNode.bundle.getString ("LBL_NTMetric");

    static final String shortDescription = 
	MetricsNode.bundle.getString ("HINT_NTMetric");

    /** Creates new NTMetric */
    public NTMetric(ClassMetrics classMetrics) {
        super(classMetrics);
    }

    /**
     * Do not use this constructor!  It's only to be used by the Lookup
     * service when dynamically loading metric classes.
     */
    public NTMetric() {
    }

    public String getName() {
        return "NTMetric";
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public String getShortDescription() {
        return shortDescription;
    }
    
    public MetricSettings getSettings() {
	return NTMetricSettings.getDefault();
    }

    private void buildMetric() {
        if (metric == null) {
            MessageFormat detailsMsgTemplate = new MessageFormat(
                MetricsNode.bundle.getString ("STR_TrampDetails"));
            MessageFormat selfMsgTemplate = new MessageFormat(
                MetricsNode.bundle.getString ("STR_SelfTramp"));
            Object[] msgParams = new Object[2];

            Set detailMsgs = new TreeSet();
            int totalTramps = 0;

            Iterator iter = classMetrics.getMethods().iterator();
            while (iter.hasNext()) {
                MethodMetrics mm = (MethodMetrics)iter.next();
                if (!mm.isSynthetic() && mm.hasTramps()) {
                    totalTramps += mm.getTrampCount();
                    Iterator iter2 = mm.getTramps();
                    while (iter2.hasNext()) {
                        String msg;
                        MethodMetrics.Parameter p = 
                            (MethodMetrics.Parameter)iter2.next();
                        int pIdx = p.getIndex();
                        msgParams[0] = mm.getFullName();
                        msgParams[1] = new Integer(pIdx);
                        if (pIdx == 0) {
                            msg = selfMsgTemplate.format(msgParams);
                        } else {
                            msg = detailsMsgTemplate.format(msgParams);
                        }
                        detailMsgs.add(msg);
                    }
                }
            }
            metric = new Integer(totalTramps);
            
            StringBuffer sb = new StringBuffer();
            sb.append(MetricsNode.bundle.getString ("STR_Tramps"));
            iter = detailMsgs.iterator();
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
	return new Integer(mm.getTrampCount());
    }
}

/*
 * MethodApprovals.java
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

import org.netbeans.modules.metrics.options.MetricSettings;
import java.io.*;
import java.util.*;

/**
 * Class representing a per-method metric approval for a Java file.
 * 
 * @author  tball
 * @version 
 */
class MethodApprovals implements Comparable, ApprovalAcceptor {
    private String methodName;
    private Set approvals = new TreeSet();

    MethodApprovals(String methodName) {
	this.methodName = methodName;
    }

    String getMethodName() {
	return methodName;
    }

    int getApprovalLevel(Metric metric) {
        Class metricClass = metric.getClass();
        Iterator i = approvals.iterator();
        while (i.hasNext()) {
            MetricValue mv = (MetricValue)i.next();
            if (metricClass.equals(mv.getMetricClass()))
                return mv.getMetric();
        }
	return -1;
    }

    int getWarningLevel(Metric metric) {
	int level = getApprovalLevel(metric);
	if (level != -1)
	    return level;

	// No approval, return default value.
	MetricSettings settings = metric.getSettings();
	return settings.getWarningLevel();
    }

    int getErrorLevel(Metric metric) {
	int level = getApprovalLevel(metric);
	if (level != -1)
	    return level;

	// No approval, return default value.
	MetricSettings settings = metric.getSettings();
	return settings.getErrorLevel();
    }

    public void addApproval(MetricValue mv) {
	approvals.add(mv);
    }

    void writeXML(PrintWriter xml) {
	if (approvals.size() > 0) {
	    xml.println("    <method_approvals method=\"" + methodName + "\">");
	    Iterator i = approvals.iterator();
	    while (i.hasNext()) {
		MetricValue mv = (MetricValue)i.next();
		mv.writeXML(xml, "      ");
	    }
	    xml.println("    </method_approvals>");
	}
    }

    public boolean equals(Object obj) {
        if (this == obj)
	    return true;
	return (obj instanceof MethodApprovals) ? 
	  (methodName.equals(((MethodApprovals)obj).methodName)) : false;
    }

    public int hashCode() {
	return methodName.hashCode();
    }

    public int compareTo(Object obj) {
        return methodName.compareTo(((MethodApprovals)obj).methodName);
    }

    public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append("method approval: ");
	sb.append(methodName);
	sb.append("\"");
	return sb.toString();
    }
}

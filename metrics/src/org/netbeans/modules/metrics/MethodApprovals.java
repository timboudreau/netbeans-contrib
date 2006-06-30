/*
 * MethodApprovals.java
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

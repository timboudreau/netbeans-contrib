/*
 * ClassApprovals.java
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
 * Class that stores any metric approvals for a Java class.
 *
 * @author  tball
 * @version
 */
class ClassApprovals implements Comparable, ApprovalAcceptor {
    private String className;
    private Set approvals = new TreeSet();
    private Map methodApprovalsMap = null;

    ClassApprovals(String className) {
	this.className = className;
    }

    String getClassName() {
	return className;
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

    private int getWarningLevel(Metric metric) {
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

    void addMethodApprovals(MethodApprovals ma) {
	if (methodApprovalsMap == null)
	    methodApprovalsMap = new TreeMap();
	methodApprovalsMap.put(ma.getMethodName(), ma);
    }

    MethodApprovals getMethodApprovals(String name) {
	if (methodApprovalsMap == null)
	    return null;
	return (MethodApprovals)methodApprovalsMap.get(name);
    }

    void writeXML(PrintWriter xml) {
	if (approvals.size() > 0 || methodApprovalsMap.size() > 0) {
	    xml.println("  <class_approvals class=\"" + className + "\">");

	    Iterator i = approvals.iterator();
	    while (i.hasNext()) {
		MetricValue mv = (MetricValue)i.next();
		mv.writeXML(xml, "    ");
	    }

	    if (methodApprovalsMap != null) {
		i = methodApprovalsMap.values().iterator();
		while (i.hasNext()) {
		    MethodApprovals ma = (MethodApprovals)i.next();
		    ma.writeXML(xml);
		}
	    }

	    xml.println("  </class_approvals>");
	}
    }

    public boolean equals(Object obj) {
        if (this == obj)
	    return true;
	return (obj instanceof ClassApprovals) ? 
	  (className.equals(((ClassApprovals)obj).className)) : false;
    }

    public int hashCode() {
	return className.hashCode();
    }

    public int compareTo(Object obj) {
        return className.compareTo(((ClassApprovals)obj).className);
    }

    public String toString() {
	String s = "approvals: " + className + " metrics:";
	Iterator i = approvals.iterator();
	while (i.hasNext())
	    s += " {" + ((MetricValue)i.next()).toString() + "}";
	if (methodApprovalsMap != null) {
	    s += " method metrics:";
	    i = methodApprovalsMap.values().iterator();
	    while (i.hasNext())
		s += " {" + ((MethodApprovals)i.next()).toString() + "}";
	}
	return s;
    }
}

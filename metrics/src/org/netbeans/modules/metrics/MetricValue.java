/*
 * MetricValue.java
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

import java.io.*;

/**
 * Class that holds a metric value and its approval.
 * 
 * @author  tball
 * @version 
 */
class MetricValue implements Comparable {
    private int metric;
    private Class metricClass;
    private String approver;
    private String comment;

    MetricValue(Class metricClass, int newMetric) {
	this.metricClass = metricClass;
	this.metric = newMetric;
    }

    MetricValue(Class metricClass, int newMetric, 
		String approver, String comment) {
	this(metricClass, newMetric);
	this.approver = approver;
	this.comment = comment;
    }

    int getMetric() {
	return metric;
    }

    Class getMetricClass() {
	return metricClass;
    }

    String getApprover() {
	return approver;
    }

    void setApprover(String approver) {
	this.approver = approver;
    }

    String getComment() {
	return comment;
    }

    void setComment(String comment) {
	this.comment = comment;
    }

    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (!(obj instanceof MetricValue))
	    return false;
	MetricValue mv = (MetricValue)obj;
	return 
	    metricClass.equals(mv.metricClass) &&
	    metric == mv.metric &&
	    approver.equals(mv.approver) &&
	    comment.equals(mv.comment);
    }

    public int hashCode() {
	int result = 31*metricClass.hashCode() + metric;
	result = 31*result + (approver == null ? 0 : approver.hashCode());
	result = 31*result + (comment == null ? 0 : comment.hashCode());
	return result;
    }

    public int compareTo(Object obj) {
	MetricValue mv = (MetricValue)obj;
	String name1 = metricClass.getName();
	String name2 = mv.metricClass.getName();
	int ret = name1.compareTo(name2);
	if (ret != 0)
	    return ret;
	if (metric != mv.metric)
	    return (metric < mv.metric ? -1 : 1);
	ret = approver.compareTo(mv.approver);
	if (ret != 0)
	    return ret;
	return comment.compareTo(mv.comment);
    }

    public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append("class=\"");
	sb.append(metricClass.getName());
	sb.append("\" metric=\"");
	sb.append(metric);
	sb.append("\"");
	return sb.toString();
    }

    void writeXML(PrintWriter xml, String indent) {
	xml.println(indent + "<approval metric=\"" + metric + 
		    "\" class=\"" + metricClass.getName() + "\">");
	if (approver != null) {
	    xml.print(indent + "  <approver name=\"" + approver + '\"');
	    if (comment != null)
		xml.print(" comment=\"" + comment + '\"');
	    xml.println("/>");
	}
	xml.println(indent + "</approval>");
    }
}

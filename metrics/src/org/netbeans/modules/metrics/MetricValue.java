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
import java.util.*;

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
	return (obj instanceof MetricValue) ? 
	    (metricClass.equals(((MetricValue)obj).metricClass)) : false;
    }

    public int compareTo(Object obj) {
	String name1 = metricClass.getName();
	String name2 = ((MetricValue)obj).metricClass.getName();
	return name1.compareTo(name2);
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

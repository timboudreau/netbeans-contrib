/*
 * MetricValue.java
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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

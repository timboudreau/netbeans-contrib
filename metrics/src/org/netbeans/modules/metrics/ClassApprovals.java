/*
 * ClassApprovals.java
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

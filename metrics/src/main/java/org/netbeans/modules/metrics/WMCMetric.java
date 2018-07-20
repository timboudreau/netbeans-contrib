/*
 * WMCMetric.java
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

import org.netbeans.modules.metrics.options.*;

import java.util.Iterator;

/**
 * Class which calculates the Weighted Methods per Class metric.
 * WMC is defined as the sum of the complexity of each method;
 * unfortunately, there is no consensus as to the definition of
 * "complexity".  Most metrics tools just punt and assign a
 * complexity value of one for each method, such that WMC equals
 * the number of methods.
 *
 * I think a more reasonable definition is to sum the code paths of
 * each method, which is approximately the McCabe Cyclomatic Complexity
 * value at the method level.  I made this more Java-centric by 
 * subtracting one code path from each private method, since they can 
 * only be an extension of a more public method.  This avoids penalizing 
 * developers who refactor their classes to use smaller methods, which 
 * aids source comprehension.
 *
 * NOTE:  Something needs to be done to better factor package-private
 * methods.  I'm pushing this off for now until I investigate what
 * sort of package metrics make sense. (TAB)
 *
 * @author  tball
 * @version 
 */
public class WMCMetric extends AbstractMetric {

    static final String displayName = 
        MetricsNode.bundle.getString ("LBL_WMCMetric");

    static final String shortDescription = 
	MetricsNode.bundle.getString ("HINT_WMCMetric");

    protected WMCMetric(ClassMetrics classMetrics) {
        super(classMetrics);
    }

    public String getName() {
        return "WMCMetric";
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public Integer getMetricValue() {
        if (metric == null) {
            createMetric();
        }
        return metric;
    }
    
    public String getDetails() {
        if (details == null) {
            createMetric();
        }
        return details;
    }

    public MetricSettings getSettings() {
	return WMCMetricSettings.getDefault();
    }

    public Integer getMetricValue(MethodMetrics mm) throws NoSuchMetricException {
	if (mm.isSynthetic())  // skip synthetic methods
	    return new Integer(0);
	int c = mm.getCodePathCount();
	if (mm.isPrivate())
	    c--;
	return new Integer(c);
    }

    private void createMetric() {
        int n = 0;
        StringBuffer buf = new StringBuffer();
        buf.append("WMC for ");
        buf.append(classMetrics.getName());
        buf.append('\n');

        Iterator iter = classMetrics.getMethods().iterator();
        while (iter.hasNext()) {
            MethodMetrics mm = (MethodMetrics)iter.next();
	    int c = 0;
	    try {
		c = getMetricValue(mm).intValue();
	    } catch (NoSuchMetricException e) {
		// assert(false);
	    }
            n += c;

            buf.append("   ");
            buf.append(c);
            buf.append("   ");
            buf.append(mm.getFullName());
            buf.append('\n');
        }
        metric = new Integer(n);
        details = buf.toString();
    }

    public boolean needsOtherClasses() {
        return false;
    }

    public boolean isMethodMetric() {
	return true;
    }

    /**
     * Actually a private class used by the MetricsLoader, but
     * must be public since its instance is created by the XML
     * filesystem.
     */
    public static class Factory implements MetricFactory {
	public Metric createMetric(ClassMetrics cm) {
	    return new WMCMetric(cm);
	}
	public Class getMetricClass() {
	    return WMCMetric.class;
	}
    }
}

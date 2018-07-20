/*
 * DITMetric.java
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

import org.netbeans.modules.metrics.options.DITMetricSettings;
import org.netbeans.modules.metrics.options.MetricSettings;

/**
 * Calculates Depth of Inheritence Tree for a Class.  This
 * is defined as the number of super classes a class has.
 * In C++ this is frequently zero, but in Java only
 * java.lang.Object has a DIT of zero.
 *
 * @author  tball
 * @version
 */
public class DITMetric extends AbstractMetric {

    static final String displayName = 
        MetricsNode.bundle.getString ("LBL_DITMetric");

    static final String shortDescription = 
	MetricsNode.bundle.getString ("HINT_DITMetric");

    protected DITMetric(ClassMetrics classMetrics) {
        super(classMetrics);
    }

    public String getName() {
        return "DITMetric";
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public MetricSettings getSettings() {
	return DITMetricSettings.getDefault();
    }

    private void buildMetric() {
	int n = 0;
	StringBuffer sb = new StringBuffer();
	ClassMetrics cm = classMetrics;
	sb.append(cm.getName().getExternalName());
	sb.append('\n');
	String indent = "   ";
	while (true) {
	    cm = cm.getSuperClass();

	    /* Object's superclass may be an empty string instead of null
	     * (depends on compiler).
	     */
	    if (cm == null)
		break;
	    String name = cm.getName().getExternalName();
	    if (name.length() == 0)
		break;

	    n++;
	    sb.append(indent);
	    indent += "   ";
	    sb.append(name);
	    sb.append('\n');
	}
	metric = new Integer(n);
	sb.deleteCharAt(sb.length() - 1); // delete last newline
	details = sb.toString();
    }

    public Integer getMetricValue() {
        if (metric == null)
	    buildMetric();
        return metric;
    }

    public String getDetails() {
        if (details == null)
	    buildMetric();
        return details;
    }
    
    public boolean needsOtherClasses() {
        return false;
    }

    /**
     * Actually a private class used by the MetricsLoader, but
     * must be public since its instance is created by the XML
     * filesystem.
     */
    public static class Factory implements MetricFactory {
	public Metric createMetric(ClassMetrics cm) {
	    return new DITMetric(cm);
	}
	public Class getMetricClass() {
	    return DITMetric.class;
	}
    }
}

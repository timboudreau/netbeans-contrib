/*
 * NOCMetric.java
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
import org.netbeans.modules.classfile.ClassName;

/**
 *
 * @author  tball
 * @version
 */
public class NOCMetric extends AbstractMetric {

    static final String displayName =
        MetricsNode.bundle.getString ("LBL_NOCMetric");

    static final String shortDescription =
	MetricsNode.bundle.getString ("HINT_NOCMetric");

    /** Creates new NOCMetric */
    protected NOCMetric(ClassMetrics classMetrics) {
        super(classMetrics);
    }

    public String getName() {
        return "NOCMetric";
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public MetricSettings getSettings() {
	return NOCMetricSettings.getDefault();
    }

    public Integer getMetricValue() {
        metric = new Integer(classMetrics.numberOfChildClasses());
        return metric;
    }

    public String getDetails() {
        StringBuffer sb = new StringBuffer();
        sb.append(MetricsNode.bundle.getString ("STR_ChildClasses"));
        Iterator iter = classMetrics.getChildClasses().iterator();
        while (iter.hasNext()) {
            sb.append("\n   ");
            sb.append(((ClassName)iter.next()).getExternalName());
        }
        details = sb.toString();
        return details;
    }

    public boolean needsOtherClasses() {
        return true;
    }

    /**
     * Actually a private class used by the MetricsLoader, but
     * must be public since its instance is created by the XML
     * filesystem.
     */
    public static class Factory implements MetricFactory {
	public Metric createMetric(ClassMetrics cm) {
	    return new NOCMetric(cm);
	}
	public Class getMetricClass() {
	    return NOCMetric.class;
	}
    }
}

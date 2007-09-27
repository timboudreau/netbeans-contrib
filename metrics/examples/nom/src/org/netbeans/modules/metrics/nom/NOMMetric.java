/*
 * NOMMetric.java
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

package org.netbeans.modules.metrics.nom;

import org.netbeans.modules.metrics.*;
import org.netbeans.modules.metrics.options.MetricSettings;
import org.openide.util.NbBundle;
import java.util.ResourceBundle;
import java.util.Iterator;

/**
 * The "Number of Methods" metric.  This is a trivial example metric
 * class which simply returns the number of methods defined in the
 * associated ClassFile.
 *
 * Note:  abstract methods are not included, as there are not any 
 * associated method bodies.  Also, synthetic methods (those
 * created by the compiler which are normally invisible) are
 * counted.
 */
public class NOMMetric extends AbstractMetric {

    /** ResourceBundle used in this class. */
    static ResourceBundle bundle = 
        NbBundle.getBundle (NOMMetric.class);

    static final String displayName = 
        bundle.getString ("LBL_NOMMetric");

    static final String shortDescription = 
	bundle.getString ("HINT_NOMMetric");

    /** Creates new NOMMetric */
    protected NOMMetric(ClassMetrics classMetrics) {
        super(classMetrics);
    }

    public String getName() {
        return "NOMMetric";
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public String getShortDescription() {
        return shortDescription;
    }
    
    public MetricSettings getSettings() {
	return NOMMetricSettings.getDefault();
    }

    public Integer getMetricValue() {
        return new Integer(classMetrics.getMethods().size());
    }
    
    public String getDetails() {
	// Return a string listing the method names.
	StringBuffer sb = 
	    new StringBuffer(bundle.getString("STR_ClassMethods"));
	Iterator i = classMetrics.getMethods().iterator();
	while (i.hasNext()) {
	    sb.append("\n   ");
	    MethodMetrics mm = (MethodMetrics)i.next();
	    sb.append(mm.getName());
	}
	sb.append('\n');
        return sb.toString();
    }

    public boolean needsOtherClasses() {
	// This metric does not need any other classes to calculate itself.
        return false;
    }

    public boolean isMethodMetric() {
	// This is a class-level metric.
	return false;
    }

    public Integer getMetricValue(MethodMetrics mm) throws NoSuchMetricException {
	// Not called, because isMethodMetric() returns false.
	return new Integer(-1);
    }

    /**
     * This a private class used by the MetricsLoader, but
     * must be public since its instance is created by the XML
     * filesystem.
     */
    public static class Factory implements MetricFactory {
	public Metric createMetric(ClassMetrics cm) {
	    return new NOMMetric(cm);
	}
	public Class getMetricClass() {
	    return NOMMetric.class;
	}
    }
}

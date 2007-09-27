/*
 * MetricsLoader.java
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

import java.util.Collection;
import java.util.Iterator;
import org.openide.ErrorManager;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;


/**
 * The utility class responsible for discovering, loading and managing
 * the collection of installed metrics.
 *
 * @author  tball
 * @version
 */
public final class MetricsLoader {

    private MetricsLoader() {
	// don't instantiate
    }

    private static int maxMetrics;
    private static MetricFactory[] metricFactories;
    private static Class[] metricClasses;

    // Lookup installed metric classes.
    static {
	Lookup.Template template = new Lookup.Template(MetricFactory.class);
	final Lookup.Result result = Lookup.getDefault().lookup(template);
	result.addLookupListener(new LookupListener() {
		public void resultChanged(LookupEvent e) {
		    loadMetricFactories(result);
		}
	    });
	loadMetricFactories(result);
    }

    private static void loadMetricFactories(Lookup.Result result) {
	synchronized (MetricsLoader.class) {
	    try {
		Collection c = result.allInstances();
		maxMetrics = c.size();
		metricFactories = new MetricFactory[maxMetrics];
		metricClasses = new Class[maxMetrics];
		int n = 0;
		for (Iterator i = c.iterator(); i.hasNext(); ) {
		    metricFactories[n] = (MetricFactory)i.next();
		    metricClasses[n] = metricFactories[n].getClass();
		    n++;
		}
	    } catch (Exception e) {
		String msg = 
		    "metrics module exception loading factories: " + e;
		ErrorManager.getDefault().log(ErrorManager.ERROR, msg);
	    }
	}

	String msg = "metrics module: " + maxMetrics + 
	    " metric(s) were loaded";
	ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, msg);
    }

    public static Class[] getMetricClasses() {
	return metricClasses;
    }

    public static int getNumberOfMetrics() {
	return maxMetrics;
    }

    /**
     * Create a new set of metrics objects for a given ClassMetrics
     * client.  The returned set consists of one metric instance for
     * each installed Metric class, and is not in any specific order.
     *
     * @param cm  the ClassMetrics object the created metrics will analyze.
     * @return  an array of Metric objects.
     */
    public static Metric[] createMetricsSet(ClassMetrics cm) {
	Metric[] metrics = new Metric[maxMetrics];
	for (int i = 0; i < maxMetrics; i++)
	    metrics[i] = metricFactories[i].createMetric(cm);
	return metrics;
    }

    static String getModuleVersion() {
	Lookup.Template templ = new Lookup.Template(ModuleInfo.class);
	Lookup.Result result = Lookup.getDefault().lookup(templ);
	Iterator i = result.allInstances().iterator();
	while (i.hasNext()) {
	    ModuleInfo mi = (ModuleInfo)i.next();
	    String name = mi.getCodeName();
	    if (name.equals("org.netbeans.modules.metrics/1"))
		return mi.getSpecificationVersion().toString();
	}
	return "<unknown>";
    }
}

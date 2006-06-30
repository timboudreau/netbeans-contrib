/*
 * CBOMetric.java
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

import org.netbeans.modules.metrics.options.*;

import java.util.Iterator;
import org.netbeans.modules.classfile.ClassName;

/**
 * Class which calculates Coupling Between Objects (fan-out) for a
 * ClassElement.  CBO is defined as the number of dependencies to other
 * classes plus the number of classes which are dependent on this class.
 *
 * @author  tball
 * @version
 */
public class CBOMetric extends AbstractMetric {

    static final String displayName = 
        MetricsNode.bundle.getString ("LBL_CBOMetric");

    static final String shortDescription = 
	MetricsNode.bundle.getString ("HINT_CBOMetric");

    protected CBOMetric(ClassMetrics classMetrics) {
        super(classMetrics);
    }

    public String getName() {
        return "CBOMetric";
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public MetricSettings getSettings() {
	return CBOMetricSettings.getDefault();
    }

    private boolean includeClass(ClassName cls, boolean includeJDK, 
                                 boolean includeOpenIDE) {
        // Don't check unless we have to...
        if (includeJDK && includeOpenIDE)
            return true;

        String name = cls.getExternalName();
        if (!includeJDK && 
            ((name.startsWith("java.") ||
              name.startsWith("javax.") ||
              name.startsWith("sun.") ||
              name.startsWith("com.sun.corba") ||
              name.startsWith("com.sun.image") ||
              name.startsWith("com.sun.imageio") ||
              name.startsWith("com.sun.java") ||
              name.startsWith("com.sun.naming") ||
              name.startsWith("com.sun.security"))))
            return false;

        if (!includeOpenIDE &&
            name.startsWith("org.openide."))
            return false;

        return true;
    }

    public Integer getMetricValue() {
        boolean includeJDK = 
            CBOMetricSettings.getDefault().includeJDKClasses();
        boolean includeIDE = 
            CBOMetricSettings.getDefault().includeOpenIDEClasses();

        int metric = 0;
        Iterator it = classMetrics.getDependentClasses().iterator();
        while (it.hasNext()) {
            ClassName cls = (ClassName)it.next();
            if (includeClass(cls, includeJDK, includeIDE))
                metric++;
        }

        it = classMetrics.getClientClasses().iterator();
        while (it.hasNext()) {
            ClassName cls = (ClassName)it.next();
            // check that it is not already counted as a dependent class
            if (includeClass(cls, includeJDK, includeIDE) && 
                !classMetrics.hasDependency(cls))
                    metric++;
        }
        return new Integer(metric);
    }
    
    public String getDetails() {
        boolean includeJDK = 
            CBOMetricSettings.getDefault().includeJDKClasses();
        boolean includeIDE = 
            CBOMetricSettings.getDefault().includeOpenIDEClasses();

        StringBuffer buf = new StringBuffer();
        buf.append("CBO for ");
        buf.append(classMetrics.getName());

        buf.append("\n  classes this class references:");
        int refs = 0;
        Iterator it = classMetrics.getDependentClasses().iterator();
        while (it.hasNext()) {
            ClassName cls = (ClassName)it.next();
            if (includeClass(cls, includeJDK, includeIDE)) {
                buf.append("\n    ");
                buf.append(cls.getExternalName());
                refs++;
            }
        }
        if (refs == 0)
            buf.append("\n    none");

        buf.append("\n  classes which reference this class:");
        it = classMetrics.getClientClasses().iterator();
        int clients = 0;
        while (it.hasNext()) {
            ClassName cls = (ClassName)it.next();
            if (includeClass(cls, includeJDK, includeIDE) && 
                !classMetrics.hasDependency(cls)) {
                buf.append("\n    ");
                buf.append(cls.getExternalName());
                clients++;
            }
        }
        if (clients == 0) {
            buf.append("\n    none");
        }
        return buf.toString();
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
	    return new CBOMetric(cm);
	}
	public Class getMetricClass() {
	    return CBOMetric.class;
	}
    }
}


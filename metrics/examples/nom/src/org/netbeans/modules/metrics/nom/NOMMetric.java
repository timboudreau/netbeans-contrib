/*
 * NOMMetric.java
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

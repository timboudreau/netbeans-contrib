/*
 * DITMetric.java
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

import org.netbeans.modules.metrics.options.*;

import java.io.IOException;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

import org.openide.src.ClassElement;
import org.openide.src.Identifier;

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

    public DITMetric(ClassMetrics classMetrics) {
        super(classMetrics);
    }

    /**
     * Do not use this constructor!  It's only to be used by the Lookup
     * service when dynamically loading metric classes.
     */
    public DITMetric() {
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
}

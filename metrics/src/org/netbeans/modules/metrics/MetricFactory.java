/*
 * MetricFactory.java
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

/**
 * Interface defining a Metric factory.  Each metric should define a
 * factory class that the MetricsLoader uses to create sets of metrics
 * on demand.
 *
 * It is recommended that this interface be defined in a separate class
 * from the metric itself.  This is because the MetricsLoader must
 * reference factory instances via the XML filesystem.  Since metrics
 * must have an associated ClassMetrics object to be valid, they cannot
 * be created via the XML filesystem.  This class needs to be public to
 * be accessable by this filesystem.
 *
 * @author Thomas Ball
 */
public interface MetricFactory {

    /**
     * Create a metric for the specified ClassMetrics object.
     * @param  cm  the ClassMetrics object the metric uses for its calculation.
     * @return a Metric object associated with the ClassMetrics object.
     */
    Metric createMetric(ClassMetrics cm);

    /**
     * Returns the associated Metric's class object.  This is
     * used by services such as the ApprovalFile class, but is
     * not used for metric instance creation.
     */
    Class getMetricClass();
}

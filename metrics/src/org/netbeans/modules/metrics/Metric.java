/*
 * Metric.java
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

import org.netbeans.modules.metrics.options.MetricSettings;
import java.beans.PropertyEditor;

public interface Metric {

    public final static int METRIC_OKAY = 0;
    public final static int METRIC_WARN = 1;
    public final static int METRIC_FAIL = 2;

    PropertyEditor getDetailsViewer();

    int getWarningLevel();

    void resetMetric();

    String getName();
    String getDisplayName();
    String getShortDescription();
    Integer getMetricValue();
    String getDetails();
    MetricSettings getSettings();

    /**
     * needsOtherClasses:  true if this metric is defined using 
     * information from classes other than the one defined by
     * the ClassMetrics object, which can't be determined by
     * inspection.  Another way to think of this method is that
     * it indicates whether the metric is valid for a single class.
     * <p>
     * For example, the NOC metric (Number Of
     * Children) requires that all other classes which possibly
     * subclass from this class must first be scanned, so the NOC
     * metric would return <b>true</b> for this method.  A metric
     * such as RFC (Response For Class) references other classes,
     * but does so bytaking its list of dependencies and
     * scanning those classes as well.  It would therefore return
     * <b>false</b>.
     *
     * Only metrics which return <b>false</b> to this method are
     * displayed in a class' metrics properties page.  Metrics
     * which only make sense in context with other classes are
     * shown in the table created by the "Metrics..." tools command.
     */
    boolean needsOtherClasses();

    /**
     * @return true if metric is available for individual method in class.
     */
    boolean isMethodMetric();

    /**
     * getMetricValue:  returns a metric value for a specified
     * MethodMetrics object.
     *
     * @param mm the MethodMetrics object
     * @return   the metric value
     * @throws   NoSuchMetricException if this metric is not a
     *           method-level metric.
     * @see isMethodMetric
     */
    Integer getMetricValue(MethodMetrics mm) throws NoSuchMetricException;

    int getWarningLevel(MethodMetrics mm) throws NoSuchMetricException;
}

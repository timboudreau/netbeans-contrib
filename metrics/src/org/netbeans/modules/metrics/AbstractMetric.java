/*
 * AbstractMetric.java
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

import java.awt.Component;
import java.awt.Event;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.io.*;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.openide.TopManager;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;
import org.openide.options.SystemOption;
import org.openide.src.ClassElement;
import org.openide.src.Identifier;

public abstract class AbstractMetric implements Metric {
    protected ClassMetrics classMetrics;
    protected Integer metric;
    protected String details;
    private PropertyEditor viewer;

    public AbstractMetric(ClassMetrics classMetrics) {
        this.classMetrics = classMetrics;
    }

    public AbstractMetric() {
    }

    public String toString() {
        return getMetricValue().toString();
    }

    public PropertyEditor getDetailsViewer() {
        if (viewer == null)
            viewer = new DetailsViewer();
        return viewer;
    }

    /**
     * Returns the MetricSettings object associated with this metric.
     */
    public abstract MetricSettings getSettings();

    public int getWarningLevel() {
        int val = getMetricValue().intValue();
        MetricSettings settings = getSettings();
        int warnLevel = settings.getWarningLevel();
        int errLevel = settings.getErrorLevel();

        return val >= errLevel ? Metric.METRIC_FAIL : 
            (val >= warnLevel ? Metric.METRIC_WARN : Metric.METRIC_OKAY);
    }

    public int getWarningLevel(MethodMetrics mm) throws NoSuchMetricException {
	if (!isMethodMetric()) 
	    throw new NoSuchMetricException(getName());

        int val = getMetricValue(mm).intValue();
        MetricSettings settings = getSettings();
        int warnLevel = settings.getMethodWarningLevel();
        int errLevel = settings.getMethodErrorLevel();

        return val >= errLevel ? Metric.METRIC_FAIL : 
            (val >= warnLevel ? Metric.METRIC_WARN : Metric.METRIC_OKAY);
    }

    public void resetMetric() {
        metric = null;
        details = null;
    }

    public abstract String getName();
    public abstract String getDisplayName();
    public abstract String getShortDescription();
    public abstract Integer getMetricValue();
    public abstract String getDetails();

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
     * such as RFC (Response For a Class) references other classes,
     * but does so bytaking its list of dependencies and
     * scanning those classes as well.  It would therefore return
     * <b>false</b>.
     *
     * Only metrics which return <b>false</b> to this method are
     * displayed in a class' metrics properties page.  Metrics
     * which only make sense in context with other classes are
     * shown in the table created by the "Metrics..." tools command.
     */
    public abstract boolean needsOtherClasses();

    /**
     * @return true if metric is available for individual method in class.
     */
    public boolean isMethodMetric() {
	return false;
    }

    public Integer getMetricValue(MethodMetrics mm) throws NoSuchMetricException {
	throw new NoSuchMetricException(getName());
    }

    class DetailsViewer extends PropertyEditorSupport {
        TextPanel text = null;

        DetailsViewer() {
            text = new TextPanel(AbstractMetric.this.getDetails());
        }

        public String getAsText() {
            return getValue().toString();
        }

        public Component getCustomEditor() {
            return text;
        }

        public boolean supportsCustomEditor() {
            return true;
        }
    }
}

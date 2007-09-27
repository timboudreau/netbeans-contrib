/*
 * AbstractMetric.java
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

import org.netbeans.modules.metrics.options.MetricSettings;

import java.awt.*;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import javax.swing.UIManager;

public abstract class AbstractMetric implements Metric {
    protected ClassMetrics classMetrics;
    protected Integer metric;
    protected String details;
    private PropertyEditor viewer;

    protected AbstractMetric(ClassMetrics classMetrics) {
        this.classMetrics = classMetrics;
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

	public boolean isPaintable() {
	    return true;
	}

	public void paintValue(Graphics g, Rectangle r) {
	    Color color;
	    switch (AbstractMetric.this.getWarningLevel()) {
	      case Metric.METRIC_FAIL:
		color = Color.red;
		break;
	      case Metric.METRIC_WARN:
		color = Color.yellow;
		break;
	      default:
		color = Color.black;
	    }
            String text = getAsText();
	    Color oldColor = g.getColor();
            Color bgColor = UIManager.getColor("TextField.background"); //NOI18N
            if (bgColor == null)
                bgColor = Color.white;
            g.setColor(bgColor);
	    g.fillRect(r.x, r.y, r.width, r.height);
	    g.setColor(color);
	    FontMetrics fm = g.getFontMetrics();
	    int h = fm.getAscent() + fm.getDescent();
	    int y = ((r.height - h) / 2) + fm.getAscent() + r.y;
	    g.drawString(text, PAD, y);
	    g.setColor(oldColor);
	}
        private static final int PAD = 3;  // 3 pixel inset

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

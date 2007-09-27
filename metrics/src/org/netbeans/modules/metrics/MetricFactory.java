/*
 * MetricFactory.java
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

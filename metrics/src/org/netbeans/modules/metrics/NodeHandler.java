/*
 * NodeHandler.java
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

import org.openide.nodes.Node;

/**
 * NodeHandler: cookie that gives nodes access to ClassMetrics and
 * MethodMetrics objects.
 */
interface NodeHandler extends Node.Cookie {
    void resetWarningLight();
    String getNodeName();
    int getWarningLevel();
    int getWarningLevel(Metric m);
    int getMetricValue(Metric m);
    Metric[] getMetrics();
    void addApproval(ApprovalsFile appFile, MetricValue mv);
}

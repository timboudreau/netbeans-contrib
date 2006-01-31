/*
 * ApproveMetricsAction.java
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

import org.openide.loaders.DataFolder;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;

public class ApproveMetricsAction extends NodeAction {

    private static final long serialVersionUID = -8004696291292130889L;

    // Enable if one or more classes have metric warnings or failures.
    protected boolean enable(Node[] arr) {
        if ((arr == null) || (arr.length == 0)) 
            return false;
        return true;
    }

    /** Human presentable name of the action. This should be
    * presented as an item in a menu.
    * @return the name of the action
    */
    public String getName() {
        return MetricsNode.bundle.getString("ACT_ApproveMetric");
    }

    /** Help context where to find more about the action.
    * @return the help context for this action
    */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(ApproveMetricsAction.class);
    }

    protected String iconResource(){
        return "org/netbeans/modules/metrics/resources/approved.gif"; //NOI18N
    }

    /**
    * Standard perform action extended by actually activated nodes.
    *
    * @param activatedNodes gives array of actually activated nodes.
    */
    protected void performAction (final Node[] activatedNodes) {
        List approvalNodes = getApprovalNodes(activatedNodes);
        System.out.println("found " + approvalNodes.size() + " nodes");
	List approvalList = createApprovalList(approvalNodes);

	ApproveMetricsPanel panel = new ApproveMetricsPanel(approvalList);

	int ret = JOptionPane.showConfirmDialog(
	    null, panel,
	    MetricsNode.bundle.getString("STR_ApprovalDialogTitle"),
	    JOptionPane.OK_CANCEL_OPTION);

	if (ret == JOptionPane.OK_OPTION) {
	    String approver = System.getProperty("user.name");
	    String comment = panel.getComment();

	    // Open approvals file for this directory.
	    DataFolder df = null;
            Node node = activatedNodes[0];
	    do {
		node = node.getParentNode();  // get directory node
		if (node == null)
		    throw new Error("ApproveMetricAction failure: " +
				"can't find directory");
		df = (DataFolder)node.getCookie(DataFolder.class);
	    } while (df == null);
	    ApprovalsFile appFile = 
		ApprovalsFile.getApprovalsFile(df.getPrimaryFile());
	    
	    Iterator iter = approvalList.iterator();
	    while (iter.hasNext()) {
		PanelLine pl = (PanelLine)iter.next();
                if (pl.checked) {
                    MetricValue mv = new MetricValue(
                         pl.metricClass, pl.value, approver, comment);
                    pl.nodeHandler.addApproval(appFile, mv);
                }
	    }

            iter = approvalNodes.iterator();
            while (iter.hasNext()) {
                NodeHandler nh = (NodeHandler)iter.next();
		nh.resetWarningLight();
            }
	}
    }

    static class PanelLine {
	NodeHandler nodeHandler;
	String nodeName;
	String metricName;
	Class metricClass;
	int value;
        boolean checked;

	PanelLine(NodeHandler nh, String m, Class mClass, int v) {
	    nodeHandler = nh;
	    nodeName = nh.getNodeName();
	    metricName = m;
	    metricClass = mClass;
	    value = v;
            checked = true;
	}
    }

    private List getApprovalNodes(final Node[] arr) {
	List approvalList = new ArrayList();
	getApprovalNodes(arr, approvalList);
	return approvalList;
    }

    // support recursion into node hierarchy
    private void getApprovalNodes(Node[] arr, List approvalList) {
	for (int i = 0; i < arr.length; i++) {
	    NodeHandler nh = (NodeHandler)arr[i].getCookie(NodeHandler.class);
	    if (nh != null) {
                approvalList.add(nh);
            }
            
	    // check child nodes
	    getApprovalNodes(arr[i].getChildren().getNodes(), approvalList);
        }
    }
     
    private List createApprovalList(List approvalNodes) {
        List approvalList = new ArrayList();
        Iterator i = approvalNodes.iterator();
        while (i.hasNext()) {
            NodeHandler nh = (NodeHandler)i.next();
            Metric[] metrics = nh.getMetrics();
            for (int j = 0; j < metrics.length; j++) {
                Metric m = metrics[j];
                int warn = nh.getWarningLevel(m);
                if (warn > Metric.METRIC_OKAY) {
                    PanelLine pl = new PanelLine(nh, 
                                                 m.getDisplayName(), 
                                                 m.getClass(), 
                                                 nh.getMetricValue(m));
                    approvalList.add(pl);
                }
            }
        }
        return approvalList;
    }

    protected boolean asynchronous() {
        return false;
    }
}

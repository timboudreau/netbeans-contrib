/*
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
 */

package org.netbeans.modules.buildmonitor;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeEvent;
import java.util.prefs.NodeChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

/**
 * A button which displays the name of the build, listens to a BuildMonitor
 * and updates any of its status changes by changing its icon.  When clicked, 
 * it launches the build's status URL in a browser window.
 *
 * @author Tom Ball, Jesse Glick
 */
public class BuildStatus extends JButton implements PropertyChangeListener {
    private BuildMonitor monitor;
    private JPopupMenu popupMenu;

    public BuildStatus(BuildMonitor monitor) {
        super(monitor.getName(), monitor.getStatus().getIcon());
        this.monitor = monitor;
        setBorderPainted(false);
        setFocusPainted(false);
        setMargin(new java.awt.Insets(0,0,0,0));
        if (monitor.getStatus() == Status.NO_STATUS_AVAIL) {
            setContentAreaFilled(false);
        }
        setToolTipText(monitor.getStatusDescription());
        setActions();
    }
    
    private void setActions() {
        addActionListener(showReportAction);  // button click -> show report
        popupMenu = new JPopupMenu();
        popupMenu.add(showReportAction);
        popupMenu.add(showBuildAction);
        popupMenu.add(refreshStatusAction);
        popupMenu.addSeparator();
        popupMenu.add(configureAction);
        popupMenu.add(deleteAction);
        popupMenu.add(newAction);
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if(e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            public void mouseReleased(MouseEvent e) {
                if(e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        enableActions();
    }
    
    public void addNotify() {
        super.addNotify();
        monitor.addPropertyChangeListener(this);
    }

    public void removeNotify() {
        super.removeNotify();
        monitor.removePropertyChangeListener(this);
    }

    /**
     * Monitor has signaled a status change.
     */
    public void propertyChange(PropertyChangeEvent e) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                setText(monitor.getName());
                setIcon(monitor.getStatus().getIcon());
                setToolTipText(monitor.getStatusDescription());
                repaint();
                enableActions();
            }
        });
    }
    
    private void enableActions() {
        boolean enabled = monitor.getStatus() != Status.NO_STATUS_AVAIL;
        showReportAction.setEnabled(enabled);
        showBuildAction.setEnabled(enabled);
        // refreshStatusAction is always enabled
    }
        
    /**
     * Displays the current build's summary page.
     */
    private Action showReportAction = new AbstractAction(getString("ACT_REPORT")) { //NOI18N
        public void actionPerformed(ActionEvent e) {
            showURL(monitor.getStatusLink());
        }
    };
    
    /**
     * Displays the continuous build status page.
     */
    private Action showBuildAction = new AbstractAction(getString("ACT_BUILD")) { //NOI18N
        public void actionPerformed(ActionEvent e) {
            showURL(monitor.getBuildLink());
        }
    };

    /**
     * Refreshes the build status.
     */
    private Action refreshStatusAction = new AbstractAction(getString("ACT_REFRESH")) { //NOI18N
        public void actionPerformed(ActionEvent e) {
            refreshStatus();
        }
    };

    /**
     * Action to configure this monitor.
     */
    private final Action configureAction = new AbstractAction(getString("ACT_CONFIGURE")) { // NOI18N
        public void actionPerformed(ActionEvent e) {
            try {
                Node representation = new BeanNode<BuildMonitor>(monitor);
                PropertySheet ps = new PropertySheet();
                ps.setNodes(new Node[] {representation});
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(ps));
            } catch (IntrospectionException x) {
                Exceptions.printStackTrace(x);
            }
        }
    };

    /**
     * Action to delete this monitor.
     */
    private final Action deleteAction = new DeleteAction();
    private final class DeleteAction extends AbstractAction implements NodeChangeListener { // NOI18N
        DeleteAction() {
            super(getString("ACT_DELETE"));
            refreshEnablement();
            BuildMonitorPanel.ROOT.addNodeChangeListener(WeakListeners.create(NodeChangeListener.class, this, BuildMonitorPanel.ROOT));
        }
        public void childAdded(NodeChangeEvent ev) {
            refreshEnablement();
        }
        public void childRemoved(NodeChangeEvent ev) {
            refreshEnablement();
        }
        /** Do not permit last build monitor to be deleted because then we would have no New action! */
        private void refreshEnablement() {
            try {
                setEnabled(BuildMonitorPanel.ROOT.childrenNames().length > 1);
            } catch (BackingStoreException x) {
                Exceptions.printStackTrace(x);
            }
        }
        public void actionPerformed(ActionEvent e) {
            monitor.delete();
        }
    };

    /**
     * Action to add a new build monitor.
     */
    private static final Action newAction = new AbstractAction(getString("ACT_NEW")) { // NOI18N
        public void actionPerformed(ActionEvent e) {
            NotifyDescriptor.InputLine line = new NotifyDescriptor.InputLine(getString("BuildStatus.new.name.text"), getString("BuildStatus.new.title"));
            if (DialogDisplayer.getDefault().notify(line) != NotifyDescriptor.OK_OPTION) {
                return;
            }
            final String name = line.getInputText();
            line = new NotifyDescriptor.InputLine(getString("BuildStatus.new.url.text"), getString("BuildStatus.new.title"));
            if (DialogDisplayer.getDefault().notify(line) != NotifyDescriptor.OK_OPTION) {
                return;
            }
            try {
                String id = name.replaceAll("[^a-zA-Z0-9_-]", "_");
                if (BuildMonitorPanel.ROOT.nodeExists(id)) {
                    for (int i = 2; ; i++) {
                        String alt = id + "_" + i;
                        if (!BuildMonitorPanel.ROOT.nodeExists(alt)) {
                            id = alt;
                            break;
                        }
                    }
                }
                BuildMonitor m = BuildMonitor.create(BuildMonitorPanel.ROOT.node(id));
                m.setName(name);
                m.setURL(new URL(line.getInputText()));
            } catch (Exception x) {
                Exceptions.printStackTrace(x);
            }
        }
    };

    private void showURL(URL url) {
         if (url != null)
            HtmlBrowser.URLDisplayer.getDefault().showURL(url);
    }
    
    private void refreshStatus() {
        monitor.updateBuildStatus();
    }
    
    private static String getString(String key) {
        return NbBundle.getMessage(BuildStatus.class, key);
    }

}

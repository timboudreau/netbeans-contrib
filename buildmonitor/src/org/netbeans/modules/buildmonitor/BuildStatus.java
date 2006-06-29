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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import org.openide.awt.HtmlBrowser;
import org.openide.util.NbBundle;

/**
 * A button which displays the name of the build, listens to a BuildMonitor
 * and updates any of its status changes by changing its icon.  When clicked, 
 * it launches the build's status URL in a browser window.
 *
 * @author Tom Ball
 */
public class BuildStatus extends JButton implements PropertyChangeListener {
    private BuildMonitor monitor;
    private JPopupMenu popupMenu;

    /** Creates a new instance of BuildStatus */
    public BuildStatus(BuildMonitor monitor) {
	super(monitor.getName(), monitor.getStatus().getIcon());
        this.monitor = monitor;
	setBorderPainted(false);
	setFocusPainted(false);
        setMargin(new java.awt.Insets(0,0,0,0));
        if (monitor.getStatus() == Status.NO_STATUS_AVAIL)
            setContentAreaFilled(false);
        setToolTipText(monitor.getStatusDescription());
        setActions();
	monitor.addPropertyChangeListener(this);
    }
    
    private void setActions() {
        addActionListener(showReportAction);  // button click -> show report
        popupMenu = new JPopupMenu();
        popupMenu.add(showReportAction);
        popupMenu.add(showBuildAction);
        popupMenu.add(refreshStatusAction);
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
    
    /**
     * Monitor has signaled a status change.
     */
    public void propertyChange(PropertyChangeEvent e) {
	BuildMonitor monitor = (BuildMonitor)e.getSource();
        final String name = monitor.getName();
	final Icon icon = monitor.getStatus().getIcon();
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
                setName(name);
		setIcon(icon);
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
    
    private void showURL(URL url) {
         if (url != null)
            HtmlBrowser.URLDisplayer.getDefault().showURL(url);
    }
    
    private void refreshStatus() {
        monitor.updateBuildStatus();
    }
    
    private String getString(String key) {
        return NbBundle.getBundle(BuildStatus.class).getString(key);
    }
}

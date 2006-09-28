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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

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

    /**
     * Action to configure this monitor.
     */
    private final Action configureAction = new AbstractAction(getString("ACT_CONFIGURE")) { // NOI18N
        public void actionPerformed(ActionEvent e) {
            String path = monitor.getConfigPath();
            if (path == null) {
                return; // old userdir, probably
            }
            FileObject conf = Repository.getDefault().getDefaultFileSystem().findResource(path);
            if (conf == null) {
                return; // ???
            }
            final Node representation;
            try {
                representation = DataObject.find(conf).getNodeDelegate();
            } catch (DataObjectNotFoundException x) {
                x.printStackTrace();
                return;
            }
            PropertySheet ps = new PropertySheet();
            ps.setNodes(new Node[] {representation});
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(ps));
        }
    };

    /**
     * Action to delete this monitor.
     */
    private final Action deleteAction = new AbstractAction(getString("ACT_DELETE")) { // NOI18N
        private final FileObject dir;
        private final FileChangeListener l = new FileChangeAdapter() {
            public void fileDataCreated(FileEvent fe) {
                refreshEnablement();
            }
            public void fileDeleted(FileEvent fe) {
                refreshEnablement();
            }
        };
        {
            dir = Repository.getDefault().getDefaultFileSystem().findResource("Services/BuildMonitor");
            if (dir != null) {
                refreshEnablement();
                dir.addFileChangeListener(FileUtil.weakFileChangeListener(l, dir));
            }
        }
        /** Do not permit last build monitor to be deleted because then we would have no New action! */
        private void refreshEnablement() {
            setEnabled(dir.getChildren().length > 1);
        }
        public void actionPerformed(ActionEvent e) {
            String path = monitor.getConfigPath();
            if (path == null) {
                return;
            }
            FileObject conf = Repository.getDefault().getDefaultFileSystem().findResource(path);
            if (conf == null) {
                return;
            }
            try {
                conf.delete();
            } catch (IOException x) {
                x.printStackTrace();
            }
        }
    };

    /**
     * Action to add a new build monitor.
     */
    private static final Action newAction = new AbstractAction(getString("ACT_NEW")) { // NOI18N
        public void actionPerformed(ActionEvent e) {
            final FileObject dir = Repository.getDefault().getDefaultFileSystem().findResource("Services/BuildMonitor");
            if (dir == null) {
                return; // ???
            }
            NotifyDescriptor.InputLine line = new NotifyDescriptor.InputLine(getString("BuildStatus.new.name.text"), getString("BuildStatus.new.title"));
            if (DialogDisplayer.getDefault().notify(line) != NotifyDescriptor.OK_OPTION) {
                return;
            }
            final String name = line.getInputText();
            line = new NotifyDescriptor.InputLine(getString("BuildStatus.new.url.text"), getString("BuildStatus.new.title"));
            if (DialogDisplayer.getDefault().notify(line) != NotifyDescriptor.OK_OPTION) {
                return;
            }
            final String url = line.getInputText();
            try {
                final URL u = new URL(url);
                dir.getFileSystem().runAtomicAction(new FileSystem.AtomicAction() {
                    public void run() throws IOException {
                        FileObject nue = dir.createData(FileUtil.findFreeFileName(dir, name.replaceAll("[^a-zA-Z0-9_-]", "_"), "settings"), "settings");
                        nue.setAttribute("name", name);
                        nue.setAttribute("url", u);
                        nue.setAttribute("minutes", new Integer(30));
                        InputStream is = BuildStatus.class.getResourceAsStream("resources/buildMonitor.xml");
                        try {
                            OutputStream os = nue.getOutputStream();
                            try {
                                FileUtil.copy(is, os);
                            } finally {
                                os.close();
                            }
                        } finally {
                            is.close();
                        }
                    }
                });
            } catch (IOException x) {
                x.printStackTrace();
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

/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.bugs.bugzilla;

import java.util.LinkedList;
import java.net.URL;
import java.net.MalformedURLException;
import java.text.MessageFormat;

import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

import org.netbeans.modules.tasklist.core.TaskListView;
import org.netbeans.modules.tasklist.bugs.*;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.StatusDisplayer;

import javax.swing.*;

/**
 * Bridge which provides Bugzilla data to the BugList
 * This class is almost exactally the same as IZBugEngine.
 *
 * @author Tor Norbye, serff
 */
public class BZBugEngine implements BugEngine { // XXX remove the publicness

    public BZBugEngine() {
    }

    /**
     * Return the user name of the engine
     */
    public String getName() {
        return (NbBundle.getMessage(BZBugEngine.class, "BugZilla")); // NOI18N;
    }

    public JComponent getQueryCustomizer(BugQuery query, boolean edit) {
        return new BugzillaQueryPanel(query, edit);
    }

    public void refresh(final BugQuery query, final BugList list) {
        // Do in the background
        RequestProcessor.postRequest(new Runnable() {
            public void run() {
                doRefresh(query, list);
            }
        });
    }

    /**
     * TODO Change this so it adds pugs to the model one at a time.  I hate waiting...
     */
    public void doRefresh(BugQuery inQuery, BugList list) {
        TaskListView v = TaskListView.getCurrent();
        BugsView view = null;
        if (v instanceof BugsView) {
            view = (BugsView) v;
            view.setCursor(Utilities.createProgressCursor(view));
        }
        try {
            // Do a bug query
            String baseurl = inQuery.getBaseUrl() + "/buglist.cgi?";
            String query = inQuery.getQueryString();
            if ((baseurl == null || baseurl.equals("")) || (query == null || query.equals(""))) {
                //They didn't enter anything on the gui
                StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(BZBugEngine.class,
                        "BadQuery")); // NOI18N
                return;
            }
            System.out.println("Baseurl = " + baseurl + " query = " + query);

            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(BZBugEngine.class,
                    "Refreshing")); // NOI18N
            URL url = null;
            try {
                url = new URL(baseurl);
            } catch (MalformedURLException e) {
                ErrorManager.getDefault().notify(e);
            }
            if (url != null) {
                Bugzilla bz = new Bugzilla(url);
                try {
                    StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(BZBugEngine.class,
                            "DoingQuery")); // NOI18N
                    int bugids[] = bz.query(query);

                    // Provide some update

                    int n = bugids.length;
                    LinkedList issues = new LinkedList();
                    for (int i = 0; i < n; i++) {
                        StatusDisplayer.getDefault().setStatusText(MessageFormat.format(NbBundle.getMessage(BZBugEngine.class,
                                "QueryingBug"), // NOI18N
                                new String[]{Integer.toString(bugids[i])}));

                        Issue izbug = bz.getBug(bugids[i]);

                        Bug bug = new Bug(Integer.toString(izbug.getId()),
                                izbug.getSummary(),
                                izbug.getPriority(),
                                izbug.getType(),
                                izbug.getComponent(),
                                izbug.getSubcomponent(),
                                izbug.getCreated(),
                                "",
                                izbug.getAssignedTo(),
                                izbug.getReportedBy(),
                                izbug.getStatus(),
                                izbug.getTargetMilestone(),
                                0);
                        bug.setEngine(this);

                        issues.add(bug);
                    }

                    // Successful list fetch -- replace the contents
                    list.setBugs(issues);
                } catch (org.xml.sax.SAXException se) {
                    ErrorManager.getDefault().notify(se);
                    System.out.println("Couldn't read bug list: sax exception");
                } catch (java.net.UnknownHostException uhe) {
                    StatusDisplayer.getDefault().setStatusText(MessageFormat.format(NbBundle.getMessage(BZBugEngine.class,
                            "NoNet"), // NOI18N
                            new String[]{baseurl}));

                } catch (java.io.IOException ioe) {
                    ErrorManager.getDefault().notify(ioe);
                    System.out.println("Couldn't read bug list: io exception");
                }
                StatusDisplayer.getDefault().setStatusText("");
            }
        } finally {
            if (view != null) {
                view.setCursor(null);
            }
        }
    }

    /**
     * View a particular bug.
     */
    public void viewBug(Bug bug, String server) {
//	String urlstring = "http://192.168.3.3/bugzilla/show_bug.cgi?id=" + bug.getId();
//        String urlstring = "http://bugzilla.mozilla.org/show_bug.cgi?id=" + bug.getId();
        // Show URL
        try {
            // XXX why server/service doe not contain bugzilla too?
            URL url = new URL(new URL(server), "bugzilla/show_bug.cgi?id=" + bug.getId());
            HtmlBrowser.URLDisplayer.getDefault().showURL(url);
        } catch (MalformedURLException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
}

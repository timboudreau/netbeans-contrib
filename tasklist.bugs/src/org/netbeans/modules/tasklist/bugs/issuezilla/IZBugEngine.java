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

package org.netbeans.modules.tasklist.bugs.issuezilla;

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
import org.netbeans.modules.tasklist.bugs.bugzilla.BugzillaQueryPanel;
import org.netbeans.modules.tasklist.bugs.bugzilla.SourcePanel;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.StatusDisplayer;

import javax.swing.*;

/**
 * Bridge which provides Issuezilla data to the BugList
 *
 * @author Tor Norbye
 */
public final class IZBugEngine implements BugEngine { // XXX remove the publicness

    public IZBugEngine() {
    }

    /**
     * Return the user name of the engine
     */
    public String getName() {
        return (NbBundle.getMessage(IZBugEngine.class, "IssueZilla")); // NOI18N;
    }

    public void refresh(final BugQuery query, final BugList list) {
        // Do in the background
        RequestProcessor.postRequest(new Runnable() {
            public void run() {
                doRefresh(query, list);
            }
        });
    }

    public JComponent getQueryCustomizer(BugQuery query, boolean edit) {
        return new SourcePanel();
    }

    public void doRefresh(BugQuery inQuery, BugList list) {
        TaskListView v = TaskListView.getCurrent();
        BugsView view = null;
        if (v instanceof BugsView) {
            view = (BugsView) v;
            view.setCursor(Utilities.createProgressCursor(view));
        }
        try {

            // Do a bug query
            String query = null;
            String baseurl = null;
//        query = System.getProperty("netbeans.tasklist.bugquery");
//        if (query == null) {
//            // TEMPORARY HACK     TODO
//            TopManager.getDefault().notify(new NotifyDescriptor.Message("Tasklist bug summary:\nAdd -J-Dnetbeans.tasklist.bugquery=http://your.bug.url?<query>\nto your runide.sh startup arguments. (This is\nobviously only a temporary hack solution until\nI've added a query customizer.)\n\nTo determine what query to use, go to issuezilla:\n   http://www.netbeans.org/issues/query.cgi\nand create a custom query. Then, add that query as <query> above.\n\nAnd don't forget to make sure to set your\nproxies if you're behind a firewall! You can do that through Tools -> Setup Wizard."));
//            return;
//        } else {
//            //get the baseurl
//            int index = query.indexOf("?");
//            if (index != -1) {
//                baseurl = query.substring(0, index + 1);
//                query = query.substring(index + 1);
//            } else {
//                //we have to have the URL
//                TopManager.getDefault().notify(new NotifyDescriptor.Message("Tasklist bug summary:\nAdd -J-Dnetbeans.tasklist.bugquery=You must have the full URL for this query (Ex. http://www.netbeans.org/issues/buglist.cgi?<query>"));
//            }
//        }
            baseurl = inQuery.getBaseUrl() + "/buglist.cgi?";
            query = inQuery.getQueryString();

            if ((baseurl == null || baseurl.equals("")) || (query == null || query.equals(""))) {
                //They didn't enter anything on the gui
                StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(IZBugEngine.class,
                        "BadQuery")); // NOI18N
                return;
            }

            //String query= "issue_type=DEFECT&component=projects&issue_status=UNCONFIRMED&issue_status=NEW&issue_status=STARTED&issue_status=REOPENED&version=4.0+dev&email1=&emailtype1=substring&emailassigned_to1=1&email2=&emailtype2=substring&emailreporter2=1&issueidtype=include&issue_id=&changedin=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&short_desc=&short_desc_type=substring&long_desc=&long_desc_type=substring&issue_file_loc=&issue_file_loc_type=substring&status_whiteboard=&status_whiteboard_type=substring&keywords=&keywords_type=anywords&field0-0-0=noop&type0-0-0=noop&value0-0-0=&cmdtype=doit&newqueryname=&order=Reuse+same+sort+as+last+time";


            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(IZBugEngine.class,
                    "Refreshing")); // NOI18N
            URL url = null;
            try {
                url = new URL(baseurl);
            } catch (MalformedURLException e) {
                ErrorManager.getDefault().notify(e);
            }
            if (url != null) {
                Issuezilla iz = new Issuezilla(url);
                try {
                    StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(IZBugEngine.class,
                            "DoingQuery")); // NOI18N

                    int bugids[] = iz.query(query);

                    // Provide some update

                    int n = bugids.length;
                    LinkedList issues = new LinkedList();
                    for (int i = 0; i < n; i++) {
                        StatusDisplayer.getDefault().setStatusText(MessageFormat.format(NbBundle.getMessage(IZBugEngine.class,
                                "QueryingBug"), // NOI18N
                                new String[]{Integer.toString(bugids[i])}));

                        Issue izbug = iz.getBug(bugids[i]);

                        Bug bug = new Bug(Integer.toString(izbug.getId()),
                                izbug.getSummary(),
                                izbug.getPriority(),
                                izbug.getType(),
                                izbug.getComponent(),
                                izbug.getSubcomponent(),
                                izbug.getCreated(),
                                izbug.getKeywords(),
                                izbug.getAssignedTo(),
                                izbug.getReportedBy(),
                                izbug.getStatus(),
                                izbug.getTargetMilestone(),
                                izbug.getVotes());
                        bug.setEngine(this);

                        issues.add(bug);
                    }

                    // Successful list fetch -- replace the contents
                    list.setBugs(issues);
                } catch (org.xml.sax.SAXException se) {
                    ErrorManager.getDefault().notify(se);
                    System.out.println("Couldn't read bug list: sax exception");
                } catch (java.net.UnknownHostException uhe) {
                    StatusDisplayer.getDefault().setStatusText(MessageFormat.format(NbBundle.getMessage(IZBugEngine.class,
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
    public void viewBug(Bug bug, String service) {
//	String urlstring = "http://www.netbeans.org/issues/show_bug.cgi?id=" +
//	    bug.getId();
        // Show URL
        try {
            URL url = new URL(new URL(service), "issues/show_bug.cgi?id=" + bug.getId());
            HtmlBrowser.URLDisplayer.getDefault().showURL(url);
        } catch (MalformedURLException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
}

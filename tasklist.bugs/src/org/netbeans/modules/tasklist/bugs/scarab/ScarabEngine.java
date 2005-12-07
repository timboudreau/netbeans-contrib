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

package org.netbeans.modules.tasklist.bugs.scarab;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.net.URL;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.List;

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
 * Bridge which provides Scarab data to the BugList
 * This class is almost exactally the same as IZBugEngine.
 * 
 * @author Tor Norbye, serff
 */
public final class ScarabEngine implements BugEngine {

    public ScarabEngine() {
    }

    /**
     * Return the user name of the engine
     */
    public String getName() {
        return (NbBundle.getMessage(ScarabEngine.class, "Scarab")); // NOI18N;
    }

    public JComponent getQueryCustomizer(final BugQuery query, 
            final boolean edit) {
        
        return new SourcePanel();
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
     * 
     */
    public void doRefresh(final BugQuery inQuery, final BugList list) {
        if( !(inQuery instanceof ScarabBugQuery) ){
            throw new IllegalArgumentException("ScarabEngine.doRefresh only excepts ScarabBugQuery argument"); //NOI18N
        }
        final ScarabBugQuery sbQuery = (ScarabBugQuery)inQuery;
        final TaskListView v = TaskListView.getCurrent();
        BugsView view = null;
        if (v instanceof BugsView) {
            view = (BugsView) v;
            view.setCursor(Utilities.createProgressCursor(view));
        }
        try {
            if ((inQuery.getBaseUrl()  == null || inQuery.getBaseUrl().equals(""))  //NOI18N
                    || (inQuery.getQueryString() == null || inQuery.getQueryString().equals(""))) { //NOI18N
                //They didn't enter anything on the gui
                StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(ScarabEngine.class,
                        "BadQuery")); // NOI18N
                return;
            }            
            // Do a bug query
            final String baseurl = inQuery.getBaseUrl();
            final String query = "downloadtype=1&go="+inQuery.getQueryString(); //NOI18N

            System.out.println("Baseurl = " + baseurl + " query = " + query); //NOI18N

            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(ScarabEngine.class,
                    "Refreshing")); // NOI18N
            URL url = null;
            try {
                url = new URL(baseurl);
            } catch (MalformedURLException e) {
                ErrorManager.getDefault().notify(e);
            }
            if (url != null) {
                final Scarab scarab = new Scarab(url);
                try {
                    StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(ScarabEngine.class,
                            "DoingQuery")); // NOI18N

                    // Successful list fetch -- replace the contents
                    final List issues = scarab.query(query);
                    final List bugs = new ArrayList();
                    for (Iterator it = issues.iterator(); it.hasNext();) {
                        
                        final Issue issue = (Issue)it.next();
                        StatusDisplayer.getDefault().setStatusText(MessageFormat.format(NbBundle.getMessage(ScarabEngine.class,
                                "QueryingBug"), // NOI18N
                                new String[]{issue.getId()}));

                        final Object priorityObj = issue.getAttribute(sbQuery.getAttributeName(Issue.PRIORITY));
                        final int priority = ( priorityObj != null && priorityObj instanceof Number)
                            ? ((Number)priorityObj).intValue()
                            : 0;
                        final Object votesObj = issue.getAttribute(sbQuery.getAttributeName(Issue.VOTES));
                        final int votes = ( votesObj != null && votesObj instanceof Number)
                            ? ((Number)votesObj).intValue()
                            : 0;
                        
                        final Bug bug = new Bug(issue.getId(),
                                (String) issue.getAttribute(sbQuery.getAttributeName(Issue.SUMMARY)),
                                priority,
                                issue.getType(),
                                (String) issue.getAttribute(sbQuery.getAttributeName(Issue.COMPONENT)),
                                (String) issue.getAttribute(sbQuery.getAttributeName(Issue.SUBCOMPONENT)),
                                issue.getCreated(),
                                (String) issue.getAttribute(sbQuery.getAttributeName(Issue.KEYWORDS)),
                                (String) issue.getAttribute(sbQuery.getAttributeName(Issue.ASSIGNED_TO)),
                                issue.getReportedBy(),
                                (String) issue.getAttribute(sbQuery.getAttributeName(Issue.STATUS)),
                                (String) issue.getAttribute(sbQuery.getAttributeName(Issue.TARGET)),
                                votes);
                        bug.setEngine(this);

                        bugs.add(bug);
                    }
                    list.setBugs(bugs);
                    
                } catch (org.xml.sax.SAXException se) {
                    ErrorManager.getDefault().notify(se);
                    System.out.println("Couldn't read bug list: sax exception"); //NOI18N
                } catch (java.net.UnknownHostException uhe) {
                    StatusDisplayer.getDefault().setStatusText(MessageFormat.format(NbBundle.getMessage(ScarabEngine.class,
                            "NoNet"), // NOI18N
                            new String[]{baseurl}));

                } catch (java.io.IOException ioe) {
                    ErrorManager.getDefault().notify(ioe);
                    System.out.println("Couldn't read bug list: io exception"); //NOI18N
                }
                StatusDisplayer.getDefault().setStatusText(""); //NOI18N
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
    public void viewBug(final Bug bug, final String server) {
        // Show URL
        try {
            // XXX why server/service doe not contain bugzilla too?
            final URL url = new URL(new URL(server), "id/" + bug.getId());
            HtmlBrowser.URLDisplayer.getDefault().showURL(url);
        } catch (MalformedURLException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
}

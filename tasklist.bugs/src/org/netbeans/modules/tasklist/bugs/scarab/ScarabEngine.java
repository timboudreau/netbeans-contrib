/*
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

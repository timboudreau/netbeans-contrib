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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import javax.swing.plaf.basic.BasicFileChooserUI;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.HashMap;
import java.net.URL;
import java.net.MalformedURLException;
import java.text.MessageFormat;

import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.TopManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.Repository;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.XMLDataObject;
import org.openide.util.NbBundle;
import org.openide.cookies.InstanceCookie;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.openide.xml.*;

import org.netbeans.modules.tasklist.core.TaskListView;
import org.netbeans.modules.tasklist.bugs.*;

/**
 * Bridge which provides Bugzilla data to the BugList
 * This class is almost exactally the same as IZBugEngine.  
 * @author Tor Norbye, serff
 */
public class BZBugEngine implements BugEngine { // XXX remove the publicness

    private BugList list = null;
    
    public BZBugEngine(BugList list) {
	this.list = list;
    }

    /** Return the user name of the engine */
    public String getName() {
	return (NbBundle.getMessage(BZBugEngine.class, "BugZilla")); // NOI18N;
    }
    
    public void refresh(final BugQuery query) {
        // Do in the background
        RequestProcessor.postRequest(new Runnable() {
                public void run() {
		    doRefresh(query);
		}
	    }
        );
    }

    /** 
     * TODO Change this so it adds pugs to the model one at a time.  I hate waiting...
     */
    public void doRefresh(BugQuery inQuery) {
        TaskListView v = TaskListView.getCurrent();
        BugsView view = null;
        if (v instanceof BugsView) {
            view = (BugsView)v;
            view.setCursor(Utilities.createProgressCursor(view));
        }
        try {
            // Do a bug query
            String baseurl = inQuery.getBaseUrl() + "/buglist.cgi?";
            String query = inQuery.getQueryString();
            if ((baseurl == null || baseurl.equals("")) || (query == null || query.equals(""))) {
                //They didn't enter anything on the gui
                TopManager.getDefault().setStatusText(
                                    NbBundle.getMessage(BZBugEngine.class, 
                                                  "BadQuery")); // NOI18N
                return;
            }
            System.out.println("Baseurl = " + baseurl + " query = " + query);

            TopManager.getDefault().setStatusText(
                              NbBundle.getMessage(BZBugEngine.class, 
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
                    TopManager.getDefault().setStatusText(
                                    NbBundle.getMessage(BZBugEngine.class, 
                                                  "DoingQuery")); // NOI18N
                    int bugids[] = bz.query(query);

                    // Provide some update

                    int n = bugids.length;
                    LinkedList issues = new LinkedList();
                    for (int i = 0; i < n; i++) {
                        TopManager.getDefault().setStatusText(
                                       MessageFormat.format(
                                        NbBundle.getMessage(BZBugEngine.class, 
                                                 "QueryingBug"), // NOI18N
                                        new String[] { Integer.toString(bugids[i]) }));

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
                    TopManager.getDefault().setStatusText(
                                       MessageFormat.format(
                                        NbBundle.getMessage(BZBugEngine.class, 
                                                 "NoNet"), // NOI18N
                                        new String[] { baseurl }));

                } catch (java.io.IOException ioe) {
                    ErrorManager.getDefault().notify(ioe);
                    System.out.println("Couldn't read bug list: io exception");
                }
                TopManager.getDefault().setStatusText("");
            }
        } finally {
            if (view != null) {
                view.setCursor(null);
            }
        }
    }

    /** View a particular bug. */
    public void viewBug(Bug bug) {
//	String urlstring = "http://192.168.3.3/bugzilla/show_bug.cgi?id=" + bug.getId();
        String urlstring = "http://bugzilla.mozilla.org/show_bug.cgi?id=" + bug.getId();
	// Show URL
	try {
	    URL url = new URL(urlstring);
	    TopManager.getDefault().showUrl(url);
	} catch (MalformedURLException e) {
	    TopManager.getDefault().getErrorManager().notify(e);
	}	
    }
}

/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.checkstyle;

import org.netbeans.api.tasklist.*;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;
import java.util.Properties;
import java.io.File;
import org.openide.cookies.SourceCookie;
import org.openide.explorer.view.*;
import org.openide.nodes.*;
import org.openide.filesystems.FileUtil;
import org.openide.ErrorManager;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.text.DataEditorSupport;

import com.puppycrawl.tools.checkstyle.*;
import com.puppycrawl.tools.checkstyle.api.*;

import org.netbeans.modules.tasklist.core.TLUtils;

/**
 * This class uses the Checkstyle rule checker to provide rule violation
 * suggestions.
 * <p>
 * @todo This version only operates on the disk-versions of
 *   the source files! Either get checkstyle modified to
 *   have a Reader interface, or save files to temporary buffers.
 * @todo Add automatic fixers for some of these rules.
 * @todo Refactor so I can share some code with the pmd bridge
 * <p>
 * @author Tor Norbye
 */


public class ViolationProvider extends DocumentSuggestionProvider
    implements AuditListener {

    final private static String TYPE = "checkstyle-violations"; // NOI18N

    public String[] getTypes() {
        return new String[] { TYPE };
    }

    // javadoc in super()
    public void rescan(SuggestionContext env, Object request) {
        try {
            dataobject = DataObject.find(env.getFileObject());
        } catch (DataObjectNotFoundException e) {
            e.printStackTrace();
        }
        document = env.getDocument();
        this.env = env;
        this.request = request;
        List newTasks = scan(env);
        SuggestionManager manager = SuggestionManager.getDefault();

        if ((newTasks == null) && (showingTasks == null)) {
            return;
        }
        manager.register(TYPE, newTasks, showingTasks, request);
        showingTasks = newTasks;
    }

    void rescan() {
        rescan(env, request);
    }

    /** List "owned" by the scan() method and updated by the audit listener
     * methods. */
    private List tasks = null;
    
    public List scan(SuggestionContext env) {
        tasks = null;

        SuggestionManager manager = SuggestionManager.getDefault();
        if (!manager.isEnabled(TYPE)) {
            return null;
        }

        /* This code is for looking up the dynamic content of the
           document - but see below - we don't need it yet...
        SourceCookie cookie =
            (SourceCookie)dobj.getCookie(SourceCookie.class);

        // The file is not a java file
        if(cookie == null) {
            return null;
        }
        String text = null;
        try {
            int len = doc.getLength();
            text = doc.getText(0, len);
        } catch (BadLocationException e) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
            return null;
        }
        Reader reader = new StringReader(text);
        //String name = cookie.getSource().getClasses()[0].getName().getFullName();
        */
        
        // YUCK! This is a hack! It will operate on the saved file,
        // not the buffer contents! However, checkstyle doesn't seem to
        // have an API where I can pass in a string reader - it wants to
        // read the files directly! XXX
        File file = FileUtil.toFile(env.getFileObject());
        if (file != null) {
            try {
                // TODO: this should only be done once, not for each scan!!!
                Checker checker = new Checker();
                ModuleFactory moduleFactory = null;
                checker.setModuleFactory(moduleFactory);
                Configuration config = null;
                Properties props = System.getProperties();
                try {
                    // For now, grab the configuration from the module
                    File f = org.openide.modules.InstalledFileLocator.getDefault().locate("configs/checkstyle.xml", "org.netbeans.modules.tasklist.checkstyle", false);
                    //System.out.println("FILE LOCATED = " + f);
                    if (f == null) {
                        ErrorManager.getDefault().log("Couldn't find configs/checkstyle.xml");
                        return null;
                    }
                    config = ConfigurationLoader.loadConfiguration(f.getPath(), new PropertiesExpander(props));
                } catch (CheckstyleException e) {
                    ErrorManager.getDefault().notify(e);
                    return null;
                }                                             
                checker.configure(config);
                checker.addListener(this);
                checker.process(new File[] { file }); // Yuck!
            } catch (Exception e) {
                ErrorManager.getDefault().notify(e);
            }
        }
        return tasks;
    }
    
    public void clear(SuggestionContext env,
                      Object request) {
        if (showingTasks != null) {
            SuggestionManager manager = SuggestionManager.getDefault();
            manager.register(TYPE, null, showingTasks, request);
	    showingTasks = null;
	}     
    }


    // Implements AuditListener
    /**
     * notify that the audit is about to start
     * @param aEvt the event details
     */
    public void auditStarted(AuditEvent aEvt) {
        //System.out.println("audidStarted(" + aEvt + ")");
    }

    /**
     * notify that the audit is finished
     * @param aEvt the event details
     */
    public void auditFinished(AuditEvent aEvt) {
        //System.out.println("audidFinished(" + aEvt + ")");
    }

    /**
     * notify that audit is about to start on a specific file
     * @param aEvt the event details
     */
    public void fileStarted(AuditEvent aEvt) {
        //System.out.println("fileStarted(" + aEvt + ")");
    }

    /**
     * notify that audit is finished on a specific file
     * @param aEvt the event details
     */
    public void fileFinished(AuditEvent aEvt) {
        //System.out.println("fileFinished(" + aEvt + ")");
    }

    /**
     * notify that an exception happened while performing audit
     * @param aEvt the event details
     * @param aThrowable details of the exception
     */
    public void addException(AuditEvent aEvt, Throwable aThrowable) {
        ///System.out.println("addException(" + aEvt + "," + aThrowable + ")");
    }
    
    
    /**
     * notify that an audit error was discovered on a specific file
     * @param aEvt the event details
     */
    public void addError(AuditEvent aEvt) {
        //System.out.println("addError(" + aEvt + ")");
        
        try {
            // Violation line numbers seem to be 0-based
            final Line line = TLUtils.getLineByNumber(dataobject, aEvt.getLine());
                    
            SuggestionPerformer action = null;
            action = null;
            String description = aEvt.getLocalizedMessage().getMessage();
                    
            SuggestionManager manager = SuggestionManager.getDefault();
            Suggestion s = manager.createSuggestion(
                        TYPE,
                        description,
                        action,
                        this);

            SeverityLevel sv = aEvt.getSeverityLevel();
            if (sv == SeverityLevel.IGNORE) {
                s.setPriority(SuggestionPriority.LOW);
            } else if (sv == SeverityLevel.INFO) {
                s.setPriority(SuggestionPriority.MEDIUM_LOW);
            } else if (sv == SeverityLevel.WARNING) {
                s.setPriority(SuggestionPriority.MEDIUM);
            } else if (sv == SeverityLevel.ERROR) {
                // Even most of the errors seem pretty tame - "line longer than
                // 80 characters", etc. - so make these medium as well.
                // Would be nice if Checkstyle would be more careful with
                // the use of the ERROR level.
                s.setPriority(SuggestionPriority.MEDIUM);
            } 
            
            s.setLine(line);
            if (tasks == null) {
                tasks = new ArrayList(40); // initial guess
            }
            tasks.add(s);
        } catch (Exception e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    
    /** The list of tasks we're currently showing in the tasklist */
    private List showingTasks = null;

    private SuggestionContext env;
    private DataObject dataobject = null;
    private Document document = null;
    private Object request = null;
}

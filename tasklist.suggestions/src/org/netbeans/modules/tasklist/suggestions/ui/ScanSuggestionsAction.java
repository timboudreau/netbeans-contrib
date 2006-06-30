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

package org.netbeans.modules.tasklist.suggestions.ui;


import java.io.IOException;
import javax.swing.text.Document;
import javax.swing.*;

import org.netbeans.modules.tasklist.client.SuggestionManager;
import org.netbeans.modules.tasklist.core.TaskList;
import org.netbeans.modules.tasklist.suggestions.ui.SuggestionsView;
import org.netbeans.modules.tasklist.suggestions.SuggestionList;
import org.netbeans.modules.tasklist.suggestions.SuggestionsScanner;
import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.actions.CookieAction;
import org.openide.util.Utilities;
import org.openide.util.RequestProcessor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/** 
 * Scan a set of directories for suggestions
 *
 * @todo Provide a progress dialog, with a Cancel button
 * @todo Modifications to files opened in the background should
 *      get saved!
 *
 * @todo It's probably overkill for me to go and open the Documents
 *   for every file I'm scanning; a buffered file reader inside of
 *   a StringReader would do the trick, provided SourceScanner would
 *   accept a String argument. That's a simple refactoring operation
 *   since most of the internals in the SourceScanner is dealing with
 *   the String value of the document. I should probably do a getDocument
 *   though in case a file is already open - that way I get the current
 *   (edited, not saved) version of the file which is what we want - or
 *   it not?
 *
 * @author Tor Norbye 
 */
public class ScanSuggestionsAction extends CookieAction {

    private static final long serialVersionUID = 1;

    protected boolean asynchronous() {
        return true;
    }

    protected void performAction(final Node[] nodes) {
        if (nodes == null) {
            return;
        }

        final SuggestionsView[] view = new SuggestionsView[] {null};
        try {
            final SuggestionList list = new SuggestionList();
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    // The category should be DIFFERENT from the category used
                    // for the default suggestion view (the active scanning view)
                    // such that the "Show Suggestions View" action does not
                    // locate and reuse these windows - and so they can have different
                    // column configurations (filename is not useful in the active
                    // suggestions view window, but is critical in the directory
                    // scan for example.)
                    view[0] = new SuggestionsView("suggestionsscan", // NOI18N
                        NbBundle.getMessage(ScanSuggestionsAction.class,
                            "ScannedTasks"), // NOI18N
                        list, false,
                        "org/netbeans/modules/tasklist/suggestions/folderscan.gif"); // NOI18N
                    view[0].showInMode();
                    view[0].setCursor(Utilities.createProgressCursor(view[0]));
                }
            });

            DataObject.Container[] folders = new DataObject.Container[nodes.length];
            for (int i = 0; i < nodes.length; i++) {
                folders[i] = (DataObject.Container) nodes[i].getCookie(DataObject.Container.class);
            }
            SuggestionsScanner.getDefault().scan(folders, list, null);
            
            Integer count = new Integer(list.size());
            StatusDisplayer.getDefault ().setStatusText(
                NbBundle.getMessage(ScanSuggestionsAction.class,
                                   "ScanDone", count)); // NOI18N
        } finally {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    if (view[0] != null) {
                        view[0].setCursor(null);
                    }
                }
            });
        }
    }

    public String getName() {
        return NbBundle.getMessage(ScanSuggestionsAction.class, "ScanDir"); // NOI18N
    }

    protected String iconResource() {
        return "org/netbeans/modules/tasklist/suggestions/ui/scanDir.gif"; // NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected int mode() {
        return MODE_ALL;
    }
    
    protected Class[] cookieClasses() {
        return new Class[] { DataObject.Container.class };
    }

}

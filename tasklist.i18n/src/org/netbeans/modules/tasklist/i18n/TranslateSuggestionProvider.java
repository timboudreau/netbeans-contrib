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

package org.netbeans.modules.tasklist.i18n;

import java.text.MessageFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.api.mdr.MDRepository;
import org.netbeans.jmi.javamodel.Element;
import org.netbeans.jmi.javamodel.Resource;
import org.netbeans.jmi.javamodel.StringLiteral;
import org.netbeans.modules.javacore.api.JavaModel;

import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle;

import org.netbeans.modules.tasklist.client.*;
import org.netbeans.modules.tasklist.providers.DocumentSuggestionProvider;
import org.netbeans.modules.tasklist.providers.SuggestionContext;
import org.netbeans.modules.tasklist.core.TLUtils;
import org.netbeans.modules.tasklist.core.util.TextPositionsMapper;
import org.openide.text.NbDocument;


/**
 * This class scans for Java string tokens without a // NOI18N comment
 *
 * @author tl
 */
public class TranslateSuggestionProvider extends DocumentSuggestionProvider {
    public final static String TYPE = "nb-tasklist-i18n"; // NOI18N
    public static final Logger LOGGER = TLUtils.getLogger(TranslateSuggestionProvider.class);
    private static final String TEXT = NbBundle.getMessage(
        TranslateSuggestionProvider.class, "ProblemText"); // NOI18N

    static {
        LOGGER.setLevel(Level.FINE);
        Thread t = new Thread(new TranslateOpenProjectsScanner());
        t.setPriority(Thread.MIN_PRIORITY);
        t.setDaemon(true);
        t.start();
    }    

    /** The list of tasks we're currently showing in the tasklist */
    private List showingTasks = null;

    public TranslateSuggestionProvider() {
    }
    
    public String getType() {
        return TYPE;
    }

    public void rescan(SuggestionContext env, Object request) {
        List newTasks = scan(env);
        SuggestionManager manager = SuggestionManager.getDefault();

        if ((newTasks == null) && (showingTasks == null)) {
            return;
        }
        manager.register(TYPE, newTasks, showingTasks);
        showingTasks = newTasks;
    }

    public List scan(SuggestionContext env) {
        SuggestionManager manager = SuggestionManager.getDefault();
        if (!manager.isEnabled(TYPE)) {
            return null;
        }
        TranslateFileChecker c = new TranslateFileChecker(env.getFileObject());
        TranslateFileChecker.Error[] err = c.run();
        List tasks = new ArrayList(err.length);
        
        for (int i = 0; i < err.length; i++) {
            SuggestionPerformer action = new AddI18NCommentPerformer();
            String text = MessageFormat.format(TEXT, new Object[] {
                err[i].constant
            });
            SuggestionAgent problem = SuggestionManager.getDefault().
                createSuggestion(null, TYPE, text, action, null);
            try {
                DataObject dataObject = DataObject.find(env.getFileObject());
                problem.setLine(TLUtils.getLineByNumber(dataObject, 
                    err[i].line + 1));
            } catch (DataObjectNotFoundException e) {
                // ignore
                ErrorManager.getDefault().notify(e);
            }
            tasks.add(problem.getSuggestion());
        }
        
        return tasks;
    }

    public void clear(SuggestionContext env,
                      Object request) {
        // Remove existing items
        if (showingTasks != null) {
            SuggestionManager manager = SuggestionManager.getDefault();
            manager.register(TYPE, null, showingTasks);
            showingTasks = null;
        }
    }

}

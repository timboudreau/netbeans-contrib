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

package org.netbeans.modules.tasklist.pmd;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleViolation;
import pmd.*;
import pmd.config.ConfigUtils;
import pmd.config.PMDOptionsSettings;
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
import org.openide.cookies.SourceCookie;
import org.openide.explorer.view.*;
import org.openide.nodes.*;
import org.openide.ErrorManager;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.text.DataEditorSupport;

import org.netbeans.modules.tasklist.core.TLUtils;
import org.netbeans.modules.tasklist.client.Suggestion;
import org.netbeans.modules.tasklist.client.SuggestionManager;
import org.netbeans.modules.tasklist.client.SuggestionPerformer;
import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.netbeans.modules.tasklist.providers.DocumentSuggestionProvider;
import org.netbeans.modules.tasklist.providers.SuggestionContext;
import org.openide.src.ClassElement;
import org.openide.src.Identifier;

/**
 * This class uses the PMD rule checker to provide rule violation
 * suggestions.
 * <p>
 * @todo Add more automatic fixers for rules. 
 *  Potentially easy: UnusedModifier.
 * Other candidates: StringToString, StringInstantiation, ...
 * MustUseBraces?
 * <p>
 * @author Tor Norbye
 */


public class ViolationProvider extends DocumentSuggestionProvider {

    final private static String TYPE = "pmd-violations"; // NOI18N
    private SuggestionContext env;

    public String[] getTypes() {
        return new String[] { TYPE };
    }

    private Thread last;

    // javadoc in super()
    public void rescan(SuggestionContext env, Object request) {
        assert last == null || last == Thread.currentThread() : "Concurent access by: " + last + " and: " + Thread.currentThread();
        last = Thread.currentThread();
        try {
//            System.err.println("\nPMD.rescan" + request + "[" + ((this.env != null) ?  this.env.getFileObject().toString() : "null") + "]: " + showingTasks );

            this.env = env;
            this.request = request;
            List newTasks = scan(env);
            last = Thread.currentThread();
            SuggestionManager manager = SuggestionManager.getDefault();

            if ((newTasks == null) && (showingTasks == null)) {
                return;
            }

            manager.register(TYPE, newTasks, showingTasks, request);
            showingTasks = newTasks;
        } finally {
            last = null;
        }
    }

    void rescan() {
        rescan(env, request);
    }
    
    public List scan(SuggestionContext env) {
        assert last == null || last == Thread.currentThread() : "Concurent access by: " + last + " and: " + Thread.currentThread();
        last = Thread.currentThread();
        try {
//            System.err.println("\nPMD.scan[" + ((env != null) ?  env.getFileObject().toString() : "null") + "]: " + showingTasks );

            List tasks = null;
            try {

            SuggestionManager manager = SuggestionManager.getDefault();
            if (!manager.isEnabled(TYPE)) {
                return null;
            }

            DataObject dataObject = DataObject.find(env.getFileObject());
            SourceCookie cookie =
                (SourceCookie)dataObject.getCookie(SourceCookie.class);

            // The file is not a java file
            if(cookie == null) {
                return null;
            }

            String text = (String) env.getCharSequence();
            Reader reader = new StringReader(text);
            // XXX got an unexplained NPE in here somewhere...
            ClassElement[] topClazzes = cookie.getSource().getClasses();
            if (topClazzes.length == 0) {
                // Empty file, skip.
                return null;
            }
            assert topClazzes[0] != null : cookie.getSource().getClass().getName();
            Identifier topClazzName = topClazzes[0].getName();
            assert topClazzName != null : topClazzes[0].getClass().getName();
            String name = topClazzName.getFullName();
            PMD pmd = new PMD();
            RuleContext ctx = new RuleContext();
            Report report = new Report();
            ctx.setReport(report);
            ctx.setSourceCodeFilename(name);

            RuleSet set = new RuleSet();
            List rlist = ConfigUtils.createRuleList(
                                 PMDOptionsSettings.getDefault().getRules());
            Iterator it = rlist.iterator();
            while(it.hasNext()) {
                set.addRule((Rule)it.next());
            }
            try {
                pmd.processFile(reader, set, ctx);
            } catch (Exception e) {
                // For some reason, some of the PMD classes
                // throw RuntimeExceptions or PMDExceptions.
                // I suspect PMD wasn't written with the intent of it being run
                // on incomplete or invalid classes. So we just swallow the
                // exceptions here
                ; // Avoid PMD warning about empty catch block - this is intentional
            } catch (Error e) {
                // Ditto. It throws some non-exceptions like TokenMgrError
                ; // Avoid PMD warning about empty catch block - this is intentional
            }
            Iterator iterator = ctx.getReport().iterator();

            Image taskIcon = Utilities.loadImage("org/netbeans/modules/tasklist/pmd/fixable.gif"); // NOI18N

            if(!ctx.getReport().isEmpty()) {
                while(iterator.hasNext()) {
                    final RuleViolation violation = (RuleViolation)iterator.next();
                    try {
                        // Violation line numbers seem to be 0-based
                        final Line line = TLUtils.getLineByNumber(dataObject, violation.getLine());

                        //System.out.println("Next violation = " + violation.getRule().getName() + " with description " + violation.getDescription() + " on line " + violation.getLine());

                        boolean fixable = false;
                        SuggestionPerformer action = null;
                        String rulename = violation.getRule().getName();
                        if (rulename.equals("UnusedImports") || // NOI18N
                            rulename.equals("ImportFromSamePackage") || // NOI18N
                            rulename.equals("DontImportJavaLang") || // NOI18N
                            rulename.equals("DuplicateImports")) { // NOI18N
                            fixable = true;
                            boolean comment = false;
                            action = new ImportPerformer(line, violation, comment);
                        } else if (rulename.equals("UnusedLocalVariable") && // NOI18N
                                   isDeleteSafe(line)) { // only a check
                            fixable = true;
                            action = new SuggestionPerformer() {
                                public void perform(Suggestion s) {
                                    // Remove the particular line
                                    TLUtils.deleteLine(line, "");
                                }
                                public boolean hasConfirmation() {
                                    return true;
                                }
                                public Object getConfirmation(Suggestion s) {
                                    DataObject dao = DataEditorSupport.findDataObject(line);
                                    int linenumber = line.getLineNumber();
                                    String filename = dao.getPrimaryFile().getNameExt();
                                    String ruleDesc = violation.getRule().getDescription();
                                    String ruleExample = violation.getRule().getExample();
                                    String beforeDesc = NbBundle.getMessage(ViolationProvider.class,
                                            "UnusedConfirmation"); // NOI18N

                                    StringBuffer sb = new StringBuffer(200);
                                    Line l = line;
                                    sb.append("<html>"); // NOI18N
                                    TLUtils.appendSurroundingLine(sb, l, -1);
                                    sb.append("<br>");
                                    sb.append("<b><strike>");
                                    sb.append(line.getText());
                                    sb.append("</strike></b>");
                                    sb.append("<br>");
                                    TLUtils.appendSurroundingLine(sb, l, +1);
                                    sb.append("</html>"); // NOI18N
                                    String beforeContents = sb.toString();

                                    return new org.netbeans.modules.tasklist.core.ConfPanel(beforeDesc,
                                          beforeContents, null, null,
                                          filename, linenumber, getBottomPanel(ruleDesc, ruleExample));
                                }
                            };

                        } else if (rulename.equals("UnusedPrivateField")) { // NOI18N
                            fixable = true;
                            boolean comment = false;
                            action = new RemovePerformer(true,
                                                         line, violation,
                                                         comment);
                        } else if (rulename.equals("UnusedPrivateMethod")) { // NOI18N
                            fixable = true;
                            boolean comment = false;
                            action = new RemovePerformer(false,
                                                         line, violation,
                                                         comment);
                        } else {
                            action = null;
                        }

                        Suggestion s = manager.createSuggestion(
                            TYPE,
                            rulename + " : " + // NOI18N
                               violation.getDescription(),
                            action,
                            this);

                        // Make sure PMD's rule range is still the same
                        // as ours. If not, we've gotta scale it
                        // JDK14: assert Rule.LOWEST_PRIORITY == 5;
                        switch (violation.getRule().getPriority()) {
                        case 1: s.setPriority(SuggestionPriority.HIGH); break;
                        case 2: s.setPriority(SuggestionPriority.MEDIUM_HIGH); break;
                        case 3: s.setPriority(SuggestionPriority.MEDIUM); break;
                        case 4: s.setPriority(SuggestionPriority.MEDIUM_LOW); break;
                        case 5: s.setPriority(SuggestionPriority.LOW); break;
                        default: s.setPriority(SuggestionPriority.MEDIUM); break;
                        }

                        s.setLine(line);
                        if (fixable) {
                            s.setIcon(taskIcon);
                        }
                        if (tasks == null) {
                            tasks = new ArrayList(ctx.getReport().size());
                        }
                        tasks.add(s);
                    } catch (Exception e) {
                        ErrorManager.getDefault().notify(e);
                    }
                }
            }
            } catch (Exception e) {
                ErrorManager.getDefault().notify(e);
            }
            return tasks;
        } finally {
            last = null;
        }
    }
    
    /**
     * Checks designed to prevent deleting additional content on the
     * line - for example, it won't delete anything if it detects
     * multiple statements on the line, or function calls. It may err
     * on the safe side; e.g. not delete even when it would be safe to do so.
     */
    private static boolean isDeleteSafe(String text) {
        /*
          What about a weird corner case like this:
          int z = 0;
          for (int y = 0;
          z < 5;
          z++) {
          importantCall();
          }
          Will I delete the "for(int y = 0" line since y is unused?

          No - because I will see the "(" and bail!
        */

        // A small statemachine to figure out if the line can
        // be "safely" deleted
        int n = text.length();
        boolean inString = false;
        boolean escaped = false;

        //  What do we initialize comment too? It's POSSIBLE that you
        //  have code like this
        //  /* Begin comment:
        //     end */    int unused = 5;
        //  ...and I begin in the middle of a comment. But I think this
        // is an unusual scenario...
        boolean comment = false;
                
        boolean seenSemi = false;
        boolean seenComma = false;
        for (int i = 0; i < n; i++) {
            char c = text.charAt(i);
            if (comment) {
                if ((c == '*') && (i < (n-1)) &&
                    ((text.charAt(i+1) == '/'))) {
                    comment = false;
                } else {
                    continue;
                }
            } else if (c == '\\') {
                escaped = !escaped;
            } else if (c == '"') {
                if (!escaped) {
                    inString = !inString;
                }
            } else if ((c == '/') && (i < (n-1)) &&
                       ((text.charAt(i+1) == '*'))) {
                comment = true;
            } else if (c == '(') {
                if (!inString && !escaped) {
                    // BAIL! "(" on a line makes me nervous, e.g. unused
                    // variable "success" in
                    //   boolean success = saveData();
                    //System.out.println("BAILING: function call on the line!");
                    return false;
                }
            } else if (c == ',') {
                if (!inString && !escaped) {
                    seenComma = true;
                }
            } else if (c == ';') {
                if (!inString && !escaped) {
                    seenSemi = true;
                }
            } else if (Character.isWhitespace(c)) {
                // do nothing
            } else {
                // Some other character
                if (!inString && !escaped && (seenSemi || seenComma)) {
                    // BAIL -- we've seen text after a semicolon or
                    // comma - multiple statements on the line!
                    //System.out.println("BAILING: character after semi=" + seenSemi + " or comma=" + seenComma);
                    return false;
                }
            }
        }
        return true;
    }
    
    /** Check if line/field deletion is safe. Safe here means
     * that the field does not get assigned some method (which may
     * have an important side effect)
     */
    public static boolean isDeleteSafe(Line line) {
        Document doc = TLUtils.getDocument(line);
        Element elm = TLUtils.getElement(doc, line);
        if (elm == null) {
            return false;
        }
        int offset = elm.getStartOffset();
        int endOffset = elm.getEndOffset();

        try {
            String text = doc.getText(offset, endOffset-offset);
            return isDeleteSafe(text);
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
        return false;
    }
    
    static JPanel getBottomPanel(String ruleDesc, String ruleExample) {
        java.awt.GridBagConstraints gridBagConstraints;
        // Variables declaration - do not modify
        javax.swing.JLabel jLabel9;
        javax.swing.JLabel jLabel8;
        javax.swing.JScrollPane jScrollPane2;
        javax.swing.JTextArea descText;
        javax.swing.JScrollPane jScrollPane1;
        javax.swing.JPanel jPanel1;
        javax.swing.JTextArea exampleText;
        // End of variables declaration
        
        
        jPanel1 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        descText = new javax.swing.JTextArea();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        exampleText = new javax.swing.JTextArea();

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel8.setText(NbBundle.getMessage(ViolationProvider.class, "Description")); // NOI18N();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jLabel8, gridBagConstraints);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(200, 200));
        descText.setWrapStyleWord(true);
        descText.setLineWrap(true);
        descText.setEditable(false);
        jScrollPane1.setViewportView(descText);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jScrollPane1, gridBagConstraints);

        jLabel9.setText(NbBundle.getMessage(ViolationProvider.class, "Example")); // NOI18N();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jLabel9, gridBagConstraints);

        jScrollPane2.setPreferredSize(new java.awt.Dimension(200, 200));
        exampleText.setEditable(false);
        exampleText.setPreferredSize(null);
        jScrollPane2.setViewportView(exampleText);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jScrollPane2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(18, 12, 11, 11);

        // Dont' use monospaced fonts
        descText.setFont(jLabel8.getFont());
        exampleText.setFont(jLabel8.getFont());
        
        descText.setText(ruleDesc.trim());
        exampleText.setText(ruleExample.trim());
        
        return jPanel1;
    }

    public void clear(SuggestionContext env,
                      Object request) {

        assert last == null || last == Thread.currentThread() : "Concurent access by: " + last + " and: " + Thread.currentThread();
        last = Thread.currentThread();

        try {
            if (showingTasks != null) {
//                System.err.println("\nPMD.clear[" + ((this.env != null) ?  this.env.getFileObject().toString() : "null") + "]: " + showingTasks );
                SuggestionManager manager = SuggestionManager.getDefault();
                manager.register(TYPE, null, showingTasks, request);
                showingTasks = null;
            }
        } finally {
            last = null;
        }
    }

    /** The list of tasks we're currently showing in the tasklist */
    private List showingTasks = null;

    private Object request = null;
}

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

package org.netbeans.modules.tasklist.copyright;

import javax.swing.text.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.*;

import org.openide.ErrorManager;
import org.openide.text.Line;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle;

import org.netbeans.api.tasklist.*;
import org.netbeans.spi.tasklist.DocumentSuggestionProvider;
import org.netbeans.spi.tasklist.SuggestionContext;

import org.netbeans.modules.tasklist.core.TLUtils;
import org.netbeans.modules.tasklist.core.ConfPanel;

/**
 * This class scans the given document errors in the copyright
 * declaration.
 *
 * @author Tor Norbye
 */
public class CopyrightChecker extends DocumentSuggestionProvider {

    final private static String TYPE = "nb-tasklist-copyright"; // NOI18N

    /**
     * Return the typenames of the suggestions that this provider
     * will create.
     * @return An array of string names. Should never be null. Most
     *  providers will create Suggestions of a single type, so it will
     *  be an array with one element.
     */
    public String[] getTypes() {
        return new String[]{TYPE};
    }

    public void rescan(SuggestionContext env, Object request) {
        List newTasks = scan(env);
        SuggestionManager manager = SuggestionManager.getDefault();

        if ((newTasks == null) && (showingTasks == null)) {
            return;
        }
        manager.register(TYPE, newTasks, showingTasks, request);
        showingTasks = newTasks;
    }

    public List scan(SuggestionContext env) {
        SuggestionManager manager = SuggestionManager.getDefault();
        if (!manager.isEnabled(TYPE)) {
            return null;
        }
        Suggestion s = checkCopyright(env);
        if (s != null) {
            List tasks = new ArrayList(1);
            tasks.add(s);
            return tasks;
        }
        return null;
    }


    public void clear(SuggestionContext env,
                      Object request) {
        // Remove existing items
        if (showingTasks != null) {
            SuggestionManager manager = SuggestionManager.getDefault();
            manager.register(TYPE, null, showingTasks, request);
            showingTasks = null;
        }
    }

    /*
  Copyright idea:
  search for n lines (or m characters) for the regexp
    copyright|Copyright|COPYRIGHT|(C)|(c)|�
  Then on any matching lines, look for year-tokens,
  where a year token is a 4 digit number beginning with 19 or 20.
  (y2.1k bug!)    Three supported patterns:
     NNNN      : single year.
     NNNN-MMMM : year range
     NNNN,MMMM,OOOO,...: year list
  For single year, see if NNNN < current year. If so,
  add task to change it to NNNN-currentyear.
  For year range, see if MMMM < current year. If so,
  add task to change it to NNNN-currentyear.
  For year list, see if currentyear is in the list. If not,
  add current year to the list (or better yet, change the range to
  lowest-highest).

  Optionally, also offer to add copyright to files missing one.
    */


    /**
     * Check the top of the document to see if there's a copyright
     * in it, and if so, check that the year includes the current
     * year.  Support is limited to cases where
     * <ul>
     *  <li> The copyright string and the year list appear on the same line
     *  <li> The years are 4 digits long, not 2 or some other number
     *  <li> The years are between 1900 and 2100 (this restriction can
     *       be removed and is there to avoid false positives such that
     *       only years are identified)
     *  <li> The copyright string is one of copyright, Copyright, COPYRIGHT,
     *       (C), (c), and �.
     *  <li> The date is either a single years, a comma separated list of years,
     *       or a year range (separated with a dash, no whitespace surrounding
     *       the dash.)
     *  <li> There is only a single copyright declaration in the file
     *       (it will only consider the first one)
     *  <li> The copyright appears near the beginning of the file
     *       (currently in one of the first 10*80 characters although this
     *       may change)
     * </ul>
     *
     * @param doc The document to be scanned
     * @param dobj The dataobject of the document
     * @param updating True iff this is an update scan
     * @param newTasks TODO
     *
     * @todo Decide if I really need both the updating and the replace
     *       flag and if updating doesn't perhaps imply replace
     * @todo FIX PERFORMANCE! Really bad right now; parsing regexp each
     *       time, creating a String from a document, computing the date
     *       each time (DateFormats are expensive), etc.
     * @todo Perhaps merge it into the regular scanning (cl.line checking
     *       when line index < N) ?
     * @todo Do year computation to make sure that if we have a FUTURE
     *       year in a date range, we can bail (ok, that won't work
     *       for copyright 2100-2150, but that seems absurd anyway)
     * @todo Skip this check for read-only files? Can't edit them anyway
     * @todo Only suggest this task once the document has been edited?
     *       If a user is -visiting- a file (e.g. during debugging) it
     *       may be pointless.
     */
    Suggestion checkCopyright(final SuggestionContext env) {
        // TODO - cache the regexp across invocations
        Pattern re = null;
        try {
            // XXX make this configurable?
            re = Pattern.compile("copyright|Copyright|COPYRIGHT|\\(C\\)|\\(c\\)|�"); // NOI18N
        } catch (PatternSyntaxException e) {
            // Internal error: the regexp should have been validated when
            // the user edited it
            ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
            return null;
        }

        // TODO - cache and share with other scanner!
        CharSequence chars = env.getCharSequence();
        int len = chars.length();
        if (len > 10 * 80) {
            len = 10 * 80; // Only look at the top 10 or so lines (truncate document)
        }

        int index = 0;
        Matcher matcher = re.matcher(chars);
        if (matcher.find(index)) { // TODO - find a way to bound the search?
            int begin = matcher.start();
            index = matcher.end();

            // Compute the copyright line contents: back up to find the
            // beginning of the line, then move forwards to skip the
            // backspace. The line extends to the end of the line.

            // Find end of line
            while (index < len) {
                char c = chars.charAt(index);
                if (c == '\n' || c == '\r') {
                    break;
                }
                index++;
            }

            // Find beginning of line
            while (begin >= 0) {
                char c = chars.charAt(begin);
                if (c == '\n' || c == '\r') {
                    break;
                }
                begin--;
            }
            begin++; // skip the \n
            CharSequence line;
            if (index < len) {
                line = chars.subSequence(begin, index);
            } else {
                line = chars.subSequence(begin, chars.length());
            }

            // Get current year
            // TODO cache!
            final String year = new SimpleDateFormat("yyyy").format(new Date());

            // Scan copyright line to look for copyright years
            int n = line.length() - 3; // only support 4-digit years for now
            int c0 = -1; // previous character
            int cp = -1; // previous previous character
            int rangeEnd = -1;
            int listEnd = -1;
            int dateEnd = -1;
            int firstDate = 0;
            for (int i = 0; i < n; i++) {
                char c1 = line.charAt(i);
                char c2 = line.charAt(i + 1);
                char c3 = line.charAt(i + 2);
                char c4 = line.charAt(i + 3);
                if ((c1 == '1' || (c1 == '2')) && // Y3K bug here!
                        (c2 == '9' || (c2 == '0')) && // Y2.1K bug here!
                        Character.isDigit(c3) &&
                        Character.isDigit(c4)) {

                    if (firstDate == 0) {
                        firstDate = i;
                    }

                    // TODO -- what if the current year is GREATER than
                    // the current year? (e.g. somebody did
                    // copyright 1975-2010 just to be on the safe side?)
                    // Check for that!

                    // See if the current year is in the list
                    if ((c4 == year.charAt(3)) &&
                            (c3 == year.charAt(2)) &&
                            (c2 == year.charAt(1)) &&
                            (c1 == year.charAt(0))) {
                        // Yes, so bail
                        return null;
                    }

                    // I've found a year!
                    if (c0 == '-') {
                        // This is the potential end of a date-range
                        rangeEnd = i;
                    } else if ((c0 == ',') || ((cp == ',') && (c0 == ' '))) {
                        // This is the potential end of a comma-list
                        listEnd = i;
                    } else {
                        dateEnd = i;
                    }
                }
                cp = c0;
                c0 = c1;
            }

            if ((rangeEnd == -1) && (listEnd == -1) && (dateEnd == -1)) {
                // Copyright in the text, but no year found ... could offer
                // to add one, but where? For now, keep quiet
                return null;
            }

            // Ensure that we use the last date occurrence as our insert position
            // Reverse order of checks used below
            if (dateEnd > listEnd) {
                listEnd = -1;
            }
            if (listEnd > rangeEnd) {
                rangeEnd = -1;
            }

            int lastDate = 0;
            if (rangeEnd != -1) {
                lastDate = rangeEnd + 4;
            } else if (listEnd != -1) {
                lastDate = listEnd + 4;
            } else if (dateEnd != -1) {
                lastDate = dateEnd + 4;
            }

            String range = "";
            if (rangeEnd != -1) {
                range = line.subSequence(firstDate, rangeEnd).toString() + year;
            } else if (listEnd != -1) {
                range = line.subSequence(firstDate, lastDate).toString() +
                        ", " + year; // NOI18N
            } else { // assert dateEnd != -1
                range = line.subSequence(firstDate, lastDate).toString() +
                        "-" + year; // NOI18N
            }

            final String oldRange = line.subSequence(firstDate, lastDate).toString();
            final String newRange = range;
            String description =
                    NbBundle.getMessage(CopyrightChecker.class,
                            "CopyrightDesc", // NOI18N
                            newRange, oldRange);

            int linenum = 1;
            for (int k = 0; k < begin; k++) {
                // TODO make sure this works on other platforms with
                // strange newline handling (e.g. windows: \r\n, mac: \r)
                // Currently it won't work if you're on a \r system
                if (chars.charAt(k) == '\n') {
                    linenum++;
                }
            }
            final int lineno = linenum;

            SuggestionManager manager = SuggestionManager.getDefault();
            Suggestion copyrightTask = manager.createSuggestion(TYPE,
                    description,
                    null,
                    this);
            try {
                DataObject dataObject = DataObject.find(env.getFileObject());
                copyrightTask.setLine(TLUtils.getLineByNumber(dataObject, lineno));
            } catch (DataObjectNotFoundException e) {
                // ignore
            }

            final String verify = ""; // XXX Not used. Idea was to
            // produce string
            // expected at line[pos] to make sure user hasn't messed with
            // the line (aha! Let's just store the line!)

            // XXX IMPORTANT! Invalidate the fixer as soon as the document
            // is edited. Alternatively, "recompute" the positions before
            // actually committing the change! (Need some refactoring of this
            // rather lengthy and cluttered method)
            final int fRangeEnd = rangeEnd;
            final int fBegin = begin;
            final int fListEnd = listEnd;
            final int fDateEnd = dateEnd;

            final Document doc = env.getDocument();
            SuggestionPerformer jackson = new SuggestionPerformer() {
                public void perform(Suggestion s) {
                    // Replace the end of the range
                    substitute(doc, fRangeEnd, fBegin, fListEnd,
                            fDateEnd, verify, year);
                }

                public boolean hasConfirmation() {
                    return true;
                }

                public Object getConfirmation(Suggestion s) {
                    String text = getLineContents(doc, lineno - 1);
                    Document newdoc = new PlainDocument();
                    String preview = null;
                    try {
                        newdoc.insertString(0, text, null);
                        // XXX Gotta subtract from the indices!
                        int rangeEnd = fRangeEnd;
                        int listEnd = fListEnd;
                        int dateEnd = fDateEnd;
                        /*
                        int rangeEnd = (fRangeEnd != -1) ?
                            fRangeEnd-fBegin : -1;
                        int listEnd = (fListEnd != -1) ?
                            fListEnd-fBegin : -1;
                        int dateEnd = (fDateEnd != -1) ?
                            fDateEnd-fBegin : -1;
                         */
                        substitute(newdoc, rangeEnd, 0, listEnd, dateEnd,
                                verify, year);
                        preview = newdoc.getText(0, newdoc.getLength());
                    } catch (BadLocationException ex) {
                        ErrorManager.getDefault().notify(
                                ErrorManager.WARNING,
                                ex);
                        ex.printStackTrace();
                    }

                    //return NbBundle.getMessage(CopyrightChecker.class,
                    // "CopyrightConfirmation", oldRange, newRange, text);
                    String filename = env.getFileObject().getNameExt();
                    String beforeDesc = NbBundle.getMessage(CopyrightChecker.class,
                            "CopyrightConfirmation"); // NOI18N
                    //String beforeContents = "<html><b>" + text.trim() + "</b></html>";
                    String afterDesc = NbBundle.getMessage(CopyrightChecker.class,
                            "CopyrightConfirmationAfter"); // NOI18N
                    //String afterContents = "<html><b>" + preview.trim() + "</b></html>";


                    int fd = TLUtils.firstDiff(text, preview);
                    int ld = TLUtils.lastDiff(text, preview);

                    Line l = s.getLine();
                    StringBuffer sb = new StringBuffer(200);
                    sb.append("<html>"); // NOI18N
                    // HACK: When the text begins with
                    // "// Hello" it does NOT get rendered by
                    // Swing! (On this Apple JDK that I'm developing it
                    // on anyway). So hack around it by putting some
                    // useless attributes in there.
                    sb.append("<b></b>");
                    // XXX Make sure it can begin with // copyright 2000 !
                    TLUtils.appendSurroundingLine(sb, l, -1);
                    //sb.append("<b>");
                    TLUtils.appendAttributed(sb, text, fd,
                            text.length() - ld,
                            true, true);
                    //sb.append("</b>");
                    TLUtils.appendSurroundingLine(sb, l, +1);
                    sb.append("</html>"); // NOI18N
                    String beforeContents = sb.toString();


                    sb.setLength(0);
                    sb.append("<html>");
                    // HACK: I also noticed that "/*\n* Copyright"
                    // wouldn't correctly draw the first line, so
                    // hack around it by putting some useless
                    // attributes in there.
                    sb.append("<b></b>");

                    TLUtils.appendSurroundingLine(sb, l, -1);
                    //sb.append("<b>");
                    TLUtils.appendAttributed(sb, preview, fd,
                            preview.length() - ld,
                            true, true);
                    //sb.append("</b>");

                    TLUtils.appendSurroundingLine(sb, l, +1);
                    sb.append("</html>"); // NOI18N
                    String afterContents = sb.toString();

                    return new ConfPanel(beforeDesc,
                            beforeContents, afterDesc,
                            afterContents,
                            filename, lineno, null);
                }
            };
            copyrightTask.setAction(jackson);
            copyrightTask.setPriority(SuggestionPriority.LOW);
            return copyrightTask;
        } else {
            // Make no-copyright warnings optional
            // TODO - check for that here

            String warning = NbBundle.getMessage(CopyrightChecker.class,
                    "NoCopyright"); // NOI18N
            SuggestionPerformer action = new SuggestionPerformer() {
                public void perform(Suggestion s) {
                    String comment = getComment(false);
                    if ((comment != null) && (comment.length() > 0)) {
                        try {
                            env.getDocument().insertString(0, comment, null);
                        } catch (BadLocationException e) {
                            ErrorManager.getDefault().notify(
                                    ErrorManager.WARNING, e);
                        }
                        // TODO Should I put a message in the status
                        // window which tells you how to change the
                        // copyright that is used?
                    } else {
                        JTextArea labelArea = new JTextArea();
                        labelArea.setWrapStyleWord(true);
                        labelArea.setLineWrap(true);
                        labelArea.setEditable(false);
                        labelArea.setText(
                                NbBundle.getMessage(CopyrightChecker.class,
                                        "NoChosenCopyright")); // NOI18N
                        labelArea.setBackground((java.awt.Color) UIManager.
                                getDefaults().get(
                                        "Label.background")); // NOI18N
                        JTextArea textArea = new JTextArea();
                        textArea.setRows(8);
                        String sample = getSampleLicense();
                        textArea.setText(sample);
                        textArea.select(0, sample.length());
                        JScrollPane pane = new JScrollPane(textArea);
                        // TODO: add text area to panel!

                        JPanel body = new JPanel();
                        body.setLayout(new BorderLayout());
                        body.add(labelArea, BorderLayout.NORTH);
                        body.add(pane, BorderLayout.CENTER);
                        body.setPreferredSize(new Dimension(400, 300));
                        NotifyDescriptor nd =
                                new NotifyDescriptor.Confirmation(
                                        body,
                                        NotifyDescriptor.OK_CANCEL_OPTION
                                );
                        Object result =
                                DialogDisplayer.getDefault().notify(nd);
                        if (NotifyDescriptor.OK_OPTION == result) {
                            String copyright = textArea.getText().trim();
                            if (copyright.length() > 0) {
                                CopyrightSettings settings =
                                        (CopyrightSettings) CopyrightSettings.findObject(
                                                CopyrightSettings.class, true);
                                settings.setScanCopyright(copyright);
                                // recurse!
                                perform(s);
                            }
                        }
                    }
                }

                private String getCopyright() {
                    CopyrightSettings settings =
                            (CopyrightSettings) CopyrightSettings.findObject(CopyrightSettings.class, true);
                    String copyright = settings.getScanCopyright();
                    return copyright;
                }

                private String getComment(boolean makeHtml) {
                    String copyright = getCopyright();
                    if ((copyright == null) || (copyright.length() == 0)) {
                        return null;
                    }
                    String prefix = "";
                    String suffix = "";
                    String linefix = null;
                    String ext = env.getFileObject().getExt();
                    if (ext.equalsIgnoreCase("java") || // NOI18N
                            ext.equalsIgnoreCase("cc") || // NOI18N
                            ext.equalsIgnoreCase("cpp")) {  // NOI18N
                        linefix = "//"; // NOI18N
                        prefix = "/*"; // NOI18N
                        suffix = "*/"; // NOI18N
                    } else if (ext.equalsIgnoreCase("html") || // NOI18N
                            ext.equalsIgnoreCase("htm") || // NOI18N
                            ext.equalsIgnoreCase("xml")) {  // NOI18N
                        prefix = "<!--"; // NOI18N
                        suffix = "-->"; // NOI18N
                    } else if (ext.equalsIgnoreCase("jsp")) {  // NOI18N
                        prefix = "<%--"; // NOI18N
                        suffix = "--%>"; // NOI18N
                    } else if (ext.equalsIgnoreCase("c")) { // NOI18N
                        prefix = "/*"; // NOI18N
                        suffix = "*/"; // NOI18N
                    } else if (ext.equalsIgnoreCase("properties") || // NOI18N
                            ext.equalsIgnoreCase("sh")) { // NOI18N
                        linefix = "#"; // NOI18N
                    }
                    int n = copyright.length();
                    if (linefix != null) {
                        // Insert a comment string at the beginning of
                        // every line
                        StringBuffer sb = new StringBuffer(2 * n);
                        if (makeHtml) {
                            sb.append("<html><body>"); // NOI18N
                        }
                        boolean commentOut = true;
                        if (prefix != "") {
                            commentOut = !startsWithComment(copyright, 0, n,
                                    prefix);
                        }
                        boolean newline = true;
                        for (int i = 0; i < n; i++) {
                            if (newline && commentOut &&
                                    !startsWithComment(copyright, i, n, linefix)) {
                                if (makeHtml) {
                                    if (i != 0) {
                                        sb.append("</i>"); // NOI18N
                                    }
                                    sb.append("<b>"); // NOI18N
                                    TLUtils.appendHTMLString(sb, linefix);
                                    sb.append("</b>&nbsp;<i>"); // NOI18N
                                } else {
                                    sb.append(linefix);
                                    sb.append(' ');
                                }
                            }
                            newline = false;
                            char c = copyright.charAt(i);
                            if (c == '\n') {
                                newline = true;
                            }
                            if (makeHtml) {
                                TLUtils.appendHTMLChar(sb, c);
                            } else {
                                sb.append(c);
                            }
                        }
                        if (makeHtml) {
                            sb.append("</i><br></body></html>"); // NOI18N
                        } else {
                            sb.append('\n');
                        }
                        return sb.toString();
                    } else {
                        // TODO - check to see if license already contains
                        // a comment prefix.
                        StringBuffer sb =
                                new StringBuffer(n + 20);
                        if (makeHtml) {
                            sb.append("<html><body>"); // NOI18N
                            // HACK: When the text begins with
                            // "/* Hello" it does NOT get rendered
                            // by Swing! (On this Apple JDK that
                            // I'm developing it on anyway). So
                            // hack around it by putting some
                            // useless attributes in there.
                            sb.append("<b></b><i>"); // NOI18N
                        }
                        boolean commentOut = true;
                        if (startsWithComment(copyright, 0, n, prefix) ||
                                ((linefix != null) &&
                                (startsWithComment(copyright, 0, n, linefix)))) {
                            commentOut = false;
                        }
                        if (commentOut) {
                            if (makeHtml) {
                                sb.append("<b>");
                                TLUtils.appendHTMLString(sb, prefix);
                                sb.append("</b>");
                            } else {
                                sb.append(prefix);
                            }
                            sb.append('\n');
                        }
                        if (makeHtml) {
                            TLUtils.appendHTMLString(sb, copyright);
                        } else {
                            sb.append(copyright);
                        }
                        if (commentOut) {
                            sb.append('\n');
                            if (makeHtml) {
                                sb.append("<b>");
                                TLUtils.appendHTMLString(sb, suffix);
                                sb.append("</b>");
                            } else {
                                sb.append(suffix);
                            }
                        }
                        if (makeHtml) {
                            sb.append("</i><br></body></html>"); // NOI18N
                        } else {
                            sb.append('\n');
                        }
                        return sb.toString();
                    }
                }

                public boolean hasConfirmation() {
                    String copyright = getCopyright();
                    return ((copyright != null) && (copyright.length() > 0));
                }

                public Object getConfirmation(Suggestion s) {
                    String comment = getComment(true);
                    String filename = env.getFileObject().getNameExt();
                    return new ConfPanel(
                            NbBundle.getMessage(CopyrightChecker.class,
                                    "InsertCopyright"), // NOI18N
                            comment,
                            NbBundle.getMessage(CopyrightChecker.class,
                                    "ChangeCopyright"), // NOI18N
                            null,
                            filename, 1, null);
                }
            };

            SuggestionManager manager = SuggestionManager.getDefault();
            Suggestion copyrightTask = manager.createSuggestion(TYPE,
                    warning,
                    action,
                    this);
            try {
                DataObject dataObject = DataObject.find(env.getFileObject());
                copyrightTask.setLine(TLUtils.getLineByNumber(dataObject, 1));
            } catch (DataObjectNotFoundException e) {
                // ignore
            }
            // Normal priority sounds about right
            return copyrightTask;
        }
    }

    private String getSampleLicense() {
        return "YOUR LICENSE HERE. For example, here's the Sun Public License\n" +
                "used by the tasklist modules:\n\n" +
                "                 Sun Public License Notice\n\n" +
                "The contents of this file are subject to the Sun Public License\n" +
                "Version 1.0 (the \"License\"). You may not use this file except in\n" +
                "compliance with the License. A copy of the License is available at\n" +
                "http://www.sun.com/\n\n" +
                "The Original Code is NetBeans. The Initial Developer of the Original\n" +
                // TODO - dynamically compute the end range year here
                "Code is Sun Microsystems, Inc. Portions Copyright 1997-" +
                new SimpleDateFormat("yyyy").format(new Date()) +
                " Sun\n" +
                "Microsystems, Inc. All Rights Reserved.\n\n\n";

    }

    /** Returns true if the given line (starting at str+index) begins
     with the given comment prefix (after an optional range of whitespace) */
    private boolean startsWithComment(String str, int index, int len,
                                      String commentPrefix) {
        while ((index < len) &&
                (Character.isSpace(str.charAt(index)))) {
            index++;
        }
        return str.startsWith(commentPrefix, index);
    }

    /**
     * @param begin Position within the document to start
     * @param verify String at begin should be $verify
     */
    private void substitute(Document doc, int rangeEnd, int begin, int listEnd,
                            int dateEnd, String verify,
                            String year) {
        if (rangeEnd != -1) {
            // Replace the end of the range
            final int pos = rangeEnd + begin;
            try {
                doc.remove(pos, 4);
                doc.insertString(pos, year, null);
            } catch (BadLocationException e) {
                ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
            }
        } else if (listEnd != -1) {
            // Add one more item to the list
            final int pos = listEnd + 4 + begin;
            try {
                doc.insertString(pos, ", " + year, null);
            } catch (BadLocationException e) {
                ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
            }
        } else { // assert dateEnd != -1
            final int pos = dateEnd + 4 + begin;
            // Create a date range
            try {
                doc.insertString(pos, "-" + year, null);
            } catch (BadLocationException e) {
                ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
            }
        }
    }

    private String getLineContents(Document doc, int linenumber) {
        Element elm = getElement(doc, linenumber);
        if (elm == null) {
            ErrorManager.getDefault().log(ErrorManager.USER, "getElement was null");
            return null;
        }
        int offset = elm.getStartOffset();
        int endOffset = elm.getEndOffset();

        try {
            String text = doc.getText(offset, endOffset - offset);
            return text;
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
        return null;
    }


    private Element getElement(Document d, int linenumber) {
        if (d == null) {
            ErrorManager.getDefault().log(ErrorManager.USER, "d was null");
            return null;
        }

        if (!(d instanceof StyledDocument)) {
            ErrorManager.getDefault().log(ErrorManager.USER, "Not a styleddocument");
            return null;
        }

        StyledDocument doc = (StyledDocument) d;
        Element e = doc.getParagraphElement(0).getParentElement();
        if (e == null) {
            // try default root (should work for text/plain)
            e = doc.getDefaultRootElement();
        }
        Element elm = e.getElement(linenumber);
        return elm;
    }


    /** The list of tasks we're currently showing in the tasklist */
    private List showingTasks = null;
}

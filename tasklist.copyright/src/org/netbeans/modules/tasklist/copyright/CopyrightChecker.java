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
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle;

import org.netbeans.modules.tasklist.client.*;
import org.netbeans.modules.tasklist.providers.DocumentSuggestionProvider;
import org.netbeans.modules.tasklist.providers.SuggestionContext;

import org.netbeans.modules.tasklist.core.TLUtils;

/**
 * This class scans the given document errors in the copyright
 * declaration.
 *
 * Copyright idea:
 * search for n lines (or m characters) for the regexp
 * copyright|Copyright|COPYRIGHT|(C)|(c)|\u00a9
 * Then on any matching lines, look for year-tokens,
 * where a year token is a 4 digit number beginning with 19 or 20.
 * (y2.1k bug!)    Three supported patterns:
 * NNNN      : single year.
 * NNNN-MMMM : year range
 * NNNN,MMMM,OOOO,...: year list
 * For single year, see if NNNN < current year. If so,
 * add task to change it to NNNN-currentyear.
 * For year range, see if MMMM < current year. If so,
 * add task to change it to NNNN-currentyear.
 * For year list, see if currentyear is in the list. If not,
 * add current year to the list (or better yet, change the range to
 * lowest-highest).
 *
 * Optionally, also offer to add copyright to files missing one.
 *
 * @author Tor Norbye
 * @author Tim Lebedkov
 */
public class CopyrightChecker extends DocumentSuggestionProvider {
    final private static String TYPE = "nb-tasklist-copyright"; // NOI18N
    
    // Get current year
    private final String year = new SimpleDateFormat("yyyy").format(new Date());
    
    /** The list of tasks we're currently showing in the tasklist */
    private List showingTasks = null;
    
    private Pattern re;

    public CopyrightChecker() {
        try {
            // XXX make this configurable?
            re = Pattern.compile("copyright|Copyright|COPYRIGHT|\\(C\\)|\\(c\\)|\u00a9"); // NOI18N
        } catch (PatternSyntaxException e) {
            // Internal error: the regexp should have been validated when
            // the user edited it
            ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
        }
    }
    
    public String getType() {
        return TYPE;
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
     *       (C), (c), and \u00a9.
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
     * @param env a context
     *
     * @todo Decide if I really need both the updating and the replace
     *       flag and if updating doesn't perhaps imply replace
     * @todo FIX PERFORMANCE! Really bad right now; creating a String from a 
     *       document etc.
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
    private Suggestion checkCopyright(final SuggestionContext env) {
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

            String range;
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
            SuggestionAgent copyrightTask = manager.createSuggestion(TYPE,
                description, null, this);
            try {
                DataObject dataObject = DataObject.find(env.getFileObject());
                copyrightTask.setLine(TLUtils.getLineByNumber(dataObject, lineno));
            } catch (DataObjectNotFoundException e) {
                // ignore
                ErrorManager.getDefault().notify(e);
            }

            //final String verify = ""; 
            // XXX Not used. Idea was to
            // produce string
            // expected at line[pos] to make sure user hasn't messed with
            // the line (aha! Let's just store the line!)

            // XXX IMPORTANT! Invalidate the fixer as soon as the document
            // is edited. Alternatively, "recompute" the positions before
            // actually committing the change! 
            final int fRangeEnd = rangeEnd;
            final int fBegin = begin;
            final int fListEnd = listEnd;
            final int fDateEnd = dateEnd;
            final Document doc = env.getDocument();
            
            SuggestionPerformer jackson = new ChangeCopyrightDatesPerformer(
                env, fRangeEnd, fBegin, fListEnd, fDateEnd, doc, lineno, year);
            copyrightTask.setAction(jackson);
            copyrightTask.setPriority(SuggestionPriority.LOW);
            return copyrightTask.getSuggestion();
        } else {
            // Make no-copyright warnings optional
            // TODO - check for that here

            String warning = NbBundle.getMessage(CopyrightChecker.class,
                    "NoCopyright"); // NOI18N

            SuggestionPerformer action = new AddCopyrightPerformer(env);
            SuggestionManager manager = SuggestionManager.getDefault();
            SuggestionAgent copyrightTask = manager.createSuggestion(TYPE,
                warning, action, this);
            try {
                DataObject dataObject = DataObject.find(env.getFileObject());
                copyrightTask.setLine(TLUtils.getLineByNumber(dataObject, 1));
            } catch (DataObjectNotFoundException e) {
                // ignore
                ErrorManager.getDefault().notify(e);
            }
            
            // Normal priority sounds about right
            return copyrightTask.getSuggestion();
        }
    }
}

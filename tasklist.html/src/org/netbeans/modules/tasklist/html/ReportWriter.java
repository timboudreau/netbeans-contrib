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

package org.netbeans.modules.tasklist.html;

import org.netbeans.api.tasklist.*;

import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import org.openide.ErrorManager;
import org.openide.explorer.view.*;


import org.openide.loaders.DataObject;
import org.openide.text.Line;

import org.netbeans.modules.html.*;

import org.w3c.tidy.*;

import org.netbeans.modules.tasklist.core.TLUtils;


/** Special PrintWriter which has intimate knowledge of
    the "Report" class in org.w3c.tidy.Report. It uses
    this knowledge to decompose output written to the
    tidy print writer into separate error warnings that
    are then forwarded to the ErrorReporter. */
class ReportWriter extends PrintWriter {
    // XXX ErrorReporter - Shouldn't that be "ErrorPresenter" instead?
        private ErrorReporter reporter = null;
        private int line = -1;
        private int column = -1;
        private boolean warning = false;
        private StringBuffer sb = new StringBuffer(200);

        ReportWriter(ErrorReporter reporter) {
            super(new StringWriter()); // dummy string writer, don't need it
            this.reporter = reporter;
        }

        public void print(String msg) {
            if (msg.startsWith("Error: ")) { // from TidyMessages.properties
                warning = false;
                /* Leave Error prefix around: gives more weight to these
                if (msg.length() == 7) {
                    return;
                }
                msg = msg.substring(7); // Chop off Warning prefix
                */
            } else if (msg.startsWith("Warning: ")) { // from TidyMessages.properties
                warning = true;
                if (msg.length() == 8) {
                    return;
                }
                msg = msg.substring(8); // Chop off Warning prefix
            }
            // Special knowledge: when we get a print() and there has
            // been no output yet, it's probably a line/column
            // marker
            if ((sb.length() == 0) &&
                (msg.startsWith("line "))) { // should this be bundle chkd?
                      // for now, no other bundles
                      // in distribution
                int i = 5;
                int digit;
                line = 0;
                while (true) {
                    char c = msg.charAt(i++);
                    digit = Character.digit(c, 10);
                    if (digit == -1) {
                        break;
                    }
                    line *= 10;
                    line += digit;
                }
                // We don't care about the column yet so no point
                // processing it
            } else {
                sb.append(msg);
            }
        }

        public void println(String msg) {
            if (msg.startsWith("Error: ")) { // from TidyMessages.properties
                warning = false;
                /* Leave Error prefix around: gives more weight to these
                if (msg.length() == 7) {
                    return;
                }
                msg = msg.substring(7); // Chop off Warning prefix
                */
            } else if (msg.startsWith("Warning: ")) { // from TidyMessages.properties
                warning = true;
                if (msg.length() == 8) {
                    return;
                }
                msg = msg.substring(8); // Chop off Warning prefix
            }
            String content;
            if (sb.length() > 0) {
                sb.append(msg);
                content = sb.toString();
            } else {
                content = msg;
            }
            report(content);
        }

        public void println() {
            if (sb.length() > 0) {
                report(sb.toString());
            }
        }

        private void report(String msg) {
            reporter.reportError(line, column, !warning, msg);
            line = -1;
            column = -1;
            sb.setLength(0);
            warning = false;
        }
    }

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

package org.netbeans.modules.tasklist.html;

import java.io.PrintWriter;
import java.io.StringWriter;

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

/* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
/*
/* Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
/*
/* The contents of this file are subject to the terms of either the GNU
/* General Public License Version 2 only ("GPL") or the Common
/* Development and Distribution License("CDDL") (collectively, the
/* "License"). You may not use this file except in compliance with the
/* License. You can obtain a copy of the License at
/* http://www.netbeans.org/cddl-gplv2.html
/* or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
/* specific language governing permissions and limitations under the
/* License.  When distributing the software, include this License Header
/* Notice in each file and include the License file at
/* nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
/* particular file as subject to the "Classpath" exception as provided
/* by Sun in the GPL Version 2 section of the License file that
/* accompanied this code. If applicable, add the following below the
/* License Header, with the fields enclosed by brackets [] replaced by
/* your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
/*
/* Contributor(s):
 *
 * The Original Software is RemoteFS. The Initial Developer of the Original
/* Software is Libor Martinek. Portions created by Libor Martinek are
 * Copyright (C) 2000. All Rights Reserved.
/*
/* If you wish your version of this file to be governed by only the CDDL
/* or only the GPL Version 2, indicate your decision by adding
/* "[Contributor] elects to include this software in this distribution
/* under the [CDDL or GPL Version 2] license." If you do not indicate a
/* single choice of license, a recipient has the option to distribute
/* your version of this file under either the CDDL, the GPL Version 2 or
/* to extend the choice of license to its licensees as provided above.
/* However, if you add GPL Version 2 code and therefore, elected the GPL
/* Version 2 license, then the option applies only if the new code is
/* made subject to such option by the copyright holder.
 *
 * Contributor(s): Libor Martinek.
 */
package org.netbeans.modules.remotefs.ftpclient;

import java.io.*;

/** FTP Response class that reads and stores response from FTP server.
 *
 * @author  Libor Martinek
 * @version 1.0
 */
public class FTPResponse extends Object {

    /** FTP Response digit code. */
    private int code;
    /** Text message */
    private String response;
    /** Positive Preliminary response */
    public static final int POSITIVE_PRELIMINARY = 1;
    /** Positive Completion resposne */
    public static final int POSITIVE_COMPLETION = 2;
    /** Positive Intermediate response */
    public static final int POSITIVE_INTERMEDIATE = 3;
    /** Treansient Negative Completion response */
    public static final int TRANSIENT_NEGATIVE_COMPLETION = 4;
    /** Permanent Negative Completion */
    public static final int PERMANENT_NEGATIVE_COMPLETION = 5;

    /** Creates new FTPResponse and reads response from server.
     * @param in BufferedReader to read response from
     * @throws IOException
     */
    public FTPResponse(BufferedReader in) throws IOException {
        String line = in.readLine();
        // Get digit code
        String stringcode = null;
        if (line != null) {
            stringcode = line.substring(0, 3);
        }
        try {
            code = Integer.parseInt(stringcode);
        } catch (NumberFormatException e) {
            code = 0;
        }
        // Get Response
        if (line.length() >= 4 && line.charAt(3) == '-') {
            StringBuffer multiline = new StringBuffer();
            multiline.append(line);
            do {
                line = in.readLine();
                multiline.append("\n");
                multiline.append(line);
            } while (!line.startsWith(stringcode) || line.startsWith(stringcode + "-"));
            response = multiline.toString();
        } else {
            response = line;
        }
    }

    /** Writes response from FT server to Log stream
     * @param log
     */
    protected void writeLog(PrintWriter log) {
        if (log != null) {
            log.println(response);
            log.flush();
        }
    }

    /** Returns response.
     * @return String response
     */
    public String getResponse() {
        return response;
    }

    /** Returns response
     * @return String response
     */
    public String toString() {
        return response;
    }

    /** Returns code of response.
     * @return code
     */
    public int getCode() {
        return code;
    }

    /** Returns first digit of response code.
     * @return first digit of code
     */
    public int getFirstDigit() {
        return getCode() / 100;
    }

    /** Returns second digit of response code.
     * @return second digit of code
     */
    public int getSecondDigit() {
        return (getCode() % 100) / 10;
    }

    /** Returns third digit of response code.
     * @return third digit of code
     */
    public int getThirdDigit() {
        return getCode() % 10;
    }

    /** Test whether this response is Positive Preliminary.
     * @return
     */
    public boolean isPositivePreliminary() {
        return getFirstDigit() == POSITIVE_PRELIMINARY;
    }

    /** Test whether this response is Positive Completion.
     * @return
     */
    public boolean isPositiveCompletion() {
        return getFirstDigit() == POSITIVE_COMPLETION;
    }

    /** Test whether this response is Positive Intermediate.
     * @return
     */
    public boolean isPositiveIntermediate() {
        return getFirstDigit() == POSITIVE_INTERMEDIATE;
    }

    /** Test whether this response is Transient Negative Completion.
     * @return
     */
    public boolean isTransientNegativeCompletion() {
        return getFirstDigit() == TRANSIENT_NEGATIVE_COMPLETION;
    }

    /** Test whether this response is Permament Negative Completion.
     * @return 
     */
    public boolean isPermanentNegativeCompletion() {
        return getFirstDigit() == PERMANENT_NEGATIVE_COMPLETION;
    }
}

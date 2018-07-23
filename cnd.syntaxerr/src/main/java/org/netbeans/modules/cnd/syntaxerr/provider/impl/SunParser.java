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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.syntaxerr.provider.impl;

import org.netbeans.modules.cnd.syntaxerr.DebugUtils;

class SunParser extends BaseParser {

    protected void parseCompilerOutputLine(String line, String interestingFileName, ErrorBag errorBag) {
	if (DebugUtils.TRACE) {
	    System.err.printf("\tPARSING: \t%s\n", line);
	}
	boolean dummy = // unused: in C++, I would write just findErrorOrWarning(...) || findErrorOrWarning(...), but in Java I can't
	    findErrorOrWarning(line, ": Error: ", true, interestingFileName, errorBag) ||
	    findErrorOrWarning(line, ": Warning: ", false, interestingFileName, errorBag);
    }

    private boolean findErrorOrWarning(String line, String keyword, boolean error, String interestingFileName, ErrorBag errorBag) {
	int keywordPos = line.indexOf(keyword);
	if (keywordPos <= 0) {
	    return false;
	}
	String message = line.substring(keywordPos + keyword.length());
	if (!line.startsWith("\"")) {
	    return true;
	}
	int fileEndPos = 1;
	boolean backSlash = false;
	while (fileEndPos < line.length()) {
	    char c = line.charAt(fileEndPos);
	    if (c == '\"' && !backSlash) {
		break;
	    }
	    backSlash = (c == '\\');
	    fileEndPos++;
	}
	String fileName = line.substring(1, fileEndPos);
	if (!fileName.equals(interestingFileName)) {
	    return true;
	}
	int lineStartPos = line.indexOf("line", fileEndPos);
	if (lineStartPos < 0) {
	    return true;
	}
	lineStartPos += "line".length() + 1;
	int lineEndPos = line.indexOf(":", lineStartPos);
	if (lineEndPos < 0) {
	    return true;
	}
	String strPosition = line.substring(lineStartPos, lineEndPos).trim();
	int lineNum = Integer.parseInt(strPosition);
	int colNum = -1;
	if (DebugUtils.TRACE) {
	    System.err.printf("\t\tFILE: %s LINE: %8d COL: %d MESSAGE: %s\n", fileName, lineNum, colNum, message);
	}
	errorBag.add(message, error, lineNum, colNum);
	return true;
    }
}

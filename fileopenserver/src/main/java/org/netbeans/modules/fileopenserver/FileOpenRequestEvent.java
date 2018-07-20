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

package org.netbeans.modules.fileopenserver;

import java.util.EventObject;

/**
 * The file open request.
 *
 * @author Sandip Chitale (Sandip.Chitale@Sun.Com)
 */
public class FileOpenRequestEvent extends EventObject {
	
	public static final String OPEN_REQUEST    = "open";
	public static final String EXPLORE_REQUEST = "explore";
	
	private boolean _external;
	private String  _fileName;
	private int     _lineNumber = 0;
	private int     _columnNumber = 0;
	
	private String  _actionName;

	/**
	 * Request to open file in Eclipse with given filename.
	 * 
	 * @param source
	 * @param fileName
	 */
	public FileOpenRequestEvent(Object source, String fileName) {
		this(source, fileName, 0, 0);
	}


	/**
	 * Request to open file in Eclipse with given filename and position the curson at specified line and column.
	 * 
	 * @param source
	 * @param fileName
	 * @param lineNumber
	 * @param columnNumber
	 */
	public FileOpenRequestEvent(Object source, String fileName, int lineNumber, int columnNumber) {
		this(source, fileName, lineNumber, columnNumber, false);
	}

	/**
	 * 
	 * Request to open file in external editor with given filename and position the curson at specified line and column.
	 * 
	 * @param source
	 * @param fileName
	 * @param lineNumber
	 * @param columnNumber
	 * @param external
	 */
	public FileOpenRequestEvent(Object source, String fileName, int lineNumber, int columnNumber, boolean external) {
		super(source);
		_external = external;
		_fileName = fileName;
		_lineNumber = lineNumber;
		_columnNumber = columnNumber;
	}
	
	/**
	 * Returns if the request is to open the file in external editor.
	 */
	public boolean isExternal() {
		return _external;
	}
	
	/**
	 * Return the file name to open.
	 * 
	 * @return Returns the fileName.
	 */
	public String getFileName() {
		return _fileName;
	}
	
	/**
	 * Returns the lineNumber
	 * 
	 * @return Returns the lineNumber.
	 */
	public int getLineNumber() {
		return _lineNumber;
	}	
	
	/**
	 * @return Returns the _columnNumber.
	 */
	public int getColumnNumber() {
		return _columnNumber;
	}
}

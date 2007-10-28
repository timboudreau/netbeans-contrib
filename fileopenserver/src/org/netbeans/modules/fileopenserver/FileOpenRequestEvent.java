/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
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

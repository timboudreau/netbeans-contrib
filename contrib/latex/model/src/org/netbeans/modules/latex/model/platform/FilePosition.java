/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.latex.model.platform;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/** Represents a position in the viewed file.
 *
 *  @author Jan Lahoda
 */
public class FilePosition {
    
    private FileObject file;
    private int    line;
    private int    column;
    
    /** Creates a new instance of FilePosition */
    public FilePosition(FileObject file, int line, int column) {
        this.file = file;
        this.line = line;
        this.column = column;
    }
    
    /** Getter for property column.
     * @return Value of property column.
     *
     */
    public int getColumn() {
        return column;
    }
    
    /** Getter for property fileName.
     * @return Value of property fileName.
     *
     */
    public FileObject getFile() {
        return file;
    }
    
    /** Getter for property line.
     * @return Value of property line.
     *
     */
    public int getLine() {
        return line;
    }

    public String toString() {
        return FileUtil.getFileDisplayName(file) + ":" + line;
    }
    
}

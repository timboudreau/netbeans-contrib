/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

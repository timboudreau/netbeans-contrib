/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.latex.model.Utilities;

import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.NbDocument;
import org.openide.text.PositionRef;

/**Represents a position in the LaTeX source.
 *
 * @author Jan Lahoda
 */
public final class SourcePosition implements Serializable {
    
    private Object      file;
    private Document    doc;
    private Position    position;
    
    /**For use only during parsing for performance reasons!!!!!!!!!!
     */
    public SourcePosition(DataObject od, Document doc, int offset) {
        try {
            position  = doc.createPosition(offset);
            this.doc = doc;
            this.file = od.getPrimaryFile();
        } catch (BadLocationException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public SourcePosition(Document doc, int offset) {
        this(Utilities.getDefault().getFile(doc), doc, offset);
    }

    /** Creates a new instance of ParsePosition */
    public SourcePosition(Object file, Document doc, final Position offset) {
        this.file   = file;
        this.doc = doc;
        this.position = offset;
    }

    public SourcePosition(Object file, Document doc, int offset) {
        try {
            position  = doc.createPosition(offset);
            this.doc = doc;
            this.file = file;
        } catch (BadLocationException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof SourcePosition) {
            SourcePosition pos = (SourcePosition) obj;
            
            return file.equals(pos.file) && getOffsetValue() == pos.getOffsetValue();
        }
        
        return false;
    }
    
//    public SourcePosition(Object file, Document doc, int offset) {
//        this(file, doc, doc.createPosition(offset));
//    }
    
    public Object getFile() {
        return file;
    }
    
    public Position getOffset() {
        return position;
    }
    
    public int getLine() {
        return NbDocument.findLineNumber((StyledDocument) doc, position.getOffset());
    }
    
    public int getColumn() {
        return NbDocument.findLineColumn((StyledDocument) doc, position.getOffset());
    }
    
    public int getOffsetValue() {
        return position.getOffset();
    }
    
    public String toString() {
        return "Positon:[file=" + getFile() + ", line=" + getLine() + ", column=" + getColumn() + ", offset=" + getOffsetValue() + "]";
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(getFile());
        out.writeInt(getOffsetValue());
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        try {
        file = in.readObject();
        doc  = Utilities.getDefault().openDocument(file);
        position = doc.createPosition(in.readInt());
        } catch (BadLocationException e) {
            IOException toThrow = new IOException(e.getMessage());
            
            toThrow.initCause(e);
            
            throw toThrow;
        }
    }

}
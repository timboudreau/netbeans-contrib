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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
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
//    public SourcePosition(DataObject od, Document doc, int offset) {
//        try {
//            position  = doc.createPosition(offset);
//            this.doc = doc;
//            this.file = od.getPrimaryFile();
//        } catch (BadLocationException e) {
//            throw new IllegalStateException(e.getMessage());
//        }
//    }

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
            
            return Utilities.getDefault().compareFiles(file, pos.file) && getOffsetValue() == pos.getOffsetValue();
        }
        
        return false;
    }
    
    public int hashCode() {
        return 34;// ^ getOffsetValue();// ^ file.hashCode();
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
    
    public Document getDocument() {
        return doc;
    }
    
    public String toString() {
        return "Position:[file=" + getFile() + ", line=" + getLine() + ", column=" + getColumn() + ", offset=" + getOffsetValue() + "]";
    }
    
    public String dump() {
        return "Position:[file=" + Utilities.getDefault().getFileShortName(getFile()) + ", line=" + getLine() + ", column=" + getColumn() + ", offset=" + getOffsetValue() + "]";
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
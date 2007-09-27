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
package org.netbeans.modules.latex.loaders;

import java.io.*;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.latex.model.command.LaTeXSourceFactory;

import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.text.CloneableEditor;
import org.openide.text.DataEditorSupport;
import org.openide.text.DataEditorSupport.Env;
import org.openide.windows.CloneableOpenSupport;


import org.openide.cookies.*;
import org.openide.util.Lookup;

/** Support for editing a data object as text.
 *
 * @author Jan Lahoda
 */
// Replace OpenCookie with EditCookie or maybe ViewCookie as desired:
public class TexEditorSupport extends DataEditorSupport implements EditorCookie, OpenCookie, CloseCookie, PrintCookie, EditorCookie.Observable {
    
    private static final boolean debugReparse = Boolean.getBoolean("netbeans.debug.reparse.title");
    
    /** Create a new editor support.
     * @param obj the data object whose primary file will be edited as text
     */
    public TexEditorSupport(TexDataObject obj) {
        super(obj, new TexEnv(obj));
        // Set a MIME type as needed, e.g.:
        setMIMEType("text/x-tex");
    }
    
    public void open() {
        //Lets give the LaTeXSource managment system and the user opportunity
        //find out what is the main file of this file:
        for (LaTeXSourceFactory f : Lookup.getDefault().lookupAll(LaTeXSourceFactory.class)) {
            if (f.supports(getDataObject().getPrimaryFile())) {
                f.findMainFile(getDataObject().getPrimaryFile());
                break;
            }
        }
        
        super.open();
    }
    
//    private void updateTitlesImpl() {
//        synchronized (sourceLock) {
//            if (documentListener == null) {
//            }
//        }
//        
//        updateTitles();
//    }
    
    protected CloneableEditor createCloneableEditor () {
        TexCloneableEditorCreatorJustForUI creator = (TexCloneableEditorCreatorJustForUI) Lookup.getDefault().lookup(TexCloneableEditorCreatorJustForUI.class);
        
        if (creator != null)
            return creator.createCloneableEditor(this);
        
        return super.createCloneableEditor();
    }

    protected void initializeCloneableEditor(CloneableEditor editor) {
//        System.err.println("TexEditorSupport.initializeCloneableEditor start, editor=" + editor);
        super.initializeCloneableEditor(editor);
        
//        synchronized(sourceLock) {
//            if (source == null) {
//                Object file = getDataObject().getPrimaryFile();
//                
//                source = LaTeXSource.get(file);
//                
//                new SyntacticColoring(source);
//                sourceCount = 1;
//            } else {
//                sourceCount++;
//            }
//        }
        //TODO: Is this the right place?:
//        Model.getDefault().ensureParsed(getDataObject());
    }
    
//    protected void notifyClosed() {
//        super.notifyClosed();
//    }
    
    private static char[] symbols = new char[] {
        'v',
        '-',
        '+'
    };
    
    /** Called when the document is modified.
     * Here, adding a save cookie to the object and marking it modified.
     * @return true if the modification is acceptable
     */
    protected boolean notifyModified() {
        if (!super.notifyModified()) {
            return false;
        }
        TexDataObject obj = (TexDataObject)getDataObject();
        if (obj.getCookie(SaveCookie.class) == null) {
            obj.setModified(true);
            // You must implement this method on the object:
            obj.addSaveCookie(new Save());
        }
        return true;
    }
    
    /** Called when the document becomes unmodified.
     * Here, removing the save cookie from the object and marking it unmodified.
     */
    protected void notifyUnmodified() {
        TexDataObject obj = (TexDataObject)getDataObject();
        SaveCookie save = (SaveCookie)obj.getCookie(SaveCookie.class);
        if (save != null) {
            // You must implement this method on the object:
            obj.removeSaveCookie(save);
            obj.setModified(false);
        }
        super.notifyUnmodified();
    }
    
    public String getCharSet() {
        return ((TexDataObject) getDataObject()).getCharSet();
    }
    
    public void setCharSet(String nue) throws IOException {
        ((TexDataObject) getDataObject()).setCharSet(nue);
    }
    
//    public void open() {
//        if (
//        DialogDisplayer.getDefault().createDialog(new DialogDescriptor("", "Make Main Document"));
//    }
    
    /** A save cookie to use for the editor support.
     * When saved, saves the document to disk and marks the object unmodified.
     */
    private class Save implements SaveCookie {
        public void save() throws IOException {
            saveDocument();
            getDataObject().setModified(false);
        }
    }

    /**
     * Actually write file data to an output stream from an editor kit's document.
     * Called during a file save by {@link #saveDocument}.
     * <p>The default implementation just calls {@link EditorKit#write(OutputStream, Document, int, int) EditorKit.write(...)}.
     * Subclasses could override this to provide support for persistent guard blocks, for example.
     * @param doc the document to write from
     * @param kit the associated editor kit
     * @param stream the open stream to write to
     * @throws IOException if there was a problem writing the file
     * @throws BadLocationException should not normally be thrown
     * @see #loadFromStreamToKit
     */
    protected void saveFromKitToStream (StyledDocument doc, EditorKit kit, OutputStream stream) throws IOException, BadLocationException {
        Writer output;
        
        String charSet = getCharSet();
        
        if (null == charSet || "".equals(charSet))
            output = new OutputStreamWriter(stream);
        else
            output = new OutputStreamWriter(stream, charSet);

        kit.write(output, doc, 0, doc.getLength());
    }


    /**
     * Actually read file data into an editor kit's document from an input stream.
     * Called during a file load by {@link #prepareDocument}.
     * <p>The default implementation just calls {@link EditorKit#read(InputStream, Document, int) EditorKit.read(...)}.
     * Subclasses could override this to provide support for persistent guard blocks, for example.
     * @param doc the document to read into
     * @param stream the open stream to read from
     * @param kit the associated editor kit
     * @throws IOException if there was a problem reading the file
     * @throws BadLocationException should not normally be thrown
     * @see #saveFromKitToStream
     */
    protected void loadFromStreamToKit (StyledDocument doc, InputStream stream, EditorKit kit) throws IOException, BadLocationException {
        Reader input;
        
        String charSet = getCharSet();
        
        if (null == charSet || "".equals(charSet))
            input = new InputStreamReader(stream);
        else
            input = new InputStreamReader(stream, charSet);

        kit.read(input, doc, 0);
    }
    
    private void updateTitlesImpl() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run(){
                updateTitles();
            }
        });
    }
    
    /** A description of the binding between the editor support and the object.
     * Note this may be serialized as part of the window system and so
     * should be static, and use the transient modifier where needed.
     */
    private static class TexEnv extends Env {
        
        private static final long serialVersionUID = -4792346465387686993L;
        
        /** Create a new environment based on the data object.
         * @param obj the data object to edit
         */
        public TexEnv(TexDataObject obj) {
            super(obj);
        }
        
        /** Get the file to edit.
         * @return the primary file normally
         */
        protected FileObject getFile() {
            return getDataObject().getPrimaryFile();
        }
        
        /** Lock the file to edit.
         * Should be taken from the file entry if possible, helpful during
         * e.g. deletion of the file.
         * @return a lock on the primary file normally
         * @throws IOException if the lock could not be taken
         */
        protected FileLock takeLock() throws IOException {
            return ((TexDataObject)getDataObject()).getPrimaryEntry().takeLock();
        }
        
        /** Find the editor support this environment represents.
         * Note that we have to look it up, as keeping a direct
         * reference would not permit this environment to be serialized.
         * @return the editor support
         */
        public CloneableOpenSupport findCloneableOpenSupport() {
            return (TexEditorSupport)getDataObject().getCookie(TexEditorSupport.class);
        }
        
    }
    
}

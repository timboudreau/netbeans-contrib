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

package org.netbeans.modules.erd.io;

import java.io.IOException;
import org.netbeans.modules.dbschema.SchemaElement;
import org.netbeans.modules.erd.editor.ERDEditorSupport;
import org.netbeans.modules.erd.model.DocumentSerializer;
import org.netbeans.modules.erd.model.ERDDocumentAwareness;
import org.openide.cookies.EditCookie;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;

public class ERDDataObject extends MultiDataObject {
    

    
    private ERDEditorSupport editorSupport;
    private OpenEdit openEdit;
    private DocumentSerializer serializer;
    private Object lock=new Object();
    private FileObject erdFile;
    public ERDDataObject(FileObject erdfile, ERDDataLoader loader) throws DataObjectExistsException, IOException {
        super(erdfile, loader);
        CookieSet cookies = getCookieSet();
        editorSupport=new ERDEditorSupport(this);
        serializer=new DocumentSerializer(this);
        erdFile=erdfile; 
        cookies.add( editorSupport);
    }
    
    protected Node createNodeDelegate() {
        return new ERDDataNode(this);
    }
    

    public void addSaveCookie (SaveCookie save) {
        getCookieSet ().add (save);
    }

    public void removeSaveCookie (SaveCookie save) {
        getCookieSet ().remove (save);
    }

    public void setModified (boolean modif) {
        super.setModified (modif); // TODO
    }

    public FileObject getERDFile(){
        return erdFile;
    }    
    
   
    public ERDEditorSupport getEditorSupport(){
       return editorSupport;    
    }
    
    
   /* public Node.Cookie createCookie(Class klass) {
        if (OpenCookie.class.equals(klass)  ||  EditCookie.class.equals(klass)) {
            if (openEdit == null)
                openEdit = new OpenEdit();
            return openEdit;
        }

        return null;
    }*/
    
    private class OpenEdit implements OpenCookie, EditCookie {
        public void open() {
            
            final ERDEditorSupport es = getEditorSupport();
            if (es != null)
                es.openERD();

       
        }
        public void edit() {
            
           final ERDEditorSupport es = getEditorSupport();
            if (es != null)
                es.openERD();

        }
    }
    
    
    public void addDesignDocumentAwareness (ERDDocumentAwareness listener) {
     getDocumentSerializer().addDesignDocumentAwareness(listener);       
    }
    
   /* public DataObjectContext getContext () {
        return context;
    }*/

    /*public DesignEditorSupport getEditorSupport () {
        return editorSupport;
    }*/

    public DocumentSerializer getDocumentSerializer () {
        synchronized (lock) {
            if (serializer == null)
                serializer = new DocumentSerializer (this);
            return serializer;
        }
    }

    /*public CodeResolver getCodeResolver () {
        return codeResolver;
    }*/

    public void notifyClosed () {
        serializer = null;
    }
    
}

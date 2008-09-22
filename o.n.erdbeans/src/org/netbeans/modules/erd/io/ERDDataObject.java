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

package org.netbeans.modules.erd.io;

import java.io.IOException;
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
import org.openide.util.Lookup;

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

    @Override
    public Lookup getLookup() {
        return getCookieSet().getLookup();
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

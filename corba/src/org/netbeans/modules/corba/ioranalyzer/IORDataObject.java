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

package org.netbeans.modules.corba.ioranalyzer;

import java.io.*;
import org.openide.loaders.*;
import org.openide.filesystems.*;
import org.openide.nodes.*;
import org.openide.cookies.*;
import org.openide.text.*;
import org.openide.util.*;

public class IORDataObject extends MultiDataObject implements FileChangeListener {

    static final long serialVersionUID = 2206846110094280146L;
    private  IOREditorSupport editorCookie;

    public IORDataObject (FileObject fo, MultiFileLoader loader) throws DataObjectExistsException {
	super (fo, loader);
        fo.addFileChangeListener (this);
    }
    
    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public Node.Cookie getCookie (Class clazz) {
        if (clazz.isAssignableFrom(IOREditorSupport.class)) {
            this.lazyInit();
            return this.editorCookie;
        }
        else
            return super.getCookie(clazz);
    }
    
    protected Node createNodeDelegate () {
        return new IORNode (this);
    }
    
    public String getContent () {
        FileObject fobj = getPrimaryFile();
        BufferedReader in = null;
        try {
            in = new BufferedReader ( new InputStreamReader ( fobj.getInputStream()));
            return in.readLine();
        }catch (IOException e) {
            return null;
        }
        finally {
            if (in != null)
                try {
                    in.close();
                }catch (IOException ioe){}
        }
    }

	public CookieSet getCookieSet0 () {
		return this.getCookieSet();		
	}

    public void fileDeleted(final org.openide.filesystems.FileEvent event) {
    }
    
    public void fileDataCreated(final org.openide.filesystems.FileEvent event) {
    }
    
    public void fileFolderCreated(final org.openide.filesystems.FileEvent event) {
    }
    
    public void fileRenamed(final org.openide.filesystems.FileRenameEvent event) {
    }
    
    public void fileAttributeChanged(final org.openide.filesystems.FileAttributeEvent event) {
    }
    
    public void fileChanged(final org.openide.filesystems.FileEvent event) {
        org.openide.nodes.Children cld = this.getNodeDelegate().getChildren();
        if (cld instanceof ProfileChildren) {
            ((ProfileChildren)cld).update();
        }
    }
    
    public boolean isLoaded () {
        if (this.editorCookie == null)
            return false;
        return editorCookie.isDocumentLoaded();
    }
    
    private void lazyInit () {
        if (this.editorCookie == null)
            this.editorCookie = new IOREditorSupport (this);
    }
    
}

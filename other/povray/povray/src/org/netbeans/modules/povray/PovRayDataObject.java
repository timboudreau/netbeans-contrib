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
/*
 * PovRayDataObject.java
 *
 * Created on February 16, 2005, 2:29 PM
 */

package org.netbeans.modules.povray;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.netbeans.api.povproject.MainFileProvider;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.cookies.EditCookie;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Node;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.DataEditorSupport;
import org.openide.util.HelpCtx;
import org.openide.windows.CloneableOpenSupport;

/**
 * Data object representing a pov-ray .pov or .inc scene file. 
 *
 * @author Timothy Boudreau
 */
public class PovRayDataObject extends MultiDataObject {
    
    public PovRayDataObject(FileObject file, MultiFileLoader ldr) throws DataObjectExistsException {
        super(file, ldr);
        //DataEditorSupport has a factory for simple plain-text editing support;
        //we use it here to make it possible to double click .pov files to
        //edit them
        Node.Cookie cookie = (Node.Cookie)DataEditorSupport.create(this, getPrimaryEntry(), getCookieSet ());
        getCookieSet ().add (cookie);
        // ( for more info, see 
        //  http://www.netbeans.org/issues/show_bug.cgi?id=17081 )
    }
    
    public Node createNodeDelegate() {
        return new PovRayDataNode(this);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public FileObject handleMove(DataFolder df) throws IOException {
        FileObject file = getPrimaryFile();
        Project project = FileOwnerQuery.getOwner(file);
        boolean isMainFile = false;
        MainFileProvider provider = null;
        if (project != null) {
            provider = (MainFileProvider) project.getLookup().lookup(MainFileProvider.class);
            if (provider != null) { //could be if we're for some reason in a java project
                isMainFile = provider.getMainFile().equals(file);
            }
        }
        
        FileObject result = super.handleMove(df);
        if (isMainFile) {
            Project project2 = FileOwnerQuery.getOwner(result);
            if (project2 == project && project != null) {
                provider.setMainFile(result);
            } else {
                provider.setMainFile(null);
            }
        }
        return result;
    }
    
    public FileObject handleRename(String name) throws IOException {
        FileObject file = getPrimaryFile();
        Project project = FileOwnerQuery.getOwner(file);
        boolean isMainFile = false;
        MainFileProvider provider = null;
        if (project != null) {
            provider = (MainFileProvider) project.getLookup().lookup(MainFileProvider.class);
            if (provider != null) { //could be if we're for some reason in a java project
                isMainFile = provider.getMainFile().equals(file);
            }
        }
        
        FileObject result = super.handleRename(name);
        if (isMainFile) {
            provider.setMainFile(result);
        }
        return result;
    }
}

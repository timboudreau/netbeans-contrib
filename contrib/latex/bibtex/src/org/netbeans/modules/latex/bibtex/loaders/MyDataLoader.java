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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
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
package org.netbeans.modules.latex.bibtex.loaders;

import java.io.IOException;

import org.openide.actions.CopyAction;
import org.openide.actions.CutAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.EditAction;
import org.openide.actions.FileSystemAction;
import org.openide.actions.OpenAction;
import org.openide.actions.PasteAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.RenameAction;
import org.openide.actions.SaveAsTemplateAction;
import org.openide.actions.ToolsAction;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.ExtensionList;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/** Recognizes single files in the Repository as being of a certain type.
 *
 * @author Jan Lahoda
 */
public class MyDataLoader extends UniFileLoader {
    
    public MyDataLoader() {
        this("org.netbeans.modules.latex.bibtex.loaders.BiBTexDataObject");
    }
    
    // Can be useful for subclasses:
    protected MyDataLoader(String recognizedObjectClass) {
        super(recognizedObjectClass);
    }
    
    protected String defaultDisplayName() {
        return NbBundle.getMessage(MyDataLoader.class, "LBL_loaderName");
    }
    
    protected void initialize() {
        super.initialize();
        
        ExtensionList extensions = new ExtensionList();
        extensions.addMimeType("text/x-bibtex");
        setExtensions(extensions);
    }
    
    protected SystemAction[] defaultActions() {
        return new SystemAction[] {
            SystemAction.get(OpenAction.class),
            SystemAction.get(EditAction.class),
            // SystemAction.get(CustomizeBeanAction.class),
            SystemAction.get(FileSystemAction.class),
            null,
            /*
            SystemAction.get(CompileAction.class),
            null,
            SystemAction.get(BuildAction.class),
            null,
            SystemAction.get(ExecuteAction.class),
            null,
             */
            SystemAction.get(CutAction.class),
            SystemAction.get(CopyAction.class),
            SystemAction.get(PasteAction.class),
            null,
            SystemAction.get(DeleteAction.class),
            SystemAction.get(RenameAction.class),
            null,
            SystemAction.get(SaveAsTemplateAction.class),
            null,
            SystemAction.get(ToolsAction.class),
            SystemAction.get(PropertiesAction.class),
        };
    }
    
    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new BiBTexDataObject(primaryFile, this);
    }
    
    // Additional user-configurable properties:
    /*
    public String getMyProp() {
        return (String)getProperty("myProp");
    }
    public void setMyProp(String nue) {
        putProperty("myProp", nue, true);
    }
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeUTF(getMyProp());
    }
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        setMyProp(in.readUTF());
    }
     */
    
}

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

package org.netbeans.modules.docbook.resources.solbook;

import java.io.IOException;

import org.openide.actions.*;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

public class SolBookDataLoader extends UniFileLoader {

    
    public static final String MIME_SOLBOOK = "text/x-solbook+xml";

    private static final long serialVersionUID = 1L;

    public SolBookDataLoader() {
        super("org.netbeans.modules.docbook.resources.solbook.SolBookDataObject");
    }
    
    protected String defaultDisplayName() {
        return NbBundle.getMessage(SolBookDataLoader.class, "LBL_loaderName");
    }
    
    protected void initialize() {
        super.initialize();
        ExtensionList extensions = new ExtensionList();
//        extensions.addMimeType(MIME_SLIDES);
//        extensions.addMimeType(MIME_DOCBOOK);
        extensions.addMimeType(MIME_SOLBOOK);
        setExtensions(extensions);
    }
/*    protected SystemAction[] defaultActions() {
//        SystemAction[] s = new SystemAction[] {
//            SystemAction.get(OpenAction.class),
//            SystemAction.get(FileSystemAction.class),
//            null,
//            SystemAction.get(ToHtmlAction.class),
//            null,
//            SystemAction.get(CutAction.class),
//            SystemAction.get(CopyAction.class),
//            SystemAction.get(PasteAction.class),
//            null,
//            SystemAction.get(DeleteAction.class),
//            SystemAction.get(RenameAction.class),
//            null,
//            SystemAction.get(SaveAsTemplateAction.class),
//            null,
//            SystemAction.get(ToolsAction.class),
//            SystemAction.get(PropertiesAction.class),
//        };
        SystemAction[] s = super.defaultActions();
        SystemAction[] result = new SystemAction[s.length + 1];
        System.arraycopy (s, 0, result, 1, s.length);
        result[0] = result[1];
        result[1] = SystemAction.get (ToHtmlAction.class);
        return result;
    }
 */ 
    
    protected String actionsContext () {
        return "Loaders/text/x-solbook+xml/Actions"; // NOI18N
    }

    
    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new SolBookDataObject(primaryFile, this);
    }
    
}

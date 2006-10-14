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

package org.netbeans.modules.docbook;

import java.io.IOException;

import org.openide.actions.*;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

public class DocBookDataLoader extends UniFileLoader {

    public static final String MIME_SLIDES = "text/x-docbook-slides+xml";
    public static final String MIME_DOCBOOK = "text/x-docbook+xml";

    private static final long serialVersionUID = 1L;

    public DocBookDataLoader() {
        super("org.netbeans.modules.docbook.DocBookDataObject");
    }
    
    protected String defaultDisplayName() {
        return NbBundle.getMessage(DocBookDataLoader.class, "LBL_loaderName");
    }
    
    protected void initialize() {
        super.initialize();
        ExtensionList extensions = new ExtensionList();
        extensions.addMimeType(MIME_SLIDES);
        extensions.addMimeType(MIME_DOCBOOK);
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
        return "Loaders/text/x-docbook+xml/Actions"; // NOI18N
    }

    
    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new DocBookDataObject(primaryFile, this);
    }
    
}

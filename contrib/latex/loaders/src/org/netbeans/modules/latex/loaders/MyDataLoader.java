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
package org.netbeans.modules.latex.loaders;

import java.io.IOException;

import org.openide.actions.CopyAction;
import org.openide.actions.CutAction;
import org.openide.actions.DeleteAction;
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
        this("org.netbeans.modules.latex.loaders.TexDataObject");
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
        extensions.addExtension("tex");
        extensions.addExtension("latex");
        setExtensions(extensions);
    }
    
    protected SystemAction[] defaultActions() {
        return new SystemAction[] {
            SystemAction.get(OpenAction.class),
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
        return new TexDataObject(primaryFile, this);
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

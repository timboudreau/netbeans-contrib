/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.group;


import org.openide.actions.*;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.NbBundle;


/** 
 * Loader for <code>GroupShadow</code> data object.
 *
 * @author Jaroslav Tulach
 */
public class GroupShadowLoader extends DataLoader {

    /** Generated serial veriosn UID. */
    static final long serialVersionUID =-2768192459953761627L;

    /** Constants. */
    public static final String GS_EXTENSION = "group"; // NOI18N
    
    /** List of extensions. */
    private ExtensionList extensions;

    
    /** Creates loader. */
    public GroupShadowLoader () {
        super("org.netbeans.modules.group.GroupShadow"); // NOI18N
        
        extensions = new ExtensionList();
        extensions.addExtension(GS_EXTENSION);
    }
    
    
    /** Gets default display name. Overrides superclass method. */
    protected String defaultDisplayName() {
        return NbBundle.getMessage(GroupShadowLoader.class, "PROP_GroupShadowName");
    }
    
    /** Gets default system actions. Overrides superclass method. */
    protected SystemAction[] defaultActions() {
        return new SystemAction[] {
            SystemAction.get(OpenLocalExplorerAction.class),
            SystemAction.get(FileSystemAction.class),
            null,
            SystemAction.get(CompileAction.class),
            SystemAction.get(CompileAllAction.class),
            null,
            SystemAction.get(BuildAction.class),
            SystemAction.get(BuildAllAction.class),
            null,
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
            SystemAction.get(PropertiesAction.class)
        };
    }

    
    /** Finds appropriate data object for given file object. Overrides superclass method. 
     * If the extension is registered then gets <code>GroupShadow</code>. */
    protected DataObject handleFindDataObject (
        FileObject fo, DataLoader.RecognizedFiles recognized
    ) throws java.io.IOException {
        if (getExtensions().isRegistered(fo)) {
            return new GroupShadow(fo, this);
        }
        return null;
    }

    /** Getter for <code>extensions</code> property. */
    public ExtensionList getExtensions() {
        return extensions;
    }

    /** Setter for <code>extensions</code> property. */
    public void setExtensions(ExtensionList extensions) {
        this.extensions = extensions;
    }
}

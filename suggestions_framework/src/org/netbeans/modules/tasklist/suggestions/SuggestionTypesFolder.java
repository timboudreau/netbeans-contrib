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

package org.netbeans.modules.tasklist.suggestions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.FolderInstance;
import org.openide.filesystems.Repository;

/** Representation of the "Suggestions/Types" folder. All
 * instances created through the createInstance() method are
 * stored in Map and passed to SuggestionType.setTypes(). This
 * class should only be responsible for processing of the folder, 
 * listening of the changes in folder etc. Clients should use 
 * SuggestionType.getType and other methods in SuggestionType 
 * for access to SuggestionTypes.
 * <p>
 * Based on AnnotationTypesFolder in the editor package.
 * <p>
 *
 * @author Tor Norbye, David Konecny
 */
final public class SuggestionTypesFolder extends FolderInstance {
    
    /** folder for suggestion type XML files */
    private static final String FOLDER = "Suggestions/Types";
    
    /** instance of this class */
    private static SuggestionTypesFolder folder;

    /** map of suggestiontype_name <-> SuggestionType_instance*/
    private Map suggestionTypes;

    /** FileObject which represent the folder with suggestion types*/
    private FileObject fo;
    
    /** Creates new SuggestionTypesFolder */
    private SuggestionTypesFolder(FileObject fo, DataFolder fld) {
        super(fld);
        recreate();
        instanceFinished();
        
        this.fo = fo;

        /* XXX Not needed
        // add listener on changes in suggestion types folder
        fo.addFileChangeListener(new FileChangeAdapter() {
            public void fileDeleted(FileEvent fe) {
                SuggestionType type;
                for (Iterator it = SuggestionTypes.getTypes().getSuggestionTypeNames(); it.hasNext(); ) {
                    type = SuggestionTypes.getTypes().getType((String)it.next());
                    if ( type != null && ((FileObject)type.getProp(SuggestionType.PROP_FILE)).equals(fe.getFile()) ) {
                        SuggestionTypes.getTypes().removeType(type.getName());
                        break;
                    }
                }
            }
        });
        */
    }

    /** Gets SuggestionTypesFolder singleton instance. */
    public static synchronized SuggestionTypesFolder getSuggestionTypesFolder(){
        if (folder != null) {
            return folder;
        }
        
        FileObject f = Repository.getDefault().getDefaultFileSystem().findResource(FOLDER);
        if (f == null) {
            return null;
        }
        
        try {
            DataObject d = DataObject.find(f);
            DataFolder df = (DataFolder)d.getCookie(DataFolder.class);
            if (df != null)
                folder = new SuggestionTypesFolder(f, df);
        } catch (DataObjectNotFoundException ex) {
            org.openide.ErrorManager.getDefault().notify(org.openide.ErrorManager.INFORMATIONAL, ex);
            return null;
        }
        return folder;
    }

    /** Called for each XML file found in FOLDER directory */
    protected Object createInstance(InstanceCookie[] cookies) throws IOException, ClassNotFoundException {
        suggestionTypes = new HashMap(cookies.length * 4 / 3);
        
        for (int i = 0; i < cookies.length; i++) {
            Object o = cookies[i].instanceCreate();
            if (o instanceof SuggestionType) {
                SuggestionType type = (SuggestionType)o;
                type.setPosition(i);
                suggestionTypes.put(type.getName(), type);
            }
        }
        
        // set all these types to SuggestionType static member
        SuggestionTypes.getTypes().setTypes(suggestionTypes);
        
        return null;
    }
}

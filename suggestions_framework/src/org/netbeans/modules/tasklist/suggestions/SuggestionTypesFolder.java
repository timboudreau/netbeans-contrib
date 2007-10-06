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
        SuggestionTypes.getDefault().setTypes(suggestionTypes);
        
        return null;
    }
}

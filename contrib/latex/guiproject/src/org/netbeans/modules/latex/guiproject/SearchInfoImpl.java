/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.latex.guiproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openidex.search.SearchInfo;

/**
 *
 * @author Jan Lahoda
 */
public final class SearchInfoImpl implements SearchInfo {
    
    private LaTeXGUIProject p;
    
    /** Creates a new instance of SearchInfoImpl */
    public SearchInfoImpl(LaTeXGUIProject p) {
        this.p = p;
    }

    public boolean canSearch() {
        return true;
    }

    public Iterator objectsToSearch() {
        List result = new ArrayList();
        
        for (Iterator i = p.getContainedFiles().iterator(); i.hasNext(); ) {
            FileObject file = (FileObject) i.next();
            
            try {
                DataObject od = DataObject.find(file);
                result.add(od);
            } catch (DataObjectNotFoundException ex) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            }
        }
        
        return Collections.unmodifiableCollection(result).iterator();
    }
    
}

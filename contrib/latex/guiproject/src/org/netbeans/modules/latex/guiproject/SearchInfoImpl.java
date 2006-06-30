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

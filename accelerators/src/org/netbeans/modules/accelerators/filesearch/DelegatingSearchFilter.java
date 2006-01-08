/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is the Accelerators module. 
 * The Initial Developer of the Original Code is Andrei Badea. 
 * Portions Copyright 2005-2006 Andrei Badea.
 * All Rights Reserved.
 * 
 * Contributor(s): Andrei Badea
 */

package org.netbeans.modules.accelerators.filesearch;

import org.openide.filesystems.FileObject;

/**
 *
 * @author Andrei Badea
 */
public class DelegatingSearchFilter implements SearchFilter {
    
    final SearchFilter[] delegates;
    
    public DelegatingSearchFilter(SearchFilter delegate1, SearchFilter delegate2, SearchFilter delegate3) {
        this(new SearchFilter[] { delegate1, delegate2, delegate3 });
    }
    
    public DelegatingSearchFilter(SearchFilter[] delegates) {
        this.delegates = delegates;
    }

    public boolean accept(FileObject fo) {
        for (int i = 0; i < delegates.length; i++) {
            if (!delegates[i].accept(fo)) {
                return false;
            }
        }
        return true;
    }
}

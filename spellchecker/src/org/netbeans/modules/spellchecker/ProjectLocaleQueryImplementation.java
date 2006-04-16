/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.spellchecker;

import java.util.Locale;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.spellchecker.spi.LocaleQueryImplementation;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Jan Lahoda
 */
public class ProjectLocaleQueryImplementation implements LocaleQueryImplementation {
    
    /** Creates a new instance of ProjectLocaleQueryImplementation */
    public ProjectLocaleQueryImplementation() {
    }

    public Locale findLocale(FileObject file) {
        Project p = FileOwnerQuery.getOwner(file);
        
        if (p != null) {
            LocaleQueryImplementation i = (LocaleQueryImplementation) p.getLookup().lookup(LocaleQueryImplementation.class);
            
            if (i != null) {
                return i.findLocale(file);
            }
        }
        
        return null;
    }
    
}

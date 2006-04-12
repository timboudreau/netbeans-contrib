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
package org.netbeans.modules.spellchecker.api;

import java.util.Collection;
import java.util.Locale;
import org.netbeans.modules.spellchecker.spi.LocaleQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Template;

/**
 *
 * @author Jan Lahoda
 */
public final class LocaleQuery {
    
    private LocaleQuery() {
    }

    public static Locale findLocale(FileObject file) {
        for (/*@SuppressWarnings("unchecked") */LocaleQueryImplementation i : (Collection<LocaleQueryImplementation>)Lookup.getDefault().lookup(new Template(LocaleQueryImplementation.class)).allInstances()) {
            Locale l = i.findLocale(file);

            if (l != null)
                return l;
        }

        return Locale.getDefault();
    }

}

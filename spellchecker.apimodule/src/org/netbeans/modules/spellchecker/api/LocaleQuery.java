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

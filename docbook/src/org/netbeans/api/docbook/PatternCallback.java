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
package org.netbeans.api.docbook;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.netbeans.modules.docbook.ParseJobFactory;
import org.openide.filesystems.FileObject;


/**
 * Callback implementation which provides a regexp pattern.
 */ 
public abstract class PatternCallback<T extends Pattern> extends Callback {
    public PatternCallback(Pattern pattern) {
        super (pattern);
        if (pattern == null) {
            throw new NullPointerException("Pattern null");
        }
    }

    /**
     * Callback which is invoked as the regular expression is processed.
     * Will be called once for each match to the pattern that is found.
     * 
     * @return false if no further matches are needed, true otherwise
     */ 
    public abstract boolean process(FileObject f, MatchResult match, CharSequence content);

    final boolean doCancel(FileObject ob) {
        ParseJobFactory.cancelled (this, ob);
        return true;
    }
    
    public String toString() {
        return "PatternCallback@" + System.identityHashCode(this) + "=" + getProcessor();
    }
}
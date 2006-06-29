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

package org.netbeans.modules.searchandreplace.model;

/**
 * Encapsulates all of the parameters that describe a text search.
 * Created by the panel the user fills in in the search dialog.
 *
 * @see org.netbeans.modules.searchandreplace.SearchInput
 *
 * @author Tim Boudreau
 */
public final class SearchDescriptor implements Cloneable {
    private final boolean includeIgnored;
    private final String searchText;
    private final String replaceText;
    private final boolean shouldReplace;
    private final boolean subfolders;
    private final boolean searchBinary;
    private final boolean caseSensitive;
    
    /** Creates a new instance of SearchDescriptor */
    public SearchDescriptor(final String searchText, final String replaceText, final boolean shouldReplace, final boolean caseSensitive, final boolean searchBinary, final boolean subfolders, final boolean includeIgnored) {
        this.searchText = searchText;
        this.replaceText = replaceText;
        this.shouldReplace = shouldReplace;
        this.caseSensitive = caseSensitive;
        this.searchBinary = searchBinary;
        this.subfolders = subfolders;
        this.includeIgnored = includeIgnored;
    }

    public boolean isIncludeIgnored() {
        return includeIgnored;
    }

    public String getSearchText() {
        return searchText;
    }

    public String getReplaceText() {
        return replaceText;
    }

    public boolean isShouldReplace() {
        return shouldReplace;
    }

    public boolean isIncludeSubfolders() {
        return subfolders;
    }

    public boolean isIncludeBinaryFiles() {
        return searchBinary;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }
}

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

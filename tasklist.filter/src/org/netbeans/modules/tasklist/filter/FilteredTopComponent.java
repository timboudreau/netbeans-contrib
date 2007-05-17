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

package org.netbeans.modules.tasklist.filter;

/**
 * FilterAction will work for all views that implement this interface.
 */
public interface FilteredTopComponent {
    /**
     * Returns the corresponding repository of filters
     *
     * @return repository
     */
    FilterRepository getFilters();

    /**
     * Returns the active filter.
     *
     * @return active filter or null
     */
    Filter getFilter();

    /**
     * Sets another active filter.
     *
     * @param f a new active filter or null
     */
    void setFilter(Filter f);
    
    /**
     * Creates a new empty filter.
     *
     * @return created filter
     */
    Filter createFilter();
}

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

package org.netbeans.modules.debugger.localsviewenhancements.ui.models;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public interface Constants {
    /**
     * Locals Modifiers column id.
     *
     * @see org.netbeans.spi.viewmodel.ColumnModel#getID
     * @see org.netbeans.spi.viewmodel.ColumnModel#getPreviuosColumnID
     * @see org.netbeans.spi.viewmodel.ColumnModel#getNextColumnID
     */
    public static final String LOCALS_MODIFIERS_COLUMN_ID = "LocalsModifiers";

    /**
     * Locals Declared Type column id.
 *
     * @see org.netbeans.spi.viewmodel.ColumnModel#getID
     * @see org.netbeans.spi.viewmodel.ColumnModel#getPreviuosColumnID
     * @see org.netbeans.spi.viewmodel.ColumnModel#getNextColumnID
     */
    public static final String LOCALS_DECLARED_TYPE_COLUMN_ID = "LocalsDeclaredType";
    
    /**
     * Locals Declared In column id.
     *
     * @see org.netbeans.spi.viewmodel.ColumnModel#getID
     * @see org.netbeans.spi.viewmodel.ColumnModel#getPreviuosColumnID
     * @see org.netbeans.spi.viewmodel.ColumnModel#getNextColumnID
     */
    public static final String LOCALS_DECLARED_IN_COLUMN_ID = "LocalsDeclaredIn";
}

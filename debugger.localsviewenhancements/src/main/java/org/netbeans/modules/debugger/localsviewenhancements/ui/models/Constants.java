/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
    
    /**
     * Locals Object Id column id.
     *
     * @see org.netbeans.spi.viewmodel.ColumnModel#getID
     * @see org.netbeans.spi.viewmodel.ColumnModel#getPreviuosColumnID
     * @see org.netbeans.spi.viewmodel.ColumnModel#getNextColumnID
     */
    public static final String LOCALS_OBJECT_ID_COLUMN_ID = "LocalsObjectId";
}

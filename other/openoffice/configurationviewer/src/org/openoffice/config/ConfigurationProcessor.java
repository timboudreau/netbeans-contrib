/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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

package org.openoffice.config;

import com.sun.star.uno.XInterface;

/**
 * 
 * @author S. Aubrecht
 */
interface ConfigurationProcessor {
    /**
     * Process value element.
     *
     * @param configPath Full configuration path where the given value is stored at.
     * @param userValue Configuration value from user's settings.
     * @param isUserDefaultValue True if user's configuration value hasn't been modified (i.e. is the default one)
     * @param sharedValue Configuration value from shared settings.
     * @param isUserDefaultValue True if shared configuration value hasn't been modified (i.e. is the default one)
     */
    public void processValueElement( String configPath, 
            Object userValue, boolean isUserDefaultValue, 
            Object sharedValue, boolean isSharedDefaultValue );

    /**
     * Process structural element (sub-tree node).
     *
     * @param sPath Full configuration path to the given sub-tree.
     * @param userElement Sub-tree from user's settings.
     * @param sharedElement Sub-tree from shared settings.
     */
    public void processStructuralElement( String sPath, XInterface userElement, XInterface sharedElement );
    
    /**
     * Invoked when all values and nodes have been processed, apply final formating here.
     */
    public void format();
}

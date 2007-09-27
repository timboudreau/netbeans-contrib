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
package org.netbeans.modules.registry.mergedctx;

import org.netbeans.api.registry.ContextException;
import org.netbeans.spi.registry.BasicContext;

final class MaskUtils {
    private static final String MASK_EXTENSION = "_mergehidden";//NOI18N


    static boolean existMaskForCtx(/*MergedDelegates delegates*/final BasicContext activeCtx, final String subcontextName) {
        //final BasicContext activeCtx = delegates.getActiveDelegate(false);
        if (activeCtx == null) return false;
        return (activeCtx.getSubcontextNames().contains(subcontextName + MASK_EXTENSION));
    }

    static boolean existMaskForBinding(final BasicContext activeCtx, final String bindingName) {
        if (activeCtx == null) return false;
        return (activeCtx.getBindingNames().contains((bindingName + MASK_EXTENSION)));//lookupObject(bindingName + MASK_EXTENSION) != null);
    }

    static boolean existMaskForAttributes(final BasicContext activeCtx, final String bindingName, final String attributeName) {
        if (activeCtx == null) return false;
        return (activeCtx.getAttributeNames(bindingName).contains(attributeName + MASK_EXTENSION));        
    }

    static boolean isMaskForCtxName(final String contextName) {
        return contextName.endsWith(MASK_EXTENSION);
    }

    static boolean isMaskForBindingName(final String bindingName) {
        return bindingName.endsWith(MASK_EXTENSION);
    }

    static boolean isMaskForAttributeName(final String attributeName) {
        return attributeName.endsWith(MASK_EXTENSION);

    }

    static void createMaskForCtx(final BasicContext activeCtx, final String subcontextName) throws ContextException {
        if (!existMaskForCtx(activeCtx, subcontextName))
            activeCtx.createSubcontext(subcontextName + MASK_EXTENSION);
    }

    static void createMaskForBinding(final BasicContext activeCtx, final String bindingName) throws ContextException {
        if (!existMaskForBinding(activeCtx, bindingName))
            activeCtx.bindObject(bindingName + MASK_EXTENSION, "mask"); //NOI18N
    }

    static void createMaskForAttributes(final BasicContext activeCtx, final String bindingName, final String attributeName) throws ContextException {
        if (!existMaskForAttributes(activeCtx, bindingName, attributeName))
            activeCtx.setAttribute(bindingName, attributeName + MASK_EXTENSION, "mask");//NOI18N
    }

    static void deleteMaskForCtx(final BasicContext activeCtx, final String subcontextName) throws ContextException {
        if (activeCtx == null) return;

        if (existMaskForCtx(activeCtx, subcontextName))
            activeCtx.destroySubcontext(subcontextName + MASK_EXTENSION);
    }

    static void deleteMaskForBinding(final BasicContext activeCtx, final String bindingName) throws ContextException {
        if (activeCtx == null) return;

        if (existMaskForBinding(activeCtx, bindingName))
            activeCtx.bindObject(bindingName + MASK_EXTENSION, null);
    }

    static void deleteMaskForAttributes(final BasicContext activeCtx, final String bindingName, final String attributeName) throws ContextException {
        if (activeCtx == null) return;

        if (existMaskForAttributes(activeCtx, bindingName, attributeName))
            activeCtx.setAttribute(bindingName, attributeName + MASK_EXTENSION, null);
    }

}

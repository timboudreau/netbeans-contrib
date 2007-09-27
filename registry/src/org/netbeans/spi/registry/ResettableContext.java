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

package org.netbeans.spi.registry;

import org.netbeans.api.registry.ContextException;

/**
 * {@link BasicContext} extension supporting default values. If the implementation of
 * backend storage has concept of default values it should implement also
 * this context extension which allows examination of whether the
 * bound object has some default value, whether current value is modified
 * or the original one and also allows to revert the modified value to the
 * default one.
 *
 * <p>If the binding name is <code>null</code> then the context is examined. The context
 * should be considered as modified if following condition is true for it or
 * for any of its subcontexts: some binding in the context is modified or list
 * of subcontext is different from default one. Reverting context means that
 * all modified bindings in the context (including the subcontexts) are reverted
 * and all nondefault subcontexts are destroyed.
 *
 * @author  David Konecny
 */
public interface ResettableContext extends BasicContext {

    /**
     * Exist a default value?
     *
     * @param bindingName the binding name or null for the context
     * @return true if there is a default
     */    
    boolean hasDefault(String bindingName);

    /**
     * Check whether the value is modified.
     * For existing binding for which there is no default value
     * (that is {@link #hasDefault} is false) returns this method
     * always true.
     *
     * @param bindingName the binding name or null for the context
     * @return true if default value is modified; always returns true if 
     *         default value does not exist
     */    
    boolean isModified(String bindingName);

    /**
     * Revert modification. Will do something only if value is modified
     * (ie. {@link #isModified} returns true). If there is no default
     * value (ie. {@link #hasDefault} returns false) the revert operation
     * is identical to unbinding of object or destroying of context
     * content.
     *
     * @param bindingName the binding name or null for the context
     * @throws ContextException can throw exception if there were problems
     *         during removal of modified values
     */    
    void revert(String bindingName) throws ContextException;

}

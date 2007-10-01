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
package org.netbeans.api.xmi;

/** Configuration class for objects taking XMI as input (e.g. {@link XMIReader}).
 *
 * @author Martin Matula
 * @author Brian Smith
 */
public abstract class XMIInputConfig {
    /** Sets reference resolver to be used for resolving hrefs when reading XMI.
     * XMIReader/Consumer should call {@link XMIReferenceResolver#register} for each
     * object deserialized from XMI that has an xmi id associated with it.
     * At the end of the XMI document, the XMI reader/consumer should call
     * {@link XMIReferenceResolver#resolve} passing all of unresolved 
     * external references (hrefs).
     * For immutable configurations this method throws 
     * <code>UnsupportedOperationException</code>.
     * @param resolver Resolver to be used.
     */
    public abstract void setReferenceResolver(XMIReferenceResolver resolver);
    
    /** Returns a reference resolver to be used.
     * This method should never return <code>null</code> for a configuration
     * associated with an XMIReader/Consumer. Otherwise if 
     * <code>null</CODE> is returned, the default reference resolver
     * will be used.
     * @return Reference resolver to be used or <code>null</code>.
     */    
    public abstract XMIReferenceResolver getReferenceResolver();
}

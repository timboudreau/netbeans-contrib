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

import javax.jmi.reflect.RefObject;

/** Implementation of this interface can be passed to XMI producers/writers
 * (using {@link XMIOutputConfig#setReferenceProvider} method) to enable custom controling of
 * target documents the written object should go into and what XMI ID they should use.
 * If an XMI producer supports this property, it will call {@link #getReference} method for each
 * RefObject to be written into the document and either use the XMI ID returned (if
 * the object should reside in the same document) or serialize only a href to the 
 * object (if it resides in a different XMI document).
 *
 * @author Martin Matula
 */
public interface XMIReferenceProvider {
    /** Method called by XMI producer for each object that is serialized or
     * referenced from the generated XMI document. If the returned reference
     * points to the same document as being written, xmi.id part of the returned
     * reference will be used
     * and wherever the element is referenced, simple xmi.idref
     * with this xmi.id will be generated. If the returned reference
     * points to a different file, href will be generated.
     * Format of the generated href should be result of the following:
     * <p><code>getDocumentURI() + "#" + getXmiId()</code></p>
     * @param object Object to be serialized (or referenced).
     * @return Structure representing reference to the object.
     */
    public XMIReference getReference(RefObject object);

    /** Simple structure for representing XMI references to elements
     * corresponding to an object.
     */
    public static final class XMIReference {
        private final String systemId;
        private final String xmiId;

        /** Creates a new instance of XMIReference.
         * @param systemId URI of the home document for the object.
         * @param xmiId xmi.id of the object.
         */
        public XMIReference(String systemId, String xmiId) {
            this.systemId = systemId;
            this.xmiId = xmiId;
        }

        /** Returns URI (system ID) of the home document for the object.
         * This method can return <code>null</code> which means that the
         * XMIReferenceProvider does not control what file the object should go to,
         * thus the writer should write it to the document that is being produced.
         * @return Document URI
         */
        public String getSystemId() {
            return systemId;
        }
        
        /** Returns XMI ID for the object.
         * @return xmi.id
         */
        public String getXmiId() {
            return xmiId;
        }
    }
}

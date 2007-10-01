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
import javax.jmi.reflect.RefPackage;
import java.util.Collection;
import javax.jmi.xmi.MalformedXMIException;
import java.io.IOException;

/** Implementation of this interface can be passed to XMI reader/consumer
 * (using {@link XMIInputConfig#setReferenceResolver} method) to enable custom resolving of hrefs.
 * If an XMIReferenceResolver is registered for an XMI consumer, the XMI consumer will call
 * {@link #register} for each object that it successfuly deserialized from the XMI file.
 * At the end of the document the XMI consumer will call {@link #resolve} passing all the
 * hrefs found in the document and an object that implements {@link XMIReferenceResolver.Client}
 * to receive callbacks.
 * Implementation of XMIReferenceResolver should try to
 * resolve these hrefs and make callbacks to the passed client (by calling
 * {@link XMIReferenceResolver.Client#resolvedReference}) for each resolved href.<p>
 * Note, that in some obscure cases, the XMI consumer may call {@link #register} method
 * even during the execution of {@link XMIReferenceResolver.Client#resolvedReference} method 
 * in case when XMI consumer had to postpone creation of some object because of unresolved 
 * reference to an object within an attribute (i.e. if an unresolved object was part of 
 * an attribute value).
 * <i>IMPORTANT: During the whole XMI reading, the XMI consumer should
 * hold a lock on the used XMIReferenceResolver instance to avoid concurrency problems.</i>
 *
 * @author Martin Matula
 * @author Daniel Prusa
 */
public interface XMIReferenceResolver {
    /** Registers an object that can be resolved. This method should be called by
     * XMI consumer each time it successfuly deserializes an object from XMI, given that the object
     * was assigned an xmiId (for objects that do not have xmiId defined in XMI file
     * this method should not be called).<p>
     * Implementation of this interface should remember all the registered objects and
     * use them for resolving hrefs.
     * @param systemId URI of the document that called this method (the URI is essential for
     * correct resolution of cyclic and relative references).
     * @param xmiId XMI ID of the object deserialized from XMI. If XMI ID for the object
     * is not available, this method should not be called.
     * @param object Object deserialized from XMI.
     */
    public void register(String systemId, String xmiId, RefObject object);
    
    /**
     * Resolves external references and calls 
     * {@link XMIReferenceResolver.Client#resolvedReference} for each.
     * Before returning from this method (only in case of outermost call to it - i.e. this
     * does not hold for nested recursive calls from XMI consumers created from within this
     * method) the instance of this class should be restored to its initial state (all
     * registered references should be forgotten).
     *
     * @param client Implementation of callback method used for reference resolving notifications.
     * @param extent Target package (for resolved objects).
     * @param systemId URI of the document where href is used. This parameter is provided only 
     * if it is known by XMI consumer, otherwise <code>null</code> is passed.
     * @param configuration Configuration to be used for XMI consumer used for reading 
     * external XMI document to resolve the href (if needed).
     * @param hrefs References to be resolved.
     *
     * @throws MalformedXMIException Thrown
     * to indicate an error (element cannot be resolved, etc.)
     * @throws IOException I/O error during XMI reading.
     */
    public void resolve(Client client, RefPackage extent, String systemId, XMIInputConfig configuration, Collection hrefs) throws MalformedXMIException, IOException;
    
    public interface Client {
        /** Method called by reference resolver for each href resolved during the call
         * to {@link XMIReferenceResolver#resolve}.
         * @param href Resolved reference.
         * @param object Object that the reference was resolved to.
         */
        public void resolvedReference(String href, RefObject object);
    }
}

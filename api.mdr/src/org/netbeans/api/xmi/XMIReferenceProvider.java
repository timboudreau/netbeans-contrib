/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
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

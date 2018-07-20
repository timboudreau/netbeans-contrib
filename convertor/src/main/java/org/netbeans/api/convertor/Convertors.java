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

package org.netbeans.api.convertor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import org.netbeans.modules.convertor.Accessor;
import org.netbeans.modules.convertor.ConvertorsPool;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/** 
 * Main API class with methods allowing conversion of object to
 * namespace aware XML fragment and recreation of object from that fragment.
 * Apart from methods doing the conversions there is couple of other 
 * helper methods, for example for listing all currently supported conversions
 * that is listing of available convertor descriptors; listening on changes of available
 * convertors; methods for testing convertibility of the XML fragment or object, etc.
 *
 * @author  David Konecny
 */
public final class Convertors {

    /** Property name of the list of convertor descriptors */
    public static final String CONVERTOR_DESCRIPTORS = "convertorDescriptors"; // NOI18N
    
    private java.beans.PropertyChangeSupport support;

    private static Convertors DEFAULT = new Convertors();
    
    // Yarda's Accessor Pattern in practise
    static {
        Accessor.DEFAULT = new AccessorImpl();
    }

    private Convertors() {
        support = new PropertyChangeSupport(this);
    }

    /** Get default instance of Convertors class. Can be used for listing of
     * convertor descriptors and for listening on changes in that list.
     *
     * @return singleton instance of Convertors class
     */
    public static Convertors getDefault() {
        return DEFAULT;
    }
    
    static Document createDocument() {
        Document doc = null;
        doc = XMLUtil.createDocument("convertors", null, null, null);
        doc.removeChild(doc.getFirstChild());
        return doc;
    }
    
    /**
     * Is there a convertor for the given XML element?
     *
     * @param element XML element to convertor; cannot be null
     * @return true if this element can be converted to an object
     */    
    public static boolean canRead(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("Element cannot be null."); // NOI18N
        }
        return canRead(element.getNamespaceURI(), element.getNodeName());
    }
    
    /**
     * Is there a convertor for the given XML namespace and element name?
     *
     * @param namespace XML namespace; cannot be null
     * @param element element name; cannot be null
     * @return true if element with this name from this namespace can be converted to an object
     */    
    public static boolean canRead(String namespace, String element) {
        if (namespace == null || element == null) {
            throw new IllegalArgumentException("Namespace and element cannot be null."); // NOI18N
        }
        return ConvertorsPool.getDefault().getReadConvertor(namespace, element) != null;
    }
    
    /**
     * Is there a convertor for the given object?
     *
     * @param o an object; cannot be null
     * @return true if there is registered convertor for the given object class
     */    
    public static boolean canWrite(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("Object cannot be null."); // NOI18N
        }
        return ConvertorsPool.getDefault().getWriteConvertor(o) != null;
    }
    
    /**
     * Creates instance from the given XML namespace aware fragment.
     * See also {@link #canRead} for how to test whether the element
     * is convertible or not.
     *
     * @param element XML namespace aware element which will be converted to 
     *    an object; cannot be null
     * @return instance of the object created from the element; cannot be null
     * @throws ConvertorException thrown when there is no convertor for the
     *    passed element or when there was a runtime error during conversion.
     *    Client should call {@link #canRead} to prevent this exception.
     */    
    public static Object read(Element element) {
        ConvertorDescriptor cd = ConvertorsPool.getDefault().getReadConvertor(element.getNamespaceURI(), element.getNodeName());
        if (cd == null) {
            throw new ConvertorException("There is no convertor registered "+ // NOI18N
                "for element with namespace URI "+element.getNamespaceURI()); // NOI18N
        }
        return cd.getConvertor().read(element);
    }
    
    /**
     * Creates instance from the given input stream. The stream is expected
     * to contain namespace aware XML document otherwise the SAXException
     * is thrown. It is also expected that convertor exists for the root element
     * of this XML document otherwise IOException is thrown. See also 
     * {@link #read(Element)}.
     *
     * @param is input stream containing XML namespace aware document
     * @return instance of the object created from the stream; cannot be null
     * @throws SAXException thrown when input stream does not contain valid XML document
     * @throws IOException thrown when XML document cannot be read from input 
     *    stream or when convertor does not exist for root element of XML document. 
     * @throws ConvertorException thrown when there was a runtime error 
     *    during conversion.
     */    
    public static Object read(InputStream is) throws SAXException, IOException {
        InputSource iss = new InputSource(is);
        Document doc = XMLUtil.parse(iss, false, true, null, null);
        if (!canRead(doc.getDocumentElement())) {
            throw new IOException("Stream cannot be converted. "+ // NOI18N
                "There is no convertor for element "+doc.getDocumentElement()); // NOI18N
        }
        return read(doc.getDocumentElement());
    }
    
    /**
     * Converts the object to XML namespace aware fragment. 
     *
     * @param doc document to which the returned element should belong
     * @param o object to convert
     * @return element describing converted object
     * @throws ConvertorException thrown when there is no convertor for the
     *    passed object or when there was a runtime error during conversion.
     *    Client should call {@link #canWrite} to prevent this exception.
     */    
    public static Element write(Document doc, Object o) {
        ConvertorDescriptor cd = ConvertorsPool.getDefault().getWriteConvertor(o);
        if (cd == null) {
            throw new ConvertorException("There is no convertor registered for instance "+o); // NOI18N
        }
        return cd.getConvertor().write(doc, o);
    }

    /**
     * Converts the object to XML document and writes it into
     * given output stream. 
     *
     * @param os output stream to which the XML document will be written
     * @param o object to convert
     * @throws ConvertorException thrown when there is no convertor for the
     *    passed object or when there was a runtime error during conversion.
     *    Client should call {@link #canWrite} to prevent this exception.
     * @throws IOException thrown when XML document cannot be written to output stream
     */    
    public static void write(OutputStream os, Object o) throws IOException {
        Document doc = createDocument();
        Element e = write(doc, o);
        doc.appendChild(e);
        XMLUtil.write(doc, os, "UTF-8"); // NOI18N
    }

    /**
     * Gets set of all available convertors.
     *
     * @return set of {@link ConvertorDescriptor} instances. Method always
     *      returns new instance of set.
     */    
    public Set getConvertorDescriptors() {
        return ConvertorsPool.getDefault().getDescriptors();
    }

    /**
     * Adds listener on changes of this object.
     *
     * @param listener property change listener
     */    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
    
    /**
     * Removes listener on changes of this object.
     *
     * @param listener property change listener
     */    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
    
    final static void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        DEFAULT.support.firePropertyChange(propertyName, oldValue, newValue);
    }
    
}

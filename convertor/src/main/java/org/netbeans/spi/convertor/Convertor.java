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

package org.netbeans.spi.convertor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Base interface for object conversion to XML namespace aware fragment
 * and conversion of this fragment back to the object.
 *
 * <p>Inseparatable part of the convertor is its declarative registration which
 * must be done in JAR Manifest. The example of such registration is:
 *
 * <p><code>
 *  &nbsp;Name: com/yourdomain/YourConvertor.class<br>
 *  &nbsp;NetBeans-Convertor: {yournamespace}yourelement, yourclass
 * </code>
 *
 * <p>where
 *
 * <p><lu>
 *   <li><code>Name:</code> is fully qualified name of the class of your convertor,
 *         that is class implementing Convertor interface</li>
 *   <li><code>NetBeans-Convertor:</code> declaration of your convertor</li>
 *   <li><code>yournamespace</code> is XML namespace which
 *       your convertor is capable to read</li>
 *   <li><code>yourelement</code> is element name from 
 *       the XML namespace which your convertor is capable to read</li>
 *   <li><code>yourclass</code> is fully qualified name of the 
 *       class which instances (but not subclasses!) is your convertor capable to 
 *       persist. This is the only attribute which is optional and if omitted it means 
 *       that your convertor is not capable to persist any class.</li>
 *</lu>
 *
 * <p>The Convertor infrastructure will use the information from manifest to 
 * create instance of your convertor and will call your convertor only with parameters
 * matching the declared criteria.
 *
 * <p>It is guaranteed that {@link #read} method will be
 * called only with element from the declared namespace and with the
 * declared name. The object created by read method does not have to be
 * assignable to class in NetBeans-Convertor attribute. It can be
 * object of any type.
 *
 * <p>It is guaranteed that {@link #write} method will be called only
 * if the object's class is equal to class declared in NetBeans-Convertor
 * attribute and only if the convertor's classloader is equal to, 
 * or a descendant of, the classloader used to load the object's class.
 * If NetBeans-Convertor attribute does not specify class name the write method will
 * never be called. The element created by write method is not constrained by
 * the value of NetBeans-Convertor attribute. It can be element with arbitrary
 * name and namespace.
 *
 * <p>The JAR Manifest can contain multiple convertors. One convertor can be
 * registered for multiple namespaces/elements. In such a case the name of 
 * the <code>NetBeans-Convertor</code> attribute must be suffixed by "-" and 
 * number, eg.:
 *
 * <p><code> 
 *  &nbsp;Name: com/yourdomain/YourConvertor.class<br>
 *  &nbsp;NetBeans-Convertor: {yourdomain.com/firstns}firstelement, com.yourdomain.FirstClass<br>
 *  &nbsp;NetBeans-Convertor-2: {yourdomain.com/secondns}secondreadonlyelement<br>
 *  &nbsp;NetBeans-Convertor-3: {yourdomain.com/thirdns}thirdelement, com.yourdomain.ThirdClass
 * </code>
 *
 * <p><strong>Recommendations for convertor implementors:</strong><br>
 * <lu>
 *   <li>It is strongly recommended to keep all details of the Java memory 
 *         representation (implementation class names, field names, etc.)
 *         out of the storage format. Otherwise you run into the problems 
 *         whenever you refactor the implementation class internally.</li>
 * </lu>
 *
 * <p>See also {@link SimplyConvertible} for information about how to persist
 * your object without writing your own Convertor.
 *
 * @author  David Konecny
 */
public interface Convertor {
    
    /**
     * Creates object from the element.
     *
     * @param element element which namespace and name will correspond to
     *     the namespace and name with which this convertor was registered
     * @return instance of the object created from the element; cannot be null;
     *    can be of arbitrary type
     * @throws org.netbeans.api.convertor.ConvertorException can throw this 
     *     exception when there is runtime problem with conversion of element
     *     to object
     */
    Object read(Element element);
    
    /** 
     * Converts the object to element. This method will be only called
     * when NetBeans-Convertor attribute declares a class.
     *
     * @param doc document to which the returned element should belong
     * @param inst object to convert; the class of the instance will be always
     *     equal to the class specified in NetBeans-Convertor attribute
     * @return element describing converted object; cannot be null;
     *     returned element can be of any name and namespace
     * @throws org.netbeans.api.convertor.ConvertorException can throw this 
     *     exception when there is runtime problem with conversion of object 
     *     to element
     */
    Element write(Document doc, Object inst);

}

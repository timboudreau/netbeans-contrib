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

import java.util.Properties;

/**
 * SimplyConvertible is way how to persist your object by Convertor infrastructure
 * without writing any convertor.
 *
 * <p>Three things must be done to make this happen:
 * <lu>
 *    <li>your class must have public default constructor</li>
 *    <li>your class must implement SimplyConvertible interface</li>
 *    <li>you must register your class in JAR Manifest</li>
 * </lu>
 *
 * <p>Default public constructor is necessary because Convertor infrastructure will
 * use it to create instance of your class.
 *
 * <p>SimplyConvertible interface has two methods. The {@link #write} method
 * will be called on your object whenever your instance needs to be persist. The
 * method will pass you empty {@link java.util.Properties} object to which you
 * can store all relevant data of your instance. All String properties will be
 * then persisted by the Convertor infrastructure. Second method is {@link #read}
 * method which does opposite. Instance of your class is first created by
 * default constructor and then this method will pass you Properties object
 * with all properties which you stored in write() method. That allows you to
 * reinitialize your instance to the state before it was persisted.
 * The Convertor infrastructure guarantees that it will not call the read()
 * method more than once and it will call it immediatelly after the instance 
 * was created by your default constructor.
 *
 * <p>The Properties object content will be persisted as XML namespace aware
 * fragment with following structure:
 *
 * <p><code> 
 * &nbsp;&lt;yourelement xmlns="yournamespace"&gt;<br>
 * &nbsp;&nbsp;&lt;propertykey1&gt;propertyvalue1&lt;/propertykey1&gt;<br>
 * &nbsp;&nbsp;&lt;propertykey2&gt;propertyvalue2&lt;/propertykey2&gt;<br>
 * &nbsp;&nbsp;&lt;propertykeyN&gt;propertyvalueN&lt;/propertykeyN&gt;<br>
 * &nbsp;&lt;/yourelement&gt;<br>
 * </code> 
 *
 * <p>Property keys are used as XML element names and so the
 * same restrictions as for XML element names are valid for property keys.
 * Similarly the property values need to be valid XML text content (i.e. no
 * control characters, newlines will be normalized, etc.).  Invalid property 
 * key or property value will result in runtime 
 * {@link org.netbeans.api.convertor.ConvertorException}. The XML elements 
 * are created in lexicographical order according to property keys.
 *
 * <p>Declarative registration looks like:
 *
 * <p><code> 
 *  &nbsp;Name: com/yourdomain/YourClass.class<br>
 *  &nbsp;NetBeans-Simply-Convertible: {yournamespace}yourelement<br>
 * </code>
 *
 * <p>where
 *
 * <p><lu>
 *   <li><code>Name:</code> is fully qualified name of your class which 
 *         implements SimplyConvertible interface</li>
 *   <li><code>NetBeans-Simply-Convertible:</code> declaration of simply
 *          convertible</li>
 *   <li><code>yournamespace</code> is XML namespace
 *          to which your class will be persisted</li>
 *   <li><code>yourelement</code> is element name
 *          to which your class will be persisted</li>
 *</lu>
 *
 * <p>Although it was said that simply convertible object must implement 
 * SimplyConvertible interface there are cases when this is not desirable and
 * so it does not have implement it. For example it might be desirable to hide
 * fact that object is simply convertible when object is part of an API contract.
 * In such a case you do not have to implement SimplyConvertible interface.
 * However your object must have two methods with the same signatures
 * as SimplyConvertible methods have and default constructor. The methods
 * and constructor do not have to have public access.
 * 
 * <p>See also {@link Convertor} for information about how to write regular 
 * convertor.
 *
 * @author  David Konecny
 */
public interface SimplyConvertible {
    
    /**
     * Read object state from the given Properties instance.
     * The method will be called only once by Convertor infrastructure just
     * after the instance was created by default constructor.
     *
     * @param p properties instance with properties stored by write() method
     * @throws org.netbeans.api.convertor.ConvertorException can throw this 
     *     exception when content of Properties instance is malformed
     */    
    void read(Properties p);

    /**
     * Write object state to the given Properties instance.
     * The Convertor infrastructure will take care about persistence of
     * content of Properties instance. Non-String properties are forbidden.
     * For naming restrictions on property keys and values see the class Javadoc.
     *
     * @param p empty properties instance for the data to be persisted
     */    
    void write(Properties p);
    
}

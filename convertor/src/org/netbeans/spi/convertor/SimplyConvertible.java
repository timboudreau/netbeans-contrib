/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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

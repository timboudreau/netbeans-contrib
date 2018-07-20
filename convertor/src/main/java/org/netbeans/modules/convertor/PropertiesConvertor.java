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

package org.netbeans.modules.convertor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import org.netbeans.api.convertor.ConvertorException;
import org.netbeans.spi.convertor.Convertor;
import org.netbeans.spi.convertor.SimplyConvertible;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/** 
 *
 * @author  David Konecny
 */
public final class PropertiesConvertor implements Convertor {
    
    private String namespace;
    private String rootElement;
    private String writes;

    public PropertiesConvertor(String namespace, String rootElement, String writes) {
        assert namespace != null;
        assert rootElement != null;
        assert writes != null;
        this.namespace = namespace;
        this.rootElement = rootElement;
        this.writes = writes;
    }

    public Object read(Element element) {
        assert element.getNodeName().equals(rootElement) : "Element "+element+ // NOI18N
            "  cannot be converted by instance of PropertiesConvertor setuped "+ // NOI18N
            "for root elment "+element;
        Properties p = new Properties();
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element)node;
                String propName = e.getNodeName();
                String propVal = getTextValue(e);
                p.setProperty(propName, propVal);
            }
        }
        return createInstance(element, p, writes);
    }
    
    public Element write(Document doc, Object inst) {
        Properties p = new Properties();
        if (inst instanceof SimplyConvertible) {
            SimplyConvertible sc = (SimplyConvertible)inst;
            sc.write(p);
        } else {
            Method m;
            try {
                m = inst.getClass().getDeclaredMethod("write", new Class[]{Properties.class}); // NOI18N
                m.setAccessible(true);
                // check that there is also read(Properties) method
                inst.getClass().getDeclaredMethod("read", new Class[]{Properties.class}); // NOI18N
            } catch (Exception ex) {
                ConvertorException ce = new ConvertorException("Class "+inst.getClass().getName()+ // NOI18N
                    " cannot be stored as SimplyConvertible, because it does not implement SimplyConvertible interface nor"+ // NOI18N
                    " it has read(Properties) and write(Properties) methods."); // NOI18N
                ce.initCause(ex);
                throw ce;
            }
            try {
                m.invoke(inst, new Object[]{p});
            } catch (Exception ex) {
                ConvertorException ce = new ConvertorException("Could not call "+ // NOI18N
                    "introspected write(Properties) method on class "+ // NOI18N
                    inst.getClass().getName());
                ce.initCause(ex);
                throw ce;
            }
        }
        Element ee = doc.createElementNS(namespace,  rootElement);
        Set keys = new TreeSet(p.keySet());
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            // TODO: check here that key is valid name for XML element
            String val = p.getProperty(key);
            Element e = doc.createElementNS(namespace, key);
            Text t = doc.createTextNode(val);
            e.appendChild(t);
            ee.appendChild(e);
        }
        return ee;
    }
    
    private Object createInstance(Element element, Properties p, String className) {
        try {
            Class c = InstanceUtils.findClass(className);
            if (SimplyConvertible.class.isAssignableFrom(c)) {
                SimplyConvertible sc = (SimplyConvertible)c.newInstance();
                sc.read(p);
                return sc;
            } else {
                Constructor co;
                Method m;
                try {
                    co = c.getDeclaredConstructor(new Class[]{});
                    co.setAccessible(true);
                    m = c.getDeclaredMethod("read", new Class[]{Properties.class}); // NOI18N
                    m.setAccessible(true);
                    // check that there is also write(Properties) method
                    c.getDeclaredMethod("write", new Class[]{Properties.class}); // NOI18N
                } catch (Exception ex) {
                    ConvertorException ce = new ConvertorException("Class "+c.getName()+ // NOI18N
                        " cannot be instantiated as SimplyConvertible, because it does not implement SimplyConvertible interface nor"+ // NOI18N
                        " it has read(Properties) and write(Properties) methods."); // NOI18N
                    ce.initCause(ex);
                    throw ce;
                }
                Object o = co.newInstance(new Object[]{});
                m.invoke(o, new Object[]{p});
                return o;
            }
        } catch (ConvertorException ex) {
            throw ex;
        } catch (Exception ex) {
            ConvertorException ex2 = new ConvertorException("Unexpected exception. SimplyConvertible could "+ // NOI18N
                "not instantiate element "+element); // NOI18N
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    static String getTextValue(Element element) {
        StringBuffer sb = new StringBuffer();
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.TEXT_NODE) {
                sb.append(((Text)node).getData());
            }
            if (node.getNodeType() == Node.CDATA_SECTION_NODE) {
                sb.append(((CDATASection)node).getData());
            }
        }
        return sb.toString();
    }
    
}

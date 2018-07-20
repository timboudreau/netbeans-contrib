/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.commons.palette.java.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.netbeans.modules.portalpack.commons.palette.java.JavaMethod;
import org.openide.filesystems.FileObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author satyaranjan
 */
public class JavaPaletteXMLUtil {

    private static String MODIFIER = "modifier";
    private static String MODIFIERS = "modifiers";
    private static String ANNOTATIONS = "annotations";
    private static String ANNOTATION = "annotation";
    private static String PARAMS = "params";
    private static String PARAM = "param";
    private static String EXCEPTIONS = "exceptions";
    private static String EXCEPTION = "exception";
    private static String RETURN_TYPE = "return-type";
    private static String NAME = "name";

    public static Document createDocument(InputStream ins) {
        try {

            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().
                newDocumentBuilder();
            return docBuilder.parse(ins);
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }
        return null;
    }

    public static JavaMethod getMethodDefination(InputStream ins) {
        if (ins == null) {
            return null;
        }
        Document doc = createDocument(ins);
        if (doc == null) {
            return null;
        }
        JavaMethod method;
        
        String name = getTextValue(doc, NAME);
        
        method = new JavaMethod(name);

        List<String> modifiers = getTextValues(doc, MODIFIERS, MODIFIER);
        method.setModifiers(modifiers);

        List<String> annotations = getTextValues(doc, ANNOTATIONS, ANNOTATION);
        method.setAnnotations(annotations);

        
        String methodName = getTextValue(doc, NAME);
        method.setMethodName(methodName);

        method.setParameters(populateParams(doc));

        List<String> exceptions = getTextValues(doc, EXCEPTIONS, EXCEPTION);
        method.setExceptionList(exceptions);
        
        String returnType = getTextValue(doc, RETURN_TYPE);
        method.setReturnType(returnType);
        
        return method;

    }

    private static List<String> getTextValues(Document doc, String parentElm, String childElm) {

        List values = new ArrayList();
        NodeList parentList = doc.getElementsByTagName(parentElm);

        int length = parentList.getLength();
        if (length != 0) {
            Node parent = parentList.item(0);
            NodeList childrenList =
                ((Element) parent).getElementsByTagName(childElm);

            length = childrenList.getLength();
            for (int i = 0; i < length; i++) {
                Element node = (Element) childrenList.item(i);
                if (node != null) {
                    String value = node.getTextContent().trim();
                    if (value != null && value.length() != 0) {
                        values.add(value);
                    }
                }
            }
        }

        return values;
    }

    private static String getTextValue(Document doc, String element) {

        NodeList nodeList = doc.getElementsByTagName(element);

        int length = nodeList.getLength();
        if (length == 0) {
            return "";
        }
        Element node = (Element) nodeList.item(0);
        if (node != null) {
            return node.getTextContent().trim();

        }
        
        return "";
    }
    
    private static JavaMethod.ParameterInfo[] populateParams(Document doc) {
        List values = new ArrayList();
        NodeList parentList = doc.getElementsByTagName(PARAMS);

        int length = parentList.getLength();
        if (length != 0) {
            Node parent = parentList.item(0);
            NodeList childrenList =
                ((Element) parent).getElementsByTagName(PARAM);

            length = childrenList.getLength();
            for (int i = 0; i < length; i++) {
                Element node = (Element) childrenList.item(i);
                
                String type = node.getAttribute("type");
                String name = node.getAttribute("varname");
                
                JavaMethod.ParameterInfo paramInfo = new JavaMethod.ParameterInfo(name,type);
                values.add(paramInfo);
            }
        }
        
        if(values.size() > 0)
            return (JavaMethod.ParameterInfo [])values.toArray(new JavaMethod.ParameterInfo[0]);
        
        return new JavaMethod.ParameterInfo[0];

    }
    
   
}

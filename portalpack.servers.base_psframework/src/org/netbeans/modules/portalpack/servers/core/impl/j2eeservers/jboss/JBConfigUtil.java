/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * John Platts
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 * Portions Copyrighted 2009 John Platts
 */
package org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.jboss;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.openide.util.Utilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Configuration utility class for JBoss Application Server
 * @author John Platts
 */
public class JBConfigUtil {

    private JBConfigUtil() {
    }
    public static final String LIB = "lib" + File.separator; //NOI18N
    public static final String CLIENT = "client" + File.separator; //NOI18N
    private static final Logger LOGGER = Logger.getLogger(JBConfigUtil.class.getName());

    public static String getJnpPort(String domainDir) {
        String serviceXml = domainDir + File.separator + "conf" + File.separator + "jboss-service.xml"; //NOI18N
        File xmlFile = new File(serviceXml);
        if (!xmlFile.exists()) {
            return "";
        }

        InputStream inputStream = null;
        Document document = null;
        try {
            inputStream = new FileInputStream(xmlFile);
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);

            // get the root element
            Element root = document.getDocumentElement();

            // get the child nodes
            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeName().equals("mbean")) {  // NOI18N
                    NodeList nl = child.getChildNodes();
                    if (!child.getAttributes().getNamedItem("name").getNodeValue().equals("jboss:service=Naming")) //NOI18N
                    {
                        continue;
                    }
                    for (int j = 0; j < nl.getLength(); j++) {
                        Node ch = nl.item(j);

                        if (ch.getNodeName().equals("attribute")) {  // NOI18N
                            if (!ch.getAttributes().getNamedItem("name").getNodeValue().equals("Port")) //NOI18N
                            {
                                continue;
                            }
                            return ch.getFirstChild().getNodeValue();
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger("global").log(Level.INFO, null, e);
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch(Exception e) {
                }
            }
        }
        
        return "";
    }
    
    public static boolean isValidJbossRootDirectory(File jbossRootDir) {
        if(jbossRootDir == null || !jbossRootDir.isDirectory()) {
            return false;
        }
        
        File runScript = new File(jbossRootDir, "bin" +
                File.separator + (Utilities.isWindows() ?
                    JBStartServer.RUN_BAT : JBStartServer.RUN_SH));

        if(!runScript.isFile()) {
            return false;
        }

        return true;
    }

    public static boolean isValidJbossInstanceDirectory(File instanceDir) {
        if(instanceDir == null || !instanceDir.isDirectory()) {
            return false;
        }

        File confDir = new File(instanceDir, "conf");
        if(!confDir.isDirectory()) {
            return false;
        }

        File jbossServiceXmlFile = new File(confDir, "jboss-service.xml");
        if(!jbossServiceXmlFile.isFile()) {
            return false;
        }

        File deployDir = new File(instanceDir, "deploy");
        if(!deployDir.isDirectory()) {
            return false;
        }

        File jbosswebSar = new File(deployDir, "jbossweb.sar");
        if(!jbosswebSar.isDirectory()) {
            return false;
        }

        File serverXml = new File(jbosswebSar, "server.xml");
        if(!serverXml.exists()) {
            return false;
        }

        return true;
    }

    public static class GetHttpPortResult implements Serializable {
        private static final long serialVersionUID = -10L;
        public GetHttpPortResult(int port, boolean secure) {
            this.port = port;
            this.secure = secure;
        }

        public int getPort() {
            return port;
        }

        public boolean isSecure() {
            return secure;
        }

        private int port;
        private boolean secure;
    }
    private static class GetHttpPortStopProcessingException extends SAXException {
    }
    private static class GetHttpPortContentHandler extends DefaultHandler {
        public void startElement(String uri, String localName, String qName,
                Attributes atts) throws SAXException {
            String elemName = getElementName(localName, qName);

            if("Service".equals(elemName)) {
                insideServiceElement = true;
            }
            if("Connector".equals(elemName)) {
                String protocolAttr = null;
                String secureAttr = null;
                String schemeAttr = null;
                String portAttr = null;

                if(atts != null) {
                    for(int i = 0; i < atts.getLength(); i++) {
                        String attrLocalName = atts.getLocalName(i);
                        String attrQname = atts.getQName(i);
                        String attrName = getElementName(attrLocalName,
                                attrQname);

                        if("protocol".equals(attrName)) {
                            protocolAttr = atts.getValue(i);
                        }
                        if("secure".equals(attrName)) {
                            secureAttr = atts.getValue(i);
                        }
                        if("scheme".equals(attrName)) {
                            schemeAttr = atts.getValue(i);
                        }
                        if("port".equals(attrName)) {
                            portAttr = atts.getValue(i);
                        }
                    }
                }

                String uppercaseProtocol = (protocolAttr != null) ?
                        protocolAttr.toUpperCase(Locale.ENGLISH) : "";
                boolean isHttp =
                        !uppercaseProtocol.contains("AJP") &&
                        !uppercaseProtocol.contains("HTTP");

                boolean isSecure =
                        (secureAttr != null &&
                         (secureAttr = secureAttr.trim()).equalsIgnoreCase("true")) ||
                        (schemeAttr != null &&
                         (schemeAttr = schemeAttr.trim()).equalsIgnoreCase("https"));

                if(portAttr != null) {
                    int port;
                    try {
                        port = Integer.parseInt(portAttr);
                    } catch(NumberFormatException e) {
                        port = -1;
                    }

                    if(isValidPort(port)) {
                        if(isSecure) {
                            secureResult =
                                    new GetHttpPortResult(port, true);
                        } else {
                            result = new GetHttpPortResult(port, false);
                        }
                    }
                }

                if(result != null) {
                    // End processing here because we have found a non-HTTPS
                    // HTTP connector
                    throw new GetHttpPortStopProcessingException();
                }
            }
        }

        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            String elemName = getElementName(localName, qName);

            if("Service".equals(elemName)) {
                insideServiceElement = true;
            }
        }
        
        protected String getElementName(String localName, String qName) {
            if (localName != null && localName.length() >= 1) {
                return localName;
            } else {
                if(qName == null) {
                    return null;
                }

                int i = qName.indexOf(':');
                if(i == -1) {
                    return qName;
                } else {
                    return qName.substring(1, qName.length());
                }
            }
        }

        public GetHttpPortResult getResult() {
            return ((result != null) ? result :
                ((secureResult != null) ? secureResult : null));
        }

        private boolean insideServiceElement = false;
        private GetHttpPortResult result = null;
        private GetHttpPortResult secureResult = null;
    }

    public static GetHttpPortResult getHttpPort(String domainDir) {
        String serverXml = "deploy" + File.separator + "jbossweb.sar" +
                File.separator + "server.xml"; //NOI18N
        File xmlFile = new File(domainDir, serverXml);
        if (!xmlFile.exists()) {
            return null;
        }

        InputStream inputStream = null;
        Document document = null;
        try {
            inputStream = new FileInputStream(xmlFile);

            SAXParserFactory saxParserFactory =
                    SAXParserFactory.newInstance();

            SAXParser saxParser = saxParserFactory.newSAXParser();
            GetHttpPortContentHandler contentHandler =
                    new GetHttpPortContentHandler();

            try {
                saxParser.parse(inputStream, contentHandler);
            } catch(GetHttpPortStopProcessingException e) {
                // Ignore a GetHttpPortStopProcessingException exception
                // that is thrown because this is simply thrown to stop SAX
                // processing
            }

            return contentHandler.getResult();
        } catch (Exception e) {
            Logger.getLogger("global").log(Level.INFO, null, e);
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch(Exception e) {
                }
            }
        }
        return null;
    }

    public static boolean isValidPort(int port) {
        return ((port > 0) && (port < 65536));
    }
    public static boolean isValidPort(String port) {
        if(port == null || (port = port.trim()).length() < 1) {
            return false;
        }

        try {
            return isValidPort(Integer.parseInt(port));
        } catch(NumberFormatException e) {
            return false;
        }
    }
}

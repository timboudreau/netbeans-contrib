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

package org.netbeans.modules.primitivesettings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vita
 */
@org.openide.util.lookup.ServiceProvider(service=java.net.URLStreamHandlerFactory.class)
public class PropertiesURLStreamHandlerFactory implements URLStreamHandlerFactory {
    
    private static final Logger LOG = Logger.getLogger(PropertiesURLStreamHandlerFactory.class.getName());
    
    private static String PROTOCOL = "property"; //NOI18N
    
    /** Creates a new instance of PropertiesURLStreamHandlerFactory */
    public PropertiesURLStreamHandlerFactory() {
    }

    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (PROTOCOL.equals(protocol)) {
            return new Handler();
        } else {
            return null;
        }
    }
    
    private static final class Handler extends URLStreamHandler {
        
        protected URLConnection openConnection(URL u) throws IOException {
            if (u.getProtocol().equals(PROTOCOL)) {
                return new Connection(u);
            } else {
                throw new IOException("Bad protocol: " + u.getProtocol()); //NOI18N
            }
        }
    } // End of Handler class

    private static final class Connection extends URLConnection {
        
        private static final String ENCODING = "UTF-8"; //NOI18N
        
        private String xmlContents;
        
        public Connection(URL url) {
            super(url);
        }
        
        public void connect() throws IOException {
            if (!connected) {
                xmlContents = parseContents(getURL().toString());
                connected = true;
            }
        }
        
        public String getHeaderField(String name) {
            return null;
        }

        public String getHeaderFieldKey(int n) {
            return null;
        }

        public String getHeaderField(int n) {
            return null;
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("This operation is not supprted"); //NOI18N
        }

        public InputStream getInputStream() throws IOException {
            connect();
            return new ByteArrayInputStream(xmlContents.getBytes(ENCODING));
        }

        public Map<String, List<String>> getHeaderFields() {
            return Collections.EMPTY_MAP;
        }

        public String getContentType() {
            return "text/xml"; //NOI18N
        }

        public int getContentLength() {
            try {
                connect();
                return xmlContents.length();
            } catch (IOException e) {
                LOG.log(Level.WARNING, "Invalid properties: URL", e); //NOI18N
                return 0;
            }
        }

        public String getContentEncoding() {
            return ENCODING;
        }

        private String parseContents(String contents) {
            String propertyTypeName;
            String propertyValue;
            
            // cut off the protocol and ':'
            int idxA = PROTOCOL.length() + 1;
            int idxB = contents.indexOf('=', idxA); //NOI18N
            
            if (idxB != -1) {
                propertyTypeName = contents.substring(idxA, idxB);
                propertyValue = contents.substring(idxB + 1);
            } else {
                propertyTypeName = null;
                propertyValue = null;
            }
            
            StringBuilder sb = new StringBuilder(50);
            // XML prologue
            sb.append("<?xml version=\"1.0\" encoding=\"").append(ENCODING).append("\"?>"); //NOI18N
            // Root element
            sb.append("<").append(PrimitiveSettingConvertor.ROOT); //NOI18N
            sb.append(" xmlns=\"").append(PrimitiveSettingConvertor.NAMESPACE).append("\">"); //NOI18N
            if (propertyTypeName != null && propertyValue != null) {
                // Property value element
                sb.append("<").append(propertyTypeName).append(">"); //NOI18N
                sb.append(propertyValue);
                sb.append("</").append(propertyTypeName).append(">"); //NOI18N
            }
            // End of root element
            sb.append("</").append(PrimitiveSettingConvertor.ROOT).append(">"); //NOI18N
            
            return sb.toString();
        }
    } // End of Connection class
}

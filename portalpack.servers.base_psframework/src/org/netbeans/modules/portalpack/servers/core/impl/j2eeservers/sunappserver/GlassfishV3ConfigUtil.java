/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.sunappserver;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.xml.sax.SAXException;

/**
 *
 * @author satya
 */
public class GlassfishV3ConfigUtil extends SunAppConfigUtil {

    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);

    public GlassfishV3ConfigUtil(File domainDir)
            throws IOException, SAXException, ParserConfigurationException, ReadAccessDeniedException {
        super(domainDir);
    }

    @Override
    public String getPort() {
        String port = "";
        if (getDocument() == null) {
            return "";
        }

        String listenerName = "";
        try {

            listenerName = getXPath().evaluate("/domain/configs/config[@name='server-config']/http-service/virtual-server[@id='server']/@network-listeners", getDocument());
        } catch (XPathExpressionException ex) {
            ex.printStackTrace();
        }

        if (listenerName != null) {
            listenerName = listenerName.split(",")[0];
        }
        try {
            port = getXPath().evaluate("/domain/configs/config[@name='server-config']/network-config/network-listeners/network-listener[@name=\'" + listenerName + "\']/@port", getDocument());
        } catch (XPathExpressionException ex) {
            logger.log(Level.SEVERE, "ParseError", ex);
        }

        if (port == null) {
            return "";
        }

        return port.trim();
    }

    @Override
    public String getAdminPort() {
        String port = "";
        if (getDocument() == null) {
            return "";
        }

        String listenerName = "";
        try {

            listenerName = getXPath().evaluate("/domain/configs/config[@name='server-config']/http-service/virtual-server[@id='__asadmin']/@network-listeners", getDocument());
        } catch (XPathExpressionException ex) {
            ex.printStackTrace();
        }

        if (listenerName != null) {
            listenerName = listenerName.split(",")[0];
        }
        try {
            port = getXPath().evaluate("/domain/configs/config[@name='server-config']/network-config/network-listeners/network-listener[@name=\'" + listenerName + "\']/@port", getDocument());
        } catch (XPathExpressionException ex) {
            logger.log(Level.SEVERE, "ParseError", ex);
        }


        if (port == null) {
            return "";
        }

        return port.trim();
    }


    @Override
    public String getJMXConnectorPort() {
        String port = "";
        if (getDocument() == null) {
            return "";
        }

        try {

            port = getXPath().evaluate("/domain/configs/config[@name='server-config']/admin-service/jmx-connector/@port", getDocument());
        } catch (XPathExpressionException ex) {
            ex.printStackTrace();
        }


        if (port == null || port.trim().length() == 0) {
            return "8686";
        }

        return port.trim();
    }
}

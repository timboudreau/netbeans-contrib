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
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.servers.websynergy.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.servers.core.common.ServerConstants;
import org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.tomcat.TomcatConstant;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.websynergy.common.LiferayConstants;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Santh Chetan Chadalavada
 */
public class LiferayHelper {

    protected static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);

    public static String getAutoDeployDirectory(PSConfigObject psconfig) {
        return getAutoDeployDirectory(psconfig, Boolean.FALSE);
    }
    public static String getAutoDeployDirectory(PSConfigObject psconfig,boolean force) {

        String v = psconfig.getProperty(LiferayConstants.LR_VERSION);
        if (v == null || v.trim().length() == 0) {
            return psconfig.getProperty(LiferayConstants.AUTO_DEPLOY_DIR);
        }

        String aDepDir = psconfig.getProperty(LiferayConstants.AUTO_DEPLOY_DIR);
        if (aDepDir != null && aDepDir.trim().length() != 0 && !force) {
            return psconfig.getProperty(LiferayConstants.AUTO_DEPLOY_DIR);
        }

        int version = 1;

        try {
            version = Integer.parseInt(v);
        } catch (Exception e) {
            return psconfig.getProperty(LiferayConstants.AUTO_DEPLOY_DIR);
        }

        if (version <= 5200) {
            return psconfig.getProperty(LiferayConstants.AUTO_DEPLOY_DIR);
        }

        /*String defaultAutoDeployDir = "";
        if (psconfig.getServerType().equals(ServerConstants.SUN_APP_SERVER_9)) {
        defaultAutoDeployDir = new File(psconfig.getServerHome()).getParentFile().getAbsolutePath() + File.separator + "deploy";
        } else {
        defaultAutoDeployDir = new File(psconfig.getProperty(TomcatConstant.CATALINA_BASE)).getParentFile().getAbsolutePath() + File.separator + "deploy";
        }*/
        try {
            String portalUri = LiferayHelper.normalizeURL(psconfig.getPortalUri());
			URL url = null;
            String urlStr = "http://" + psconfig.getHost() + ":" + psconfig.getPort() + portalUri + "/c/portal/" + "json_service?serviceClassName=" + "com.liferay.portal.service.http.PortalServiceJSON" + "&serviceMethodName=getAutoDeployDirectory";
            if(getLiferayBuildNumber(psconfig) >= 5203) {
                urlStr = "http://" + psconfig.getHost() + ":" + psconfig.getPort() + portalUri +"/c/portal/" + "json_service?serviceClassName=" + "com.liferay.portal.service.PortalServiceUtil" + "&serviceMethodName=getAutoDeployDirectory";
            }
            try {
                url = new URL(urlStr);
            } catch (MalformedURLException e) {
                logger.warning("Failed to create URL for the websynergy json service ");
                logger.warning(e.getMessage());
                //return defaultAutoDeployDir;
                return null;
            }

            String jsonString = getContentFromHttpURL(url);
            if (jsonString == null) {
                //return defaultAutoDeployDir;
                return null;
            }

            JSONObject json = new JSONObject(jsonString);
            String autoDeployDir = json.getString("returnValue");

            psconfig.setAndSaveProperty(LiferayConstants.AUTO_DEPLOY_DIR,
                    FileUtil.normalizeFile(new File(autoDeployDir)).getAbsolutePath());

            return autoDeployDir;
        } catch (JSONException ex) {
            logger.log(Level.SEVERE, "Error", ex);
            return null;
        }

    }
 public static String getContentFromHttpURL(URL url) {
        BufferedReader br = null;
        try {
            // TODO code application logic here
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            InputStream ins = con.getInputStream();

            br = new BufferedReader(new InputStreamReader(ins));
            String content = "";
            String line = br.readLine();
            while (line != null) {
                content += line;
                line = br.readLine();
            }

            return content;
        } catch (Exception e) {
            //logger.log(Level.SEVERE, "Error", e);
            return null;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    public static String normalizeURL(String portalUri) {
		if (portalUri == null || portalUri.trim().length() == 0 || portalUri.trim().equals("/")) {
			portalUri = "";
		} else if (!portalUri.startsWith("/")) {
			portalUri = "/" + portalUri;
		}

		return portalUri;
	}

    private static int getLiferayBuildNumber(PSConfigObject psconfig) {
        try{
            return Integer.parseInt(
                    psconfig.getProperty(LiferayConstants.LR_VERSION));
        }catch(Exception e) {
            return 1;
        }
    }
}

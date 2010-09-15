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

package org.netbeans.modules.j2ee.sun.ws7.util;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.lang.reflect.*;

import org.netbeans.modules.j2ee.sun.ws7.WS7LibsClassLoader;

public class Util {
    // Get the instance location from wsenv file
    public static String getInstanceLocation(String serverLocation) {
        String instanceLocation = "";

        if(serverLocation!=null && serverLocation.trim().length()!=0) {
            String wsenvPath = serverLocation + File.separator + "lib" + File.separator + "wsenv";

            boolean isWindows = (File.separatorChar == '\\');
            if (isWindows)
                wsenvPath += ".bat";

            if(new File(wsenvPath).exists())
                instanceLocation = getInstanceRoot(wsenvPath, isWindows);
        }

        return instanceLocation;
    }

    public static String getInstanceRoot(String wsenvPath, boolean isWindows) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(wsenvPath));
            String line = null;
            do {
                    line = br.readLine();
                    if (line.contains("WS_INSTANCEROOT=")) {
                        if (isWindows) {
                            return line.split("=")[1];
                        } else {
                            return line.substring(line.indexOf("=")+2, line.indexOf(";")-1);
                        }
                    }
            } while(line != null);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isRunning(String host, int port){
       try {
             java.net.InetSocketAddress isa = new java.net.InetSocketAddress(java.net.InetAddress.getByName(host), port);
             java.net.Socket socket = new java.net.Socket();
             socket.connect(isa);
             socket.close();
             return true;
        } catch (IOException e) {
            return false;
        }

    }

    public static HashMap getPorts(String serverXmlLocation) {
        List<String> ssl_ports = new ArrayList<String>();
        List<String> non_ssl_ports = new ArrayList<String>();
        File serverXml = new File(serverXmlLocation+File.separator+"admin-server"+File.separator+"config"+File.separator+"server.xml");
        try {
            WS7LibsClassLoader wscl = new WS7LibsClassLoader();
            wscl.addURL(new File(serverXmlLocation + File.separator + "lib" + File.separator + "webserv-rt.jar"));
            Class serverCls = wscl.loadClass("com.sun.webserver.config.serverbeans.Server");

            // Call create graph method (on serverXml file)  and getHttpListener method from the server class
            Method cgMth = serverCls.getMethod("createGraph", new Class[] {serverXml.getClass()});
            Method hlMth = serverCls.getMethod("getHttpListener", null);

            // Load HttpListenerType class and get the required methods
            Class hlCls = wscl.loadClass("com.sun.webserver.config.serverbeans.HttpListenerType");
            Method getPortMth = hlCls.getMethod("getPort", null);
            Method getSslMth =  hlCls.getMethod("getSsl", null);
            Method isHLEnabledMth = hlCls.getMethod("isEnabled", null);

            // Load SslType class as getSsl method returns a SslType class, which will be used to invoke the getEnabled method
            Class sslTypeCls = wscl.loadClass("com.sun.webserver.config.serverbeans.SslType");
            Method isSSLEnabledMth = sslTypeCls.getMethod("getEnabled", new Class[] {Integer.TYPE});

            // Make sure that no previous ports exist
            ssl_ports.clear();
            non_ssl_ports.clear();

            Object createGraph = cgMth.invoke(serverCls, new Object[]{serverXml});
            Object[] httpListeners = (Object[]) hlMth.invoke(createGraph, null);

            // Iterate through all the http-listeners to separte ssl and non-ssl ports
            for(Object hl : httpListeners) {
                Object isHLEnabled = isHLEnabledMth.invoke(hl, null);
                if (!((Boolean)isHLEnabled))
                    continue;

                Object port = getPortMth.invoke(hl, null);
                Object ssl = getSslMth.invoke(hl, null);
                Object isSSLEnabled = isSSLEnabledMth.invoke(ssl, new Object[] {new Integer(0)});
                if ((Boolean)isSSLEnabled) {
                    ssl_ports.add(port.toString());
                } else {
                    non_ssl_ports.add(port.toString());
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        HashMap ports = new HashMap();
        ports.put("ssl_ports", ssl_ports);
        ports.put("non_ssl_ports", non_ssl_ports);
        return ports;

    }
}


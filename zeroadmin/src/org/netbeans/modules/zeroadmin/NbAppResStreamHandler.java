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

package org.netbeans.modules.zeroadmin;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Maps an "appres:[port]//" URL to "&lt;protocol&gt;://&lt;server&gt;[:port]".
 * For example we can download a jar resource from a remote host by giving the
 * following URL: "appres://myapp/myresource.jar". This might translate to the
 * following standard URL: "http://myserver.office.net/myapp/myresource.jar".
 * <p>
 * The supported protocols are "http" and "https" depending on the server
 * configuration. It is possible to explicitly define the port number (which
 * is mapped to a specific protocol) when creating the URL. The following port
 * to protocol mappings are used:
 * <ul>
 *   <li>443 -&gt; https</li>
 *   <li>444 -&gt; https</li>
 *   <li>9444 -&gt; https</li>
 *   <li>any other port -&gt; http</li>
 * </ul>
 * <p>
 * The server host name is taken from the "netbeans.apphost" system property
 * and the protocol selection depends on the "netbeabs.ssl" system property.
 * The protocol is selected (if port is not given explicitly) based on the
 * following rule:
 * <ol>
 *   <li>if "netbeans.ssl" is set and is "true" then use "https"</li>
 *   <li>if "netbeans.ssl" is not set or is not "true" then use "http"</li>
 * </ol>
 * <p>
 * Here's some examples:
 * <pre>
 * - HTTP --
 * netbeans.apphost=myhost.mydomain.nu
 * Original URL: appres://myapp/myresource.jar
 * Translated URL: http://myhost.mydomain.nu:80/myapp/myresource.jar
 *
 * - HTTPS --
 * netbeans.apphost=myhost.mydomain.nu
 * netbeans.ssl=true
 * Original URL: appres://myapp/myresource.jar
 * Translated URL: https://myhost.mydomain.nu:443/myapp/myresource.jar
 *
 * netbeans.apphost=myhost.mydomain.nu
 * Original URL: appres:443//myapp/myresource.jar
 * Translated URL: https://myhost.mydomain.nu:443/myapp/myresource.jar
 *
 * </pre>
 * <p>
 * This stream handler delegates the actual protocol handling to the default
 * java implementations.
 *
 * @author Nokia
 * @version 1.0
 */
public class NbAppResStreamHandler extends URLStreamHandler {

//============================================================================
// Constants
//============================================================================
    private static final Logger log = Logger.getLogger(NbAppResStreamHandler.class.getName());
    private static final String P_APPHOST = "netbeans.apphost";
    private static final String P_SSL = "netbeans.ssl";
//============================================================================
// Protected methods
//============================================================================

    /**
     * @see java.net.URLStreamHandler
     */
    protected URLConnection openConnection(URL u) throws IOException {
        log.entering(getClass().getName(), "openConnection", u);
        // Sanity check
        if (!"appres".equals(u.getProtocol())) {
            throw new IOException("mismatched protocol " + u); // NOI18N
        }
        log.exiting(getClass().getName(), "openConnection");
        return new NbAppResURLConnection(u);
    }

    /**
     * @see java.net.URLStreamHandler
     */
    @Override
    protected void parseURL(URL u, String spec, int start, int limit) {
        log.entering(getClass().getName(), "parseURL", u);
        
        String apphost = System.getProperty(P_APPHOST, "localhost");
        String ssl = System.getProperty(P_SSL);
        String path = getPath(spec);
        String query = getQuery(spec);
        String ref = getRef(spec);
        int port = getPort(spec);

        String proto = "http";

        if (port == -1) {
            if (ssl != null && ssl.equals("true")) {
                proto = "https";
                port = 443;
            } else if (ssl == null || !ssl.equals("true")) {
                port = 80;
            }
        } else {
            // Explicit port -> protocol selection
            if (port == 443 || port == 9444) {
                proto = "https";
            }
        }

        // Protocol is actually taken from "u.getProtocol()" inside setURL.
        // Meaning that the protocol will always be "appres".
        setURL(u, proto, apphost, port, null, null, path, query, ref);
        log.finer("---- parseURL - newUrl: " + u + " spec: " + spec );
        log.finer("URL.getProtocol: " + u.getProtocol());
        log.finer("URL.getAuthority: " + u.getAuthority());
        log.finer("URL.getHost: " + u.getHost());
        log.finer("URL.getPort: " + u.getPort());
        log.finer("URL.getFile: " + u.getFile());
        log.finer("URL.getPath: " + u.getPath());
        log.finer("URL.getQuery: " + u.getQuery());
        log.finer("URL.getRef: " + u.getRef());
        log.finer("URL.getUserInfo: " + u.getUserInfo());
    }
//============================================================================
// Private methods
//============================================================================

    /**
     * Return port part.
     *
     * @param spec              the URL spec
     * @return                  the port number or -1 if missing from URL
     */
    private final int getPort(String spec) {
        log.entering(getClass().getName(), "getPort", spec);
        int port = -1;
        int i_colon = spec.lastIndexOf(':');
        int i_start = i_colon + 1;
        int i_end = spec.indexOf('/', i_start);

        if (i_end == -1) {
            i_end = spec.length();
        }

        if (i_colon != -1 && Character.isDigit(spec.charAt(i_start))) {
            port = Integer.parseInt(spec.substring(i_start, i_end));
        }
        log.exiting(getClass().getName(), "getPort", port);
        return port;
    }

    /**
     * Return path part.
     *
     * @param spec              the URL specification string
     * @return                  the path part or null
     */
    private final String getPath(String spec) {
        log.entering(getClass().getName(), "getPath", spec);
        String path = null;
        int i_colon = spec.lastIndexOf(':');
        int i_start = i_colon + 1;
        int i_end = spec.length();

        if (i_colon != -1) {
            // Jump over possible port
            i_start = spec.indexOf('/', i_start);

            if ((i_start + 1) < i_end) {
                i_start++;

                if (spec.charAt(i_start) == '/') {
                    i_start++;
                }

                int i_query = spec.indexOf('?');

                if (i_query != -1) {
                    i_end = i_query;
                } else {
                    int i_ref = spec.indexOf('#');

                    if (i_ref != -1) {
                        i_end = i_ref;
                    }
                }

                if (i_start < i_end) {
                    path = "/" + spec.substring(i_start, i_end);
                }
            }
        }
        log.exiting(getClass().getName(), "getPath", path);
        return path;
    }

    /**
     * Return query part.
     *
     * @param spec              the URL specification string
     * @return                  the query part or null
     */
    private final String getQuery(String spec) {
        log.entering(getClass().getName(), "getQuery", spec);
        String query = null;
        int i_start = spec.indexOf('?') + 1;
        int i_end = spec.length();

        if (i_start > 0) {
            int i_ref = spec.indexOf('#');

            if (i_ref != -1) {
                i_end = i_ref;
            }

            query = spec.substring(i_start, i_end);
        }
        log.exiting(getClass().getName(), "getQuery", query);
        return query;
    }

    /**
     * Return ref part.
     *
     * @param spec              the URL specification string
     * @return                  the ref part or null
     */
    private final String getRef(String spec) {
        log.entering(getClass().getName(), "getRef", spec);
        String ref = null;
        int i_hash = spec.indexOf('#');

        if (i_hash != -1) {
            ref = spec.substring(i_hash + 1);
        }
        log.exiting(getClass().getName(), "getRef", ref);
        return ref;
    }
    
//============================================================================
// Inner classes
//============================================================================

    /**
     * Call this from META-INF/services/java.net.URLStreamHandlerFactory.
     */
    @org.openide.util.lookup.ServiceProvider(service=java.net.URLStreamHandlerFactory.class)
    public static final class Factory implements URLStreamHandlerFactory {

        public URLStreamHandler createURLStreamHandler(String protocol) {
            if ("appres".equals(protocol)) {
                return new NbAppResStreamHandler();
            } else {
                return null;
            }
        }
    }

/**
     * Delegates connection handling to standard handlers.
     */
    private static final class NbAppResURLConnection extends URLConnection {

        /** The delegate URL connection */
        private URLConnection real = null;
        /** Buffered exception that is thrown in connect(), if set */
        private IOException exception = null;

        /**
         * Creates the application resource connection.
         */
        public NbAppResURLConnection(URL u) {
            super(u);
        }

        /**
         * Establishes a connection to the server.
         */
        public synchronized void connect() throws IOException {
            if (exception != null) {
                IOException e = exception;
                exception = null;
                throw e;
            }

            if (!connected) {
                getDelegate().connect();
                connected = true;
            }
        }

        /**
         * Creates delegate connection and opens it (openConnection).
         */
        private URLConnection getDelegate() {
            if (real == null) {
                String protocol = "http";
                int port = url.getPort();

                if (port == 443) {
                    protocol = "https";
                } else if (port == 9810) {
                    protocol = "iiop";
                }

                try {
                    URL realUrl = new URL(protocol, url.getHost(), port, url.getFile());
                    log.finer("url == " + getURL() + "\nrealUrl == " + realUrl); // NOI18N
                    real = realUrl.openConnection();
                } catch (Exception e) {
                    throw new IllegalStateException(e.toString());
                }
            }

            return real;
        }

        /**
         * Tries to connect, if not, keeps the exception so that it can
         * later be thrown in {@link #connect}.
         */
        private void tryToConnect() {
            if (!connected && exception == null) {
                try {
                    connect();
                } catch (IOException ioe) {
                    exception = ioe;
                }
            }
        }

        @Override
        public String getHeaderField(int n) {
            tryToConnect();
            if (connected) {
                return real.getHeaderField(n);
            } else {
                return null;
            }
        }

        /** Get the name of a header.
         * @param n the index
         * @return the header name
         */
        @Override
        public String getHeaderFieldKey(int n) {
            tryToConnect();
            if (connected) {
                return real.getHeaderFieldKey(n);
            } else {
                return null;
            }
        }

        /** Get a header by name.
         * @param key the header name
         * @return the value
         */
        @Override
        public String getHeaderField(String key) {
            tryToConnect();
            if (connected) {
                return real.getHeaderField(key);
            } else {
                return null;
            }
        }

        /** Get an input stream on the connection.
         * @throws IOException for the usual reasons
         * @return a stream to the object
         */
        @Override
        public InputStream getInputStream() throws IOException {
            connect();
            return real.getInputStream();
        }

        /** Get an output stream on the object.
         * @throws IOException for the usual reasons
         * @return an output stream writing to it
         */
        @Override
        public OutputStream getOutputStream() throws IOException {
            connect();
            return real.getOutputStream();
        }

        /** Get the type of the content.
         * @return the MIME type
         */
        @Override
        public String getContentType() {
            tryToConnect();
            if (connected) {
                return real.getContentType();
            } else {
                return "application/octet-stream"; // NOI18N
            }
        }

        /** Get the length of content.
         * @return the length in bytes
         */
        @Override
        public int getContentLength() {
            tryToConnect();
            if (connected) {
                return real.getContentLength();
            } else {
                return 0;
            }
        }

        /**
         * Adds a general request property specified by a key-value pair.
         */
        @Override
        public void addRequestProperty(String key, String value) {
            getDelegate().addRequestProperty(key, value);
        }

        /**
         * Set the value of the allowUserInteraction field of this URLConnection.
         */
        @Override
        public void setAllowUserInteraction(boolean allowuserinteraction) {
            getDelegate().setAllowUserInteraction(allowuserinteraction);
        }

        /**
         * Sets the default value of the useCaches field to the specified value.
         */
        @Override
        public void setDefaultUseCaches(boolean defaultusecaches) {
            getDelegate().setDefaultUseCaches(defaultusecaches);
        }

        /**
         * Sets the value of the doInput field for this URLConnection to the specified value.
         */
        @Override
        public void setDoInput(boolean doinput) {
            getDelegate().setDoInput(doinput);
        }

        /**
         * Sets the value of the doOutput field for this URLConnection to the specified value.
         */
        @Override
        public void setDoOutput(boolean dooutput) {
            getDelegate().setDoOutput(dooutput);
        }

        /**
         * Sets the value of the ifModifiedSince field of this URLConnection to the specified value.
         */
        @Override
        public void setIfModifiedSince(long ifmodifiedsince) {
            getDelegate().setIfModifiedSince(ifmodifiedsince);
        }

        /**
         * Sets the general request property.
         */
        @Override
        public void setRequestProperty(String key, String value) {
            getDelegate().setRequestProperty(key, value);
        }

        /**
         * Sets the value of the useCaches field of this URLConnection to the specified value.
         */
        @Override
        public void setUseCaches(boolean usecaches) {
            getDelegate().setUseCaches(usecaches);
        }
    }
}
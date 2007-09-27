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

package org.netbeans.modules.rmi.registry;

import java.net.*;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;

import org.openide.util.NbBundle;
import org.openide.util.WeakSet;

/** Socket factory for internal RMI Registry. Allows unbind any RMI service.
 *
 * @author  mryzl
 */

public class RMIRegistrySF extends java.rmi.server.RMISocketFactory {

    /** Default timeout for the socket. */
    public static final int DEFAULT_TIMEOUT = 1000;

    /** Set of all sockets. */
    private static WeakSet set = new WeakSet();

    /** Currently disabled sockets. 
    */
    private static HashMap map = new HashMap();

    /** Creates new RMIRegistrySSF. */
    public RMIRegistrySF() {
    }
  
    public ServerSocket createServerSocket(int port) throws java.io.IOException {
        Integer key = new Integer(port);
        Long timeout = (Long) map.get(key);
        if (timeout != null) {
            if (System.currentTimeMillis() < timeout.longValue()) throw new SSCanceledException();
            else map.remove(key);
        }
        ServerSocket ss = getDefaultSocketFactory().createServerSocket(port);
        set.add(ss);
        return ss;
    }

    /** Cancel socket.
    * @param port - port of the socket to be canceled
    * @param timeout - the same port could be used only after the timeou
    * @return false if there is no such socket
    */
    public static boolean cancelSocket(int port, int timeout) throws java.io.IOException {
        boolean status = false;
        Iterator it = set.iterator();
        while (it.hasNext()) {
            ServerSocket sss = (ServerSocket) it.next();
            if (sss.getLocalPort() == port) {
                sss.close(); // don't remove it, it should be removed
                                       // automatically
                map.put(new Integer(port), new Long(System.currentTimeMillis() + timeout));                                       
                status = true;
            }
        }
        return status;
    }

    /**
     * Create a client socket connected to the specified host and port.
     * @param  host   the host name
     * @param  port   the port number
     * @return a socket connected to the specified host and port.
     * @exception IOException if an I/O error occurs during socket creation
     * @since 1.2
     */
    public Socket createSocket(String host, int port) throws java.io.IOException {
        return getDefaultSocketFactory().createSocket(host, port);
    }
    
    /** Exception that is used when the server socket is being canceled.
    */
    static class SSCanceledException extends java.io.IOException {
        public SSCanceledException() {
            super();
        }

        public SSCanceledException(String msg) {
            super(msg);
        }
    }
}

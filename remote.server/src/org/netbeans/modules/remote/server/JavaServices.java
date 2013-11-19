/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.remote.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.dew4nb.api.Server;

/**
 *
 * @author Tomas Zezula
 */
public class JavaServices {

    private static final Logger LOG = Logger.getLogger(JavaServices.class.getName());

    //@GuardedBy("JavaServices.class")
    private static JavaServices instance;
    private final Object lock = new Object();
    private Server server;
    //@GuardedBy("lock")
    private int state;

    private JavaServices() {
    }

    void configure(int port) throws IOException {
        LOG.log(
            Level.INFO,
            "HTTP server configured with port: {0}",    //NOI18N
            port);
        server = Server.createBuilder().
                setPort(port).
                build();
        moveState(1);
    }

    void start () throws IOException {
        LOG.info("Starting HTTP server.");  //NOI18N
        moveState(2);
    }

    void stop () throws IOException {
        LOG.info("Stopping HTTP server.");  //NOI18N
        moveState(0);
    }


    private int moveState(int state) throws IOException {
        if (state < 0 || state > 2) {
            throw new IllegalArgumentException(Integer.toString(state));
        }
        synchronized (lock) {
            if (state == 0) {
                this.state = state;
            } else {
                this.state |= state;
            }
            switch (this.state) {
                case 0:
                    server.stop();
                    break;
                case 3:
                    server.start();
            }
            return this.state;
        }
    }


    public static synchronized JavaServices getInstance() {
        if (instance == null) {
            instance = new JavaServices();
        }
        return instance;
    }
}

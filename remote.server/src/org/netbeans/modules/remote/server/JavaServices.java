/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

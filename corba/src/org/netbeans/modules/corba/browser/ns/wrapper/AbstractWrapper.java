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

/*
 * AbstractWrapper.java
 *
 * Created on 12. ??jen 2000, 10:30
 */

package org.netbeans.modules.corba.browser.ns.wrapper;

/**
 *
 * @author  root
 * @version
 */
public abstract class AbstractWrapper extends Object implements Runnable {

    public static final short NOT_INITIALIZED = 0;
    public static final short INITIALIZED = 1;
    public static final short ERROR = 2;

    protected short port;
    protected Thread thread;
    protected String ior;
    protected short state;

    /** Creates new AbstractWrapper */
    public AbstractWrapper() {
	this.state = NOT_INITIALIZED;
    }
    
    public void start (short port) {
        this.port = port;
        this.thread = new Thread (this);
	this.thread.setName ("Local CORBA Name Service");
        this.thread.start();
    }
    public void stop () {
        if (thread != null && thread.isAlive())
            thread.interrupt();
    }
    
    public String getIOR() {
	synchronized (this) {
	    while (this.state == NOT_INITIALIZED) {
		try {
		    this.wait();
                }catch (InterruptedException ie) {}
	    }
	    switch (this.state) {
		case INITIALIZED:
		    return ior;
		case ERROR:
		    return null;
	    }
	}
        return null;
    }

}

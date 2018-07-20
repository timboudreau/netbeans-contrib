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
 * Children.java
 *
 * Created on September 5, 2000, 5:06 PM
 */

package org.netbeans.modules.corba.browser.ir.nodes;

import org.openide.nodes.*;
import org.netbeans.modules.corba.browser.ir.util.Refreshable;
import org.netbeans.modules.corba.browser.ir.util.AsyncTarget;

/**
 *
 * @author  tzezula
 * @version
 */
public abstract class Children extends org.openide.nodes.Children.Keys implements Refreshable, AsyncTarget {

    //private static final boolean DEBUG = true;
    private static final boolean DEBUG = false;

    /** Creates new Children */
    public static final int NOT_INITIALIZED = 0;
    public static final int TRANSIENT = 1;
    public static final int INITIALIZED = 2;
    public static final int SYNCHRONOUS = 3;
    
    protected int state;
    protected Node waitNode;
    
    public Children() {
        super ();
        synchronized (this) {
            this.state = NOT_INITIALIZED;
        }
    }
    
    public abstract void createKeys();
    
    public synchronized int getState () {
        return this.state;
    }
    
    public void removeNotify () {
	synchronized (this) {
	    this.state = NOT_INITIALIZED;
	}
    }
    
    public void preinvoke () {
    }
    
    public void invoke () {
        if (DEBUG)
            System.out.println("Invoke");
        createKeys ();
    }
    
    public void postinvoke () {
        if (this.waitNode != null) {
            remove ( new Node[] {this.waitNode});
            this.waitNode = null;
        }
        synchronized (this) {
            this.state = INITIALIZED;
        }
    }

}

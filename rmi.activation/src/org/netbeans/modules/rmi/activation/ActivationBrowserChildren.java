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

package org.netbeans.modules.rmi.activation;

import java.beans.*;
import java.util.*;

import org.openide.nodes.*;
import org.openide.util.RequestProcessor;

import org.netbeans.modules.rmi.activation.settings.RMIActivationSettings;

/** List of children of a containing node.
 * Remember to document what your permitted keys are!
 *
 * @author mryzl, Jan Pokorsky
 */
public class ActivationBrowserChildren extends Children.Keys implements PropertyChangeListener {

    /** Refresh task. */
    private RequestProcessor.Task task = null;
    /** Refresh time */
    private int timeout;
    
    private static boolean debug = Boolean.getBoolean("org.netbeans.modules.rmi.test"); // NOI18N
    
    /**
    */
    public ActivationBrowserChildren() {
    }
    
    /** Schedule refresh task.
    */
    protected void scheduleRefreshTask(int millis) {
        if (task == null) {
            updateTimeout();
            task = RequestProcessor.getDefault().create(new RefreshTask());
            task.setPriority(Thread.MIN_PRIORITY);
        }
        task.schedule(millis);
        if (debug) System.err.println("ABChildren: Task resheduled: " + millis + " ms."); // NOI18N
    }
    
    /**
    */
    protected void addNotify () {
        scheduleRefreshTask(0);
        RMIActivationSettings.getDefault().addPropertyChangeListener(this);
    }
    
    /**
    */
    protected void removeNotify () {
        setKeys (Collections.EMPTY_SET);
        RMIActivationSettings.getDefault().removePropertyChangeListener(this);
    }
    
    /** Creates nodes. */
    protected Node[] createNodes (Object key) {
        // interpret your key here...usually one node generated, but could be zero or more
        ActivationSystemItem item = (ActivationSystemItem) key;
        return new Node[] { new ActivationSystemNode(item, new ActivationSystemChildren(item)) };
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent p1) {
        if (RMIActivationSettings.PROP_ACTIVATION_SYSTEM_ITEMS.equals(p1.getPropertyName())) {
//            scheduleRefreshTask(0);
            setKeys(RMIActivationSettings.getDefault().getActivationSystemItems());
        } else if (RMIActivationSettings.PROP_REFRESH_TIME.equals(p1.getPropertyName())) {
            updateTimeout();
            if (timeout > 0) scheduleRefreshTask(timeout);
            else if (task != null && task.isFinished()) {
                boolean cancel = task.cancel();
                if (debug) System.err.println("ABChildren: task canceled: " + cancel); // NOI18N
            }
        }
    }
    
    /** Sets refresh time for browser from system options.
     */
    private void updateTimeout() {
        timeout = RMIActivationSettings.getDefault().getRefreshTime();
    }
    
    /** Automatic refresh of all connected activation system browsers. */
    private class RefreshTask implements Runnable {

        public void run() {
            ActivationSystemItem[] items = RMIActivationSettings.getDefault().getActivationSystemItems();
            setKeys(items);
            for(int i = 0; i < items.length; i++) {
                items[i].updateActivationItems();
            }
            if (timeout > 0) scheduleRefreshTask(timeout);
        }
        
    }
    
}
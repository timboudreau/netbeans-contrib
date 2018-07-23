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

package org.netbeans.modules.vcscore.runtime;

import java.lang.ref.*;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.io.*;

import org.openide.nodes.*;
import org.openide.filesystems.*;
import org.openide.filesystems.FileSystem; // override java.io.FileSystem
import org.openide.loaders.*;
import org.openide.util.*;
import java.beans.*;

/**
 *
 * @author builder
 */
public class RuntimeMainChildren extends Children.Keys  {

    private static final String NUM_OF_FINISHED_CMDS_TO_COLLECT_CHANGED_METHOD = "numOfFinishedCmdsToCollectChanged"; // NOI18N

    private LinkedList providerList;
    private RuntimeProviderListener rpl = new RuntimeProviderListener();
    
    public RuntimeMainChildren() {
        super();
    
        /** add subnodes..
         */
        providerList = new LinkedList();
        RuntimeCommandsProvider[] providers = RuntimeCommandsProvider.getRegistered();
        RuntimeCommandsProvider.addRegisteredListenerWeakly(rpl);
        if (providers != null) providerList.addAll(Arrays.asList(providers));
    }
    
    private void refreshKeys(final Collection collection) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setKeys(collection);
            }
        });
    }

    /** Called when the preparetion of nodes is needed
     */
    protected void addNotify() {
        refreshKeys (getProviders());
    }

    /** Called when all children are garbage collected *
    protected void removeNotify() {
        System.out.println(" !!  !! removeNotify(), FS size = "+getProviders().size()+"\n");
    }
     */

    private Collection getProviders() {
        /** add subnodes..
         */
        return providerList;
    }
    
    /** Creates nodes for given key.
    */
    protected Node[] createNodes( final Object key ) {
        RuntimeCommandsProvider provider = (RuntimeCommandsProvider) key;
        if (providerList.contains(provider)) {
            Node fsRuntime = provider.getNodeDelegate();
            if (fsRuntime != null) return new Node[] { fsRuntime };
        } 
        return null;
    }
    
    
    public class RuntimeProviderListener extends Object implements PropertyChangeListener {
        
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            RuntimeCommandsProvider oldProvider = (RuntimeCommandsProvider) propertyChangeEvent.getOldValue();
            RuntimeCommandsProvider newProvider = (RuntimeCommandsProvider) propertyChangeEvent.getNewValue();
            if (oldProvider != null) {
                if (providerList.remove(oldProvider) && newProvider != null) {
                    newProvider.notifyRemoved();
                }
            }
            if (newProvider != null) {
                providerList.add(newProvider);
            }
            RuntimeMainChildren.this.refreshKeys(providerList);
        }
        
    }    
}

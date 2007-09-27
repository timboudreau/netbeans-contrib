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

import java.io.IOException;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;

import org.openide.*;
import org.openide.cookies.*;
import org.openide.nodes.*;
import org.openide.util.*;
import org.openide.util.actions.*;

/**
 *
 * @author  mryzl
 */

public class ServiceNode extends AbstractNode implements Node.Cookie {

    /** Icon for service. */
    static final String SERVICE_ICON_BASE = "org/netbeans/modules/rmi/registry/resources/rmiService"; // NOI18N

    /** Creates new ServiceNode. */
    public ServiceNode(ServiceItem item) {
        this(item, Children.LEAF);
    }

    /** Creates new ServiceNode. */
    public ServiceNode(ServiceItem item, Children children) {
        super(children);
        setName(item.getName());
        CookieSet cookies = getCookieSet();
        cookies.add(item);
        cookies.add(this);

        // add class annotation property
        try {
            Sheet sheet = getSheet();
            Sheet.Set expert;
            if ((expert = sheet.get(Sheet.EXPERT)) == null) {
                expert = Sheet.createExpertSet();
                sheet.put(expert);
            }
            Node.Property p = new PropertySupport.Reflection(ServiceNode.this, String.class, "getClassAnnotation", null); // NOI18N
            p.setName("ClassAnnotation"); // NOI18N
            p.setDisplayName(getBundle("PROP_classAnnotation")); // NOI18N
            p.setShortDescription(getBundle("HINT_classAnnotation")); // NOI18N
            expert.put(p);
        } catch (NoSuchMethodException ex) {
            org.openide.ErrorManager.getDefault().notify(org.openide.ErrorManager.INFORMATIONAL, ex);
        }

        setIconBase(SERVICE_ICON_BASE);
        systemActions = new SystemAction[] {
                            SystemAction.get(org.openide.actions.DeleteAction.class),
                            null,
                            SystemAction.get(org.openide.actions.ToolsAction.class),
                            SystemAction.get(org.openide.actions.PropertiesAction.class),
                        };
    }

    /** Returns the class annotation (representing the location for a class)
    * that RMI will use to annotate the call stream
    * when marshalling objects of the given class.
    * @return class annotation
    */
    public String getClassAnnotation() {
        try {
            ServiceItem sitem = (ServiceItem) getCookie(ServiceItem.class);
            return RMIClassLoader.getClassAnnotation(sitem.getServiceClass());
        } catch (NullPointerException ex) {
            // ex.printStackTrace();
            // if class is null, return null too
        }
        return null;
    }

    public void destroy() throws IOException {
        // call unbind
        RegistryItem item = (RegistryItem) getParentNode().getCookie(RegistryItem.class);
        try {
            Registry registry = item.getRegistry();
            ServiceItem sitem = (ServiceItem) getCookie(ServiceItem.class);
            registry.unbind(sitem.getName());
        } catch (AccessException ex) {
            // if this operation is not permitted (if originating from a non-local host, for example)
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE));
        } catch (RemoteException ex) {
            // Access can be encapsulated in RemoteException
            Throwable detail = ex.detail;
            if (detail instanceof AccessException) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(detail.getMessage(), NotifyDescriptor.ERROR_MESSAGE));
            } else {
                throw ex;
            }
        } catch (NotBoundException ex) {
            // just refresh
        } finally {
            RMIRegistryChildren.updateItem(item);
        }
    }

    public boolean canDestroy() {
        return true;
    }
    
    public HelpCtx getHelpCtx () {
        return new HelpCtx (ServiceNode.class.getName());
    }

    private static String getBundle( String key ) {
        return NbBundle.getMessage( ServiceNode.class, key );
    }
}


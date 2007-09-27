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

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.awt.Dialog;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.rmi.registry.LocateRegistry;

import org.openide.*;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.*;
import org.openide.nodes.*;
import org.openide.util.*;
import org.openide.util.actions.SystemAction;
import org.openide.src.*;
import org.openide.src.nodes.SourceChildren;
import org.openide.util.datatransfer.NewType;

import org.netbeans.modules.rmi.registry.settings.RMIRegistrySettings;

/** RMI Registry root node.
*
* @author Martin Ryzl
*/
public class RMIRegistryNode extends AbstractNode
    implements Node.Cookie, RefreshCookie {

    /** Icon base. */
    static final String REGISTRY_ICON_BASE = "org/netbeans/modules/rmi/registry/resources/rmiRegistry"; // NOI18N

    /** Instance of registry node. */
    private static RMIRegistryNode rrnode;

    public RMIRegistryNode() {
        super(new RMIRegistryChildren());
        init();
        rrnode = this;
    }

    private void init() {
        setDisplayName(getBundle("PROP_RMIRegistry_Name")); // NOI18N
        setName(getBundle("PROP_RMIRegistry_Name")); // NOI18N
        systemActions = new SystemAction[] {
                            SystemAction.get(org.openide.actions.NewAction.class),
                            SystemAction.get(org.netbeans.modules.rmi.registry.LocalRegistryAction.class),
                            null,
                            SystemAction.get(org.netbeans.modules.rmi.registry.RMIRegistryRefreshAction.class),
                            SystemAction.get(org.netbeans.modules.rmi.registry.ResetLoaderAction.class),
                            null,
                            SystemAction.get(org.openide.actions.PropertiesAction.class)
                        };
        setIconBase(REGISTRY_ICON_BASE);
        CookieSet cookies = getCookieSet();
        cookies.add(this);
    }

    void refreshItem(RegistryItem item) {
        RMIRegistryChildren children = (RMIRegistryChildren) getChildren();
        children.refresh(item);
    }
    
    /** Causes refresh of the node
     *
     */
    public void refresh() {
        RMIRegistryChildren children = (RMIRegistryChildren) getChildren();
        children.refreshIt();
    }

    /** Get default instance of the node.
    */
    public static RMIRegistryNode getNode() {
        return rrnode;
    }

    /** Get possible types for new action.
    */
    public NewType[] getNewTypes() {
        return new NewType[] { new NewRegistryType() };
    }
    
    
    public HelpCtx getHelpCtx () {
        return new HelpCtx (RMIRegistryNode.class.getName());
    }

    private static String getBundle( String key ) {
        return NbBundle.getMessage( RMIRegistryNode.class, key );
    }
    
    /** NewType for RMIRegistry.
    */
    class NewRegistryType extends NewType implements ActionListener {
        private Dialog myDialog;
        private RegistryPanel rpanel;

        public String getName() {
            return getBundle("PROP_New_RMI_Registry"); // NOI18N
        }

        public void create() {
            rpanel = new RegistryPanel();
            DialogDescriptor dialogDesc = new DialogDescriptor(
               rpanel,
               getBundle("LAB_New_RMI_Registry"),  // NOI18N
               false,
               DialogDescriptor.OK_CANCEL_OPTION, 
               DialogDescriptor.OK_OPTION,
               this
            );
//            dialogDesc.setClosingOptions(new Object[0]);
            myDialog = DialogDisplayer.getDefault().createDialog(dialogDesc);
            myDialog.show();
        }

        public void actionPerformed(final ActionEvent ae) {

            RequestProcessor.getDefault().postRequest(new Runnable() {

                                              public void run() {
                                                  RegistryItem ri;
                                                  if (ae.getSource() == DialogDescriptor.OK_OPTION) {
                                                      try {
                                                          String portStr = rpanel.getPort();
                                                          int port = java.rmi.registry.Registry.REGISTRY_PORT;
                                                          if (portStr != null && portStr.length() > 0) port = Integer.parseInt(portStr);
                                                          if (port < 1 || port > 65535) throw new NumberFormatException();
                                                          
                                                          String host = rpanel.getHost();
                                                          if ((host == null) || (host.trim().length() == 0)) {
                                                              host = "localhost"; // NOI18N
                                                          }
                                                          ri = new RegistryItem(host, port);
                                                          RMIRegistrySettings.getInstance().addRegistryItem(ri);

                                                      } catch (NumberFormatException ex) {
                                                          DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                                                                                             getBundle("ERR_UnknownPort"), // NOI18N
                                                                                             NotifyDescriptor.ERROR_MESSAGE
                                                                                         ));
                                                          return;
                                                      } catch (java.net.UnknownHostException e) {
                                                          DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                                                                                             getBundle("ERR_UnknownHost"), // NOI18N
                                                                                             NotifyDescriptor.ERROR_MESSAGE
                                                                                         ));
                                                      } catch (java.rmi.UnknownHostException e) {
                                                          DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                                                                                             getBundle("ERR_UnknownHost"), // NOI18N
                                                                                             NotifyDescriptor.ERROR_MESSAGE
                                                                                         ));
                                                      } catch (Exception e) {
                                                          ErrorManager.getDefault().notify(e);
                                                      }
                                                  }
                                                  myDialog.dispose();
                                              }
                                          });
        }
    }
}

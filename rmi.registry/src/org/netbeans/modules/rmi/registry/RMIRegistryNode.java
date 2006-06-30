/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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

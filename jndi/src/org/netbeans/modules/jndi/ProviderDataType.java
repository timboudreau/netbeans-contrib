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

package org.netbeans.modules.jndi;

import java.util.Vector;
import java.awt.Dialog;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import org.openide.util.datatransfer.*;
import org.openide.*;
import org.openide.nodes.*;
import org.openide.filesystems.Repository;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileLock;
import org.netbeans.modules.jndi.settings.JndiSystemOption;


/** This class represents a NewType for JndiProvidersNode
 */
public class ProviderDataType extends NewType {

    /** Temporary Dialog holder */
    private Dialog dlg;
    
    /** Owner of this DataType*/
    private JndiProvidersNode node;
    
    /** Cached JndiSystemOption*/
    private JndiSystemOption settings;


    /** Creates new ProviderDataType
     *  @param JndiProviderNode node for which the class is being created
     */
    public ProviderDataType (JndiProvidersNode node) {
        this.node = node;
    }

    /** Creation of new child node */
    public void create () {
        if (this.settings == null) {
            settings = (JndiSystemOption)JndiSystemOption.findObject (JndiSystemOption.class);
        }
        final NewProviderPanel panel = new NewProviderPanel();
        final DialogDescriptor descriptor = new DialogDescriptor(panel,
                                            JndiRootNode.getLocalizedString("TITLE_NewProvider"),
                                            true,
                                            DialogDescriptor.OK_CANCEL_OPTION,
                                            DialogDescriptor.OK_OPTION,
                                            new ActionListener() {
                                                public void actionPerformed(ActionEvent event) {
                                                    if (event.getSource() == DialogDescriptor.OK_OPTION) {
                                                        String provider = panel.getFactory();
                                                        String context = panel.getContext();
                                                        String root = panel.getRoot();
                                                        String authentication = panel.getAuthentification();
                                                        String principal = panel.getPrincipal();
                                                        String credentials = panel.getCredentials();
                                                        Vector rest = panel.getAditionalProperties();
                                                        if (provider==null || provider.equals("")){
                                                            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(JndiRootNode.getLocalizedString("EXC_Template_Item"), NotifyDescriptor.Message.ERROR_MESSAGE));
                                                            return;
                                                        }
                                                        if (ProviderDataType.this.settings.getProviders(false).get(provider)!=null){
                                                            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(JndiRootNode.getLocalizedString("EXC_Template_Provider_Exists"),NotifyDescriptor.Message.ERROR_MESSAGE));
                                                            return;
                                                        }
                                                        FileSystem fs = Repository.getDefault().getDefaultFileSystem();
                                                        FileObject fo = fs.getRoot().getFileObject("JNDI");
                                                        ProviderProperties p = new ProviderProperties();
                                                        p.setFactory(provider);
                                                        p.setContext(context);
                                                        p.setRoot(root);
                                                        p.setAuthentification(authentication);
                                                        p.setPrincipal(principal);
                                                        p.setCredentials(credentials);
                                                        p.setAdditional(rest);
                                                        FileLock lock=null;
                                                        try{
                                                            String label = provider.replace('.','_');
                                                            FileObject templateFile=fo.createData(label,"impl");
                                                            lock = templateFile.lock();
                                                            java.io.OutputStream out = templateFile.getOutputStream(lock);
                                                            p.store(out,JndiRootNode.getLocalizedString("FILE_COMMENT"));
                                                            out.close();
                                                            p.addPropertyChangeListener(ProviderDataType.this.settings);
                                                            ProviderDataType.this.settings.getProviders(false).put(provider,p);
                                                            ProviderDataType.this.node.getChildren ().add ( new Node[] {new ProviderNode (provider)});
                                                        }catch(java.io.IOException ioe){
                                                            ioe.printStackTrace();
                                                            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(JndiRootNode.getLocalizedString("EXC_Template_IOError"), NotifyDescriptor.Message.ERROR_MESSAGE));
                                                            return;
                                                        }
                                                        finally{
                                                            if (lock!=null) lock.releaseLock();
                                                        }
                                                        dlg.setVisible(false);
                                                        dlg.dispose();
                                                    }else if (event.getSource() == DialogDescriptor.CANCEL_OPTION){
                                                        dlg.setVisible(false);
                                                        dlg.dispose();
                                                    }
                                                }
                                            });
        dlg = DialogDisplayer.getDefault().createDialog(descriptor);
        dlg.setVisible(true);
    }

    /** Returns name of object that this class is factory for
     *  @return String name
     */
    public String getName() {
        return JndiRootNode.getLocalizedString("CTL_NEW_PROVIDER");
    }

}
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
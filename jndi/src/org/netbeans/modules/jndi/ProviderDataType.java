/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.enterprise.modules.jndi;

import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.awt.Dialog;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.naming.Context;
import org.openide.util.datatransfer.*;
import org.openide.*;
import org.openide.nodes.*;
import org.openide.filesystems.Repository;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileLock;


/** This class represents a NewType for JndiProvidersNode 
 */
public class ProviderDataType extends NewType {
  
  /** Node for which the class was created*/
  private JndiProvidersNode node;
  
  /** Temporary Dialog holder */
  private Dialog dlg;
  

  /** Creates new ProviderDataType 
   *  @param JndiProviderNode node for which the class is being created
   */
  public ProviderDataType(JndiProvidersNode node) {
    this.node = node;
  }
  
  /** Creation of new child node */
  public void create () {
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
                  TopManager.getDefault().notify(new NotifyDescriptor.Message(JndiRootNode.getLocalizedString("EXC_Template_Item"), NotifyDescriptor.Message.ERROR_MESSAGE));
                  return;
              }
              if (ProviderDataType.this.node.providers.get(provider)!=null){
                TopManager.getDefault().notify(new NotifyDescriptor.Message(JndiRootNode.getLocalizedString("EXC_Template_Provider_Exists"),NotifyDescriptor.Message.ERROR_MESSAGE));
                return;
              }
              FileSystem fs = TopManager.getDefault().getRepository().getDefaultFileSystem();
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
                p.addPropertyChangeListener(node);
                ProviderDataType.this.node.providers.put(provider,p);
                ProviderDataType.this.node.getChildren ().add ( new Node[] {new ProviderNode (provider)});
              }catch(java.io.IOException ioe){
                ioe.printStackTrace();
                TopManager.getDefault().notify( new NotifyDescriptor.Message(JndiRootNode.getLocalizedString("EXC_Template_IOError"), NotifyDescriptor.Message.ERROR_MESSAGE));
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
    dlg = TopManager.getDefault().createDialog(descriptor);
    dlg.setVisible(true);
  }
  
  /** Returns name of object that this class is factory for
   *  @return String name
   */
  public String getName() {
    return JndiRootNode.getLocalizedString("CTL_NEW_PROVIDER");
  }
  
}
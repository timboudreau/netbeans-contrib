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

package org.netbeans.modules.jndi;

import java.io.IOException;
import java.awt.Dialog;
import java.awt.event.*;
import java.util.Hashtable;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.naming.*;
import javax.naming.directory.*;

import org.openide.util.datatransfer.*;
import org.openide.*;
import org.openide.nodes.*;
import org.netbeans.modules.jndi.settings.JndiSystemOption;
import org.netbeans.modules.jndi.gui.TimeOutPanel;
import org.netbeans.modules.jndi.gui.NotFoundPanel;
import org.openide.util.HelpCtx;

/** This class represents Datatype for JNDI Nodes
 *  It is responsible for adding of new contexts and creating subdirs in Jndi tree
 *
 *  @author Ales Novak, Tomas Zezula
 */
final class JndiDataType extends NewType {

    /** Node for which is the NewType created */
    protected AbstractNode node;
    /** Variable for dialog */
    private Dialog dlg = null;
    /** Panel for Dialog */
    private NewJndiRootPanel panel;
    /** Hashtable of providers taken from JNDI root node */
    private JndiProvidersNode pnode;
    /** State of connect Thread*/
    private short state;

    /** Constructor
     *  @param node the Jndi root node
     */
    public JndiDataType(JndiRootNode node, JndiProvidersNode pnode) {
        this.pnode=pnode;
        this.node = node;
    }

    /** Constructor for
     *  @param node the Jndi context
     */
    public JndiDataType(JndiNode node) {
        this.node = node;
    }

    /** Constructor
     *  @param node the Jndi non Context object
     */ 
    public JndiDataType(JndiLeafNode node) {
        this.node = node;
    }


    /** This is callbvack for OpenAPi, just set name tu null and call
     *  create(String)
     */
    public void create() throws IOException{
        this.create(null);
    }


    /** This method creates either new context, if this.node is instance of JndiRootNode,
     *  or Subdir if this.node is instance of JNDINode
     */
    public void create(String provider) throws IOException {

        DialogDescriptor descriptor = null;

        if (node instanceof JndiRootNode) {
            // Ask for new initial context and context factory
            panel = new NewJndiRootPanel(this.pnode.providers);
            panel.select(provider);
            final JButton okButton = new JButton (JndiRootNode.getLocalizedString("CTL_Ok"));
            final JButton cancelButton = new JButton (JndiRootNode.getLocalizedString("CTL_Cancel"));
            descriptor = new DialogDescriptor(panel,
                          JndiRootNode.getLocalizedString("TITLE_NewContext"),
                          true,
                          new Object[] {okButton, cancelButton},
                          okButton,
                          DialogDescriptor.BOTTOM_ALIGN,
                          HelpCtx.DEFAULT_HELP,
                          new ActionListener() {
                            public void actionPerformed(ActionEvent event) {
                              if (event.getSource() == okButton) {
                                  okButton.setEnabled (false);  // Disable buttons for this transient time
                                  cancelButton.setEnabled (false);
                                org.openide.TopManager.getDefault().setStatusText(JndiRootNode.getLocalizedString("TITLE_WaitOnConnect"));
                                Runnable controller = new Runnable () {
                                     public void run () {
                                         // redispatch to a different thread
                                         Runnable run = new Runnable() {
                                         public void run() {
                                         try {
                                         // Here we have to check the context, if it works
                                         // because all ehe operation starting with addContext
                                         // are asynchronous to AWT Thread
                                         Class.forName(panel.getFactory());
                                         String root = panel.getRoot();
                                         Hashtable env = ((JndiRootNode)node).createContextProperties(
                                             panel.getLabel(),
                                             panel.getFactory(),
                                             panel.getContext(),
                                             root,
                                             panel.getAuthentification(),
                                             panel.getPrincipal(),
                                             panel.getCredentials(),
                                             panel.getAditionalProperties());
                                             Context ctx = new JndiDirContext(env);
                                             if (root != null && root.length() > 0){
                                                ctx  = (Context) ctx.lookup(root);
                                             }
                                             else{
                                                // If we don't perform lookup
                                                // we should check the context
                                                ((JndiDirContext)ctx).checkContext();
                                             }
                                             ((JndiRootNode)node).addContext(ctx);
                                             org.openide.TopManager.getDefault().setStatusText(JndiRootNode.getLocalizedString("TXT_Connected"));
                                             dlg.setVisible (false);
                                             dlg.dispose();
                                          }catch (ClassNotFoundException cnfe){
                                            TopManager.getDefault().setStatusText(JndiRootNode.getLocalizedString("TXT_ConnectFailed"));
                                            NotFoundPanel errdescriptor = new NotFoundPanel (panel.getFactory());
                                            TopManager.getDefault().notify(new NotifyDescriptor.Message(errdescriptor,NotifyDescriptor.ERROR_MESSAGE));
                                          }
                                          catch (NamingException ne) {
                                            Throwable e;
                                            if (ne.getRootCause() != null) {
                                              e = ne.getRootCause();
                                            } else {
                                              e = ne;
                                            }
                                            if (e instanceof JndiException) {
                                               TopManager.getDefault().setStatusText(JndiRootNode.getLocalizedString("TXT_ConnectFailed"));
                                               TopManager.getDefault().notify(new NotifyDescriptor.Message(JndiRootNode.getLocalizedString("EXC_Items"), NotifyDescriptor.Message.ERROR_MESSAGE));
                                            }
                                            else if (e instanceof javax.naming.InterruptedNamingException || e instanceof java.io.InterruptedIOException || e instanceof java.lang.InterruptedException ){
                                                String msg;
                                                if ((e.getMessage() == null) || e.getMessage().equals("")) {
                                                msg = e.getClass().getName();
                                                } else {
                                                  msg = e.getClass().getName() + ": " + e.getMessage();
                                                }
                                                TopManager.getDefault().setStatusText(JndiRootNode.getLocalizedString("TXT_ConnectFailed"));
                                                TopManager.getDefault().notify (new NotifyDescriptor.Exception(e,new TimeOutPanel(msg,JndiRootNode.getLocalizedString("NOTE_TimeOut"))));
                                            }
                                            else {
                                                TopManager.getDefault().setStatusText(JndiRootNode.getLocalizedString("TXT_ConnectFailed"));
                                                JndiRootNode.notifyForeignException(e);
                                            }
                                           }
                                           catch (NullPointerException npe){
                                            // Thrown by some providers when bad url is given
                                            TopManager.getDefault().setStatusText(JndiRootNode.getLocalizedString("TXT_ConnectFailed"));
                                            JndiRootNode.notifyForeignException(npe);
                                           }
                                           finally {
                                               okButton.setEnabled (true);  // Enable buttons
                                               cancelButton.setEnabled (true);
                                           }
                                          }
                                         };
                                         Thread t = new Thread(run);
                                         t.start();
                                         try {
                                            int waitTime;
                                            JndiSystemOption option = (JndiSystemOption) JndiSystemOption.findObject (JndiSystemOption.class, true);
                                            if (option != null)
                                                waitTime = option.getTimeOut();
                                             else
                                                waitTime = JndiSystemOption.DEFAULT_TIMEOUT;
                                            t.join(4000);
                                         }catch (InterruptedException ie){}
                                         if (t.isAlive()){
                                            t.interrupt();
                                         }
                                       }  // run outher
                                     }; // Runnable Outher
                          new Thread (controller).start();
                          }
                      }});
            descriptor.setClosingOptions (new Object[] {
                                              DialogDescriptor.CANCEL_OPTION
                                          });
            dlg = TopManager.getDefault().createDialog(descriptor);
            dlg.setVisible(true);
        } else if (node instanceof JndiNode) {

            // Ask for subcontext name
            // This is under construction
            final NewJndiSubContextPanel subCtxPanel = new NewJndiSubContextPanel();
            descriptor = new DialogDescriptor(subCtxPanel,
                                              JndiRootNode.getLocalizedString("TITLE_NewJndiDirectory"),
                                              false,
                                              DialogDescriptor.OK_CANCEL_OPTION,
                                              DialogDescriptor.OK_OPTION,
                                              new ActionListener() {
                                                  public void actionPerformed(ActionEvent event) {
                                                      if (event.getSource() == DialogDescriptor.OK_OPTION) {
                                                          JndiChildren cld = (JndiChildren) node.getChildren();
                                                          Context context = cld.getContext();
                                                          try {
                                                              Name nCtx = ((Name) cld.getOffset().clone()).add(subCtxPanel.getName());
                                                              context.createSubcontext(nCtx);
                                                              cld.prepareKeys();
                                                          } catch (NamingException ne) {
                                                              JndiRootNode.notifyForeignException(ne);
                                                          }
                                                          dlg.setVisible(false);
                                                          dlg.dispose();
                                                      } else if (event.getSource() == DialogDescriptor.CANCEL_OPTION) {
                                                          dlg.setVisible(false);
                                                          dlg.dispose();
                                                      }
                                                  }
                                              }
                                             );
            dlg = TopManager.getDefault().createDialog(descriptor);
            dlg.setVisible(true);
        }

        // Jndi Leaf can't create subcontexts
    }

    /** Returns name of Node class
     *  @return stringified type of node
     */
    public String getName() {
        if (this.node instanceof JndiRootNode) {
            return JndiRootNode.getLocalizedString("CTL_Context");
        } else if (this.node instanceof JndiNode) {
            return JndiRootNode.getLocalizedString("CTL_Directory");
        } else {
            return "";
        }
    }
}

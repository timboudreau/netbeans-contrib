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
    
    /** Statuts of connect operation to context
     */
    private static final int SUCCESSFULL = 0;
    private static final int CLASS_NOT_FOUND_EXCEPTION = 1;
    private static final int NAMING_EXCEPTION = 2;
    private static final int INTERRUPTED_EXCEPTION = 3;
    private static final int NAMING_INTERRUPTED_EXCEPTION = 4;
    private static final int JNDI_EXCEPTION =5;
    private static final int OTHER_EXCEPTION = 6;
	private static final int TIMEOUT_TO_SHORT = 7;
    
    /** This class represents an status set by connector thread
     *  to inform the controller thread about result of connect
     *  operation
     */
    static class ConnectOperationStatus {
        
        private int status;
        private Throwable exception;
        
        public ConnectOperationStatus () {
            this.status = SUCCESSFULL;
        }
        
        public ConnectOperationStatus (int status) {
            this.status = status;
        }
        
        public ConnectOperationStatus (int status, Throwable e) {
            this.status = status;
            this.exception = e;
        }
        
        public int getOperationStatus () {
            return this.status;
        }
        
        public Throwable getException () {
            return this.exception;
        }
    }

    /** Node for which is the NewType created */
    protected AbstractNode node;
    /** Variable for dialog */
    private Dialog dlg = null;
    /** Panel for Dialog */
    private NewJndiRootPanel panel;
    /** State of connect Thread*/
    private short state;

    /** Constructor
     *  @param node the Jndi root node
     */
    public JndiDataType(JndiRootNode node) {
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
            panel = new NewJndiRootPanel();
            panel.select(provider);
            final JButton okButton = new JButton (JndiRootNode.getLocalizedString("CTL_Ok"));
            okButton.getAccessibleContext().setAccessibleDescription (JndiRootNode.getLocalizedString("CTL_Ok"));
            final JButton cancelButton = new JButton (JndiRootNode.getLocalizedString("CTL_Cancel"));
            cancelButton.getAccessibleContext().setAccessibleDescription(JndiRootNode.getLocalizedString("CTL_Cancel"));
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
                                
                                Runnable controller = new Runnable () {
                                     public void run () {
                                         // redispatch to a different thread
                                         // the Connector class
                                         class Connector implements Runnable{
                                             
                                            private ConnectOperationStatus status;
                                             
                                            public void run() {
                                                try {
                                                    // Here we have to check the context, if it works
                                                    // because all ehe operation starting with addContext
                                                    // are asynchronous to AWT Thread
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
                                                    checkContextValidity (env);         // Should be optimized
                                                    ((JndiRootNode)node).addContext(env);
                                                    synchronized (this) { 
                                                        this.status = new ConnectOperationStatus (SUCCESSFULL);
                                                    }
                                                }catch (ClassNotFoundException cnfe){
                                                    synchronized (this) {
                                                        this.status = new ConnectOperationStatus(CLASS_NOT_FOUND_EXCEPTION,cnfe);
                                                    }
                                                }
                                                catch (NamingException ne) {
                                                    synchronized (this) {
                                                        Throwable e;
                                                        if (ne.getRootCause() != null)
                                                            e = ne.getRootCause();
                                                        else
                                                            e = ne;
                                                        if (e instanceof JndiException) {
                                                            this.status = new ConnectOperationStatus (JNDI_EXCEPTION,e);
                                                        }
                                                        else if (e instanceof javax.naming.InterruptedNamingException || e instanceof InterruptedException) {
                                                            this.status = new ConnectOperationStatus (NAMING_INTERRUPTED_EXCEPTION,e);
                                                        }
                                                        else {
                                                            this.status = new ConnectOperationStatus (NAMING_EXCEPTION,e);
                                                        }
                                                    }
                                                }
                                                catch (Exception generalExc){
                                                    // Thrown by some providers when bad url is given
                                                    synchronized (this) {
                                                        this.status = new ConnectOperationStatus (OTHER_EXCEPTION, generalExc);
                                                    }
                                                }
                                          }
                                          
                                          public synchronized ConnectOperationStatus getOperationStatus () {
                                              return this.status;
                                          }
                                          
                                          private void checkContextValidity (Hashtable env) throws Exception {
                                              Class.forName((String)env.get (javax.naming.Context.INITIAL_CONTEXT_FACTORY));
                                              JndiDirContext ctx = new JndiDirContext (env);
                                              ctx.checkContext ();
                                          }
                                          
                                         };
                                         try {
                                            okButton.setEnabled (false);  // Disable buttons for this transient time
                                            cancelButton.setEnabled (false);
                                            org.openide.TopManager.getDefault().setStatusText(JndiRootNode.getLocalizedString("TITLE_WaitOnConnect"));
                                            Connector connector = new Connector();
                                            Thread t = new Thread(connector);
                                            t.start();
                                            try {
                                                int waitTime;
                                                JndiSystemOption option = (JndiSystemOption) JndiSystemOption.findObject (JndiSystemOption.class, true);
                                                if (option != null)
                                                    waitTime = option.getTimeOut();
                                                else
                                                    waitTime = JndiSystemOption.DEFAULT_TIMEOUT;
                                                t.join(waitTime);
                                            }catch (InterruptedException ie){}
                                            if (t.isAlive()){
                                                t.interrupt();
                                                if (t.isAlive()) // If provider does not test isInterrupted
                                                    t.stop ();   // cancel it by ThreadDeath
                                            }
                                            ConnectOperationStatus status = connector.getOperationStatus ();
                                            int statusCode = TIMEOUT_TO_SHORT; // By default we suppose that time was to short to start connector thread
                                            if (status != null)
                                                statusCode = status.getOperationStatus();
                                            switch (statusCode) {
                                                case SUCCESSFULL:
                                                    org.openide.TopManager.getDefault().setStatusText(JndiRootNode.getLocalizedString("TXT_Connected"));
                                                    dlg.setVisible(false);
                                                    dlg.dispose();
                                                    break;
                                                case CLASS_NOT_FOUND_EXCEPTION:
                                                    TopManager.getDefault().setStatusText(JndiRootNode.getLocalizedString("TXT_ConnectFailed"));
                                                    NotFoundPanel errdescriptor = new NotFoundPanel(panel.getFactory());
                                                    TopManager.getDefault().notify(new NotifyDescriptor.Message(errdescriptor,NotifyDescriptor.ERROR_MESSAGE));
                                                    break;
                                                case JNDI_EXCEPTION:
                                                    TopManager.getDefault().setStatusText(JndiRootNode.getLocalizedString("TXT_ConnectFailed"));
                                                    TopManager.getDefault().notify(new NotifyDescriptor.Message(JndiRootNode.getLocalizedString("EXC_Items"), NotifyDescriptor.Message.ERROR_MESSAGE));
                                                    break;
                                                case TIMEOUT_TO_SHORT:
                                                    TopManager.getDefault().notify(new NotifyDescriptor.Message(JndiRootNode.getLocalizedString("EXC_TimeoutToShort"), NotifyDescriptor.Message.ERROR_MESSAGE));
                                                    break;
                                                case INTERRUPTED_EXCEPTION:
                                                case NAMING_INTERRUPTED_EXCEPTION:
                                                    Throwable e = status.getException();
                                                    String msg;
                                                    if ((e.getMessage() == null) || e.getMessage().equals(""))
                                                        msg = e.getClass().getName();
                                                    else
                                                        msg = e.getClass().getName() + ": " + e.getMessage();
                                                    TopManager.getDefault().setStatusText(JndiRootNode.getLocalizedString("TXT_ConnectFailed"));
                                                    TopManager.getDefault().notify(new NotifyDescriptor.Exception(e,new TimeOutPanel(msg,JndiRootNode.getLocalizedString("NOTE_TimeOut"))));
                                                    break;
                                                case NAMING_EXCEPTION:
                                                    TopManager.getDefault().setStatusText(JndiRootNode.getLocalizedString("TXT_ConnectFailed"));
                                                    JndiRootNode.notifyForeignException(status.getException());
                                                    break;
                                                case OTHER_EXCEPTION:
                                                    TopManager.getDefault().setStatusText(JndiRootNode.getLocalizedString("TXT_ConnectFailed"));
                                                    JndiRootNode.notifyForeignException(status.getException());
                                                    break;
                                            }
                                         }finally {
                                             okButton.setEnabled (true);
                                            cancelButton.setEnabled (true);
                                         }
                                       }  // run outher
                                     }; // Runnable Outher
                            new Thread (controller).start();
                          }
                          else if (event.getSource () == cancelButton) {
                              dlg.setVisible (false);
                              dlg.dispose();
                          }
                      }});
            dlg = TopManager.getDefault().createDialog(descriptor);
            dlg.setVisible(true);
        } else if (node instanceof JndiNode) {

            // Ask for subcontext name
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
                                                              context.createSubcontext(subCtxPanel.getName());
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

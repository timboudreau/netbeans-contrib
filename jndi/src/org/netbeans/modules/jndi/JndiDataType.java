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

import java.io.IOException;
import java.awt.Dialog;
import java.awt.event.*;
import java.util.Hashtable;
import javax.swing.JPanel;
import javax.naming.*;
import javax.naming.directory.*;

import org.openide.util.datatransfer.*;
import org.openide.*;
import org.openide.nodes.*;


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
   *  @param node the Jndi non COntext object
   */ 
  public JndiDataType(JndiLeafNode node) {
    this.node = node;
  }
  
  
  /** This method creates either new context, if this.node is instance of JndiRootNode,
   *  or Subdir if this.node is instance of JNDINode
   */
  public void create() throws IOException {

    DialogDescriptor descriptor = null;

    if (node instanceof JndiRootNode) {
      // Ask for new initial context and context factory
      panel = new NewJndiRootPanel(this.pnode.providers);
      descriptor = new DialogDescriptor(panel,
        JndiRootNode.getLocalizedString("TITLE_NewContext"),
        true,
        DialogDescriptor.OK_CANCEL_OPTION,
        DialogDescriptor.OK_OPTION,
        new ActionListener() {
          public void actionPerformed(ActionEvent event) {
            if (event.getSource() == DialogDescriptor.OK_OPTION) {
              // redispatch to a different thread
              Runnable run = new Runnable() {
                public void run() {
                  try {
                    ((JndiRootNode) node).addContext(
                      panel.getLabel(),
                      panel.getFactory(),
                      panel.getContext(),
                      panel.getAuthentification(),
                      panel.getPrincipal(),
                      panel.getCredentials(),
                      panel.getAditionalProperties()
                    );
                  } catch (NamingException ne) {
                    Throwable e;
                    if (ne.getRootCause() != null) {
                      e = ne.getRootCause();
                    } else {
                      e = ne;
                    }
                    if (e instanceof JndiException) {
                      TopManager.getDefault().notify(new NotifyDescriptor.Message(JndiRootNode.getLocalizedString("EXC_Items"), NotifyDescriptor.Message.ERROR_MESSAGE));
                    } else {
                      JndiRootNode.notifyForeignException(e);
                    }
                  }
                }
              };
              // slow
              Thread t = new Thread(run);
              t.start();
              try {
                t.join(4000);
              } catch (InterruptedException e)  {
              }
              if (t.isAlive()) {
                t.interrupt();
                // t.stop();
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
              DirContext context = cld.getContext();
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
      return "Context";
    } else if (this.node instanceof JndiNode) {
      return "Directory";
    } else {
      return "";
    }
  }
}

/*
 * <<Log>>
 *  10   Gandalf   1.9         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  9    Gandalf   1.8         10/6/99  Tomas Zezula    
 *  8    Gandalf   1.7         8/7/99   Ian Formanek    getString->getLocalizedString
 *        to avoid compiler warnings
 *  7    Gandalf   1.6         7/9/99   Ales Novak      localization + code 
 *       requirements followed
 *  6    Gandalf   1.5         6/18/99  Ales Novak      redesigned + delete 
 *       action
 *  5    Gandalf   1.4         6/10/99  Ales Novak      gemstone support + 
 *       localizations
 *  4    Gandalf   1.3         6/9/99   Ales Novak      refresh action + 
 *       destroying subcontexts
 *  3    Gandalf   1.2         6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  2    Gandalf   1.1         6/8/99   Ales Novak      sources beautified + 
 *       subcontext creation
 *  1    Gandalf   1.0         6/4/99   Ales Novak      
 * $
 */

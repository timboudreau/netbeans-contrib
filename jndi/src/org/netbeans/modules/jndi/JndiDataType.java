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
import javax.swing.JPanel;
import javax.naming.*;
import javax.naming.directory.*;

import org.openide.util.datatransfer.*;
import org.openide.*;
import org.openide.nodes.*;

/** This class represents Datatype for JNDI subtree.
* It is responsible for adding of new contexts and creating subdirs
*/
final class JndiDataType extends NewType {

  protected AbstractNode node;
  private Dialog dlg = null;
  private NewJndiRootPanel panel;

  // Constructor for JNDI root
  public JndiDataType(JndiRootNode node) {
    this.node = node;
  }
  
  // Constructor for JNDI subdirectory
  public JndiDataType(JndiNode node) {
    this.node = node;
  }
  
  // Constructoy for JNDI leafnode
  public JndiDataType(JndiLeafNode node) {
    this.node = node;
  }
  
  
  //This method creates either new context, it this.node is instance of JndiRootNode
  // or Subdir if this.node is instance of JNDINode
  public void create() throws IOException {

    DialogDescriptor descriptor = null;

    if (node instanceof JndiRootNode) {
      // Ask for new initial context and context factory
      panel = new NewJndiRootPanel();
      descriptor = new DialogDescriptor(panel,
        JndiRootNode.getString("TITLE_NewContext"),
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
                    e.printStackTrace();
                    if (e instanceof JndiException) {
                      TopManager.getDefault().notify(new NotifyDescriptor.Message(JndiRootNode.getString("EXC_Items"), NotifyDescriptor.Message.ERROR_MESSAGE));
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
                // t.stop();?
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
        JndiRootNode.getString("TITLE_NewJndiDirectory"),
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
  
  //Returns stingified type of node
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

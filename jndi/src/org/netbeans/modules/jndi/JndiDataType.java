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

import com.netbeans.ide.util.datatransfer.*;
import com.netbeans.ide.*;
import com.netbeans.ide.nodes.*;

/** This class represents Datatype for JNDI subtree.
* It is responsible for adding of new contexts and creating subdirs
*/
final class JndiDataType extends NewType {

  protected AbstractNode node;
  private Dialog dlg = null;
  private JPanel panel;

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
        "New Jndi Context",
        true,
        DialogDescriptor.OK_CANCEL_OPTION,
        DialogDescriptor.OK_OPTION,
        new ActionListener() {
          public void actionPerformed(ActionEvent event) {
            if (event.getSource() == DialogDescriptor.OK_OPTION) {
              try {
                ((JndiRootNode)node).addContext(
                  ((NewJndiRootPanel)panel).getLabel(),
                  ((NewJndiRootPanel)panel).getFactory(),
                  ((NewJndiRootPanel)panel).getContext(),
                  ((NewJndiRootPanel)panel).getAuthentification(),
                  ((NewJndiRootPanel)panel).getPrincipal(),
                  ((NewJndiRootPanel)panel).getCredentials(),
                  ((NewJndiRootPanel)panel).getAditionalProperties()
                );
              } catch(NamingException ne) {
                if (ne instanceof JndiException) {
                  TopManager.getDefault().notify(new NotifyDescriptor.Message("Items Label, Factory, Context and root must be filled!",NotifyDescriptor.Message.ERROR_MESSAGE));
                } else {
                  TopManager.getDefault().notifyException(ne);
                }
                return;
              }
              dlg.setVisible(false);
              dlg.dispose();
            } else if (event.getSource()==DialogDescriptor.CANCEL_OPTION) {
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
      panel = new NewJndiSubContextPanel();
      descriptor = new DialogDescriptor(panel,"New Jndi Directory",
        false,
        DialogDescriptor.OK_CANCEL_OPTION,
        DialogDescriptor.OK_OPTION,
        new ActionListener() {
          public void actionPerformed(ActionEvent event) {
            if (event.getSource() == DialogDescriptor.OK_OPTION) {
              JndiChildren cld = (JndiChildren) node.getChildren();
              DirContext context = cld.getContext();
              try {
                Name nCtx = ((Name) cld.getOffset().clone()).add(panel.getName());
                context.createSubcontext(nCtx);
                cld.prepareKeys();
              } catch(NamingException ne) {
                TopManager.getDefault().notifyException(ne);
                return;
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

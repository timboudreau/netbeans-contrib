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
import java.util.ArrayList;
import org.openide.modules.ModuleInstall;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
/*JNDI Module
 *
 * @author Tomas Zezula
 */
public final class JndiModule extends ModuleInstall {
  
  /** The list with red properties*/
  static ArrayList redProviders = null;
  
  public void installed() {
  }

  public void uninstalled() {
  }

  public void restored() {
  }
  
  /** Externalization write
   *  @param ObjectOutput out the stream
   */
  public void writeExternal (java.io.ObjectOutput out) {
    JndiRootNode node = JndiRootNode.getDefault();
    if (node != null){
      Node[] nodes = node.getChildren().getNodes();
      ArrayList array = new ArrayList();
      for (int i = 0; i < nodes.length; i++) {
        if (!nodes[i].getName().equals(JndiRootNode.getLocalizedString(JndiProvidersNode.DRIVERS))) {
          try {
           Hashtable contextProperties = ((JndiAbstractNode) nodes[i]).getInitialDirContextProperties();
          array.add(contextProperties);
        }catch (javax.naming.NamingException ne){}
        }
      }
      try {
          out.writeObject(array);
        }catch(java.io.IOException ioe){}
    }
  }
  
  /** Externalization read
   *  @param ObjectInput in the stream
   */
  public void readExternal (java.io.ObjectInput in) {
    try{
      redProviders = (ArrayList) in.readObject();
      JndiRootNode node = JndiRootNode.getDefault();
      if ( node != null ) node.initStartContexts();
    }catch(java.io.IOException ioe){
    }
    catch(ClassNotFoundException cnfe) {
    }
  }

  
}

/*
 * <<Log>>
 *  7    Gandalf   1.6         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  6    Gandalf   1.5         10/6/99  Tomas Zezula    
 *  5    Gandalf   1.4         10/4/99  Tomas Zezula    
 *  4    Gandalf   1.3         7/9/99   Ales Novak      localization + code 
 *       requirements followed
 *  3    Gandalf   1.2         6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  2    Gandalf   1.1         6/8/99   Ales Novak      sources beautified + 
 *       subcontext creation
 *  1    Gandalf   1.0         6/4/99   Ales Novak      
 * $
 */

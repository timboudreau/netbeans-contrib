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

package org.netbeans.modules.corba.browser.ir.nodes;

import java.util.ArrayList;
import org.openide.TopManager;
import org.openide.util.datatransfer.ExClipboard;
import java.awt.datatransfer.StringSelection;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;
import org.openide.nodes.Node;
import org.netbeans.modules.corba.browser.ir.util.Refreshable;
import org.netbeans.modules.corba.browser.ir.util.Generatable;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupportFactory;
/** 
 *
 * @author  Tomas Zezula
 * @version 1.0
 */
public abstract class IRContainerNode extends IRAbstractNode implements Node.Cookie, Generatable {

  /** Creates new IRContainerNode */
  public IRContainerNode(Children children) {
    super(children);
    this.getCookieSet().add(this);
  }
  
  
  public void refresh () {
    ((Refreshable)this.getChildren()).createKeys ();
  }

  public void generateCode() {
      Node node = this.getParentNode();
      String code ="";
      
      // Generate the start of namespace
      ArrayList stack = new ArrayList();
      while ( node instanceof IRContainerNode){
	  stack.add(((GenerateSupportFactory)node).createGenerator());
	  node = node.getParentNode();
      }
      int size = stack.size();
      for (int i = size -1 ; i>=0; i--)
        code = code + ((GenerateSupport)stack.get(i)).generateHead((size -i -1));
      
      
      // Generate element itself
      code = code + this.createGenerator().generateSelf(size);
      
      //Generate tail of namespace
      for (int i = 0; i< stack.size(); i++)
        code = code + ((GenerateSupport)stack.get(i)).generateTail((size -i));
      
      ExClipboard clipboard = TopManager.getDefault().getClipboard();
      StringSelection genCode = new StringSelection (code);
      clipboard.setContents(genCode,genCode);
  }
  
  public SystemAction[] createActions (){
    return new SystemAction[] {
      SystemAction.get (org.netbeans.modules.corba.browser.ir.actions.GenerateCodeAction.class),
      null,
      SystemAction.get (org.netbeans.modules.corba.browser.ir.actions.RefreshAction.class),
      null,
      SystemAction.get (org.openide.actions.PropertiesAction.class)
    };
  }
  
}

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

import javax.naming.CompositeName;
import javax.naming.NamingException;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.util.Enumeration;
import java.io.IOException;
import javax.naming.directory.DirContext;
import org.openide.TopManager;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node.Cookie;
import org.openide.nodes.NodeAdapter;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.Sheet;
import org.openide.util.actions.SystemAction;

/** Common base class for JndiNode and JndiLeafNode.
* The class provides copy (source generating)/delete actions.
*
* @author Ales Novak, Tomas Zezula
*/
abstract class JndiObjectNode extends AbstractNode implements Cookie, TemplateCreator {   

  
  /** Set to true only if this node is being destroyed.
  * If a node with removed set to true is removed then its parent calls refresh.
  */
  private boolean removed = false;
 
  /** My actions */
  private SystemAction[] jndiactions;

  /**
  * @param children
  * @param name
  */
  public JndiObjectNode(Children children, String name) {
    super (children);
    setName(name);
    getCookieSet().add(this);
    if (! children.equals(Children.LEAF)) {
      addNodeListener(new Refresher());
    }
  }
  
  /** @return actions */
  public final SystemAction[] getActions() {
    if (jndiactions == null) {
      jndiactions = createActions();
    }
    return jndiactions;
  }
  
  /** @creates actions */
  protected abstract SystemAction[] createActions();
  
  /** @return true */
  public final boolean canCopy() {
      return true;
  }

  /** @return @link isRoot */
  public final boolean canDestroy() {
    return true;
  }
  
  /** Creates property sheet for the node
   *  @return Sheet the property sheet
   */
  public Sheet createSheet () {
    Sheet sheet = Sheet.createDefault ();
    sheet.get (Sheet.PROPERTIES).put (
      new JndiProperty ("NAME",
                        String.class,
			JndiRootNode.getString("TXT_Name"),
			this.getName ()));
    sheet.get (Sheet.PROPERTIES).put (
      new JndiProperty ("OFFSET",
                        String.class,
			JndiRootNode.getString("TXT_Path"),
			this.getOffset().toString ()));
    sheet.get(Sheet.PROPERTIES).put (
      new JndiProperty ("CLASS",
                        String.class,
			JndiRootNode.getString("TXT_Class"),
			this.getClassName()));
    Enumeration keys =	( (JndiDirContext) this.getContext()).getEnvironment ().keys();		
    Enumeration elements =( (JndiDirContext) this.getContext ()).getEnvironment ().elements ();
    while (keys.hasMoreElements()){
	String key = (String)keys.nextElement();
	String value = (String)elements.nextElement();
	if (key.equals(JndiRootNode.NB_ROOT) || 
	    key.equals(JndiRootNode.NB_LABEL)) {
	  continue;
	}
	sheet.get (Sheet.PROPERTIES).put (
	  new JndiProperty (key,
	                    String.class,
			    key,
			    value));
    }
    setSheet (sheet);			  
    return sheet;
  }  

  /** Creates a java source code for obtaining 
   *  reference to this node
   *  @return String the java source code
   *  @exception NamingException when a JNDI fault happends.
   */
  public abstract String createTemplate() throws NamingException;
  
  /** Returns initial dir context
   *  @return DirContext initial context of this JNDI subtree 
   */
  public abstract DirContext getContext();
  
  /** Returns the offset of this Node in subtree of his context
   * @return CompositeName the offset in subtree
   */
  public abstract CompositeName getOffset();
  
  /** Returns class name of Jndi Object
   *  @return String class name
   */
  public abstract String getClassName();
  
  /** Inserts generated text into the clipboard */
  public final Transferable clipboardCopy() throws IOException {
    try {
      return new StringSelection(createTemplate());
    } catch (NamingException ne) {
      TopManager.getDefault().notifyException(ne);
      return null;
    }
  }
  
  /** Marks this node as destroyed. */
  public final void setRemoved() {
    removed = true;
  }
  
  /** Does nothing. */
  public void refresh() {
    throw new UnsupportedOperationException();
  }
  
  /** Listens for changes of its subnodes. Calls refresh then. */
  class Refresher extends NodeAdapter {
    public void childrenRemoved(NodeMemberEvent ev) {
      Node[] delta = ev.getDelta();
      
      for (int i = 0; i < delta.length; i++) {
        JndiObjectNode child = (JndiObjectNode) delta[i].getCookie(JndiObjectNode.class);
        if (child != null) {
          if (child.removed) {
            refresh();
          }
        }
      }
    }
  }
}

/*
* <<Log>>
*  2    Gandalf   1.1         7/9/99   Ales Novak      localization + code 
*       requirements followed
*  1    Gandalf   1.0         6/18/99  Ales Novak      
* $
*/

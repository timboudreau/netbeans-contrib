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

import java.awt.Dialog;
import java.util.StringTokenizer;
import java.util.Properties;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.ResourceBundle;
import java.util.Hashtable;
import java.net.URL;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.naming.NamingException;
import javax.naming.Context;
import javax.naming.directory.DirContext;

import org.openide.TopManager;
import org.openide.NotifyDescriptor;
import org.openide.actions.NewAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.ToolsAction;
import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node.Cookie;
import org.openide.nodes.Children;
import org.openide.nodes.DefaultHandle;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;
import com.netbeans.enterprise.modules.jndi.utils.Refreshd;

/** Top Level JNDI Node
 *
 * @author Ales Novak, Tomas Zezula
 */
public final class JndiRootNode extends AbstractNode{
  
  /** Name of property holding the name of context */
  public final static String NB_LABEL="NB_LABEL";
  /** Name of property holding the initial offset */
  public final static String NB_ROOT="HomePath";
  /** SystemActions*/
  protected SystemAction[] jndiactions = null;
  /** NewTypes*/
  protected NewType[] jndinewtypes = null;
  
  /** The holder of an instance of this class*/
  private static JndiRootNode instance = null;
  
  /** The asynchronous refresher*/
  Refreshd refresher;

  
  /** Constructor
   */
  public JndiRootNode() {
    super(new Children.Array());
    instance = this;
    this.refresher = new Refreshd();
    this.refresher.start();
    setName("JNDI");
    setIconBase(JndiIcons.ICON_BASE + JndiIcons.getIconName(JndiRootNode.NB_ROOT));
    JndiProvidersNode drivers = new JndiProvidersNode();
    this.getChildren().add(new Node[] {drivers});
    this.jndinewtypes= new NewType[]{ new JndiDataType(this,drivers)};
  }
  
  
  public static JndiRootNode getDefault () {
    return instance;
  }
  
  /** Returns name of the node
   *  @return Object the name of node
   */
  public Object getValue() {
    return getName();
  }

  /** Sets name of this node
   *  @param name name of the object
   */  
  public void setValue(Object name) {
    if (name instanceof String) {
      setName((String) name);
    }
  }
  
  /** Disable Destroying
   * @return false
   */
  public boolean canDestroy() {
    return false;
  }
  
  /** Disable Copy
   * @return false
   */
  public boolean canCopy() {
    return false;
  }
  
  /** Disable Cut
   * @return false
   */
  public boolean canCut() {
    return false;
  }
  
  /** Disable Rename
   *  @return false
   */
  public boolean canRename() {
    return false;
  }

  /** No default action
   *  @return null;
   */  
  public org.openide.util.actions.SystemAction getDefaultAction() {
    return null;
  }

  /** Returns actions for this node	
   *  @return array of SystemAction
   */
  public org.openide.util.actions.SystemAction[] getActions() {
    if (jndiactions == null) {
      jndiactions = this.createActions();
    }
    return jndiactions;
  }
  
  /** Creates actions for this node 
   *  @return array of SystemAction
   */
  public org.openide.util.actions.SystemAction[] createActions() {
    return new SystemAction[] {
      SystemAction.get(NewAction.class),
    };
  }
  
  /** Creates an JNDI Type
   *  @return array with JndiDataType
   */
  public NewType[] getNewTypes() {
    return this.jndinewtypes;
  }

  /** Creates handle
   *  @return Handle 
   */ 
  public Handle getHandle() {
    return DefaultHandle.createHandle(this);
  }
  

  /** This function adds an Context
   *  @param context adds context from String
   */	
  public void addContext(String context) throws NamingException {
    JndiNode[] nodes;	    
    nodes = new JndiNode[1]; 
    Properties env = parseStartContext(context);
    DirContext ctx = new JndiDirContext(env);
    nodes[0] = new JndiNode(ctx);
    getChildren().add(nodes);
  }
  
  /** This function adds an Context
   *  @param label name of node
   *  @param factory JndiFactory
   *  @param context starting Context
   *  @param authentification authentification to naming system
   *  @param principals principals for naming system
   *  @param credentials credentials for naming system
   *  @param prop vector type java.lang.String, additional properties in form key=value
   */
  public void addContext(String label, String factory, String context, String root, String authentification, String principal, String credentials, Vector prop) throws NamingException {
      Properties env = createContextProperties(label,factory,context,root, authentification, principal, credentials, prop);
      this.addContext (env);
  }
  
  /** This method adds new Context
   *  @param Hashtable properties of context
   **/
  void addContext (Hashtable properties) throws NamingException {
      JndiDirContext ctx = new JndiDirContext(properties);
      String root = (String)properties.get(NB_ROOT);
      Context rootedCtx;
      if (root != null){
        rootedCtx = (Context) ctx.lookup(root);
      }
      else{
        rootedCtx = ctx;
      }
      addContext(rootedCtx);
  }
  
  /** This methods adds new Context
   *  @param javax.naming.Context context
   *  @exception java.naming.NamingException
   */
  void addContext (Context rootedCtx) throws NamingException {
      JndiNode[] nodes = new JndiNode[1];
      nodes[0]= new JndiNode(rootedCtx);
      this.getChildren().add(nodes);
  }
  
  /** This method transforms parameters to properties for Context
   *  @param label name of node
   *  @param factory JndiFactory
   *  @param context starting Context
   *  @param authentification authentification to naming system
   *  @param principals principals for naming system
   *  @param credentials credentials for naming system
   *  @param prop vector type java.lang.String, additional properties in form key=value
   *  @exception JndiException
   */
   final Properties createContextProperties (String label, String factory, String context, String root, String authentification, String principal, String credentials, Vector prop) throws JndiException{
    if (label==null || factory==null || label.equals("") || factory.equals("")) throw new JndiException("Arguments missing");
      Properties env = new Properties();
      env.put(JndiRootNode.NB_LABEL,label);
      env.put(Context.INITIAL_CONTEXT_FACTORY,factory);
      env.put(JndiRootNode.NB_ROOT,"");
      if (context != null && context.length() > 0) {
        env.put(Context.PROVIDER_URL,context);
      }
      if (authentification != null && !authentification.equals("")) {
        env.put(Context.SECURITY_AUTHENTICATION, authentification);
      }
      if (principal != null && !principal.equals("")) {
        env.put(Context.SECURITY_PRINCIPAL, principal);
      }
      if (credentials != null && !credentials.equals("")) {
        env.put(Context.SECURITY_CREDENTIALS,credentials);
      }
      if (root != null && !root.equals("")) {
        env.put(NB_ROOT,root);
      }
      for (int i = 0; i < prop.size(); i++) {
        StringTokenizer tk = new StringTokenizer(((String)prop.elementAt(i)),"=");
        if (tk.countTokens() != 2) {
          continue;
        }
        String path = tk.nextToken();
        if (path.equals(NB_ROOT)) {
          env.put(NB_ROOT, tk.nextToken());
        } else {
          env.put(path, tk.nextToken());
        }
      }
      return env;
  }
  
  /**This function takes a string and converts it to set of properties
   * @return Properties set of properties if Ok, null on error
   */ 
  private Properties parseStartContext(String ident) throws NamingException {
    StringTokenizer tk = new StringTokenizer(ident,"|");
    Properties env = new Properties();
    
    try {
      env.put(JndiRootNode.NB_LABEL,tk.nextToken());
      env.put(Context.INITIAL_CONTEXT_FACTORY,tk.nextToken());
      env.put(Context.PROVIDER_URL,tk.nextToken());
    } catch(NoSuchElementException nee) {
      // The parameters above are obligatory
      throw new JndiException("Argument missing");
    }
    try {
      env.put(JndiRootNode.NB_ROOT,tk.nextToken());
    } catch(NoSuchElementException nee) {
      //If this parameter is missing set it to empty string.
      env.put(JndiRootNode.NB_ROOT,"");
    }
    try {
      env.put(Context.SECURITY_AUTHENTICATION, tk.nextToken());
      env.put(Context.SECURITY_PRINCIPAL,tk.nextToken());
      env.put(Context.SECURITY_CREDENTIALS,tk.nextToken());
    } catch(NoSuchElementException nee) {
      // no more elements
    }
    return env;
  }
  
  
  /** This method adds an disabled Context
   *  @param Hashtable properties of Context
   */
  public final void addDisabledContext ( Hashtable properties) {
    Node[] nodes = new Node[1];
    nodes[0]= new JndiDisabledNode(properties);
    this.getChildren().add(nodes);
  }
  
  
  /** Set up initial start contexts
  */
  public synchronized void initStartContexts(java.util.ArrayList nodes) {
    if (nodes!=null){
      for (int i = 0; i < nodes.size(); i++) {
        try{
          this.addContext((Hashtable)nodes.get(i));
        }catch(NamingException ne){
          this.addDisabledContext((Hashtable)nodes.get(i));
        }
      }
    }
  }
  

  /** Notifies about an exception that was raised in non Netbeans code. 
   */
  public static void notifyForeignException(Throwable t) {

    String msg;
    
    if ((t.getMessage() == null) ||
        t.getMessage().equals("")) {
          msg = t.getClass().getName();
    } else {
      msg = t.getClass().getName() + ": " + t.getMessage();
    }
    
    final NotifyDescriptor nd = new NotifyDescriptor.Exception(t, msg);
    Runnable run = new Runnable() {
      public void run() {
        TopManager.getDefault().notify(nd);
      }
    };
    java.awt.EventQueue.invokeLater(run);
  }

  /** Bundle with localizations. */
  private static ResourceBundle bundle;
  /** @return a localized string */
  public static String getLocalizedString(String s) {
    if (bundle == null) {
      bundle = NbBundle.getBundle(JndiRootNode.class);
    }
    return bundle.getString(s);
  }
  
  /** Shows an status
   *  @param String message
   */
  public static void showStatus (String message) {
     TopManager.getDefault().setStatusText(message);
  }
  
  /** shows an localized status
   *  @param String message
   */
  public static void showLocalizedStatus (String message) {
      showStatus(getLocalizedString(message));
  }
  
  
}

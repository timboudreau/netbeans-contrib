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

import java.util.Vector;
import java.io.IOException;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.naming.Context;
import org.openide.TopManager;
import org.openide.NotifyDescriptor;
import org.openide.util.actions.SystemAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.ToolsAction;
import org.openide.actions.DeleteAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node.Cookie;
import org.openide.nodes.Sheet;


/** This class represents a provider (factory)
 */
public class ProviderNode extends AbstractNode implements Cookie{

  /** Name used for JndiIcons*/
  public static final String DRIVER = "DRIVER";
  public static final String DISABLED_DRIVER="DEADDRIVER";
  
  /** key in Hashtable of providers, for which this node is inserted*/
  private String name;
  /** System actions of this node*/
  private SystemAction[] nodeActions;
  
  /** Creates new ProviderNode 
   *  @param String name key in Hashtable of providers
   */
  public ProviderNode (String name) {
    super (Children.LEAF);
    this.getCookieSet().add(this);
    this.name=name;
    String label;
    int index = name.lastIndexOf ('.');
    if (index < 0) label = name;
    else label = name.substring (index+1);
    this.setName (label);
    try{
      Class.forName(name);
      this.setIconBase (JndiIcons.ICON_BASE + JndiIcons.getIconName(ProviderNode.DRIVER));
    }catch (ClassNotFoundException cnf){
      this.setIconBase (JndiIcons.ICON_BASE + JndiIcons.getIconName(ProviderNode.DISABLED_DRIVER));
    }
  }
  
  
  /** Sets name of this node
   *  @param Object name
   */
  public void setValue (Object name) {
    if (name instanceof String) {
      this.setName( (String) name);
    }
  }
  
  /** Returns the name of this node
   *  @return Object name of node
   */
  public Object getValue () {
    return this.getName();
  }
  
  /** Returns true if the node can copy
   *  @param boolean can / can not copy
   */
  public boolean canCopy () {
    return false;
  }
  
  /** Returns true if the node can destroy
   *  @param boolean can / can not destroy
   */
  public boolean canDestroy () {
    return true;
  }
  
  /** Returns true if the node can cut
   *  @param boolean can / can not cut
   */
  public boolean canCut () {
    return false;
  }
  
  /** Returns true if the node can rename
   *  @param boolean can / can not rename
   */
  public boolean canRename () {
    return false;
  }
  
  
  /** Creates SystemActions
   *  @return SystemAction[] actions
   */
  public SystemAction[] createActions () {
    return new SystemAction[] {
      SystemAction.get(ProviderTestAction.class),
      SystemAction.get(ProviderConnectAction.class),
      null,
      SystemAction.get(DeleteAction.class),
      null,
      SystemAction.get(PropertiesAction.class),
    };
  }
  
  /** Returns actions of this node
   *  @return SystemAction[] actions
   */
  public SystemAction[] getActions () {
    if (this.nodeActions == null) {
      this.nodeActions = this.createActions();
    }
    return this.nodeActions;
  }
  
  
  /** Creates property sheet of this node
   *  @return Sheet created sheet
   */
  public Sheet createSheet () {
    Sheet sheet = Sheet.createDefault ();
    Sheet.Set set = sheet.get (Sheet.PROPERTIES);
    ProviderProperties properties = (ProviderProperties) ((JndiProvidersNode)this.getParentNode()).providers.get (this.name);
    if (properties != null) {
      Property property = new ProviderProperty (Context.INITIAL_CONTEXT_FACTORY, String.class,JndiRootNode.getLocalizedString("TXT_Factory"),JndiRootNode.getLocalizedString("TIP_Factory"),properties,false);
      set.put (property);
      property = new ProviderProperty (Context.PROVIDER_URL,String.class,JndiRootNode.getLocalizedString("TXT_InitialContext"),JndiRootNode.getLocalizedString("TIP_InitialContext"),properties,true);
      set.put (property);
      property = new ProviderProperty (JndiRootNode.NB_ROOT,String.class,JndiRootNode.getLocalizedString("TXT_Root"),JndiRootNode.getLocalizedString("TIP_Root"),properties,true);
      set.put (property);
      property = new ProviderProperty (Context.SECURITY_AUTHENTICATION,String.class,JndiRootNode.getLocalizedString("TXT_Auth"),JndiRootNode.getLocalizedString("TIP_Auth"),properties,true);
      set.put (property);
      property = new ProviderProperty (Context.SECURITY_PRINCIPAL,String.class,JndiRootNode.getLocalizedString("TXT_Principal"),JndiRootNode.getLocalizedString("TIP_Principal"),properties,true);
      set.put (property);
      property = new ProviderProperty (Context.SECURITY_CREDENTIALS,String.class,JndiRootNode.getLocalizedString("TXT_Credentials"),JndiRootNode.getLocalizedString("TIP_Credentials"),properties,true);
      set.put (property);
      property = new ProviderProperty (ProviderProperties.ADDITIONAL,String.class,JndiRootNode.getLocalizedString("TXT_OtherProps"), JndiRootNode.getLocalizedString("TIP_Additional"),properties,true);
      set.put (property);
    }
    return sheet;
  }
  
  /** Callback for DeleteAction, disposes this node
   *  @exception IOException
   */
  public void destroy () throws IOException {
    ((JndiProvidersNode) this.getParentNode ()).destroyProvider (this.name);
    super.destroy ();
  }
  
  /** Callback for ProviderTestAction, tests if the provider class is accessible
   */
  public void testProvider () {
    try{
      Class.forName(this.name);
      TopManager.getDefault().notify( new NotifyDescriptor.Message(JndiRootNode.getLocalizedString("MSG_CLASS_FOUND"), NotifyDescriptor.Message.INFORMATION_MESSAGE));
    }catch(ClassNotFoundException cnfe){
      TopManager.getDefault().notify(new NotifyDescriptor.Message(JndiRootNode.getLocalizedString("MSG_CLASS_NOT_FOUND"), NotifyDescriptor.Message.INFORMATION_MESSAGE));
    }
  }
  
  
  /** Opens the add context dialog with prefilled fields
   */
  public void connectUsing() {
    try{
      ((JndiDataType)JndiRootNode.getDefault().jndinewtypes[0]).create(name);
    }catch(java.io.IOException ioe){/** Should never happend*/}
  }
  
  
  /** Returns the customizer of this node
   *  @return Component customizer
   */
  public java.awt.Component getCustomizer(){
    Customizer p = new Customizer(((JndiProvidersNode)this.getParentNode()).providers.get (this.name));
    return p;
  }
  
  
  /** Returns true, this node support its own customizer
   *  @return boolean trye
   */
  public boolean hasCustomizer(){
    return true;
  }
  
  /** Fires the propertyChangeEvent to notify about chages
   *  @param String name, name of changed property 
   *  @param Object oldv, old value of property
   *  @param Object newv, new value of property
   */
  public void updateData(String name, Object oldv, Object newv){
    this.firePropertyChange(name,oldv,newv);
  }
  
  /** Customizer for this node*/
  class Customizer extends NewProviderPanel implements FocusListener, ListDataListener {
    private ProviderProperties target;
    
    Customizer (final java.lang.Object target){
      super();
      this.target = (ProviderProperties)target;
      this.factory.setText(this.target.getFactory());
      this.context.setText(this.target.getContext());
      this.context.addFocusListener(this);
      this.root.setText(this.target.getRoot());
      this.root.addFocusListener(this);
      this.authentification.setText(this.target.getAuthentification());
      this.authentification.addFocusListener(this);
      this.principal.setText(this.target.getPrincipal());
      this.principal.addFocusListener(this);
      this.credentials.setText(this.target.getCredentials());
      this.credentials.addFocusListener(this);
      this.properties.setData(this.target.getAdditional());
      this.properties.addListDataListener(this);
      this.factory.setEnabled(false);
    }
  
    /** Handles action fired when field is changed in customizer
     *  @param FocusEvent event
     */
    public void focusLost(FocusEvent event){
      String newv;
      String oldv;
      
      if (event.getSource()==this.context){
        newv = this.context.getText();
        oldv = this.target.getContext();
        if (!newv.equals(oldv)){
          this.target.setContext(this.context.getText());
          ProviderNode.this.updateData(Context.INITIAL_CONTEXT_FACTORY,oldv,newv);
        }
      }
      else if (event.getSource()==this.authentification){
        newv = this.authentification.getText();
        oldv = this.target.getAuthentification();
        if (!newv.equals(oldv)){
          this.target.setAuthentification(this.authentification.getText());
          ProviderNode.this.updateData(Context.SECURITY_AUTHENTICATION,oldv,newv);
        }
      }
      else if (event.getSource()==this.credentials){
        newv = this.credentials.getText();
        oldv = this.target.getCredentials();
        if (!newv.equals(oldv)){
          this.target.setCredentials(this.credentials.getText());
          ProviderNode.this.updateData(Context.SECURITY_CREDENTIALS,oldv,newv);
        }
      }
      else if (event.getSource()==this.principal){
        newv = this.principal.getText();
        oldv = this.target.getPrincipal();
        if (!newv.equals(oldv)){
          this.target.setPrincipal(this.principal.getText());
          ProviderNode.this.updateData(Context.SECURITY_PRINCIPAL,oldv,newv);
        }
      }
      else if (event.getSource()==this.root){
        newv = this.root.getText();
        oldv = this.target.getRoot();
        if (!newv.equals(oldv)){
          this.target.setRoot(this.root.getText());
          ProviderNode.this.updateData(JndiRootNode.NB_ROOT,oldv,newv);
        }
      }
    }
  
    public void focusGained(final java.awt.event.FocusEvent event) {
    }
  
  
    /** Handles action fired when additional properties are changed in customizer
     *  @param ListDataEvent event
     */
    public void intervalAdded(final javax.swing.event.ListDataEvent event) {
      if (event.getSource()==this.properties){
        Vector newv = properties.asVector();
        this.target.setAdditional(newv);
        ProviderNode.this.updateData(ProviderProperties.ADDITIONAL,newv,null);
      }
    }

    /** Handles action fired when additional properties are changed in customizer
     *  @param ListDataEvent event
     */
    public void intervalRemoved(final javax.swing.event.ListDataEvent event) {
      if (event.getSource()==this.properties){
        Vector newv = properties.asVector();
        this.target.setAdditional(newv);
        ProviderNode.this.updateData(ProviderProperties.ADDITIONAL,newv,null);
      }
    }

    /** Handles action fired when additional properties are changed in customizer
     *  @param ListDataEvent event
     */
    public void contentsChanged(final javax.swing.event.ListDataEvent event) {
      if (event.getSource()==this.properties){
        Vector newv = properties.asVector();
        this.target.setAdditional(newv);
        ProviderNode.this.updateData(ProviderProperties.ADDITIONAL,newv,null);
      }
    }
  }
  
  
}
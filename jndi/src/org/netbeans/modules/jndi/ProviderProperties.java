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
import java.util.StringTokenizer;
import java.util.StringTokenizer;
import java.io.*;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.naming.Context;
import org.openide.TopManager;
import org.openide.NotifyDescriptor;

/** 
 *
 * @author  Tomas Zezula
 * @version 
 */

/** This class represents an holder of properties of provider that fire PropertyChangeEvent on change*/
public class ProviderProperties extends Object {

  public static final String ADDITIONAL="Additional";
  /** Listeners for change of property */
  private PropertyChangeSupport listeners;
  private Vector additional;
  private String factory;
  private String context;
  private String credentials;
  private String authentification;
  private String principal;
  private String root;
  
  /** Creates new ProviderProperties */
  public ProviderProperties () {
    this.listeners = new PropertyChangeSupport(this);
    this.additional = new Vector();
    this.factory="";
    this.context="";
    this.credentials="";
    this.authentification="";
    this.principal="";
    this.root="";
  }
  
  
  /** Adds an property change listener
   *  @param PropertyChangeListener listener
   */
  public final void addPropertyChangeListener (PropertyChangeListener listener) {
    this.listeners.addPropertyChangeListener (listener);
  }
  
  /** Removes an property change listener
   *  @param PropertyChangeListener listener
   */
  public final void removePropertyChangeListener (PropertyChangeListener listener) {
    this.listeners.removePropertyChangeListener (listener);
  }
  
  /** Mutator for factory
   *  @param String factory name of factory
   */
  public final void setFactory ( String factory) {
    this.factory = factory;
    firePropertyChange ("factory",null,this.factory);
  }

  /** Mutator for Context
   *  @param String context name of context
   */
  public final void setContext ( String context) {
    this.context = context;
    firePropertyChange ("context",null,this.context);
  }
  
  
  /** Mutator for Authentification
   *  @param String authentification
   */
  public final void setAuthentification (String authentification) {
    this.authentification = authentification;
    firePropertyChange ("authentification",null,this.authentification);
  }
  
  /** Mutator for credentials
   *  @param String credentials
   */
  public final void setCredentials (String credentials) {
    this.credentials = credentials;
    firePropertyChange ("credentials",null,this.credentials);
  }
  
  /** Mutator for principal
   *  @param String principal
   */
  public final void setPrincipal (String principal) {
    this.principal = principal;
    firePropertyChange ("principal",null,this.principal);
  }
  
  /** Mutator for additional
   *  @param Vector additional
   */
  public final void setAdditional (Vector additional) {
    this.additional = additional;
    firePropertyChange ("additional",null,this.additional);
  }
  
  /** Mutator for root
   *  @param String root
   */
  public final void setRoot (String root) {
    this.root = root;
    firePropertyChange("root",null,this.root);
  }
  
  /** Accessor for factory
   *  @return String factory
   */
  public final String getFactory () {
    return this.factory;
  }
  
  /** Accessor for context
   *  @return String context
   */
  public final String getContext () {
    return this.context;
  }
  
  /** Accessor for authentfication
   *  @return String authentification
   */
  public final String getAuthentification () {
    return this.authentification;
  }
  
  /** Accessor for credentials
   *  @return String credentials
   */
  public final String getCredentials() {
    return this.credentials;
  }
  
  /** Accessor for principal
   *  @return String principal
   */
  public final String getPrincipal () {
    return this.principal;
  }
  
  /** Accessor for additional properties
   *  @return Vector additional properties
   */
  public final Vector getAdditional () {
    return this.additional;
  }
  
  /** Returns a copy of addtional properties
   *  @return Vector additional properties
   */
  public final Vector getAdditionalSave () {
    return (Vector) this.additional.clone();
  }
  
  /** Returns an root of context
   *  @return String root
   */
  public final String getRoot() {
    return this.root;
  }
  
  /** Returns an string representing property according to name of property
   *  @param String key name of property
   *  @return Object value
   */
  public Object getProperty (String key) {
    if (key.equals(Context.INITIAL_CONTEXT_FACTORY))
      return this.getFactory();
    else if (key.equals(Context.PROVIDER_URL))
      return this.getContext();
    else if (key.equals(Context.SECURITY_AUTHENTICATION))
      return this.getAuthentification();
    else if (key.equals(Context.SECURITY_PRINCIPAL))
      return this.getPrincipal();
    else if (key.equals(Context.SECURITY_CREDENTIALS))
      return this.getCredentials();
    else if (key.equals(JndiRootNode.NB_ROOT))
      return this.getRoot();
    else if (key.equals(ProviderProperties.ADDITIONAL)) {
      if (this.additional != null) {
        StringBuffer buf = new StringBuffer();
        for (int i=0; i<this.additional.size();i++){
          buf.append ((String)this.additional.elementAt (i));
          if (i != this.additional.size()-1) {
            buf.append ("; ");
          }
        }
        return buf.toString();
      }
    }
    return null;
  }
  
  
  /** Sets an property according to key
   *  @param String key
   *  @param Object value
   */
  public void setProperty (String key, Object value) {
    if (key.equals(Context.INITIAL_CONTEXT_FACTORY))
      this.setFactory((String) value);
    else if (key.equals(Context.PROVIDER_URL))
      this.setContext((String) value);
    else if (key.equals(Context.SECURITY_AUTHENTICATION))
      this.setAuthentification((String) value);
    else if (key.equals(Context.SECURITY_PRINCIPAL))
      this.setPrincipal((String) value);
    else if (key.equals(Context.SECURITY_CREDENTIALS))
      this.setCredentials((String) value);
    else if (key.equals(JndiRootNode.NB_ROOT))
      this.setRoot((String)value);
    else if (key.equals(ProviderProperties.ADDITIONAL)){
      parseAdditional((String) value);
      this.firePropertyChange(ADDITIONAL,null,this.additional);
    }
  }
  
  /** Loads property from stream 
   *  @param InputStream stream
   *  @exception IOException
   */
  public void load (InputStream rawIn) throws IOException {
    BufferedReader in = new BufferedReader ( new InputStreamReader (rawIn));
    String line;
    while ((line = in.readLine())!=null){
      line = line.trim();
      if (line.startsWith("#")) continue;
      StringTokenizer tk = new StringTokenizer(line,"=");
      if (tk.countTokens()!=2) continue;
      String key = tk.nextToken().trim();
      String value = tk.nextToken().trim();
      if (key.equals(Context.INITIAL_CONTEXT_FACTORY)){
        this.factory = value;
      }
      else if (key.equals(Context.PROVIDER_URL)){
        this.context = value;
      }
      else if (key.equals(Context.SECURITY_AUTHENTICATION)){
        this.authentification = value;
      }
      else if (key.equals(Context.SECURITY_PRINCIPAL)){
        this.principal = value;
      }
      else if (key.equals(Context.SECURITY_CREDENTIALS)){
        this.credentials = value;
      }
      else if (key.equals(JndiRootNode.NB_ROOT)){
        this.root = value;
      }
      else{
        this.additional.addElement(key+"="+value);
      }
    }
    in.close();
  }
  
  /** Stores property to stream 
   *  @param OutputStream stream
   *  @exception IOException
   */
  public void store (OutputStream rawOut, String comments) throws IOException {
    PrintWriter out = new PrintWriter (new OutputStreamWriter (rawOut));
    out.println("# "+comments);
    out.println(Context.INITIAL_CONTEXT_FACTORY+"="+this.factory);
    out.println(Context.PROVIDER_URL+"="+this.context);
    out.println(Context.SECURITY_AUTHENTICATION+"="+this.authentification);
    out.println(Context.SECURITY_PRINCIPAL+"="+this.principal);
    out.println(Context.SECURITY_CREDENTIALS+"="+this.credentials);
    out.println(JndiRootNode.NB_ROOT+"="+this.root);
    for (int i= 0; i< this.additional.size();i++) {
      out.println((String)this.additional.elementAt(i));
    }
    out.flush();
    out.close();
  }
  
  /** Fires PropertyChangeEvent
   *  @param String name of property
   *  @param Object old value
   *  @param Object new value
   */
  private void firePropertyChange (String name, Object oldValue, Object newValue){
    PropertyChangeEvent event = new PropertyChangeEvent(this,name,oldValue,newValue);
    this.listeners.firePropertyChange(event);
  }
  
  
  /** Parses stringified representation of additional properties to Vector
   *  @param String additional properties
   */
  private void parseAdditional (String properties) {
    StringTokenizer tk = new StringTokenizer (properties, ";");
    int count = tk.countTokens();
    this.additional.removeAllElements();
    for (int i = 0; i < count; i++) {
      String token = tk.nextToken();
      if ( (i == count -1) && (token.length() < 3)) break;
      StringTokenizer vktk = new StringTokenizer (token,"=");
      if (vktk.countTokens() != 2){
        TopManager.getDefault().notify ( new NotifyDescriptor.Message (JndiRootNode.getLocalizedString("EXC_ParseError")+" "+token+" "+JndiRootNode.getLocalizedString("EXC_ParseError2"),NotifyDescriptor.Message.ERROR_MESSAGE));
        continue;
      }
      String key = vktk.nextToken().trim();
      String value = vktk.nextToken().trim();
      this.additional.addElement (key+"="+value);
    }
  }
  
}
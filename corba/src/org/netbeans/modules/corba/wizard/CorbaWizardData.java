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

package org.netbeans.modules.corba.wizard;

import org.openide.loaders.DataFolder;
import org.netbeans.modules.corba.settings.CORBASupportSettings;


/** 
 *
 * @author  tzezula
 * @version 
 */
public class CorbaWizardData extends Object {

  public static final int CLIENT=8;   // Generate Client
  public static final int SERVER=4;   // Generate Server
  public static final int IMPL=2;     // Generate Implementation
  public static final int IDL=1;
  
  private CORBASupportSettings ccs;
  private int generate;
  private String impl;
  private String bindMethod;
  private Object idlSource;
  private DataFolder destinationPackage;
  private String name;

  /** Creates new CorbaWizardData */
  public CorbaWizardData() {
    this.ccs = (CORBASupportSettings) CORBASupportSettings.findObject (CORBASupportSettings.class, true);
  }
  
  
  public CORBASupportSettings getSettings() {
    return ccs;
  }
  
  public void setCORBAImpl (String impl) {
    this.impl = impl;
  }
  
  public void setBindMethod (String bindMethod) {
    this.bindMethod = bindMethod;
  }
  
  public void setGenerate (int mask) {
    this.generate = mask;
  }
  
  public void setSource (Object source) {
    this.idlSource = source;
  }
  
  public String getCORBAImpl() {
    return this.impl;
  }

  public String getName () {
    return this.name;
  }
  
  public String getBindMethod () {
    return this.bindMethod;
  }
  
  public int getGenerate(){
    return this.generate;
  }
  
  public Object getSource () {
    return this.idlSource;
  }
  
  public void setDestinationPackage (DataFolder object) {
    this.destinationPackage = object;
  }
  
  public DataFolder getDestinationPackage () {
    return this.destinationPackage;
  }

  public void setName (String name) {
    this.name = name;
  }
  
}
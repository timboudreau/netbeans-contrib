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
    private int generate;                       // What to generate
    private String impl;                        // Name of CORBA Implementation
    private String bindMethod;                  // Selected binding method
    private Object idlSource;                   // Source of IDL either File Name or IRNode or WizardNode
    private DataFolder destinationPackage;      // Destination folder
    private String name;                        // Name of IDL
    private boolean tie;                        // Should generate tie based impls
    private Object bindingDetails;              // Details about binding either ContextNode or File Name
    private String rootInterface;               // The root interface that should be bounded.
    private String defaultOrb;
    private String defaultClientBinding;
    private String defaultServerBinding;
    private boolean defaultTie;         
    

    /** Creates new CorbaWizardData */
    public CorbaWizardData() {
        this.ccs = (CORBASupportSettings) CORBASupportSettings.findObject (CORBASupportSettings.class, true);
    }
  
  
    public CORBASupportSettings getSettings() {
        return ccs;
    }
    
    public void setBindingDetails (Object bindingDetails) {
        this.bindingDetails = bindingDetails;
    }
  
    public void setCORBAImpl (String impl) {
        this.impl = impl;
    }
  
    public void setBindMethod (String bindMethod) {
        this.bindMethod = bindMethod;
    }
    
    public void setRootInterface (String rootInterface) {
        this.rootInterface = rootInterface;
    }
  
    public void setGenerate (int mask) {
        this.generate = mask;
    }
  
    public void setSource (Object source) {
        this.idlSource = source;
    }
    
    public Object getBindingDetails () {
        return this.bindingDetails;
    }
    
    public String getRootInterface() {
        return this.rootInterface;
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
    
    public boolean getTie () {
        return this.tie;
    }
    
    public void setTie (boolean tie) {
        this.tie = tie;
    }
    
    public void setDefaultOrbValue (String orb){
        this.defaultOrb = orb;
    }
    
    public void setDefaultServerBindingValue (String value) {
        this.defaultServerBinding = value;
    }
    
    public void setDefaultClientBindingValue (String value) {
        this.defaultClientBinding = value;
    }
    
    public void setDefaultTie (boolean tie) {
      this.defaultTie = tie;
    }
    
    public String getDefaultOrbValue () {
        return this.defaultOrb;
    }
    
    public String getDefaultServerBindingValue () {
        return this.defaultServerBinding;
    }
    
    public String getDefaultClientBindingValue () {
        return this.defaultClientBinding;
    }
    
    public boolean getDefaultTie () {
        return this.defaultTie;
    }
    
  
}

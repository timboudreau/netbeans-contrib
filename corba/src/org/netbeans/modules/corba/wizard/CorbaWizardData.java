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
import org.netbeans.modules.corba.IDLDataObject;
import org.netbeans.modules.corba.settings.CORBASupportSettings;
import org.netbeans.modules.corba.settings.ORBSettingsBundle;

import java.util.HashMap;


/** 
 *
 * @author  tzezula
 * @version 
 */
public class CorbaWizardData extends Object {

    public static final int CB_CLIENT=16;   // Generate Call-back Client
    public static final int CLIENT=8;   // Generate Client
    public static final int SERVER=4;   // Generate Server
    public static final int IMPL=2;     // Generate Implementation
  
    private CORBASupportSettings ccs;
    private int generate;                       // What to generate
    private String impl;                        // Name of CORBA Implementation
    private String bindMethod;                  // Selected binding method
    private IDLDataObject idlSource;               // The IDL for which the wizard is started
    private boolean tie;                        // Should generate tie based impls
    private Object bindingDetails;              // Details about binding
    private String rootInterface;               // The root interface that should be bounded.
    private String callBackInterface;           // The call-back interface that should be bounded.
    private String defaultOrb;
    private String defaultClientBinding;
    private String defaultServerBinding;
    private String defaultSkeletons;
    private HashMap defaultJavaTemplateCodePatchTable;
    

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
  
    public void setCallBackInterface (String callBackInterface) {
        this.callBackInterface = callBackInterface;
    }

    public void setGenerate (int mask) {
        this.generate = mask;
    }

    public Object getBindingDetails () {
        return this.bindingDetails;
    }

    public String getRootInterface() {
        return this.rootInterface;
    }
  
    public String getCallBackInterface() {
        return this.callBackInterface;
    }

    public String getCORBAImpl() {
        return this.impl;
    }

  
    public String getBindMethod () {
        return this.bindMethod;
    }
    
    public String getClientBindMethod() {
        return getClientBindMethod(this.bindMethod);
    }

    public static String getClientBindMethod (String serverBind) {
        if (serverBind.equals (ORBSettingsBundle.SERVER_NS))
            return ORBSettingsBundle.CLIENT_NS;
        if (serverBind.equals (ORBSettingsBundle.SERVER_IOR_TO_FILE))
            return ORBSettingsBundle.CLIENT_IOR_FROM_FILE;
        if (serverBind.equals (ORBSettingsBundle.SERVER_IOR_TO_OUTPUT))
            return ORBSettingsBundle.CLIENT_IOR_FROM_INPUT;
        if (serverBind.equals (ORBSettingsBundle.SERVER_BINDER))
            return ORBSettingsBundle.CLIENT_BINDER;
        return serverBind;
    }

    public int getGenerate(){
        return this.generate;
    }
  
    public void setIdlSource (IDLDataObject object) {
        this.idlSource = object;
    }
  
    public IDLDataObject getIdlSource () {
        return this.idlSource;
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

    public void setDefaultSkeletons (String value){
        this.defaultSkeletons = value;
    }
    
    public void setDefaultJavaTemplateCodePatchTable(HashMap value) {
        this.defaultJavaTemplateCodePatchTable = (HashMap)value.clone();
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

    public String getDefaultSkeletons (){
        return this.defaultSkeletons;
    }

    public HashMap getDefaultJavaTemplateCodePatchTable() {
        return this.defaultJavaTemplateCodePatchTable;
    }
    
    public DataFolder getDestinationPackage () {
        return this.idlSource.getFolder();
    }
    
    public String getName () {
        return this.idlSource.getName();
    }
     
}

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

package org.netbeans.modules.corba.settings;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * @author  Karel Gardas
 * @version 0.01, Jan 8 2001
 */
public class POASettings extends Object {

    public static final boolean DEBUG = false;

    public static final String LBR = "("; // NOI18N
    public static final String RBR = ")"; // NOI18N
    public static final String COMMA_SEPARATOR = ", "; // NOI18N
    public static final String DOT = "."; // NOI18N
    public static final String NEW = "new "; // NOI18N

    public static final char NAME_VALUE_SEPARATOR = ':';

    public static final String ALL_SERVANTS = "all-servants";
    public static final String DEFAULT_SERVANT = "default-servant"; // NOI18N
    public static final String SERVANT_MANAGER = "servant-manager"; // NOI18N
    public static final String SERVANT_WITH_SYSTEM_ID = "servant-with-system-id"; // NOI18N
    public static final String SERVANT_WITH_USER_ID = "servant-with-user-id"; // NOI18N

    private List _M_policies;

    private String _M_section_init_poas;
    
    private String _M_section_activate_poas;
    
    private String _M_section_init_servants;
    
    private String _M_root_poa_init;
    
    private String _M_create_poa;
    
    private String _M_activate_poa;
    
    private String _M_policies_declaration;
    
    private String _M_policies_header;
    
    private String _M_policies_footer;
    
    private String _M_policies_separator;
    
    private String _M_poa_manager;
    /*    
	  private int _M_none;
	  
	  private int _M_system_id;
	  
	  private int _M_user_id;
    */
    private String _M_activate_servant_with_system_id;
    
    private String _M_activate_servant_with_user_id;
    
    private String _M_create_servant_instance;
    
    private String _M_set_default_servant;
    
    private String _M_set_servant_manager;

    private String _M_set_poa_activator;
    
    private String _M_get_root_poa_pattern;
    
    private String _M_root_poa_pattern;
    
    private String _M_poa_pattern;
    
    private String _M_policies_pattern;
    
    private String _M_servant_pattern;
    
    private String _M_servant_instance_pattern;
    
    private String _M_default_servant_pattern;
    
    private String _M_servant_manager_pattern;

    private String _M_servant_class;

    private String _M_poa_activator_pattern;

    private String _M_policies_header_pattern;

    private String _M_policies_declaration_pattern;

    private String _M_policies_footer_pattern;

    //private String _M_set_poa_activator;

    private String _M_unrecognized_policy;

    private String _M_default_default_servant_var_name;
    private String _M_default_poa_activator_var_name;
    private String _M_default_poa_name;
    private String _M_default_servant_id;
    private String _M_default_poa_var_name;
    private String _M_default_servant_id_var_name;
    private String _M_default_servant_manager_var_name;
    private String _M_default_servant_var_name;


    public POASettings () {
    }

    public void addPolicy (POAPolicyDescriptor __policy) {
	if (DEBUG)
	    System.out.println ("addPolicy: " + __policy);
	if (_M_policies == null)
	    _M_policies = new LinkedList ();
	_M_policies.add (__policy);
    }

    public List getPolicies () {
	//System.out.println ("from: " + this);
	if (_M_policies == null)
	    _M_policies = new LinkedList ();
	return _M_policies;
    }

    public POAPolicyDescriptor getPolicyByName (String __name) {
	Iterator __iterator = this.getPolicies ().iterator ();
	while (__iterator.hasNext ()) {
	    POAPolicyDescriptor __policy = (POAPolicyDescriptor)__iterator.next ();
	    if (__policy.getName ().equals (__name))
		return __policy;
	}
	return null;
    }

    public String getSectionInitPOAs () {
        return _M_section_init_poas;
    }
    
    public void setSectionInitPOAs (String __section_init_poas) {
        _M_section_init_poas = __section_init_poas;
    }
    
    public String getSectionActivatePOAs () {
        return _M_section_activate_poas;
    }
    
    public void setSectionActivatePOAs (String __section_activate_poas) {
        _M_section_activate_poas = __section_activate_poas;
    }
    
    public String getSectionInitServants () {
        return _M_section_init_servants;
    }
    
    public void setSectionInitServants (String __section_init_servants) {
        _M_section_init_servants = __section_init_servants;
    }
    
    public String getRootPOAInit () {
        return _M_root_poa_init;
    }
    
    public void setRootPOAInit (String __root_poa_init) {
        _M_root_poa_init = __root_poa_init;
    }
    
    public String getCreatePOA () {
        return _M_create_poa;
    }
    
    public void setCreatePOA (String __create_poa) {
        _M_create_poa = __create_poa;
    }
    
    public String getActivatePOA () {
        return _M_activate_poa;
    }
    
    public void setActivatePOA (String __activate_poa) {
        _M_activate_poa = __activate_poa;
    }
    
    public String getPoliciesDeclaration () {
        return _M_policies_declaration;
    }
    
    public void setPoliciesDeclaration (String __policies_declaration) {
        _M_policies_declaration = __policies_declaration;
    }
    
    public String getPoliciesHeader () {
        return _M_policies_header;
    }
    
    public void setPoliciesHeader (String __policies_header) {
	if (DEBUG)
	    System.out.println  ("POASettings::setPoliciesHeader  ("
				 + __policies_header + ");");
        _M_policies_header = __policies_header;
    }
    
    public String getPoliciesFooter () {
        return _M_policies_footer;
    }
    
    public void setPoliciesFooter (String __policies_footer) {
        _M_policies_footer = __policies_footer;
    }
    
    public String getPoliciesSeparator () {
        return _M_policies_separator;
    }
    
    public void setPoliciesSeparator (String __policies_separator) {
        _M_policies_separator = __policies_separator;
    }
    
    public String getGetPOAManagerMethod () {
        return _M_poa_manager;
    }
    
    public void setGetPOAManagerMethod (String __poa_manager) {
        _M_poa_manager = __poa_manager;
    }
    /*
      public int getNone () {
      return _M_none;
      }
      
      public void setNone (int __none) {
      _M_none = __none;
      }
      
      public int getSystemId () {
      return _M_system_id;
      }
      
      public void setSystemId (int __system_id) {
      _M_system_id = __system_id;
      }
      
      public int getUserId () {
      return _M_user_id;
      }
      
      public void setUserId (int __user_id) {
      _M_user_id = __user_id;
      }
    */
    public String getActivateServantWithSystemId () {
        return _M_activate_servant_with_system_id;
    }
    
    public void setActivateServantWithSystemId (String __activate_servant_with_system_id) {
        _M_activate_servant_with_system_id = __activate_servant_with_system_id;
    }
    
    public String getActivateServantWithUserId () {
        return _M_activate_servant_with_user_id;
    }
    
    public void setActivateServantWithUserId (String __activate_servant_with_user_id) {
        _M_activate_servant_with_user_id = __activate_servant_with_user_id;
    }
    
    public String getCreateServantInstance () {
        return _M_create_servant_instance;
    }
    
    public void setCreateServantInstance (String __create_servant_instance) {
        _M_create_servant_instance = __create_servant_instance;
    }
    
    public String getSetDefaultServant () {
        return _M_set_default_servant;
    }
    
    public void setSetDefaultServant (String __set_default_servant) {
        _M_set_default_servant = __set_default_servant;
    }
    
    public String getSetServantManager () {
        return _M_set_servant_manager;
    }
    
    public void setSetServantManager (String __set_servant_manager) {
        _M_set_servant_manager = __set_servant_manager;
    }
    
    public String getGetRootPOAPattern () {
        return _M_get_root_poa_pattern;
    }
    
    public void setGetRootPOAPattern (String __value) {
        _M_get_root_poa_pattern = __value;
    }
    
    public String getRootPOAPattern () {
        return _M_root_poa_pattern;
    }
    
    public void setRootPOAPattern (String __root_poa_pattern) {
        _M_root_poa_pattern = __root_poa_pattern;
    }
    
    public String getPOAPattern () {
        return _M_poa_pattern;
    }
    
    public void setPOAPattern (String __poa_pattern) {
        _M_poa_pattern = __poa_pattern;
    }
    
    public String getPoliciesPattern () {
        return _M_policies_pattern;
    }
    
    public void setPoliciesPattern (String __policies_pattern) {
        _M_policies_pattern = __policies_pattern;
    }
    
    public String getServantPattern () {
        return _M_servant_pattern;
    }
    
    public void setServantPattern (String __servant_pattern) {
        _M_servant_pattern = __servant_pattern;
    }
    
    public String getServantInstancePattern () {
        return _M_servant_instance_pattern;
    }
    
    public void setServantInstancePattern (String __servant_instance_pattern) {
        _M_servant_instance_pattern = __servant_instance_pattern;
    }
    
    public String getDefaultServantPattern () {
        return _M_default_servant_pattern;
    }
    
    public void setDefaultServantPattern (String __default_servant_pattern) {
        _M_default_servant_pattern = __default_servant_pattern;
    }
    
    public String getServantManagerPattern () {
        return _M_servant_manager_pattern;
    }
    
    public void setServantManagerPattern (String __servant_manager_pattern) {
        _M_servant_manager_pattern = __servant_manager_pattern;
    }

    public void setServantClass (String __value) {
	_M_servant_class = __value;
    }

    public String getServantClass () {
	return _M_servant_class;
    }
    
    public void setPOAActivatorPattern (String __value) {
	_M_poa_activator_pattern = __value;
    }

    public String getPOAActivatorPattern () {
	return _M_poa_activator_pattern;
    }
    
    public void setPoliciesHeaderPattern (String __value) {
	_M_policies_header_pattern = __value;
    }

    public String getPoliciesHeaderPattern () {
	return _M_policies_header_pattern;
    }
    
    public void setPoliciesDeclarationPattern (String __value) {
	_M_policies_declaration_pattern = __value;
    }

    public String getPoliciesDeclarationPattern () {
	return _M_policies_declaration_pattern;
    }
    
    public void setPoliciesFooterPattern (String __value) {
	_M_policies_footer_pattern = __value;
    }

    public String getPoliciesFooterPattern () {
	return _M_policies_footer_pattern;
    }
    
    public void setSetPOAActivator (String __value) {
	_M_set_poa_activator = __value;
    }

    public String getSetPOAActivator () {
	return _M_set_poa_activator;
    }
    
    public void setUnrecognizedPolicy (String __value) {
	_M_unrecognized_policy = __value;
    }

    public String getUnrecognizedPolicy () {
	return _M_unrecognized_policy;
    }


    public void setDefaultDefaultServantVarName (String __value) {
	_M_default_default_servant_var_name = __value;
    }

    public String getDefaultDefaultServantVarName () {
	return _M_default_default_servant_var_name;
    }

    public void setDefaultPOAActivatorVarName (String __value) {
	_M_default_poa_activator_var_name = __value;
    }

    public String getDefaultPOAActivatorVarName () {
	return _M_default_poa_activator_var_name;
    }

    public void setDefaultPOAName (String __value) {
	_M_default_poa_name = __value;
    }

    public String getDefaultPOAName () {
	return _M_default_poa_name;
    }

    public void setDefaultServantId (String __value) {
	_M_default_servant_id = __value;
    }

    public String getDefaultServantId () {
	return _M_default_servant_id;
    }

    public void setDefaultPOAVarName (String __value) {
	_M_default_poa_var_name = __value;
    }

    public String getDefaultPOAVarName () {
	return _M_default_poa_var_name;
    }

    public void setDefaultServantIdVarName (String __value) {
	_M_default_servant_id_var_name = __value;
    }

    public String getDefaultServantIdVarName () {
	return _M_default_servant_id_var_name;
    }

    public void setDefaultServantVarName (String __value) {
	_M_default_servant_var_name = __value;
    }

    public String getDefaultServantVarName () {
	return _M_default_servant_var_name;
    }

    public void setDefaultServantManagerVarName (String __value) {
	_M_default_servant_manager_var_name = __value;
    }

    public String getDefaultServantManagerVarName () {
	return _M_default_servant_manager_var_name;
    }

    public String toString () {
	if (this == null)
	    return "POASettings: NULL";
	StringBuffer __buf = new StringBuffer ();
	__buf.append ("POASettings: ");
	__buf.append ("\n _M_policies: ");
	Iterator __iterator = this.getPolicies ().iterator ();
	while (__iterator.hasNext ()) {
	    __buf.append (((POAPolicyDescriptor)__iterator.next ()).toString ());
	}
	__buf.append ("\n _M_section_init_poas: ");
	__buf.append (_M_section_init_poas);
	__buf.append ("\n _M_section_activate_poas: ");
	__buf.append (_M_section_activate_poas);
	__buf.append ("\n_M_section_init_servants: ");
	__buf.append (_M_section_init_servants);
	__buf.append ("\n_M_root_poa_init: ");
	__buf.append (_M_root_poa_init);
	__buf.append ("\n_M_create_poa: ");
	__buf.append (_M_create_poa);
	__buf.append ("\n_M_activate_poa: ");
	__buf.append (_M_activate_poa);
	__buf.append ("\n_M_policies_declaration: ");
	__buf.append (_M_policies_declaration);
	__buf.append ("\n_M_policies_header: ");
	__buf.append (_M_policies_header);
	__buf.append ("\n_M_policies_footer: ");
	__buf.append (_M_policies_footer);
	__buf.append ("\n_M_policies_separator: ");
	__buf.append (_M_policies_separator);
	__buf.append ("\n_M_poa_manager: ");
	__buf.append (_M_poa_manager);
	__buf.append ("\n_M_activate_servant_with_system_id: ");
	__buf.append (_M_activate_servant_with_system_id);
	__buf.append ("\n_M_activate_servant_with_user_id: ");
	__buf.append (_M_activate_servant_with_user_id);
	__buf.append ("\n_M_create_servant_instance: ");
	__buf.append (_M_create_servant_instance);
	__buf.append ("\n_M_set_default_servant: ");
	__buf.append (_M_set_default_servant);
	__buf.append ("\n_M_set_servant_manager: ");
	__buf.append (_M_set_servant_manager);
	__buf.append ("\n_M_set_poa_activator: ");
	__buf.append (_M_set_poa_activator);
	__buf.append ("\n_M_get_root_poa_pattern: ");
	__buf.append (_M_get_root_poa_pattern);
	__buf.append ("\n_M_root_poa_pattern: ");
	__buf.append (_M_root_poa_pattern);
	__buf.append ("\n_M_poa_pattern: ");
	__buf.append (_M_poa_pattern);
	__buf.append ("\n_M_policies_pattern: ");
	__buf.append (_M_policies_pattern);
	__buf.append ("\n_M_servant_pattern: ");
	__buf.append (_M_servant_pattern);
	__buf.append ("\n_M_servant_instance_pattern: ");
	__buf.append (_M_servant_instance_pattern);
	__buf.append ("\n_M_default_servant_pattern: ");
	__buf.append (_M_default_servant_pattern);
	__buf.append ("\n_M_servant_manager_pattern: ");
	__buf.append (_M_servant_manager_pattern);
	__buf.append ("\n_M_servant_class: ");
	__buf.append (_M_servant_class);
	__buf.append ("\n_M_poa_activator_pattern: ");
	__buf.append (_M_poa_activator_pattern);
	__buf.append ("\n_M_policies_header_pattern: ");
	__buf.append (_M_policies_header_pattern);
	__buf.append ("\n_M_policies_declaration_pattern: ");
	__buf.append (_M_policies_declaration_pattern);
	__buf.append ("\n_M_policies_footer_pattern: ");
	__buf.append (_M_policies_footer_pattern);
	__buf.append ("\n_M_unrecognized_policy: ");
	__buf.append (_M_unrecognized_policy);
	__buf.append ("\n _M_default_default_servant_var_name: ");
	__buf.append (_M_default_default_servant_var_name);
	__buf.append ("\n _M_default_poa_activator_var_name: ");
	__buf.append (_M_default_poa_activator_var_name);
	__buf.append ("\n _M_default_poa_name: ");
	__buf.append (_M_default_poa_name);
	__buf.append ("\n _M_default_poa_var_name: ");
	__buf.append (_M_default_poa_var_name);
	__buf.append ("\n _M_default_servant_id: ");
	__buf.append (_M_default_servant_id);
	__buf.append ("\n _M_default_servant_id_var_name: ");
	__buf.append (_M_default_servant_id_var_name);
	__buf.append ("\n _M_default_servant_var_name: ");
	__buf.append (_M_default_servant_var_name);
	__buf.append ("\n _M_default_servant_manager_var_name: ");
	__buf.append (_M_default_servant_manager_var_name);
	/*
	  __buf.append (" : ");
	  __buf.append ();
	*/
	return __buf.toString ();
    }
	
}

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

import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import java.util.Properties;
import java.util.Vector;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Set;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

import org.omg.CORBA.ORB;

import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

import org.openide.options.SystemOption;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.execution.NbProcessDescriptor;

import org.netbeans.modules.java.settings.JavaSettings;

import org.openide.TopManager;
import org.openide.NotifyDescriptor;

import org.openide.filesystems.FileObject;

import org.openide.loaders.XMLDataObject;

import org.netbeans.modules.corba.IDLDataLoader;
import org.netbeans.modules.corba.utils.Assertion;

/*
 * @author Karel Gardas
 * @version 0.01, March 27 2000
 */

public class ORBSettings implements java.io.Serializable {

    private static final boolean DEBUG = false;
    //private static final boolean DEBUG = true;

    //private static final boolean DYNLOAD = true;
    private static final boolean DYNLOAD = false;

    //private static final boolean PRODUCTION = true;
    private static final boolean PRODUCTION = false;

    static final long serialVersionUID = 6055827315091215552L;
    /*
      private String[] _M_check_sections = {"CTL_NAME", "SERVER_IMPORT", "CLIENT_IMPORT", "SETTINGS_ORB_PROPERTIES", // NOI18N
      "ORB_SERVER_INIT", "ORB_CLIENT_INIT", "ORB_SERVER_RUN", // NOI18N
      "ORB_OBJECT_ACTIVATION", "DIR_PARAM", // NOI18N
      "PACKAGE_PARAM", "COMPILER", "PACKAGE_DELIMITER", // NOI18N
      "ERROR_EXPRESSION", "FILE_POSITION", "LINE_POSITION", // NOI18N
      "COLUMN_POSITION", "MESSAGE_POSITION", "TIE_PARAM", // NOI18N
      // added for implementation generator
      "IMPLBASE_IMPL_PREFIX", "IMPLBASE_IMPL_POSTFIX", // NOI18N
      "EXT_CLASS_PREFIX", "EXT_CLASS_POSTFIX", // NOI18N
      "TIE_IMPL_PREFIX", "TIE_IMPL_POSTFIX", // NOI18N
      "IMPL_INT_PREFIX", "IMPL_INT_POSTFIX"}; // NOI18N
      
      private String[] _M_cbindings = {"NS", "IOR_FROM_FILE", "IOR_FROM_INPUT", "BINDER"}; // NOI18N
      
      private String[] _M_sbindings = {"NS", "IOR_TO_FILE", "IOR_TO_OUTPUT", "BINDER"}; // NOI18N
    */

    //private transient Properties _M_properties;

    transient private POASettings _M_poa_settings;

    private String _M_skeletons = ORBSettingsBundle.INHER;

    private String _M_params;

    private ORBSettingsWrapper _M_server_binding;

    private ORBSettingsWrapper _M_client_binding;

    private boolean _M_hide_generated_files = true;

    private String _M_generation = ORBSettingsBundle.GEN_EXCEPTION;

    private String _M_synchro = ORBSettingsBundle.SYNCHRO_ON_UPDATE;

    // added for new impl generator

    private boolean _M_use_guarded_blocks = true;

    private String _M_delegation = ORBSettingsBundle.DELEGATION_STATIC;

    // finder

    private String _M_find_method = ORBSettingsBundle.PACKAGE_AND_SUB_PACKAGES;

    // advanced settings

    private NbProcessDescriptor _M_idl;
    private String _M_tie_param;
    private String _M_package_param;
    private String _M_dir_param;
    private String _M_package_delimiter;
    private String _M_error_expression;
    private String _M_file_position;
    private String _M_line_position;
    private String _M_column_position;
    private String _M_message_position;
    private String _M_implbase_impl_prefix;
    private String _M_implbase_impl_postfix;
    private String _M_ext_class_prefix;
    private String _M_ext_class_postfix;
    private String _M_tie_impl_prefix;
    private String _M_tie_impl_postfix;
    private String _M_impl_int_prefix;
    private String _M_impl_int_postfix;

    private boolean _M_is_tie;

    private String _M_value_impl_prefix;
    private String _M_value_impl_postfix;
    private String _M_valuefactory_impl_prefix;
    private String _M_valuefactory_impl_postfix;

    private String _M_tie_class_prefix;
    private String _M_tie_class_postfix;

    /*
      private String _M_server_import;
      private String _M_client_import;
      private String _M_orb_properties;
      private String _M_orb_init;
      private String _M_poa_init;
      private String _M_servants_init;
      private String _M_servant_activation;
      private String _M_poa_activation;
      private String _M_orb_server_run;
    */
    /*
      private String _M_cpp_directories = ".";
      private String _M_cpp_defined_symbols = "";
      private String _M_cpp_undefined_symbols = "";
    */

    private String _M_cpp_params;

    private transient List _M_server_bindings;
    private transient List _M_client_bindings;

    //private transient String _M_server_import;
    //private transient String _M_client_import;

    private transient Properties _M_java_template_table;
    private transient Properties _M_java_template_table_backup;
    private transient Properties _M_idl_template_table;

    private transient HashMap _M_java_patch_table;

    private String _M_table = "USER="+System.getProperty("user.name")+"\n";
    //      + "VERSION="+System.getProperty ("org.openide.major.version")+"\n";

    private String _M_orb_name; // transient for better I18N process
    private String _M_orb_tag; // tag which identifies ORB settings after deseerialization

    // It'll be set to true if this.getORBTag () == null in CORBASupportSettings::setBeans
    private transient boolean _M_boston_settings = false;

    private transient PropertyChangeSupport _M_property_change_support;

    private transient boolean _M_supported = false;

    private transient String _M_local_bundle;

    //private static Hashtable _M_all_properties;

    /** @return human presentable name */
    public String displayName() {
	if (DEBUG)
	    System.out.println ("ORBSettings::displayName () -> " + _M_orb_name); // NOI18N
	return _M_orb_name;
    }


    public String getName () {
	if (DEBUG)
	    System.out.println ("ORBSettings::getName () -> " + _M_orb_name); // NOI18N
	//Thread.dumpStack ();
	if (!this.isSupported ())
	    return _M_orb_name + " " + ORBSettingsBundle.CTL_UNSUPPORTED;
        return _M_orb_name;
    }


    public ORBSettings () {
        if (DEBUG)
	    System.out.println ("ORBSettings () ..."); // NOI18N
	//this.setJavaTemplateCodePatchTable (ORBSettings.getStandardCommentsPatchTable ());
	_M_property_change_support = new PropertyChangeSupport (this);
    }
    /*
    public ORBSettings (ORBSettings __parent) {
	_M_client_bindings = __parent._M_client_bindings;
	_M_server_bindings = __parent._M_server_bindings;
	_M_properties = __parent._M_properties;
	_M_skeletons = __parent._M_skeletons;
	_M_params = __parent._M_params;
	_M_server_binding = __parent._M_server_binding;
	_M_client_binding = __parent._M_client_binding;
	_M_hide_generated_files = __parent._M_hide_generated_files;
	_M_generation = __parent._M_generation;
	_M_synchro = __parent._M_synchro;
	_M_idl = __parent._M_idl;
	_M_tie_param = __parent._M_tie_param;
	_M_package_param = __parent._M_package_param;
	_M_dir_param = __parent._M_dir_param;
	_M_orb_class = __parent._M_orb_class;
	_M_orb_singleton = __parent._M_orb_singleton;
	_M_orb_import = __parent._M_orb_import;
	_M_package_delimiter = __parent._M_package_delimiter;
	_M_error_expression = __parent._M_error_expression;
	_M_file_position = __parent._M_file_position;
	_M_line_position = __parent._M_line_position;
	_M_column_position = __parent._M_column_position;
	_M_message_position = __parent._M_message_position;
	_M_implbase_impl_prefix = __parent._M_implbase_impl_prefix;
	_M_implbase_impl_postfix = __parent._M_implbase_impl_postfix;
	_M_ext_class_prefix = __parent._M_ext_class_prefix;
	_M_ext_class_postfix = __parent._M_ext_class_postfix;
	_M_tie_impl_prefix = __parent._M_tie_impl_prefix;
	_M_tie_impl_postfix = __parent._M_tie_impl_postfix;
	_M_impl_int_prefix = __parent._M_impl_int_prefix;
	_M_impl_int_postfix = __parent._M_impl_int_postfix;
	_M_is_tie = __parent._M_is_tie;
	_M_table = __parent._M_table;
	_M_orb_name = __parent._M_orb_name;
	//_M_property_change_support = __parent._M_property_change_support;
	_M_property_change_support = new PropertyChangeSupport (this);
	_M_supported = __parent._M_supported;
    }
    */

    //public void init () {
        //_M_client_bindings = new ArrayList ();
        //_M_server_bindings = new ArrayList ();
	/*
	  if (DYNLOAD || !PRODUCTION) {
	  loadImpl ();
	  //setOrb ("ORBacus for Java 3.1.x");
	  setClientBinding (CORBASupport.CLIENT_IOR_FROM_FILE);
	  setServerBinding (CORBASupport.SERVER_IOR_TO_FILE);
	  generation = CORBASupport.GEN_EXCEPTION;
	  }
	*/
	/*
	  if (PRODUCTION) {
	  loadImpl ();
	  //setOrb ("JDK 1.2 ORB");
	  setClientBinding (CORBASupport.CLIENT_NS);
	  setServerBinding (CORBASupport.SERVER_NS);
	  }
	*/
    //}

    /*
      public void readExternal (ObjectInput in) 
      throws java.io.IOException, 
      java.lang.ClassNotFoundException {
      deserealization = true;
      super.readExternal (in);
      deserealization = false;
      }
    */
    
    private void writeObject (java.io.ObjectOutputStream __out) throws IOException {
	if (DEBUG)
	    System.out.println ("ORBSettings::writeObject (" + __out + ")"); // NOI18N
	__out.defaultWriteObject ();
	//__out.writeObject (this.getBeans ());
    }
      
    
    private void readObject (java.io.ObjectInputStream __in)
	throws IOException, ClassNotFoundException {
	if (DEBUG)
	    System.out.println ("ORBSettings::readObject (" + __in + ")"); // NOI18N
	__in.defaultReadObject ();
	//__in.readObject ();
    }


    public String getClientBindingName () {

        String name = ""; // NOI18N
	if (DEBUG) {
	    System.out.println ("ORBSettings::getClientBindingName ()"); // NOI18N
	    if (_M_client_binding != null)
		System.out.println (_M_client_binding.getValue ());
	    else
		System.out.println ("is NULL"); // NOI18N
	}

        if (_M_client_binding != null) {
            if (_M_client_binding.getValue ().equals (ORBSettingsBundle.CLIENT_NS))
                name = "NS"; // NOI18N
            if (_M_client_binding.getValue ().equals (ORBSettingsBundle.CLIENT_IOR_FROM_FILE))
                name = "IOR_FROM_FILE"; // NOI18N
            if (_M_client_binding.getValue ().equals (ORBSettingsBundle.CLIENT_IOR_FROM_INPUT))
                name = "IOR_FROM_INPUT"; // NOI18N
            if (_M_client_binding.getValue ().equals (ORBSettingsBundle.CLIENT_BINDER))
                name = "BINDER"; // NOI18N
        }
	if (DEBUG)
	    System.out.println ("name: " + name); // NOI18N
        return name;
    }


    public String getServerBindingName () {

        String name = ""; // NOI18N
	if (DEBUG) {
	    System.out.println ("ORBSettings::getServerBindingName ()"); // NOI18N
	    if (_M_server_binding != null)
		System.out.println (_M_server_binding.getValue ());
	    else
		System.out.println ("is NULL"); // NOI18N
	}
        if (_M_server_binding != null) {
            if (_M_server_binding.getValue ().equals (ORBSettingsBundle.SERVER_NS))
                name = "NS"; // NOI18N
            if (_M_server_binding.getValue ().equals (ORBSettingsBundle.SERVER_IOR_TO_FILE))
                name = "IOR_TO_FILE"; // NOI18N
            if (_M_server_binding.getValue ().equals (ORBSettingsBundle.SERVER_IOR_TO_OUTPUT))
                name = "IOR_TO_OUTPUT"; // NOI18N
            if (_M_server_binding.getValue ().equals (ORBSettingsBundle.SERVER_BINDER))
                name = "BINDER"; // NOI18N
        }
	if (DEBUG)
	    System.out.println ("name: " + name); // NOI18N
        return name;
    }


    public String getSkeletons () {
        return _M_skeletons;
    }

    public void setSkeletons (String __value) {
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
        String __old = _M_skeletons;
	_M_skeletons = __value;
        firePropertyChange ("_M_skeletons", __old, _M_skeletons); // NOI18N
	this.cacheThrow ();
    }

    public void setParams (String __value) {
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
        String __old = _M_params;
	_M_params = __value;
        firePropertyChange ("_M_params", __old, _M_params); // NOI18N
	this.cacheThrow ();
    }

    public String getParams () {
        return _M_params;
    }

    public String param () {
        return _M_params;
    }


    public ORBSettingsWrapper getClientBinding () {
	if (_M_client_binding == null) {
	    List __bindings = this.getClientBindings ();
	    ORBBindingDescriptor __desc = (ORBBindingDescriptor)__bindings.get (0);
	    //System.out.println ("getClientBinding () -> " + __desc);
	    String __name = __desc.getName ();
	    _M_client_binding = new ORBSettingsWrapper (this, __name);
	}
        return _M_client_binding;
    }

    public void setClientBinding (ORBSettingsWrapper __value) {
	if (DEBUG)
	    System.out.println ("setClientBinding () <- " + __value);
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
        ORBSettingsWrapper __old = _M_client_binding;
	_M_client_binding = __value;
	this.setClientBindingTemplateTable ();
        if (this ==((CORBASupportSettings)org.openide.options.SystemOption.findObject (CORBASupportSettings.class, true)).getActiveSetting())
	    this.setJavaTemplateTable ();
        firePropertyChange ("_M_client_binding", __old, _M_client_binding); // NOI18N
	this.cacheThrow ();
    }

    
    public void setClientBindingTemplateTable () {
	//set well defined template tag CLIENT_BINDING_NAME
	ORBSettingsWrapper __binding = this.getClientBinding ();
	this.addJavaTemplateCode ("CLIENT_BINDING_NAME", __binding.getValue ());
	ORBBindingDescriptor __client_binding = null;
	Iterator __iterator = this.getClientBindings ().iterator ();
	while (__iterator.hasNext ()) {
	    ORBBindingDescriptor __tmp = (ORBBindingDescriptor)__iterator.next ();
	    if (__tmp.getName ().equals (__binding.getValue ())) {
		__client_binding = __tmp;
		break;
	    }
	}

	if (__client_binding != null) {
	    this.addJavaTemplateCode (__client_binding.getTemplateTag (), 
				      __client_binding.getCode ());
	    this.addJavaTemplateCode ("CLIENT_BINDING_IMPORT",
				      __client_binding.getImport ());
	    this.addJavaTemplateCode (__client_binding.getJavaTemplateCodeTable ());
	}

    }
    

    public void setClientBindingFromString (String __value) {
	if (DEBUG)
	    System.out.println ("ORBSettings::setClientBindingFromString (" + __value + ")"); // NOI18N
	this.setClientBinding (new ORBSettingsWrapper (this, __value));
    }

    public ORBSettingsWrapper getServerBinding () {
	if (_M_server_binding == null) {
	    List __bindings = this.getServerBindings ();
	    ORBBindingDescriptor __desc = (ORBBindingDescriptor)__bindings.get (0);
	    //System.err.println ("getServerBinding () -> " + __desc);
	    String __name = __desc.getName ();
	    _M_server_binding = new ORBSettingsWrapper (this, __name);
	    //_M_server_binding = new ORBSettingsWrapper 
	    //(this, this.getLocalizedString (__name));
	}
	return _M_server_binding;
    }

    public void setServerBinding (ORBSettingsWrapper __value) {
	if (DEBUG)
	    System.out.println ("ORBSettings::setServerBinding (" + __value + ")"); // NOI18N
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	ORBSettingsWrapper __old = _M_server_binding;
	_M_server_binding = __value;
	this.setServerBindingTemplateTable ();
        if (this == ((CORBASupportSettings)org.openide.options.SystemOption.findObject (CORBASupportSettings.class, true)).getActiveSetting())
	    this.setJavaTemplateTable ();
	firePropertyChange ("_M_server_binding", __old, _M_server_binding); // NOI18N
	this.cacheThrow ();
	//public void setServerBinding (String __value) {
	//_M_server_binding = __value;
        //String old = _server_binding;
        //_server_binding = s;
        //firePropertyChange ("_server_binding", old, _server_binding); // NOI18N
    }


    public void setServerBindingTemplateTable () {
	// set well defined template tag SERVER_BINDING_NAME
	ORBSettingsWrapper __binding = this.getServerBinding ();
	this.addJavaTemplateCode ("SERVER_BINDING_NAME", __binding.getValue ());
	ORBBindingDescriptor __server_binding = null;
	Iterator __iterator = this.getServerBindings ().iterator ();
	while (__iterator.hasNext ()) {
	    ORBBindingDescriptor __tmp = (ORBBindingDescriptor)__iterator.next ();
	    if (__tmp.getName ().equals (__binding.getValue ())) {
		__server_binding = __tmp;
		break;
	    }
	}

	if (__server_binding != null) {
	    this.addJavaTemplateCode (__server_binding.getTemplateTag (), 
				      __server_binding.getCode ());
	    this.addJavaTemplateCode ("SERVER_BINDING_IMPORT",
				      __server_binding.getImport ());
	    this.addJavaTemplateCode (__server_binding.getJavaTemplateCodeTable ());
	}

    }


    public void setServerBindingFromString (String __value) {
	if (DEBUG) {
	    System.out.println ("ORBSettings::setServerBindingFromString (" + __value + ")"); // NOI18N
	    if (__value == null) {
		//Thread.dumpStack ();
	    }
	}
	this.setServerBinding (new ORBSettingsWrapper (this, __value));
    }


    // advanced settings
    public NbProcessDescriptor getIdl () {
	//System.out.println ("getIdl () -> " + _M_idl);
        return _M_idl;
    }

    public String idl () {
	//System.out.println ("idl () -> " + _M_idl.getProcessName ());
        return _M_idl.getProcessName ();
    }

    public void setIdl (NbProcessDescriptor __value) {
	if (DEBUG)
	    System.out.println ("setIdl (" + __value.getProcessName () + " " 
				+ __value.getArguments () + ")");
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	NbProcessDescriptor __old = _M_idl;
	_M_idl = __value;
        //System.out.println ("setIdl :-)"); // NOI18N
        //System.out.println ("switch: " + idl.getClasspathSwitch ()); // NOI18N
        //int length = idl.getProcessArgs ().length;
        //String[] params = idl.getProcessArgs ();
        //for (int i=0; i<length; i++)
        //	 System.out.println ("param[" + i + "]: " + params[i]); // NOI18N

        //Thread.dumpStack ();
        firePropertyChange ("_M_idl", __old, _M_idl); // NOI18N
	this.cacheThrow ();
	//System.out.println ("setIdl - at the end: " + _M_idl);
    }

    public void setTieParam (String __value) {
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
        String __old = _M_tie_param;
	_M_tie_param = __value;
        //_tie_param = s;
        firePropertyChange ("_M_tie_param", __old, _M_tie_param); // NOI18N
	this.cacheThrow ();
    }

    public boolean isTie () {

        if (_M_skeletons.equals (ORBSettingsBundle.TIE)) {
            _M_is_tie = true;
            if (DEBUG)
                System.out.println ("is TIE"); // NOI18N
        }
        else {
            _M_is_tie = false;
            if (DEBUG)
                System.out.println ("isn't TIE"); // NOI18N
        }
	
        return _M_is_tie;
    }

    public String getTieParam () {
        return _M_tie_param;
    }

    public void setPackageParam (String __value) {
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
        String __old = _M_package_param;
	_M_package_param = __value;
        firePropertyChange ("_M_package_param", __old, _M_package_param); // NOI18N
	this.cacheThrow ();
    }

    public String getPackageParam () {
        return _M_package_param;
    }

    public String package_param () {
        return _M_package_param;
    }

    public void setDirParam (String __value) {
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
        String __old = _M_dir_param;
	_M_dir_param = __value;
        firePropertyChange ("_M_dir_param", __old, _M_dir_param); // NOI18N
	this.cacheThrow ();
    }

    public String getDirParam () {
        return _M_dir_param;
    }

    public String dir_param () {
        return _M_dir_param;
    }

    public String getPackageDelimiter () {
        return _M_package_delimiter;
    }

    public void setPackageDelimiter (String __value) {
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
        String __old = _M_package_delimiter;
	_M_package_delimiter = __value;
        firePropertyChange ("_M_package_delimiter", __old, _M_package_delimiter); // NOI18N
	this.cacheThrow ();
    }

    public char delim () {
        return _M_package_delimiter.charAt (0);
    }

    public String getErrorExpression () {
        return _M_error_expression;
    }

    public void setErrorExpression (String __value) {
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
        String __old = _M_error_expression;
	_M_error_expression = __value;
        firePropertyChange ("_M_error_expression", __old, _M_error_expression); // NOI18N
	this.cacheThrow ();
    }

    public String expression () {
        return _M_error_expression;
    }

    public String getFilePosition () {
        return _M_file_position;
    }

    public void setFilePosition (String __value) {
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
        String __old = _M_file_position;
	_M_file_position = __value;
        firePropertyChange ("_M_file_position", __old, _M_file_position); // NOI18N
	this.cacheThrow ();
    }

    public int file () {
        return new Integer(_M_file_position).intValue ();
    }

    public String getLinePosition () {
        return _M_line_position;
    }

    public void setLinePosition (String __value) {
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
        String __old = _M_line_position;
	_M_line_position = __value;
        firePropertyChange ("_M_line_position", __old, _M_line_position); // NOI18N
	this.cacheThrow ();
    }

    public int line () {
        return new Integer(_M_line_position).intValue ();
    }

    public String getColumnPosition () {
        return _M_column_position;
    }

    public void setColumnPosition (String __value) {
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
        String __old = _M_column_position;
	_M_column_position = __value;
        firePropertyChange ("_M_column_position", __old, _M_column_position); // NOI18N
	this.cacheThrow ();
    }

    public int column () {
        return new Integer(_M_column_position).intValue ();
    }

    public String getMessagePosition () {
        return _M_message_position;
    }

    public void setMessagePosition (String __value) {
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
        String __old = _M_message_position;
	_M_message_position = __value;
        firePropertyChange ("_M_message_position", __old, _M_message_position); // NOI18N
	this.cacheThrow ();
    }

    public int message () {
        return new Integer(_M_message_position).intValue ();
    }


    public void setImplBaseImplPrefix (String __value) {
	//System.out.println (this.getOrbName () + " -> setImplBaseImplPrefix '" + __value + "'");
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	String __old = _M_implbase_impl_prefix;
        _M_implbase_impl_prefix = __value;
	firePropertyChange ("_M_implbase_impl_prefix", __old, _M_implbase_impl_prefix); // NOI18N
	this.cacheThrow ();
    }

    public String getImplBaseImplPrefix () {
	//System.out.println (this.getOrbName () + " -> getImplBaseImplPrefix -> '" + _M_implbase_impl_prefix + "'");
        return _M_implbase_impl_prefix;
    }

    public void setImplBaseImplPostfix (String __value) {
	//System.out.println (this.getOrbName () + " -> setImplBaseImplPostfix '" + __value + "'");
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	String __old = _M_implbase_impl_postfix;
        _M_implbase_impl_postfix = __value;
	firePropertyChange ("_M_implbase_impl_postfix", __old, _M_implbase_impl_postfix); // NOI18N
	this.cacheThrow ();
    }

    public String getImplBaseImplPostfix () {
	//System.out.println (this.getOrbName () + " -> getImplBaseImplPostfix -> '" + _M_implbase_impl_postfix + "'");
        return _M_implbase_impl_postfix;
    }


    public void setExtClassPrefix (String __value) {
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	String __old = _M_ext_class_prefix;
        _M_ext_class_prefix = __value;
	firePropertyChange ("_M_ext_class_prefix", __old, _M_ext_class_prefix); // NOI18N
	this.cacheThrow ();
    }

    public String getExtClassPrefix () {
        return _M_ext_class_prefix;
    }

    public void setExtClassPostfix (String __value) {
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	String __old = _M_ext_class_postfix;
        _M_ext_class_postfix = __value;
	firePropertyChange ("_M_ext_class_postfix", __old, _M_ext_class_postfix); // NOI18N
	this.cacheThrow ();
    }

    public String getExtClassPostfix () {
        return _M_ext_class_postfix;
    }

    public void setTieImplPrefix (String __value) {
	//System.out.println (this.getOrbName () + " -> setTieImplPrefix '" + _M_tie_impl_prefix + "'");
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	String __old = _M_tie_impl_prefix;
        _M_tie_impl_prefix = __value;
	firePropertyChange ("_M_tie_impl_prefix", __old, _M_tie_impl_prefix); // NOI18N
	this.cacheThrow ();
    }

    public String getTieImplPrefix () {
	//System.out.println (this.getOrbName () + " -> getTieImplPrefix () -> '" + _M_tie_impl_prefix + "'");
        return _M_tie_impl_prefix;
    }

    public void setTieImplPostfix (String __value) {
	//System.out.println (this.getOrbName () + " -> setTieImplPostfix '" + _M_tie_impl_postfix + "'");
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	String __old = _M_tie_impl_postfix;
        _M_tie_impl_postfix = __value;
	firePropertyChange ("_M_tie_impl_postfix", __old, _M_tie_impl_postfix); // NOI18N
	this.cacheThrow ();
    }

    public String getTieImplPostfix () {
	//System.out.println (this.getOrbName () + " -> getTieImplPostfix () -> '" + _M_tie_impl_postfix + "'");
        return _M_tie_impl_postfix;
    }


    public void setImplIntPrefix (String __value) {
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	String __old = _M_impl_int_prefix;
        _M_impl_int_prefix = __value;
	firePropertyChange ("_M_impl_int_prefix", __old, _M_impl_int_prefix); // NOI18N
	this.cacheThrow ();
    }

    public String getImplIntPrefix () {
        return _M_impl_int_prefix;
    }

    public void setImplIntPostfix (String __value) {
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	String __old = _M_impl_int_postfix;
        _M_impl_int_postfix = __value;
	firePropertyChange ("_M_impl_int_postfix", __old, _M_impl_int_postfix); // NOI18N
	this.cacheThrow ();
    }

    public String getImplIntPostfix () {
        return _M_impl_int_postfix;
    }


    public void setValueImplPrefix (String __value) {
	//System.out.println ("setValueImplPrefix () <- " + __value);
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	String __old = _M_value_impl_prefix;
        _M_value_impl_prefix = __value;
	firePropertyChange ("_M_value_impl_prefix", __old, _M_value_impl_prefix); // NOI18N
	this.cacheThrow ();
    }

    public String getValueImplPrefix () {
        return _M_value_impl_prefix;
    }

    public void setValueImplPostfix (String __value) {
	//System.out.println ("setValueImplPostfix () <- " + __value);
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	String __old = _M_value_impl_postfix;
        _M_value_impl_postfix = __value;
	firePropertyChange ("_M_value_impl_postfix",
			    __old, _M_value_impl_postfix); // NOI18N
	this.cacheThrow ();
    }

    public String getValueImplPostfix () {
        return _M_value_impl_postfix;
    }


    public void setValueFactoryImplPrefix (String __value) {
	//System.out.println ("setValueFactoryImplPrefix () <- " + __value);
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	String __old = _M_valuefactory_impl_prefix;
        _M_valuefactory_impl_prefix = __value;
	firePropertyChange ("_M_valuefactory_impl_prefix",
			    __old, _M_valuefactory_impl_prefix); // NOI18N
	this.cacheThrow ();
    }

    public String getValueFactoryImplPrefix () {
        return _M_valuefactory_impl_prefix;
    }

    public void setValueFactoryImplPostfix (String __value) {
	//System.out.println ("setValueFactoryImplPostfix () <- " + __value);
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	String __old = _M_valuefactory_impl_postfix;
        _M_valuefactory_impl_postfix = __value;
	firePropertyChange ("_M_valuefactory_impl_postfix",
			    __old, _M_valuefactory_impl_postfix);// NOI18N
	this.cacheThrow ();
    }

    public String getValueFactoryImplPostfix () {
        return _M_valuefactory_impl_postfix;
    }


    public void setReplaceableStringsTable (String __value) {
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
        String __old = _M_table;
	_M_table = __value;
        firePropertyChange ("_M_table", __old, _M_table); // NOI18N
	this.cacheThrow ();
    }

    public String getReplaceableStringsTable () {
        return _M_table;
    }

    public Properties getReplaceableStringsProps () {
	//Properties _M_properties = new Properties ();
	Properties __properties = new Properties ();
	try {
	    __properties.load (new StringBufferInputStream (_M_table));
	    //props.load (new StringReader (_table));
	}
	catch (IOException e) {
	}
	__properties.putAll (this.getIDLTemplateTable ());
	return __properties;
    }

    public void fireChangeChoices () {
        //firePropertyChange ("_client_binding", null, null); // NOI18N
        //firePropertyChange ("_server_binding", null, null); // NOI18N
    }

    public boolean hideGeneratedFiles () {
	//System.out.println ("ORBSettings::hideGeneratedFiles () -> " + _M_hide_generated_files); // NOI18N
        return _M_hide_generated_files;
    }


    public void setHideGeneratedFiles (boolean __value) {
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	boolean __old = _M_hide_generated_files;
        _M_hide_generated_files = __value;	
	firePropertyChange ("_M_hide_generated_files", __old, _M_hide_generated_files); // NOI18N
	this.cacheThrow ();
	IDLDataLoader __loader = (IDLDataLoader)IDLDataLoader.findObject 
	    (IDLDataLoader.class, true);
	__loader.setHide (_M_hide_generated_files);       
    }


    public void setJavaTemplateTable () {

        int index = 0;

        String tmp_property;
	//boolean DEBUG=true;
        if (DEBUG)
	    System.out.println ("ORBSettings::setJavaTemplateTable"); // NOI18N

        JavaSettings js = (JavaSettings)JavaSettings.findObject (JavaSettings.class, true);
        Properties __properties = js.getReplaceableStringsProps ();
	
	// first repaire map
	this.repaireJavaTemplateCodeTable ();
	// set template tags for bindings
	this.setServerBindingTemplateTable ();
	this.setClientBindingTemplateTable ();
	//this.setJavaTemplateCodeTable (this.patchJavaCodeTable 
	//(this.getJavaTemplateCodeTable ()));
	Properties __orb_props = this.patchJavaCodeTable (this.getJavaTemplateCodeTable ());
	if (DEBUG)
	    System.out.println ("__orb_props: " + __orb_props);
	if (__properties == null)
	    __properties = new Properties ();
	//__properties.putAll (this.getJavaTemplateCodeTable ());
	__properties.putAll (__orb_props);
        
        ByteArrayOutputStream bs = new ByteArrayOutputStream ();
        try {
            __properties.store (bs, null);
        } catch (IOException e) {
            if (DEBUG)
                System.out.println (e);
        }
        if (DEBUG)
	    System.out.println ("properties: " + bs.toString ()); // NOI18N
        js.setReplaceableStringsTable (bs.toString ());

    }


    public Properties patchJavaCodeTable (Properties __props) {
	//boolean DEBUG=true;
	HashMap __patch_table = this.getJavaTemplateCodePatchTable ();
	Properties __result = new Properties ();
	__result.putAll (__props);
	Set __key_set = __result.keySet ();
	Iterator __key_iter = __key_set.iterator ();
	while (__key_iter.hasNext ()) {
	    String __props_key = (String)__key_iter.next ();
	    String __props_value = (String)__props.get (__props_key);
	    if (DEBUG) {
		System.out.println ("patching value: " + __props_value);
		System.out.println ("for key: " + __props_key);
	    }
	    Set __keys = __patch_table.keySet ();
	    Iterator __iterator = __keys.iterator ();
	    String __patched_value = __props_value;
	    while (__iterator.hasNext ()) {
		String __key = (String)__iterator.next ();
		String __value = (String)__patch_table.get (__key);
		if (DEBUG) {
		    System.out.println ("replace from: " + __key);
		    System.out.println ("replace to: " + __value);
		}
		String __before = __patched_value;
		__patched_value = Utilities.replaceString
		    (__patched_value, __key, __value);
		if (!__patched_value.equals (__before)) {
		    if (DEBUG)
			System.out.println ("patched value: " + __patched_value);
		    __result.put (__props_key, __patched_value);
		}
	    }
	}
	if (DEBUG)
	    System.out.println ("patchJavaCodeTable () -> " + __result);
	return __result;
    }

    /*
      public Properties patchJavaCodeTable (Properties __props) {
	// this method found value with guarded block with init poa section and
	// change it from name 'poa_section' to 'poa_section_<orb_tag>'
	// where 'poa_section' is user defined in xml orb config file
	Properties __return = new Properties ();
	if (this.getPOASettings () == null) {
	    // nothing to patch => escape
	    return __props;
	}
	Enumeration __keys = __props.keys ();
	while (__keys.hasMoreElements ()) {
	    Object __key = __keys.nextElement ();
	    Object __value = __props.get (__key);
	    String __tag = (String)__value;
	    String __new_tag = "";
	    int __index = __tag.indexOf (this.getPOASettings ().getSectionInitPOAs ());
	    if (__index > -1) {
		//System.out.println ("tag: " + __tag);
		int __end_index = __index
		    + this.getPOASettings ().getSectionInitPOAs ().length ();
		String __possible_orb_tag = "";
		try {
		    __possible_orb_tag = __tag.substring
			(__end_index + 1, __end_index + this.getORBTag ().length () + 1);
		} catch (StringIndexOutOfBoundsException __ex) {
		}
		//System.out.println ("possible orb tag: `" + __possible_orb_tag + "'");
		if (!__possible_orb_tag.equals (this.getORBTag ())) {
		    __new_tag = __tag.substring (0, __end_index);
		    __new_tag += "_" + this.getORBTag ();
		    __new_tag += __tag.substring (__end_index, __tag.length ());
		    //System.out.println ("new_tag: " + __new_tag);
		    __tag = __new_tag;
		}
	    }
	    int __last_index = __tag.lastIndexOf
		(this.getPOASettings ().getSectionInitPOAs ());
	    if (__last_index > -1) {
		//System.out.println ("ltag: " + __tag);
		int __end_index = __last_index
		    + this.getPOASettings ().getSectionInitPOAs ().length ();
		String __possible_orb_tag = "";
		try {
		    __possible_orb_tag = __tag.substring
			(__end_index + 1, __end_index + this.getORBTag ().length () + 1);
		} catch (StringIndexOutOfBoundsException __ex) {
		}
		//System.out.println ("lpossible orb tag: `" + __possible_orb_tag + "'");
		if (!__possible_orb_tag.equals (this.getORBTag ())) {
		    __new_tag = __tag.substring (0, __end_index);
		    __new_tag += "_" + this.getORBTag ();
		    __new_tag += __tag.substring (__end_index, __tag.length ());
		    //System.out.println ("lnew_tag: " + __new_tag);
		    __tag = __new_tag;
		}
	    }
	    __return.put (__key, __tag);
	}
	return __return;
    }
    */

    public void loadImpl (FileObject __fo)
	throws FileNotFoundException, IOException, SAXException {
	//boolean DEBUG=true;
	if (DEBUG)
	    System.out.println ("ORBSettings::loadImpl (" + __fo + ");");
	Parser __parser = XMLDataObject.createParser (true);
	ORBSettingsHandlerImpl __handler = new ORBSettingsHandlerImpl ();
	ORBSettingsRecognizer __recognizer 
	    = new ORBSettingsRecognizer (__handler, new ORBSettingsParsletImpl ());
	__handler.setSettings (this);
	__parser.setDocumentHandler (__recognizer);
	InputStream __in = __fo.getInputStream ();
	try {
	    __parser.parse (XMLDataObject.createInputSource (__fo.getURL ()));
	} catch (SAXException __ex) {
	    __ex.printStackTrace ();
	    throw __ex;
	} finally {
	    __in.close ();
	}
	this.backupJavaTemplateCodeTable ();
	if (DEBUG)
	    System.out.println ("loading ... OK");
    }


    public String getGeneration () {
        //System.out.println ("ORBSettings::getGeneration () -> " + _M_generation + " for " + _M_orb_name); // NOI18N
        return _M_generation;
    }


    public void setGeneration (String __value) {
        //System.out.println ("ORBSettings::setGeneration (" + __value + "); for " + _M_orb_name); // NOI18N
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	String __old = _M_generation;
        _M_generation = __value;
	firePropertyChange ("_M_generation", __old, _M_generation); // NOI18N
	this.cacheThrow ();
    }


    public String getSynchro () {
        //System.out.println ("getSynchro () -> " + _M_synchro); // NOI18N
        return _M_synchro;
    }


    public void setSynchro (String __value) {
        if (DEBUG)
	    System.out.println ("setSynchro (" + __value + ");"); // NOI18N
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	String __old = _M_synchro;
        _M_synchro = __value;
	firePropertyChange ("_M_synchro", __old, _M_synchro); // NOI18N
	this.cacheThrow ();
    }


    public String getDelegation () {
	//System.out.println (this.getName () + "::getDelegation () -> " + _M_delegation);
        return _M_delegation;
    }


    public void setDelegation (String __value) {
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	String __old = _M_delegation;
        _M_delegation = __value;
	if (DEBUG)
	    System.out.println ("set delegation on " + this.getName () + " to " + __value);
	firePropertyChange ("_M_delegation", __old, _M_delegation); // NOI18N
	this.cacheThrow ();
    }


    public boolean getUseGuardedBlocks () {
        return _M_use_guarded_blocks;
    }


    public void setUseGuardedBlocks (boolean __value) {
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	boolean __old = _M_use_guarded_blocks;
        _M_use_guarded_blocks = __value;
	firePropertyChange ("_M_use_guarded_blocks", __old, 
			    _M_use_guarded_blocks); // NOI18N
	this.cacheThrow ();
    }


    public void addPropertyChangeListener (PropertyChangeListener __listener) {
	if (DEBUG)
	    System.out.println ("ORBSettings::addPropertyChangeListener (" // NOI18N
				+ __listener + ")"); // NOI18N
	//_M_listeners.add (__listener);
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	_M_property_change_support.addPropertyChangeListener (__listener);
    }
    
    
    public void removePropertyChangeListener (PropertyChangeListener __listener) {
	if (DEBUG)
	    System.out.println ("ORBSettings::removePropertyChangeListener (" // NOI18N
				+ __listener + ")"); // NOI18N
	//_M_listeners.remove (__listener);
	_M_property_change_support.removePropertyChangeListener (__listener);
    }
    
    
    public void firePropertyChange (PropertyChangeEvent __event) {
	_M_property_change_support.firePropertyChange (__event);
    }
      
    
    public void firePropertyChange (String __property_name, boolean __old_value,
				    boolean __new_value) {
	_M_property_change_support.firePropertyChange (__property_name, __old_value,
						       __new_value);
    }
    
    
    public void firePropertyChange (String __property_name, int __old_value,
				    int __new_value) {
	_M_property_change_support.firePropertyChange (__property_name, __old_value,
						       __new_value);
    }
    
    
    public void firePropertyChange (String __property_name, java.lang.Object __old_value, 
				    java.lang.Object __new_value) {
	_M_property_change_support.firePropertyChange (__property_name, __old_value,
						       __new_value);
    } 


    public String getOrbName () {
	if (DEBUG)
	    System.out.println ("getOrbName () -> " + _M_orb_name);
	//Thread.dumpStack ();
	return _M_orb_name;
    }


    public void setOrbName (String __value) {
	if (DEBUG)
	    System.out.println ("ORBSettings::setOrbName (" + __value + ")"); // NOI18N
	_M_orb_name = __value;
	// set well defined template tag ORB_NAME
	this.addJavaTemplateCode ("ORB_NAME", _M_orb_name);
    }


    public List getServerBindings () {
	if (DEBUG)
	    System.out.println ("ORBSettings::getServerBindings () -> " + _M_server_bindings); // NOI18N
	if (_M_server_bindings == null)
	    _M_server_bindings = new ArrayList ();
	return _M_server_bindings;
    }


    public void setServerBindings (List __value) {
	_M_server_bindings = __value;
    }


    public List getClientBindings () {
	//if (DEBUG)
	//System.out.println ("ORBSettings::getClientBindings () -> " + _M_client_bindings); // NOI18N
	if (_M_client_bindings == null)
	    _M_client_bindings = new ArrayList ();
	return _M_client_bindings;
    }


    public void setClientBindings (List __value) {
	_M_client_bindings = __value;
    }


    public void setSupported (boolean __value) {
	_M_supported = __value;
    }


    public boolean isSupported () {
	return _M_supported;

    }


    public void cacheThrow () {
	CORBASupportSettings __css = (CORBASupportSettings)
	    CORBASupportSettings.findObject (CORBASupportSettings.class, true);
	__css.cacheThrow ();
    }


    public void setPOASettings (POASettings __value) {
	if (DEBUG)
	    System.out.println ("set POA Settings !!!!!!!!");
	_M_poa_settings = __value;
    }


    public void createPOASettings () {
	_M_poa_settings = new POASettings ();
    }


    public POASettings getPOASettings () {
	//System.out.println (this.getOrbName () + " ::getPOASettings () -> " + _M_poa_settings);
	//if (_M_poa_settings == null) {
	//_M_poa_settings = new POASettings ();
	//System.out.println ("after null: getPOASettings () -> " + _M_poa_settings);
	//}
	return _M_poa_settings;
    }


    public void setORBTag (String __value) {
	_M_orb_tag = __value;
    }


    public String getORBTag () {
	return _M_orb_tag;
    }


    public void addJavaTemplateCode (String __key, String __value) {
	//System.out.println ("add property for " + this.getName () + ": " + __key + ": " + __value);
	if (_M_java_template_table == null)
	    _M_java_template_table = new Properties ();
	_M_java_template_table.setProperty (__key, __value);
    }


    public void addJavaTemplateCode (Properties __value) {
	if (_M_java_template_table == null)
	    _M_java_template_table = new Properties ();
	_M_java_template_table.putAll (__value);
    }


    public void setJavaTemplateCodeTable (Properties __value) {
	if (DEBUG)
	    System.out.println ("setJavaTemplateCodeTable () <- " + __value);
	_M_java_template_table = __value;
    }


    public Properties getJavaTemplateCodeTable () {
	if (_M_java_template_table == null)
	    _M_java_template_table = new Properties ();
	return _M_java_template_table;
    }


    public void backupJavaTemplateCodeTable () {
	//System.out.println (this.getName () + "::backupJavaTemplateCodeTable () <- " + this.getJavaTemplateCodeTable ());
	_M_java_template_table_backup = new Properties ();
	_M_java_template_table_backup.putAll (this.getJavaTemplateCodeTable ());
	//_M_java_template_table_backup = new Properties (this.getJavaTemplateCodeTable ());
    }

    
    public void repaireJavaTemplateCodeTable () {
	//System.out.println (this.getName () + "::repaireJavaTemplateCodeTable () -> " + _M_java_template_table_backup);
	//Thread.dumpStack ();
	//if (_M_java_template_table_backup == null)
	//_M_java_template_table_backup = new Properties ();
	Properties __tmp = new Properties ();
	__tmp.putAll (_M_java_template_table_backup);
	this.setJavaTemplateCodeTable (__tmp);
    }


    public void addIDLTemplateCode (String __key, String __value) {
	if (_M_idl_template_table == null)
	    _M_idl_template_table = new Properties ();
	_M_idl_template_table.setProperty (__key, __value);
    }


    public void addIDLTemplateCode (Properties __value) {
	if (_M_idl_template_table == null)
	    _M_idl_template_table = new Properties ();
	_M_idl_template_table.putAll (__value);
    }


    public void setIDLTemplateTable (Properties __value) {
	_M_idl_template_table = __value;
    }


    public Properties getIDLTemplateTable () {
	if (_M_idl_template_table == null)
	    _M_idl_template_table = new Properties ();
	return _M_idl_template_table;
    }


    public void addServerBinding (ORBBindingDescriptor __value) {
	//System.out.println ("addServerBinding () <- " + __value);
	List __bindings = this.getServerBindings ();
	__bindings.add (__value);
    }


    public void addClientBinding (ORBBindingDescriptor __value) {
	//System.out.println ("addClientBinding () <- " + __value);
	List __bindings = this.getClientBindings ();
	__bindings.add (__value);
    }


    public void setLocalBundle (String __value) {
	if (DEBUG)
	    System.out.println ("setLocalBundle () <- " + __value);
	_M_local_bundle = __value;
    }


    public String getLocalBundle () {
	if (DEBUG)
	    System.out.println ("getLocalBundle () -> " + _M_local_bundle);
	return _M_local_bundle;
    }


    public String getLocalizedString (String __value) {
	String __lstring = __value;
	// we have to replace all splaces with underscores
	if (DEBUG)
	    System.out.println ("getLocalizedString (" + __value + ")");
	String __query = __value.replace (' ', '_');
	//System.out.println ("query string: " + __query);
	if (this.getLocalBundle () != null && (!this.getLocalBundle ().equals (""))) {
	    if (DEBUG)
		System.out.println ("looking for bundle of class: "+this.getLocalBundle ());
	    ResourceBundle __bundle = null;
	    try {
		Class __class = TopManager.getDefault ().systemClassLoader ().loadClass 
		    (this.getLocalBundle ());
		__bundle = NbBundle.getBundle (__class);
		if (DEBUG)
		    System.out.println ("found bundle: " + __bundle);
		//System.out.print ("with these keys: ");
		//Enumeration __enum = __bundle.getKeys ();
		//while (__enum.hasMoreElements ())
		//System.out.println (__enum.nextElement ());
		String __resource = __bundle.getString (__query);
		//System.out.println ("found resource: " + __resource);
		__lstring = __resource;
	    } catch (Exception __ex) {
		//__ex.printStackTrace ();
		//System.out.println ("__ex: " + __ex);
		//} catch (Throwable __th) {
		
		//System.out.println ("__th: " + __th);
		//}
	    }
	}
	if (DEBUG)
	    System.out.println ("getLocalizedString () -> " + __lstring);
	return __lstring;
    }


     public static String xml2java (String __value) {
        StringBuffer __buf = new StringBuffer ();
        int __index = __value.indexOf ("\\n"); // NOI18N
        if (__index > -1) {
            //System.out.println("index: " + __index);
            String __begin = __value.substring (0, __index);
            //System.err.println ("__begin: " + __begin);
            __buf.append (__begin);
            __buf.append ("\n");
            __buf.append (ORBSettings.xml2java (__value.substring (__index + 2, __value.length ())));
        }
        else {
            __buf.append (__value);
        }
        return __buf.toString ();
    }

    public String getInitPOASection () {
	if (this.getPOASettings () != null) {
	    StringBuffer __buf = new StringBuffer ();
	    __buf.append (this.getPOASettings ().getSectionInitPOAs ());
	    __buf.append ("_"); // NOI18N
	    __buf.append (this.getORBTag ());
	    return __buf.toString ();
	}
	return null;
    }


    public void addJavaTemplateCodePatchPair (String __key, String __value) {
	if (_M_java_patch_table == null)
	    this.setJavaTemplateCodePatchTable (new HashMap ());
	Assertion.assert (__key != null && __value != null);
	//System.out.println (__key + " -> " + __value);
	_M_java_patch_table.put (__key, __value);
	//this.setJavaTemplateTable ();
    }


    public HashMap getJavaTemplateCodePatchTable () {
	if (DEBUG)
	    System.out.print ("getJavaTemplateCodePatchTable () -> ");
	if (_M_java_patch_table == null) {
	    if (DEBUG)
		System.out.print ("null, then: "); 
	    //this.setJavaTemplateCodePatchTable
	    //(ORBSettings.getStandardCommentsPatchTable ());
	    this.setJavaTemplateCodePatchTable (new HashMap ());
	}
	if (DEBUG)
	    System.out.println (_M_java_patch_table);
	return _M_java_patch_table;
    }


    public void setJavaTemplateCodePatchTable (HashMap __map) {
	if (DEBUG)
	    Thread.dumpStack ();
	//System.out.println (this.getName () + ": setJavaTemplateCodePatchTable: " + __map);
	Assertion.assert (__map != null);
	_M_java_patch_table = __map;

	String __poa_block_begin = "//GEN-BEGIN:poa_section";
	String __poa_block_end = "//GEN-END:poa_section";
	String __poa_one_line_block = "//GEN-LINE:poa_section";

	_M_java_patch_table.put (__poa_block_begin,
				 __poa_block_begin + "_" + this.getORBTag ());
	_M_java_patch_table.put (__poa_block_end,
				 __poa_block_end + "_" + this.getORBTag ());
	_M_java_patch_table.put (__poa_one_line_block,
				 __poa_one_line_block + "_" + this.getORBTag ());
    }
    
//      public static HashMap getStandardCommentsPatchTable () {
//  	HashMap __map = new HashMap ();
//  	__map.put ("/*FFJ_CORBA_TODO_OBJECT_NARROWING*/", "// place narrowing of your object here\n// something like:  <class> <name> = <class>Helper.narrow (obj);\n//                  if (<name> == null) throw new RuntimeException ();");
//  	__map.put ("/*FFJ_CORBA_TODO_OA_VAR_NAME*/", "poa");
//  	__map.put ("/*FFJ_CORBA_TODO_SERVANT_VAR_NAME*/", "/* place here name of servant variable */");
//  	__map.put ("/*FFJ_CORBA_TODO_FILE_NAME*/", "\"<file_name>\"");
//  	__map.put ("/*FFJ_CORBA_TODO_CLIENT_CODE_FROM_NS*/", "// paste code retrieved using the Copy Client Code action (on an object node in the Naming Service Browser) here");
//  	__map.put ("/*FFJ_CORBA_TODO_SERVER_CODE_FROM_NS*/", "// paste code retrieved using the Copy Server Code action (on a context node in the Naming Service Browser) here");
//  	return __map;
//      }
    
    /*
    public void setCPPDirectories (String __dirs) {
	_M_cpp_directories = __dirs;
    }


    public String getCPPDirectories () {
	return _M_cpp_directories;
    }


    public void setCPPDefinedSymbols (String __syms) {
	_M_cpp_defined_symbols = __syms;
    }


    public String getCPPDefinedSymbols () {
	return _M_cpp_defined_symbols;
    }


    public void setCPPUndefinedSymbols (String __unsyms) {
	_M_cpp_undefined_symbols = __unsyms;
    }


    public String getCPPUndefinedSymbols () {
	return _M_cpp_undefined_symbols;
    }
    */

    public void setCPPParams (String __params) {
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	String __old = _M_cpp_params;
	_M_cpp_params = __params;
	firePropertyChange ("_M_cpp_params", __old, _M_cpp_params); // NOI18N
	//this.cacheThrow ();
    }


    public String getCPPParams () {
	return _M_cpp_params;
    }

    public String getTieClassPrefix () {
	return _M_tie_class_prefix;
    }

    public void setTieClassPrefix (String __value) {
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	String __old = _M_tie_class_prefix;
	_M_tie_class_prefix = __value;
	firePropertyChange ("_M_tie_class_prefix", __old, _M_tie_class_prefix); // NOI18N
    }

    public String getTieClassPostfix () {
	return _M_tie_class_postfix;
    }

    public void setTieClassPostfix (String __value) {
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	String __old = _M_tie_class_postfix;
	_M_tie_class_postfix = __value;
	firePropertyChange ("_M_tie_class_postfix", __old, _M_tie_class_postfix); // NOI18N
    }


    public void setFindMethod (String __value) {
	if (_M_property_change_support == null)
	    _M_property_change_support = new PropertyChangeSupport (this);
	String __old = _M_find_method;
        _M_find_method = __value;
	firePropertyChange ("_M_find_method", __old, _M_find_method); // NOI18N
	//this.cacheThrow ();
    }    

    public String getFindMethod () {
	return _M_find_method;
    }

    public boolean isBostonSettings () {
	return _M_boston_settings;
    }

    public void setBostonSettings (boolean __value) {
	_M_boston_settings = __value;
    }

    public String toString () {
	StringBuffer __buf = new StringBuffer ();
	__buf.append (this.getName ());
	__buf.append ("\n");
	__buf.append (this.getORBTag ());
	__buf.append ("\nserver binding: ");
	__buf.append (this.getServerBinding ());
	__buf.append (" : ");
	__buf.append (System.identityHashCode (this.getServerBinding ()));
	__buf.append ("\nclient binding: ");
	__buf.append (this.getClientBinding ());
	__buf.append (" : ");
	__buf.append (System.identityHashCode (this.getClientBinding ()));
	
	return __buf.toString ();
    }

}





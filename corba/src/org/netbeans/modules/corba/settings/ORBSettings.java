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

import java.io.*;
import org.omg.CORBA.*;

import org.openide.options.SystemOption;
//import org.openide.options.ContextSystemOption;
import org.openide.util.NbBundle;
import org.openide.execution.NbProcessDescriptor;

import org.netbeans.modules.java.settings.JavaSettings;

import java.util.Properties;
import java.util.Vector;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;


import org.openide.TopManager;
import org.openide.filesystems.FileObject;
import org.netbeans.modules.corba.*;

public class ORBSettings implements java.io.Serializable, java.lang.Comparable {

    private static final boolean DEBUG = false;
    //private static final boolean DEBUG = true;

    //private static final boolean DYNLOAD = true;
    private static final boolean DYNLOAD = false;

    //private static final boolean PRODUCTION = true;
    private static final boolean PRODUCTION = false;

    static final long serialVersionUID = 6055827315091215552L;

    private String[] _M_check_sections = {"CTL_NAME", "IMPORT", "SETTINGS_ORB_PROPERTIES",
					  "ORB_SERVER_INIT", "ORB_CLIENT_INIT", "ORB_SERVER_RUN",
					  "ORB_OBJECT_ACTIVATION", "DIR_PARAM",
					  "PACKAGE_PARAM", "COMPILER", "PACKAGE_DELIMITER",
					  "ERROR_EXPRESSION", "FILE_POSITION", "LINE_POSITION",
					  "COLUMN_POSITION", "MESSAGE_POSITION", "TIE_PARAM",
					  // added for implementation generator
					  "IMPLBASE_IMPL_PREFIX", "IMPLBASE_IMPL_POSTFIX",
					  "EXT_CLASS_PREFIX", "EXT_CLASS_POSTFIX",
					  "TIE_IMPL_PREFIX", "TIE_IMPL_POSTFIX",
					  "IMPL_INT_PREFIX", "IMPL_INT_POSTFIX"};

    private String[] _M_cbindings = {"NS", "IOR_FROM_FILE", "IOR_FROM_INPUT", "BINDER"};

    private String[] _M_sbindings = {"NS", "IOR_TO_FILE", "IOR_TO_OUTPUT", "BINDER"};



    public Vector _M_client_bindings;

    public Vector _M_server_bindings;

    public Properties _M_properties;

    public String _M_skeletons = CORBASupport.INHER;

    public String _M_params;

    public ORBSettingsWrapper _M_server_binding;

    public ORBSettingsWrapper _M_client_binding;

    public boolean _M_hide_generated_files = true;

    public String _M_generation = CORBASupport.GEN_NOTHING;

    public String _M_synchro = CORBASupport.SYNCHRO_ON_UPDATE;

    // advanced settings

    public NbProcessDescriptor _M_idl;
    public String _M_tie_param;
    public String _M_package_param;
    public String _M_dir_param;
    public String _M_orb_class;
    public String _M_orb_singleton;
    public String _M_orb_import;
    public String _M_package_delimiter;
    public String _M_error_expression;
    public String _M_file_position;
    public String _M_line_position;
    public String _M_column_position;
    public String _M_message_position;
    public String _M_impl_prefix;
    public String _M_impl_postfix;
    public String _M_ext_class_prefix;
    public String _M_ext_class_postfix;
    public String _M_tie_prefix;
    public String _M_tie_postfix;
    public String _M_impl_int_prefix;
    public String _M_impl_int_postfix;

    private boolean _M_is_tie;

    public String _M_table = "USER="+System.getProperty("user.name")+"\n";
    //      + "VERSION="+System.getProperty ("org.openide.major.version")+"\n";

    private String _M_orb_name;

    private PropertyChangeSupport _M_property_change_support;

    /** @return human presentable name */
    public String displayName() {
	if (DEBUG)
	    System.out.println ("ORBSettings::displayName () -> " + _M_orb_name);
        return _M_orb_name;
    }


    public String getName() {
	//if (DEBUG)
	//    System.out.println ("ORBSettings::getName () -> " + _M_orb_name);
        return _M_orb_name;
    }


    public int compareTo (java.lang.Object __object) {
	try {
	    return this.getName ().compareTo (((ORBSettings)__object).getName ());
	} catch (Exception __ex) {
	    __ex.printStackTrace ();
	}
	return -1;
    }


    public ORBSettings () {
        if (DEBUG)
            System.out.println ("ORBSettings () ...");

        _M_client_bindings = new Vector (5);
        _M_server_bindings = new Vector (5);
	_M_property_change_support = new PropertyChangeSupport (this);
    }

    public void init () {
        _M_client_bindings = new Vector (5);
        _M_server_bindings = new Vector (5);
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
    }

    /*
      public void readExternal (ObjectInput in) 
      throws java.io.IOException, 
      java.lang.ClassNotFoundException {
      deserealization = true;
      super.readExternal (in);
      deserealization = false;
      }
    */
    /*
      public void setAdvancedClientBinding (String binding) {
      
      if (DEBUG)
      System.out.println ("client binding: " + binding);
      //if (DEBUG)
      // System.out.println ("ctl_client_binding: " + getCtlClientBindingName ());
      setJavaTemplateTable ();
      }
    */
    /*
      public void setAdvancedServerBinding (String binding) {
      
      if (DEBUG)
      System.out.println ("server binding: " + binding);
      //if (DEBUG)
      //	 System.out.println ("ctl_server_binding: " + getCtlServerBindingName ());
      setJavaTemplateTable ();
      }
    */
    /*
      public Vector getNames () {
      
      if (names == null) {
      // lazy initialization
      names = new Vector (5);
      loadImpl ();
      }
      
      return names;
      }
    */
    /*
      public String getOrb () {
      //loadImpl ();
      return orb;
      }
    */
    /*
      public void setOrb (String s) {
      String old = "";
      orb = s;
      try {
      //if (!deserealization)
      firePropertyChange ("orb", old, orb);
      } catch (Exception e) {
      e.printStackTrace ();
      }
      //setAdvancedOptions ();
      //loadImpl (); -- it's for template debuging only !!!
      }
    */
    /*

      public String getOrbName () {
        
      String name = "";

      if (orb.equals (CORBASupport.ORBIX))
      name = "ORBIX";
      if (orb.equals (CORBASupport.VISIBROKER))
      name = "VISIBROKER";
      if (orb.equals (CORBASupport.ORBACUS))
      name = "ORBACUS";
      if (orb.equals (CORBASupport.JAVAORB))
      name = "JAVAORB";

      return name;
      }
     
      public String getCtlOrbName () {
      return "CTL_" + getOrbName () + "_";
      }

    */

    
    private void writeObject (java.io.ObjectOutputStream __out) throws IOException {
	if (DEBUG)
	    System.out.println ("ORBSettings::writeObject (" + __out + ")");
	__out.defaultWriteObject ();
	//__out.writeObject (this.getBeans ());
    }
      
    
    private void readObject (java.io.ObjectInputStream __in) throws IOException, ClassNotFoundException {
	if (DEBUG)
	    System.out.println ("ORBSettings::readObject (" + __in + ")");
	__in.defaultReadObject ();
	//__in.readObject ();
      }


    public String getClientBindingName () {

        String name = "";
	if (DEBUG) {
	    System.out.println ("ORBSettings::getClientBindingName ()");
	    if (_M_client_binding != null)
		System.out.println (_M_client_binding.getValue ());
	    else
		System.out.println ("is NULL");		
	}

        if (_M_client_binding != null) {
            if (_M_client_binding.getValue ().equals (CORBASupport.CLIENT_NS))
                name = "NS";
            if (_M_client_binding.getValue ().equals (CORBASupport.CLIENT_IOR_FROM_FILE))
                name = "IOR_FROM_FILE";
            if (_M_client_binding.getValue ().equals (CORBASupport.CLIENT_IOR_FROM_INPUT))
                name = "IOR_FROM_INPUT";
            if (_M_client_binding.getValue ().equals (CORBASupport.CLIENT_BINDER))
                name = "BINDER";
        }
	if (DEBUG)
	    System.out.println ("name: " + name);
        return name;
    }

    /*
      public String getCtlClientBindingName () {
      return getCtlOrbName () + "CLIENT_" + getClientBindingName ();
      }
    */

    public String getServerBindingName () {

        String name = "";
	if (DEBUG) {
	    System.out.println ("ORBSettings::getServerBindingName ()");
	    if (_M_server_binding != null)
		System.out.println (_M_server_binding.getValue ());
	    else
		System.out.println ("is NULL");		
	}
        if (_M_server_binding != null) {
            if (_M_server_binding.getValue ().equals (CORBASupport.SERVER_NS))
                name = "NS";
            if (_M_server_binding.getValue ().equals (CORBASupport.SERVER_IOR_TO_FILE))
                name = "IOR_TO_FILE";
            if (_M_server_binding.getValue ().equals (CORBASupport.SERVER_IOR_TO_OUTPUT))
                name = "IOR_TO_OUTPUT";
            if (_M_server_binding.getValue ().equals (CORBASupport.SERVER_BINDER))
                name = "BINDER";
        }
	if (DEBUG)
	    System.out.println ("name: " + name);
        return name;
    }

    /*
      public String getCtlServerBindingName () {
      return getCtlOrbName () + "SERVER_" + getServerBindingName ();
      }
    */

    public String getSkeletons () {
        return _M_skeletons;
    }

    public void setSkeletons (String __value) {
        String __old = _M_skeletons;
	_M_skeletons = __value;
        firePropertyChange ("_M_skeletons", __old, _M_skeletons);
    }

    public void setParams (String __value) {
        String __old = _M_params;
	_M_params = __value;
        firePropertyChange ("_M_params", __old, _M_params);
    }

    public String getParams () {
        return _M_params;
    }

    public String param () {
        return _M_params;
    }


    public ORBSettingsWrapper getClientBinding () {
        return _M_client_binding;
    }

    public void setClientBinding (ORBSettingsWrapper __value) {
        ORBSettingsWrapper __old = _M_client_binding;
	_M_client_binding = __value;
	this.setJavaTemplateTable ();
        firePropertyChange ("_M_client_binding", __old, _M_client_binding);
    }

    public void setClientBindingFromString (String __value) {
	if (DEBUG)
	    System.out.println ("ORBSettings::setClientBindingFromString (" + __value + ")");
	this.setClientBinding (new ORBSettingsWrapper (this, __value));
    }
    /*
      public String getServerBinding () {
      return _M_server_binding;
      }
    */
    public ORBSettingsWrapper getServerBinding () {
	return _M_server_binding;
    }

    public void setServerBinding (ORBSettingsWrapper __value) {
	if (DEBUG)
	    System.out.println ("ORBSettings::setServerBinding (" + __value + ")");
	ORBSettingsWrapper __old = _M_server_binding;
	_M_server_binding = __value;
	this.setJavaTemplateTable ();
	firePropertyChange ("_M_server_binding", __old, _M_server_binding);
	//public void setServerBinding (String __value) {
	//_M_server_binding = __value;
        //String old = _server_binding;
        //_server_binding = s;
        //firePropertyChange ("_server_binding", old, _server_binding);
    }

    public void setServerBindingFromString (String __value) {
	if (DEBUG) {
	    System.out.println ("ORBSettings::setServerBindingFromString (" + __value + ")");
	    if (__value == null)
		Thread.dumpStack ();
	}
	this.setServerBinding (new ORBSettingsWrapper (this, __value));
    }


    // advanced settings
    public NbProcessDescriptor getIdl () {
        return _M_idl;
    }

    public String idl () {
        return _M_idl.getProcessName ();
    }

    public void setIdl (NbProcessDescriptor __value) {
	NbProcessDescriptor __old = _M_idl;
	_M_idl = __value;
        //System.out.println ("setIdl :-)");
        //System.out.println ("switch: " + idl.getClasspathSwitch ());
        //int length = idl.getProcessArgs ().length;
        //String[] params = idl.getProcessArgs ();
        //for (int i=0; i<length; i++)
        //	 System.out.println ("param[" + i + "]: " + params[i]);

        //Thread.dumpStack ();
        firePropertyChange ("_M_idl", __old, _M_idl);
    }

    public void setTieParam (String __value) {
        String __old = _M_tie_param;
	_M_tie_param = __value;
        //_tie_param = s;
        firePropertyChange ("_M_tie_param", __old, _M_tie_param);
    }

    public boolean isTie () {

        if (_M_skeletons.equals (CORBASupport.TIE)) {
            _M_is_tie = true;
            if (DEBUG)
                System.out.println ("is TIE");
        }
        else {
            _M_is_tie = false;
            if (DEBUG)
                System.out.println ("isn't TIE");
        }
	
        return _M_is_tie;
    }

    public String getTieParam () {
        return _M_tie_param;
    }

    //public static String tie_param () {
    //   return _tie_param;
    //}

    public void setPackageParam (String __value) {
        String __old = _M_package_param;
	_M_package_param = __value;
        firePropertyChange ("_M_package_param", __old, _M_package_param);
    }

    public String getPackageParam () {
        return _M_package_param;
    }

    public String package_param () {
        return _M_package_param;
    }

    public void setDirParam (String __value) {
        String __old = _M_dir_param;
	_M_dir_param = __value;
        firePropertyChange ("_M_dir_param", __old, _M_dir_param);
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
        String __old = _M_package_delimiter;
	_M_package_delimiter = __value;
        firePropertyChange ("_M_package_delimiter", __old, _M_package_delimiter);
    }

    public char delim () {
        return _M_package_delimiter.charAt (0);
    }

    public String getErrorExpression () {
        return _M_error_expression;
    }

    public void setErrorExpression (String __value) {
        String __old = _M_error_expression;
	_M_error_expression = __value;
        firePropertyChange ("_M_error_expression", __old, _M_error_expression);
    }

    public String expression () {
        return _M_error_expression;
    }

    public String getFilePosition () {
        return _M_file_position;
    }

    public void setFilePosition (String __value) {
        String __old = _M_file_position;
	_M_file_position = __value;
        firePropertyChange ("_M_file_position", __old, _M_file_position);
    }

    public int file () {
        return new Integer(_M_file_position).intValue ();
    }

    public String getLinePosition () {
        return _M_line_position;
    }

    public void setLinePosition (String __value) {
        String __old = _M_line_position;
	_M_line_position = __value;
        firePropertyChange ("_M_line_position", __old, _M_line_position);
    }

    public int line () {
        return new Integer(_M_line_position).intValue ();
    }

    public String getColumnPosition () {
        return _M_column_position;
    }

    public void setColumnPosition (String __value) {
        String __old = _M_column_position;
	_M_column_position = __value;
        firePropertyChange ("_M_column_position", __old, _M_column_position);
    }

    public int column () {
        return new Integer(_M_column_position).intValue ();
    }

    public String getMessagePosition () {
        return _M_message_position;
    }

    public void setMessagePosition (String __value) {
        String __old = _M_message_position;
	_M_message_position = __value;
        firePropertyChange ("_M_message_position", __old, _M_message_position);
    }

    public int message () {
        return new Integer(_M_message_position).intValue ();
    }


    public void setImplBasePrefix (String __value) {
	String __old = _M_impl_prefix;
        _M_impl_prefix = __value;
	firePropertyChange ("_M_impl_prefix", __old, _M_impl_prefix);
    }

    public String getImplBasePrefix () {
        return _M_impl_prefix;
    }

    public void setImplBasePostfix (String __value) {
	String __old = _M_impl_postfix;
        _M_impl_postfix = __value;
	firePropertyChange ("_M_impl_postfix", __old, _M_impl_postfix);
    }

    public String getImplBasePostfix () {
        return _M_impl_postfix;
    }


    public void setExtClassPrefix (String __value) {
	String __old = _M_ext_class_prefix;
        _M_ext_class_prefix = __value;
	firePropertyChange ("_M_ext_class_prefix", __old, _M_ext_class_prefix);
    }

    public String getExtClassPrefix () {
        return _M_ext_class_prefix;
    }

    public void setExtClassPostfix (String __value) {
	String __old = _M_ext_class_postfix;
        _M_ext_class_postfix = __value;
	firePropertyChange ("_M_ext_class_postfix", __old, _M_ext_class_postfix);
    }

    public String getExtClassPostfix () {
        return _M_ext_class_postfix;
    }

    public void setTiePrefix (String __value) {
	String __old = _M_tie_prefix;
        _M_tie_prefix = __value;
	firePropertyChange ("_M_tie_prefix", __old, _M_tie_prefix);
    }

    public String getTiePrefix () {
        return _M_tie_prefix;
    }

    public void setTiePostfix (String __value) {
	String __old = _M_tie_postfix;
        _M_tie_postfix = __value;
	firePropertyChange ("_M_tie_postfix", __old, _M_tie_postfix);
    }

    public String getTiePostfix () {
        return _M_tie_postfix;
    }


    public void setImplIntPrefix (String __value) {
	String __old = _M_impl_int_prefix;
        _M_impl_int_prefix = __value;
	firePropertyChange ("_M_impl_int_prefix", __old, _M_impl_int_prefix);
    }

    public String getImplIntPrefix () {
        return _M_impl_int_prefix;
    }

    public void setImplIntPostfix (String __value) {
	String __old = _M_impl_int_postfix;
        _M_impl_int_postfix = __value;
	firePropertyChange ("_M_impl_int_postfix", __old, _M_impl_int_postfix);
    }

    public String getImplIntPostfix () {
        return _M_impl_int_postfix;
    }


    public void setReplaceableStringsTable (String __value) {
        String __old = _M_table;
	_M_table = __value;
        firePropertyChange ("_M_table", __old, _M_table);
    }

    public String getRaplaceableStringsTable () {
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
	__properties.putAll (_M_properties);
        return __properties;
    }

    public void fireChangeChoices () {
        //firePropertyChange ("_client_binding", null, null);
        //firePropertyChange ("_server_binding", null, null);
    }

    /*
      public String[] getClientBindingsChoices () {
      
      String[] choices;
      choices = new String[1];
      choices[0] = new String ("");
      int index = -1;
      int length = -1;
      
      for (int i=0; i<getNames ().size (); i++) {
      if (DEBUG)
      System.out.println ("names[" + i + "] = " + getNames ().elementAt (i));
      if (getNames ().elementAt (i).equals (orb)) {
      index = i;
      break;
      }
      }
      if (index >= 0) {
      length = ((Vector)clientBindings.elementAt (index)).size ();
      choices = new String[length];
      if (DEBUG) {
      System.out.println ("index: " + index);
      System.out.println ("orb: " + orb);
      System.out.println ("length: " + length);
      System.out.println ("bindings: " + (Vector)clientBindings.elementAt (index));
      }
      }
      if (index >= 0)
      for (int i=0; i<length; i++) {
      choices[i] = (String)((Vector)clientBindings.elementAt (index)).elementAt (i);
      if (DEBUG)
      System.out.println ("choice: " + choices[i]);
      }
      
      return choices;
      }
    */

    /*
      public String[] getServerBindingsChoices () {
      
      String[] choices;
      choices = new String[1];
      choices[0] = new String ("");
      int index = -1;
      int length = -1;
      
      for (int i=0; i<getNames ().size (); i++) {
      if (DEBUG)
      System.out.println ("names[" + i + "] = " + getNames ().elementAt (i));
      if (getNames ().elementAt (i).equals (orb)) {
      index = i;
      break;
      }
      }
      if (index >= 0) {
      length = ((Vector)serverBindings.elementAt (index)).size ();
      choices = new String[length];
      if (DEBUG) {
      System.out.println ("index: " + index);
      System.out.println ("orb: " + orb);
      System.out.println ("length: " + length);
      System.out.println ("bindings: " + (Vector)serverBindings.elementAt (index));
      }
      }
      if (index >= 0)
      for (int i=0; i<length; i++) {
      choices[i] = (String)((Vector)serverBindings.elementAt (index)).elementAt (i);
      if (DEBUG)
      System.out.println ("choice: " + choices[i]);
      }
      
      return choices;
      }
    */
    public boolean hideGeneratedFiles () {
        return _M_hide_generated_files;
    }


    public void setHideGeneratedFiles (boolean __value) {
	boolean __old = _M_hide_generated_files;
        _M_hide_generated_files = __value;
	firePropertyChange ("_M_hide_generated_files", __old, _M_hide_generated_files);       
    }
    /*
      public void setAdvancedOrbOptions (String orb) {
      
      if (DEBUG)
      System.out.println ("orb: " + orb);
      
      if (DEBUG)
      System.out.println ("setAdvancedOptions :)");
      JavaSettings js = (JavaSettings)JavaSettings.findObject (JavaSettings.class, true);
      
      //ClientBindingPropertyEditor cbedit = (ClientBindingPropertyEditor)ClientBindingPropertyEditor.findObject (ClientBindingPropertyEditor.class, true);
      //cbedit.setChoices (getChoices);
      
      String old_tie = getTieParam ();
      String old_dir = getDirParam ();
      String old_package = getPackageParam ();
      String old_expression = getErrorExpression ();
      String old_file = getFilePosition ();
      String old_line = getLinePosition ();
      String old_column = getColumnPosition ();
      String old_message = getMessagePosition ();
      
      // added for generator
      String old_implbase_impl_prefix = getImplBasePrefix ();
      String old_implbase_impl_postfix = getImplBasePostfix ();
      String old_ext_class_prefix = getExtClassPrefix ();
      String old_ext_class_postfix = getExtClassPostfix ();
      String old_tie_prefix = getTiePrefix ();
      String old_tie_postfix = getTiePostfix ();
      String old_impl_int_prefix = getImplIntPrefix ();
      String old_impl_int_postfix = getImplIntPostfix ();
      
      
      
      String new_expression = "";
      String new_file = "";
      String new_line = "";
      String new_column = "";
      String new_message = "";
      String new_dir = "";
      String new_package = "";
      String new_tie = "";
      
      // added for generator
      String new_implbase_prefix;
      String new_implbase_postfix;
      String new_ext_class_prefix;
      String new_ext_class_postfix;
      String new_tie_prefix;
      String new_tie_postfix;
      String new_impl_int_prefix;
      String new_impl_int_postfix;
      
      NbProcessDescriptor old_idl = getIdl ();
      NbProcessDescriptor new_idl = null;
      String old_delimiter = getPackageDelimiter();
      String new_delimiter = ".";
      
      setJavaTemplateTable ();
    */
    /*
      int index = -1;
      
      for (int i = 0; i<getNames ().size (); i++) {
      if (getNames ().elementAt (i).equals (orb)) {
      index = i;
      break;
      }
      }
      
      if (index == -1)
      return;
    */
    /*
      new_tie = _M_properties.getProperty ("TIE_PARAM");
      new_dir = _M_properties.getProperty ("DIR_PARAM");
      new_package = _M_properties.getProperty ("PACKAGE_PARAM");
      
      //String[] tmp1 = new String[] {NbProcessDescriptor.CP_REPOSITORY};
      //String[] tmp1 = new String[] {""};
      
      //new_idl = new NbProcessDescriptor ( (String)((Properties)props.elementAt (index)).getProperty ("COMPILER"), NbProcessDescriptor.NO_SWITCH, tmp1);
      String compiler = (String)_M_properties.getProperty ("COMPILER");
      if (DEBUG)
      System.out.println ("compiler: " + compiler);
      
      StringTokenizer st = new StringTokenizer (compiler);
      //      String process = compiler.substring (0, compiler.indexOf (' '));
      String process = st.nextToken ();
      //      String args = compiler.substring (compiler.indexOf (' '), compiler.length () - 1);
      String args = "";
      while (st.hasMoreTokens ()) {
      if (args.length () > 0)
      args = args + " " + st.nextToken ();
      else
      args = st.nextToken ();
      }
      if (DEBUG) {
      System.out.println ("process: " + process);
      System.out.println ("args: " + args);
      }
      new_idl = new NbProcessDescriptor (process, args, "");
      new_expression = _M_properties.getProperty ("ERROR_EXPRESSION");
      new_file = _M_properties.getProperty ("FILE_POSITION");
      new_line = _M_properties.getProperty ("LINE_POSITION");
      new_column = _M_properties.getProperty ("COLUMN_POSITION");
      new_message = _M_properties.getProperty ("MESSAGE_POSITION");
      new_delimiter = _M_properties.getProperty ("PACKAGE_DELIMITER");
      
      // added for generator
      new_implbase_prefix = _M_properties.getProperty ("IMPLBASE_IMPL_PREFIX");
      new_implbase_postfix = _M_properties.getProperty ("IMPLBASE_IMPL_POSTFIX");
      new_ext_class_prefix = _M_properties.getProperty ("EXT_CLASS_PREFIX");
      new_ext_class_postfix = _M_properties.getProperty ("EXT_CLASS_POSTFIX");
      new_tie_prefix = _M_properties.getProperty ("TIE_IMPL_PREFIX");
      new_tie_postfix = _M_properties.getProperty ("TIE_IMPL_POSTFIX");
      new_impl_int_prefix = _M_properties.getProperty ("IMPL_INT_PREFIX");
      new_impl_int_postfix = _M_properties.getProperty ("IMPL_INT_POSTFIX");

      setTieParam (new_tie);
      setDirParam (new_dir);
      setPackageParam (new_package);
      setIdl (new_idl);
      setErrorExpression (new_expression);
      setFilePosition (new_file);
      setLinePosition (new_line);
      setColumnPosition (new_column);
      setMessagePosition (new_message);
      setPackageDelimiter (new_delimiter);
      
      // added for generator
      setImplBasePrefix (new_implbase_prefix);
      setImplBasePostfix (new_implbase_postfix);
      setExtClassPrefix (new_ext_class_prefix);
      setExtClassPostfix (new_ext_class_postfix);
      setTiePrefix (new_tie_prefix);
      setTiePostfix (new_tie_postfix);
      setImplIntPrefix (new_impl_int_prefix);
      setImplIntPostfix (new_impl_int_postfix);
      
      if (DEBUG)
      System.out.println ("setAdvancedOptions () - end!");
      
      }
    */
    public void setJavaTemplateTable () {

        int index = 0;

        String tmp_property;

        if (DEBUG)
            System.out.println ("ORBSettings::setJavaTemplateTable");

        JavaSettings js = (JavaSettings)JavaSettings.findObject (JavaSettings.class, true);
        Properties __properties = js.getReplaceableStringsProps ();

        try {
	    /*
	      for (int i = 0; i<getNames ().size (); i++) {
	      if (getNames ().elementAt (i).equals (orb)) {
	      index = i;
	      break;
	      }
	      }
	    */
            //if (DEBUG)
            //   System.out.println ("props at position: " + props.elementAt (index));

            //if (DEBUG)
            //   System.out.println (((Properties)props.elementAt (index)).getProperty
            //     ("SETTINGS_ORB_PROPERTIES"));

            //if (DEBUG)
            //    System.out.println ("sett: " + ((Properties)props.elementAt (index)).getProperty
            //			("SETTINGS_ORB_PROPERTIES"));
            if (DEBUG)
                System.out.println ("cb: " + this.getClientBindingName ());
            if (DEBUG)
                System.out.println ("sb: " + this.getServerBindingName ());

            __properties.setProperty ("ORB_NAME", this.getOrbName ());

            if (this.getServerBinding () != null)
                __properties.setProperty ("SERVER_BINDING", this.getServerBinding ().getValue ());
            if (this.getClientBinding () != null)
                __properties.setProperty ("CLIENT_BINDING", this.getClientBinding ().getValue ());


            __properties.setProperty ("SETTINGS_ORB_PROPERTIES", _M_properties.getProperty
				      ("SETTINGS_ORB_PROPERTIES"));
            if (_M_properties.getProperty
		("IMPORT_" + this.getClientBindingName ()) != null) {
                __properties.setProperty ("ORB_IMPORT",_M_properties.getProperty
					  ("IMPORT_" + this.getClientBindingName ()));
            }
	    else {
                if (_M_properties.getProperty
		    ("IMPORT_" + this.getServerBindingName ()) != null) {
                    __properties.setProperty ("ORB_IMPORT",_M_properties.getProperty
					      ("IMPORT_" + this.getServerBindingName ()));
                }
                else {
                    __properties.setProperty ("ORB_IMPORT", _M_properties.getProperty
					      ("IMPORT"));
                }
            }
	    
            __properties.setProperty ("ORB_SERVER_INIT", _M_properties.getProperty
				      ("ORB_SERVER_INIT"));
            __properties.setProperty ("ORB_CLIENT_INIT", _M_properties.getProperty
				      ("ORB_CLIENT_INIT"));
	    
	    if (!this.getClientBindingName ().equals (""))
		if ((tmp_property = _M_properties.getProperty
		     ("CLIENT_" + this.getClientBindingName ())) != null)
		    __properties.setProperty ("ORB_CLIENT_BINDING", tmp_property);
	    /*
	      __properties.setProperty ("ORB_CLIENT_BINDING", _M_properties.getProperty 
	      ("CLIENT_" + this.getClientBindingName ()));
	    */
	    
	    if (!this.getServerBindingName ().equals (""))
		if ((tmp_property = _M_properties.getProperty
		     ("SERVER_" + this.getServerBindingName ())) != null)
		    __properties.setProperty ("ORB_SERVER_BINDING", tmp_property);
	    /*
	      __properties.setProperty ("ORB_SERVER_BINDING", _M_properties.getProperty 
	      ("SERVER_" + this.getServerBindingName ()));
	    */
            __properties.setProperty ("ORB_OBJECT_ACTIVATION", _M_properties.getProperty
				      ("ORB_OBJECT_ACTIVATION"));

            __properties.setProperty ("ORB_SERVER_RUN", _M_properties.getProperty
				      ("ORB_SERVER_RUN"));

            // added for implementation generator
            /*
            p.setProperty ("IMPL_PREFIX", ((Properties)props.elementAt (index)).getProperty 
            ("IMPL_PREFIX"));

            p.setProperty ("IMPL_POSTFIX", ((Properties)props.elementAt (index)).getProperty 
            ("IMPL_POSTFIX"));

            p.setProperty ("EXT_CLASS_PREFIX", ((Properties)props.elementAt (index)).getProperty 
            ("EXT_CLASS_PREFIX"));

            p.setProperty ("EXT_CLASS_POSTFIX", ((Properties)props.elementAt (index)).getProperty 
            ("EXT_CLASS_POSTFIX"));
            */

        } catch (Exception e) {
            e.printStackTrace ();
        }


        //js.setReplaceableStringsTable
        ByteArrayOutputStream bs = new ByteArrayOutputStream ();
        try {
            __properties.store (bs, null);
        } catch (IOException e) {
            if (DEBUG)
                System.out.println (e);
        }
        //if (DEBUG)
        //	 System.out.println ("properties: " + bs.toString ());
        js.setReplaceableStringsTable (bs.toString ());

    }


    public void loadImpl (FileObject __fo) {

        _M_properties = new Properties ();
        _M_client_bindings = new Vector (5);
        _M_server_bindings = new Vector (5);
	

        if (DEBUG)
            System.out.println ("loadImpl (" + __fo.getName () + ") ...");

        TopManager tm = TopManager.getDefault ();

        try {
	    Properties __properties = new Properties ();
	    __properties.load (__fo.getInputStream ());

	    // checking of important properties fields

	    _M_properties = __properties;
	    for (int j=0; j<_M_check_sections.length; j++) {
		if (__properties.getProperty (_M_check_sections[j]) == null) {
		    throw new PropertyNotFoundException (_M_check_sections[j]);
		}
	    }
	    setOrbName (__properties.getProperty ("CTL_NAME"));
	    
	    if (DEBUG)
		System.out.println ("impl: " + _M_orb_name);
	    //getNames ().add (__properties.getProperty ("CTL_NAME"));

	    // make client and server bindings

	    Vector __client_bindings = new Vector (5);
	    for (int j=0; j<_M_cbindings.length; j++)
		if (__properties.getProperty ("CLIENT_" + _M_cbindings[j]) != null) {
		    if (DEBUG)
			System.out.println ("add cb: " + "CTL_CLIENT_" + _M_cbindings[j]);
		    __client_bindings.add (CORBASupport.bundle.getString
					   ("CTL_CLIENT_" + _M_cbindings[j]));
		}
	    //_M_client_bindings.add (__client_bindings);
	    _M_client_bindings = __client_bindings;
	    
	    Vector __server_bindings = new Vector (5);
	    for (int j=0; j<_M_sbindings.length; j++)
		if (__properties.getProperty ("SERVER_" + _M_sbindings[j]) != null) {
		    if (DEBUG)
			System.out.println ("add sb: " + "CTL_SERVER_" + _M_sbindings[j]);
		    __server_bindings.add (CORBASupport.bundle.getString
					   ("CTL_SERVER_" + _M_sbindings[j]));
		}
	    //_M_server_bindings.add (__server_bindings);
	    _M_server_bindings = __server_bindings;
	    //System.out.println ("props: ");
	    //props.list (System.out);
	    
	    if (DEBUG) {
		//System.out.println ("names: " + getNames ());
		System.out.println ("clients bindings: " + _M_client_bindings);
		System.out.println ("servers bindings: " + _M_server_bindings);
	    }
	    setServerBinding (new ORBSettingsWrapper (this));
	    setClientBinding (new ORBSettingsWrapper (this));

	    //setAdvancedOrbOptions ("");
	    setTieParam (_M_properties.getProperty ("TIE_PARAM"));
	    setDirParam (_M_properties.getProperty ("DIR_PARAM"));
	    setPackageParam (_M_properties.getProperty ("PACKAGE_PARAM"));

	    String compiler = (String)_M_properties.getProperty ("COMPILER");
	    if (DEBUG)
		System.out.println ("compiler: " + compiler);

	    StringTokenizer st = new StringTokenizer (compiler);
	    String process = st.nextToken ();
	    String args = "";
	    while (st.hasMoreTokens ()) {
		if (args.length () > 0)
		    args = args + " " + st.nextToken ();
		else
		    args = st.nextToken ();
	    }
	    if (DEBUG) {
		System.out.println ("process: " + process);
		System.out.println ("args: " + args);
	    }
	    setIdl (new NbProcessDescriptor (process, args, ""));

        setErrorExpression (_M_properties.getProperty ("ERROR_EXPRESSION"));
        setFilePosition (_M_properties.getProperty ("FILE_POSITION"));
        setLinePosition (_M_properties.getProperty ("LINE_POSITION"));
        setColumnPosition (_M_properties.getProperty ("COLUMN_POSITION"));
        setMessagePosition (_M_properties.getProperty ("MESSAGE_POSITION"));
        setPackageDelimiter (_M_properties.getProperty ("PACKAGE_DELIMITER"));

        // added for generator
        setImplBasePrefix (_M_properties.getProperty ("IMPLBASE_IMPL_PREFIX"));
        setImplBasePostfix (_M_properties.getProperty ("IMPLBASE_IMPL_POSTFIX"));
        setExtClassPrefix (_M_properties.getProperty ("EXT_CLASS_PREFIX"));
        setExtClassPostfix (_M_properties.getProperty ("EXT_CLASS_POSTFIX"));
        setTiePrefix (_M_properties.getProperty ("TIE_IMPL_PREFIX"));
        setTiePostfix (_M_properties.getProperty ("TIE_IMPL_POSTFIX"));
        setImplIntPrefix (_M_properties.getProperty ("IMPL_INT_PREFIX"));
        setImplIntPostfix (_M_properties.getProperty ("IMPL_INT_POSTFIX"));
   
	} catch (Exception e) {
	    e.printStackTrace ();
	}
    }

    /*
      public ORB getORB () {
      if (_ORB == null)
      initOrb ();
      return _ORB;
      }
      
      public void initOrb () {
      _ORB = ORB.init (new String[] {""}, null);
      }


      public Vector getNamingServiceChildren () {
      //System.out.println ("getNamingServiceChildren");
      return namingChildren;
      }
      
      public void setNamingServiceChildren (Vector children) {
      //System.out.println ("setNamingServiceChildren");
      namingChildren = children;
      }
      
      public Vector getInterfaceRepositoryChildren () {
      //System.out.println ("getInterfaceRepositoryChildren: " + IRChildren.size ());
      return IRChildren;
      }
      
      public void setInterfaceRepositoryChildren (Vector children) {
      //System.out.println ("setInterfaceRepositoryChildren: " + children.size ());
      IRChildren = children;
      }
    */

    public String getGeneration () {
        //System.out.println ("getGeneration () -> " + generation);
        return _M_generation;
    }

    public void setGeneration (String __value) {
        //System.out.println ("setGeneration (" + value + ");");
	String __old = _M_generation;
        _M_generation = __value;
	firePropertyChange ("_M_generation", __old, _M_generation);
    }

    public String getSynchro () {
        //System.out.println ("getSynchro () -> " + synchro);
        return _M_synchro;
    }

    public void setSynchro (String __value) {
        //System.out.println ("setSynchro (" + value + ");");
	String __old = _M_synchro;
        _M_synchro = __value;
	firePropertyChange ("_M_synchro", __old, _M_synchro);
    }


    public void addPropertyChangeListener (PropertyChangeListener __listener) {
	if (DEBUG)
	    System.out.println ("ORBSettings::addPropertyChangeListener (" + __listener + ")");
	//_M_listeners.add (__listener);
	_M_property_change_support.addPropertyChangeListener (__listener);
    }
    
    
    public void removePropertyChangeListener (PropertyChangeListener __listener) {
	if (DEBUG)
	    System.out.println ("ORBSettings::removePropertyChangeListener (" + __listener + ")");
	//_M_listeners.remove (__listener);
	_M_property_change_support.removePropertyChangeListener (__listener);
    }
    
    
    public void firePropertyChange (PropertyChangeEvent __event) {
	_M_property_change_support.firePropertyChange (__event);
    }
      
    
    public void firePropertyChange (String __property_name, boolean __old_value,
				    boolean __new_value) {
	_M_property_change_support.firePropertyChange (__property_name, __old_value, __new_value);
    }
    
    
    public void firePropertyChange (String __property_name, int __old_value, int __new_value) {
	_M_property_change_support.firePropertyChange (__property_name, __old_value, __new_value);
    }
    
    
    public void firePropertyChange (String __property_name, java.lang.Object __old_value, 
				    java.lang.Object __new_value) {
	_M_property_change_support.firePropertyChange (__property_name, __old_value, __new_value);
    } 

    /*
      public Vector getListeners () {
      return _M_listeners;
      }
    */
    /*
      public boolean hasListener (PropertyChangeListener __listener) {
      for (int __i = 0; __i < _M_listeners.size (); __i++) {
      if (__listener == _M_listeners.elementAt (__i))
      return true;
      }
      return false;
      }
    */
    
    public String getOrbName () {
	return _M_orb_name;
    }

    public void setOrbName (String __value) {
	if (DEBUG)
	    System.out.println ("ORBSettings::setOrbName (" + __value + ")");
	_M_orb_name = __value;
    }

    public Vector getServerBindings () {
	if (DEBUG)
	    System.out.println ("ORBSettings::getServerBindings () -> " + _M_server_bindings);
	return _M_server_bindings;
    }

    public Vector getClientBindings () {
	if (DEBUG)
	    System.out.println ("ORBSettings::getClientBindings () -> " + _M_client_bindings);
	return _M_client_bindings;
    }
}





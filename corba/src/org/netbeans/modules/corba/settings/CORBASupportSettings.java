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

package com.netbeans.enterprise.modules.corba.settings;

import java.io.*;

import com.netbeans.ide.options.SystemOption;
//import com.netbeans.ide.options.ContextSystemOption;
import com.netbeans.ide.util.NbBundle;

import com.netbeans.developer.modules.loaders.java.settings.JavaSettings;

import java.util.Properties;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import com.netbeans.enterprise.modules.corba.*;

public class CORBASupportSettings extends SystemOption implements PropertyChangeListener {
   
   private static final boolean DEBUG = false;
   //private static final boolean DEBUG = true;

   public static String orb = CORBASupport.ORBIX;
   
   public static String skels = CORBASupport.INHER;

   public static String params;

   public static String _server_binding = CORBASupport.SERVER_NS;

   public static String _client_binding = CORBASupport.CLIENT_NS;    

   // advanced settings

   public static String _test;
    
   public static File idl;
    
   public static String _package_param;
    
   public static String _dir_param;
    
   public static String _orb_class;
    
   public static String _orb_singleton;
    
   public static String _orb_import;
    
   public static String _package_delimiter;
    
   public static String _error_expression;
    
   public static String _file_position;
    
   public static String _line_position;
    
   public static String _column_position;
    
   public static String _message_position;

   public static String _table = "USER="+System.getProperty("user.name")+"\n";


   /** @return human presentable name */
   public String displayName() {
      return CORBASupport.bundle.getString("CTL_CORBASupport_options");
   }
  
   public CORBASupportSettings () {
      //	setOrb (CORBASupport.bundle.getString ("CTL_ORBIX"));
      //addOption (getCORBASupportAdvancedSettings ());
      if (DEBUG)
	 System.out.println ("CORBASupportSettings () ...");
      addPropertyChangeListener (this); 
      //addOption (getCORBASupportAdvancedSettings ());
      //      setOrb (CORBASupport.bundle.getString ("CTL_ORBIX"));
   }

   public void propertyChange (PropertyChangeEvent event) {
      
      if (DEBUG)
	 System.out.println ("propertyChange: " + event.getPropertyName ());
      if (event.getPropertyName ().equals ("orb"))
	 setAdvancedOrbOptions ((String) event.getNewValue ());
      if (event.getPropertyName ().equals ("_client_binding"))
	 setAdvancedClientBinding ((String) event.getNewValue ());
      if (event.getPropertyName ().equals ("_server_binding"))
	 setAdvancedServerBinding ((String) event.getNewValue ());
      
   }

   public void setAdvancedClientBinding (String binding) {

      if (DEBUG)
	 System.out.println ("client binding: " + binding);
      if (DEBUG)
	 System.out.println ("ctl_client_binding: " + getCtlClientBindingName ());
      setJavaTemplateTable ();
   }

   public void setAdvancedServerBinding (String binding) {

      if (DEBUG)
	 System.out.println ("server binding: " + binding);
      if (DEBUG)
	 System.out.println ("ctl_server_binding: " + getCtlServerBindingName ());
      setJavaTemplateTable ();
   }
   

   public String getOrb () {
      return orb;
   }

   public void setOrb (String s) {
      String old = "";
      orb = s;
      firePropertyChange ("orb", old, orb);
      //setAdvancedOptions ();
   }

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

   public String getClientBindingName () {

      String name = "";

      if (_client_binding.equals (CORBASupport.CLIENT_NS))
	 name = "NS";
      if (_client_binding.equals (CORBASupport.CLIENT_IOR_FROM_FILE))
	 name = "IOR_FROM_FILE";
      if (_client_binding.equals (CORBASupport.CLIENT_IOR_FROM_INPUT))
	 name = "IOR_FROM_INPUT";
      if (_client_binding.equals (CORBASupport.CLIENT_BINDER))
	 name = "BINDER";
 
      return name;
   }

   public String getCtlClientBindingName () {
      return getCtlOrbName () + "CLIENT_" + getClientBindingName ();
   }

   public String getServerBindingName () {

      String name = "";

      if (_server_binding.equals (CORBASupport.SERVER_NS))
	 name = "NS";
      if (_server_binding.equals (CORBASupport.SERVER_IOR_TO_FILE))
	 name = "IOR_TO_FILE";
      if (_server_binding.equals (CORBASupport.SERVER_IOR_TO_OUTPUT))
	 name = "IOR_TO_OUTPUT";
      if (_server_binding.equals (CORBASupport.SERVER_BINDER))
	 name = "BINDER";
 
      return name;
   }

   public String getCtlServerBindingName () {
      return getCtlOrbName () + "SERVER_" + getServerBindingName ();
   }

   public String getSkels () {
      return skels;
   }

   public void setSkels (String s) {
      String old = skels;
      skels = s;
      firePropertyChange ("skels", old, skels);
    }

   public void setParams (String s) {
      String old = params;
      params = s;
      firePropertyChange ("params", old, params);
   }

   public String getParams () {
      return params;
   }

   public static String param () {
      return params;
   }


   public String getClientBinding () {
      return _client_binding;
   }

   public void setClientBinding (String s) {
       String old = _client_binding;
      _client_binding = s;
      firePropertyChange ("_client_binding", old, _client_binding);
   }

   public String getServerBinding () {
      return _server_binding;
   }

   public void setServerBinding (String s) {
       String old = _server_binding;
      _server_binding = s;
      firePropertyChange ("_server_binding", old, _server_binding); 
   }

    
   // advanced settings
   public File getIdl () {
      return idl;
   }

   public static String idl () {
      return idl.getPath ();
   }

   public void setIdl (File s) {
      File old = idl;
      idl = s;
      firePropertyChange ("idl", old, idl);
   }

   public void setPackage_param (String s) {
      String old = _package_param;
      _package_param = s;
      firePropertyChange ("_package_param", old, _package_param);
   }

   public String getPackage_param () {
      return _package_param;
   }

   public static String package_param () {
      return _package_param;
   }

   public void setDir_param (String s) {
      String old = _dir_param;
      _dir_param = s;
      firePropertyChange ("_dir_param", old, _dir_param);
   }

   public String getDir_param () {
      return _dir_param;
   }

   public static String dir_param () {
      return _dir_param;
   }

   public String getPackageDelimiter () {
      return _package_delimiter;
   }

   public void setPackageDelimiter (String s) {
      String old = _package_delimiter;
      _package_delimiter = s;
      firePropertyChange ("_package_delimiter", old, _package_delimiter);
   }

   public static char delim () {
      return _package_delimiter.charAt (0);
   }

   public String getErrorExpression () {
      return _error_expression;
   }

   public void setErrorExpression (String s) {
      String old = _error_expression;
      _error_expression = s;
      firePropertyChange ("_error_expression", old, _error_expression);
   }

   public static String expression () {
      return _error_expression;
   }

   public String getFilePosition () {
      return _file_position;
   }

   public void setFilePosition (String s) {
      String old = _file_position;
      _file_position = s;
      firePropertyChange ("_file_position", old, _file_position);
   }

   public static int file () {
      return new Integer(_file_position).intValue ();
   }

   public String getLinePosition () {
      return _line_position;
   }

   public void setLinePosition (String s) {
      String old = _line_position;
      _line_position = s;
      firePropertyChange ("_line_position", old, _line_position);
   }

   public static int line () {
      return new Integer(_line_position).intValue ();
   }

   public String getColumnPosition () {
      return _column_position;
   }

   public void setColumnPosition (String s) {
      String old = _column_position;
      _column_position = s;
      firePropertyChange ("_column_position", old, _column_position);
   }

   public static int column () {
      return new Integer(_column_position).intValue ();
   }

   public String getMessagePosition () {
      return _message_position;
   }

   public void setMessagePosition (String s) {
      String old = _message_position;
      _message_position = s;
      firePropertyChange ("_message_position", old, _message_position);
   }

   public static int message () {
      return new Integer(_message_position).intValue ();
   }

   public void setReplaceableStringsTable (String s) {
      String old = _table;
      _table = s;
      firePropertyChange ("_table", old, _table);
   }

   public String getRaplaceableStringsTable () {
      return _table;
   }
   
   public Properties getReplaceableStringsProps () {
      Properties props = new Properties ();
      try {
	 props.load (new StringBufferInputStream(_table));
      }
      catch (IOException e) {
      }
      return props;
   }

 
   public void setAdvancedOrbOptions (String orb) {

      if (DEBUG)
	 System.out.println ("orb: " + orb);

      /*
      String name = "";
      String orb_name = "CTL_";
     
      if (orb.equals (CORBASupport.ORBIX))
	 name = "ORBIX";
      if (orb.equals (CORBASupport.VISIBROKER))
	 name = "VISIBROKER";
      if (orb.equals (CORBASupport.ORBACUS))
	 name = "ORBACUS";
      if (orb.equals (CORBASupport.JAVAORB))
	 name = "JAVAORB";
      orb_name = orb_name + name + "_";
      */

      if (DEBUG)
	 System.out.println ("setAdvancedOptions :)");
      JavaSettings js = (JavaSettings)JavaSettings.findObject (JavaSettings.class, true);
      
      String old_dir = getDir_param ();
      String old_package = getPackage_param ();
      String old_expression = getErrorExpression ();
      String old_file = getFilePosition ();
      String old_line = getLinePosition ();
      String old_column = getColumnPosition ();
      String old_message = getMessagePosition ();
      String new_expression = "";
      String new_file = "";
      String new_line = "";
      String new_column = "";
      String new_message = "";
      String new_dir = "";
      String new_package = "";
      File old_idl = getIdl ();
      File new_idl = new File ("noname_idl");
      String old_delimiter = getPackageDelimiter();
      String new_delimiter = ".";
	
      /*

      Properties p = js.getReplaceableStringsProps ();

      try {
	 if (DEBUG)
	    System.out.println ("orb: " + getCtlOrbName ());
	 p.setProperty ("SETTINGS_ORB_PROPERTIES", CORBASupport.bundle.getString 
			(getCtlOrbName () + "SETTINGS_ORB_PROPERTIES"));
	 p.setProperty ("ORB_IMPORT", CORBASupport.bundle.getString (getCtlOrbName () + "IMPORT"));
	 p.setProperty ("ORB_SERVER_INIT", CORBASupport.bundle.getString 
			(getCtlOrbName () + "ORB_SERVER_INIT"));
	 p.setProperty ("ORB_CLIENT_INIT", CORBASupport.bundle.getString 
			(getCtlOrbName () + "ORB_CLIENT_INIT"));
      } catch (Exception e) {
	 e.printStackTrace ();
      }


      //js.setReplaceableStringsTable 
      ByteArrayOutputStream bs = new ByteArrayOutputStream ();
      try {
	 p.store (bs, null);
      } catch (IOException e) {
	 if (DEBUG)
	    System.out.println (e);
      }
      if (DEBUG)
	 System.out.println ("properties: " + bs.toString ());
      js.setReplaceableStringsTable (bs.toString ());    
      */

      setJavaTemplateTable ();


      new_dir = CORBASupport.bundle.getString (getCtlOrbName () + "DIR_PARAM");
      new_package = CORBASupport.bundle.getString (getCtlOrbName () + "PACKAGE_PARAM");
      new_idl = new File (CORBASupport.bundle.getString (getCtlOrbName () + "COMPILER"));
      new_expression = CORBASupport.bundle.getString (getCtlOrbName () + "ERROR_EXPRESSION");
      new_file = CORBASupport.bundle.getString (getCtlOrbName () + "FILE_POSITION");
      new_line = CORBASupport.bundle.getString (getCtlOrbName () + "LINE_POSITION");
      new_column = CORBASupport.bundle.getString (getCtlOrbName () + "COLUMN_POSITION");
      new_message = CORBASupport.bundle.getString (getCtlOrbName () + "MESSAGE_POSITION");
      new_delimiter = CORBASupport.bundle.getString (getCtlOrbName () + "PACKAGE_DELIMITER");

      setDir_param (new_dir);
      setPackage_param (new_package);
      setIdl (new_idl);
      setErrorExpression (new_expression);
      setFilePosition (new_file);
      setLinePosition (new_line);
      setColumnPosition (new_column);
      setMessagePosition (new_message);
      setPackageDelimiter (new_delimiter);

      if (DEBUG)
	 System.out.println ("setAdvancedOptions () - end!");

   }

   public void setJavaTemplateTable () {

      if (DEBUG)
	 System.out.println ("setJavaTemplateTable");

      JavaSettings js = (JavaSettings)JavaSettings.findObject (JavaSettings.class, true);
      Properties p = js.getReplaceableStringsProps ();

      try {
	 if (DEBUG)
	    System.out.println ("orb: " + getCtlOrbName ());
	 p.setProperty ("SETTINGS_ORB_PROPERTIES", CORBASupport.bundle.getString 
			(getCtlOrbName () + "SETTINGS_ORB_PROPERTIES"));
	 p.setProperty ("ORB_IMPORT", CORBASupport.bundle.getString (getCtlOrbName () + "IMPORT"));
	 p.setProperty ("ORB_SERVER_INIT", CORBASupport.bundle.getString 
			(getCtlOrbName () + "ORB_SERVER_INIT"));
	 p.setProperty ("ORB_CLIENT_INIT", CORBASupport.bundle.getString 
			(getCtlOrbName () + "ORB_CLIENT_INIT"));
	 p.setProperty ("ORB_CLIENT_BINDING", CORBASupport.bundle.getString
			(getCtlClientBindingName ()));
	 p.setProperty ("ORB_SERVER_BINDING", CORBASupport.bundle.getString
			(getCtlServerBindingName ()));
			
      } catch (Exception e) {
	 e.printStackTrace ();
      }


      //js.setReplaceableStringsTable 
      ByteArrayOutputStream bs = new ByteArrayOutputStream ();
      try {
	 p.store (bs, null);
      } catch (IOException e) {
	 if (DEBUG)
	    System.out.println (e);
      }
      if (DEBUG)
	 System.out.println ("properties: " + bs.toString ());
      js.setReplaceableStringsTable (bs.toString ());    

   }
    
}



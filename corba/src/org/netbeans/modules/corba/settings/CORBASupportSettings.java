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

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.File;

import org.omg.CORBA.ORB;

import org.openide.TopManager;
import org.openide.ErrorManager;
import org.xml.sax.SAXException;

import org.openide.options.SystemOption;
//import org.openide.options.ContextSystemOption;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;

import org.openide.execution.NbProcessDescriptor;

import org.netbeans.modules.java.settings.JavaSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.Vector;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.HashMap;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextSupport;
import java.beans.beancontext.BeanContextProxy;

import org.openide.TopManager;
import org.openide.NotifyDescriptor;

import org.openide.filesystems.FileObject;

import org.netbeans.modules.corba.CORBASupport;
import org.netbeans.modules.corba.IDLDataLoader;

import org.netbeans.modules.corba.utils.FullBeanContextSupport;

public class CORBASupportSettings extends SystemOption implements BeanContextProxy {
								  //PropertyChangeListener {

    private static final boolean DEBUG = false;
    //private static final boolean DEBUG = true;

    private static final boolean DYNLOAD = true;
    //private static final boolean DYNLOAD = false;

    private static final boolean PRODUCTION = true;
    //private static final boolean PRODUCTION = false;

    static final long serialVersionUID = -2809668725556980488L;

    private static String _M_orb_name;

    private static String _M_orb_tag;

    // used in CORBASupportSettings::getActiveSetting ()
    private static String _M_orb_tag_cache;
    private static ORBSettings _M_setting_cache;

    private static FullBeanContextSupport _S_implementations
	= new FullBeanContextSupport ();

    private static FullBeanContextSupport _S_loaded_context;

    private ORB _M_orb;

    private static Vector _M_naming_children;

    private static Vector _M_ir_children;

    private boolean _M_loaded = false;
    private boolean _M_in_init = false;
    //private boolean deserealization;

    private HashMap _M_boston_names;

    /** @return human presentable name */
    public String displayName() {
        return CORBASupport.SETTINGS;
    }

    public CORBASupportSettings () {
        if (DEBUG) 
            System.out.println ("CORBASupportSettings () ..."); // NOI18N
    }

    private void init_boston_names_table () {
	_M_boston_names = new HashMap ();
	_M_boston_names.put ("J2EE ORB", "j2ee");
	_M_boston_names.put ("JacORB Beta 1.0 (unsupported)", "jacorb12");
	_M_boston_names.put ("JavaORB 1.2.x (unsupported)", "javaorb22");
	_M_boston_names.put ("JDK 1.2 ORB", "jdk12");
	_M_boston_names.put ("JDK 1.3 ORB", "jdk13");
	_M_boston_names.put ("ORBacus for Java 3.x for Windows (unsupported)", "orbacus3w");
	_M_boston_names.put ("ORBacus for Java 3.x (unsupported)", "orbacus3u");
	_M_boston_names.put ("ORBacus for Java 4.x for Windows (unsupported)", "orbacus4w");
	_M_boston_names.put ("ORBacus for Java 4.x (unsupported)", "orbacus4u");
	_M_boston_names.put ("Orbix 2000 for Java 1.x", "orbix20004j");
	_M_boston_names.put ("OrbixWeb 3.2", "orbixweb32");
	_M_boston_names.put ("VisiBroker for Java 3.4", "visibroker34");
	_M_boston_names.put ("VisiBroker for Java 4.0", "vb4");
    }

    public void init () {
	if (DEBUG)
	    System.out.println ("CORBASupportSettings::init ();");
	_M_in_init = true;
	_M_naming_children = new Vector ();
	_M_ir_children = new Vector ();
	this.init_boston_names_table ();
	/*
	  if (DYNLOAD && !PRODUCTION) {
	  this.setOrb ("ORBacus for Java 3.1.x"); // NOI18N
	  }

	  if (PRODUCTION) {
	  this.setOrb ("JDK 1.2 ORB"); // NOI18N
	  }
	*/
	if (!_M_loaded)
	    this.setBeans (this.getBeans ());
	if (this.getActiveSetting () == null)
	    this.setORBTag ("jdk13");
	_M_in_init = false;
    }

    public void readExternal (ObjectInput __in)
	throws java.io.IOException, java.lang.ClassNotFoundException {
	if (DEBUG)
	    System.out.println (":-)CORBASupportSettings::readExternal (" + __in + ")"); // NOI18N
	_M_in_init = true;
	//deserealization = true;
	//try {
	super.readExternal (__in);
	//} catch (Exception __x) {
	//__x.printStackTrace ();
	//throw __x;
	//}
	//IDLDataLoader __loader = (IDLDataLoader)IDLDataLoader.findObject
	//    (IDLDataLoader.class, true);
	//System.out.println ("CORBASupportSettings (IDLDataLoader)__in.readObject ()"); // NOI18N
	//__loader = (IDLDataLoader)__in.readObject ();
	//System.out.println ("CORBASupportSettings done"); // NOI18N
	_M_in_init = false;
	//this.setOrb (this.getOrb ());
	//deserealization = false;
	//this.setOrb (_M_orb_name);
	/*
	  if (this.getActiveSetting () != null) {
	  if (DEBUG)
	  System.out.println ("seting java template map after loading");
	  this.getActiveSetting ().setJavaTemplateTable ();
	  }
	*/
    }


    public void writeExternal (ObjectOutput __out) throws IOException {
	if (DEBUG)
	    System.out.println ("CORBASupportSettings::writeExternal (" + __out + ")"); // NOI18N
	//_M_implementations.writeExternal (__out);
	//((BeanContextSupport)_M_implementations).writeObject (__out);
	super.writeExternal (__out);
	//IDLDataLoader __loader = (IDLDataLoader)IDLDataLoader.findObject
	//    (IDLDataLoader.class, true);
	//__out.writeObject (__loader);
      }

    /*
      private void writeObject (java.io.ObjectOutputStream __out) throws IOException {
      if (DEBUG)
      System.out.println ("CORBASupportSettings::writeObject (" + __out + ")");
      __out.defaultWriteObject ();
      //__out.writeObject (this.getBeans ());
      }


      private void readObject (java.io.ObjectInputStream __in) throws IOException, ClassNotFoundException {
      if (DEBUG)
      System.out.println ("CORBASupportSettings::readObject (" + __in + ")");
      __in.defaultReadObject ();
      //__in.readObject ();
      }
    */

    public boolean isGlobal () {
        return false;
    }


    public Vector getNames () {
	if (DEBUG)
	    System.out.println ("CORBASupportSettings::getNames ()"); // NOI18N

	Vector __names = new Vector ();
	Iterator __iterator = _S_implementations.iterator ();
	while (__iterator.hasNext ()) {
	    __names.add (((ORBSettings)__iterator.next ()).getName ());
	}
	return __names;
    }


    public String getOrb () {
	if (DEBUG)
	    System.out.println ("getOrb () -> " + _M_orb_name);
	if (_M_orb_name == null)
	    _M_orb_name = "";
        return _M_orb_name;
    }


    public synchronized void setOrb (String __value) {
	//boolean DEBUG=true;
	if (DEBUG)
	    System.out.println ("CORBASupportSettings::setOrb (" + __value + ")"); // NOI18N
        String __old = _M_orb_name;
	_M_orb_name = __value;
	//setJavaTemplateTable ();
	/*
	  ORBSettings __settings = this.getActiveSetting ();
	  if (__settings != null)
	  __settings.setJavaTemplateTable ();
	*/
	String __orb_name = this.getOrb ();
	__orb_name = this.removeUnsupportedPostfix (__orb_name);
	//if (__orb_name.endsWith (ORBSettingsBundle.CTL_UNSUPPORTED))
	//__orb_name = this.getOrb ().substring
	//(0, this.getOrb ().length ()
	//- (ORBSettingsBundle.CTL_UNSUPPORTED.length () + 1));
	this.firePropertyChange ("_M_orb_name", __old, _M_orb_name); // NOI18N
	if (this.getActiveSetting () != null) {
	    if (!__orb_name.equals (this.getActiveSetting ().getOrbName ())) {
		Iterator __iter = _S_implementations.iterator ();
		while (__iter.hasNext ()) {
		    ORBSettings __tmp = (ORBSettings)__iter.next ();
		    if (__orb_name.equals (__tmp.getOrbName ())) {
			this.setORBTag (__tmp.getORBTag ());
			break;
		    }
		}
	    }
	}
	else {
	    System.out.println ("getActiveSetting () -> " + this.getActiveSetting ());
	    // this.getActiveSetting () == null => we have to set orb tag
	    Iterator __iter = _S_implementations.iterator ();
	    while (__iter.hasNext ()) {
		ORBSettings __tmp = (ORBSettings)__iter.next ();
		if (this.getOrb ().equals (__tmp.getOrbName ())) {
		    this.setORBTag (__tmp.getORBTag ());
		    break;
		}
	    }
	}
	/*
	  this.cacheThrow ();

	  if (!_M_in_init) {
	  boolean __orb_hide = this.getActiveSetting ().hideGeneratedFiles ();
	  IDLDataLoader __loader = (IDLDataLoader)IDLDataLoader.findObject
	  (IDLDataLoader.class, true);
	  boolean __old_hide = __loader.getHide ();
	  //System.out.println ("__orb_hide: " + __orb_hide); // NOI18N
	  //System.out.println ("__old_hide: " + __old_hide); // NOI18N
	  //if (__old_hide != __orb_hide)
	  __loader.setHide (__orb_hide);
	  }
	*/
    }


    public static FileObject findImplFolder () {
	TopManager __tm = TopManager.getDefault ();

	Enumeration __folders
	    = __tm.getRepository ().getDefaultFileSystem ().getRoot ().getFolders (false);
	while (__folders.hasMoreElements ()) {
	    FileObject __fo = (FileObject)__folders.nextElement ();
	    if (DEBUG)
		System.out.println (__fo.getName ());
	    if (__fo.toString ().equals ("CORBA")) { // NOI18N
		return __fo;
	    }
	}
	return null;
    }
    

    private String debug_tag (int __value) {
	switch (__value) {
	case 0: return "-";
	case 1: return "\\";
	case 2: return "|";
	case 3: return "/";
	}
	return "";
    }


    //public synchronized void loadImpl () {
    public synchronized BeanContext loadImpl () {
	//boolean DEBUG=true;
	//boolean BSD_DEBUG=true;
	boolean BSD_DEBUG=false;
	int __position = 0;
	String __prefix = "loading ";
	String __postfix = "\r";
	if (DEBUG)
	    System.out.println ("CORBASupportSettings::loadImpl ()"); // NOI18N

	//if (_M_loaded)
	//    return;

	//BeanContextSupport __context = new BeanContextSupport ();
	BeanContext __context = new FullBeanContextSupport ();

	//if (_M_orb_names == null)
	//    _M_orb_names = new Vector (5);
	FileObject __parent = CORBASupportSettings.findImplFolder ();
	if (DEBUG)
	    System.out.println ("__parent: " + __parent);
	if (__parent != null) {
	    _M_loaded = true;
	    FileObject[] __files = __parent.getChildren ();
	    for (int __i = 0; __i<__files.length ; __i++) {
		if (!__files[__i].getExt ().equals ("xml"))
		    continue;
		if (DEBUG)
		    System.out.println ("file: " + __files[__i].toString ()); // NOI18N
		if (BSD_DEBUG) {
		    System.out.print (__prefix + this.debug_tag (__position)
				      + __postfix);
		    __position++;
		    if (__position > 3)
			__position = 0;
		}
		if (DEBUG)
		    System.out.println ("loading...");
		ORBSettings __orb_settings = new ORBSettings ();
		try {
		    //System.out.println ("before loading");
		    __orb_settings.loadImpl (__files[__i]);
		    //System.out.println ("after loading");
		    //if (__orb_settings.getProperties () != null) {
		    //we found impl file for this setting
		    //_M_implementations.add (__orb_settings);
		    // loadImpl postcondition
		    if (__orb_settings.getORBTag()!= null && __orb_settings.getOrbName()!=null)
		    	__context.add (__orb_settings);
		    else {
		    	TopManager tm = TopManager.getDefault();
			if (tm != null) {
				ErrorManager errMgr = tm.getErrorManager();
				if (errMgr != null) {
					errMgr.log ("org::netbeans:modules::corba::settings::CORBASupportSettings::loadImpl postcondition failed\n"+  // No I18N
					            "Can not process XML file:"+__files[__i]);	// No I18N
				}
			}
		    }
		    //_M_orb_names.add (__orb_settings.getOrbName ());
		    //}
		} catch (SAXException __ex) {
		    __ex.printStackTrace ();
		} catch (FileNotFoundException __ex) {
		    __ex.printStackTrace ();
		} catch (IOException __ex) {
		    TopManager.getDefault ().notifyException (__ex);
		    __ex.printStackTrace ();
		}
	    }
	}
	else {
	    //TopManager.getDefault ().notify
	    //(new NotifyDescriptor.Message (CORBASupport.CANT_FIND_IMPLS));
	    //System.out.println ("can't find system/CORBA directory"); // NOI18N
	}

	//if (!_M_loaded) // unsucesfull loading
	//return null;

	if (BSD_DEBUG)
	    System.out.println (__prefix + "done.");

	return __context;
	/*
	  if (DEBUG) {
	  System.out.println ("----!!!!");
	  java.lang.Object[] __beans = this.getBeans ();
	  for (int __i = 0; __i < __beans.length; __i++) {
	  System.out.println (__i + " : " + __beans[__i]);
	  }
	  }
	*/
    }


    public ORB getORB () {
        if (_M_orb == null)
            this.initOrb ();
        return _M_orb;
    }

    public void initOrb () {
	String __orb_class = System.getProperty ("org.omg.CORBA.ORBClass");
	String __orb_singleton = System.getProperty ("org.omg.CORBA.ORBSingletonClass");
	boolean __set_property = false;
	String __home = System.getProperty ("netbeans.home");
	String __config_url = "";
	if (__home != null && (!(__home.equals ("")))) {
	    __config_url = "file://";
	    if (Utilities.isWindows ())
		__config_url += "/";
	    __config_url += __home + File.separatorChar + "bin" + File.separatorChar;
	    __config_url += "openorb.xml";
	}
	else {
	    __config_url = "openorb.xml";
	}
	if (__orb_class == null && __orb_singleton == null) {
	    __set_property = true;
	}
	else {
	    if (__orb_class == null || __orb_singleton == null) {
		// partial setup
		StringBuffer __buf = new StringBuffer ();
		__buf.append (CORBASupport.PARTIAL_CONFIGURATION);
		__buf.append ("\n\n");
		__buf.append ("org.omg.CORBA.ORBClass = ");
		__buf.append (__orb_class);
		__buf.append ("\n");
		__buf.append ("org.omg.CORBA.ORBSingletonClass = ");
		__buf.append (__orb_singleton);
		__buf.append ("\n\n");
		__buf.append (CORBASupport.OPENORB_CONFIGURATION);
		String __msg = __buf.toString ();
		__set_property = true;
		TopManager.getDefault ().notify (new NotifyDescriptor.Message
		    (__msg));
	    }
	}
	Properties __props = System.getProperties ();
	System.out.println ("Initializing ORB");
	if (__set_property) {
	    __props.put ("org.omg.CORBA.ORBClass", "org.openorb.CORBA.ORB");
	    __props.put ("org.omg.CORBA.ORBSingletonClass",
			 "org.openorb.CORBA.ORBSingleton");
	    System.out.println ("openorb.config=" + __config_url);
	    __props.put ("openorb.config", __config_url);
	}
        _M_orb = ORB.init (new String[] {""}, __props); // NOI18N
	System.out.println ("ORB class: " + _M_orb.getClass ().getName ());
    }


    public Vector getNamingServiceChildren () {
        //System.out.println ("getNamingServiceChildren: " + _M_naming_children); // NOI18N
	if (_M_naming_children == null) {
	    //System.out.println ("_M_naming_children == null");
	    _M_naming_children = new Vector ();
	}
        return _M_naming_children;
    }

    public void setNamingServiceChildren (Vector __children) {
        //System.out.println ("setNamingServiceChildren: " + children); // NOI18N
        _M_naming_children = __children;
    }

    public Vector getInterfaceRepositoryChildren () {
        //System.out.println ("getInterfaceRepositoryChildren: " + _M_ir_children.size ()); // NOI18N
	if (_M_ir_children == null) {
	    //System.out.println ("_M_ir_children == null");
	    _M_ir_children = new Vector ();
	}
        return _M_ir_children;
    }

    public void setInterfaceRepositoryChildren (Vector __children) {
        //System.out.println ("setInterfaceRepositoryChildren: " + children.size ()); // NOI18N
        _M_ir_children = __children;
    }


    public java.beans.beancontext.BeanContextChild getBeanContextProxy () {
	if (DEBUG)
	    System.out.println ("CORBASupportSettings::getBeanContextProxy ()"); // NOI18N
	//Thread.dumpStack ();
	if (!_M_loaded) {
	    //loadImpl ();
	    this.setBeans (this.getBeans ());
	}
        return _S_implementations;
    }

    
    public void addBean (java.lang.Object __bean) {
        _S_implementations.add (__bean);
    }

    
    public void removeBean (java.lang.Object __bean) {
        _S_implementations.remove (__bean);
    }


    public java.lang.Object[] getBeans () {
	//if (DEBUG)
	//System.out.println ("CORBASupportSettings::getBeans () -> " + _S_implementations); // NOI18N
	if (_S_implementations == null) {
	    //_M_implementations = new BeanContextSupport ();
	    _S_implementations = new FullBeanContextSupport ();
	}
	//java.lang.Object[] __array = _M_implementations.toArray ();
	return _S_implementations.toArray ();
	//java.util.Arrays.sort (__array, new ORBSettingsComparator ());
	//System.out.println ("after sort:");
	//for (int __i=0; __i < __array.length; __i++)
	//    System.out.println (__i + " -> " + ((ORBSettings)__array[__i]).getName ());
	//return __array;
    }


    public synchronized void setBeans (java.lang.Object[] __beans) {
	try {
	//boolean DEBUG = true;

        boolean __boston_project = false;
	if (DEBUG)
	    System.out.println ("CORBASupportSettings::setBeans ("
			    + __beans + ":" + __beans.length + ");"); // NOI18N
	//Thread.dumpStack ();

	//_M_implementations = new BeanContextSupport ();
	//BeanContextSupport __tmp_implementations = new BeanContextSupport ();
	if (_S_implementations == null) {
	    _S_implementations = new FullBeanContextSupport ();
	}
	else {
	    ArrayList __tmp = new ArrayList ();
	    __tmp.addAll (_S_implementations);
	    if (DEBUG)
		System.out.println ("beans for remove: " + __tmp.size ());
	    Iterator __tmp_iter = __tmp.iterator ();
	    while (__tmp_iter.hasNext ()) {
		ORBSettings __s = (ORBSettings)__tmp_iter.next ();
		if (DEBUG)
		    System.out.println ("removing: " + __s.getOrbName ());
		_S_implementations.remove (__s);
	    }
	    //if (__tmp.size () > 0)
	    //_S_implementations.removeAll (__tmp);
	}
	FullBeanContextSupport __tmp_implementations = new FullBeanContextSupport ();
	//FullBeanContextSupport __distinguish_implementations = new FullBeanContextSupport ();

	//FullBeanContextSupport __loaded_context
	//= (FullBeanContextSupport)this.loadImpl ();
	if (DEBUG)
	    System.out.println ("_S_loaded_context: " + _S_loaded_context);
	if (_S_loaded_context == null || _M_loaded == false) {
//	    synchronized (this) {
		//Thread.dumpStack ();
		_S_loaded_context = (FullBeanContextSupport)this.loadImpl ();
//	    }
	}
	FullBeanContextSupport __serialized_context = new FullBeanContextSupport ();
	// At the first we have to filter deserialized settings and keep only those
	// for which we have (loaded) settings
	for (int i = 0; i < __beans.length; i++) {
	    ORBSettings __setting = (ORBSettings)__beans[i];
	    if (DEBUG)
		System.out.println ("trying " + __setting.getName ());
	    Iterator __iter = _S_loaded_context.iterator ();
	    while (__iter.hasNext ()) {
		ORBSettings __tmp = (ORBSettings)__iter.next ();
		String __t_tag = __tmp.getORBTag ();
		String __s_tag = __setting.getORBTag ();
		if (__s_tag == null) {
		    // it looks like old boston setting
		    __s_tag = (String)_M_boston_names.get (__setting.getOrbName ());
		    System.out.println ("boston tag: " + __s_tag);
		    __setting.setORBTag (__s_tag);
		    __setting.setBostonSettings (true);
		}
		if (DEBUG)
		    System.out.println ("comparing: " + __t_tag	+ " with " + __s_tag);
		if (__t_tag.equals (__s_tag)) {
		    if (DEBUG)
			System.out.println ("RIGHT :-))");
		    __serialized_context.add (__beans[i]);
		    break;
		}
	    }
	}

	// now we will try to find serialized settings to selected loaded settings
	Iterator __loaded_iterator = _S_loaded_context.iterator ();
	while (__loaded_iterator.hasNext ()) {
	    ORBSettings __loaded_setting = (ORBSettings)__loaded_iterator.next ();
	    ORBSettings __serialized_setting = null;
	    if ((__serialized_setting = this.findSettingByTag
		 (__serialized_context, __loaded_setting.getORBTag ())) != null)
		//|| (
		/*
		  (__serialized_setting = this.findSettingByName
		  (__serialized_context, __loaded_setting.getOrbName ())) != null)
		*/
		//)
	    {
		// we find serialized setting with same name
		// so we add it because it can have some serialized state
		// which loaded setting don't have
		if (DEBUG) {
		    System.out.println ("add serialized setting: " // NOI18N
					+ __serialized_setting.getName ());
		}
		//_M_implementations.add (__serialized_setting);

		// _M_supported is transient variable so I have to set it from impl file
		// I have to set all transient variables
		__serialized_setting.setSupported (__loaded_setting.isSupported ());
		__serialized_setting.setPOASettings (__loaded_setting.getPOASettings ());
		__serialized_setting.setJavaTemplateCodeTable
		    (__loaded_setting.getJavaTemplateCodeTable ());
		__serialized_setting.backupJavaTemplateCodeTable ();
		__serialized_setting.setServerBindings
		    (__loaded_setting.getServerBindings ());
		__serialized_setting.setClientBindings
		    (__loaded_setting.getClientBindings ());
		__serialized_setting.setIDLTemplateTable
		    (__loaded_setting.getIDLTemplateTable ());
		__serialized_setting.setJavaTemplateCodePatchTable
		    (__loaded_setting.getJavaTemplateCodePatchTable ());
		__serialized_setting.setLocalBundle (__loaded_setting.getLocalBundle ());
		if (__serialized_setting.isBostonSettings ()) {
		    // this seems like old boston project
		    if (DEBUG)
			System.out.println ("old project for "
					    + __serialized_setting.getName ());
		    __boston_project = true;
		    // sometimes on boston Orb name has '(unsupported)' postfix
		    // we must recovery it after deserialization from boston
		    __serialized_setting.setOrbName (__loaded_setting.getOrbName ());
		    __serialized_setting.setORBTag (__loaded_setting.getORBTag ());
		    __serialized_setting.setDelegation (__loaded_setting.getDelegation ());
		    __serialized_setting.setUseGuardedBlocks
			(__loaded_setting.getUseGuardedBlocks ());
		    __serialized_setting.setFindMethod (__loaded_setting.getFindMethod ());
		    // expert options
		    __serialized_setting.setImplBaseImplPrefix
			(__loaded_setting.getImplBaseImplPrefix ());
		    __serialized_setting.setImplBaseImplPostfix
			(__loaded_setting.getImplBaseImplPostfix ());
		    __serialized_setting.setTieClassPrefix
			(__loaded_setting.getTieClassPrefix ());
		    __serialized_setting.setTieClassPostfix
			(__loaded_setting.getTieClassPostfix ());
		    __serialized_setting.setTieImplPrefix
			(__loaded_setting.getTieImplPrefix ());
		    __serialized_setting.setTieImplPostfix
			(__loaded_setting.getTieImplPostfix ());
		    __serialized_setting.setValueFactoryImplPrefix
			(__loaded_setting.getValueFactoryImplPrefix ());
		    __serialized_setting.setValueFactoryImplPostfix
			(__loaded_setting.getValueFactoryImplPostfix ());
		    __serialized_setting.setValueImplPrefix
			(__loaded_setting.getValueImplPrefix ());
		    __serialized_setting.setValueImplPostfix
			(__loaded_setting.getValueImplPostfix ());
		}

		__tmp_implementations.add (__serialized_setting);
	    }
	    else {
		// we don't find serialized setting with same name as this loaded setting
		// so we add this loaded setting
		if (DEBUG) {
		    System.out.println ("add loaded setting: "
					+ __loaded_setting.getName ()); // NOI18N
		}
		__tmp_implementations.add (__loaded_setting);
	    }
	}
	//Iterator __iterator = __tmp_implementations.iterator ();
	//while (__iterator.hasNext ()) {
	//_M_implementations.add (__iterator.next ());
	//}
	//_M_implementations.addAll (__tmp_implementations);
	// now we must distinguish between supported and unsupported orbs and sort same
	// implementions by name
	BeanContext __supported_orbs = new FullBeanContextSupport ();
	BeanContext __unsupported_orbs = new FullBeanContextSupport ();
	Iterator __iterator = __tmp_implementations.iterator ();
	while (__iterator.hasNext ()) {
	    ORBSettings __settings = (ORBSettings)__iterator.next ();
	    if (__settings.isSupported ())
		__supported_orbs.add (__settings);
	    else
		__unsupported_orbs.add (__settings);
	}

	//ArrayList __sorted_implementations = new ArrayList (__tmp_implementations);
	//ArrayList __sorted_implementations = new ArrayList (__distinguish_implementations);
	ArrayList __sorted_supported_orbs = new ArrayList (__supported_orbs);
	ArrayList __sorted_unsupported_orbs = new ArrayList (__unsupported_orbs);
	Collections.sort (__sorted_supported_orbs, new ORBSettingsComparator ());
	Collections.sort (__sorted_unsupported_orbs, new ORBSettingsComparator ());
	//Iterator __iterator = __sorted_implementations.iterator ();
	//__iterator = __distinguish_implementations.iterator ();
	__iterator = __sorted_supported_orbs.iterator ();
	while (__iterator.hasNext ()) {
	    _S_implementations.add (__iterator.next ());
	}
	__iterator = __sorted_unsupported_orbs.iterator ();
	while (__iterator.hasNext ()) {
	    _S_implementations.add (__iterator.next ());
	}
	if (DEBUG) {
	    __iterator = _S_implementations.iterator ();
	    while (__iterator.hasNext ()) {
		System.out.println (((ORBSettings)__iterator.next ()).getName ());
	    }
	}

	//System.out.println ("At the end of setBeans method");
	//this.setORBTag (this.getORBTag ());
	if (__boston_project) {
	    // the tag is usually set to jdk13 and it's wrong
	    // because boston project has no tag
	    this.setORBTag ((String)_M_boston_names.get (this.getOrb ()));
	    if (DEBUG) {
		System.out.println ("Boston project hack for changing name");
		System.out.println ("tag: " + this.getORBTag ());
	    }
	    ORBSettings __settings = this.getSettingByTag (this.getORBTag ());
	    if (DEBUG)
		System.out.println ("found name: " + __settings.getName ());
	    //this.setORBTag (this.getORBTag ());
	    this.setOrb (__settings.getName ());
	    //this.setORBTag (this.getORBTag ());
	}
	else {
	    if (DEBUG)
		System.out.println ("Pilsen project.");
	}
	this.cacheThrow ();
	if (_M_loaded && this.getActiveSetting () != null) {
	    if (DEBUG)
		System.out.println ("seting java template map after loading");
	    this.getActiveSetting ().setJavaTemplateTable ();
	}

	} catch (Exception __ex) {
	    //__ex.printStackTrace ();
	    TopManager.getDefault ().getErrorManager ().notify (__ex);
	}
    }


    public ORBSettings findSettingByName (BeanContext __context, String __name) {
	Iterator __iterator = __context.iterator ();
	String __tmp_name = null;
	__tmp_name = this.removeUnsupportedPostfix (__name);
	/*
	  if (__name.endsWith (ORBSettingsBundle.CTL_UNSUPPORTED)) {
	  __tmp_name = __name.substring 
	  (0, __name.length () 
	  - (ORBSettingsBundle.CTL_UNSUPPORTED.length () + 1));
	  }
	*/
	String __orb_name = null;
	while (__iterator.hasNext ()) {
	    ORBSettings __setting = (ORBSettings)__iterator.next ();
	    __orb_name = __setting.getOrbName ();
	    __orb_name = this.removeUnsupportedPostfix (__orb_name);
	    /*
	      if (__orb_name.endsWith (ORBSettingsBundle.CTL_UNSUPPORTED)) {
	      __orb_name = __orb_name.substring 
	      (0, __orb_name.length () 
	      - (ORBSettingsBundle.CTL_UNSUPPORTED.length () + 1));
	      }
	    */
	    if (__orb_name.equals (__tmp_name))
		return __setting;
	}
	return null;
    }


    public ORBSettings findSettingByTag (BeanContext __context, String __tag) {
	if (DEBUG)
	    System.out.println ("findSettingByTag () <- " + __tag);
	Iterator __iterator = __context.iterator ();
	while (__iterator.hasNext ()) {
	    ORBSettings __setting = (ORBSettings)__iterator.next ();
	    if (__setting.getORBTag ().equals (__tag))
		return __setting;
	}
	return null;
    }


    public ORBSettings getSettingByName (String __name) {
	if (DEBUG) 
	    System.out.println ("CORBASupportSettings::getSettingByName (" + __name + ")"); // NOI18N
	//Thread.dumpStack ();
	if (!_M_loaded) {
	    //loadImpl ();
	    this.setBeans (this.getBeans ());
	}
	String __changed_name = this.removeUnsupportedPostfix (__name);
	String __tmp_name = null;
	java.lang.Object[] __settings = this.getBeans ();
	for (int __i = 0; __i < __settings.length; __i++) {
	    ORBSettings __setting = (ORBSettings)__settings[__i];
	    __tmp_name = this.removeUnsupportedPostfix (__setting.getName ());
	    if (__tmp_name.equals (__changed_name)) {
		return __setting;
	    }
	}
	//return null;
	// for future compatibility
	return this.getSettingByTag (__name);
    }


    public ORBSettings getSettingByTag (String __tag) {
	if (DEBUG)
	    System.out.println ("CORBASupportSettings::getSettingByTag (" + __tag + ")"); // NOI18N
	//Thread.dumpStack ();
	if (!_M_loaded) {
	    //loadImpl ();
	    this.setBeans (this.getBeans ());
	}
	java.lang.Object[] __settings = this.getBeans ();
	for (int __i = 0; __i < __settings.length; __i++) {
	    ORBSettings __setting = (ORBSettings)__settings[__i];
	    if (__setting.getORBTag ().equals (__tag)) {
		return __setting;
	    }
	}	
	return null;
    }


    public void cacheThrow () {
	//System.out.println ("CCS::cacheThrow ()"); // NOI18N
	_M_orb_tag_cache = null;
	_M_setting_cache = null;
    }


    public ORBSettings getActiveSetting () {
	//if (DEBUG)
	//System.out.println ("CORBASupportSettings::getActiveSetting ()"); // NOI18N
	//Thread.dumpStack ();
	if (!_M_loaded) {
	    //loadImpl ();
	    this.setBeans (this.getBeans ());
	}
	if (_M_orb_tag_cache != null) {
	    if (_M_orb_tag_cache.equals (this.getORBTag ()) && (_M_setting_cache != null)) {
		//if (DEBUG) {
		//System.out.println ("cache hit"); // NOI18N
		//System.out.println ("orb: " + _M_orb_tag_cache); // NOI18N
		//}
		return _M_setting_cache;
	    }
	}

	_M_orb_tag_cache = this.getORBTag ();

	//if (DEBUG)
	//System.out.println ("cache wasn't successfull"); // NOI18N

	java.lang.Object[] __settings = this.getBeans ();
	for (int __i = 0; __i < __settings.length; __i++) {
	    ORBSettings __setting = (ORBSettings)__settings[__i];
	    String __tag = __setting.getORBTag ();
	    //if (DEBUG)
	    //System.out.println (__name + " X " + this.getOrb ()); // NOI18N
	    if (__tag.equals (_M_orb_tag_cache)) {
		//System.out.println ("orb: " + this.getOrb ()); // NOI18N
		_M_setting_cache = __setting;
		return __setting;
	    }
	}
	//System.out.println ("orb: " + _M_orb_tag_cache); // NOI18N
	//Thread.dumpStack ();
	return null;
    }


    public synchronized void setORBTag (String __value) {
	//try {
	//boolean DEBUG=true;
	// Precondition
	if (__value == null) {
	    TopManager tm = TopManager.getDefault();
	    if (tm != null) {
	    	ErrorManager errManager = tm.getErrorManager();
		if (errManager != null) {
			errManager.log ("org::netbeans::modules::corba::settings::CORBASupportSettings::setORBTag precondition failed!\n"+  // No I18N
					"__value == null");  // No I18N
		}
	    }
	    return;
	}
	if (DEBUG)
	    System.out.println ("CORBASupportSettings::setORBTag (" + __value + ")"); // NOI18N
        String __old = _M_orb_tag;
	_M_orb_tag = __value;
	//setJavaTemplateTable ();
	ORBSettings __settings = this.getActiveSetting ();
	if (__settings != null)
	    __settings.setJavaTemplateTable ();
	this.firePropertyChange ("_M_orb_tag", __old, _M_orb_tag); // NOI18N
	this.cacheThrow ();

	String __orb_name = this.getOrb ();
	__orb_name = this.removeUnsupportedPostfix (__orb_name);
	/*
	  if (__orb_name.endsWith (ORBSettingsBundle.CTL_UNSUPPORTED))
	  __orb_name = this.getOrb ().substring
	  (0, this.getOrb ().length ()
	  - (ORBSettingsBundle.CTL_UNSUPPORTED.length () + 1));
	*/
	if (__settings == null
	    || (!(__orb_name.equals (__settings.getOrbName ())))) {
	    Iterator __iter = _S_implementations.iterator ();
	    while (__iter.hasNext ()) {
		ORBSettings __tmp = (ORBSettings)__iter.next ();
		if (this.getORBTag ().equals (__tmp.getORBTag ())) {
		    this.setOrb (__tmp.getOrbName ());
		    break;
		}
	    }
	}

	if (!_M_in_init) {
	    boolean __orb_hide = this.getActiveSetting ().hideGeneratedFiles ();
	    IDLDataLoader __loader = (IDLDataLoader)IDLDataLoader.findObject
		(IDLDataLoader.class, true);
	    boolean __old_hide = __loader.getHide ();
	    //System.out.println ("__orb_hide: " + __orb_hide); // NOI18N
	    //System.out.println ("__old_hide: " + __old_hide); // NOI18N
	    //if (__old_hide != __orb_hide)
	    __loader.setHide (__orb_hide);
	}

	//} catch (Exception __ex) {
	//__ex.printStackTrace ();
	//}
    }

    public String getORBTag () {
	return _M_orb_tag;
    }

    public String removeUnsupportedPostfix (String __name) {
	String __tmp = __name;
	//System.out.print ("nameWithoutUnsupportedPostfix () <- `" + __name + "'");
	if (__name.endsWith (ORBSettingsBundle.CTL_UNSUPPORTED)) {
	    __tmp = __name.substring
		(0, __name.length ()
		 - (ORBSettingsBundle.CTL_UNSUPPORTED.length () + 1));
	}
	//System.out.println ("  -> `" + __tmp + "'");
	return __tmp;
    }

}



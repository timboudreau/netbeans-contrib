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

package org.netbeans.modules.corba;

import java.util.Vector;
import java.beans.PropertyEditor;

import org.openide.TopManager;

import org.openide.loaders.*;
import org.openide.actions.*;
import org.openide.util.actions.*;
import org.openide.nodes.*;
import org.openide.cookies.OpenCookie;
import org.openide.actions.OpenAction;

// my
import org.openide.filesystems.FileUtil;


import org.netbeans.modules.corba.idl.node.*;
import org.netbeans.modules.corba.settings.CORBASupportSettings;
import org.netbeans.modules.corba.settings.ORBSettings;
import org.netbeans.modules.corba.settings.OrbPropertyEditor;

/**
 *
 *
 * @author Karel Gardas
 */

/** IDL Node implementation.
 * Leaf node, default action opens editor or instantiates template.
 * Icons redefined.
 */

public class IDLNode extends DataNode {

    //public static final boolean DEBUG = true;
    public static final boolean DEBUG = false;

    /** Icon base for the IDLNode node */
    public static final String IDL_ICON_BASE =
        "org/netbeans/modules/corba/settings/idl"; // NOI18N
    public static final String IDL_ERROR_ICON =
        "org/netbeans/modules/corba/settings/idl-error"; // NOI18N

    IDLDocumentChildren _M_children;
    IDLDataObject _M_ido;

    /** Default constructor, constructs node */
    public IDLNode (DataObject __data_object) throws Exception {
        //super(dataObject, Children.LEAF);
        //try {
        super (__data_object, new IDLDocumentChildren ((IDLDataObject) __data_object));
        _M_ido = (IDLDataObject)__data_object;
        this.setIconBase (IDL_ICON_BASE);
        _M_children = (IDLDocumentChildren)this.getChildren ();
        _M_children.setNode (this);
        //children.startParsing ();
        //} catch (ParseException e) {
        //setIconBase(IDL_ERROR_ICON);
        //}
        if (DEBUG)
            System.out.println ("IDLNode constructor!!!"); // NOI18N
    }

    /** Overrides default action from DataNode.
     * Instantiate a template, if isTemplate() returns true.
     * Opens otherwise.
     */
    public SystemAction getDefaultAction () {
        SystemAction result = super.getDefaultAction();
        return result == null ? SystemAction.get(OpenAction.class) : result;
    }

    public boolean canRename () {
        return true;
    }

    public IDLDataObject getIDLDataObject () {
        return (IDLDataObject) this.getDataObject ();
    }

    public void update () {
	//System.out.println ("IDLNode::update ();"); // NOI18N
        _M_children.setSrc (_M_ido.getSources ());
        _M_children.createKeys ();
    }

    // for better debuging
    /*
      public Node.Cookie getCookie (java.lang.Class clazz) {
      System.out.println ("IDLNode::getCookie (" + clazz + ");");
      return super.getCookie (clazz);
      }
    */
    protected Sheet createSheet () {
        Sheet sheet = super.createSheet();

        Sheet.Set ps = sheet.get(Sheet.PROPERTIES);
        ps = new Sheet.Set ();
	ps.setName("Compilation"); // NOI18N
	//ps.setDisplayName(Util.getString("PROP_executionSetName"));
	ps.setDisplayName (CORBASupport.IDL_COMPILATION);
	//ps.setShortDescription(Util.getString("HINT_executionSetName"));
	ps.setShortDescription (CORBASupport.IDL_COMPILATION_HINT);
	ps.put (new PropertySupport.ReadWrite (
					       "compilation", // NOI18N
					       String.class,
					       CORBASupport.ORB_FOR_COMPILATION,
					       CORBASupport.ORB_FOR_COMPILATION_HINT
					       ) {
		public Object getValue() {
		    String __setuped = getIDLDataObject().getOrbForCompilation ();
		    CORBASupportSettings __css 
			= (CORBASupportSettings) CORBASupportSettings.findObject
			(CORBASupportSettings.class, true);
		    ORBSettings __settings = __css.getSettingByName (__setuped);
		    if (__setuped != null && __settings != null)
			return new String (__settings.getName ());
		    else {
			return new String (__css.getActiveSetting ().getName ());
		    }
		}
		
		public void setValue (Object __value) {
		    if (__value instanceof String) {
			try {
			    String __name = (String)__value;
			    CORBASupportSettings __css = (CORBASupportSettings)
				CORBASupportSettings.findObject
				(CORBASupportSettings.class, true);
			    ORBSettings __setting = __css.getSettingByName (__name);
			    getIDLDataObject().setOrbForCompilation (__setting.getORBTag ());
			    return;
			}
			catch (java.io.IOException __ex) {
			    TopManager.getDefault ().notifyException (__ex);
			    //__ex.printStackTrace ();
			}
			catch (IllegalArgumentException __ex) {
			    TopManager.getDefault ().notifyException (__ex);
			    //__ex.printStackTrace ();
			}
		    }
		}
		
		public PropertyEditor getPropertyEditor() {
		    return new OrbPropertyEditor ();
		}
	    });
	
	/*  
	  ExecSupport es = (ExecSupport) getCookie (ExecSupport.class);
	  if (es != null)
	  es.addProperties (ps);
	  CompilerSupport cs = (CompilerSupport) getCookie (CompilerSupport.class);
	  if (cs != null)
	  cs.addProperties (ps);
	*/
        sheet.put(ps);

        return sheet;
    }

}


/*
 * <<Log>>
 *  15   Gandalf   1.14        11/4/99  Karel Gardas    - update from CVS
 *  14   Gandalf   1.13        11/4/99  Karel Gardas    update from CVS
 *  13   Gandalf   1.12        10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  12   Gandalf   1.11        10/5/99  Karel Gardas    update from CVS
 *  11   Gandalf   1.10        10/1/99  Karel Gardas    updates from CVS
 *  10   Gandalf   1.9         8/3/99   Karel Gardas    
 *  9    Gandalf   1.8         7/10/99  Karel Gardas    
 *  8    Gandalf   1.7         6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  7    Gandalf   1.6         5/28/99  Karel Gardas    
 *  6    Gandalf   1.5         5/28/99  Karel Gardas    
 *  5    Gandalf   1.4         5/22/99  Karel Gardas    
 *  4    Gandalf   1.3         5/15/99  Karel Gardas    
 *  3    Gandalf   1.2         5/8/99   Karel Gardas    
 *  2    Gandalf   1.1         4/24/99  Karel Gardas    
 *  1    Gandalf   1.0         4/23/99  Karel Gardas    
 * $
 */




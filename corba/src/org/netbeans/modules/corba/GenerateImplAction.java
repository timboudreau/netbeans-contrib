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

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.*;
import org.openide.nodes.Node;

import org.netbeans.modules.corba.utils.AssertionException;

/** Actions for IDL Node.
*
* @author Karel Gardas
* @version 0.01, May 21, 1999
*/

public class GenerateImplAction extends CookieAction {

    static final long serialVersionUID =7123829348277092914L;

    //private String name = NbBundle.getBundle (CORBASupport.class).getString ("CTL_GenerateImpl");
    private String _M_generate = NbBundle.getBundle (CORBASupport.class).getString
                              ("ACT_GENERATE"); // NOI18N
    private String _M_update_and_generate = NbBundle.getBundle 
	(CORBASupport.class).getString ("ACT_UPDATE_AND_GENERATE"); // NOI18N
    private String _M_update = NbBundle.getBundle (CORBASupport.class).getString
                            ("ACT_UPDATE"); // NOI18N

    private String _M_name = "uninitialized generate implementation action";

    /** @return set of needed cookies */
    protected Class[] cookieClasses () {
        return new Class[] { IDLNodeCookie.class };
    }

    /** @return false */
    protected boolean surviveFocusChange () {
        return false;
    }

    /** @return exactly one */
    protected int mode () {
        return MODE_EXACTLY_ONE;
    }

    /** Human presentable name of the action. This should be
    * presented as an item in a menu.
    * @return the name of the action
    */
    public String getName() {
        //return NbBundle.getBundle (CORBASupport.class).getString ("CTL_GenerateImpl");
        //System.out.println ("getName () -> " + name); // NOI18N
        return _M_name;
    }

    /** Help context where to find more about the action.
    * @return the help context for this action
    */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP; // [PENDING]
    }

    /** Resource name for the icon.
    * @return resource name
    */
    protected String iconResource () {
        return "/org/openide/resources/actions/empty.gif"; // no icon // NOI18N
    }


    protected boolean enable (Node[] __activated_nodes) {
        //name = "Update Implementations"; // NOI18N
	//System.out.println ("GenerateImplAction::enable (" + activatedNodes + ");");
	String __old_name = _M_name;
	boolean __generate = false;
	boolean __update_and_generate = false;
	boolean __update = false;
        try {
	    for (int __i=0; __i<__activated_nodes.length; __i++) {
		IDLDataObject __ido = (IDLDataObject)__activated_nodes[__i].getCookie 
		    (IDLDataObject.class);
		if (__ido == null)
		    throw new AssertionException ("__ido == null");
		int __value = __ido.hasGeneratedImplementation ();
		if (__value == 0) {
		    //_M_name = _M_generate;
		    __generate = true;
		}
		if (__value == 1) {
		    //_M_name = _M_update_and_generate;
		    __update_and_generate = true;
		}
		if (__value == 2) {
		    //_M_name = _M_update;
		    __update = true;
		}
	    }
        } catch (Exception __ex) {
            //__ex.printStackTrace ();
	    //System.out.println ("-> false");
            return false;
        } finally {
	    if (__generate) {
		//System.out.println ("generate...");
		_M_name = _M_generate;
	    }
	    if (__update) {
		//System.out.println ("update...");
		_M_name = _M_update;
	    }
	    if (__generate && __update && !__update_and_generate) {
		//System.out.println ("__generate && __update && !__update_and_generate");
		__update_and_generate = true;
	    }
	    if (__update_and_generate) {
		//System.out.println ("__update_and_generate");
		_M_name = _M_update_and_generate;
	    }
	    if (!__update_and_generate && !__update && !__generate) {
		// exception for first IDO
		_M_name = _M_update_and_generate;
	    }
	    this.firePropertyChange ("name", __old_name, _M_name);
	    //this.firePropertyChange ("name", __old_name, _M_name);
	    //this.firePropertyChange ("name", __old_name, _M_name);
	}
	//System.out.println ("-> true");
        return true;
    }

    /**
    * Standart perform action extended by actually activated nodes.
    * @see CallableSystemAction#performAction
    *
    * @param activatedNodes gives array of actually activated nodes.
    */
    protected void performAction (final Node[] __activated_nodes) {
	for (int __i=0; __i<__activated_nodes.length; __i++) {
	    IDLNodeCookie __unc 
		= (IDLNodeCookie)__activated_nodes[__i].getCookie(IDLNodeCookie.class);
	    if (__unc != null) {
		__unc.GenerateImpl 
		    ((IDLDataObject)__activated_nodes[__i].getCookie (IDLDataObject.class));
	    }
	}
    }
}

/*
 * <<Log>>
 *  13   Gandalf   1.12        2/8/00   Karel Gardas    
 *  12   Gandalf   1.11        11/27/99 Patrik Knakal   
 *  11   Gandalf   1.10        11/4/99  Karel Gardas    - update from CVS
 *  10   Gandalf   1.9         11/4/99  Karel Gardas    update from CVS
 *  9    Gandalf   1.8         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  8    Gandalf   1.7         10/1/99  Karel Gardas    updates from CVS
 *  7    Gandalf   1.6         8/3/99   Karel Gardas    
 *  6    Gandalf   1.5         7/10/99  Karel Gardas    
 *  5    Gandalf   1.4         6/9/99   Ian Formanek    Fixed resources for 
 *       package change
 *  4    Gandalf   1.3         6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  3    Gandalf   1.2         5/28/99  Karel Gardas    
 *  2    Gandalf   1.1         5/28/99  Karel Gardas    
 *  1    Gandalf   1.0         5/22/99  Karel Gardas    
 * $
 */

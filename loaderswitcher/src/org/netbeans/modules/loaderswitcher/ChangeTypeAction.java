/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.loaderswitcher;

import org.openide.nodes.Node;
import org.openide.loaders.DataObject;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CookieAction;
import org.openide.util.NbBundle;

/**
*  Changes the type of a DataObject using a user dialog.
* @author Jaroslav Tulach
*/
public class ChangeTypeAction extends CookieAction {
    /** generated Serialized Version UID */
    static final long serialVersionUID = 1352346576761226839L;

    /* Returns false - action should be disabled when a window with no
    * activated nodes is selected.
    *
    * @return false do not survive the change of focus
    */
    protected boolean surviveFocusChange () {
        return false;
    }

    /* Human presentable name of the action. This should be
    * presented as an item in a menu.
    * @return the name of the action
    */
    public String getName () {
        return org.openide.util.NbBundle.getMessage(ChangeTypeAction.class, "Change_object_type");
    }

    /* Help context where to find more about the action.
    * @return the help context for this action
    */
    public HelpCtx getHelpCtx () {
        return new HelpCtx (ChangeTypeAction.class);
    }

    /* The resource string to our icon.
    * @return the icon resource string
    */
    protected String iconResource () {
        return "org/netbeans/modules/loaderswitcher/ChangeTypeAction.gif"; // NOI18N
    }

    /* @return the mode of action. */
    protected int mode() {
        return MODE_EXACTLY_ONE;
    }

    /* Creates a set of classes that are tested by this cookie.
    * Here only HtmlDataObject class is tested.
    *
    * @return list of classes the that this cookie tests
    */
    protected Class[] cookieClasses () {
        return new Class[] { org.openide.loaders.DataObject.class };
    }

    /* Actually performs the action.
    * Calls edit on all activated nodes which supports
    * HtmlDataObject cookie.
    */
    protected void performAction (final Node[] activatedNodes) {
        for (int i = 0; i < activatedNodes.length; i++) {
            DataObject es = (DataObject)activatedNodes[i].getCookie(DataObject.class);
            if (es != null) {
                ObjectType.convert (es);
            }
        }
    }
}

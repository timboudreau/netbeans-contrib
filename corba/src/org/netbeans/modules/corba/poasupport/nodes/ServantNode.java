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

package org.netbeans.modules.corba.poasupport.nodes;

import java.io.IOException;
import java.awt.Component;

import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.netbeans.modules.corba.settings.POASettings;
import org.netbeans.modules.corba.poasupport.*;
import org.netbeans.modules.corba.poasupport.tools.*;

/** A simple node with no children.
 *
 * @author Dusan Balek
 */
public class ServantNode extends POAMemberElementNode {
    
    public static final String ICON_BASE =
    "/org/netbeans/modules/corba/poasupport/resources/ServantNodeIcon"; // NOI18N
    
    public ServantNode(ServantElement _element) {
        super (_element);
        if (_element.getIDAssignmentMode() == POASettings.SERVANT_WITH_SYSTEM_ID)
            super.setName (_element.getIDVarName());
        else
            super.setName (_element.getObjID());
        setIconBase (ICON_BASE);
    }
    
    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
        // When you have help, change to:
        // return new HelpCtx (ServantNode.class);
    }
    
    public ServantElement getServantElement () {
        return (ServantElement)element;
    }
    
    public void setName (String nue) {
        if (isWriteable()) {
            if (getServantElement().getIDAssignmentMode() == POASettings.SERVANT_WITH_SYSTEM_ID) {
                if (POAChecker.checkServantIDVarName(nue, getServantElement(), true))
                    getServantElement().setIDVarName(nue);
            }
            else {
                if (POAChecker.checkServantID(nue, getServantElement(), true))
                    getServantElement().setObjID(nue);
            }
        }
    }
    
    public void destroy () throws IOException {
        ((POANode)getParentNode()).getPOAElement().removeServant(getServantElement());
        super.destroy ();
    }
    
    // Create a property sheet:
    protected Sheet createSheet () {
        Sheet sheet = super.createSheet ();
        Sheet.Set ps = sheet.get (Sheet.PROPERTIES);
        if (getServantElement().getIDAssignmentMode() == POASettings.SERVANT_WITH_SYSTEM_ID)
            ps.put (new PropertySupport.ReadWrite ("IDVarName", String.class, POASupport.getString("LBL_POAMemberSheet_IDVarName"), POASupport.getString("MSG_POAMemberSheet_IDVarName")) { // NOI18N
                public Object getValue () {
                    return getServantElement().getIDVarName ();
                }
                public void setValue(Object value) {
                    if (POAChecker.checkServantIDVarName((String)value, getServantElement(), false))
                        getServantElement().setIDVarName((String)value);
                }
                public boolean canWrite() {
                    return isWriteable();
                }
            });
        else
            ps.put (new PropertySupport.ReadWrite ("objID", String.class, POASupport.getString("LBL_POAMemberSheet_ObjID"), POASupport.getString("MSG_POAMemberSheet_ObjID")) { // NOI18N
                public Object getValue () {
                    return getServantElement().getObjID ();
                }
                public void setValue(Object value) {
                   if (POAChecker.checkServantID((String)value, getServantElement(), false))
                       getServantElement().setObjID((String)value);
                }
                public boolean canWrite() {
                    return isWriteable();
                }
            });
        return sheet;
    }
    
    public Component getCustomizer () {
        return new POAMemberCustomizer (getServantElement());
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent p1) {
        if (p1.getSource() != getServantElement())
            return;
        if (p1.getPropertyName().equals(ServantElement.PROP_OBJ_ID) || p1.getPropertyName().equals(ServantElement.PROP_ID_VAR_NAME)) {
            super.setName((String)p1.getNewValue());
            return;
        }
        super.firePropertyChange(p1.getPropertyName(), p1.getOldValue(), p1.getNewValue());

    }
    
    // RECOMMENDED - handle cloning specially (so as not to invoke the overhead of FilterNode):
    /*
    public Node cloneNode () {
        // Try to pass in similar constructor params to what you originally got:
        return new ServantNode ();
    }
     */
    
    /*
    public Transferable clipboardCopy () {
        // Add to, do not replace, the default node copy flavor:
        ExTransferable et = ExTransferable.create (super.clipboardCopy ());
        et.put (new ExTransferable.Single (DataFlavor.stringFlavor) {
                protected Object getData () {
                    return ServantNode.this.getDisplayName ();
                }
            });
        return et;
    }
    public Transferable clipboardCut () {
        // Add to, do not replace, the default node cut flavor:
        ExTransferable et = ExTransferable.create (super.clipboardCut ());
        // This is not so useful because this node will not be destroyed afterwards
        // (it is up to the paste type to decide whether to remove the "original",
        // and it is not safe to assume that getData will only be called once):
        et.put (new ExTransferable.Single (DataFlavor.stringFlavor) {
                protected Object getData () {
                    return ServantNode.this.getDisplayName ();
                }
            });
        return et;
    }
     */
    
}

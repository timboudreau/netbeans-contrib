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

package org.netbeans.modules.corba.poasupport;

import org.netbeans.modules.corba.settings.POASettings;
import org.netbeans.modules.corba.poasupport.tools.POAChecker;

/*
 * @author Dusan Balek
 */

public class ServantElement extends POAMemberElement {

    public static String PROP_OBJ_ID = "ObjID"; // NOI18N
    public static String PROP_ID_VAR_NAME = "IDVarName"; // NOI18N

    private String objID = null;
    private String idVarName = null;
    private String idAssignmentMode;

    public ServantElement(POAElement _parentPOA, boolean _writeable) {
        super (_parentPOA, _writeable);
        String disabledMode = POAChecker.checkDisabledServantActivation(_parentPOA.getPolicies());
        if (disabledMode.equals(POASettings.SERVANT_WITH_USER_ID)) {
            idVarName = getDefaultIDVarName();
            idAssignmentMode = POASettings.SERVANT_WITH_SYSTEM_ID;
        }
        else {
            objID = getDefaultObjID();
            idAssignmentMode = POASettings.SERVANT_WITH_USER_ID;
        }
    }
    
    public String getIDAssignmentMode () {
        return idAssignmentMode;
    }
    
    public String getDefaultVarName() {
        int counter = 1;
        while (!canUseAsNewVarName(POASupport.getPOASettings().getDefaultServantVarName() + String.valueOf(counter)))
            counter++;
        return POASupport.getPOASettings().getDefaultServantVarName() + String.valueOf(counter);
    }

    public boolean canUseAsServantID(String id) {
        return getParentPOA().canUseIDForServant(id, this);
    }

    public String getDefaultObjID() {
        int counter = 1;
        while (!canUseAsServantID(POASupport.getPOASettings().getDefaultServantId() + String.valueOf(counter)))
            counter++;
        return POASupport.getPOASettings().getDefaultServantId() + String.valueOf(counter);
    }

    public String getObjID () {
        return objID;
    }
        
    public void setObjID (String _objID) {
        if (idAssignmentMode != POASettings.SERVANT_WITH_USER_ID)
            return;
        if (!_objID.equals(objID)) {
            String oldID = objID;
            objID = _objID;
            firePropertyChange(new java.beans.PropertyChangeEvent(this, PROP_OBJ_ID, oldID, objID));
        }
    }

    public String getDefaultIDVarName() {
        int counter = 1;
        while (!canUseAsNewVarName(POASupport.getPOASettings().getDefaultServantIdVarName() + String.valueOf(counter)))
            counter++;
        return POASupport.getPOASettings().getDefaultServantIdVarName() + String.valueOf(counter);
    }

    public String getIDVarName () {
        return idVarName;
    }
        
    public void setIDVarName (String _name) {
        if (idAssignmentMode != POASettings.SERVANT_WITH_SYSTEM_ID)
            return;
        if (!_name.equals(idVarName)) {
            String oldName = idVarName;
            idVarName = _name;
            firePropertyChange(new java.beans.PropertyChangeEvent(this, PROP_ID_VAR_NAME, oldName, idVarName));
        }
    }
}

/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
        String disabledMode = POAChecker.checkDisabledServantActivation(_parentPOA, _parentPOA.getPolicies());
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
        POASettings settings;
        String _tag = getParentPOA().getRootPOA().getORBTag();
        if (_tag != null)
            settings = POASupport.getCORBASettings().getSettingByTag(_tag).getPOASettings();
        else
            settings = POASupport.getPOASettings();
        while (!canUseAsNewVarName(settings.getDefaultServantVarName() + String.valueOf(counter)))
            counter++;
        return settings.getDefaultServantVarName() + String.valueOf(counter);
    }

    public boolean canUseAsServantID(String id) {
        return getParentPOA().canUseIDForServant(id, this);
    }

    public String getDefaultObjID() {
        int counter = 1;
        POASettings settings;
        String _tag = getParentPOA().getRootPOA().getORBTag();
        if (_tag != null)
            settings = POASupport.getCORBASettings().getSettingByTag(_tag).getPOASettings();
        else
            settings = POASupport.getPOASettings();
        while (!canUseAsServantID(settings.getDefaultServantId() + String.valueOf(counter)))
            counter++;
        return settings.getDefaultServantId() + String.valueOf(counter);
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
        POASettings settings;
        String _tag = getParentPOA().getRootPOA().getORBTag();
        if (_tag != null)
            settings = POASupport.getCORBASettings().getSettingByTag(_tag).getPOASettings();
        else
            settings = POASupport.getPOASettings();
        while (!canUseAsNewVarName(settings.getDefaultServantIdVarName() + String.valueOf(counter)))
            counter++;
        return settings.getDefaultServantIdVarName() + String.valueOf(counter);
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

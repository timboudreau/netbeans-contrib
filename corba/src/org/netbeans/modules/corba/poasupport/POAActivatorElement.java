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

/*
 * @author Dusan Balek
 */

public class POAActivatorElement extends POAMemberElement {
    
    public POAActivatorElement(POAElement _parentPOA, boolean _writeable) {
        super(_parentPOA, _writeable);
    }
    
    public String getDefaultVarName() {
        int counter = 1;
        POASettings settings;
        String _tag = getParentPOA().getRootPOA().getORBTag();
        if (_tag != null)
            settings = POASupport.getCORBASettings().getSettingByTag(_tag).getPOASettings();
        else
            settings = POASupport.getPOASettings();
        while (!canUseAsNewVarName(settings.getDefaultPOAActivatorVarName() + String.valueOf(counter)))
            counter++;
        return settings.getDefaultPOAActivatorVarName() + String.valueOf(counter);
    }
}

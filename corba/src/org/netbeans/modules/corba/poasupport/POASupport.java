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

import java.util.ResourceBundle;
import org.openide.util.NbBundle;
import org.netbeans.modules.corba.settings.*;
/*
 *
 * @author Dusan Balek
 */

public class POASupport implements java.beans.PropertyChangeListener {
    
    private static ResourceBundle bundle = NbBundle.getBundle (POASupport.class);
    private static POASupport instance;

    static {
        instance = new POASupport((CORBASupportSettings) CORBASupportSettings.findObject(CORBASupportSettings.class, true));
    }

    public static String getString(String str) {
        return bundle.getString(str);
    }
    
    public static POASettings getPOASettings() {
        return instance.settings;
    }

    public static CORBASupportSettings getCORBASettings() {
        return instance.corbaSettings;
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent p1) {
        if (p1.getPropertyName().equals("_M_orb_tag")) { // NOI18N
            settings = corbaSettings.getActiveSetting().getPOASettings();
        }
    }

    private POASettings settings;
    private CORBASupportSettings corbaSettings;
    
    private POASupport(CORBASupportSettings _corbaSettings) {
        corbaSettings = _corbaSettings;
        settings = _corbaSettings.getActiveSetting().getPOASettings();
        _corbaSettings.addPropertyChangeListener(this);
    }
}

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

package org.netbeans.modules.rmi.activation.settings;

import java.beans.*;
import java.util.Arrays;
import java.util.HashSet;
import org.netbeans.modules.rmi.activation.ActivationSystemItem;

import org.openide.options.SystemOption;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/** Options for something or other.
 *
 * @author  Jan Pokorsky
 */
public class RMIActivationSettings extends SystemOption {

    static final long serialVersionUID = 1451198855962856446L;

    public static final String PROP_REFRESH_TIME = "refreshTime";
    
    public static final String PROP_ACTIVATION_SYSTEM_ITEMS = "activationSystemItems";
    
    /** Default time between two updates. */
    public static final int DEFAULT_REFRESH_TIME = 60000;

    // No constructor please!

    /** Defines whether the option is project or global specific.
    * @return true, the option is global.
    */
    public boolean isGlobal() {
        return true;
    }

    public String displayName () {
        return NbBundle.getMessage (RMIActivationSettings.class, "LBL_ActivationSettings");
    }

    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }

    /** Default instance of this system option, for the convenience of associated classes. */
    public static RMIActivationSettings getDefault () {
        return (RMIActivationSettings) findObject (RMIActivationSettings.class, true);
    }

    /** Getter for property refreshTime.
     * @return Value of property refreshTime.
 */
    public int getRefreshTime() {
        Integer time = (Integer) getProperty(PROP_REFRESH_TIME);
        return (time != null)? time.intValue(): DEFAULT_REFRESH_TIME;
    }
    
    /** Setter for property refreshTime.
     * @param refreshTime New value of property refreshTime.
 */
    public void setRefreshTime(int refreshTime) throws PropertyVetoException {
        if (refreshTime < 0) refreshTime = 0;
        putProperty(PROP_REFRESH_TIME, new Integer(refreshTime), true);
    }
    
    public void setActivationSystemItems(ActivationSystemItem[] items) {
    
    }
    
    public ActivationSystemItem[] getActivationSystemItems() {
        return new ActivationSystemItem[0];
    }
    
    public void addActivationSystemItem(ActivationSystemItem as) {
        HashSet set = new HashSet(Arrays.asList(getActivationSystemItems()));
        set.add(as);
        setActivationSystemItems((ActivationSystemItem[]) set.toArray(new ActivationSystemItem[set.size()]));
    }
    
    public void removeActivationSystemItem(ActivationSystemItem as) {
        HashSet set = new HashSet(Arrays.asList(getActivationSystemItems()));
        set.remove(as);
        setActivationSystemItems((ActivationSystemItem[]) set.toArray(new ActivationSystemItem[set.size()]));
    }
    
}

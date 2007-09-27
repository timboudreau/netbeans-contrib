/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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

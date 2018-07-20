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

package org.netbeans.modules.vcscore.grouping;

import java.util.Hashtable;
import java.util.HashSet;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Properties;
import java.io.*;

import org.openide.util.NbBundle;
import org.openide.options.SystemOption;
import org.openide.util.actions.SystemAction;
import org.openide.util.Utilities;
import org.openide.util.SharedClassObject;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;


/** Options for VcsGroup feature
*
* @author Milos Kleint
*/
public class VcsGroupSettings extends SystemOption {


    public static final String PROP_AUTO_ADDITION = "autoAddition";//NOI18N
    public static final String PROP_SHOW_LINKS = "showLinks";//NOI18N
    public static final String PROP_DISABLE_GROUPS = "disableGroups";
    
    public static final int ADDITION_MANUAL = 0;
    public static final int ADDITION_ASK = 2;
    public static final int ADDITION_TO_DEFAULT = 1;

    
    
    static final long serialVersionUID = -4620483329253274979L;

    protected void initialize () {
        super.initialize();
        setAutoAddition(0);
        setDisableGroups(false);
        setShowLinks(false);
    }
    /** human presentable name */
    public String displayName() {
        return NbBundle.getBundle(VcsGroupSettings.class).getString("CTL_VcsGroup_settings"); // NOI18N
    }


    

    /** Getter for property showLinks.
     * @return Value of property showLinks.
     */
    public boolean isShowLinks() {
        return ((Boolean)getProperty(PROP_SHOW_LINKS)).booleanValue();
    }
    
    /** Setter for property showLinks.
     * @param showLinks New value of property showLinks.
     */
    public void setShowLinks(boolean show) {
        putProperty(PROP_SHOW_LINKS, show ? Boolean.TRUE : Boolean.FALSE, true);
    }
    
    /** Getter for property autoAddition.
     * @return Value of property autoAddition.
     */
    public int getAutoAddition() {
        return ((Integer)getProperty(PROP_AUTO_ADDITION)).intValue();
    }
    
    /** Setter for property autoAddition.
     * @param autoAddition New value of property autoAddition.
     */
    public void setAutoAddition(int autoAddition) {
        putProperty(PROP_AUTO_ADDITION, new Integer(autoAddition), true);
    }
    
    /** Getter for property disableGroups.
     * @return Value of property disableGroups.
     */
    public boolean isDisableGroups() {
        return ((Boolean)getProperty(PROP_DISABLE_GROUPS)).booleanValue();
    }
    
    /** Setter for property disableGroups.
     * @param disableGroups New value of property disableGroups.
     */
    public void setDisableGroups(boolean disableGroups) {
        putProperty(PROP_DISABLE_GROUPS, disableGroups ? Boolean.TRUE : Boolean.FALSE, true);
    }
    
}

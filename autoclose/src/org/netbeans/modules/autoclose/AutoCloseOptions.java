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
package org.netbeans.modules.autoclose;

import java.util.Collections;
import java.util.Map;
import org.openide.options.SystemOption;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/** Options for something or other.
 *
 * @author Jan Lahoda
 */
public class AutoCloseOptions extends SystemOption {

    private static final long serialVersionUID = 1L;

    public static final String PROP_MAX_OPENED_FILES = "maxOpenedFiles";
    public static final String PROP_AUTO_CLOSE_ENABLED = "autoCloseEnabled";
    public static final String PROP_TIMESTAMP_MAP = "timestampMap";
    
    // No constructor please!
    
    protected void initialize() {
        super.initialize();
        // If you have more complex default values which might require
        // other parts of the module to already be installed, do not
        // put them here; e.g. make the getter return them as a
        // default if getProperty returns null. (The class might be
        // initialized partway through module installation.)
        setMaxOpenedFiles(15);
        setAutoCloseEnabled(true);
    }
    
    public String displayName() {
        return NbBundle.getMessage(AutoCloseOptions.class, "LBL_settings");
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you provide context help then use:
        // return new HelpCtx(NBLaTeXSourceFactoryStorageSettings.class);
    }
    
    /** Default instance of this system option, for the convenience of associated classes. */
    public static AutoCloseOptions getDefault() {
        return (AutoCloseOptions)findObject(AutoCloseOptions.class, true);
    }
    
    public boolean isAutoCloseEnabled() {
        return ((Boolean) getProperty(PROP_AUTO_CLOSE_ENABLED)).booleanValue();
    }
    
    public void setAutoCloseEnabled(boolean value) {
        putProperty(PROP_AUTO_CLOSE_ENABLED, Boolean.valueOf(value));
    }
    
    public int getMaxOpenedFiles() {
        return ((Integer) getProperty(PROP_MAX_OPENED_FILES)).intValue();
    }
    
    public void setMaxOpenedFiles(int value) {
        // Automatically fires property changes if needed etc.:
        putProperty(PROP_MAX_OPENED_FILES, new Integer(value), true);
        // If you need to start some service, or do something else with
        // an external effect, you should not use putProperty(...): keep
        // the data as a private static member, and manually modify that
        // variable and use firePropertyChange(...). Because if putProperty(...)
        // is used, getters and setters will be skipped during project save
        // and restore, which may cause problems.
    }
    
    public Map getTimestampMap() {
        Map result = (Map) getProperty(PROP_TIMESTAMP_MAP);
        
        if (result == null) {
            return Collections.EMPTY_MAP;
        }
        
        return result;
    }
    
    public void setTimestampMap(Map m) {
        putProperty(PROP_TIMESTAMP_MAP, m);
    }
    
}

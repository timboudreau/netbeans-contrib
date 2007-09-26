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
 *
 * Contributor(s): Jesse Glick, Michael Ruflin
 */

package org.netbeans.modules.sysprops;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import javax.swing.Action;
import org.openide.actions.NewAction;
import org.openide.actions.OpenLocalExplorerAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.ToolsAction;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.actions.SystemAction;
import org.openide.util.NbBundle;

/** Root Node for all SystemProperties.
 *
 * @author Michael Ruflin, Jesse Glick
 */
public class SystemPropertiesNode extends PropertyNode {
    
    /**
     * Creates a new SystemPropertiesNode.
     */
    public SystemPropertiesNode() {
        super(null, listAllProperties());
        
        // Set FeatureDescriptor stuff:
        setDisplayName(NbBundle.getMessage(SystemPropertiesNode.class, "LBL_AllPropsNode"));
        setShortDescription(NbBundle.getMessage(SystemPropertiesNode.class, "HINT_AllPropsNode"));
    }
    
    /** Get a list of all system properties.
     * @return all of them
     */
    public static List/*<String>*/ listAllProperties() {
        List l = new ArrayList();
        Iterator it = new TreeSet(System.getProperties().keySet()).iterator();
        while (it.hasNext()) {
            String prop = (String) it.next();
            // Exclude environment variables here.
            if (! prop.startsWith("Env-") && ! prop.startsWith("env-")) {
                l.add(prop);
            }
        }
        return l;
    }
    
    /**
     * Returns an Array of SystemActions allowed by this Node.
     * @return a specialized set of actions
     */
    public Action[] getActions(boolean context) {
        return new Action[] {
            SystemAction.get(RefreshPropertiesAction.class),
            null,
            SystemAction.get(NewAction.class),
            null,
            SystemAction.get(ToolsAction.class),
            null,
            SystemAction.get(PropertiesAction.class),
        };
    }
    
    /**
     * Clones this Node.
     * @return a clone
     */
    public Node cloneNode() {
        return new SystemPropertiesNode();
    }
    
    /** Get the property sheet.
     * @return the usual, plus environment variables
     */
    protected Sheet createSheet() {
        Sheet s = super.createSheet();
        Sheet.Set ss = new Sheet.Set();
        ss.setName("envvars");
        ss.setDisplayName(NbBundle.getMessage(SystemPropertiesNode.class, "LBL_envvars_tab"));
        ss.setShortDescription(NbBundle.getMessage(SystemPropertiesNode.class, "HINT_envvars_tab"));
        Iterator it = new TreeSet(System.getProperties().keySet()).iterator();
        while (it.hasNext()) {
            String prop = (String) it.next();
            // List environment variables (only the ones with proper case).
            if (prop.startsWith("Env-")) {
                String env = prop.substring(4); // cut off Env-
                ss.put(new EnvVarProp(env));
            }
        }
        s.put(ss);
        return s;
    }

    public String getName() {
        return "SystemPropertiesNode";
    }
    
    public boolean canRename() {
        return false;
    }

    /** Property representing one environment variable. */
    private static final class EnvVarProp extends PropertySupport.ReadOnly {
        public EnvVarProp(String env) {
            super(env, String.class, env, NbBundle.getMessage(SystemPropertiesNode.class, "HINT_env_value"));
        }
        public Object getValue() {
            return System.getProperty("Env-" + getName());
        }
    }
    
}

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

package org.netbeans.modules.jndi;

import java.util.Hashtable;
import javax.naming.NameClassPair;


/** This class is an icon holder.
 *
 * @author Tomas Zezula
 */
abstract class JndiIcons extends Object {

    /** The directory with Jndi icons*/
    public final static String ICON_BASE = "org/netbeans/modules/jndi/resources/";
    /** The array of pairs (Class,IconName)*/
    private final static String[] defaultIcons = {"*","interface",
            JndiRootNode.NB_ROOT,"jndi",
            JndiProvidersNode.DRIVERS,"providerfolder",
            ProviderNode.DRIVER,"driver",
            ProviderNode.DISABLED_DRIVER,"disableddriver",
            JndiDisabledNode.DISABLED_CONTEXT_ICON,"disabled",
            WaitNode.WAIT_ICON,"wait",
            "javax.naming.Context","folder",
            "java.io.File","file"};
    /** Hashtable with Class name as key, Icon name as value*/
    private static Hashtable icontable;


    /** Returns icon name for string containig the name of Class
     *  @param name  name oc Class
     *  @return name of icon
     */
    public static String getIconName(String name) {
        String iconname = null;

        if (icontable == null) {
            lazyInitialize();
        }
        if (name != null) {
            iconname = (String) icontable.get(name);
        }
        if (iconname != null) {
            return iconname;
        } else {
            return (String) icontable.get("*");
        }
    }

    /** Returns the name of icon for NameClassPair
     *  @param name  NameClassPair for which the icon should be returned  
     *  @return String name of icon
     */
    public static String getIconName(NameClassPair name) {

        String iconname;

        if (icontable == null) {
            lazyInitialize();
        }

        if (name == null) {
            return (String) icontable.get("*");
        }

        iconname = (String) icontable.get(name.getClassName());
        if (iconname != null) {
            return iconname;
        } else {
            return (String) icontable.get("*");
        }
    }

    /**lazy_initialization
     */
    private static void lazyInitialize() {
        icontable = new Hashtable();
        for (int i=0; i < defaultIcons.length; i += 2) {
            icontable.put(defaultIcons[i], defaultIcons[i+1]);
        }
    }
}

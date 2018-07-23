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

package org.netbeans.modules.metrics.options;

import org.openide.options.SystemOption;
import org.openide.util.NbBundle;

/** Options for CBO metric.
 *
 * @author  tball
 */
public class CBOMetricSettings extends SystemOption implements MetricSettings {

    private static final long serialVersionUID = 6187796499098479549L;

    public static final String PROP_DEFAULT_WARNING =
        "CBOMetric.default_warning_value";
    public static final String PROP_DEFAULT_ERROR =
        "CBOMetric.default_error_value";
    public static final String PROP_INCLUDE_JDK_CLASSES = 
        "CBOMetric.include_JDK_classes";
    public static final String PROP_INCLUDE_OPENIDE_CLASSES = 
        "CBOMetric.include_OpenIDE_classes";

    /** Singleton instance */
    private static CBOMetricSettings singleton;

    protected void initialize () {
        super.initialize ();
        setWarningLevel(5);
        setErrorLevel(10);
        setIncludeJDKClasses(true);
        setIncludeOpenIDEClasses(true);
    }

    public String displayName () {
        return NbBundle.getMessage(CBOMetricSettings.class, "LBL_CBOMetricSettings");
    }

    /** Default instance of this system option. */
    public static CBOMetricSettings getDefault() {
        if (singleton == null) {
            singleton = (CBOMetricSettings) 
                findObject(CBOMetricSettings.class, true);
        }
        return singleton;
    }

    public int getWarningLevel () {
        return ((Integer)getProperty(PROP_DEFAULT_WARNING)).intValue();
    }

    public void setWarningLevel (int value) {
        // Automatically fires property changes if needed etc.:
        putProperty(PROP_DEFAULT_WARNING, new Integer(value), true);
    }

    public int getErrorLevel () {
        return ((Integer)getProperty(PROP_DEFAULT_ERROR)).intValue();
    }

    public void setErrorLevel (int value) {
        putProperty(PROP_DEFAULT_ERROR, new Integer(value), true);
    }

    public boolean includeJDKClasses() {
        return ((Boolean)getProperty(PROP_INCLUDE_JDK_CLASSES)).booleanValue();
    }

    public void setIncludeJDKClasses(boolean value) {
        putProperty(PROP_INCLUDE_JDK_CLASSES, value ? Boolean.TRUE : Boolean.FALSE, true);
    }

    public boolean includeOpenIDEClasses() {
        return ((Boolean)getProperty(PROP_INCLUDE_OPENIDE_CLASSES)).booleanValue();
    }

    public void setIncludeOpenIDEClasses(boolean value) {
        putProperty(PROP_INCLUDE_OPENIDE_CLASSES, value ? Boolean.TRUE : Boolean.FALSE, true);
    }

    // this metric doesn't support per-method values
    public int getMethodWarningLevel() { return -1; }
    public void setMethodWarningLevel(int value) {}
    public int getMethodErrorLevel() { return -1; }
    public void setMethodErrorLevel(int value) {}
}

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

package org.netbeans.modules.j2ee.jetty.customizer;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.util.NbBundle;
import org.netbeans.modules.j2ee.deployment.common.api.J2eeLibraryTypeProvider;
import org.netbeans.modules.j2ee.deployment.plugins.spi.J2eePlatformImpl;

/**
 * Jetty instance customizer which is accessible from server properties
 * @author  novakm
 */
public class JetCustomizer extends JTabbedPane {
    
//    private static final String CLASSPATH = J2eeLibraryTypeProvider.VOLUME_TYPE_CLASSPATH;
//    private static final String JAVADOC = J2eeLibraryTypeProvider.VOLUME_TYPE_JAVADOC;
    
    private final J2eePlatformImpl platform;
    private final JetCustomizerDataSupport custData;
    
    /**
     * Constructor that initializes components and instance variables
     * @param custData - data for this customizer
     * @param platform - platform ve want to customize
     */
    public JetCustomizer(JetCustomizerDataSupport custData, J2eePlatformImpl platform) {
        this.custData = custData;
        this.platform = platform;
        initComponents();
    }
    
    private void initComponents() {
        getAccessibleContext().setAccessibleName(NbBundle.getMessage(JetCustomizer.class,"ACS_Customizer"));
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(JetCustomizer.class,"ACS_Customizer"));
        // set help ID according to selected tab
        addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                String helpID = null;
                switch (getSelectedIndex()) {
                    case 0 : helpID = "jetty_customizer_user";   // NOI18N
                        break;
                    case 1 : helpID = "jetty_customizer_classes";   // NOI18N
                        break;
                    case 2 : helpID = "jetty_customizer_javadoc";   // NOI18N
                        break;
                }
                putClientProperty("HelpID", helpID); // NOI18N
            }
        });
        addTab(NbBundle.getMessage(JetCustomizer.class,"TXT_Tab_User"),
                JetCustomizerSupport.createUserCustomizer(custData.getInstanceProperties()));
        addTab(NbBundle.getMessage(JetCustomizer.class,"TXT_Tab_Classes"),
                JetCustomizerSupport.createClassesCustomizer(custData.getClassModel()));
        addTab(NbBundle.getMessage(JetCustomizer.class,"TXT_Tab_Javadoc"),
                JetCustomizerSupport.createJavadocCustomizer(custData.getJavadocsModel(), null));
    }
}
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
package org.netbeans.modules.j2ee.geronimo2.customiser;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.util.NbBundle;
import org.netbeans.modules.j2ee.deployment.common.api.J2eeLibraryTypeProvider;
import org.netbeans.modules.j2ee.deployment.plugins.spi.J2eePlatformImpl;

/**
 * Ge instance customizer which is accessible from server manager.
 *
 */
public class GeCustomizer extends JTabbedPane {
    
    private static final String CLASSPATH = J2eeLibraryTypeProvider.VOLUME_TYPE_CLASSPATH;
    private static final String JAVADOC = J2eeLibraryTypeProvider.VOLUME_TYPE_JAVADOC;
    
    private final J2eePlatformImpl platform;
    private final GeCustomizerDataSupport custData;
    
    public GeCustomizer(GeCustomizerDataSupport custData, J2eePlatformImpl platform) {
        this.custData = custData;
        this.platform = platform;
        initComponents();
    }
    
    private void initComponents() {
        getAccessibleContext().setAccessibleName(NbBundle.getMessage(GeCustomizer.class,"ACS_Customizer"));
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(GeCustomizer.class,"ACS_Customizer"));
        // set help ID according to selected tab
        addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                String helpID = null;
                switch (getSelectedIndex()) {
                    case 0 : helpID = "geronimo_customizer_user";   // NOI18N
                        break;
                    case 1 : helpID = "geronimo_customizer_classes";   // NOI18N
                        break;
                    case 2 : helpID = "geronimo_customizer_javadoc";   // NOI18N
                        break;
                }
                putClientProperty("HelpID", helpID); // NOI18N
            }
        });
        addTab(NbBundle.getMessage(GeCustomizer.class,"TXT_Tab_User"),
                GeCustomizerSupport.createUserCustomizer(custData));
        addTab(NbBundle.getMessage(GeCustomizer.class,"TXT_Tab_Classes"),
                GeCustomizerSupport.createClassesCustomizer(custData.getClassModel()));
        addTab(NbBundle.getMessage(GeCustomizer.class,"TXT_Tab_Javadoc"),
                GeCustomizerSupport.createJavadocCustomizer(custData.getJavadocsModel(), null));
    }
}
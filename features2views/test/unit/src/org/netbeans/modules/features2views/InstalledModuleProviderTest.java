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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.features2views;

import org.netbeans.modules.features2views.InstalledModuleProvider;
import java.io.File;
import java.util.Set;
import org.netbeans.junit.NbTestCase;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

/**
 *
 * @author Jirka Rechtacek
 */
public class InstalledModuleProviderTest extends NbTestCase {
    
    public InstalledModuleProviderTest (String testName) {
        super (testName);
    }

    @Override
    public void setUp () throws Exception {
        assertNotNull (Lookup.getDefault ().lookup (ModuleInfo.class));
    }

    public void testGetCluster () {
        Set<ModuleInfo> installed =  InstalledModuleProvider.getDefault ().getModuleInfos (false);
        assertFalse ("Some modules must be loaded.", installed.isEmpty ());
        for (ModuleInfo info : installed) {
            File cluster = InstalledModuleProvider.getCluster (info);
            assertTrue (info.getCodeNameBase () + " returns any cluster or null.", cluster == null || cluster instanceof File);
            System.out.println(info.getCodeNameBase () + " has cluster " + InstalledModuleProvider.getCluster (info));
        }
    }
    
    public void testGetCategory () {
        Set<ModuleInfo> installed =  InstalledModuleProvider.getDefault ().getModuleInfos (false);
        assertFalse ("Some modules must be loaded.", installed.isEmpty ());
        for (ModuleInfo info : installed) {
            String category = InstalledModuleProvider.getCategory (info);
            assertTrue (info.getCodeNameBase () + " returns any cluster or null.", category == null || category instanceof String);
            System.out.println(info.getCodeNameBase () + " has category " + InstalledModuleProvider.getCategory (info));
            if (category == null) {
                assertTrue ("Only fixed/autoload/eager module[" + info + "] doesn't need to category.", InstalledModuleProvider.isAutoload (info) ||
                        InstalledModuleProvider.isEager (info) || InstalledModuleProvider.isFixed (info));
            }
        }
    }
}

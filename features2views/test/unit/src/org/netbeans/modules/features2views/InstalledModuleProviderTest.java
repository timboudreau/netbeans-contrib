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

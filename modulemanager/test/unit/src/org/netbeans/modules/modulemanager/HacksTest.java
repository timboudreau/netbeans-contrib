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
package org.netbeans.modules.modulemanager;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.Module;
import org.netbeans.core.startup.Main;
import org.netbeans.core.startup.NbProblemDisplayer;
import org.netbeans.junit.NbTestCase;
import org.openide.modules.Dependency;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

/**
 *
 * @author Jirka Rechtacek
 */
public class HacksTest extends NbTestCase {
    private Collection<? extends ModuleInfo> moduleInfos = Collections.emptySet ();
    org.netbeans.ModuleManager mgr = null;
    
    public HacksTest (String testName) {
        super (testName);
    }

    @Override
    protected void setUp () throws Exception {
        super.setUp ();
        assertNotNull (Lookup.getDefault ().lookup (ModuleInfo.class));
        Lookup.Result<ModuleInfo> result = Lookup.getDefault ().lookup (new Lookup.Template<ModuleInfo> (ModuleInfo.class));
        moduleInfos = result.allInstances ();
        assertFalse ("Some module infos found.", moduleInfos.isEmpty ());
        mgr = Main.getModuleSystem ().getManager ();
        assertNotNull ("ModuleManager found.", mgr);
        assertFalse ("Some modules found.", mgr.getModules().isEmpty ());
        assertEquals ("Same size modules and module infos", moduleInfos.size (), mgr.getModules ().size ());
    }

    public void testHacks () {
        for (Module m : mgr.getModules ()) {
            testGetModule (m);
            testIsValid (m);
            testIsFixed (m);
            testGetJarFile (m);
            testIsReloadable (m);
            testSetReloadable (m, true);
            testIsAutoload (m);
            testIsEager (m);
            testGetProblems (m);
            if (m.getJarFile () != null) {
                testCreateModuleHistory (m.getJarFile ().toString ());
            }
            testMessageForProblem (m);
            testGetEffectiveClasspath (m);
        }
    }
    
    public void testModuleManager_get () throws Exception {
        ModuleManager mm = Hacks.getModuleManager ();
        assertNotNull ("Hacks.ModuleManager found.", mm);
        Set<ModuleInfo> infos = new HashSet<ModuleInfo> ();
        for (Module m : mgr.getModules ()) {
            ModuleInfo info = mm.get (m.getCodeNameBase ());
            assertNotNull ("ModuleInfo found for " + m.getCodeNameBase (), info);
            infos.add (info);
        }
        assertEquals ("Same size ModuleInfos and original modules.", mgr.getModules ().size (), infos.size ());
    }

    public void testModuleManager_enable () throws Exception {
        ModuleManager mm = Hacks.getModuleManager ();
        assertNotNull ("Hacks.ModuleManager found.", mm);
        Set<ModuleInfo> infos = new HashSet<ModuleInfo> ();
        for (Module m : mgr.getModules ()) {
            ModuleInfo info = mm.get (m.getCodeNameBase ());
            assertNotNull ("ModuleInfo found for " + m.getCodeNameBase (), info);
            infos.add (info);
        }
        assertEquals ("Same size ModuleInfos and original modules.", mgr.getModules ().size (), infos.size ());
        try {
            mm.enable (infos);
            fail ("Already enabled should be thrown.");
        } catch (Exception x) {
            if (x.getMessage () != null && x.getMessage ().indexOf ("Already enabled") != -1) {
                // ok
            } else {
                fail (x.getMessage ());
            }
        }
    }

    public void testModuleManager_getModules () throws Exception {
        ModuleManager mm = Hacks.getModuleManager ();
        assertNotNull ("Hacks.ModuleManager found.", mm);
        Set<ModuleInfo> infos = new HashSet<ModuleInfo> ();
        for (Module m : mgr.getModules ()) {
            ModuleInfo info = mm.get (m.getCodeNameBase ());
            assertNotNull ("ModuleInfo found for " + m.getCodeNameBase (), info);
            infos.add (info);
        }
        Set<ModuleInfo> modules = mm.getModules ();
        assertNotNull ("Hacks.ModuleManager returns modules.", modules);
        assertFalse ("Hacks.ModuleManager returns some modules.", modules.isEmpty ());
        assertEquals ("Same size ModuleInfos from Hacks.ModuleInfos and original modules.", mgr.getModules ().size (), modules.size ());
        for (ModuleInfo i : modules) {
            assertTrue (i.getCodeNameBase () + " present in " + infos, infos.contains(i));
        }
        for (ModuleInfo i : infos) {
            assertTrue (i.getCodeNameBase () + " present in " + infos, modules.contains(i));
        }
    }

    public void testModuleManager_simulateDisable () throws Exception {
        ModuleManager mm = Hacks.getModuleManager ();
        assertNotNull ("Hacks.ModuleManager found.", mm);
        Set<ModuleInfo> infos = new HashSet<ModuleInfo> ();
        for (Module m : mgr.getModules ()) {
            ModuleInfo info = mm.get (m.getCodeNameBase ());
            assertNotNull ("ModuleInfo found for " + m.getCodeNameBase (), info);
            if (! m.isFixed ()) {
                infos.add (info);
            }
        }
        List<ModuleInfo> affectedModules = mm.simulateDisable (infos);
        assertNotNull ("Some module found in simulateDisable().", affectedModules);
        // XXX: assertFalse ("There are modules for testing.", infos.isEmpty ());
        for (ModuleInfo i : infos) {
            affectedModules = mm.simulateDisable (Collections.singleton (i));
            assertNotNull ("Some module found in simulateDisable().", affectedModules);
            assertFalse ("Some module are affected.", affectedModules.isEmpty ());
        }
    }

    public void testModuleManager_simulateEnable () throws Exception {
        ModuleManager mm = Hacks.getModuleManager ();
        assertNotNull ("Hacks.ModuleManager found.", mm);
        Set<ModuleInfo> infos = new HashSet<ModuleInfo> ();
        for (Module m : mgr.getModules ()) {
            ModuleInfo info = mm.get (m.getCodeNameBase ());
            assertNotNull ("ModuleInfo found for " + m.getCodeNameBase (), info);
            if (! m.isFixed ()) {
                infos.add (info);
            }
        }
        List<ModuleInfo> affectedModules = mm.simulateEnable (infos);
        assertNotNull ("Some module found in simulateEnable().", affectedModules);
        // XXX: assertFalse ("There are modules for testing.", infos.isEmpty ());
        for (ModuleInfo i : infos) {
            affectedModules = mm.simulateEnable (Collections.singleton (i));
            assertNotNull ("Some module found in simulateEnable().", affectedModules);
            assertFalse ("Some module are affected.", affectedModules.isEmpty ());
        }
    }

    public void testModuleManager_getModuleInterdependencies () throws Exception {
        ModuleManager mm = Hacks.getModuleManager ();
        assertNotNull ("Hacks.ModuleManager found.", mm);
        Set<ModuleInfo> infos = new HashSet<ModuleInfo> ();
        for (Module m : mgr.getModules ()) {
            ModuleInfo info = mm.get (m.getCodeNameBase ());
            assertNotNull ("ModuleInfo found for " + m.getCodeNameBase (), info);
            Set excepted = mgr.getModuleInterdependencies (m, true, true);
            Set result = mm.getModuleInterdependencies (info, true, true);
            assertEquals ("Same result in Hacks.getModuleInterdependencies() and original.", excepted, result);
        }
    }

    private void testGetModule (Module m) {
        assertEquals ("getModule(" + m + ") returns right module.", m, Hacks.getModule (m));
    }

    private void testIsValid (Module m) {
        assertEquals ("isValid", m.isValid (), Hacks.isValid (m));
    }

    private void testIsFixed (Module m) {
        assertEquals ("isFixed", m.isFixed (), Hacks.isFixed (m));
    }

    private void testGetJarFile (Module m) {
        assertEquals ("getJarFile", m.getJarFile (), Hacks.getJarFile (m));
    }

    private void testIsReloadable (Module m) {
        assertEquals ("isReloadable", m.isReloadable (), Hacks.isReloadable (m));
    }

    private void testSetReloadable (Module m, boolean state) {
        try {
            m.setReloadable (state);
            try {
                Hacks.setReloadable (m, state);
            } catch (Exception x) {
                fail (x.getMessage ());
            }
        } catch (Exception x) {
            log (x.getMessage ());
        }
    }

    private void testIsAutoload (Module m) {
        assertEquals ("isAutoload", m.isAutoload (), Hacks.isAutoload (m));
    }

    private void testIsEager (Module m) {
        assertEquals ("isEager", m.isEager (), Hacks.isEager (m));
    }

    private void testGetProblems (Module m) {
        assertEquals ("getProblems", m.getProblems (), Hacks.getProblems (m));
    }

    private void testCreateModuleHistory (String jar) {
        try {
            Object result = Hacks.createModuleHistory (jar);
            assertNotNull ("New instance of ModuleHistory found.", result);
        } catch (Exception x) {
            fail (x.getMessage());
        }
    }

    private void testGetEffectiveClasspath (Module m) {
        try {
            assertNotNull (Hacks.getEffectiveClasspath (m));
        } catch (Exception x) {
            fail (x.getMessage());
        }
    }

    private void testMessageForProblem (Module m) {
        try {
            Dependency dep = Dependency.create (Dependency.TYPE_REQUIRES, "org.netbeans.ufo").iterator ().next ();
            String result = Hacks.messageForProblem (m, dep);
            String excepted = NbProblemDisplayer.messageForProblem (m, dep);
            assertEquals ("Hacks.messageForProblem() returns as same as original NbProblemDisplayer.", excepted, result);
        } catch (Exception x) {
            fail (x.getMessage());
        }
    }

}

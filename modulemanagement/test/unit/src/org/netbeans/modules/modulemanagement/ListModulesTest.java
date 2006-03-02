/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.modulemanagement;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;
import junit.framework.*;
import org.netbeans.api.sendopts.CommandException;
import org.netbeans.api.sendopts.CommandLine;
import org.openide.modules.ModuleInfo;
import org.openide.modules.SpecificationVersion;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Jaroslav Tulach
 */
public class ListModulesTest extends TestCase {
    static {
        System.setProperty("org.openide.util.Lookup", Lkp.class.getName());
    }
    
    public ListModulesTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        Lkp.ic.setPairs(Collections.EMPTY_LIST);
        Lkp.ic.add(new ModuleOptions());
    }
    
    protected void tearDown() throws Exception {
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(ListModulesTest.class);
        
        return suite;
    }

    public void testAModuleIsPrinted() throws CommandException {
        Lkp.ic.add(new MyModule("my.cnb", 5, new SpecificationVersion("1.3"), true));
        Lkp.ic.add(new MyModule("my.snd.cnb", -1, new SpecificationVersion("5.3.1"), false));

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        CommandLine.getDefault().parse(new String[] { "--listmodules" }, System.in, os, err, new File("."));

        assertEquals("No errors", 0, err.size());

        String out = os.toString();
        if (out.indexOf("my.cnb") == -1) {
            fail("my.cnb should be there: " + out);
        }
        if (out.indexOf("my.snd.cnb") == -1) {
            fail("snd should be there: " + out);
        }
        if (out.indexOf("disabled") == -1) {
            fail("disabled should be there: " + out);
        }
        if (out.indexOf("enabled") == -1) {
            fail("enabled should be there: " + out);
        }
        if (out.indexOf("/5") == -1) {
            fail("5 should be there: " + out);
        }
        if (out.indexOf("1.3") == -1) {
            fail("1.3 should be there: " + out);
        }
        if (out.indexOf("5.3.1") == -1) {
            fail("5.3.1 should be there: " + out);
        }
    }
    
    public static final class Lkp extends AbstractLookup {
        public static InstanceContent ic = new InstanceContent();
        
        public Lkp() {
            super(ic);
        }
    }

    private static class MyModule extends ModuleInfo {

        private boolean enabled;
        private String cnb;
        private int r;
        private SpecificationVersion specV;

        MyModule(String cnb, int r, SpecificationVersion specV, boolean enabled) {
            this.cnb = cnb;
            this.r = r;
            this.specV = specV;
            this.enabled = enabled;
        }

        public String getCodeNameBase() {
            return cnb;
        }

        public int getCodeNameRelease() {
            return r;
        }

        public String getCodeName() {
            if (r == -1) {
                return getCodeNameBase();
            }
            return getCodeNameBase() + '/' + r;
        }

        public SpecificationVersion getSpecificationVersion() {
            return specV;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public Object getAttribute(String attr) {
            return null;
        }

        public Object getLocalizedAttribute(String attr) {
            return null;
        }

        public Set getDependencies() {
            return new HashSet();
        }

        public boolean owns(Class clazz) {
            return false;
        }

    }
}

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
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;
import junit.framework.*;
import org.netbeans.api.sendopts.CommandException;
import org.netbeans.api.sendopts.CommandLine;
import org.openide.modules.ModuleInfo;
import org.openide.modules.SpecificationVersion;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Jaroslav Tulach
 */
public class EnableDisableTest extends TestCase {
    
    public EnableDisableTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        // initialize whole infra
        Lookup.getDefault().lookup(ModuleInfo.class);
    }
    
    protected void tearDown() throws Exception {
    }
    
    public void testAModuleIsPrinted() throws CommandException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        CommandLine.getDefault().parse(new String[] { "--listmodules" }, System.in, os, err, new File("."));

        assertEquals("No error", 0, err.size());

        if (os.toString().indexOf("org.netbeans.bootstrap") < 0) {
            fail("We want default module to be found: " + os.toString());
        }

        
    }
}

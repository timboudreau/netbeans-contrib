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
 * Software is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 */

package org.netbeans.modules.enode;

import javax.swing.Action;

import junit.textui.TestRunner;

import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;

import org.openide.ErrorManager;
import org.openide.modules.ModuleInfo;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

import org.netbeans.api.enode.ExtensibleNode;
import org.netbeans.api.registry.*;

/**
 * This class should test the setting icons in the
 * ExtensibleNode.
 * @author David Strupl
 */
public class ExtensibleIconsTest extends NbTestCase {
    /** root Context */
    private Context root;

    public ExtensibleIconsTest(String name) {
        super(name);
    }
    
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(ExtensibleIconsTest.class));
    }
    
    /**
     * Sets up the testing environment by creating testing folders
     * on the system file system.
     */
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
        String baseFolder = ExtensibleNode.E_NODE_ICONS.substring(1, ExtensibleNode.E_NODE_ICONS.length()-1);
        root = Context.getDefault().createSubcontext(baseFolder);
    }
    
    /**
     * Deletes the folders created in method setUp().
     */
    protected void tearDown() throws Exception {
    }

    /**
     */
    public void test1() throws Exception {
        ExtensibleNode en1 = new ExtensibleNode("a/b/c", true);
        java.lang.reflect.Method getIconManagerMethod = ExtensibleNode.class.getDeclaredMethod("getIconManager", new Class[0]);
        getIconManagerMethod.setAccessible(true);
        Object iconMan = getIconManagerMethod.invoke(en1, new Object[0]);
        
        Context b = root.createSubcontext("a/b");
        Context c = root.createSubcontext("a/b/c");
        
//         b.putObject("i1", base1);
        
        root.destroySubcontext("a");
    }
}

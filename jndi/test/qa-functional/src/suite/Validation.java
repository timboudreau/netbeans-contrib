/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package suite;

public class Validation extends org.netbeans.junit.NbTestCase {
    
    public Validation(String name) {
        super (name);
    }
    
    public static junit.framework.Test suite () {
        org.netbeans.junit.NbTestSuite test = new org.netbeans.junit.NbTestSuite();
        test.addTest (new basic.JNDITest.JNDITest ("testJNDI"));
        test.addTest (new providers.CNSTest.CNSTest ("testAll_CNS"));
        test.addTest (new providers.NISTest.NISTest ("testAll_NIS"));
        test.addTest (new providers.RefFSTest.RefFSTest ("testAll_FS"));
        test.addTest (new providers.RegistryTest.RegistryTest ("testAll_Reg"));
        test.addTest (new providers.LDAPTest.LDAPTest ("testAll_LDAP"));
        test.addTest (new a11y.Main ("testAll"));
        return test;
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite ()); 
    }
    
}

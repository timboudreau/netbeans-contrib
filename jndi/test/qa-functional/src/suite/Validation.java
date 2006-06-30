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

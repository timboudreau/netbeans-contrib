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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.test;

/** This is a certificate that is used to "unlock" testing code (methods, etc.).
 *  Please note that the this class should not be used in production
 *  (not testing) code.
 *
 *  The testing code should get TestCertificate using @link{get} method. This method
 *  returns the certificate only if <pre>netbeans.test.latex.enable</pre> environment
 *  variable is set to true. Otherwise the get method will throw an IllegalStateException.
 *
 *  DO NOT USE IN THE PRODUCTION CODE.
 *
 * @author Jan Lahoda
 */
public final class TestCertificate {
    
    /** ONLY FOR TESTING, DO NOT USE IN THE PRODUCTION CODE.
     */
    public static TestCertificate get() {
        if (Boolean.getBoolean("netbeans.test.latex.enable"))
            return new TestCertificate();
        else
            throw new IllegalStateException("Cannot create Test Certificate, because test mode is not enabled.");
    }
    
    /** Creates a new instance of TestCertificate */
    private TestCertificate() {
    }
    
}

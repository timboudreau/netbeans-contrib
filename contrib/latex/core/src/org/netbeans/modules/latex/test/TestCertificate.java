/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
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

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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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

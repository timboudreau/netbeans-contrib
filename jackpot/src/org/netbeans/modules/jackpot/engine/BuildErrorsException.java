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

package org.netbeans.modules.jackpot.engine;

/**
 * Thrown when build errors are found compiling test source files.
 *
 * @author Tom Ball
 */
public class BuildErrorsException extends java.lang.Exception {

    /**
     * Constructs an instance of <code>BuildErrorsException</code> with the build log
     * as the exception's detail message.
     * @param log the build log.
     */
    public BuildErrorsException(String log) {
        super(log);
    }
    
    /**
     * Constructs an instance of <code>BuildErrorsException</code> with the 
     * number of build errors.
     * @param errors the number of errors.
     */
    public BuildErrorsException(int errors) {
        super(Integer.toString(errors));
    }
}

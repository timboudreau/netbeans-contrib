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
 * If applicable, add the following below the CDDL Heade    r, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.erlang.platform.spi;

import org.netbeans.modules.languages.execution.ExecutionDescriptor;


/**
 * Ability for Ruby project to debug Ruby scripts/applications.
 *
 * @author Martin Krauskopf
 */
public interface RubyDebuggerImplementation {
    
    /**
     * Starts debugging of the given script.
     * 
     * @param descriptor description of the process to be debugged
     * @return debugger {@link java.lang.Process process}. Might be
     *         <tt>null</tt> if debugging cannot be started for some reason.
     *         E.g. interpreter cannot be obtained from preferences.
     */
    Process debug(final ExecutionDescriptor descriptor);
    
}

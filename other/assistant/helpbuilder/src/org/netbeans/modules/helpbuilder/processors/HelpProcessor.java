/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.helpbuilder.processors;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * HelpProcessor.java
 *
 * Created on February 21, 2003, 1:07 PM
 *
 * @author  Richard Gregor
 */
public interface HelpProcessor {
    
    /**
     * Exports processor data to appropriate file
     */
    public void export(OutputStream out) throws IOException;
 
}

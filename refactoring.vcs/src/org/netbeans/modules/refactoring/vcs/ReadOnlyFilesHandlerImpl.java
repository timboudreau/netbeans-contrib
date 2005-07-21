/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.refactoring.vcs;
import java.util.Collection;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.spi.*;

/**
 *
 * @author Jan Becicka
 */
public class ReadOnlyFilesHandlerImpl implements ReadOnlyFilesHandler {
    
    /** Creates a new instance of ReadOnlyFilesHandlerImpl */
    public ReadOnlyFilesHandlerImpl() {
    }
    
    public Problem createProblem(Collection files) {
        //if files cannot be handled by VCS return null
        return new Problem(false, "Some files are checked out as read-only. You can update them as read-write.", ProblemDetailsFactory.createProblemDetails(new CheckoutFiles(files)));
    }
    
}

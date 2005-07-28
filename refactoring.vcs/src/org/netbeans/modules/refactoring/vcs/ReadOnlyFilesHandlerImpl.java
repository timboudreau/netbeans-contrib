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
import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.spi.*;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

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
        FileObject[] fos = (FileObject[]) files.toArray(new FileObject[0]);
        Command editCmd = VcsManager.getDefault().createCommand("EDIT", fos); //NOI18N
        if (editCmd == null) return null;
        fos = editCmd.getApplicableFiles(fos);
        editCmd.setFiles(fos);
        return new Problem(false, NbBundle.getMessage(ReadOnlyFilesHandlerImpl.class, "MSG_CheckoutWarning"), ProblemDetailsFactory.createProblemDetails(new CheckoutFiles(fos, editCmd)));
    }
    
}

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

package org.netbeans.modules.refactoring.vcs;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.WeakHashMap;
import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.api.RefactoringSession;
import org.netbeans.modules.refactoring.spi.*;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author Jan Becicka
 */
public class ReadOnlyFilesHandlerImpl implements ReadOnlyFilesHandler {
    
    private WeakHashMap sessions = new WeakHashMap(2);
    /** Creates a new instance of ReadOnlyFilesHandlerImpl */
    public ReadOnlyFilesHandlerImpl() {
    }
    
    public Problem createProblem(RefactoringSession session, Collection files) {
        CheckoutFiles cof = (CheckoutFiles) sessions.get(session);
        Collection fileSet = null;
        if (cof != null) {
            //instance of CheckoutFiles created for this session, try to add files
            fileSet = new HashSet(cof.getFiles());
            if (!fileSet.addAll(files)) {
                //no files were added
                return null;
            }
        } else {
            //CheckoutFiles not found - create a new one
            fileSet = new HashSet(files);
        }
        
        FileObject[] fos = (FileObject[]) fileSet.toArray(new FileObject[0]);
        Command editCmd;
        try {
            editCmd = VcsManager.getDefault().createCommand("EDIT", fos); //NOI18N
        } catch (IllegalArgumentException iaex) {
            // The provided files are not under version control
            editCmd = null;
        }
        if (editCmd == null) return null;
        fos = editCmd.getApplicableFiles(fos);
        editCmd.setFiles(fos);
        if (cof == null) {
            cof = new CheckoutFiles(Arrays.asList(fos), editCmd);
            sessions.put(session, cof);
            return new Problem(false, NbBundle.getMessage(ReadOnlyFilesHandlerImpl.class, "MSG_CheckoutWarning"), ProblemDetailsFactory.createProblemDetails(cof));
        } else {
            cof.setEditCmd(editCmd);
            cof.setFiles(Arrays.asList(fos));
            return null;
        }
    }
}

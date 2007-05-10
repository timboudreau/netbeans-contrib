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
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.netbeans.modules.java.additional.refactorings;

import javax.swing.text.JTextComponent;
import org.netbeans.modules.refactoring.api.AbstractRefactoring;
import org.netbeans.modules.refactoring.spi.RefactoringPlugin;
import org.netbeans.modules.refactoring.spi.RefactoringPluginFactory;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;

/**
 *
 * @author Tim
 */
public class PluginFactory implements RefactoringPluginFactory {

    public PluginFactory() {
    }

    public RefactoringPlugin createInstance(AbstractRefactoring refactoring) {
        System.err.println("Create instance for " + refactoring);
        if (refactoring instanceof ExtractMethodRefactoring) {
            ExtractMethodRefactoring r = (ExtractMethodRefactoring) refactoring;
            Lookup context = refactoring.getRefactoringSource();
            DataObject dob = context.lookup(DataObject.class);
            return new ExtractMethodPlugin(r, dob.getPrimaryFile(), 
                    r.start,
                    r.end, r.handle);
        } else {
            return null;
        }
    }
}

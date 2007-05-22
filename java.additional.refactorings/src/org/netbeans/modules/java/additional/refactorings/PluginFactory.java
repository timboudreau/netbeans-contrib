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

import org.netbeans.modules.java.additional.refactorings.ExtractMethodPlugin;
import org.netbeans.modules.refactoring.api.AbstractRefactoring;
import org.netbeans.modules.refactoring.spi.RefactoringPlugin;
import org.netbeans.modules.refactoring.spi.RefactoringPluginFactory;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author Tim
 */
public class PluginFactory implements RefactoringPluginFactory {

    public PluginFactory() {
    }

    public RefactoringPlugin createInstance(AbstractRefactoring refactoring) {
        if (refactoring instanceof ExtractMethodRefactoring) {
            ExtractMethodRefactoring r = (ExtractMethodRefactoring) refactoring;
            Lookup context = refactoring.getRefactoringSource();
            DataObject dob = context.lookup(DataObject.class);
            if (dob == null) {
                //WTF... - when invoked as a popup
                //menu item directly on the popup menu, I get the usual DataNode;
                //when inside the refactoring method I get this weird lookup
                //that sometimes contains a node, sometimes doesn't,
                //sometimes contains a fileobject, sometimes doesn't.  Way cool.
                FileObject fob = context.lookup (FileObject.class);
                if (fob != null) {
                    return new ExtractMethodPlugin(r, fob, 
                        r.start,
                        r.end, r.handle);
                } else {
                    Node n = context.lookup (Node.class);
                    if (n != null) {
                        context = n.getLookup();
                    }
                    dob = context.lookup (DataObject.class);
                }
            }
            assert dob != null : "No DataObject in " + context; //NOI18N            
            return new ExtractMethodPlugin(r, dob.getPrimaryFile(), 
                    r.start,
                    r.end, r.handle);
        } else {
            return null;
        }
    }
}

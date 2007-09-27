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
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.netbeans.modules.java.additional.refactorings;

import org.netbeans.modules.java.additional.refactorings.extractmethod.ExtractMethodRefactoring;
import org.netbeans.modules.java.additional.refactorings.extractmethod.ExtractMethodPlugin;
import org.netbeans.modules.java.additional.refactorings.splitclass.ChangeSignaturePlugin;
import org.netbeans.modules.java.additional.refactorings.splitclass.ChangeSignatureRefactoring;
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
            FileObject fob = findFileObject (context);
            assert fob != null : "No DataObject in " + context; //NOI18N
            return new ExtractMethodPlugin(r, fob,
                    r.start,
                    r.end, r.handle);
        } else if (refactoring instanceof ChangeSignatureRefactoring) {
            ChangeSignatureRefactoring r = (ChangeSignatureRefactoring) refactoring;
            Lookup context = refactoring.getRefactoringSource();
            FileObject fob = findFileObject (context);
            assert fob != null : "No DataObject in " + context; //NOI18N
            return new ChangeSignaturePlugin(r, fob);
        } else {
            return null;
        }
    }

    private static FileObject findFileObject (Lookup context) {
        DataObject dob = context.lookup(DataObject.class);
        if (dob == null) {
            //WTF... - when invoked as a popup
            //menu item directly on the popup menu, I get the usual DataNode;
            //when inside the refactoring method I get this weird lookup
            //that sometimes contains a node, sometimes doesn't,
            //sometimes contains a fileobject, sometimes doesn't.  Way cool.
            FileObject fob = context.lookup (FileObject.class);
            if (fob != null) {
                return fob;
            } else {
                Node n = context.lookup (Node.class);
                if (n != null) {
                    context = n.getLookup();
                }
                dob = context.lookup (DataObject.class);
            }
        }
        return dob == null ? null : dob.getPrimaryFile();
    }
}

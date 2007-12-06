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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
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
package org.netbeans.modules.latex.guiproject;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.filesystems.FileObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXLogicalViewTest extends ProjectTestCase {
    
    public LaTeXLogicalViewTest(String name) {
        super(name);
    }
    
    private static boolean isAncestor(Node root, Node n) {
        if (n == null) return false;
        if (n == root) return true;
        return isAncestor(root, n.getParentNode());
    }
    
    private void projectFilesTestImpl(FileObject project, Collection positiveFiles, Collection negativeFiles) throws IOException {
        Project p = ProjectManager.getDefault().findProject(project);
        LogicalViewProvider logicalViewProvider = p.getLookup().lookup(LogicalViewProvider.class);
        Node logicalView = logicalViewProvider.createLogicalView();
        Node logicalViewImpl = new FilterNode(logicalView);
        
        for (Iterator i = positiveFiles.iterator(); i.hasNext(); ) {
            Object target = i.next();
            Node   found  = logicalViewProvider.findPath(logicalViewImpl, target);
            
            assertNotNull("Node corresponding to: " + target + " not found.", found);
            assertTrue("Returned node is not a recursive child of the original node: root= " + logicalViewImpl + ", found=" + found, isAncestor(logicalViewImpl, found));
            
            Lookup.Template<Object> tmpl = new Lookup.Template<Object>(null, null, target);
            Collection<? extends Object> res = found.getLookup().lookup(tmpl).allInstances();

            assertEquals("The lookup do not have correct content! root=" + logicalViewImpl + ", found=" + found, Collections.singleton(target), new HashSet<Object>(res));
        }
        
        for (Iterator i = negativeFiles.iterator(); i.hasNext(); ) {
            Object target = i.next();
            Node   found  = logicalViewProvider.findPath(logicalViewImpl, target);
            
            assertNull("Node found althought it should not be: " + found, found);
        }
    }
    
    private void nonProjectFilesTestImpl(FileObject project, FileObject file) throws IOException {
        Project p = ProjectManager.getDefault().findProject(project);
        LogicalViewProvider logicalViewProvider = p.getLookup().lookup(LogicalViewProvider.class);
        Node logicalView = logicalViewProvider.createLogicalView();
        Node logicalViewImpl = new FilterNode(logicalView);
        Node   found  = logicalViewProvider.findPath(logicalViewImpl, file);
        
        assertNull("Node found althought it should not be: " + found, found);
    }
    
    public void testProjectFiles() throws IOException {
        projectFilesTestImpl(prj1Impl, project1Files, project2Files);
        projectFilesTestImpl(prj2Impl, project2Files, project1Files);
    }
    
    public void testNonProjectFiles() throws IOException {
        nonProjectFilesTestImpl(prj1Impl, notIncluded);
        nonProjectFilesTestImpl(prj2Impl, notIncluded);
    }

    
}

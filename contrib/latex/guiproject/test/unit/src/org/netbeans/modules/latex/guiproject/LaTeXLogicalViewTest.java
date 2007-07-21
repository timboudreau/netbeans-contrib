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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.guiproject;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.latex.UnitUtilities;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.xml.sax.SAXException;

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
        LogicalViewProvider logicalViewProvider = (LogicalViewProvider) p.getLookup().lookup(LogicalViewProvider.class);
        Node logicalView = logicalViewProvider.createLogicalView();
        Node logicalViewImpl = new FilterNode(logicalView);
        
        for (Iterator i = positiveFiles.iterator(); i.hasNext(); ) {
            Object target = i.next();
            Node   found  = logicalViewProvider.findPath(logicalViewImpl, target);
            
            assertNotNull("Node corresponding to: " + target + " not found.", found);
            assertTrue("Returned node is not a recursive child of the original node: root= " + logicalViewImpl + ", found=" + found, isAncestor(logicalViewImpl, found));
            
            Lookup.Template tmpl = new Lookup.Template(null, null, target);
            Collection res = found.getLookup().lookup(tmpl).allInstances();

            assertEquals("The lookup do not have correct content! root=" + logicalViewImpl + ", found=" + found, Collections.singleton(target), new HashSet(res));
        }
        
        for (Iterator i = negativeFiles.iterator(); i.hasNext(); ) {
            Object target = i.next();
            Node   found  = logicalViewProvider.findPath(logicalViewImpl, target);
            
            assertNull("Node found althought it should not be: " + found, found);
        }
    }
    
    private void nonProjectFilesTestImpl(FileObject project, FileObject file) throws IOException {
        Project p = ProjectManager.getDefault().findProject(project);
        LogicalViewProvider logicalViewProvider = (LogicalViewProvider) p.getLookup().lookup(LogicalViewProvider.class);
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

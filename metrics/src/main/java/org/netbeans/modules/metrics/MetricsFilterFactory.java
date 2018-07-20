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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.metrics;

import org.openide.filesystems.*;
import org.openide.loaders.DataObject;
import org.openide.nodes.*;
import org.openide.src.nodes.*;
import org.openide.src.*;

import org.netbeans.modules.classfile.ClassName;

import java.io.*;
import java.util.*;

/**
 * Adds metrics node to standard Java class nodes
 *
 * @author Thomas Ball
 */
public class MetricsFilterFactory extends FilterFactory {

    private static boolean debug = false;

    public MetricsFilterFactory() {
	super();
    }

    public Node createClassNode (ClassElement element) {
        Node node = super.createClassNode(element);

        // Add metrics node to class node's children list.
        Children children = node.getChildren();
        ClassMetrics cm = null;
        try {
	    if (element.isInner()) {
		String classType = element.getSignature();
		ClassName className = ClassName.getClassName(classType);
		cm = ClassMetrics.getClassMetrics(className);
	    } else
		cm = getClassMetrics(node);
            children.add(new Node[] { new MetricsNode(cm) });
        } catch (Exception e) {
            // no class file, so don't add metrics node...
            if (debug)
                System.out.println(e.getMessage());
        }

        // Add class files associated with jdo to ClassMetrics pool.
        // It doesn't matter that the main class file is fetched again,
        // as it has been already created.
        DataObject dobj = (DataObject)node.getCookie(DataObject.class);
        if (dobj != null) {
            FileObject fobj = dobj.getPrimaryFile();
            if (fobj.getExt().equals("java")) {
                List /*<FileObject>*/ classes = ClassFinder.getCompiledClasses(fobj);
                for (Iterator i = classes.iterator(); i.hasNext();)
                   try {
                       FileObject fo = (FileObject)i.next();
                       ClassMetrics.getClassMetrics(fo);
                   } catch (IOException e) {}
            }
        }

        // Add an icon updating filter to this class node.
        if (cm != null)
            node = new TrafficNode(node, cm);

        return node;
    }

    public Node createConstructorNode (ConstructorElement element) {
	Node node = super.createConstructorNode(element);
	node = addTrafficNodeFilter(node, 
				    element.getName().getFullName(),
				    element.getParameters(),
				    null);
        return node;
    }

    public Node createMethodNode (MethodElement element) {
        Node node = super.createMethodNode(element);
	node = addTrafficNodeFilter(node, 
				    element.getName().getFullName(),
				    element.getParameters(),
				    element.getReturn());
        return node;
    }

    public Node createInitializerNode (InitializerElement element) {
        Node node = super.createInitializerNode(element);
	String name = element.isStatic() ? "<clinit>" : "<init>";
	node = addTrafficNodeFilter(node, name, noParams, null);
        return node;
    }
    private static final MethodParameter[] noParams = new MethodParameter[0];

    private Node addTrafficNodeFilter(Node node, String name, 
				      MethodParameter[] params,
				      Type ret) {
        try {
            ClassMetrics cm = getClassMetrics(node);
	    String signature = buildSignature(params, ret);
	    MethodMetrics mm = cm.getMethod(name, signature);
	    if (mm != null)
		node = new TrafficNode(node, mm);
        } catch (Exception e) {
            // no class file, so don't add TrafficNode filter...
        }

        return node;
    }

    private String buildSignature(MethodParameter[] params, Type ret) {
	StringBuffer sb = new StringBuffer(32);
	sb.append('(');
	int n = params.length;
	for (int i = 0; i < n; i++) {
	    Type type = params[i].getType();
	    sb.append(type.getSignature());
	}
	sb.append(')');
	if (ret != null)
	    sb.append(ret.getSignature());
	return sb.toString();
    }

    private ClassMetrics getClassMetrics(Node node) throws IOException {
        FileObject classFile = null;
        DataObject dobj = (DataObject)node.getCookie(DataObject.class);
        if (dobj != null) {
            FileObject fobj = dobj.getPrimaryFile();
            if (fobj.getExt().equals("java")) {
                List classes = ClassFinder.getCompiledClasses(fobj);
                if (classes.size() > 0)
                    classFile = (FileObject)classes.get(0);
            }
        }
	return ClassMetrics.getClassMetrics(classFile);
    }
}

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

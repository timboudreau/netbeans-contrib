/*
 * MetricsFilterFactory.java
 *
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * Contributor(s): Thomas Ball
 *
 * Version: $Revision$
 */

package org.netbeans.modules.metrics;

import org.openide.filesystems.*;
import org.openide.nodes.*;
import org.openide.src.nodes.*;
import org.openide.src.*;

import org.netbeans.modules.classfile.ClassName;
import org.netbeans.modules.clazz.CompiledDataObject;
import org.netbeans.modules.java.JavaDataObject;

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
        JavaDataObject jdo = 
            (JavaDataObject)node.getCookie(JavaDataObject.class);

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
        if (jdo != null) {
            Collection c = jdo.getCompiledClasses();
            if (c != null)
               for (Iterator iter = c.iterator(); iter.hasNext();) {
                   FileObject fo = (FileObject)iter.next();
                   try {
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
        JavaDataObject jdo = 
            (JavaDataObject)node.getCookie(JavaDataObject.class);
        if (jdo != null)
            classFile = FileUtil.findBrother(jdo.getPrimaryFile(), "class");
        else {
            CompiledDataObject cdo = 
                (CompiledDataObject)node.getCookie(CompiledDataObject.class);
            if (cdo != null)
                classFile = cdo.getPrimaryFile();
        }

	return ClassMetrics.getClassMetrics(classFile);
    }
}

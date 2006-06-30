/*
 * ApprovalsFile.java
 *
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
 *
 * Contributor(s): Thomas Ball
 *
 * Version: $Revision$
 */

package org.netbeans.modules.metrics;

import java.io.*;
import java.util.*;

import org.openide.filesystems.*;
import org.openide.xml.XMLUtil;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * Approvals that are stored in a per-directory XML file.
 *
 * @author  tball
 * @version
 */
class ApprovalsFile {

    private Map approvals = new TreeMap();

    public final String FILE_NAME =
	MetricsNode.bundle.getString("STR_Approvals_Filename");

    FileObject xmlFile;
    FileObject directory;

    private static ApprovalsFile lastFile = null;

    static ApprovalsFile getApprovalsFile(FileObject dir) {
        if (lastFile == null || !lastFile.directory.equals(dir))
            lastFile = new ApprovalsFile(dir);
        return lastFile;
    }

    private ApprovalsFile(FileObject directory) {
        this.directory = directory;
	xmlFile = directory.getFileObject(FILE_NAME, "");
	if (xmlFile != null)
	    read();
    }

    ClassApprovals get(String className) {
	return (ClassApprovals)approvals.get(className);
    }

    void add(ClassApprovals ca) {
	add(ca, true);
    }

    void add(ClassApprovals ca, boolean flush) {
	approvals.put(ca.getClassName(), ca);
	if (flush)
	    write();
    }

    private void read() {
	try {
	    XMLReader parser = XMLUtil.createXMLReader();
	    XMLHandler handler = new XMLHandler();
	    parser.setContentHandler(handler);
	    parser.setErrorHandler(handler);

	    InputSource is = new InputSource(
                new InputStreamReader(xmlFile.getInputStream()));
	    parser.parse(is);

	} catch (IOException e) {
	    System.err.println("IOException reading " + xmlFile.getPath() +
			       ": " + e);
	} catch (SAXException e) {
	    System.err.println("SAXException reading " + xmlFile.getPath() +
			       ": " + e);
	    Exception e2 = e.getException();
	    if (e2 != null)
		System.err.println("   embedded exception: " + e2);
	}
    }

    private void write() {
	try {
            if (xmlFile == null)
                xmlFile = directory.createData(FILE_NAME);

	    FileLock lock = xmlFile.lock();
            OutputStream os = xmlFile.getOutputStream(lock);
            PrintWriter xml = new PrintWriter(new OutputStreamWriter(os));
            xml.println("<?xml version=\"1.0\" encoding=\"ISO8859_1\"?>\n");
            xml.println("<approved_metrics>");

            Iterator i = approvals.values().iterator();
            while (i.hasNext()) {
                ClassApprovals a = (ClassApprovals)i.next();
                a.writeXML(xml);
            }
	    
            xml.println("</approved_metrics>");
            xml.close();
	    lock.releaseLock();
	} catch (IOException e) {
	    System.err.println("failed writing " + xmlFile.getPath() +
			       ": " + e);
	}
    }

    private static HashMap metricClasses = new HashMap();
    static {
	Class[] classes = MetricsLoader.getMetricClasses();
	for (int i = 0; i < classes.length; i++) {
	    Class cls = classes[i];
	    metricClasses.put(cls.getName(), cls);
	}
    }

    public String toString() {
	String s = xmlFile.getPath() + ", approvals:\n";
	Iterator i = approvals.values().iterator();
	while (i.hasNext())
	    s += "\t" + i.next().toString() + "\n";
	return s;
    }

    private class XMLHandler extends DefaultHandler {
	ApprovalAcceptor currentAcceptor = null;
	ClassApprovals classApprovals = null;
	MetricValue currentValue = null;

	public void startElement(String namespaceURI, String localName,
				 String rawName, Attributes attrs) 
	    throws SAXException {
	    if (rawName.equals("approved_metrics"))
		; // ignore
	    else if (rawName.equals("class_approvals")) {
		classApprovals = new ClassApprovals(attrs.getValue("class"));
		currentAcceptor = classApprovals;
	    } else if (rawName.equals("method_approvals"))
		currentAcceptor = 
		    new MethodApprovals(attrs.getValue("method"));
	    else if (rawName.equals("approval"))
		addApproval(attrs.getValue("class"), attrs.getValue("metric"));
	    else if (rawName.equals("approver")) 
		setApprover(attrs.getValue("name"), attrs.getValue("comment"));
	    else if (rawName.length() > 0)
		System.out.println("ApprovalsFile: unknown element " + rawName);
	}

	private void setApprover(String name, String comment) 
	    throws SAXException {
	    if (currentValue == null)
		throw new SAXException("invalid approvals file XML format.");
	    currentValue.setApprover(name);
	    if (comment != null)
		currentValue.setComment(comment);
	}

	private void addApproval(String className, String metric) 
	    throws SAXException {
	    if (currentAcceptor == null)
		throw new SAXException("invalid approvals file XML format.");
	    Class cls = (Class)metricClasses.get(className);
	    if (cls == null)
		throw new SAXException("invalid approvals file XML format.");
	    currentValue = new MetricValue(cls, Integer.parseInt(metric));
	    currentAcceptor.addApproval(currentValue);
	}

	public void endElement(String namespaceURI, String localName,
			       String rawName) {
	    if (rawName.equals("class_approvals")) {
		ApprovalsFile.this.add(classApprovals, false);
		classApprovals = null;
	    } else if (rawName.equals("method_approvals")) {
		classApprovals.addMethodApprovals((MethodApprovals)currentAcceptor);
		currentAcceptor = classApprovals;
	    }
	}

	public void warning(SAXParseException e) throws SAXException {
	    String s = "warning reading " + e.getSystemId() +
		":" + e.getLineNumber() + " " + e.getMessage();
	    System.err.println(s);
	}
	public void error(SAXParseException e) throws SAXException {
	    String s = "error reading " + e.getSystemId() +
		":" + e.getLineNumber() + " " + e.getMessage();
	    System.err.println(s);
	}
	public void fatalError(SAXParseException e) throws SAXException {
	    String s = "fatal error reading " + e.getSystemId() +
		":" + e.getLineNumber() + " " + e.getMessage();
	    System.err.println(s);
	}
    }
}

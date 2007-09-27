/*
 * ApprovalsFile.java
 *
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

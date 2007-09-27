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

package org.netbeans.modules.jackpot;

import java.util.ArrayList;
import org.openide.xml.XMLUtil;
import org.xml.sax.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A named subset of the list of inspections, which are executed sequentially
 * in a single action.  
 */
public final class QuerySet {
    transient String name;
    transient String localeName;
    transient boolean modified;
    transient boolean isDefault;
    String[] inspectionFiles;
    
    public static final String DISPLAY_NAME_PROPERTY = "displayName";
    public static final String INSPECTIONS_PROPERTY = "inspections";
    public static final String QUERYSET_SUFFIX = "queryset";
    
    QuerySet(String name, String localeName) {
        this(name, localeName, new Inspection[0]);
    }
    
    public QuerySet(String name, String localeName, Inspection[] inspections) {
        this.name = name;
        this.localeName = localeName;
        setInspectionFiles(inspections);
        modified = false;
    }
    
    public String getName() {
        return name;
    }
    
    public String getLocalizedName() {
        return localeName;
    }

    public Inspection[] getInspections() {
        InspectionsList list = InspectionsList.instance();
        Inspection[] inspections = new Inspection[inspectionFiles.length];
        for (int i = 0; i < inspectionFiles.length; i++)
            inspections[i] = list.getInspectionByFilename(inspectionFiles[i]);
        return inspections;
    }
    
    public void setInspections(Inspection[] inspections) {
        setInspectionFiles(inspections);
        modified = true;
    }
    
    private void setInspectionFiles(Inspection[] inspections) {
        inspectionFiles = new String[inspections.length];
        for (int i = 0; i < inspections.length; i++)
            inspectionFiles[i] = inspections[i].getName();
    }
    
    public int indexOf(Inspection inspection) {
        String filename = inspection.getName();
        for (int i = 0; i < inspectionFiles.length; i++)
            if (filename.equals(inspectionFiles[i]))
                return i;
        return -1;
    }
    
    public boolean isDeletable() {
        return !isDefault;
    }

    /**
     * Returns an index list of which inspections are contained in this
     * QuerySet.
     */
    public boolean[] getInspectionIndex() {
        Inspection[] inspections = InspectionsList.instance().getInspections();
        boolean[] index = new boolean[inspections.length];
        for (int i = 0; i < inspections.length; i++)
            index[i] = indexOf(inspections[i]) >= 0;
        return index;
    }
    
    public void setInspectionsByIndex(boolean[] index) {
        InspectionsList inspList = InspectionsList.instance();
        ArrayList<Inspection> l = new ArrayList<Inspection>();
        for (int i = 0; i < index.length; i++)
            if (index[i])
                l.add(inspList.getInspection(i));
        setInspections(l.toArray(new Inspection[0]));
    }

    /**
     * Returns a new QuerySet whose initial values are the same as a specified
     * original.
     */
    public QuerySet duplicate() {
        QuerySet qs = new QuerySet(name, localeName);
        qs.inspectionFiles = (String[])inspectionFiles.clone();
        QuerySetList.instance().add(qs);
        return qs;
    }
    
    public void save() throws IOException {
        if (modified) {
            FileLock lock = null;
            try {
                FileObject fo = getFileObject();
                lock = fo.lock();
                OutputStream out = getFileObject().getOutputStream(lock);
                PrintWriter xml = new PrintWriter(new OutputStreamWriter(out));
                xml.println("<?xml version=\"1.0\" encoding=\"ISO8859_1\"?>");
                xml.println("<queryset>");
                for (String inspection : inspectionFiles)
                    xml.println("   <inspection file=\"" + inspection + "\"/>");
                xml.println("</queryset>");
                xml.close();
                lock.releaseLock();
            } finally {
                if (lock != null)
                    lock.releaseLock();
            }
        }
        modified = false;
    }
    
    public void delete() throws IOException {
        assert isDeletable();
        FileObject fo = getFileObject();
        fo.delete();
        QuerySetList.instance().delete(this);
    }
    
    void restore(FileObject fo) throws IOException {
        XMLHandler handler = new XMLHandler();
	try {
            XMLReader parser = XMLUtil.createXMLReader();
            parser.setContentHandler(handler);
            parser.setErrorHandler(handler);
	    InputSource is = new InputSource(
                new InputStreamReader(fo.getInputStream()));
	    parser.parse(is);
	} catch (IOException e) {
	    System.err.println("IOException reading " + fo.getPath() +
			       ": " + e);
	} catch (SAXException e) {
	    System.err.println("SAXException reading " + fo.getPath() +
			       ": " + e);
	    Exception e2 = e.getException();
	    if (e2 != null)
		System.err.println("   embedded exception: " + e2);
	}
        inspectionFiles = handler.inspectionFiles.toArray(new String[0]);
        modified = false;
    }

    FileObject getFileObject() throws IOException {
        assert name != null;
        FileObject dir = QuerySetList.getQuerySetDirectory().getPrimaryFile();
        FileObject fo = dir.getFileObject(name, "queryset");
        if (fo == null)
            fo = dir.createData(name, "queryset");
        return fo;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer(name);
        sb.append(" [");
        boolean first = true;
        for (String s : inspectionFiles) {
            if (first)
                first = false;
            else
                sb.append(", ");
            sb.append(s);
        }
        sb.append(']');
        return sb.toString();
    }

    static class XMLHandler extends DefaultHandler {
	public String displayName;
        public ArrayList<String> inspectionFiles = new ArrayList<String>();
        private InspectionsList inspList = InspectionsList.instance();
        
	public void startElement(String namespaceURI, String localName,
				 String rawName, Attributes attrs) 
	    throws SAXException {
	    if (rawName.equals("queryset"))
		displayName = attrs.getValue("name");
	    else if (rawName.equals("inspection")) {
                String filename = attrs.getValue("file");
                Inspection insp = inspList.getInspectionByFilename(filename);
                if (insp != null)
                    inspectionFiles.add(insp.getName());
                else
                    ErrorManager.getDefault().log(ErrorManager.WARNING, "unknown inspection: " + filename);
            }
	    else if (rawName.length() > 0)
		log("QuerySet: unknown element " + rawName);
	}

	public void warning(SAXParseException e) throws SAXException {
	    log("warning reading " + e.getSystemId() +
		":" + e.getLineNumber() + " " + e.getMessage());
	}
	public void error(SAXParseException e) throws SAXException {
	    log("error reading " + e.getSystemId() +
		":" + e.getLineNumber() + " " + e.getMessage());
	}
	public void fatalError(SAXParseException e) throws SAXException {
	    log("fatal error reading " + e.getSystemId() +
		":" + e.getLineNumber() + " " + e.getMessage());
	}
        
        private void log(String s) {
            ErrorManager.getDefault().log(s);
        }
    }
}

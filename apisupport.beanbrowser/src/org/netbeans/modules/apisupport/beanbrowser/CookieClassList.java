/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.apisupport.beanbrowser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.openide.ErrorManager;
import org.openide.cookies.InstanceCookie;
import org.openide.loaders.XMLDataObject;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/** Permit other modules to add their own list of cookies to recognize.
 * See enhancement #11911, and http://www.netbeans.org/dtds/apisupport-cookie-class-list-1_0.dtd
 * @author Jesse Glick
 */
public final class CookieClassList implements InstanceCookie, XMLDataObject.Processor {
    
    private XMLDataObject xml = null;
    
    public void attachTo(XMLDataObject xmlDO) {
        xml = xmlDO;
        //System.err.println("CookieClassList.attachTo: " + xml);
    }
    
    public Class instanceClass() {
        //System.err.println("CookieClassList.instanceClass");
        return ActualList.class;
    }
    
    public String instanceName() {
        //System.err.println("CookieClassList.instanceName");
        return ActualList.class.getName();
    }
    
    public Object instanceCreate() throws IOException, ClassNotFoundException {
        //System.err.println("CookieClassList.instanceCreate: " + xml);
        try {
            Document doc = xml.getDocument();
            Element el = doc.getDocumentElement();
            NodeList nl = el.getElementsByTagName("class"); // NOI18N
            Class[] clazzes = new Class[nl.getLength()];
            for (int i = 0; i < clazzes.length; i++) {
                Element clazz = (Element) nl.item(i);
                clazzes[i] = Class.forName(clazz.getAttribute("name"), // NOI18N
                        false,
                        (ClassLoader)Lookup.getDefault().lookup(ClassLoader.class));
                if (! Node.Cookie.class.isAssignableFrom(clazzes[i])) {
                    throw new ClassNotFoundException("Not a Node.Cookie: " + clazzes[i].getName()); // NOI18N
                }
            }
            return new ActualList(clazzes, xml.getPrimaryFile().getPath());
        } catch (SAXException saxe) {
            IOException ioe = new IOException(saxe.toString());
            ErrorManager.getDefault().annotate(ioe, saxe);
            throw ioe;
        }
    }
    
    private static final class ActualList {
        private final Class[] clazzes;
        private final String origin;
        ActualList(Class[] clazzes, String origin) {
            this.clazzes = clazzes;
            this.origin = origin;
            //System.err.println("new CookieClassList.ActualList: " + java.util.Arrays.asList (clazzes));
        }
        public Class[] getClasses() {
            return clazzes;
        }
        public String toString() {
            StringBuffer buf = new StringBuffer("ActualList["); // NOI18N
            buf.append(origin);
            buf.append(": "); // NOI18N
            for (int i = 0; i < clazzes.length; i++) {
                if (i > 0) {
                    buf.append(", "); // NOI18N
                }
                buf.append(clazzes[i].getName());
            }
            buf.append("]"); // NOI18N
            return buf.toString();
        }
    }
    
    private static final ErrorManager err = ErrorManager.getDefault().getInstance("org.netbeans.modules.apisupport.beanbrowser.CookieClassList"); // NOI18N
    private static Lookup.Result clazzes = null; // Lookup.Result<CookieClassList.ActualList>
    public static Class[] getCookieClasses() {
        if (clazzes == null) {
            err.log("Looking up ActualList instances...");
            clazzes = Lookup.getDefault().lookup(new Lookup.Template(CookieClassList.ActualList.class));
        }
        List cookies = new ArrayList(); // List<Class>
        Collection actualLists = clazzes.allInstances(); // Collection<ActualList>
        err.log("actualLists=" + actualLists);
        if (actualLists.isEmpty()) {
            err.log(ErrorManager.WARNING, "Warning: #11965 still broken, Bean Browser may not show cookies");
        }
        Iterator it = actualLists.iterator();
        while (it.hasNext()) {
            ActualList list = (ActualList)it.next();
            cookies.addAll(Arrays.asList(list.getClasses()));
        }
        //System.err.println("Cookies: " + cookies);
        return (Class[])cookies.toArray(new Class[cookies.size()]);
    }
    
}

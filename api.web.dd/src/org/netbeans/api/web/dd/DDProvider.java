/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.api.web.dd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.netbeans.modules.web.dd.impl.WebAppProxy;
import org.openide.filesystems.*;
import org.xml.sax.*;
import java.util.Map;
import org.openide.util.NbBundle;

/**
 * Provides access to Deployment Descriptor root ({@link org.netbeans.api.web.dd.WebApp} object)
 *
 * @author  Milan Kuchtiak
 */

public final class DDProvider {
    private static DDProvider ddProvider;
    private Map ddMap;
    private static final String EXCEPTION_PREFIX="version:"; //NOI18N
    
    /** Creates a new instance of WebModule */
    private DDProvider() {
        ddMap=new java.util.WeakHashMap(5);
    }
    
    /**
    * Accessor method for DDProvider singleton
    * @return DDProvider object
    */
    public static synchronized DDProvider getDefault() {
        if (ddProvider==null) ddProvider = new DDProvider();
        return ddProvider;
    }
    
    /**
     * Returns the root of deployment descriptor bean graph for file object.
     *
     * @param fo FileObject representing the web.xml file
     * @return WebApp object - root of the deployment descriptor bean graph
     */
    public WebApp getDDRoot(FileObject fo) throws java.io.IOException, SAXException {
        WebAppProxy webApp = (WebAppProxy)ddMap.get(fo);
        if (webApp!=null) return webApp;
        fo.addFileChangeListener(new FileChangeAdapter(){
            public void fileChanged(FileEvent evt) {
                FileObject fo=evt.getFile();
                try {
                    WebAppProxy webApp = (WebAppProxy) ddMap.get(fo);
                    if (webApp!=null) {
                        String version = getVersion(fo.getInputStream());
                        // replacing original file in proxy WebApp
                        if (!version.equals(webApp.getVersion())) {
                            webApp.setOriginal(createWebApp(fo.getInputStream(),version));
                        }
                    }
                } 
                catch (java.io.IOException ex){}
                catch (org.xml.sax.SAXException ex){}
            }
        });
        WebApp original = createWebApp(fo.getInputStream(), getVersion(fo.getInputStream()));
        webApp=new WebAppProxy(original);
        ddMap.put(fo, webApp);
        return webApp;
    }
    
    public WebApp getDDRoot(File f) throws IOException, SAXException {
        return createWebApp(new FileInputStream(f), getVersion(new FileInputStream(f)));
    }
    
    private static WebApp createWebApp(java.io.InputStream is, String version) throws java.io.IOException{
        WebApp webApp=null;
        if (WebApp.VERSION_2_3.equals(version)) {
            webApp = org.netbeans.modules.web.dd.impl.model_2_3.WebApp.createGraph(is);
        } else {
            webApp = org.netbeans.modules.web.dd.impl.model_2_4.WebApp.createGraph(is);
        }
        return webApp;
    }
    
    private static String getVersion(java.io.InputStream is) throws java.io.IOException, SAXException {
        javax.xml.parsers.SAXParserFactory fact = javax.xml.parsers.SAXParserFactory.newInstance();
        fact.setValidating(false);
        try {
            javax.xml.parsers.SAXParser parser = fact.newSAXParser();
            try {
                parser.parse(is,new Handler());
            } catch (SAXException ex) {
                is.close();
                String message = ex.getMessage();
                if (message!=null && message.startsWith(EXCEPTION_PREFIX))
                    return message.substring(EXCEPTION_PREFIX.length());
                else throw new SAXException(NbBundle.getMessage(DDProvider.class, "MSG_cannotParse"),ex);
            }
            throw new SAXException(NbBundle.getMessage(DDProvider.class, "MSG_cannotFindRoot"));
        } catch(javax.xml.parsers.ParserConfigurationException ex) {
            throw new SAXException(NbBundle.getMessage(DDProvider.class, "MSG_parserProblem"),ex);
        }
    }
    
    private static class Handler extends org.xml.sax.helpers.DefaultHandler {
        public void startElement(String uri, String localName, String rawName, Attributes atts) throws SAXException {
            if ("web-app".equals(rawName)) { //NOI18N
                String version = atts.getValue("version"); //NOI18N
                throw new SAXException(EXCEPTION_PREFIX+(version==null?WebApp.VERSION_2_3:version));
            }
        }
    }
}
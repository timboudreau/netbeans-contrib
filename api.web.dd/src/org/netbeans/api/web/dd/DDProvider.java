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
import java.lang.ref.WeakReference;
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
     * Returns the root of deployment descriptor bean graph for given file object.
     * The method is useful for clints planning to read only the deployment descriptor
     * or to listen to the changes.
     * @param fo FileObject representing the web.xml file
     * @return WebApp object - root of the deployment descriptor bean graph
     */
    public WebApp getDDRoot(FileObject fo) throws java.io.IOException {
        
        WebAppProxy webApp = getFromCache (fo);
        if (webApp!=null) return webApp;
        
        fo.addFileChangeListener(new FileChangeAdapter() {
            public void fileChanged(FileEvent evt) {
                FileObject fo=evt.getFile();
                try {
                    WebAppProxy webApp = getFromCache (fo);
                    if (webApp!=null) {
                        String version = null;
                        try {
                            version = getVersion(fo.getInputStream());
                            // preparsing
                            SAXParseException error = parse(fo);
                            if (error!=null) {
                                webApp.setError(error);
                                webApp.setStatus(WebApp.STATE_INVALID_PARSABLE);
                            } else {
                                webApp.setError(null);
                                webApp.setStatus(WebApp.STATE_VALID);
                            }
                            WebApp original = createWebApp(fo.getInputStream(), version);
                            // replacing original file in proxy WebApp
                            if (!version.equals(webApp.getVersion())) {
                                webApp.setOriginal(original);
                            } else {// the same version
                                // replacing original file in proxy WebApp
                                if (webApp.getOriginal()==null) {
                                    webApp.setOriginal(original);
                                } else {
                                    webApp.getOriginal().merge(original,WebApp.MERGE_UPDATE);
                                }
                            }
                        } catch (SAXException ex) {
                            if (ex instanceof SAXParseException) {
                                webApp.setError((SAXParseException)ex);
                            } else if ( ex.getException() instanceof SAXParseException) {
                                webApp.setError((SAXParseException)ex.getException());
                            }
                            webApp.setStatus(WebApp.STATE_INVALID_UNPARSABLE);
                            webApp.setOriginal(null);
                            webApp.setProxyVersion(version);
                        }
                    }
                } catch (java.io.IOException ex){}
            }
        });
        
        String version=null;
        try {
            version = getVersion(fo.getInputStream());
            // preparsing
            SAXParseException error = parse(fo);
            WebApp original = createWebApp(fo.getInputStream(), version);
            webApp=new WebAppProxy(original,version);
            if (error!=null) {
                webApp.setStatus(WebApp.STATE_INVALID_PARSABLE);
                webApp.setError(error);
            }
        } catch (SAXException ex) {
            webApp = new WebAppProxy(null,version);
            webApp.setStatus(WebApp.STATE_INVALID_UNPARSABLE);
            if (ex instanceof SAXParseException) {
                webApp.setError((SAXParseException)ex);
            } else if ( ex.getException() instanceof SAXParseException) {
                webApp.setError((SAXParseException)ex.getException());
            }
        }
        ddMap.put(fo, new WeakReference (webApp));
        return webApp;
    }

    /**
     * Returns the root of deployment descriptor bean graph for given file object.
     * The method is useful for clients planning to modify the deployment descriptor.
     * Finally the {@link org.netbeans.api.web.dd.WebApp#write(org.openide.filesystems.FileObject)} should be used
     * for writing the changes.
     * @param fo FileObject representing the web.xml file
     * @return WebApp object - root of the deployment descriptor bean graph
     */
    public WebApp getDDRootCopy(FileObject fo) throws java.io.IOException {
        return (WebApp)getDDRoot(fo).clone();
    }

    private WebAppProxy getFromCache (FileObject fo) {
        WeakReference wr = (WeakReference) ddMap.get(fo);
        if (wr == null) {
            return null;
        }
        WebAppProxy webApp = (WebAppProxy) wr.get ();
        if (webApp == null) {
            ddMap.remove (fo);
        }
        return webApp;
    }
    
    /**
     * Returns the root of deployment descriptor bean graph for java.io.File object.
     *
     * @param f File representing the web.xml file
     * @return WebApp object - root of the deployment descriptor bean graph
     */    
    public WebApp getDDRoot(File f) throws IOException, SAXException {
        return createWebApp(new FileInputStream(f), getVersion(new FileInputStream(f)));
    }
    
    // PENDING j2eeserver needs BaseBean - this is a temporary workaround to avoid dependency of web project on DD impl
    /**  Convenient method for getting the BaseBean object from CommonDDBean object
     * @deprecated DO NOT USE - TEMPORARY WORKAROUND !!!!
     */
    public org.netbeans.modules.schema2beans.BaseBean getBaseBean(org.netbeans.api.web.dd.common.CommonDDBean bean) {
        if (bean instanceof org.netbeans.modules.schema2beans.BaseBean) return (org.netbeans.modules.schema2beans.BaseBean)bean;
        else if (bean instanceof WebAppProxy) return (org.netbeans.modules.schema2beans.BaseBean) ((WebAppProxy)bean).getOriginal();
        return null;
    }

    private static WebApp createWebApp(java.io.InputStream is, String version) throws java.io.IOException {
        if (WebApp.VERSION_2_3.equals(version)) {
            return org.netbeans.modules.web.dd.impl.model_2_3.WebApp.createGraph(is);
        } else {
            return org.netbeans.modules.web.dd.impl.model_2_4.WebApp.createGraph(is);
        }
    }
    
    /** Parsing just for detecting the version  SAX parser used
    */
    private static String getVersion(java.io.InputStream is) throws java.io.IOException, SAXException {
        javax.xml.parsers.SAXParserFactory fact = javax.xml.parsers.SAXParserFactory.newInstance();
        fact.setValidating(false);
        try {
            javax.xml.parsers.SAXParser parser = fact.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setContentHandler(new VersionHandler());
            reader.setEntityResolver(DDResolver.getInstance());
            try {
                reader.parse(new InputSource(is));
            } catch (SAXException ex) {
                is.close();
                String message = ex.getMessage();
                if (message!=null && message.startsWith(EXCEPTION_PREFIX))
                    return message.substring(EXCEPTION_PREFIX.length());
                else throw new SAXException(NbBundle.getMessage(DDProvider.class, "MSG_cannotParse"),ex);
            }
            is.close();
            throw new SAXException(NbBundle.getMessage(DDProvider.class, "MSG_cannotFindRoot"));
        } catch(javax.xml.parsers.ParserConfigurationException ex) {
            throw new SAXException(NbBundle.getMessage(DDProvider.class, "MSG_parserProblem"),ex);
        }
    }
    
    private static class VersionHandler extends org.xml.sax.helpers.DefaultHandler {
        public void startElement(String uri, String localName, String rawName, Attributes atts) throws SAXException {
            if ("web-app".equals(rawName)) { //NOI18N
                String version = atts.getValue("version"); //NOI18N
                throw new SAXException(EXCEPTION_PREFIX+(version==null?WebApp.VERSION_2_3:version));
            }
        }
    }
  
    private static class DDResolver implements EntityResolver {
        static DDResolver resolver;
        static synchronized DDResolver getInstance() {
            if (resolver==null) {
                resolver=new DDResolver();
            }
            return resolver;
        }        
        public InputSource resolveEntity (String publicId, String systemId) {
            if ("-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN".equals(publicId)) { //NOI18N
                  // return a special input source
             return new InputSource("nbres:/org/netbeans/modules/web/dd/impl/resources/web-app_2_3.dtd"); //NOI18N
            } else if ("-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN".equals(publicId)) { //NOI18N
                  // return a special input source
             return new InputSource("nbres:/org/netbeans/modules/web/dd/impl/resources/web-app_2_2.dtd"); //NOI18N
            } else if ("http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd".equals(systemId)) {
                return new InputSource("nbres:/org/netbeans/modules/web/dd/impl/resources/web-app_2_4.xsd"); //NOI18N
            } else {
                // use the default behaviour
                return null;
            }
        }
    }
    
    private static class ErrorHandler implements org.xml.sax.ErrorHandler {
        private int errorType=-1;
        SAXParseException error;

        public void warning(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException {
            if (errorType<0) {
                errorType=0;
                error=sAXParseException;
            }
            //throw sAXParseException;
        }
        public void error(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException {
            if (errorType<1) {
                errorType=1;
                error=sAXParseException;
            }
            //throw sAXParseException;
        }        
        public void fatalError(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException {
            errorType=2;
            throw sAXParseException;
        }
        
        public int getErrorType() {
            return errorType;
        }
        public SAXParseException getError() {
            return error;
        }        
    }
    
    public SAXParseException parse (FileObject fo) 
            throws org.xml.sax.SAXException, java.io.IOException {
        javax.xml.parsers.SAXParserFactory fact = org.apache.xerces.jaxp.SAXParserFactoryImpl.newInstance();
        fact.setValidating(true);
        fact.setNamespaceAware(true);
        DDProvider.ErrorHandler errorHandler = new DDProvider.ErrorHandler();
        try {
            javax.xml.parsers.SAXParser parser = fact.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setErrorHandler(errorHandler);
            reader.setEntityResolver(DDProvider.DDResolver.getInstance());
            reader.setFeature("http://apache.org/xml/features/validation/schema", true); // NOI18N
            reader.parse(new InputSource(fo.getInputStream()));
            SAXParseException error = errorHandler.getError();
            if (error!=null) return error;
        } catch (SAXException ex) {
            throw ex;
        } catch(javax.xml.parsers.ParserConfigurationException ex) {
            throw new SAXException(NbBundle.getMessage(DDProvider.class, "MSG_parserProblem"),ex);
        }
        return null;
    }

}
/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.suggestions;
 
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import org.openide.loaders.XMLDataObject.Processor;
import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;

import java.net.URL;
import javax.xml.parsers.SAXParserFactory;
import org.openide.TopManager;
import org.openide.cookies.InstanceCookie;
import org.openide.loaders.XMLDataObject;



/** Processor of the Suggestion Type XML file.
 * The parsing result is an instance of a SuggestionType object
 * <p>
 * Based on AnnotationTypeProcessor in the editor package.
 * <p>
 *
 * @author  Tor Norbye, David Konecny, Peter Nejedly
 */
public final class SuggestionTypeProcessor implements InstanceCookie, Processor {
    static final String DTD_PUBLIC_ID = "-//NetBeans//DTD suggestion type 1.0//EN"; // NOI18N
    static final String DTD_SYSTEM_ID = "http://www.netbeans.org/dtds/suggestion-type-1_0.dtd"; // NOI18N
    
    static final String TAG_TYPE = "type"; //NOI18N
    static final String ATTR_TYPE_NAME = "name"; // NOI18N
    static final String ATTR_TYPE_LOCALIZING_BUNDLE = "localizing_bundle"; // NOI18N
    static final String ATTR_TYPE_DESCRIPTION_KEY = "description_key"; // NOI18N
    static final String ATTR_TYPE_ICON = "icon"; // NOI18N

    /** XML data object. */
    private XMLDataObject xmlDataObject;
    
    /**
     * Suggestion type created from XML file.
     */
    private SuggestionType  suggestionType;

    /** When the XMLDataObject creates new instance of the processor,
     * it uses this method to attach the processor to the data object.
     *
     * @param xmlDO XMLDataObject
     */
    public void attachTo(XMLDataObject xmlDO) {
        xmlDataObject = xmlDO;
    }
    
    /** Create an instance.
     * @return the instance of type {@link #instanceClass}
     * @exception IOException if an I/O error occured
     * @exception ClassNotFoundException if a class was not found
     */
    public Object instanceCreate() throws IOException, ClassNotFoundException {
        if (suggestionType != null)
            return suggestionType;

        parse();
        return suggestionType;
    }
    
    /** The representation type that may be created as instances.
     * Can be used to test whether the instance is of an appropriate
     * class without actually creating it.
     *
     * @return the representation class of the instance
     * @exception IOException if an I/O error occurred
     * @exception ClassNotFoundException if a class was not found
     */
    public Class instanceClass() {
        return SuggestionType.class;
    }
    
    /** The bean name for the instance.
     * @return the name
     */
    public String instanceName() {
        return instanceClass().getName();
    }

    ////////////////////////////////////////////////////////////////////////

    private synchronized SuggestionType parse() {
        if (suggestionType == null) {
            Handler h = new Handler();

            try {
		Parser xp;
                SAXParserFactory factory = SAXParserFactory.newInstance ();
                factory.setValidating (false);
                factory.setNamespaceAware(false);
                xp = factory.newSAXParser ().getParser ();
                xp.setEntityResolver(h);
                xp.setDocumentHandler(h);
                xp.setErrorHandler(h);
                xp.parse(new InputSource(xmlDataObject.getPrimaryFile().getInputStream()));
                //st.putProp(SuggestionType.PROP_FILE, xmlDataObject.getPrimaryFile());
                //suggestionType = st;
                suggestionType = h.getSuggestionType();
                //suggestionType.setFile(xmlDataObject.getPrimaryFile()); // XXX Needed?
            } catch (Exception e) { 
                TopManager.getDefault().getErrorManager().notify(e);
            }

        }
        return suggestionType;
    }
    
    private static class Handler extends HandlerBase {
        private SuggestionType suggestionType = null;
        
        SuggestionType getSuggestionType() {
            return suggestionType;
        }

        public void startElement(String name, AttributeList amap) throws SAXException {
            if (!TAG_TYPE.equals(name)) {
                throw new SAXException("malformed SuggestionType xml file"); // NOI18N
            }
            // basic properties
            String id = amap.getValue(ATTR_TYPE_NAME);
            
            // localization stuff
            String localizer = amap.getValue(ATTR_TYPE_LOCALIZING_BUNDLE);
            String key = amap.getValue(ATTR_TYPE_DESCRIPTION_KEY);
            
            // icon
            URL icon = null;
            String uri = amap.getValue(ATTR_TYPE_ICON);
            if (uri != null) {
                try {
                    icon = new URL(uri);
                } catch (MalformedURLException ex) {
                    SAXException saxe = new SAXException(ex);
                    TopManager.getDefault().getErrorManager().
                        copyAnnotation(saxe, ex);
                    throw saxe;
                }
            }

            suggestionType = new SuggestionType(id, localizer, key, icon);
        }

        public InputSource resolveEntity(java.lang.String pid,
                                         java.lang.String sid) throws SAXException {
            if (DTD_PUBLIC_ID.equals(pid)) {
                return new InputSource(new ByteArrayInputStream(new byte[0]));
            }            
            return new InputSource (sid);            
        }
    }
    
}

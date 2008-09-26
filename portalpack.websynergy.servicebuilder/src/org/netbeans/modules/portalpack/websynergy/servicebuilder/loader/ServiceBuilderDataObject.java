/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.portalpack.websynergy.servicebuilder.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.SAXParseError;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.ServiceBuilderEditorSupport;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.helper.ServiceBuilderHelper;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Cookie;
import org.openide.util.Lookup;
import org.openide.text.DataEditorSupport;
import org.w3c.dom.Document;
import org.xml.sax.*;

public class ServiceBuilderDataObject extends MultiDataObject implements org.openide.nodes.CookieSet.Factory {

    
    private boolean documentDirty = true;
    private boolean documentValid=true;
    protected boolean nodeDirty = false;
    private InputStream inputStream;
    /** Editor support for text data object. */
    private transient ServiceBuilderEditorSupport editorSupport;
    private SAXParseError error;
    
    private ServiceBuilderHelper helper;
    //private FacesConfig lastGoodFacesConfig = null;
    
    /** Property name for property documentValid */
    public static final String PROP_DOC_VALID = "documentValid"; // NOI18N
    public ServiceBuilderDataObject(FileObject pf, ServiceBuilderFlowDataLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        getCookieSet().add(ServiceBuilderEditorSupport.class, this);
        initServiceBuilderHelper(pf);
         // Creates Check XML and Validate XML context actions
      ///  InputSource in = DataObjectAdapters.inputSource(this);
      ////  CheckXMLCookie checkCookie = new CheckXMLSupport(in);
     ////   getCookieSet().add(checkCookie);
      ///  ValidateXMLCookie validateCookie = new ValidateXMLSupport(in);
     ////   getCookieSet().add(validateCookie);
      ////  getCookieSet().assign(FileEncodingQueryImplementation.class, XmlFileEncodingQueryImpl.singleton());
    }

    @Override
    protected Node createNodeDelegate() {
        return new ServiceBuilderDataNode(this, getLookup());
    }

     /** Implements <code>CookieSet.Factory</code> interface. */
    public Node.Cookie createCookie(Class clazz) {
        if(clazz.isAssignableFrom(ServiceBuilderEditorSupport.class))
            return getEditorSupport();
        else
            return null;
    }
    
    public void initServiceBuilderHelper(FileObject pf) {
        long t1 = System.currentTimeMillis();
        helper = new ServiceBuilderHelper(pf);
        long t2 = System.currentTimeMillis();
        
        System.out.println("Time taken to parse ----------------------- "+(t2-t1));
    }
    
    public ServiceBuilderHelper getServiceBuilderHelper() {
        return helper;
    }
    /** Gets editor support for this data object. */
    public ServiceBuilderEditorSupport getEditorSupport() {
        if(editorSupport == null) {
            synchronized(this) {
                if(editorSupport == null)
                    editorSupport = new ServiceBuilderEditorSupport(this);
            }
        }
        
        return editorSupport;
    }
    
   /* public FacesConfig getFacesConfig() throws java.io.IOException {
        if (lastGoodFacesConfig == null)
            parsingDocument();
        return lastGoodFacesConfig;
    }*/
    
    /** This method is used for obtaining the current source of xml document.
    * First try if document is in the memory. If not, provide the input from
    * underlayed file object.
    * @return The input source from memory or from file
    * @exception IOException if some problem occurs
    */
    protected InputStream prepareInputSource() throws java.io.IOException {
        if ((getEditorSupport() != null) && (getEditorSupport().isDocumentLoaded())) {
            // loading from the memory (Document)
            return getEditorSupport().getInputStream();
        }
        else {
            return getPrimaryFile().getInputStream();
        }
    }
    
    /** This method has to be called everytime after prepareInputSource calling.
     * It is used for closing the stream, because it is not possible to access the
     * underlayed stream hidden in InputSource.
     * It is save to call this method without opening.
     */
    protected void closeInputSource() {
        InputStream is = inputStream;
        if (is != null) {
            try {
                is.close();
            }
            catch (IOException e) {
                // nothing to do, if exception occurs during saving.
            }
            if (is == inputStream) {
                inputStream = null;
            }
        }
    }
    
    /** This method parses XML document and calls updateNode method which
    * updates corresponding Node.
    */
    public void parsingDocument(){
        error = null;
        try {
            error = updateNode(prepareInputSource());
        }
        catch (Exception e) {
            Logger.getLogger("global").log(Level.INFO, null, e);
            setDocumentValid(false);
            return;
        }
        finally {
            closeInputSource();
            documentDirty=false;
        }
        if (error == null){
            setDocumentValid(true);
        }else {
            setDocumentValid(false);
        }
        setNodeDirty(false);
    }
    
    public void setDocumentValid (boolean valid){
        if (documentValid!=valid) {
            if (valid)
                repairNode();
            documentValid=valid;
            firePropertyChange (PROP_DOC_VALID, !documentValid ? Boolean.TRUE : Boolean.FALSE, documentValid ? Boolean.TRUE : Boolean.FALSE);
        }
    }
    
    /** This method repairs Node Delegate (usually after changing document by property editor)
    */
    protected void repairNode(){
        // PENDING: set the icon in Node
        // ((DataNode)getNodeDelegate()).setIconBase (getIconBaseForValidDocument());
        org.openide.awt.StatusDisplayer.getDefault().setStatusText("");  // NOI18N
    /*    if (inOut!=null) {
            inOut.closeInputOutput();
            errorAnnotation.detach();
        }*/
    }
    
    private org.w3c.dom.Document getDomDocument(InputStream inputSource) throws SAXParseException {
        try {
            // creating w3c document
            org.w3c.dom.Document doc = org.netbeans.modules.schema2beans.GraphManager.
                createXmlDocument(new org.xml.sax.InputSource(inputSource), false, null /*jsfCatalog*/,
                new J2eeErrorHandler(this));
            return doc;
        } catch(Exception e) {
            //    XXX Change that
            throw new SAXParseException(e.getMessage(), new org.xml.sax.helpers.LocatorImpl());
        }
    }
    

    /** Update the node from document. This method is called after document is changed.
    * @param is Input source for the document
    * @return number of the line with error (document is invalid), 0 (xml document is valid)
    */
    // TODO is prepared for handling arrors, but not time to finish it.
    protected SAXParseError updateNode(InputStream is) throws java.io.IOException{
        try {
            Document doc = getDomDocument(is);
            
            //TODO new api
            //JSF version = JSFCatalog.extractVersion(doc);
            //check version, use impl class to create graph
            //TODO new API
//            if (FacesConfig.VERSION_1_1.equals(version)) {
//                lastGoodFacesConfig = org.netbeans.modules.web.jsf.config.model_1_1.FacesConfig.createGraph(doc);
//            }
//            if (FacesConfig.VERSION_1_0.equals(version)) {
//                lastGoodFacesConfig = org.netbeans.modules.web.jsf.config.model_1_1.FacesConfig.createGraph(doc);
//            }
//            if (FacesConfig.VERSION_1_2.equals(version)) {
//                lastGoodFacesConfig = org.netbeans.modules.web.jsf.config.model_1_2.FacesConfig.createGraph(doc);
//            }
        }
        catch(SAXParseException ex) {
            return new SAXParseError(ex);
        } catch(SAXException ex) {
            throw new IOException();
        }
        return null;
    }
   
    public boolean isDocumentValid(){
        return documentValid;
    }
    /** setter for property documentDirty. Method updateDocument() usually setsDocumentDirty to false
    */
    public void setDocumentDirty(boolean dirty){
        documentDirty=dirty;
    }

    /** Getter for property documentDirty.
    * @return Value of property documentDirty.
    */
    public boolean isDocumentDirty(){
        return documentDirty;
    }
    
    /** Getter for property nodeDirty.
    * @return Value of property nodeDirty.
    */
    public boolean isNodeDirty(){
        return nodeDirty;
    }

    /** Setter for property nodeDirty.
     * @param dirty New value of property nodeDirty.
     */
    public void setNodeDirty(boolean dirty){
        nodeDirty=dirty;
    }
    public org.openide.nodes.CookieSet getCookieSet0() {
        return getCookieSet();
    }

    @Override
    public Lookup getLookup() {
        return getCookieSet().getLookup();
    }

    
    public static class J2eeErrorHandler implements ErrorHandler {

        private ServiceBuilderDataObject dataObject;

        public J2eeErrorHandler(ServiceBuilderDataObject obj) {
             dataObject=obj;
        }

        public void error(SAXParseException exception) throws SAXException {
            dataObject.createSAXParseError(exception);
            throw exception;
        }

        public void fatalError(SAXParseException exception) throws SAXException {
            dataObject.createSAXParseError(exception);
            throw exception;
        }

        public void warning(SAXParseException exception) throws SAXException {
            dataObject.createSAXParseError(exception);
            throw exception;
        }
    }
    
    private void createSAXParseError(SAXParseException error){
        this.error = new SAXParseError(error);
    }
    
}

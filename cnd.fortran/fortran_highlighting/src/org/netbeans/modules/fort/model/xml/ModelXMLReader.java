
package org.netbeans.modules.fort.model.xml;

import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Andrey Gubichev
 */
public class ModelXMLReader {
       
    private final static String START_ELEMENT_SUFFIX = "Start";
    private final static String END_ELEMENT_SUFFIX = "End";
    
    public void readModelXml(Reader src, XMLReaderContext ctx)  {
        XMLReader xmlReader = null;
        
        try {
            SAXParserFactory saxFactory = SAXParserFactory.newInstance();
            
            saxFactory.setValidating(false);
            SAXParser parser = saxFactory.newSAXParser();
            
            xmlReader = parser.getXMLReader();
            xmlReader.setContentHandler(new ModelXMLContentHandler(ctx));
            xmlReader.parse(new InputSource(src));
        } catch (ModelXMLReaderException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new ModelXMLReaderException(ex);
        }                       
    }       
    
    
    private class ModelXMLContentHandler extends DefaultHandler {       
        
        private List<XMLReaderContext> ctxStack;
        private XMLReaderContext ctxCur;
        
        public ModelXMLContentHandler(XMLReaderContext startCtx) {
            ctxCur = startCtx;
            ctxStack = new LinkedList<XMLReaderContext>();
        }
        
        public void startElement (String uri, String localName,
			          String qName, Attributes attributes)
	      throws SAXException {
        
             ctxStack.add(ctxCur);            
             Object result = invokeCtxMethod(qName + START_ELEMENT_SUFFIX,
                                             attributes, Attributes.class );
                
             if (result instanceof XMLReaderContext) {
                 ctxCur = (XMLReaderContext) result;
             }                                                                             
        }

        public void endElement (String uri, String localName, String qName)
               throws SAXException {
            
            Object backCtx = ctxCur;
            ctxCur = ctxStack.remove(ctxStack.size() - 1);
            
            invokeCtxMethod(qName + END_ELEMENT_SUFFIX,
                            backCtx, backCtx.getClass()); 
            
        }
        
        private Object invokeCtxMethod(String name, Object arg, Class argClass)  {
            Object result = null;
            Method method = null;
            
            if (ctxCur == null || arg == null) 
                return result;
            
            try {
                method = ctxCur.getClass().getMethod(name, argClass);                       
            } catch (Exception ex) {  
                 return null;
            }
            
            try {
                result = method.invoke(ctxCur, arg);
            } catch (InvocationTargetException ex) {
                throw new ModelXMLReaderException(ex.getTargetException().toString());
            } 
            catch (Exception ex) {
                throw new ModelXMLReaderException(ex);
            } 
            
            return result;
        }              
    }
}

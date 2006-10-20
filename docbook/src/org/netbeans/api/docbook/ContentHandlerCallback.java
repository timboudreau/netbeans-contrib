package org.netbeans.api.docbook;

import org.xml.sax.ContentHandler;


/**
 * Callback implementation which provides a SAX ContentHandler.
 * If the handler passed to the constructor also implements DTDHandler
 * and/or ErrorHandler, it will also receive notifications for those
 * classes' events.
 */ 
public abstract class ContentHandlerCallback<T extends ContentHandler> extends Callback {
    public ContentHandlerCallback(ContentHandler handler) {
        super (handler);
        if (handler == null) {
            throw new NullPointerException("Handler null");
        }
    }
}
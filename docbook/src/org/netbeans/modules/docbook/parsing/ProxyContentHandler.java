/*
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
 */
package org.netbeans.modules.docbook.parsing;

import java.util.Collection;
import org.netbeans.api.docbook.Callback;
import org.netbeans.modules.docbook.*;
import org.netbeans.modules.docbook.parsing.ParseJobFactory.ParseJobImpl;
import org.openide.filesystems.FileObject;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * ContentHandler/ErrorHandler/DtdHandler that proxies an array of others, to
 * allow multiple batch jobs to process a document in a single pass.
 *
 * @author Tim Boudreau
 */
final class ProxyContentHandler extends DefaultHandler implements ContentHandler, ErrorHandler, DTDHandler {
    private final ParseJobFactory.ParseJobImpl<Callback<ContentHandler>>[] jobs;
    private final boolean[] fails;
    private final FileObject ob;
    private int activeCount;
    private final boolean dtdInserted;
    ProxyContentHandler(Collection <ParseJobImpl<Callback<ContentHandler>>> jobs, FileObject ob, boolean dtdInserted) {
        this.jobs = (ParseJobImpl<Callback<ContentHandler>>[]) jobs.toArray (new ParseJobImpl[jobs.size()]);
        fails = new boolean[this.jobs.length];
        activeCount = this.jobs.length;
        this.ob = ob;
        this.dtdInserted = dtdInserted;
        assert jobs != null;
        assert ob != null;
    }

    void start() {
        for (int i = 0; i < jobs.length; i++) {
            try {
                jobs[i].setRunning(true);
                jobs[i].setEnqueued(false);
                jobs[i].doStart(jobs[i].callback, ob);
            } catch (Exception e) {
                fails[i] = true;
                jobs[i].doFail(jobs[i].callback, e, ob);
            }
        }
    }

    void done() {
        for (int i = 0; i < jobs.length; i++) {
            try {
                jobs[i].setRunning(false);
                jobs[i].doDone(jobs[i].callback, ob);
            } catch (Exception e) {
                fails[i] = true;
                jobs[i].doFail(jobs[i].callback, e, ob);
            }
        }
    }

    private Locator locator;
    public void setDocumentLocator(Locator locator) {
        assert activeCount >= 0;
        this.locator = dtdInserted ? new OffByOneLocator(locator) : locator;
        if (activeCount == 0) return;
        for (int i = 0; i < jobs.length; i++) {
            boolean wasCancelled = fails[i];
            fails[i] |= jobs[i].callback.isCancelled();
            if (!wasCancelled && fails[i]) {
                activeCount--;
            }
            if (!fails[i]) {
                jobs[i].callback.getProcessor().setDocumentLocator(this.locator);
            }
        }
    }

    public void startDocument() throws SAXException {
        assert activeCount >= 0;
        if (activeCount == 0) return;
        for (int i = 0; i < jobs.length; i++) {
            fails[i] |= jobs[i].callback.isCancelled();
            boolean wasCancelled = fails[i];
            fails[i] |= jobs[i].callback.isCancelled();
            if (!wasCancelled && fails[i]) {
                activeCount--;
            }
            if (!fails[i]) {
                try {
                    jobs[i].callback.getProcessor().startDocument();
                } catch (SAXException e) {
                    jobs[i].doFail(jobs[i].callback, e, ob);
                    fails[i] = true;
                    activeCount--;
                }
            }
        }
    }

    public void endDocument() throws SAXException {
        assert activeCount >= 0;
        if (activeCount == 0) return;
        for (int i = 0; i < jobs.length; i++) {
            boolean wasCancelled = fails[i];
            fails[i] |= jobs[i].callback.isCancelled();
            if (!wasCancelled && fails[i]) {
                activeCount--;
            }
            if (!fails[i]) {
                try {
                    jobs[i].callback.getProcessor().startDocument();
                } catch (SAXException e) {
                    jobs[i].doFail(jobs[i].callback, e, ob);
                    fails[i] = true;
                    activeCount--;
                }
            }
        }
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        assert activeCount >= 0;
        if (activeCount == 0) return;
        for (int i = 0; i < jobs.length; i++) {
            boolean wasCancelled = fails[i];
            fails[i] |= jobs[i].callback.isCancelled();
            if (!wasCancelled && fails[i]) {
                activeCount--;
            }
            if (!fails[i]) {
                try {
                    jobs[i].callback.getProcessor().startPrefixMapping (prefix, uri);
                } catch (SAXException e) {
                    jobs[i].doFail(jobs[i].callback, e, ob);
                    fails[i] = true;
                    activeCount--;
                }
            }
        }
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        assert activeCount >= 0;
        if (activeCount == 0) return;
        for (int i = 0; i < jobs.length; i++) {
            boolean wasCancelled = fails[i];
            fails[i] |= jobs[i].callback.isCancelled();
            if (!wasCancelled && fails[i]) {
                activeCount--;
            }
            if (!fails[i]) {
                try {
                    jobs[i].callback.getProcessor().endPrefixMapping(prefix);
                } catch (SAXException e) {
                    jobs[i].doFail(jobs[i].callback, e, ob);
                    fails[i] = true;
                    activeCount--;
                }
            }
        }
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        assert activeCount >= 0;
        if (activeCount == 0) {
            return;
        }
        for (int i = 0; i < jobs.length; i++) {
            boolean wasCancelled = fails[i];
            fails[i] |= jobs[i].callback.isCancelled();
            if (!wasCancelled && fails[i]) {
                activeCount--;
            }
            if (!fails[i]) {
                try {
                    jobs[i].callback.getProcessor().startElement(uri, localName, qName, atts) ;
                } catch (SAXException e) {
                    e.printStackTrace();
                    jobs[i].doFail(jobs[i].callback, e, ob);
                    fails[i] = true;
                    activeCount--;
                }
            }
        }
    }

    boolean hasErrorHandlers = true;
    public void endElement(String uri, String localName, String qName) throws SAXException {
        assert activeCount >= 0;
        if (activeCount == 0) return;
        for (int i = 0; i < jobs.length; i++) {
            boolean wasCancelled = fails[i];
            fails[i] |= jobs[i].callback.isCancelled();
            if (!wasCancelled && fails[i]) {
                activeCount--;
            }
            if (!fails[i]) {
                try {
                    jobs[i].callback.getProcessor().endElement(uri, localName, qName);
                } catch (SAXException e) {
                    jobs[i].doFail(jobs[i].callback, e, ob);
                    fails[i] = true;
                    activeCount--;
                }
            }
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        assert activeCount >= 0;
        if (activeCount == 0) return;
        for (int i = 0; i < jobs.length; i++) {
            boolean wasCancelled = fails[i];
            fails[i] |= jobs[i].callback.isCancelled();
            if (!wasCancelled && fails[i]) {
                activeCount--;
            }
            if (!fails[i]) {
                try {
                    jobs[i].callback.getProcessor().characters (ch, start, length);
                } catch (SAXException e) {
                    jobs[i].doFail(jobs[i].callback, e, ob);
                    fails[i] = true;
                    activeCount--;
                }
            }
        }
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        assert activeCount >= 0;
        if (activeCount == 0) return;
        for (int i = 0; i < jobs.length; i++) {
            boolean wasCancelled = fails[i];
            fails[i] |= jobs[i].callback.isCancelled();
            if (!wasCancelled && fails[i]) {
                activeCount--;
            }
            if (!fails[i]) {
                try {
                    jobs[i].callback.getProcessor().ignorableWhitespace (ch, start, length);
                } catch (SAXException e) {
                    jobs[i].doFail(jobs[i].callback, e, ob);
                    fails[i] = true;
                    activeCount--;
                }
            }
        }
    }

    public void processingInstruction(String target, String data) throws SAXException {
        assert activeCount >= 0;
        if (activeCount == 0) return;
        for (int i = 0; i < jobs.length; i++) {
            boolean wasCancelled = fails[i];
            fails[i] |= jobs[i].callback.isCancelled();
            if (!wasCancelled && fails[i]) {
                activeCount--;
            }
            if (!fails[i]) {
                try {
                    jobs[i].callback.getProcessor().processingInstruction (target, data);
                } catch (SAXException e) {
                    jobs[i].doFail(jobs[i].callback, e, ob);
                    fails[i] = true;
                    activeCount--;
                }
            }
        }
    }

    public void skippedEntity(String name) throws SAXException {
        assert activeCount >= 0;
        if (activeCount == 0) return;
        for (int i = 0; i < jobs.length; i++) {
            boolean wasCancelled = fails[i];
            fails[i] |= jobs[i].callback.isCancelled();
            if (!wasCancelled && fails[i]) {
                activeCount--;
            }
            if (!fails[i]) {
                try {
                    jobs[i].callback.getProcessor().skippedEntity (name);
                } catch (SAXException e) {
                    jobs[i].doFail(jobs[i].callback, e, ob);
                    fails[i] = true;
                    activeCount--;
                }
            }
        }
    }

    public void warning(SAXParseException exception) throws SAXException {
        assert activeCount >= 0;
        if (activeCount == 0) return;
        if (hasErrorHandlers) {
            boolean isErrorHandler;
            boolean found = false;
            for (int i = 0; i < jobs.length; i++) {
                boolean wasCancelled = fails[i];
                fails[i] |= jobs[i].callback.isCancelled();
                if (!wasCancelled && fails[i]) {
                    activeCount--;
                }
                isErrorHandler = !fails[i] && jobs[i].callback.getProcessor()  instanceof ErrorHandler;
                found |= isErrorHandler;
                if (isErrorHandler && !fails[i]) {
                    try {
                        ((ErrorHandler) jobs[i].callback.getProcessor()).warning (exception);
                    }  catch (SAXException e) {
                        jobs[i].doFail(jobs[i].callback, e, ob);
                        fails[i] = true;
                        activeCount--;
                    }
                }
            }
            hasErrorHandlers &= found;
        }
    }

    public void error(SAXParseException exception) throws SAXException {
        assert activeCount >= 0;
        if (activeCount == 0) return;
        if (hasErrorHandlers) {
            boolean isErrorHandler;
            boolean found = false;
            for (int i = 0; i < jobs.length; i++) {
                boolean wasCancelled = fails[i];
                fails[i] |= jobs[i].callback.isCancelled();
                if (!wasCancelled && fails[i]) {
                    activeCount--;
                }
                isErrorHandler = !fails[i] && jobs[i].callback.getProcessor()  instanceof ErrorHandler;
                found |= isErrorHandler;
                if (isErrorHandler && !fails[i]) {
                    try {
                        ((ErrorHandler) jobs[i].callback.getProcessor()).error (exception);
                    }  catch (SAXException e) {
                        jobs[i].doFail(jobs[i].callback, e, ob);
                        fails[i] = true;
                        activeCount--;
                    }
                }
            }
            hasErrorHandlers &= found;
        }
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        assert activeCount >= 0;
        if (activeCount == 0) return;
        if (hasErrorHandlers) {
            boolean isErrorHandler;
            boolean found = false;
            for (int i = 0; i < jobs.length; i++) {
                boolean wasCancelled = fails[i];
                fails[i] |= jobs[i].callback.isCancelled();
                if (!wasCancelled && fails[i]) {
                    activeCount--;
                }
                isErrorHandler = !fails[i] && jobs[i].callback.getProcessor()  instanceof ErrorHandler;
                found |= isErrorHandler;
                if (isErrorHandler && !fails[i]) {
                    try {
                        ((ErrorHandler) jobs[i].callback.getProcessor()).fatalError(exception);
                    }  catch (SAXException e) {
                        jobs[i].doFail(jobs[i].callback, e, ob);
                        fails[i] = true;
                        activeCount--;
                    }
                }
            }
            hasErrorHandlers &= found;
        }
    }

    boolean hasDtdHandlers = true;
    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
        if (hasDtdHandlers) {
            assert activeCount >= 0;
            boolean isDtdHandler;
            boolean found = false;
            if (activeCount == 0) return;
            for (int i = 0; i < jobs.length; i++) {
                boolean wasCancelled = fails[i];
                fails[i] |= jobs[i].callback.isCancelled();
                if (!wasCancelled && fails[i]) {
                    activeCount--;
                }
                isDtdHandler = !fails[i] && jobs[i].callback.getProcessor()  instanceof DTDHandler;
                found |= isDtdHandler;
                if (isDtdHandler && !fails[i]) {
                    try {
                        ((DTDHandler) jobs[i].callback.getProcessor()).notationDecl(
                                name, publicId, systemId);
                    }  catch (SAXException e) {
                        jobs[i].doFail(jobs[i].callback, e, ob);
                        fails[i] = true;
                        activeCount--;
                    }
                }
            }
            hasDtdHandlers &= found;
        }
    }

    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
        if (hasDtdHandlers) {
            assert activeCount >= 0;
            boolean isDtdHandler;
            boolean found = false;
            if (activeCount == 0) return;
            for (int i = 0; i < jobs.length; i++) {
                boolean wasCancelled = fails[i];
                fails[i] |= jobs[i].callback.isCancelled();
                if (!wasCancelled && fails[i]) {
                    activeCount--;
                }
                isDtdHandler = !fails[i] && jobs[i].callback.getProcessor()  instanceof DTDHandler;
                found |= isDtdHandler;
                if (isDtdHandler && !fails[i]) {
                    try {
                        ((DTDHandler) jobs[i].callback.getProcessor()).unparsedEntityDecl(
                                name, publicId, systemId, notationName);
                    }  catch (SAXException e) {
                        jobs[i].doFail(jobs[i].callback, e, ob);
                        fails[i] = true;
                        activeCount--;
                    }
                }
            }
            hasDtdHandlers &= found;
        }
    }
    
    /** Handles the case where we have inserted a fake DTD into the 
     * xml sequence so that we can parse correctly - compensates for the virtual
     * line added at the top of the file.
     */
    private static final class OffByOneLocator implements Locator {
        private Locator proxy;
        OffByOneLocator (Locator proxy) {
            this.proxy = proxy;
            assert proxy != null;
        }
        
        public String getPublicId() {
            return proxy.getPublicId();
        }

        public String getSystemId() {
            return proxy.getSystemId();
        }

        public int getLineNumber() {
            return Math.max (0, proxy.getLineNumber() - 2);
        }

        public int getColumnNumber() {
            return proxy.getColumnNumber();
        }
    }
    
}
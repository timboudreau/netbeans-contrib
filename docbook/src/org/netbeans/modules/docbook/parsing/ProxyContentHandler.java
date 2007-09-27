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
    ProxyContentHandler(Collection<? extends ParseJobImpl<Callback<ContentHandler>>> jobs, FileObject ob, boolean dtdInserted) {
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
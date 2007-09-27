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
 */
package org.netbeans.modules.flyingsaucer;

import java.awt.BorderLayout;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.net.URL;
import javax.swing.JScrollPane;
import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.TopComponent;
import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.swing.BasicPanel;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Component embedding an XHTML renderer.
 *
 * @author Tim Boudreau
 */
public class FlyingSaucerTopComponent extends TopComponent implements Runnable {
    private final BasicPanel pnl = new XHTMLPanel(true);
    private volatile boolean initialized = false;
    private final String urlString;
    public FlyingSaucerTopComponent(URL url) {
        this (url.toString());
    }
    
    private FlyingSaucerTopComponent (String urlString) {
        this.urlString = urlString;
        setName (trim (urlString));
        setDisplayName(getName());
        setLayout (new BorderLayout());
        add (new JScrollPane(pnl), BorderLayout.CENTER);
    }
    
    protected void componentOpened() {
        if (!initialized) {
            pnl.setErrorHandler(new EH());
            //Calling setDocument() in the event thread
            //will cause synchronous I/O and block (I don't
            //think it should, if I understand the 
            //meaning of passing true to XHTMLPanel's
            //constructor...but it does...'
            new RequestProcessor ("Html Page Loader").post(this); //NOI18N
            initialized = true;
        }
    }
    
    public void run() {
        try {
            pnl.setDocument(urlString);
        } catch (Exception e) {
            StatusDisplayer.getDefault().setStatusText(
                    NbBundle.getMessage(FlyingSaucerTopComponent.class,
                    "MSG_CantConnect", urlString));
            ErrorManager.getDefault().notify(
                    ErrorManager.WARNING, e);
        }
    }
    
    protected String preferredID() {
        return urlString;
    }
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ONLY_OPENED;
    }

    protected Object writeReplace() throws ObjectStreamException {
        return new SerializableStub (urlString);
    }
    
    private static final class SerializableStub implements Serializable {
        private static long serialVersionUID = 2039420L;
        private String urlString;
        SerializableStub (String urlString) {
            this.urlString = urlString;
        }
        
        public Object readResolve() {
            return new FlyingSaucerTopComponent (urlString);
        }
    }
    
    private class EH implements ErrorHandler {
        private InputOutput getIO() {
            return IOProvider.getDefault().getIO(urlString, false);
        }
        
        public void warning(SAXParseException exception) throws SAXException {
            getIO().getOut().println (exception.getMessage());
        }

        public void error(SAXParseException exception) throws SAXException {
            exception.printStackTrace(getIO().getErr());
        }

        public void fatalError(SAXParseException exception) throws SAXException {
            exception.printStackTrace(getIO().getErr());
        }
    }

    /**
     * Create a trimmed version of a url
     */
    private static String trim (String urlString) {
        int ix = urlString.lastIndexOf('/');
        boolean trimTrailingSlash = ix == urlString.length() - 1;
        if (trimTrailingSlash) {
            ix = urlString.lastIndexOf('/', ix - 1);
        }
        String result;
        if (ix > 0) {
            result = urlString.substring(ix + 1);
        } else {
            result = urlString;
        }
        if (trimTrailingSlash && result.length() > 1) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}

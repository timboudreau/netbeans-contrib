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
package org.netbeans.modules.docbook;

import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.netbeans.api.docbook.OutputWindowStatus;
import org.netbeans.spi.xml.cookies.DataObjectAdapters;
import org.openide.ErrorManager;
import org.openide.loaders.DataObject;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 *
 * @author Tim Boudreau
 */
public class ValidationAction extends AbstractAction implements ContextAwareAction, LookupListener {
    private final Lookup lkp;
    private final Lookup.Result res;
    /** Creates a new instance of SetMainFileAction */
    public ValidationAction () {
        this (Utilities.actionsGlobalContext());
    }

    public ValidationAction (Lookup lkp) {
        this.lkp = lkp;
        putValue (NAME, "Validate XML");
        assert lkp != null;
        this.res = lkp.lookupResult(DataObject.class);
        resultChanged (null);
    }

    public void actionPerformed(ActionEvent e) {
        DataObject ob = (DataObject) lkp.lookup(DataObject.class);
        RequestProcessor.getDefault().post (new Validator(ob));
    }

    public Action createContextAwareInstance(Lookup actionContext) {
        return new ValidationAction (actionContext);
    }

    public void resultChanged(LookupEvent ev) {
        res.allInstances();
        DataObject dob = (DataObject) lkp.lookup(DataObject.class);
        setEnabled (true); //XXX check content
    }

    private static final class Validator implements Runnable, ErrorHandler {
        private final DataObject o;
        private final OutputWindowStatus status;
        public Validator (DataObject o) {
            this.o = o;
            status = new OutputWindowStatus ("Validating " + o.getName());
        }

        public void run() {
            //XXX wrap no-doctype files in a fake doctype
            final InputSource src = DataObjectAdapters.inputSource(o);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(true);
            try {
                XMLReader reader = factory.newSAXParser().getXMLReader();
                reader.setErrorHandler(this);
                reader.parse(src);
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify (ioe);
                status.failed (ioe);
            } catch (SAXException e) {
                status.failed (e);
            } catch (ParserConfigurationException e) {
                ErrorManager.getDefault().notify (e);
                status.failed(e);
            } finally {
                status.finished("Done.", null);
            }
        }

        public void warning(SAXParseException e) throws SAXException {
            String sid = e.getSystemId();
            int line = e.getLineNumber();
            int col = e.getColumnNumber();
            String msg = e.getMessage();
            status.warn("Error: " + msg + " in " + sid + "; Line#: " + line + "; Column#: " + col + ";");
        }

        public void error(SAXParseException e) throws SAXException {
            String sid = e.getSystemId();
            int line = e.getLineNumber();
            int col = e.getColumnNumber();
            String msg = e.getMessage();
            status.warn("Error: " + msg + " in " + sid + "; Line#: " + line + "; Column#: " + col + ";");
        }

        public void fatalError(SAXParseException e) throws SAXException {
            String sid = e.getSystemId();
            int line = e.getLineNumber();
            int col = e.getColumnNumber();
            String msg = e.getMessage();
            status.warn("Error - Fatal: " + msg + " in " + sid + "; Line#: " + line + "; Column#: " + col + ";");
        }
    }
}

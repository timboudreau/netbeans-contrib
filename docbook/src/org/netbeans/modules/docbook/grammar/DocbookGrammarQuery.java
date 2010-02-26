/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */
package org.netbeans.modules.docbook.grammar;

import java.awt.Component;
import java.beans.FeatureDescriptor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import org.netbeans.modules.docbook.DocBookCatalog;
import org.netbeans.modules.docbook.DocBookDataObject;
import org.netbeans.modules.xml.api.model.GrammarEnvironment;
import org.netbeans.modules.xml.api.model.GrammarQuery;
import org.netbeans.modules.xml.api.model.GrammarQueryManager;
import org.netbeans.modules.xml.api.model.GrammarResult;
import org.netbeans.modules.xml.api.model.HintContext;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node.Property;
import org.openide.util.Exceptions;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Tim Boudreau
 */
public final class DocbookGrammarQuery extends GrammarQueryManager {
    private static final String FAKE_BOOK = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<!DOCTYPE book PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\" \"http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd\">"
            + "<book><chapter id=\"one\"><para>abc</para><section id=\"foo\"><title>Foo</title><para>foo</para></section></chapter></book>";

    @Override
    public Enumeration enabled(GrammarEnvironment ctx) {
        FileObject fo = ctx.getFileObject();
        String mime = fo == null ? null : fo.getMIMEType();
        boolean usable = DocBookDataObject.MIME_DOCBOOK.equals(mime) || DocBookDataObject.MIME_SLIDES.equals(mime) || DocBookDataObject.MIME_SOLBOOK.equals(mime);
        if (usable) {
            try {
                return en(ctx);
                //        GrammarQueryManager real = getDtdManager();
                //        if (real == null) {
                //            return null;
                //        }
                //        FileObject fo = ctx.getFileObject();
                //        String mime = fo == null ? null : fo.getMIMEType();
                //        boolean usable = DocBookDataObject.MIME_DOCBOOK.equals(mime) || DocBookDataObject.MIME_SLIDES.equals(mime) || DocBookDataObject.MIME_SOLBOOK.equals(mime);
                //        if (usable) {
                //            return ctx.getDocumentChildren();
                //        return null;
                //        return null;
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (SAXException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return null;
    }
    
    private InputSource in() throws UnsupportedEncodingException {
        InputStream in = new ByteArrayInputStream(FAKE_BOOK.getBytes("UTF-8"));
        assert in != null;
        InputSource src = new InputSource(in);
        return src;
    }

    private Enumeration en(GrammarEnvironment ctx) throws IOException, SAXException {
        Document d = XMLUtil.parse(in(), false, false, null, new DocBookCatalog.Reader());
        final NodeList nl = d.getChildNodes();
        final int max = nl.getLength();
        class E implements Enumeration {

            int ix;

            public boolean hasMoreElements() {
                return ix < max;
            }

            public Object nextElement() {
                Object result = nl.item(ix);
                ix++;
                return result;
            }
        }
        return new E();
    }

    @Override
    public GrammarQuery getGrammar(GrammarEnvironment ctx) {
        try {
            GrammarQueryManager real = getDtdManager();
            GrammarEnvironment fakeEnv = new GrammarEnvironment(en(ctx), in(), null);
            GrammarQuery q = real.getGrammar(fakeEnv);
            return new GrammarQueryImpl(q);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (SAXException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @Override
    public FeatureDescriptor getDescriptor() {
        return new FeatureDescriptor(); //Huh?
    }

    public static GrammarQueryManager getDtdManager() {
        FileObject fo = FileUtil.getConfigFile("Plugins/XML/GrammarQueryManagers/org-netbeans-modules-xml-dtd-grammar-DTDGrammarQueryProvider.instance");
        if (fo != null) {
            try {
                InstanceCookie ck = DataObject.find(fo).getLookup().lookup(InstanceCookie.class);
                Object o = ck.instanceCreate();
                if (o instanceof GrammarQueryManager) {
                    return (GrammarQueryManager) o;
                }
            } catch (ClassNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return null;
    }

    private static final class GrammarQueryImpl implements GrammarQuery {

        private final GrammarQuery delegate;

        GrammarQueryImpl(GrammarQuery delegate) {
            this.delegate = delegate;
        }

        public String toString() {
            return delegate.toString();
        }

        public Enumeration<GrammarResult> queryValues(HintContext h) {
            return delegate.queryValues(h);
        }

        public Enumeration<GrammarResult> queryNotations(String prefix) {
            return delegate.queryNotations(prefix);
        }

        public Enumeration<GrammarResult> queryEntities(String prefix) {
            return delegate.queryEntities(prefix);
        }

        public Enumeration<GrammarResult> queryElements(HintContext h) {
            return delegate.queryElements(h);
        }

        public GrammarResult queryDefault(HintContext h) {
            return delegate.queryDefault(h);
        }

        public Enumeration<GrammarResult> queryAttributes(HintContext h) {
            return delegate.queryAttributes(h);
        }

        public boolean isAllowed(Enumeration<GrammarResult> en) {
            return delegate.isAllowed(en);
        }

        public boolean hasCustomizer(HintContext nodeCtx) {
            return delegate.hasCustomizer(nodeCtx);
        }

        public Property[] getProperties(HintContext nodeCtx) {
            return delegate.getProperties(nodeCtx);
        }

        public Component getCustomizer(HintContext nodeCtx) {
            return delegate.getCustomizer(nodeCtx);
        }
    }
}

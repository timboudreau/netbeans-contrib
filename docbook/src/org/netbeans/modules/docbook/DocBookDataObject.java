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

package org.netbeans.modules.docbook;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import org.netbeans.api.docbook.MainFileProvider;
import org.netbeans.modules.docbook.parsing.ParsingServiceImpl;
import org.netbeans.spi.xml.cookies.CheckXMLSupport;
import org.netbeans.spi.xml.cookies.DataObjectAdapters;
import org.netbeans.spi.xml.cookies.TransformableSupport;
import org.netbeans.spi.xml.cookies.ValidateXMLSupport;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.xml.sax.InputSource;

public class DocBookDataObject extends MultiDataObject {
    public static final String MIME_SOLBOOK = "text/x-solbook+xml"; //NOI18N
    public static final String MIME_SLIDES = "text/x-docbook-slides+xml"; //NOI18N
    public static final String MIME_DOCBOOK = "text/x-docbook+xml"; //NOI18N
    private Reference<DocBookDataNode> nodeRef;
    protected final Lookup lkp;

    @SuppressWarnings("LeakingThisInConstructor")
    public DocBookDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        getCookieSet().add(new DocBookEditorSupport(this));
        InputSource src = DataObjectAdapters.inputSource(this);
        getCookieSet().add(new CheckXMLSupport(src));
        getCookieSet().add(new ValidateXMLSupport(src));
        getCookieSet().add(new TransformableSupport(DataObjectAdapters.source(this)));
        lkp = new ProxyLookup(getCookieSet().getLookup(), Lookups.fixed(
                new RendererImpl(this), new SavableImpl(), new ParsingServiceImpl(this),
                new Notifier()));
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    @Override
    protected Node createNodeDelegate() {
        return node(true);
    }
    
    @Override
    public Lookup getLookup() {
        return lkp;
    }

    private DocBookDataNode node(boolean create) {
        DocBookDataNode result;
        synchronized (lkp) {
            result = nodeRef == null ? null : nodeRef.get();
            if (result == null && create) {
                result = new DocBookDataNode(this);
                nodeRef = new WeakReference<DocBookDataNode>(result);
            }
        }
        return result;
    }

    private final class Notifier implements MainFileProvider.Notifier {
        public void change() {
            DocBookDataNode n = node(false);
            if (n != null) {
                n.change();
            }
        }
    }

    private final class SavableImpl implements Savable, Node.Cookie {
        public void addSaveCookie(SaveCookie save) {
            getCookieSet().add(save);
        }

        public void removeSaveCookie(SaveCookie save) {
            getCookieSet().remove(save);
        }

    }
}

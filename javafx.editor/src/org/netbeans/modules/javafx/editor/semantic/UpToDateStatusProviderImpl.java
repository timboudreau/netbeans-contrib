/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javafx.editor.semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.tools.Diagnostic;
import org.netbeans.spi.editor.errorstripe.UpToDateStatus;
import org.netbeans.spi.editor.errorstripe.UpToDateStatusProvider;
/**
 *
 * @author David Strupl
 */
class UpToDateStatusProviderImpl extends UpToDateStatusProvider {
    
    private static final Logger LOGGER = Logger.getLogger(UpToDateStatusProviderImpl.class.getName());
    private static final boolean LOGGABLE = LOGGER.isLoggable(Level.FINE);
    
    private static final Map<Document, UpToDateStatusProviderImpl> cache = new WeakHashMap<Document, UpToDateStatusProviderImpl>();

    private UpToDateStatus status = UpToDateStatus.UP_TO_DATE_DIRTY;
    private Document document;

    private UpToDateStatusProviderImpl(Document doc) {
        this.document = doc;
        cache.put(document, this);
        document.addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                markModified();
            }
            public void removeUpdate(DocumentEvent e) {
                markModified();
            }
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    private void markModified() {
        refresh(new ArrayList<Diagnostic>(), UpToDateStatus.UP_TO_DATE_DIRTY);
    }
    
    static UpToDateStatusProviderImpl forDocument(Document document) {
        
        UpToDateStatusProviderImpl result = cache.get(document);
        if (result != null) {
            return result;
        }
        
        log("Creating new UpToDateStatusProviderImpl for " + document);
        UpToDateStatusProviderImpl res = new UpToDateStatusProviderImpl(document);
        
        return res;
    }

    @Override
    public UpToDateStatus getUpToDate() {
        return status;
    }

    private static void log(String s) {
        if (LOGGABLE) {
            LOGGER.fine(s);
        }
    }

    void refresh(List<Diagnostic> diag, UpToDateStatus s) {
        status = s;
        log("UpToDateStatusProviderImpl changing status to: " + s);
        firePropertyChange(PROP_UP_TO_DATE, null, null);
    }
    
    
}

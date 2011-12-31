/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.contrib.testng;

import java.io.IOException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.core.api.multiview.MultiViews;
import org.openide.cookies.*;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.text.CloneableEditor;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.DataEditorSupport;
import org.openide.windows.CloneableOpenSupport;

/**
 *
 * @author lukas
 */
final class TestNGSuiteDataEditor extends DataEditorSupport implements OpenCookie, EditCookie, EditorCookie.Observable, PrintCookie, ChangeListener {

    private boolean addedChangeListener = false;

    public TestNGSuiteDataEditor(TestNGSuiteDataObject obj) {
        super(obj, null, new TestNGEnv(obj));
        setMIMEType(TestNGSuiteDataObject.MIME_TYPE);
    }

    @Override
    protected Pane createPane() {
        return (CloneableEditorSupport.Pane) MultiViews.createCloneableMultiView(TestNGSuiteDataObject.MIME_TYPE, getDataObject());
    }

    @Override
    protected boolean notifyModified() {
        if (!super.notifyModified()) {
            return false;
        } else {
            TestNGEnv e = (TestNGEnv) env;
            e.getTestNGSuiteDataObject().addSaveCookie(e);
            return true;
        }
    }

    @Override
    protected void notifyUnmodified() {
        super.notifyUnmodified();
        TestNGEnv e = (TestNGEnv) env;
        e.getTestNGSuiteDataObject().removeSaveCookie(e);
    }

    @Override
    protected boolean asynchronousOpen() {
        return true;
    }

    public void stateChanged(ChangeEvent e) {
        updateTitles();
    }

    private static class TestNGEnv extends DataEditorSupport.Env implements SaveCookie {

        private static final long serialVersionUID = 6587342954372956374L;

        public TestNGEnv(TestNGSuiteDataObject obj) {
            super(obj);
        }

        @Override
        public CloneableOpenSupport findCloneableOpenSupport() {
            return (CloneableOpenSupport) getDataObject().getLookup().lookup(EditCookie.class);
        }

        @Override
        protected FileObject getFile() {
            return getDataObject().getPrimaryFile();
        }

        @Override
        protected FileLock takeLock() throws IOException {
            return ((TestNGSuiteDataObject) getDataObject()).getPrimaryEntry().takeLock();
        }

        public void save() throws IOException {
            ((TestNGSuiteDataEditor) findCloneableOpenSupport()).saveDocument();
            getDataObject().setModified(false);
        }

        TestNGSuiteDataObject getTestNGSuiteDataObject() {
            return (TestNGSuiteDataObject) getDataObject();
        }
    }
}

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

package org.netbeans.modules.docbook;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import org.netbeans.api.docbook.Renderer;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.spi.actions.Single;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Tim Boudreau
 */
public final class RenderHtmlAction extends Single<Renderer> {
    private AtomicBoolean running = new AtomicBoolean();
    public RenderHtmlAction() {
        super (Renderer.class, NbBundle.getMessage(RenderHtmlAction.class, "RenderHtmlAction"), null);
    }

    @Override
    protected void actionPerformed(Renderer target) {
        File tmpDir = new File (System.getProperty("java.io.tmpdir"));
        S s = new S();
        target.render(tmpDir, s);
    }

    @Override
    protected boolean isEnabled(Renderer target) {
        return !running.get();
    }

    private final class S extends Renderer.JobStatus {
        ProgressHandle h = ProgressHandleFactory.createHandle("");

        @Override
        public void finished(String msg, File result) {
            running.set(false);
            h.finish();
            if (result != null) {
                try {
                    URL url = result.toURI().toURL();
                    URLDisplayer.getDefault().showURLExternal(url);
                } catch (MalformedURLException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }

        @Override
        public void progress(String msg) {
            h.progress(msg);
        }

        @Override
        public void started(String msg) {
            running.set(true);
            h.start();
        }

        @Override
        public void failed(Throwable t) {
            running.set(false);
            h.finish();
        }

    }
}

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
 * Contributor(s): Tim Boudreau
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.nodejs;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.netbeans.api.extexecution.ExecutionDescriptor.LineConvertorFactory;
import org.netbeans.api.extexecution.print.ConvertedLine;
import org.netbeans.api.extexecution.print.LineConvertor;
import org.openide.LifecycleManager;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author Tim Boudreau
 */
public abstract class NodeJSExecutable {

    public static NodeJSExecutable getDefault() {
        NodeJSExecutable exe = Lookup.getDefault().lookup(NodeJSExecutable.class);
        if (exe == null) {
            exe = new NodeJSExecutable.DummyExectable();
        }
        return exe;
    }

    public final void run(FileObject targetFile) throws IOException {
        assert targetFile.isValid() && targetFile.isData();
        assert !EventQueue.isDispatchThread();
        LifecycleManager.getDefault().saveAll();
        doRun(targetFile);
    }

    protected abstract Future<Integer> doRun(FileObject file) throws IOException;

    public abstract void setNodeExecutable(String location);

    public abstract String getNodeExecutable(boolean showDialog);

    static final class DummyExectable extends NodeJSExecutable {

        @Override
        protected Future<Integer> doRun(FileObject file) throws IOException {
            return new Future<Integer>() {

                @Override
                public boolean cancel(boolean bln) {
                    return false;
                }

                @Override
                public boolean isCancelled() {
                    return false;
                }

                @Override
                public boolean isDone() {
                    return true;
                }

                @Override
                public Integer get() throws InterruptedException, ExecutionException {
                    return 1;
                }

                @Override
                public Integer get(long l, TimeUnit tu) throws InterruptedException, ExecutionException, TimeoutException {
                    return 1;
                }
            };
        }

        @Override
        public void setNodeExecutable(String location) {
            throw new UnsupportedOperationException("Dummy implementation");
        }

        @Override
        public String getNodeExecutable(boolean showDialog) {
            return null;
        }
    }
}

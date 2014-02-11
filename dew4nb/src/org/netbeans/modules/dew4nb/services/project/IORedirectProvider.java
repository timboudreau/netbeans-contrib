/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */

package org.netbeans.modules.dew4nb.services.project;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.modules.dew4nb.endpoint.EndPoint;
import org.netbeans.modules.dew4nb.endpoint.Status;
import org.openide.util.Pair;
import org.openide.util.Parameters;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;

/**
 *
 * @author Tomas Zezula
 */
@ServiceProvider(service = IOProvider.class, position = 0)
public class IORedirectProvider extends IOProvider {

    private static final Pattern EMPTY_STR = Pattern.compile("^\\s*$"); //NOI18N
    private static final ThreadLocal<Pair<EndPoint.Env,String>> currentEnv = new ThreadLocal<>();
    private static final Object threadsLock = new Object();
    //@GuardedBy("threadsLock")
    private static final Map<RedirectIO,Collection<Reference<Thread>>> activeThreads =
        new WeakHashMap<>();

    public IORedirectProvider() {}


    static void bindEnv(
        @NonNull final EndPoint.Env env,
        @NonNull final ProjectAction request) {
        Parameters.notNull("env", env); //NOI18N
        Parameters.notNull("request", request); //NOI18N
        if (ProjectMessageType.invokeAction != request.getType()) {
            throw new IllegalArgumentException(String.valueOf(request.getType()));
        }
        currentEnv.set(Pair.<EndPoint.Env,String>of(
            env,
            request.getState()));
    }

    static void unbindEnv() {
        currentEnv.remove();
    }

    static void showUrl(@NonNull final URL url) {
        synchronized(threadsLock) {
out:        for (Map.Entry<RedirectIO,Collection<Reference<Thread>>> e : activeThreads.entrySet()) {
                for (Reference<Thread> tr : e.getValue()) {
                    if (Thread.currentThread() == tr.get()) {
                        e.getKey().openUrlImpl(url);
                        break out;
                    }
                }
            }
        }
    }

    @Override
    public InputOutput getIO(String name, boolean newIO) {
        if (currentEnv.get() == null) {
            return new NullIO();
        } else {
            return new RedirectIO();
        }
    }

    @Override
    public OutputWriter getStdOut() {
        return new NullOutputWriter();
    }

    private static class RedirectWriter extends Writer {

        private final RedirectIO owner;
        private final boolean errorOutput;

        RedirectWriter(
                @NonNull final RedirectIO owner,
                final boolean errorOutput) {
            Parameters.notNull("owner", owner); //NOI18N
            this.owner = owner;
            this.errorOutput = errorOutput;
        }


        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            final String str = new  String(cbuf, off, len);
            if (!EMPTY_STR.matcher(str).matches()) {
                owner.writeImpl(str, errorOutput);                
            }
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
            owner.closeImpl();
        }
    }

    private static class RedirectOutputWriter extends OutputWriter {

        private final RedirectIO owner;

        RedirectOutputWriter(
                @NonNull final RedirectIO owner,
                final boolean err) {
            super(new RedirectWriter(owner, err));
            this.owner = owner;
        }

        @Override
        public void println(String s, OutputListener l) throws IOException {
            ((PrintWriter)this.out).println(s);
        }

        @Override
        public void reset() throws IOException {
            owner.resetImpl();
        }
    }

    @SuppressWarnings("deprecation")
    private static final class RedirectIO implements InputOutput {

        private final AtomicBoolean closed;
        private volatile EndPoint.Env env;
        private volatile String state;

        RedirectIO() {
            this.closed = new AtomicBoolean();
            resetImpl();
            registerThread();
        }

        @Override
        public Reader getIn() {
            registerThread();
            return new NullReader();
        }

        @Override
        public OutputWriter getOut() {
            registerThread();
            return new RedirectOutputWriter(this, false);
        }

        @Override
        public OutputWriter getErr() {
            registerThread();
            return new RedirectOutputWriter(this, true);
        }

        @Override
        public void closeInputOutput() {            
        }

        @Override
        public boolean isClosed() {
            return false;
        }

        @Override
        public void setOutputVisible(boolean value) {
        }

        @Override
        public void setErrVisible(boolean value) {
        }

        @Override
        public void setInputVisible(boolean value) {
        }

        @Override
        public void select() {
        }

        @Override
        public boolean isErrSeparated() {
            return true;
        }

        @Override
        public void setErrSeparated(boolean value) {
        }

        @Override
        public boolean isFocusTaken() {
            return false;
        }

        @Override
        public void setFocusTaken(boolean value) {
        }

        @Override        
        public Reader flushReader() {
            return new NullReader();
        }

        void writeImpl(
            @NonNull final String data,
            final boolean err) {
            env.sendObject(createResponse(
                    state,
                    null,
                    err?
                        null :
                        data,
                    err?
                        data :
                        null,
                    null));
        }

        void closeImpl() {
            if (!closed.getAndSet(true)) {
                env.sendObject(createResponse(
                    state,
                    BuildResult.success,
                    null,
                    null,
                    null));
            }
        }

        void openUrlImpl(@NonNull final URL url) {
            Parameters.notNull("url", url); //NOI18N
            env.sendObject(createResponse(
                state,
                null,
                null,
                null,
                Collections.singleton(url)));
        }

        void resetImpl() {
            synchronized (threadsLock) {
                activeThreads.remove(this);
            }
            final Pair<EndPoint.Env,String> p = currentEnv.get();
            if (p == null) {
                throw new IllegalStateException();
            }
            env = p.first();
            state = p.second();
            closed.set(false);
        }

        private void registerThread() {
            synchronized (threadsLock) {
                Collection<Reference<Thread>> myThreads = activeThreads.get(this);
                if (myThreads == null) {
                    myThreads = new ArrayList<>();
                    activeThreads.put(this, myThreads);
                }
                myThreads.add(new WeakReference<>(Thread.currentThread()));
            }
        }
    }

    private static final class NullReader extends Reader {

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            return -1;
        }

        @Override
        public void close() throws IOException {
        }
    }

    private static final class NullWriter extends Writer {

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            System.out.println(new String(cbuf, off, len));
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }
    }

    private static final class NullOutputWriter extends OutputWriter {

        NullOutputWriter() {
            super(new NullWriter());
        }

        @Override
        public void println(String s, OutputListener l) throws IOException {
            println(s);
        }

        @Override
        public void reset() throws IOException {
        }
    }

    private static final class NullIO implements InputOutput {

        @Override
        public OutputWriter getOut() {
            return new NullOutputWriter();
        }

        @Override
        public Reader getIn() {
            return new NullReader();
        }

        @Override
        public OutputWriter getErr() {
            return new NullOutputWriter();
        }

        @Override
        public void closeInputOutput() {
        }

        @Override
        public boolean isClosed() {
            return false;
        }

        @Override
        public void setOutputVisible(boolean value) {
        }

        @Override
        public void setErrVisible(boolean value) {
        }

        @Override
        public void setInputVisible(boolean value) {
        }

        @Override
        public void select() {
        }

        @Override
        public boolean isErrSeparated() {
            return true;
        }

        @Override
        public void setErrSeparated(boolean value) {
        }

        @Override
        public boolean isFocusTaken() {
            return true;
        }

        @Override
        public void setFocusTaken(boolean value) {
        }

        @Override
        public Reader flushReader() {
            return new NullReader();
        }
    }

    @NonNull
    private static InvokeProjectActionResult createResponse(
            @NonNull final String state,
            @NullAllowed final BuildResult result,
            @NullAllowed final String stdOut,
            @NullAllowed final String stdErr,
            @NullAllowed final Collection<? extends URL> urls) {
        Parameters.notNull("state", state); //NOI18N
        final InvokeProjectActionResult res = new InvokeProjectActionResult();
        res.setType(ProjectMessageType.invokeAction);
        res.setState(state);
        res.setStatus(Status.done);
        res.setResult(result);
        if (stdOut != null) {
            res.getStdout().add(stdOut);
        }
        if (stdErr != null) {
            res.getStderr().add(stdErr);
        }
        if (urls != null) {
            for (URL url : urls) {
                res.getOpenUrl().add(url.toExternalForm());
            }
        }
        return res;
    }
}

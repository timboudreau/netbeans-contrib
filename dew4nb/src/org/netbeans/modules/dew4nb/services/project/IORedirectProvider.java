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
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.regex.Pattern;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.modules.dew4nb.endpoint.EndPoint;
import org.netbeans.modules.dew4nb.endpoint.Status;
import org.netbeans.modules.dew4nb.services.javac.InvokeProjectActionResult;
import org.netbeans.modules.dew4nb.services.javac.JavacMessageType;
import org.netbeans.modules.dew4nb.services.javac.JavacQuery;
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

    static final String PROP_STATE = "state";   //NOI18N
    static final String PROP_TYPE = "type"; //NOI18N

    private static final Pattern EMPTY_STR = Pattern.compile("^\\s*$"); //NOI18N
    private static final ThreadLocal<EndPoint.Env> currentEnv = new ThreadLocal<>();

    public IORedirectProvider() {}


    static void bindEnv(@NonNull final EndPoint.Env env) {
        Parameters.notNull("env", env); //NOI18N
        currentEnv.set(env);
    }

    static void unbindEnv() {
        currentEnv.remove();
    }

    @Override
    public InputOutput getIO(String name, boolean newIO) {
        final EndPoint.Env env = currentEnv.get();
        if (env == null) {
            throw new IllegalStateException();
        }
        return new RedirectIO(env);
    }

    @Override
    public OutputWriter getStdOut() {
        return new NullOutputWriter();
    }

    private static class RedirectWriter extends Writer {

        private final EndPoint.Env env;
        private final boolean errorOutput;

        RedirectWriter(
                @NonNull final EndPoint.Env env,
                final boolean errorOutput) {
            this.env = env;
            this.errorOutput = errorOutput;
        }


        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            final String str = new  String(cbuf, off, len);
            if (!EMPTY_STR.matcher(str).matches()) {
                env.sendObject(createResponse(
                    env,
                    null,
                    errorOutput?
                        null :
                        str,
                    errorOutput?
                        str :
                        null));
            }
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }
    }

    private static class RedirectOutputWriter extends OutputWriter {     

        RedirectOutputWriter(
                @NonNull final EndPoint.Env env,
                final boolean err) {
            super(new RedirectWriter(env, err));
        }

        @Override
        public void println(String s, OutputListener l) throws IOException {
            ((PrintWriter)this.out).println(s);
        }

        @Override
        public void reset() throws IOException {
        }
    }

    private static class RedirectIO implements InputOutput {

        private final EndPoint.Env env;

        RedirectIO(@NonNull final EndPoint.Env env) {
            Parameters.notNull("env", env);
            this.env = env;
        }

        @Override
        public Reader getIn() {
            return new NullReader();
        }

        @Override
        public OutputWriter getOut() {
            return new RedirectOutputWriter(env, false);
        }

        @Override
        public OutputWriter getErr() {
            return new RedirectOutputWriter(env, true);
        }

        @Override
        public void closeInputOutput() {
            env.sendObject(createResponse(
                env,
                BuildResult.success,
                null,
                null));
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

    @NonNull
    private static InvokeProjectActionResult createResponse(
            @NonNull final EndPoint.Env env,
            @NullAllowed final BuildResult result,
            @NullAllowed final String stdOut,
            @NullAllowed final String stdErr) {
        Parameters.notNull("env", env); //NOI18N
        final JavacMessageType type = env.getProperty(PROP_TYPE, JavacMessageType.class);
        Parameters.notNull("type", type); //NOI18N
        final String state = env.getProperty(PROP_STATE, String.class);
        Parameters.notNull("state", state); //NOI18N
        final InvokeProjectActionResult res = new InvokeProjectActionResult();
        res.setType(type);
        res.setState(state);
        res.setStatus(Status.done);
        res.setResult(result);
        if (stdOut != null) {
            res.getStdout().add(stdOut);
        }
        if (stdErr != null) {
            res.getStderr().add(stdErr);
        }
        return res;
    }
}

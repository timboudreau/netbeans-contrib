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

package org.netbeans.modules.autoproject.java.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 * Runs a Java class.
 * @author Jesse Glick
 */
public class Runner {

    private Runner() {}

    private static final List<InputOutput> deadIOs = new LinkedList<InputOutput>();
    private static final Map<InputOutput,StopAction> stopActions = new HashMap<InputOutput,StopAction>();
    //private static final Map<InputOutput,MonitorAction> monitorActions = new HashMap<InputOutput,MonitorAction>();

    public static void runJava(List<String> options, File cwd) throws IOException {
        List<String> command = new ArrayList<String>();
        command.add(new File(new File(new File(System.getProperty("java.home")).getParentFile(), "bin"), "java").getAbsolutePath());
        command.addAll(options);
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(cwd);
        final Process p = pb.start();
        StopAction stop;
        //MonitorAction monitor;
        final InputOutput io;
        synchronized (Runner.class) {
            //System.err.println("deadIOs.size=" + deadIOs.size());
            Iterator<InputOutput> it = deadIOs.iterator();
            if (it.hasNext()) {
                io = it.next();
                stop = stopActions.get(io);
                //monitor = monitorActions.get(io);
                io.getOut().reset();
                it.remove();
                while (it.hasNext()) {
                    InputOutput old = it.next();
                    old.closeInputOutput();
                    stopActions.remove(old);
                    //monitorActions.remove(old);
                    it.remove();
                }
            } else {
                stop = new StopAction();
                //monitor = new MonitorAction();
                io = IOProvider.getDefault().getIO(/*XXX I18N*/"Output", new Action[] {stop/*XXX, monitor*/});
                stopActions.put(io, stop);
                //monitorActions.put(io, monitor);
            }
        }
        stop.setProcess(p);
        //monitor.setProcess(p);
        final AtomicBoolean stopped = new AtomicBoolean();
        stop.setListener(new Runnable() {
            public void run() {
                stopped.set(true);
            }
        });
        io.getErr().print(/*XXX I18N*/"Running:");
        // XXX prettify
        for (String c : command) {
            io.getErr().print(" " + c);
        }
        io.getErr().println(""); // XXX println() does nothing!
        io.getErr().println("---------------");
        final AtomicBoolean gotOutput = new AtomicBoolean();
        Runnable onOutput = new Runnable() {
            public void run() {
                if (gotOutput.compareAndSet(false, true)) {
                    io.select();
                }
            }
        };
        // XXX ought to do hyperlinking of stack traces
        // (tricky since we need complete lines of text, but that prevents incremental output of partial lines)
        final StreamCopier outCopier = new StreamCopier(new InputStreamReader(p.getInputStream()), io.getOut(), true, onOutput);
        new StreamCopier(new InputStreamReader(p.getErrorStream()), io.getErr(), true, onOutput).start();
        onOutput = new Runnable() {
            public void run() {
                // System.out is normally not set to autoflush.
                // However if we have gotten input it is nice to flush it.
                outCopier.flush();
            }
        };
        new StreamCopier(io.getIn(), new OutputStreamWriter(p.getOutputStream()), true, onOutput).start();
        outCopier.start();
        boolean ok;
        try {
            ok = p.waitFor() == 0;
        } catch (InterruptedException x) {
            Exceptions.printStackTrace(x);
            ok = false;
        }
        stop.clear();
        //monitor.clear();
        if (gotOutput.get()) {
            io.getErr().println("---------------");
        }
        if (ok) {
            io.getErr().println(/*XXX I18N*/"Program finished.");
        } else if (stopped.get()) {
            io.getErr().println(/*XXX I18N*/"Program stopped.");
        } else {
            io.getErr().println(/*XXX I18N*/"Program failed.");
        }
        io.getIn().close();
        io.getOut().close();
        io.getErr().close();
        synchronized (Runner.class) {
            deadIOs.add(io);
        }
    }

    private static final class StopAction extends AbstractAction {

        private Process p;
        private Runnable r;

        public StopAction() {
            putValue(Action.SMALL_ICON, new ImageIcon(Utilities.loadImage("org/netbeans/modules/debugger/resources/actions/Kill.gif", true)));
            putValue(Action.SHORT_DESCRIPTION, "Stop"); // XXX I18N
        }

        public void actionPerformed(ActionEvent e) {
            p.destroy();
            setEnabled(false);
            r.run();
        }

        public void setProcess(Process p) {
            this.p = p;
            setEnabled(true);
        }

        public void setListener(Runnable r) {
            this.r = r;
        }

        public void clear() {
            p = null;
            r = null;
            setEnabled(false);
        }

    }

    /*
    private static final class MonitorAction extends AbstractAction {

        private Process p;

        public MonitorAction() {
            putValue(Action.SMALL_ICON, new ImageIcon(Utilities.loadImage("org/openide/resources/actions/find.gif", true)));
            putValue(Action.SHORT_DESCRIPTION, "Monitor"); // XXX I18N
        }

        public void actionPerformed(ActionEvent e) {
            try {
                long pid;
                Class c = p.getClass();
                if (c.getName().equals("java.lang.UNIXProcess")) {
                    Field f = c.getDeclaredField("pid");
                    f.setAccessible(true);
                    pid = (Integer) f.get(p);
                } else {
                    assert c.getName().equals("java.lang.ProcessImpl"); // Windows
                    Field f = c.getDeclaredField("handle");
                    f.setAccessible(true);
                    pid = (Long) f.get(p);
                }
                new ProcessBuilder(
                        new File(new File(new File(System.getProperty("java.home")).getParentFile(), "bin"), "jconsole").getAbsolutePath(),
                        Long.toString(pid)).start();
            } catch (Exception x) {
                Exceptions.printStackTrace(x);
            }
            // XXX ideally would call setEnabled(false) but reename if jconsole closed
        }

        public void setProcess(Process p) {
            this.p = p;
            setEnabled(true);
        }

        public void clear() {
            p = null;
            setEnabled(false);
        }

    }
     */

    /** Stolen with modifications from old ProcessExecutor. See #57000 for proposed API. */
    private static class StreamCopier extends Thread {

        private final Reader from;
        private final Writer to;
        private final boolean autoflush;
        private final Runnable onOutput;

        StreamCopier(Reader from, Writer to, boolean autoflush, Runnable onOutput) {
            this.from = from;
            this.to = to;
            this.autoflush = autoflush;
            this.onOutput = onOutput;
        }

        public @Override void run() {
            try {
                int read;
                char[] buf = new char[4096];
                while ((read = from.read(buf, 0, buf.length)) != -1) {
                    //System.err.println("for " + (onOutput != null ? "output" : "input") + " received: '" + new String(buf, 0, read) + "'");
                    if (onOutput != null) {
                        onOutput.run();
                    }
                    to.write(buf, 0, read);
                    if (autoflush) {
                        to.flush();
                    }
                }
                from.close();
                to.close();
            } catch (IOException x) {
                // ignore, could just be stopped process
            }
        }

        public void flush() {
            try {
                to.flush();
            } catch (IOException x) {
                Exceptions.printStackTrace(x);
            }
        }

    }

}

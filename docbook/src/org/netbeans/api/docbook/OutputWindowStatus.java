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
package org.netbeans.api.docbook;

import java.io.File;
import java.io.IOException;
import org.openide.ErrorManager;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;


/**
 *
 * @author Tim Boudreau
 */
public final class OutputWindowStatus extends Renderer.JobStatus {
    private final Renderer.JobStatus other;
    private final String displayName;
    public OutputWindowStatus(String displayName, Renderer.JobStatus other) {
        this.other = other;
        this.displayName = displayName;
    }

    public OutputWindowStatus(String displayName) {
        this.other = null;
        this.displayName = displayName;
    }

    private InputOutput io;
    private synchronized InputOutput getIO() {
        if (io == null) {
            io = IOProvider.getDefault().getIO(displayName, false);
        }
        return io;
    }

    public void started(String msg) {
        running = true;
        InputOutput io = getIO();
        OutputWriter out = io.getOut();
        try {
            out.reset();
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
            return;
        }
        io.select();
        out.println (msg);
        if (other != null) {
            other.started (msg);
        }
    }

    public void progress(String msg) {
//        assert running;
        InputOutput io = getIO();
        OutputWriter out = io.getOut();
        out.println (msg);
        if (other != null) {
            other.progress (msg);
        }
    }

    public void finished(String msg, File result) {
//        assert running;
        InputOutput io = getIO();
        OutputWriter out = io.getOut();
        out.println (msg);
        if (other != null) {
            other.finished (msg, result);
        }
        end();
    }

    public void warn (String msg) {
//        assert running;
        InputOutput io = getIO();
        OutputWriter out = io.getErr();
        out.println (msg);
        if (other != null) {
            other.warn (msg);
        }
    }

    public void failed(Throwable t) {
//        assert running;
        InputOutput io = getIO();
        OutputWriter out = io.getErr();
        t.printStackTrace(out);
        end();
    }

    private void end() {
        io.getOut().close();
        synchronized (this) {
            io = null;
        }
        running = false;
    }

    private volatile boolean running = false;
    public boolean isRunning() {
        return running;
    }

}

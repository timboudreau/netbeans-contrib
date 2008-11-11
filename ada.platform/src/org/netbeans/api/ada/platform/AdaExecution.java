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
package org.netbeans.api.ada.platform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.concurrent.Future;
import org.netbeans.modules.extexecution.api.ExecutionDescriptor;
import org.netbeans.modules.extexecution.api.ExecutionService;
import org.netbeans.modules.extexecution.api.ExternalProcessBuilder;
import org.netbeans.modules.extexecution.api.input.InputProcessor;
import org.openide.util.Exceptions;

/**
 *
 * @author Andrea Lucarelli
 */
public class AdaExecution {
    // execution commands

    private String command;
    private String workingDirectory;
    private String commandArgs;
    private String displayName;
    private ExecutionDescriptor descriptor = new ExecutionDescriptor().frontWindow(true).controllable(true).inputVisible(true).showProgress(true).showSuspended(true);

    /**
     * Execute the process described by this object
     * @return a Future object that provides the status of the running process
     */
    public synchronized Future<Integer> run() {
        try {
            ExecutionService service = ExecutionService.newService(buildProcess(), descriptor, displayName);
            return service.run();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }

    }

    // TODO: To modify in Custom Execution Service. See org.netbeans.modules.extexecution.api.ExecutionServiceTest.
    private ExternalProcessBuilder buildProcess() throws IOException {
        ExternalProcessBuilder processBuilder = new ExternalProcessBuilder(command);
        processBuilder = processBuilder.workingDirectory(new File(workingDirectory));
        if (commandArgs != null) {
            String args[] = org.openide.util.Utilities.parseParameters(commandArgs);
            for (int index = 0; index < args.length; index++) {
                processBuilder = processBuilder.addArgument(args[index]);
            }
        }
        return processBuilder;
    }

    // TODO: To use when Custom Execution Servcie will be created.
    private static class CheckProcess extends Process {

        private final int returnValue;
        private boolean finished;
        private boolean started;

        public CheckProcess(int returnValue) {
            this.returnValue = returnValue;
        }

        public void start() {
            synchronized (this) {
                started = true;
                notifyAll();
            }
        }

        public boolean isStarted() {
            synchronized (this) {
                return started;
            }
        }

        public boolean isFinished() {
            synchronized (this) {
                return finished;
            }
        }

        @Override
        public void destroy() {
            synchronized (this) {
                if (finished) {
                    return;
                }

                finished = true;
                notifyAll();
            }
        }

        @Override
        public int exitValue() {
            synchronized (this) {
                if (!finished) {
                    throw new IllegalStateException("Not finished yet");
                }
            }
            return returnValue;
        }

        @Override
        public InputStream getErrorStream() {
            return new InputStream() {

                @Override
                public int read() throws IOException {
                    return -1;
                }
            };
        }

        @Override
        public InputStream getInputStream() {
            return new InputStream() {

                @Override
                public int read() throws IOException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };
        }

        @Override
        public OutputStream getOutputStream() {
            return new OutputStream() {

                @Override
                public void write(int b) throws IOException {
                    // throw it away
                }
            };
        }

        @Override
        public int waitFor() throws InterruptedException {
            synchronized (this) {
                while (!finished) {
                    wait();
                }
            }
            return returnValue;
        }

        public void waitStarted() throws InterruptedException {
            synchronized (this) {
                while (!started) {
                    wait();
                }
            }
        }
    }

    public synchronized String getCommand() {
        return command;
    }

    public synchronized void setCommand(String command) {
        this.command = command;
    }

    public synchronized String getCommandArgs() {
        return commandArgs;
    }

    public synchronized void setCommandArgs(String commandArgs) {
        this.commandArgs = commandArgs;
    }

    public synchronized String getWorkingDirectory() {
        return workingDirectory;
    }

    public synchronized void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public synchronized String getDisplayName() {
        return displayName;
    }

    public synchronized void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public synchronized void setShowControls(boolean showControls) {
        descriptor = descriptor.controllable(showControls);
    }

    public synchronized void setShowInput(boolean showInput) {
        descriptor = descriptor.inputVisible(showInput);
    }

    public synchronized void setShowProgress(boolean showProgress) {
        descriptor = descriptor.showProgress(showProgress);
    }

    /**
     * Can the process be suppended
     * @param showSuspended boolean to set the status 
     */
    public synchronized void setShowSuspended(boolean showSuspended) {
        descriptor = descriptor.showSuspended(showSuspended);
    }

    /**
     * Show the window of the running process
     * @param showWindow display the windown or not?
     */
    public synchronized void setShowWindow(boolean showWindow) {
        descriptor = descriptor.frontWindow(showWindow);
    }
    private final AdaOutputProcessor outProcessor = new AdaOutputProcessor();

    /**
     * Attach a Processor to collect the output of the running process
     */
    public void attachOutputProcessor() {
        descriptor = descriptor.outProcessorFactory(new ExecutionDescriptor.InputProcessorFactory() {

            public InputProcessor newInputProcessor() {
                return (outProcessor);
            }
        });
    }

    /**
     * Retive the output form the running process
     * @return a string reader for the process
     */
    public Reader getOutput() {
        return new StringReader(outProcessor.getData());
    }

    /**
     * Attach input processor to the running process
     */
    public void attachInputProcessor() {
        //descriptor = descriptor.
    }

    /**
     * Writes data to the running process
     * @return StringWirter
     */
    public Writer getInput() {
        return null;
    }

    public void setPostExecution(Runnable postExecution) {
        descriptor.postExecution(postExecution);
    }
}

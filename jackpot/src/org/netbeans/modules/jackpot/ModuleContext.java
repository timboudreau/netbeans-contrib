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

package org.netbeans.modules.jackpot;

import org.netbeans.modules.java.source.engine.ApplicationContext;
import org.netbeans.modules.java.source.engine.BuildProgress;
import org.netbeans.modules.java.source.engine.FileSourceRewriter;
import org.netbeans.modules.java.source.engine.PropertySheetInfo;
import org.netbeans.modules.java.source.engine.QueryProgress;
import org.netbeans.modules.java.source.engine.SourceRewriter;
import org.netbeans.api.java.source.query.ResultTableModel;
import org.netbeans.modules.jackpot.ui.BuildProgressMonitor;
import org.netbeans.modules.jackpot.ui.QueryProgressMonitor;
import org.netbeans.modules.jackpot.ui.QueryResultsView;
import org.openide.awt.StatusDisplayer;
import org.openide.util.*;
import org.openide.windows.*;
import java.awt.*;
import java.io.*;
import java.text.MessageFormat;
import java.util.logging.*;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

/**
 * The ApplicationContext when running Jackpot within NetBeans.
 */
public class ModuleContext implements ApplicationContext {
    Cancellable cancelMethod;
    private long buildTime;
    static final Logger logger = Logger.getLogger("org.netbeans.jackpot");
    
    ModuleContext(Cancellable c) {
        cancelMethod = c;
	buildTime = System.currentTimeMillis();
    }
    
    public BuildProgress getBuildProgress() {
        return new BuildProgressMonitor(cancelMethod);
    }
    
    public QueryProgress getQueryProgress() {
        return new QueryProgressMonitor(cancelMethod);
    }

    public boolean setProperties(Object command, String title) {
        PropertySheetInfo.find(command.getClass()).loadValues(command);
        return true;
    }
    
    public void setResult(Object result, String title) {
        show(result, title);
    }
    public void setStatusMessage(String message) {
        StatusDisplayer.getDefault().setStatusText(message);
    }
    public void setErrorMessage(String message, String title) {
        setStatusMessage(title + ": " + message);
    }
    public PrintWriter getOutputWriter(String title) {
	InputOutput io = title != null ? getOutputWindow(title) : getLogWindow();
        return io.getOut();
    }

    private static String getString(String key) {
        return NbBundle.getMessage(ModuleContext.class, key);
    }
    
    private void show(Object o, String title) {
        if (o instanceof ResultTableModel || o instanceof ResultTableModel[])
            displayResults(o, title);
        else if (o != null) {
            PrintWriter out = getOutputWindow(title).getOut();
            out.println(o.toString());
            out.close();
        }
    }
    
    private static InputOutput getOutputWindow(String title) {
	if (title == null)
	    title = getString("LBL_Output");
        InputOutput io = IOProvider.getDefault().getIO(title, true);
        io.select();
        return io;
    }
    
    private static InputOutput logWindow;
    
    public static InputOutput getLogWindow() {
	if (logWindow == null) {
	    logWindow = IOProvider.getDefault().getIO(getString("LBL_Output"), true);
	    Formatter formatter = new LogFormatter() {
		public synchronized String format(LogRecord record) {
		    StringBuffer sb = new StringBuffer();
		    String message = formatMessage(record);
		    sb.append(message);
		    sb.append(lineSeparator);
		    return sb.toString();
		}
	    };
	    Handler handler = new WriterHandler(logWindow.getOut(), formatter);
	    logger.addHandler(handler);
	}
	return logWindow;
    }
    
    public static void closeLogWindow() {
        if (logWindow != null) {
            logWindow.getOut().close();
            logWindow = null;
        }
    }
    
    // character version of java.util.logging.StreamHandler
    private static class WriterHandler extends Handler {
	private LogManager manager = LogManager.getLogManager();
	private OutputStream output;
	private boolean doneHeader;
	private Writer writer;

	public WriterHandler(Writer out, Formatter formatter) {
	    setFormatter(formatter);
	    writer = out;
	}

	public synchronized void publish(LogRecord record) {
	    if (!isLoggable(record)) {
		return;
	    }
	    String msg;
	    try {
		msg = getFormatter().format(record);
	    } catch (Exception ex) {
		reportError(null, ex, java.util.logging.ErrorManager.FORMAT_FAILURE);
		return;
	    }

	    try {
		if (!doneHeader) {
		    writer.write(getFormatter().getHead(this));
		    doneHeader = true;
		}
		writer.write(msg);
	    } catch (Exception ex) {
		reportError(null, ex, java.util.logging.ErrorManager.WRITE_FAILURE);
	    }
	}


	public boolean isLoggable(LogRecord record) {
	    if (writer == null || record == null) {
		return false;
	    }
	    return super.isLoggable(record);
	}

	public synchronized void flush() {
	    if (writer != null) {
		try {
		    writer.flush();
		} catch (Exception ex) {	
		    reportError(null, ex, java.util.logging.ErrorManager.FLUSH_FAILURE);
		}
	    }
	}

	private synchronized void flushAndClose() {
	    if (writer != null) {
		try {
		    if (!doneHeader) {
			writer.write(getFormatter().getHead(this));
			doneHeader = true;
		    }
		    writer.write(getFormatter().getTail(this));
		    writer.flush();
		    writer.close();
		} catch (Exception ex) {
		    reportError(null, ex, java.util.logging.ErrorManager.CLOSE_FAILURE);
		}
		writer = null;
		output = null;
	    }
	}

	public synchronized void close() {
	    flushAndClose();
	}
    }
    
    
    private void displayResults(final Object o, final String title) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                QueryResultsView view = QueryResultsView.getInstance();
                try {
                    if (o instanceof ResultTableModel)
                        view.setResults((ResultTableModel)o);
                    else
                        view.setResults((ResultTableModel[])o);
                    view.setDisplayName(title);
                    view.open();
                    view.requestActive();
                } catch (Throwable t) {
                    org.openide.ErrorManager.getDefault().notify(org.openide.ErrorManager.EXCEPTION, t);
                    JackpotModule.getInstance().abortEngine();
                }
            }
        });        
    }

    public Class getCommandClass(String className) {
        try {
            return Class.forName(className, true, getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public SourceRewriter getSourceRewriter(JavaFileObject sourcefile) throws IOException {
        return new FileSourceRewriter(sourcefile);
    }
}

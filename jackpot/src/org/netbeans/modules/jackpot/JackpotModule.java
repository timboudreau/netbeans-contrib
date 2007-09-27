/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.jackpot;

import java.awt.EventQueue;
import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.netbeans.api.jackpot.Query;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.project.*;
import org.netbeans.modules.jackpot.engine.Engine;
import org.netbeans.modules.jackpot.engine.EngineException;
import org.netbeans.modules.jackpot.engine.PropertySheetInfo;
import org.netbeans.spi.jackpot.RecursiveRuleException;
import org.netbeans.modules.jackpot.engine.Result;
import org.netbeans.modules.jackpot.ui.Hyperlink;
import org.netbeans.modules.jackpot.ui.QueryResultsView;
import org.netbeans.spi.jackpot.QueryProvider;
import org.openide.*;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 * Main class for NetBeans module for Jackpot.  It provides lifecycle support
 * for a Jackpot engine instance to the rest of the module.
 */
public class JackpotModule extends ModuleInstall {
    private static JackpotModule instance = new JackpotModule();
    static final Logger logger = Logger.getLogger("org.netbeans.modules.jackpot");
    private Engine engine;
    private ModuleContext ctx;    
    
    /**
     * The single instance of the Jackpot module.
     * @return the module instance
     */
    public static JackpotModule getInstance() {
        return instance;
    }
    
    public void uninstalled() {
        Hyperlink.detachAllAnnotations();
    }
    
    private JackpotModule() {}
    
    /**
     * 
     * @param projects 
     * @throws java.lang.Exception 
     */
    public void createEngine(final Project[] projects) throws Exception {
        boolean engineStarted = false;
        boolean doBuild = projects != null;
        try {
            if (doBuild) {
                ctx = new ModuleContext();
                String sourcePath = getSourcePath(projects);
                String classPath = getCompilePath(projects);
                String bootPath = getBootClassPath(projects);
                engine = new Engine(ctx, sourcePath, classPath, bootPath);
            }
            engineStarted = true;
        } finally {
            if (!engineStarted)
                engine = null;
        }
    }
    
    /**
     * Create a query instance from a class name.
     * @param className 
     * @return the new query
     * @throws java.lang.Exception 
     */
    public Query createCommand(String className) throws Exception {
        return engine.createCommand(className);
    }
    
    /**
     * Create a new query from a rules script
     * @param queryName 
     * @param path 
     * @return the new query
     * @throws java.lang.Exception 
     */
    public Query createScript(String queryName, String path) throws Exception {
        return engine.createScript(queryName, path);
    }
    
    /**
     * Returns true if a file is recognized by any QueryProviders.
     * 
     * @param fo the file to test
     * @return true if the file is a script which can be run as a Query
     */
    public boolean isQueryScript(FileObject fo) {
        Lookup.Template<QueryProvider> template = new Lookup.Template<QueryProvider>(QueryProvider.class);
        Lookup.Result<QueryProvider> result = Lookup.getDefault().lookup(template);
        for (Class<? extends QueryProvider> cls : result.allClasses()) {
            try {
                QueryProvider qp = cls.newInstance();
                if (qp.hasQuery(fo))
                    return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
    
    /**
     * 
     * @param querySetName 
     * @param inspections 
     * @throws java.lang.Exception 
     */
    public void runCommands(String querySetName, Inspection[] inspections) throws Exception {
        try {
            LifecycleManager.getDefault().saveAll();
            Query[] queries = new Query[inspections.length];
            for (int i = 0; i < inspections.length; i++) {
                if (inspections[i] == null)
                    continue;
                String name = inspections[i].getInspector();
                String refactoring = inspections[i].getTransformer();
                String command = inspections[i].getCommand();
                queries[i] = command.endsWith(".rules") ?
                    engine.createScript(name, command) :
                    engine.createCommand(command);
            }
            ModificationResult mods = engine.runCommands(querySetName, queries);
            displayResults(querySetName, ctx.getResults(), mods);
        } catch (RecursiveRuleException e) {
            getLogWindow().getErr().println(e.getMessage());
        } catch (EngineException e) {
            Throwable cause = e.getCause();
            if (cause instanceof NoSuchMethodError || cause instanceof AbstractMethodError) {
                String s = NbBundle.getMessage(getClass(), "MSG_ConflictingMustangBuild");
                String msg = MessageFormat.format(s, cause.getLocalizedMessage());
                NotifyDescriptor d = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(d);
            } else
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, e);
        } finally {
            releaseEngine();
        }
    }
    
    /**
     * 
     * @param inspection 
     * @throws java.lang.Exception 
     */
    public void runCommand(Inspection inspection) throws Exception {
        String name = inspection.getInspector();
        String refactoring = inspection.getTransformer();
        String commandName = inspection.getCommand();
        try {
            LifecycleManager.getDefault().saveAll();
            ModificationResult mods = (commandName.endsWith(".rules")) ?
                engine.runScript(name, commandName) :
                engine.runCommand(name, commandName);
            displayResults(name, ctx.getResults(), mods);
        } catch (RecursiveRuleException e) {
            getLogWindow().getErr().println(e.getMessage());
        } catch (EngineException e) {
            Throwable cause = e.getCause();
            String msg = null;
            if (cause instanceof ModifiedSourceFileException)
                msg = NbBundle.getMessage(getClass(), "MSG_SourceFileModified");
            else if (cause instanceof NoSuchMethodError || cause instanceof AbstractMethodError) {
                String s = NbBundle.getMessage(getClass(), "MSG_ConflictingMustangBuild");
                msg = MessageFormat.format(s, cause.getLocalizedMessage());
            }
            if (msg != null) {
                NotifyDescriptor d = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(d);
            } else
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, e);
        } finally {
            releaseEngine();
        }
    }
    
    /**
     * 
     */
    public void releaseEngine() {
        engine = null;
        ctx = null;
    }
    
    private void displayResults(final String title, final List<Result> results,
                                final ModificationResult mods) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                QueryResultsView view = QueryResultsView.getInstance();
                try {
                    view.setResults(mods, results);
                    view.setDisplayName(title);
                    view.open();
                    view.requestActive();
                } catch (Throwable t) {
                    org.openide.ErrorManager.getDefault().notify(org.openide.ErrorManager.EXCEPTION, t);
                }
            }
        });        
    }
    
    private static InputOutput logWindow;
    
    /**
     * 
     * @return 
     */
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
    
    /**
     * Returns the property sheet associated with a query
     * @param className the query class
     * @return the property sheet
     */
    public PropertySheetInfo getPropertySheetInfo(String className) {
        return Engine.getPropertySheetInfo(className);
    }
    
    private static String getString(String key) {
        return NbBundle.getMessage(JackpotModule.class, key);
    }
    
    private String getSourcePath(Project[] projects) {
        FileObject[] roots = RefactoringClassPath.getSourcePath(projects).getRoots();
        return makePathString(roots);
    }
    
    private static String getCompilePath(Project[] projects) {
        FileObject[] roots = RefactoringClassPath.getCompilePath(projects).getRoots();
        return makePathString(roots);
    }
    
    private static String getBootClassPath(Project[] projects) {
        FileObject[] roots = RefactoringClassPath.getBootClassPath(projects).getRoots();
        return makePathString(roots);
    }
    
    private static String makePathString(FileObject[] roots) {
        StringBuffer path = new StringBuffer();
        for (int i = 0; i < roots.length; i++) {
            FileObject fo = roots[i];
            try {
                if (fo.getURL().toString().startsWith("jar:"))
                    fo = FileUtil.getArchiveFile(fo);
            } catch (FileStateInvalidException ex) {
                ex.printStackTrace();
            }
            File f = FileUtil.toFile(fo);
            if (f == null)
                continue;
            path.append(f.getPath());
            if (i < roots.length - 1)
                path.append(File.pathSeparatorChar);
        }
        return path.toString();
    }
}

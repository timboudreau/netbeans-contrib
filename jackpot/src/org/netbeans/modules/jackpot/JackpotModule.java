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

import org.netbeans.modules.java.source.engine.BuildErrorsException;
import org.netbeans.modules.java.source.engine.EngineFactory;
import org.netbeans.modules.java.source.engine.ApplicationContext;
import org.netbeans.modules.java.source.engine.DefaultApplicationContext;
import org.netbeans.modules.java.source.engine.EngineException;
import org.netbeans.modules.java.source.engine.JackpotEngine;
import org.netbeans.modules.java.source.engine.ReadOnlyFilesException;
import org.netbeans.modules.java.source.engine.RecursiveRuleException;
import org.netbeans.api.java.source.query.Query;
import org.netbeans.modules.java.source.engine.JavaFormatOptions;
import org.netbeans.modules.java.source.engine.PropertySheetInfo;
import org.netbeans.api.java.queries.SourceLevelQuery;
import org.netbeans.api.project.*;
import org.netbeans.modules.jackpot.ui.Hyperlink;
import org.openide.*;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.modules.ModuleInstall;
import org.openide.util.Cancellable;
import org.openide.util.NbBundle;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

/**
 * Main class for NetBeans module for Jackpot.  It provides lifecycle support
 * for a Jackpot engine instance to the rest of the module.
 */
public class JackpotModule extends ModuleInstall {
    private static JackpotModule instance = new JackpotModule();
    private JackpotEngine engine;
    private String sourceLevel;
    private List<JackpotEngineListener> listeners =
            new LinkedList<JackpotEngineListener>();
    
    public static JackpotModule getInstance() {
        return instance;
    }
    
    public void uninstalled() {
        Hyperlink.detachAllAnnotations();
    }
    
    private JackpotModule() {}
    
    public JackpotEngine getEngine() {
        return engine;
    }
    
    public boolean isRunning() {
        return engine != null;
    }
    
    public JackpotEngine createFormatingEngine() {
        
        try {
            return EngineFactory.createEngine(new ModuleContext(null));
        } catch ( Exception e ) {
            return null;
        }
        
    }
    
    public void createEngine(final Project[] projects, Cancellable cancellable) throws Exception {
        LifecycleManager.getDefault().saveAll();
        createEngine(new ModuleContext(cancellable), projects);
    }
    
    private void createEngine(ApplicationContext appContext, Project[] projects) throws Exception {
        boolean engineStarted = false;
        boolean doBuild = projects != null;
        try {
            engine = EngineFactory.createEngine(appContext);
            if (doBuild) {
                sourceLevel = "1.0"; // will be updated by the next methods.
                String sourcePath = getSourcePath(projects);
                String classPath = getCompilePath(projects);
                String bootPath = getBootClassPath(projects);
                int errors = engine.initialize(sourcePath, classPath, bootPath, sourceLevel);
                if (errors > 0)
                    throw new BuildErrorsException(errors);
                for (JackpotEngineListener listener : listeners)
                    listener.engineCreated();
            }
            engineStarted = true;
        } finally {
            if (!engineStarted && engine != null) {
                engine.close();
                engine = null;
            }
        }
    }
    
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
                    engine.createScript(name, refactoring, command) :
                    engine.createCommand(name, refactoring, command);
            }
            engine.runCommands(querySetName, queries);
            for (JackpotEngineListener listener : listeners)
                listener.commandExecuted();
        } catch (RecursiveRuleException e) {
            ModuleContext.getLogWindow().getErr().println(e.getMessage());
            releaseEngine(false, true);
        } catch (EngineException e) {
            Throwable cause = e.getCause();
            if (cause instanceof NoSuchMethodError || cause instanceof AbstractMethodError) {
                String s = NbBundle.getMessage(getClass(), "MSG_ConflictingMustangBuild");
                String msg = MessageFormat.format(s, cause.getLocalizedMessage());
                NotifyDescriptor d = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(d);
            } else
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, e);
            releaseEngine(false, true);
        }
    }
    
    public void runCommand(Inspection inspection) throws Exception {
        String name = inspection.getInspector();
        String refactoring = inspection.getTransformer();
        String commandName = inspection.getCommand();
        try {
            LifecycleManager.getDefault().saveAll();
            if (commandName.endsWith(".rules"))
                engine.runScript(name, refactoring, commandName);
            else
                engine.runCommand(name, refactoring, commandName);
            for (JackpotEngineListener listener : listeners)
                listener.commandExecuted();
        } catch (RecursiveRuleException e) {
            ModuleContext.getLogWindow().getErr().println(e.getMessage());
            releaseEngine(false, true);
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
                releaseEngine(false, true);
            } else
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, e);
            releaseEngine(false, true);
        }
    }
    
    public void releaseEngine() {
        releaseEngine(true, true);
    }
    
    public void abortEngine() {
        releaseEngine(false, true);
    }
    
    private void releaseEngine(boolean doCommit, boolean notifyListeners) {
        if (engine == null)
            return;
        try {
            boolean doClose = true;
            if (doCommit)
                try {
                    doClose = engine.commit();
                } catch (ReadOnlyFilesException e) {
                    String msg = MessageFormat.format(getString("MSG_SourceFilesReadOnly"), e.getMessage());
                    NotifyDescriptor d = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(d);
                    doClose = true;
                }
            if (doClose) {  // false if engine.commit() was cancelled
                engine.close();
                JackpotEngine je = engine;
                engine = null;
                if (notifyListeners)
                    for (JackpotEngineListener listener : listeners)
                        listener.engineReleased();
                    ModuleContext.closeLogWindow();
                    StatusDisplayer.getDefault().setStatusText("");
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    public PropertySheetInfo getPropertySheetInfo(String className) {
        boolean engineCreated = false;
        try {
            if (engine == null) {
                try {
                    createEngine(new DefaultApplicationContext(), null);
                } catch (Exception e) {
                    throw new AssertionError(e);
                }
                engineCreated = true;
            }
            return engine.getPropertySheetInfo(className);
        } finally {
            if (engineCreated)
                releaseEngine(false, false);
        }
    }
    
    public JavaFormatOptions getFormatOptions() {
        boolean engineCreated = false;
        try {
            if (engine == null) {
                try {
                    createEngine(new DefaultApplicationContext(), null);
                } catch (Exception e) {
                    throw new AssertionError(e);
                }
                engineCreated = true;
            }
            return engine.getFormatOptions();
        } finally {
            if (engineCreated)
                releaseEngine(false, false);
        }
    }
    
    public void addJackpotEngineListener(JackpotEngineListener listener) {
        listeners.add(listener);
    }
    
    public void removeJackpotEngineListener(JackpotEngineListener listener) {
        listeners.remove(listener);
    }
    
    private String getString(String key) {
        return NbBundle.getMessage(JackpotModule.class, key);
    }
    
    private String getSourcePath(Project[] projects) {
        FileObject[] roots = RefactoringClassPath.getSourcePath(projects).getRoots();
        for (FileObject fo : roots)
            updateSourceLevel(fo);
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
    
    private void updateSourceLevel(FileObject fo) {
        String newLevel = SourceLevelQuery.getSourceLevel(fo);
        if (newLevel != null && newLevel.compareTo(sourceLevel) >= 1)
            sourceLevel = newLevel;
    }
}

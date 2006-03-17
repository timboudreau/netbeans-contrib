/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.modulemanagement;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.jar.JarFile;
import org.netbeans.api.sendopts.CommandException;
import org.netbeans.spi.sendopts.AdditionalArgumentsProcessor;
import org.netbeans.spi.sendopts.Env;
import org.netbeans.spi.sendopts.NoArgumentProcessor;
import org.netbeans.spi.sendopts.Option;
import org.netbeans.spi.sendopts.OptionProvider;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;

/**
 *
 * @author Jaroslav Tulach
 */
public class ModuleOptions
implements OptionProvider, NoArgumentProcessor<Void>, AdditionalArgumentsProcessor<Void> {
    private Option<Void> list;
    private Option<Void> install;
    
    /** Creates a new instance of ModuleOptions */
    public ModuleOptions() {
    }

    private void init() {
        if (list != null) {
            return;
        }

        list = Option.withoutArgument(-1, "listmodules", this); // NOI18N
        install = Option.additionalArguments(-1, "installmodules", this); // NOI18N
    }

    public Option[] getOptions() {
        init();
        return new Option[] { list, install };
    }

    public Void process(Option option, Env env) throws CommandException {
        listAllModules(env.getOutputStream());
        return null;
    }

    private void listAllModules(PrintStream out) {
        Collection modules = Lookup.getDefault().lookup(new Lookup.Template(ModuleInfo.class)).allInstances();
        Integer number = new Integer(modules.size());
        out.println(NbBundle.getMessage(ModuleOptions.class, "MSG_ModuleListHeader", number));
        Iterator it = modules.iterator();
        while (it.hasNext()) {
            ModuleInfo module = (ModuleInfo) it.next();
            Object[] args = {
                fixedLength(module.getCodeName(), 50),
                fixedLength(module.getCodeNameBase(), 50),
                new Integer(module.getCodeNameRelease()),
                fixedLength(module.getDisplayName(), 50),
                fixedLength(module.getSpecificationVersion() == null ? "" : module.getSpecificationVersion().toString(), 15),
                fixedLength(module.getImplementationVersion(), 15),
                fixedLength(module.getBuildVersion(), 15),
                new Integer(module.isEnabled() ? 1 : 0),
            };
            out.println(NbBundle.getMessage(ModuleOptions.class, "MSG_ModuleListLine", args));
        }
        out.println(NbBundle.getMessage(ModuleOptions.class, "MSG_ModuleListFooter", number));
        out.flush();
    }

    private static String fixedLength(String s, int len) {
        if (s == null) {
            return null;
        }
        if (s.length() >= len) {
            return s.substring(0, len);
        } else {
            StringBuffer sb = new StringBuffer(len);
            sb.append(s);
            while (sb.length() < len) {
                sb.append(' ');
            }
            return sb.toString();
        }
    }

    public Void process(Option option, Env env, String[] args) throws CommandException {
        try {
            if (option == install) {
                for (String fileName : args) {
                    File f = new File(env.getCurrentDirectory(), fileName);
                    if (!f.exists()) {
                        f = new File(fileName);
                    }
                    if (!f.exists()) {
                        env.getErrorStream().println("File does not exists " + f);
                        throw CommandException.exitCode(5);
                    }

                    JarFile jar = new JarFile(f);
                    String cnb = jar.getManifest().getMainAttributes().getValue("OpenIDE-Module"); // NOI18N
                    if (cnb == null) {
                        env.getErrorStream().println("File does not seem to be a module " + f);
                        throw CommandException.exitCode(6);
                    }
                    {
                        int slash = cnb.indexOf('/');
                        if (slash >= 0) {
                            cnb = cnb.substring(0, slash);
                        }
                    }

                    final File file = f;
                    final String codebase = cnb;
                    final FileObject root = Repository.getDefault().getDefaultFileSystem().getRoot();
                    final FileObject dir = FileUtil.createFolder(root, "Modules");
                    final String fn = cnb.replace('.', '-') + ".xml";


                    class X implements FileSystem.AtomicAction {
                        FileObject conf = dir.getFileObject(fn);

                        public void run() throws IOException {
                            conf = FileUtil.createData(dir, fn);
                            FileLock lock = conf.lock();
                            PrintStream os = new PrintStream(conf.getOutputStream(lock));

                            os.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                            os.print("<!DOCTYPE module PUBLIC \"-//NetBeans//DTD Module Status 1.0//EN\"\n");
                            os.print("                        \"http://www.netbeans.org/dtds/module-status-1_0.dtd\">\n");
                            os.print("<module name=\"" + codebase + "\">\n");
                            os.print("    <param name=\"autoload\">false</param>\n");
                            os.print("    <param name=\"eager\">false</param>\n");
                            os.print("    <param name=\"enabled\">true</param>\n");
                            os.print("    <param name=\"jar\">" + file + "</param>\n");
                            os.print("    <param name=\"release\">1</param>\n");
                            os.print("    <param name=\"reloadable\">false</param>\n");
                            os.print("    <param name=\"specversion\">1.25</param>\n");
                            os.print("</module>\n");

                            os.close();
                            lock.releaseLock();
                        }
                    }
                    X x = new X();

                    if (x.conf != null) {
                        env.getErrorStream().println("Module with code base " + cnb + " is already installed. Cannot install " + f);
                        throw CommandException.exitCode(7);
                    }

                    dir.getFileSystem().runAtomicAction(x);

                    waitFor(cnb, true);
                }
            }
        } catch (InterruptedException ex) {
            throw CommandException.exitCode(4, ex);
        } catch (IOException ex) {
            throw CommandException.exitCode(4, ex);
        }


        return null;
    }

    private static void waitFor(final String codebase, final boolean shouldBeEnabled) throws IOException, InterruptedException {
        Lookup.Template t = new Lookup.Template(ModuleInfo.class);
        final Lookup.Result res = Lookup.getDefault().lookup(t);
        res.allInstances();

        class L implements LookupListener {
            private boolean go;

            public synchronized void resultChanged(LookupEvent ev) {
                notifyAll();
                go = true;
            }

            public void waitFor() throws InterruptedException {
                Collection modules;

                for(;;) {
                    synchronized (this) {
                        go = false;
                    }

                    modules = res.allInstances();

                    Iterator it = modules.iterator();
                    while (it.hasNext()) {
                        ModuleInfo m = (ModuleInfo)it.next();
                        if (m.getCodeNameBase().equals(codebase)) {
                            if (shouldBeEnabled && m.isEnabled()) {
                                return;
                            }
                        }
                    }

                    synchronized (this) {
                        if (!go) {
                            wait();
                        }
                    }
                }
            }
        }

        L list = new L();
        res.addLookupListener(list);
        res.allItems();
        list.waitFor();
    }
}


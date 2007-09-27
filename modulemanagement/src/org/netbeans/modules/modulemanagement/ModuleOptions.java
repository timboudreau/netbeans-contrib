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

package org.netbeans.modules.modulemanagement;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import org.netbeans.api.autoupdate.OperationContainer;
import org.netbeans.api.autoupdate.OperationException;
import org.netbeans.api.autoupdate.OperationSupport;
import org.netbeans.api.autoupdate.UpdateManager;
import org.netbeans.api.autoupdate.UpdateUnit;
import org.netbeans.api.sendopts.CommandException;
import org.netbeans.spi.sendopts.Env;
import org.netbeans.spi.sendopts.Option;
import org.netbeans.spi.sendopts.OptionProcessor;
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
import org.openide.util.WeakListeners;

/**
 *
 * @author Jaroslav Tulach
 */
public class ModuleOptions extends OptionProcessor {
    private static final Logger LOG = Logger.getLogger(ModuleOptions.class.getName());
    
    private Option list;
    private Option install;
    private Option disable;
    private Option enable;
    
    /** Creates a new instance of ModuleOptions */
    public ModuleOptions() {
    }

    private void init() {
        if (list != null) {
            return;
        }

        String b = "org.netbeans.modules.modulemanagement.Bundle";
        list = Option.shortDescription(
            Option.withoutArgument(Option.NO_SHORT_NAME, "listmodules"), b, "MSG_ListModules"); // NOI18N
        install = Option.shortDescription(
            Option.additionalArguments(Option.NO_SHORT_NAME, "installmodules"), b, "MSG_InstallModules"); // NOI18N
        disable = Option.shortDescription(
            Option.additionalArguments(Option.NO_SHORT_NAME, "disablemodules"), b, "MSG_DisableModules"); // NOI18N
        enable = Option.shortDescription(
            Option.additionalArguments(Option.NO_SHORT_NAME, "enablemodules"), b, "MSG_EnableModules"); // NOI18N
    }

    public Set<Option> getOptions() {
        init();
        Set<Option> s = new HashSet<Option>();
        s.add(list);
        s.add(install);
        s.add(disable);
        s.add(enable);
        return s;
    }

    private void listAllModules(PrintStream out) {
        Collection<? extends ModuleInfo> modules = Lookup.getDefault().lookupAll(ModuleInfo.class);
        Integer number = new Integer(modules.size());
        out.println(NbBundle.getMessage(ModuleOptions.class, "MSG_ModuleListHeader", number));
        Iterator<? extends ModuleInfo> it = modules.iterator();
        while (it.hasNext()) {
            ModuleInfo module = it.next();
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

    private static <T extends Throwable> T initCause(T t, Throwable cause) {
        t.initCause(cause);
        return t;
    }

    protected void process(Env env, Map<Option, String[]> optionValues) throws CommandException {
        if (optionValues.containsKey(list)) {
            listAllModules(env.getOutputStream());
        }
    
        try {
            if (optionValues.containsKey(install)) {
                String[] args = optionValues.get(install);
                for (String fileName : args) {
                    File f = new File(env.getCurrentDirectory(), fileName);
                    if (!f.exists()) {
                        f = new File(fileName);
                    }
                    if (!f.exists()) {
                        throw new CommandException(5, NbBundle.getMessage(ModuleOptions.class, "ERR_FileNotFound", f)); // NOI18N
                    }

                    JarFile jar = new JarFile(f);
                    String cnb = jar.getManifest().getMainAttributes().getValue("OpenIDE-Module"); // NOI18N
                    if (cnb == null) {
                        throw new CommandException(6, NbBundle.getMessage(ModuleOptions.class, "ERR_NotModule", f)); // NOI18N
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
                        throw new CommandException(7, NbBundle.getMessage(ModuleOptions.class, "ERR_AlreadyInstalled", f, cnb)); // NOI18N
                    }

                    dir.getFileSystem().runAtomicAction(x);

                    waitFor(cnb, true);
                }
            }

            if (optionValues.containsKey(disable)) {
                changeModuleState(optionValues.get(disable), false);
            }

            if (optionValues.containsKey(enable)) {
                changeModuleState(optionValues.get(enable), true);

            }
        } catch (InterruptedException ex) {
            throw initCause(new CommandException(4), ex);
        } catch (IOException ex) {
            throw initCause(new CommandException(4), ex);
        } catch (OperationException ex) {
            throw initCause(new CommandException(4), ex);
        }
    }

    private static void waitFor(final String codebase, final boolean shouldBeEnabled) throws IOException, InterruptedException, CommandException {
        LOG.fine("waitFor: " + codebase + " state: " + shouldBeEnabled); // NOI18N
        
        final Lookup.Result<ModuleInfo> res = Lookup.getDefault().lookupResult(ModuleInfo.class);
        res.allInstances();

        class L implements LookupListener, PropertyChangeListener {
            private boolean go;
            private Set<ModuleInfo> listening = new HashSet<ModuleInfo>();

            public synchronized void resultChanged(LookupEvent ev) {
                notifyAll();
                go = true;
                LOG.fine("go set to true by a listener"); // NOI18N
            }
            
            public synchronized void propertyChange(PropertyChangeEvent ev) {
                notifyAll();
                go = true;
                LOG.fine("go set to true by a property change listener"); // NOI18N
            }

            public void waitFor() throws CommandException, InterruptedException {
                Collection<? extends ModuleInfo> modules;

                for(;;) {
                    synchronized (this) {
                        go = false;
                        LOG.fine("go = false");
                    }

                    modules = res.allInstances();

                    boolean found = false;
                    for (ModuleInfo m : modules) {
                        if (m.getCodeNameBase().equals(codebase)) {
                            LOG.fine("found code base: " + codebase + " as " + m); // NOI18N
                            found = true;
                            if (shouldBeEnabled) {
                                if (m.isEnabled()) {
                                    return;
                                }
                            } else {
                                if (!m.isEnabled()) {
                                    return;
                                }
                            }
                            listening.add(m);
                            m.addPropertyChangeListener(this);
                            break;
                        }
                    }

                    if (!shouldBeEnabled && !found) {
                        // all modules scanned but non of it has our codename base
                        return;
                    }

                    synchronized (this) {
                        LOG.fine("waiting 10000"); // NOI18N
                        if (!go) {
                            wait(10000);
                        }
                        if (!go) {
                            LOG.fine("No event received: " + go + " exiting"); // NOI18N
                            throw new CommandException(4, NbBundle.getMessage(ModuleOptions.class, "ERR_TimeOut", codebase, shouldBeEnabled ? 1 : 0)); // NOI18N
                        }
                    }
                }
            }
            
            public void cleanUp() {
                res.removeLookupListener(this);
                for (ModuleInfo m : listening) {
                    m.removePropertyChangeListener(this);
                }
            }
        }

        L list = new L();
        try {
            res.addLookupListener(list);
            res.allItems();
            list.waitFor();
        } finally {
            list.cleanUp();
        }
        
        LOG.fine("waitFor finished");
    }

    private void changeModuleState(String[] cnbs, boolean enable) throws IOException, CommandException, InterruptedException, OperationException {
        for (String cnb : cnbs) {
            int slash = cnb.indexOf('/');
            if (slash >= 0) {
                cnb = cnb.substring(0, slash);
            }
        }
        
        Set<String> all = new HashSet<String>(Arrays.asList(cnbs));

        List<UpdateUnit> units = UpdateManager.getDefault().getUpdateUnits();
        OperationContainer<OperationSupport> operate = enable ? OperationContainer.createForEnable() : OperationContainer.createForDisable();
        for (UpdateUnit updateUnit : units) {
            if (all.contains(updateUnit.getCodeName())) {
                if (enable) {
                    operate.add(updateUnit, updateUnit.getInstalled());
                } else {
                    operate.add(updateUnit, updateUnit.getInstalled());
                }
            }
        }
        OperationSupport support = operate.getSupport();
        support.doOperation(null);
    }

}


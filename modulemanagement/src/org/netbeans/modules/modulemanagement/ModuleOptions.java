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

import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import org.netbeans.api.sendopts.CommandException;
import org.netbeans.spi.sendopts.Env;
import org.netbeans.spi.sendopts.NoArgumentProcessor;
import org.netbeans.spi.sendopts.OneArgumentProcessor;
import org.netbeans.spi.sendopts.Option;
import org.netbeans.spi.sendopts.OptionProvider;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Jaroslav Tulach
 */
public class ModuleOptions implements OptionProvider, NoArgumentProcessor<Void> {
    private Option<Void> list;
    
    /** Creates a new instance of ModuleOptions */
    public ModuleOptions() {
    }

    private void init() {
        if (list != null) {
            return;
        }

        list = Option.withoutArgument(-1, "listmodules", this); // NOI18N
    }

    public Option[] getOptions() {
        init();
        return new Option[] { list };
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
                fixedLength(module.getSpecificationVersion().toString(), 15),
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
}


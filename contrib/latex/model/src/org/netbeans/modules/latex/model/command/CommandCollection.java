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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
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
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.netbeans.modules.latex.model.Queue;


import org.netbeans.modules.latex.model.command.Command;

/**
 *
 * @author Jan Lahoda
 */
public class CommandCollection {
    
    /** Creates a new instance of CommandCollection */
    public CommandCollection() {
        commands = new HashMap<String, Command>();
        environments = new HashMap<String, Environment>();
        
//        addCommand(new Command("\\documentclass[#code]{#code}:preamble"));
    }
    
    private Map<String, Command> commands;
    private Map<String, Environment> environments;
    
    private void addContentImpl(NamedAttributableWithSubElements pack) {
        commands.putAll(pack.getCommands());
        environments.putAll(pack.getEnvironments());
    }
    
    private Set<? extends CommandPackage> computeClosure(CommandPackage pack) {
        Set<CommandPackage> result = new HashSet<CommandPackage>();
        Set<String> done   = new HashSet<String>();
        Queue<CommandPackage> q = new Queue<CommandPackage>();
        
        q.put(pack);
        
        while (!q.empty()) {
            CommandPackage current = q.pop();
            
            done.add(current.getName());
            result.add(current);
            
            Set toAdd = current.getIncludes();
            
            for (Iterator i = toAdd.iterator(); i.hasNext(); ) {
                String name = (String) i.next();
                
                if (done.contains(name))
                    continue;
                
                CommandPackage p = CommandPackage.getCommandPackageForName(name);
                
                if (p != null)
                    q.put(p);
            }
        }
        
        return result;
    }
    
    private void addPackageContentImpl(CommandPackage pack) {
        Set toAdd = computeClosure(pack);
        
        for (Iterator i = toAdd.iterator(); i.hasNext(); ) {
            CommandPackage p = (CommandPackage) i.next();
            
            addContentImpl(p);
        }
    }
    
    public void addDocumentClassContent(String name) {
        CommandPackage pack = CommandPackage.getCommandPackageForName(name);
        
        if (pack == null)
            pack = CommandPackage.getDefaultDocumentClass();
        
        if (pack == null)
            return ;
        
        if (pack.getType() != CommandPackage.DOCUMENT_CLASS)
            return ;
        
        addPackageContentImpl(pack);
    }
    
    public void addPackageContent(String name) {
        CommandPackage pack = CommandPackage.getCommandPackageForName(name);
        
        if (pack == null)
            return ;
        
        if (pack.getType() != CommandPackage.PACKAGE)
            return ;
        
        addPackageContentImpl(pack);
    }
    
    public void addCommand(Command cmd) {
        commands.put(cmd.getCommand(), cmd);
    }
    
    public void addCommandContent(Command cmd) {
        addContentImpl(cmd);
    }
    
    public void addEnvironment(Environment env) {
        environments.put(env.getName(), env);
    }
    
    public void addEnvironmentContent(Environment env) {
        addContentImpl(env);
    }
    
    public Collection getCommands() {
        return commands.values();
    }
    
    public Collection getEnvironments() {
        return environments.values();
    }
    
    public Command getCommand(String name) {
        return commands.get(name);
    }
    
    public Environment getEnvironment(String name) {
        return environments.get(name);
    }
    
    public String toString() {
        return getCommands().toString() + "+" + getEnvironments().toString();
    }
    
}

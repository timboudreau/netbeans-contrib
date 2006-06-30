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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.netbeans.modules.latex.model.Queue;

import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;

import org.netbeans.modules.latex.model.command.Command;

/**
 *
 * @author Jan Lahoda
 */
public class CommandCollection {
    
    /** Creates a new instance of CommandCollection */
    public CommandCollection() {
        commands = new HashMap();
        environments = new HashMap();
        
//        addCommand(new Command("\\documentclass[#code]{#code}:preamble"));
    }
    
    private Map commands;
    private Map environments;
    
    private void addContentImpl(NamedAttributableWithSubElements pack) {
        commands.putAll(pack.getCommands());
        environments.putAll(pack.getEnvironments());
    }
    
    private Set/*<CommandPackage>*/ computeClosure(CommandPackage pack) {
        Set result = new HashSet();
        Set done   = new HashSet();
        Queue q = new Queue();
        
        q.put(pack);
        
        while (!q.empty()) {
            CommandPackage current = (CommandPackage) q.pop();
            
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
        return (Command) commands.get(name);
    }
    
    public Environment getEnvironment(String name) {
        return (Environment) environments.get(name);
    }
    
    public String toString() {
        return getCommands().toString() + "+" + getEnvironments().toString();
    }
    
}

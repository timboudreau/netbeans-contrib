/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.cmdline.exec;

import java.io.File;

/**
 * The representation of structured execution property.
 *
 * @author  Martin Entlicher
 */
public final class StructuredExec extends Object {
    
    private File working;
    private String executable;
    private String[] args;
    
    /** Creates a new instance of StructuredExec */
    public StructuredExec(File working, String executable, String[] args) {
        this.working = working;
        this.executable = executable;
        this.args = args;
    }
    
    public File getWorking() {
        return working;
    }
    
    public void setWorking(File working) {
        this.working = working;
    }
    
    public String getExecutable() {
        return executable;
    }
    
    public void setExecutable(String executable) {
        this.executable = executable;
    }
    
    public String[] getArguments() {
        return args;
    }
    
    public void setArguments(String[] args) {
        this.args = args;
    }
    
    public void setArgument(int pos, String arg) {
        if (pos < 0 || pos >= args.length) {
            throw new IllegalArgumentException("Illegal argument position: "+pos);
        }
        args[pos] = arg;
    }
    
    public void addArgument(int pos, String arg) {
        if (pos < 0) pos = 0;
        String[] newArgs = new String[args.length + 1];
        if (pos < args.length) {
            if (pos > 0) {
                System.arraycopy(args, 0, newArgs, 0, pos);
            }
            System.arraycopy(args, pos, newArgs, pos + 1, args.length - pos);
            newArgs[pos] = arg;
        } else {
            System.arraycopy(args, 0, newArgs, 0, args.length);
            newArgs[args.length] = arg;
        }
        args = newArgs;
    }
    
    public void removeArgument(int pos) {
        if (pos < 0 || pos >= args.length) {
            throw new IllegalArgumentException("Illegal argument position: "+pos);
        }
        String[] newArgs = new String[args.length - 1];
        if (pos > 0) {
            System.arraycopy(args, 0, newArgs, 0, pos);
        }
        if (pos < (args.length - 1)) {
            System.arraycopy(args, pos + 1, newArgs, pos, args.length - pos - 1);
        }
        args = newArgs;
    }
    
}

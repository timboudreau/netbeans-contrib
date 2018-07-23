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

package org.netbeans.modules.vcscore.cmdline.exec;

import java.io.File;
import java.util.Map;
import org.netbeans.modules.vcscore.Variables;

/**
 * The representation of structured execution property.
 *
 * @author  Martin Entlicher
 */
public class StructuredExec extends Object {

    private File working;
    private String executable;
    private Argument[] args;

    /** Creates a new instance of StructuredExec.
     * @param working The working directory or <code>null</code> to use the current
     *                working directory.
     * @param executable The executable
     * @param args The list of arguments passed to the executable
     */
    public StructuredExec(File working, String executable, Argument[] args) {
        this.working = working;
        this.executable = executable;
        this.args = args;
    }
    
    /**
     * Get the working directory.
     * @return The working directory or <code>null</code> to use the current
     *         working directory.
     */
    public File getWorking() {
        return working;
    }
    
    /**
     * Set the working directory.
     * @param working The working directory or <code>null</code> to use the current
     *                working directory.
     */
    public void setWorking(File working) {
        this.working = working;
    }
    
    public String getExecutable() {
        return executable;
    }
    
    public void setExecutable(String executable) {
        this.executable = executable;
    }
    
    public Argument[] getArguments() {
        return args;
    }
    
    public void setArguments(Argument[] args) {
        this.args = args;
    }
    
    public void setArgument(int pos, Argument arg) {
        if (pos < 0 || pos >= args.length) {
            throw new IllegalArgumentException("Illegal argument position: "+pos);
        }
        args[pos] = arg;
    }
    
    public void addArgument(Argument arg) {
        addArgument(args.length, arg);
    }
    
    public void addArgument(int pos, Argument arg) {
        if (pos < 0) pos = 0;
        Argument[] newArgs = new Argument[args.length + 1];
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
        Argument[] newArgs = new Argument[args.length - 1];
        if (pos > 0) {
            System.arraycopy(args, 0, newArgs, 0, pos);
        }
        if (pos < (args.length - 1)) {
            System.arraycopy(args, pos + 1, newArgs, pos, args.length - pos - 1);
        }
        args = newArgs;
    }
    
    public StructuredExec getExpanded(Map vars, boolean warnUndefVars) {
        String ew = null;
        if (getWorking() != null) {
            ew = getWorking().getPath();
            ew = Variables.expand(vars, ew, warnUndefVars);
        }
        String ee = Variables.expand(vars, getExecutable(), warnUndefVars);
        Argument[] eargs = new Argument[args.length];
        for (int i = 0; i < args.length; i++) {
            eargs[i] = new Argument(Variables.expand(vars, args[i].getArgument(), warnUndefVars), args[i].isLine());
        }
        return new StructuredExec((ew != null) ? new File(ew) : null, ee, eargs);
    }
    
    /**
     * Representation of an argument.<br>
     * The argument can be either a single String argument, or a sequence of
     * space-separated arguments in a single String. This is distinguished
     * by the <code>line</code> property.
     */
    public static class Argument extends Object {
        
        private String argument;
        private boolean line;
        
        /**
         * Create a new argument.
         * @param argument The String value of the argument.
         * @param line When <code>false</code>, the <code>argument</code>
         *        represents a single String argument of the executable,
         *        if <code>false</code>, the <code>argument</code> represents
         *        a sequence of space-separated arguments.
         */
        public Argument(String argument, boolean line) {
            this.argument = argument;
            this.line = line;
        }
        
        public String getArgument() {
            return argument;
        }
        
        public void setArgument(String argument) {
            this.argument = argument;
        }
        
        public boolean isLine() {
            return line;
        }
        
        public void setLine(boolean line) {
            this.line = line;
        }
        
    }
    
}

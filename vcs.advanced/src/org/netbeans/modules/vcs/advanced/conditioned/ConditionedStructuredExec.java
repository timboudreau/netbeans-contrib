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

package org.netbeans.modules.vcs.advanced.conditioned;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.vcscore.cmdline.exec.StructuredExec;

import org.netbeans.modules.vcs.advanced.variables.Condition;

/**
 * The structured execution property with conditioned arguments
 *
 * @author  Martin Entlicher
 */
public class ConditionedStructuredExec extends StructuredExec implements Cloneable {
    
    /** Creates a new instance of ConditionedExecArgument */
    public ConditionedStructuredExec(java.io.File working, String executable, StructuredExec.Argument[] args) {
        super(working, executable, args);
    }
    
    public StructuredExec.Argument[] getArguments(Map vars) {
        StructuredExec.Argument[] args = getArguments();
        List newArgs = new ArrayList(args.length);
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ConditionedArgument) {
                //StructuredExec.Argument arg = args
                ConditionedArgument carg = (ConditionedArgument) args[i];
                if (carg.getCondition().isSatisfied(vars)) {
                    newArgs.add(new StructuredExec.Argument(carg.getArgument(), carg.isLine()));
                }
            } else {
                newArgs.add(args[i]);
            }
        }
        return (StructuredExec.Argument[]) newArgs.toArray(new StructuredExec.Argument[0]);
    }
    
    public Object clone() {
        StructuredExec.Argument[] args = getArguments();
        StructuredExec.Argument[] newArgs = new StructuredExec.Argument[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ConditionedArgument) {
                newArgs[i] = new ConditionedArgument((Condition) ((ConditionedArgument) args[i]).getCondition().clone(),
                                                     args[i].getArgument(), args[i].isLine());
            } else {
                newArgs[i] = new StructuredExec.Argument(args[i].getArgument(), args[i].isLine());
            }
        }
        return new ConditionedStructuredExec(getWorking(), getExecutable(), newArgs);
    }
    
    /**
     * A conditioned argument to a structured executor.
     */
    public static class ConditionedArgument extends StructuredExec.Argument {
        
        private Condition c;

        /** Creates a new instance of ConditionedExecArgument */
        public ConditionedArgument(Condition c, String argument, boolean line) {
            super(argument, line);
            this.c = c;
        }

        public Condition getCondition() {
            return c;
        }

    }
    
}

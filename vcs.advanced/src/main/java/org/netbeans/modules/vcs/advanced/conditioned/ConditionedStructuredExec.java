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

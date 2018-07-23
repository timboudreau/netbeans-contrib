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

package org.netbeans.api.vcs;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;

import org.openide.filesystems.FileObject;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

/**
 *
 * @author  Martin Entlicher
 */
class DelegatingCommandInvocationHandler extends Object implements InvocationHandler {

    private Command[] commands;
    private String cmdName;
    private Command metaCommand;
    
    /** Creates a new instance of DelegatingCommandInvocationHandler */
    public DelegatingCommandInvocationHandler(String cmdName, Command[] commands) {
        this.cmdName = cmdName;
        this.commands = commands;
    }
    
    /**
     * Processes a method invocation on a proxy instance and returns
     * the result.  This method will be invoked on an invocation handler
     * when a method is invoked on a proxy instance that it is
     * associated with.
     *
     * @param	proxy the proxy instance that the method was invoked on
     *
     * @param	method the <code>Method</code> instance corresponding to
     * the interface method invoked on the proxy instance.  The declaring
     * class of the <code>Method</code> object will be the interface that
     * the method was declared in, which may be a superinterface of the
     * proxy interface that the proxy class inherits the method through.
     *
     * @param	args an array of objects containing the values of the
     * arguments passed in the method invocation on the proxy instance,
     * or <code>null</code> if interface method takes no arguments.
     * Arguments of primitive types are wrapped in instances of the
     * appropriate primitive wrapper class, such as
     * <code>java.lang.Integer</code> or <code>java.lang.Boolean</code>.
     *
     * @return	the value to return from the method invocation on the
     * proxy instance.  If the declared return type of the interface
     * method is a primitive type, then the value returned by
     * this method must be an instance of the corresponding primitive
     * wrapper class; otherwise, it must be a type assignable to the
     * declared return type.  If the value returned by this method is
     * <code>null</code> and the interface method's return type is
     * primitive, then a <code>NullPointerException</code> will be
     * thrown by the method invocation on the proxy instance.  If the
     * value returned by this method is otherwise not compatible with
     * the interface method's declared return type as described above,
     * a <code>ClassCastException</code> will be thrown by the method
     * invocation on the proxy instance.
     *
     * @throws	Throwable the exception to throw from the method
     * invocation on the proxy instance.  The exception's type must be
     * assignable either to any of the exception types declared in the
     * <code>throws</code> clause of the interface method or to the
     * unchecked exception types <code>java.lang.RuntimeException</code>
     * or <code>java.lang.Error</code>.  If a checked exception is
     * thrown by this method that is not assignable to any of the
     * exception types declared in the <code>throws</code> clause of
     * the interface method, then an
     * {@link UndeclaredThrowableException} containing the
     * exception that was thrown by this method will be thrown by the
     * method invocation on the proxy instance.
     *
     * @see	UndeclaredThrowableException
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        if ("execute".equals(name)) {
            return execute();
        } else if ("setFiles".equals(name)) {
            throw new UnsupportedOperationException(name);
        } else if ("getFiles".equals(name)) {
            return getFiles();
        }
        Class[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = args[i].getClass();
        }
        Object ret = null;
        for (int i = 0; i < commands.length; i++) {
            try {
                Method m = commands[i].getClass().getMethod(name, parameterTypes);
                if (m != null) {
                    ret = m.invoke(commands[i], args);
                }
            } catch (Exception ex) {}
        }
        return ret;
    }
    
    void setMetaCommand(Command metaCommand) {
        this.metaCommand = metaCommand;
    }
    
    private FileObject[] getFiles() {
        ArrayList files = new ArrayList();
        for (int i = 0; i < commands.length; i++) {
            files.addAll(Arrays.asList(commands[i].getFiles()));
        }
        return (FileObject[]) files.toArray(new FileObject[files.size()]);
    }
    
    private CommandTask execute() {
        CommandTask[] tasks = new CommandTask[commands.length];
        for (int i = 0; i < commands.length; i++) {
            tasks[i] = commands[i].execute();
        }
        return new MultiCommandTask(tasks);
    }
    
    
    private class MultiCommandTask extends CommandTask {
        
        private CommandTask[] tasks;
        
        public MultiCommandTask(CommandTask[] tasks) {
            this.tasks = tasks;
        }
        
        /**
         * Get the name of the command.
         */
        public String getName() {
            return cmdName;
        }
        
        /**
         * Get the display name of the command. It will be visible on the popup menu under this name.
         * When <code>null</code>, the command will not be visible on the popup menu.
         */
        public String getDisplayName() {
            return metaCommand.getDisplayName();
        }
        
        /**
         * Get files this task acts on.
         */
        public FileObject[] getFiles() {
            return DelegatingCommandInvocationHandler.this.getFiles();
        }
        
        /**
         * Put the actual execution of this task here.
         * This method will be called automatically after process() call. Do NOT call this
         * method.
         */
        protected int execute() {
            int exitStatus = STATUS_SUCCEEDED;
            for (int i = 0; i < tasks.length; i++) {
                tasks[i].waitFinished();
                int exit = tasks[i].getExitStatus();
                if (exit != STATUS_SUCCEEDED) exitStatus = STATUS_FAILED;
            }
            return exitStatus;
        }
        
    }
    
}

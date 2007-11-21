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
 * Contributor(s):
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.debugger.threadviewenhancement.ui.models;

import com.sun.jdi.AbsentInformationException;
import org.netbeans.api.debugger.jpda.CallStackFrame;
import org.netbeans.api.debugger.jpda.JPDAThread;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.TreeModel;
import org.netbeans.spi.viewmodel.TreeModelFilter;
import org.netbeans.spi.viewmodel.UnknownTypeException;

/**
 *
 * @author sc32560
 */
public class ThreadsViewTreeModelFilter implements TreeModelFilter {

    public Object getRoot(TreeModel original) {
        return original.getRoot();
    }

    public Object[] getChildren(TreeModel original, Object parent, int from, int to) throws UnknownTypeException {        
        if (parent instanceof JPDAThread) {
            JPDAThread threadNode = (JPDAThread) parent;
            if (threadNode.isSuspended()) {
                try {
                    CallStackFrame[] callStackFrames = threadNode.getCallStack();
                    if (callStackFrames != null && callStackFrames.length > 0) {                        
                        return callStackFrames;
                    }
                } catch (AbsentInformationException ex) {
                    return new Object[0];
                }
            }            
        }
        return original.getChildren(parent, from, to);
    }

    public int getChildrenCount(TreeModel original, Object node) throws UnknownTypeException {
        int childrenCount = original.getChildrenCount(node);
        if (node instanceof JPDAThread) {
            JPDAThread threadNode = (JPDAThread) node;
            if (threadNode.isSuspended()) {
                try {
                    childrenCount += threadNode.getCallStack().length;
                } catch (AbsentInformationException ex) {
                    //
                }
            }
        }
        return childrenCount;
    }

    public boolean isLeaf(TreeModel original, Object node) throws UnknownTypeException {
        if (node instanceof CallStackFrame) {
            return true;
        } else if (node instanceof JPDAThread) {
            JPDAThread threadNode = (JPDAThread) node;
            return !threadNode.isSuspended();
        }
        return original.isLeaf(node);
    }

    public void addModelListener(ModelListener l) {
        //
    }

    public void removeModelListener(ModelListener l) {
        //
    }
}

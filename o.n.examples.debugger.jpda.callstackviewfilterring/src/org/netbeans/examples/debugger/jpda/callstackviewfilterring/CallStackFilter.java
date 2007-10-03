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

package org.netbeans.examples.debugger.jpda.callstackviewfilterring;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.debugger.jpda.CallStackFrame;
import org.netbeans.spi.viewmodel.ComputingException;
import org.netbeans.spi.viewmodel.NoInformationException;
import org.netbeans.spi.viewmodel.NodeModel;
import org.netbeans.spi.viewmodel.TreeModel;
import org.netbeans.spi.viewmodel.TreeModelFilter;
import org.netbeans.spi.viewmodel.TreeModelListener;
import org.netbeans.spi.viewmodel.UnknownTypeException;




public class CallStackFilter implements TreeModelFilter, NodeModel {
    
    
    /** 
     * Returns filtered root of hierarchy.
     *
     * @param   original the original tree model
     * @return  filtered root of hierarchy
     */
    public Object getRoot (TreeModel original) {
        return original.getRoot ();
    }
    
    /**
     * Returns number of filterred children for given node.
     * 
     * @param   original the original tree model
     * @param   node the parent node
     * @throws  NoInformationException if the set of children can not be 
     *          resolved
     * @throws  ComputingException if the children resolving process 
     *          is time consuming, and will be performed off-line 
     * @throws  UnknownTypeException if this TreeModel implementation is not
     *          able to resolve children for given node type
     *
     * @return  true if node is leaf
     */
    public int getChildrenCount (
        TreeModel original,
        Object node
    ) throws NoInformationException, ComputingException, UnknownTypeException {
        if (node.equals (original.getRoot ())) {
            Object[] originalCh = original.getChildren (
                node, 
                0, 
                original.getChildrenCount (node)
            );
            int i, k = originalCh.length, j = 0;
            boolean in = false;
            for (i = 0; i < k; i++) {
                if (! (originalCh [i] instanceof CallStackFrame)) {
                    j++;
                    continue;
                }
                CallStackFrame f = (CallStackFrame) originalCh [i];
                String className = f.getClassName ();
                if (className.startsWith ("java")) {
                    if (!in) {
                        j++;
                        in = true;
                    }
                } else {
                    in = false;
                    j++;
                }
            }
            return j;
        }
        if (node instanceof JavaFrames)
            return ((JavaFrames) node).getStack ().size ();
        return original.getChildrenCount (node);
    }
    
    /** 
     * Returns filtered children for given parent on given indexes.
     * Typically you should get original nodes 
     * (<code>original.getChildren (...)</code>), and modify them, or return
     * it without modifications. You should not throw UnknownTypeException
     * directly from this method!
     *
     * @param   original the original tree model
     * @param   parent a parent of returned nodes
     * @throws  NoInformationException if the set of children can not be 
     *          resolved
     * @throws  ComputingException if the children resolving process 
     *          is time consuming, and will be performed off-line 
     * @throws  UnknownTypeException this exception can be thrown from 
     *          <code>original.getChildren (...)</code> method call only!
     *
     * @return  children for given parent on given indexes
     */
    public Object[] getChildren (
        TreeModel original, 
        Object parent, 
        int from, 
        int to
    ) throws NoInformationException, ComputingException, UnknownTypeException {
        if (parent.equals (original.getRoot ())) {
            Object[] originalCh = original.getChildren (
                parent, 
                0, 
                original.getChildrenCount (parent)
            );
            int i, k = originalCh.length;
            ArrayList newCh = new ArrayList ();
            JavaFrames javaFrames = null;
            for (i = 0; i < k; i++) {
                if (! (originalCh [i] instanceof CallStackFrame)) {
                    newCh.add (originalCh [i]);
                    continue;
                }
                CallStackFrame f = (CallStackFrame) originalCh [i];
                String className = f.getClassName ();
                if (className.startsWith ("java")) {
                    if (javaFrames == null) {
                        javaFrames = new JavaFrames ();
                        newCh.add (javaFrames);
                    }
                    javaFrames.addFrame (f);
                } else {
                    javaFrames = null;
                    newCh.add (f);
                }
            }
            return newCh.subList (from, to).toArray ();
        }
        if (parent instanceof JavaFrames)
            return ((JavaFrames) parent).getStack ().toArray ();
        return original.getChildren (parent, from, to);
    }
    
    /**
     * Returns true if node is leaf. You should not throw UnknownTypeException
     * directly from this method!
     * 
     * @param   original the original tree model
     * @throws  UnknownTypeException this exception can be thrown from 
     *          <code>original.isLeaf (...)</code> method call only!
     * @return  true if node is leaf
     */
    public boolean isLeaf (TreeModel original, Object node) 
    throws UnknownTypeException {
        if (node instanceof JavaFrames) return false;
        return original.isLeaf (node);
    }
    
    public void addTreeModelListener (TreeModelListener l) {
    }
    public void removeTreeModelListener (TreeModelListener l) {
    }
    
    public String getDisplayName (Object node) throws UnknownTypeException {
        if (node instanceof JavaFrames)
            return "Java Callstack Frames";
        throw new UnknownTypeException (node);
    }
    
    public String getIconBase (Object node) throws UnknownTypeException {
        if (node instanceof JavaFrames)
            return "org/netbeans/examples/debugger/jpda/callstackviewfilterring/NonCurrentFrame";
        throw new UnknownTypeException (node);
    }
    
    public String getShortDescription (Object node) throws UnknownTypeException {
        if (node instanceof JavaFrames)
            return "Unimportant hidden callstack frames";
        throw new UnknownTypeException (node);
    }
    
    
    // innerclasses ............................................................
    
    private static class JavaFrames {
        private List frames = new ArrayList ();
        
        void addFrame (CallStackFrame frame) {
            frames.add (frame);
        }
        
        List getStack () {
            return frames;
        }
        
        public boolean equals (Object o) {
            if (!(o instanceof JavaFrames)) return false;
            if (frames.size () != ((JavaFrames) o).frames.size ()) return false;
            if (frames.size () == 0) return o == this;
            return frames.get (0).equals (
                ((JavaFrames) o).frames.get (0)
            );
        }
        
        public int hashCode () {
            if (frames.size () == 0) return super.hashCode ();
            return frames.get (0).hashCode ();
        }
    }
}

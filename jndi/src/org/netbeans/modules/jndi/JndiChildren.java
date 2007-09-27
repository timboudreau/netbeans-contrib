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

package org.netbeans.modules.jndi;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import javax.naming.CompositeName;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import javax.naming.NameClassPair;
import javax.naming.Context;
import javax.naming.Binding;
import org.openide.ErrorManager;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.netbeans.modules.jndi.utils.APCTarget;
import org.netbeans.modules.jndi.utils.Request;

/** Children class for Directories in JNDI tree.
 *  It's responsible for lazy initialization as well
 *  as it is an data model for JndiNode.
 *
 *  @author Ales Novak, Tomas Zezula
 */
public final class JndiChildren extends Children.Keys implements APCTarget {
    
    /** This constant represents the name of context class */
    public final static String CONTEXT_CLASS_NAME = "javax.naming.Context";
    
    /** Class object for javax.naming.Context */
    private static Class ctxClass;
    
    /** Current Directory context */
    private final Context thisContext;
    
    
    /** The shadow key list for merginag and handling errors*/
    private ArrayList keys;
    
    /** Wait node hoder*/
    private WaitNode waitNode;
    
    private CompositeName offset;
    
    /** Constructor
     *  @param parentContext the initial context
     *  @param offset the relative offset of Node in context
     */
    public JndiChildren(Context thisContext, CompositeName offset){
        this.thisContext = thisContext;
        this.offset = offset;
        this.keys = new ArrayList();
    }
    
    /** Called when node is being opened
     */
    protected void addNotify(){
        //Construct WaitNode key
        this.waitNode = new WaitNode();
        this.add( new Node[] { this.waitNode});
        prepareKeys();
    }
    
    /** Called when node is not being used any more
     */
    protected void removeNotify(){
        setKeys(Collections.EMPTY_SET);
    }
    
    /** Returns actual offset
     *  @return the relative offset of Node
     */
    public CompositeName getOffset() {
        return offset;
    }
    
    /** From compatibility reasons
     */
    public String getOffsetAsString() {
        return JndiObjectCreator.stringifyCompositeName(this.offset); // No I18N
    }
    
    /** Returns context
     *  @return the initial context
     */
    public Context getContext() {
        return thisContext;
    }
    
    /** This method creates keys
     *  exception NamingException if Context.list() failed
     */
    public void prepareKeys(){
        JndiRootNode.getDefault().refresher.post(new Request(this));
    }
    
    /** Creates Node for key
     *  @param key the key for which the Node should be created
     *  @return the array of created Nodes
     */
    public Node[] createNodes(Object key) {
        Binding binding = null;
        String objName = null;
        CompositeName newName = null;
        if (key == null) {
            return null;
        }
        if (! (key instanceof JndiKey)) {
            return null;
        }
        binding =  ((JndiKey)key).name;
        try {
            if (((JndiKey)key).failed){
                // Failed Node
                return new Node[] {new JndiFailedNode((JndiKey)key, this.offset)};
            }
            else if (isContext(binding)) {
                // Contex Node
                return new Node[] {new JndiNode((JndiKey)key, this.offset)};
            }else{
                // Leaf Node
                return new Node[] {new JndiLeafNode((JndiKey)key, this.offset)};
            }
        }catch (javax.naming.InvalidNameException invalidName) {
            return new Node[0];
        }
    }
    
    /** Heuristicaly decides whether specified class is a Context or not.
     *  @param className the name of Class
     *  @return true if className represents the name of Context*/
    static boolean isContext(Binding binding) {
        String className = binding.getClassName();
        if (className == null) {
            return false;
        }
        if (className.equals(CONTEXT_CLASS_NAME)) {
            return true;
        } else if (isPrimitive(className)) {
            return false;
        } else {
            return (binding.getObject() instanceof javax.naming.Context);
        }
    }
    
    /** Decides if the string represents the name of primitive type
     *  @param s the name of type
     *  @return true iff s is one of int, long, char, boolean, float, byte, double
     */
    private static boolean isPrimitive(String s) {
        if (s.indexOf('.') >= 0) {
            return false;
        }
        
        return s.equals("int") ||
        s.equals("short") ||
        s.equals("long") ||
        s.equals("byte") ||
        s.equals("char") ||
        s.equals("float") ||
        s.equals("double") ||
        s.equals("boolean");
    }
    
    /** Returns the super class for classes representing the Context
     *  @return Class object for javax.naming.Context
     */
    static Class getCtxClass() throws ClassNotFoundException {
        if (ctxClass == null) {
            ctxClass = Class.forName(CONTEXT_CLASS_NAME);
        }
        return ctxClass;
    }
    
    /** This method is called by Refreshd thread before performing
     *  main action
     */
    public void preAction() throws Exception{
    }
    
    /** This is the main action called by Refreshd
     */
    public void performAction() throws Exception {
        NamingEnumeration ne = thisContext.listBindings("");
        this.keys.clear();
        if (ne == null)
            return;
        while (ne.hasMore()){
            Binding b = (Binding)ne.next();
            this.keys.add(new JndiKey(b));
        }
    }
    
    
    /** This action is called by Refreshd after performing main action
     */
    public void postAction() throws Exception {
        this.setKeys(this.keys);
        if (this.waitNode != null) {
            this.remove( new Node[]{ this.waitNode});
            this.waitNode = null;
        }
    }
    
    /** Called when APCTarget operation caused an exception
     *  to perform clean up
     */
    public void actionFailed() {
        try {
            Children parentChildren = this.getNode().getParentNode().getChildren();
            if (parentChildren instanceof JndiChildren) {
                JndiKey key = ((JndiNode)this.getNode()).getKey();
                key.failed = true;
                ((JndiChildren)parentChildren).updateKey (key);
            }
            else if (parentChildren instanceof JndiRootNodeChildren) {
                JndiRootCtxKey key = new JndiRootCtxKey (this.thisContext.getEnvironment(),((JndiNode)this.getNode()).getIndex());
                ((JndiRootNodeChildren)parentChildren).updateKey (key);
            }
            else if (this.waitNode != null) {
                this.remove( new Node[] {this.waitNode});
                this.waitNode = null;
            }
        }catch (Exception exception) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, exception);
        }
    }
    
    /** public method that returns the node for which the Children is created
     *  @return Node
     */
    public final Node getOwner(){
        return this.getNode();
    }
    
    /** This method calls the refreshKey method of Children,
     *  used by Refreshd for changing the failed nodes
     * @see org.netbeans.modules.jndi.utils.Refreshd
     */
    public void updateKey(Object key){
        this.refreshKey(key);
    }
    
}


/*
 * <<Log>>
 *  14   Jaga      1.11.2.0.1.03/29/00  Tomas Zezula
 *  13   Gandalf-post-FCS1.11.2.0    2/24/00  Ian Formanek    Post FCS changes
 *  12   Gandalf   1.11        1/14/00  Tomas Zezula
 *  11   Gandalf   1.10        12/17/99 Tomas Zezula
 *  10   Gandalf   1.9         12/15/99 Tomas Zezula
 *  9    Gandalf   1.8         12/15/99 Tomas Zezula
 *  8    Gandalf   1.7         11/5/99  Tomas Zezula
 *  7    Gandalf   1.6         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  6    Gandalf   1.5         7/9/99   Ales Novak      localization + code
 *       requirements followed
 *  5    Gandalf   1.4         6/18/99  Ales Novak      redesigned + delete
 *       action
 *  4    Gandalf   1.3         6/9/99   Ales Novak      refresh action +
 *       destroying subcontexts
 *  3    Gandalf   1.2         6/9/99   Ian Formanek    ---- Package Change To
 *       org.openide ----
 *  2    Gandalf   1.1         6/8/99   Ales Novak      sources beautified +
 *       subcontext creation
 *  1    Gandalf   1.0         6/4/99   Ales Novak
 * $
 */

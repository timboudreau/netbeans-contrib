/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
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
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.netbeans.modules.jndi.utils.APCTarget;

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

    /** Initial Directory context */
    private final Context parentContext;

    /** Offset in Initial Directory context */
    private final CompositeName offset;

    /** The shadow key list for merginag and handling errors*/
    private ArrayList keys;

    /** Wait node hoder*/
    private WaitNode waitNode;

    /** Constructor
     *  @param parentContext the initial context
     *  @param offset the relative offset of Node in context
     */
    public JndiChildren(Context parentContext, CompositeName offset){
        this.parentContext = parentContext;
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
        setKeys (Collections.EMPTY_SET);
    }

    /** Returns actual offset
     *  @return the relative offset of Node
     */
    public CompositeName getOffset() {
        return offset;
    }

    /** Returns context
     *  @return the initial context
     */
    public Context getContext() {
        return parentContext;
    }

    /** This method creates keys
     *  exception NamingException if Context.list() failed
     */
    public void prepareKeys(){
        JndiRootNode.getDefault().refresher.addNewItem(this);
    }

    /** Creates Node for key
     *  @param key the key for which the Node should be created
     *  @return the array of created Nodes
     */
    public Node[] createNodes(Object key) {
        NameClassPair np = null;
        String objName = null;
        CompositeName newName = null;
        if (key == null) {
            return null;
        }
        if (! (key instanceof JndiKey)) {
            return null;
        }
        try{
            np =  ((JndiKey)key).name;
            objName = np.getName();
            newName = (CompositeName) ((CompositeName) offset.clone()).add(objName);
            if (((JndiKey)key).failed){
                // Failed Node
                return new Node[] {new JndiFailedNode(key, parentContext, newName, objName, np.getClassName())};
            }
            else if (isContext(np.getClassName())) {
                // Contex Node
                return new Node[] {new JndiNode(key, parentContext, newName, objName)};
            }else{
                // Leaf Node
                return new Node[] {new JndiLeafNode(key, parentContext, newName, objName, np.getClassName())};
            }
        }catch (NamingException ne){
            // Any of the context operations failed
            // try to add at least Failed Node
            return new Node[] {new JndiFailedNode(key, parentContext, newName, objName, np.getClassName())};
        }
    }

    /** Heuristicaly decides whether specified class is a Context or not.
     *  @param className the name of Class
     *  @return true if className represents the name of Context*/
    static boolean isContext(String className) {
        if (className.equals(CONTEXT_CLASS_NAME)) {
            return true;
        } else if (isPrimitive(className)) {
            return false;
        } else {
            try {
                Class clazz = Class.forName(className);
                if (getCtxClass().isAssignableFrom(clazz)) {
                    return true;
                }
            } catch (ClassNotFoundException e) {
                // Changed from notifying an exception to return false
                // Needed by some directory services, that provides an
                // Class repository with deployment.
                return false;
            }
        }
        return false;
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
        NamingEnumeration ne = parentContext.list(offset);
        this.keys.clear();
        if (ne == null)
            return;
        while (ne.hasMore()){
            this.keys.add(new JndiKey((NameClassPair)ne.next()));
        }
    }


    /** This action is called by Refreshd after performing main action
     */
    public void postAction() throws Exception{
        this.setKeys(this.keys);
        this.remove ( new Node[]{ this.waitNode});
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

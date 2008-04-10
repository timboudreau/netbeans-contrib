/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.models;

import org.netbeans.modules.cnd.profiler.data.Function;

/**
 *
 * @author eu155513
 */
public class CalleesTreeModel extends FunctionsTreeModel {
    public CalleesTreeModel(Function root) {
        super(new Function[]{root});
    }
    
    public Object[] getChildren (Object parent, int from, int to) {
        if (parent == ROOT) {
            return rootFunctions;
        }
        if (parent instanceof Function) {
            return ((Function)parent).getCallees().toArray();
        }
        return new Object[0];
    }
    
    public boolean isLeaf (Object node) {
        if (node == ROOT) {
            return false;
        }
        if (node instanceof Function) {
            return ((Function)node).getCallees().isEmpty();
        }
        return true;
    }
}
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
public class PlainTreeModel extends FunctionsTreeModel {
    public PlainTreeModel(Function[] data) {
        super(data);
    }
    
    public Object[] getChildren (Object parent, int from, int to) {
        if (parent == ROOT) {
            return rootFunctions;
        }
        return new Object[0];
    }
    
    public boolean isLeaf (Object node) {
        if (node == ROOT) {
            return false;
        }
        return true;
    }
}

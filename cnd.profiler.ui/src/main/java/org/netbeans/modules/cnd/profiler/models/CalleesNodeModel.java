/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.models;

import java.util.Collection;
import org.netbeans.modules.cnd.profiler.data.FunctionContainer;

/**
 *
 * @author eu155513
 */
public class CalleesNodeModel extends FunctionNodeModel {

    @Override
    public Collection getChildren(FunctionContainer fc) {
        return fc.getFunction().getCallees();
    }

    @Override
    public String getIcon() {
        return "org/netbeans/modules/cnd/profiler/resources/node.png";
    }
    
    /*@Override
    public String getIconBaseWithExtension(Object node) throws UnknownTypeException {
        if (node instanceof FunctionContainer) {
            if (!((FunctionContainer)node).getFunction().getCallees().isEmpty()) {
                return "org/netbeans/modules/cnd/profiler/resources/node.png"; // NOI18N;
            }
            return "org/netbeans/modules/cnd/profiler/resources/leaf.png"; // NOI18N;
        }
        return super.getIconBaseWithExtension(node);
    }*/
}

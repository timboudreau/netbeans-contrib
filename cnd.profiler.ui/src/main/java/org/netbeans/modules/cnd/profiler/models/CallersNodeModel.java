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
public class CallersNodeModel extends FunctionNodeModel {

    @Override
    public Collection getChildren(FunctionContainer fc) {
        return fc.getFunction().getCallers();
    }

    @Override
    public String getIcon() {
        return "org/netbeans/modules/cnd/profiler/resources/reverseNode.png";
    }
    
    /*@Override
    public String getIconBaseWithExtension(Object node) throws UnknownTypeException {
        if (node instanceof FunctionContainer) {
            if (!((FunctionContainer)node).getFunction().getCallers().isEmpty()) {
                return "org/netbeans/modules/cnd/profiler/resources/reverseNode.png"; // NOI18N;
            }
            return "org/netbeans/modules/cnd/profiler/resources/leaf.png"; // NOI18N;
        }
        return super.getIconBaseWithExtension(node);
    }*/
}

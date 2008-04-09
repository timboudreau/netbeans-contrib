/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.models;

import org.netbeans.modules.cnd.profiler.data.Function;
import org.netbeans.spi.viewmodel.UnknownTypeException;

/**
 *
 * @author eu155513
 */
public class CallersNodeModel extends FunctionNodeModel {
    @Override
    public String getIconBaseWithExtension(Object node) throws UnknownTypeException {
        if (node instanceof Function) {
            if (!((Function)node).getCallers().isEmpty()) {
                return "org/netbeans/modules/cnd/profiler/resources/reverseNode.png"; // NOI18N;
            }
            return "org/netbeans/modules/cnd/profiler/resources/leaf.png"; // NOI18N;
        }
        return super.getIconBaseWithExtension(node);
    }
}

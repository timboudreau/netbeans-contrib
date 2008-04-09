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
public class CalleesNodeModel extends FunctionNodeModel {
    @Override
    public String getIconBaseWithExtension(Object node) throws UnknownTypeException {
        if (node instanceof Function) {
            if (!((Function)node).getCallees().isEmpty()) {
                return "org/netbeans/modules/cnd/profiler/resources/node.png"; // NOI18N;
            }
            return "org/netbeans/modules/cnd/profiler/resources/leaf.png"; // NOI18N;
        }
        return super.getIconBaseWithExtension(node);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.views;

import org.netbeans.modules.cnd.profiler.data.Function;
import org.netbeans.modules.cnd.profiler.models.CallersNodeModel;
import org.netbeans.modules.cnd.profiler.models.CallersTreeModel;

/**
 *
 * @author eu155513
 */
public class CallersView extends FunctionView {
    public CallersView() {
        super(new CallersTreeModel(new Function("")), new CallersNodeModel());
    }
}

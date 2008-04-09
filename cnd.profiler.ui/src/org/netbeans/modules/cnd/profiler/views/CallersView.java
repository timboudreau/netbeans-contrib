/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.views;

import org.netbeans.modules.cnd.profiler.models.CallersNodeModel;
import org.netbeans.modules.cnd.profiler.models.CallersTreeModel;
import org.netbeans.modules.cnd.profiler.providers.TestProvider;

/**
 *
 * @author eu155513
 */
public class CallersView extends FunctionView {
    public CallersView() {
        super(new CallersTreeModel(TestProvider.getInstance().getFunctions()[0]), new CallersNodeModel());
    }
}

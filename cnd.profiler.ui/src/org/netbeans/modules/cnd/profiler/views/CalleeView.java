/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.views;

import org.netbeans.modules.cnd.profiler.models.CalleesNodeModel;
import org.netbeans.modules.cnd.profiler.models.CalleesTreeModel;
import org.netbeans.modules.cnd.profiler.providers.TestProvider;


/**
 *
 * @author eu155513
 */
public class CalleeView extends FunctionView {
    public CalleeView() {
        super(new CalleesTreeModel(TestProvider.getInstance().getFunctions()[0]), new CalleesNodeModel());
    }
}


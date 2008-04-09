/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.views;

import org.netbeans.modules.cnd.profiler.models.FunctionNodeModel;
import org.netbeans.modules.cnd.profiler.models.PlainTreeModel;
import org.netbeans.modules.cnd.profiler.providers.TestProvider;


/**
 *
 * @author eu155513
 */
public class PlainView extends FunctionView {
    public PlainView() {
        super(new PlainTreeModel(TestProvider.getInstance().getFunctions()), new FunctionNodeModel());
    }
}

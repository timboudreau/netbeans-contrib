/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.views;

import org.netbeans.modules.cnd.profiler.data.Function;
import org.netbeans.modules.cnd.profiler.models.FunctionNodeModel;
import org.netbeans.modules.cnd.profiler.models.PlainTreeModel;

/**
 *
 * @author eu155513
 */
public class PlainView extends FunctionView {
    public PlainView() {
        super(new PlainTreeModel(new Function[0]), new FunctionNodeModel());
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.views;

import org.netbeans.modules.cnd.profiler.models.CallersNodeModel;

/**
 *
 * @author eu155513
 */
public class CallersViewPanel extends FunctionViewPanel {

    public CallersViewPanel() {
        super(new CallersNodeModel());
    }

}

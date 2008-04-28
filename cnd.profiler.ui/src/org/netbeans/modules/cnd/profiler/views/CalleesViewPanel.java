/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.views;

import org.netbeans.modules.cnd.profiler.models.CalleesNodeModel;

/**
 *
 * @author eu155513
 */
public class CalleesViewPanel extends FunctionViewPanel {

    public CalleesViewPanel() {
        super(new CalleesNodeModel());
    }
}


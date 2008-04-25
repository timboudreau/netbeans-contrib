/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.views;

import org.netbeans.modules.cnd.profiler.models.FunctionNodeModel;

/**
 *
 * @author eu155513
 */
public class PlainViewPanel extends FunctionViewPanel {

    public PlainViewPanel() {
        super(new FunctionNodeModel());
    }
    
}

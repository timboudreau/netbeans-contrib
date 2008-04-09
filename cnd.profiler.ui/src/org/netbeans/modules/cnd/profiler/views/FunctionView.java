/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.views;

import java.util.Arrays;
import javax.swing.JComponent;
import org.netbeans.modules.cnd.profiler.models.FunctionNodeModel;
import org.netbeans.modules.cnd.profiler.models.FunctionsTreeModel;
import org.netbeans.modules.cnd.profiler.models.TableModelImpl;
import org.netbeans.spi.viewmodel.Models;

/**
 *
 * @author eu155513
 */
public abstract class FunctionView {
    private final JComponent component;
    private final FunctionsTreeModel treeModel;

    protected FunctionView(FunctionsTreeModel treeModel, FunctionNodeModel nodeModel) {
        this.treeModel = treeModel;
        component = Models.createView (
        Models.createCompoundModel (
        Arrays.asList(new Object[] {
                    treeModel,      // TreeModel
                    nodeModel,
                    new TableModelImpl(),
                    PropertyColumnModel.createTimeColumnModel(),
                    PropertyColumnModel.createSelfTimeColumnModel()
                })
            )
        );
    }

    public JComponent getComponent() {
        return component;
    }

    public FunctionsTreeModel getModel() {
        return treeModel;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.views;

import java.util.Arrays;
import javax.swing.JComponent;
import org.netbeans.modules.cnd.profiler.data.Function;
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
    private final FunctionNodeModel nodeModel;

    protected FunctionView(FunctionsTreeModel treeModel, FunctionNodeModel nodeModel) {
        this.treeModel = treeModel;
        this.nodeModel = nodeModel;
        
        component = Models.createView (createModel());
    }
    
    private Models.CompoundModel createModel() {
        return Models.createCompoundModel (
        Arrays.asList(new Object[] {
                    treeModel,
                    nodeModel,
                    new TableModelImpl(),
                    PropertyColumnModel.createTimeColumnModel(),
                    PropertyColumnModel.createSelfTimeColumnModel()
                })
            );
    }

    public JComponent getComponent() {
        return component;
    }
    
    public void setRoot(Function function) {
        treeModel.setRoot(function);
        Models.setModelsToView(component, createModel());
    }
    
    public void setRoot(Function[] functions) {
        treeModel.setRoot(functions);
        Models.setModelsToView(component, createModel());
    }
}

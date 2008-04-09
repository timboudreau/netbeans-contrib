/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.models;

import java.util.Vector;
import org.netbeans.modules.cnd.profiler.data.Function;
import org.netbeans.spi.viewmodel.ModelEvent;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.TreeModel;
import org.netbeans.spi.viewmodel.UnknownTypeException;

/**
 *
 * @author eu155513
 */
public abstract class FunctionsTreeModel implements TreeModel {
    protected Function[] rootFunctions;
    
    private Vector<ModelListener> listeners = new Vector ();

    protected FunctionsTreeModel(Function[] rootFunctions) {
        this.rootFunctions = rootFunctions;
    }
    
    public Object getRoot () {
        return ROOT;
    }
    
    public void setRoot(Function rootFunction) {
        this.rootFunctions = new Function[]{rootFunction};
        for (ModelListener listener : listeners) {
            listener.modelChanged(new ModelEvent.TreeChanged(this));
        }
    }
    
    public void addModelListener(ModelListener arg0) {
        listeners.add(arg0);
    }

    public int getChildrenCount(Object arg0) throws UnknownTypeException {
        return Integer.MAX_VALUE;
    }

    public void removeModelListener(ModelListener arg0) {
        listeners.remove(arg0);
    }
}

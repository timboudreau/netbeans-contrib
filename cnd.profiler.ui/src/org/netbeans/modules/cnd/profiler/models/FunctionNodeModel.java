/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.models;

import java.awt.datatransfer.Transferable;
import java.io.IOException;
import org.netbeans.modules.cnd.profiler.data.Function;
import org.netbeans.spi.viewmodel.ExtendedNodeModel;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.TreeModel;
import org.netbeans.spi.viewmodel.UnknownTypeException;
import org.openide.util.datatransfer.PasteType;

/**
 *
 * @author eu155513
 */
public class FunctionNodeModel implements ExtendedNodeModel {
    public void addModelListener(ModelListener node) {
    }

    public void removeModelListener(ModelListener node) {
    }

    public String getDisplayName (Object node) {
        if (node == TreeModel.ROOT) {
            return "Name";
        }
        if (node instanceof Function) {
            return ((Function)node).getName();
        }
        return "";
    }
    
    public String getIconBase (Object node) {
        return null;
    }
    
    public String getShortDescription (Object node) {
        if (node == TreeModel.ROOT) {
            return "Name";
        }
        return "";
    }

    public boolean canCopy(Object node) throws UnknownTypeException {
        return false;
    }

    public boolean canCut(Object node) throws UnknownTypeException {
        return false;
    }

    public boolean canRename(Object node) throws UnknownTypeException {
        return false;
    }

    public Transferable clipboardCopy(Object node) throws IOException, UnknownTypeException {
        return null;
    }

    public String getIconBaseWithExtension(Object node) throws UnknownTypeException {
        return null;
    }

    public PasteType[] getPasteTypes(Object node, Transferable arg1) throws UnknownTypeException {
        return null;
    }

    public void setName(Object node, String arg1) throws UnknownTypeException {
        
    }

    public Transferable clipboardCut(Object node) throws IOException, UnknownTypeException {
        return null;
    }
}

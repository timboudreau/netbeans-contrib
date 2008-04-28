/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.models;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.modules.cnd.profiler.data.FunctionContainer;
import org.netbeans.modules.cnd.profiler.views.Column;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author eu155513
 */
public class FunctionNode extends AbstractNode {
    protected final FunctionContainer fc;
    protected final Column[] columns;
    protected final FunctionNodeModel nodeModel;

    protected FunctionNode(Column[] columns, FunctionContainer fc, FunctionNodeModel nodeModel) {
        super(Children.LEAF, Lookups.singleton(fc));
        this.columns = columns;
        this.fc = fc;
        this.nodeModel = nodeModel;
        
        setDisplayName(fc.getFunction().getName());
        setIconBaseWithExtension(nodeModel.getIcon());
        
        if (!getKeys().isEmpty()) {
            setChildren(new MyChildren());
        }
    }

    @Override
    public Action getPreferredAction() {
        return new AbstractAction () {
            public void actionPerformed (ActionEvent e) {
                // go to source
                Csm
            }
        };
    }
    
    protected FunctionNode(Column[] columns, FunctionNodeModel nodeModel) {
        super(Children.LEAF);
        this.columns = columns;
        this.fc = null;
        this.nodeModel = nodeModel;
    }
    
    protected Collection getKeys() {
        return nodeModel.getChildren(fc);
    }
    
    protected class MyChildren extends Children.Keys {
        @Override
        protected Node[] createNodes(Object obj) {
            if (obj instanceof FunctionContainer) {
                return new Node[]{new FunctionNode(columns, (FunctionContainer)obj, nodeModel)};
            }
            return new Node[0];
        }

        @Override
        protected void addNotify() {
            super.addNotify();
            setKeys(getKeys());
        }

        @Override
        protected void removeNotify() {
            super.removeNotify();
            setKeys(Collections.emptySet());
        }
    }
    
    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set ps = Sheet.createPropertiesSet();
        for (Column column : columns) {
            ps.put(column.getPropertyFor(fc));
        }
        sheet.put(ps);
        return sheet;
    }

    public static class RootNode extends FunctionNode {
        private final FunctionContainer[] functions;

        public RootNode(FunctionContainer[] functions,Column[] columns, FunctionNodeModel nodeModel) {
            super(columns, nodeModel);
            this.functions = functions;
            setDisplayName("Name");
            if (!getKeys().isEmpty()) {
                setChildren(new MyChildren());
            }
        }

        @Override
        protected Collection getKeys() {
            if (functions != null) {
                return Arrays.asList(functions);
            } else {
                return Collections.emptyList();
            }
        }
    }
}

package org.netbeans.modules.workflow.editor.dataloader;

import org.openide.loaders.DataNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

public class WorkflowDataNode extends DataNode {

    private static final String IMAGE_ICON_BASE = "org/netbeans/modules/workflow/editor/resources/images/workflowx16.png";

    public WorkflowDataNode(WorkflowDataObject obj) {
        super(obj, Children.LEAF);
        setIconBaseWithExtension(IMAGE_ICON_BASE);
    }

    WorkflowDataNode(WorkflowDataObject obj, Lookup lookup) {
        super(obj, Children.LEAF, lookup);
        setIconBaseWithExtension(IMAGE_ICON_BASE);
    }

    //    /** Creates a property sheet. */
//    protected @Override Sheet createSheet() {
//        Sheet s = super.createSheet();
//        Sheet.Set ss = s.get(Sheet.PROPERTIES);
//        if (ss == null) {
//            ss = Sheet.createPropertiesSet();
//            s.put(ss);
//        }
//        // TODO add some relevant properties: ss.put(...)
//        return s;
//    }
}

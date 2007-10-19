package org.netbeans.modules.workflow.editor.dataloader;

import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.SimpleBeanInfo;
import org.openide.loaders.UniFileLoader;
import org.openide.util.Utilities;

public class WorkflowDataLoaderBeanInfo extends SimpleBeanInfo {

    public @Override
    BeanInfo[] getAdditionalBeanInfo() {
        try {
            return new BeanInfo[]{Introspector.getBeanInfo(UniFileLoader.class)};
        } catch ( IntrospectionException e) {
            throw new AssertionError(e);
        }
    }

    public @Override
    Image getIcon( int type) {
        if (type == BeanInfo.ICON_COLOR_16x16 || type == BeanInfo.ICON_MONO_16x16) {
            return Utilities.loadImage("org/netbeans/modules/workflow/editor/resources/images/workflowx16.png");
        } else {
            return null;
        }

    }
}

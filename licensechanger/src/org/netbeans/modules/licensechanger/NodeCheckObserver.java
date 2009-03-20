package org.netbeans.modules.licensechanger;

import org.openide.nodes.Node;

/**
 *
 * @author Tim Boudreau
 */
public interface NodeCheckObserver {
    public void onNodeChecked (Node node);
    public void onNodeUnchecked (Node node);
}

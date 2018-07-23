package org.netbeans.modules.graphicclassview;

import java.util.Comparator;
final class SceneElementComparator implements Comparator<SceneElement> {

    public SceneElementComparator(boolean inbound) {
        this.inbound = inbound;
    }

    static SceneElementComparator create(boolean inbound) {
        return new SceneElementComparator(inbound);
    }

    public int compare(SceneElement o1, SceneElement o2) {
        return refsFor(o1) - refsFor(o2);
    }

    private int refsFor(SceneElement s) {
        return inbound ? s.getInboundReferences(SceneElement.class).size() : s.getOutboundReferences(SceneElement.class).size();
    }
    private final boolean inbound;
}

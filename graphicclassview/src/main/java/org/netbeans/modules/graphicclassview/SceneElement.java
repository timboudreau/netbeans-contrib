package org.netbeans.modules.graphicclassview;

import java.awt.Image;
import java.util.*;
import org.netbeans.api.java.source.TreePathHandle;

public final class SceneElement {

    public SceneElement(SceneObjectKind kind, TreePathHandle handle, String body, String name, String type) {
        this.handle = handle;
        this.body = body;
        this.name = name;
        this.kind = kind;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getBody() {
        return body;
    }

    public TreePathHandle getHandle() {
        return handle;
    }

    public void setImage(Image img) {
        this.img = img;
    }

    public Image getImage() {
        return img;
    }

    @SuppressWarnings("unchecked")
    public <T>Set<T> getOutboundReferences(Class<T> type) {
        Set <T> set = (Set<T>) outbound.get(type);
        if (set == null) {
            set = new HashSet<T>();
            outbound.put(type, set);
        }
        return set;
    }

    @SuppressWarnings("unchecked")
    public <T>Set<T> getInboundReferences(Class<T> type) {
        Set<T> set = (Set<T>) inbound.get(type);
        if (set == null) {
            set = new HashSet();
            inbound.put(type, set);
        }
        return set;
    }

    @SuppressWarnings("unchecked")
    private void addOutboundReference(Class type, SceneElement t) {
        if (t == this) {
            return;
        } else {
            getOutboundReferences(type).add(t);
            return;
        }
    }

    @SuppressWarnings("unchecked")
    private void addInboundReference(Class type, SceneElement t) {
        if (t == this) {
            return;
        } else {
            getInboundReferences(type).add(t);
            return;
        }
    }

    public void addOutboundReference(SceneElement item) {
        addOutboundReference(item.getClass(), item);
    }

    public void addInboundReference(SceneElement item) {
        addInboundReference(item.getClass(), item);
    }

    public SceneObjectKind getKind() {
        return kind;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SceneElement other = (SceneElement) obj;
        return body == other.body || body != null && body.equals(other.body);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (body == null ? 0 : body.hashCode());
        return hash;
    }
    private final TreePathHandle handle;
    private final String body;
    private final String name;
    private final Map<Class, Set> inbound = new HashMap<Class, Set> ();
    private final Map<Class, Set> outbound = new HashMap<Class, Set> ();
    private final SceneObjectKind kind;
    private final String type;
    private Image img;
}

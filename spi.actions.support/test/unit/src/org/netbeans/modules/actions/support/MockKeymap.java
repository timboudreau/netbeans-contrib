package org.netbeans.modules.actions.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.text.Keymap;
import junit.framework.Assert;



@org.openide.util.lookup.ServiceProvider(service=javax.swing.text.Keymap.class)
public final class MockKeymap implements Keymap {
    private Map<KeyStroke, Action> actions = new HashMap<KeyStroke, Action>();
    
    public String getName() {
        Assert.fail("Not implemented");
        return null;
    }

    public Action getDefaultAction() {
        Assert.fail("Not implemented");
        return null;
    }

    public void setDefaultAction(Action a) {
        Assert.fail("Not implemented");
    }

    public Action getAction(KeyStroke key) {
        return actions.get(key);
    }

    public KeyStroke[] getBoundKeyStrokes() {
        Assert.fail("Not implemented");
        return null;
    }

    public Action[] getBoundActions() {
        Assert.fail("Not implemented");
        return null;
    }

    public KeyStroke[] getKeyStrokesForAction(Action a) {
        List<KeyStroke> arr = new ArrayList<KeyStroke>();
        for (Map.Entry<KeyStroke,Action> entry : actions.entrySet()) {
            if (entry.getValue().equals(a)) {
                arr.add(entry.getKey());
            }
        }
        return arr.toArray(new KeyStroke[0]);
    }

    public boolean isLocallyDefined(KeyStroke key) {
        Assert.fail("Not implemented");
        return false;
    }

    public void addActionForKeyStroke(KeyStroke key, Action a) {
        actions.put(key, a);
    }

    public void removeKeyStrokeBinding(KeyStroke keys) {
        Assert.fail("Not implemented");
    }

    public void removeBindings() {
        Assert.fail("Not implemented");
    }

    public Keymap getResolveParent() {
        Assert.fail("Not implemented");
        return null;
    }

    public void setResolveParent(Keymap parent) {
        Assert.fail("Not implemented");
    }
}
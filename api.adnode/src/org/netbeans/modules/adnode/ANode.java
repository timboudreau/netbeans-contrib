/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.adnode;

import java.awt.Component;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import org.netbeans.api.adaptable.Adaptable;
import org.netbeans.api.adaptable.AdaptableEvent;
import org.netbeans.api.adaptable.AdaptableListener;
import org.netbeans.api.adaptable.Adaptor;
import org.netbeans.api.adaptable.info.*;
import org.netbeans.api.adnode.*;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;
import org.openide.util.datatransfer.PasteType;

/**
 *
 * @author Jaroslav Tulach
 */
final class ANode extends org.openide.nodes.Node 
implements AdaptableListener {
    private Adaptable a;
    private Adaptor adaptor;
    
    /** Creates a new instance of ANode */
    public ANode(Adaptable a, Adaptor adaptor) {
        super(computeChildren(a, adaptor, null));
        
        this.a = a;
        this.adaptor = adaptor;
        a.addAdaptableListener(WeakListeners.create(AdaptableListener.class, this, a));
    }

    private static Children computeChildren(Adaptable a, Adaptor adaptor, Children previous) {
        SubHierarchy h = a.lookup(SubHierarchy.class);
        if (h == null) {
            return Children.LEAF;
        }
        if (previous instanceof MCh) {
            MCh mch = (MCh)previous;
            mch.hierarchy(h);
            return mch;
        }

        MCh mch = new MCh(h, adaptor);
        return mch;
    }

    public String getName() {
        Identity n = a.lookup(Identity.class);
        return n == null ? "" : n.getId();
    }

    public void stateChanged(AdaptableEvent e) {
        Set<Class> affected = e.getAffectedClasses();

        if (affected.contains(SubHierarchy.class)) {
            Children ch = computeChildren(a, adaptor, getChildren());
            if (ch != getChildren()) {
                setChildren(ch);
            }
        }

        if (affected.contains(SetOfProperties.class)) {
            firePropertySetsChange(null, null);
        }

        if (affected.contains(Identity.class)) {
            fireNameChange(null, null);
        }

        if (affected.contains(DisplayName.class)) {
            fireDisplayNameChange(null, null);
        }
        if (affected.contains(ShortDescription.class)) {
            fireShortDescriptionChange(null, null);
        }
    }

    public Node cloneNode() {
        return new ANode(a, adaptor);
    }

    public Image getIcon(int type) {
        throw new UnsupportedOperationException();
    }

    public Image getOpenedIcon(int type) {
        throw new UnsupportedOperationException();
    }

    public HelpCtx getHelpCtx() {
        HelpCtx.Provider p = a.lookup(HelpCtx.Provider.class);
        return p == null ? null : p.getHelpCtx();
    }

    public boolean canRename() {
        return a.lookup(Rename.class) != null;
    }

    public boolean canDestroy() {
        throw new UnsupportedOperationException();
    }

    public Node.PropertySet[] getPropertySets() {
        SetOfProperties p = a.lookup(SetOfProperties.class);
        return p == null ? new Node.PropertySet[0] : p.getPropertySets();
    }

    public Transferable clipboardCopy() throws IOException {
        Copy c = a.lookup(Copy.class);
        if (c == null) {
            throw new IOException();
        }
        return c.copy();
    }

    public Transferable clipboardCut() throws IOException {
        Cut c = a.lookup(Cut.class);
        if (c == null) {
            throw new IOException();
        }
        return c.cut();
    }

    public Transferable drag() throws IOException {
        Drag d = a.lookup(Drag.class);
        if (d == null) {
            throw new IOException();
        }
        return d.drag();
    }

    public boolean canCopy() {
        return a.lookup(Copy.class) != null;
    }

    public boolean canCut() {
        return a.lookup(Cut.class) != null;
    }

    public PasteType[] getPasteTypes(Transferable t) {
        PasteTypes p = a.lookup(PasteTypes.class);
        return p == null ? new PasteType[0] : p.getPasteTypes(t);
    }

    public PasteType getDropType(Transferable t, int action, int index) {
        Drop n = a.lookup(Drop.class);
        return n == null ? null : n.getDropType(t, action, index);
    }

    public NewType[] getNewTypes() {
        NewTypes n = a.lookup(NewTypes.class);
        return n == null ? new NewType[0] : n.getNewTypes();
    }

    public boolean hasCustomizer() {
        return a.lookup(Customizable.class) != null;
    }

    public Component getCustomizer() {
        Customizable c = a.lookup(Customizable.class);
        return c == null ? null : c.getCustomizer();
    }

    public Node.Handle getHandle() {
        throw new UnsupportedOperationException();
    }

    public void setName(final String s) {
        class IAE extends IllegalArgumentException {
            public IAE(Throwable cause) {
                if (cause != null) {
                    initCause(cause);
                }
            }

            public String getMessage() {
                return "There is no rename adaptor"; // NOI18N
            }

            public String getLocalizedMessage() {
                if (getCause() != null) {
                    return getCause().getLocalizedMessage();
                }

                return NbBundle.getMessage(ANode.class, "EXC_NoRenameAdaptor", s); // NOI18N
            }
        }

        Rename rename = a.lookup(Rename.class);
        if (rename == null) {
            throw new IAE(null);
        }

        try {
            rename.rename(s);
        } catch (Exception ex) {
            throw new IAE(ex);
        }
    }

    public void setDisplayName(String s) {
        throw new UnsupportedOperationException();
    }

    public void setShortDescription(String s) {
        throw new UnsupportedOperationException();
    }

    public Node.Cookie getCookie(Class type) {
        throw new UnsupportedOperationException();
    }

    public Action[] getActions(boolean context) {
        ActionProvider p = a.lookup(ActionProvider.class);
        return p == null ? new Action[0]: p.getActions();
    }

    public String getDisplayName() {
        DisplayName n = a.lookup(DisplayName.class);
        return n == null ? getName() : n.getDisplayName();
    }

    public String getShortDescription() {
        ShortDescription d = a.lookup(ShortDescription.class);
        return d == null ? getDisplayName() : d.getShortDescription();
    }

    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(this)) + "[" + a + "]";
    }

    public void destroy() throws IOException {
        throw new UnsupportedOperationException();
    }

    public String getHtmlDisplayName() {
        HtmlDisplayName d = a.lookup(HtmlDisplayName.class);
        return d == null ? null : d.getHtmlDisplayName();
    }

    public Action getPreferredAction() {
        ActionProvider p = a.lookup(ActionProvider.class);
        return p == null ? null : p.getPreferredAction();
    }

    public Object getValue(String attributeName) {
        return null;
    }

    public void setValue(String attributeName, Object value) {
        throw new UnsupportedOperationException();
    }

    public void setPreferred(boolean preferred) {
        throw new UnsupportedOperationException();
    }

    public void setHidden(boolean hidden) {
        throw new UnsupportedOperationException();
    }

    public void setExpert(boolean expert) {
        throw new UnsupportedOperationException();
    }

    public boolean isPreferred() {
        return false;
    }

    public boolean isHidden() {
        return false;
    }

    public boolean isExpert() {
        return false;
    }

    public Enumeration<String> attributeNames() {
        return Collections.enumeration(Collections.<String>emptyList());
    }

    public int hashCode() {
        return a.hashCode() + 1243;
    }

    public boolean equals(Object o) {
        if (o instanceof ANode) {
            return a.equals(((ANode)o).a);
        }
        return false;
    }

    /** Implementation of keys over SubHierarchy
     */
    private static final class MCh extends Children.Keys {
        private SubHierarchy h;
        private Adaptor adaptor;

        public MCh(SubHierarchy h, Adaptor adaptor) {
            this.h = h;
            this.adaptor = adaptor;
        }

        protected Node[] createNodes(Object key) {
            Adaptable a = adaptor.getAdaptable(key);
            return new Node[] { new ANode(a, adaptor) };
        }

        protected void addNotify() {
            setKeys(h.getChildren());
        }

        protected void removeNotify() {
            setKeys(Collections.emptyList());
        }

        private void hierarchy(SubHierarchy h) {
            this.h = h;
            addNotify();
        }


    }
}

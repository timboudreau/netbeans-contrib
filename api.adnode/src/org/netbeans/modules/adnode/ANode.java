/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.adnode;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import javax.swing.Action;
import javax.swing.Icon;
import org.netbeans.api.adaptable.Adaptable;
import org.netbeans.api.adaptable.AdaptableEvent;
import org.netbeans.api.adaptable.AdaptableListener;
import org.netbeans.api.adaptable.Adaptor;
import org.netbeans.api.adaptable.Facets.*;
import org.netbeans.api.adnode.NodeFacets.Customizable;
import org.netbeans.api.adnode.NodeFacets.Drop;
import org.netbeans.api.adnode.NodeFacets.NewTypes;
import org.netbeans.api.adnode.NodeFacets.PasteTypes;
import org.netbeans.api.adnode.NodeFacets.SetOfProperties;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
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
        Icon icon = a.lookup(Icon.class);
        if (icon == null) {
            return null;
        }
        Component c = new Canvas();
        Image img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        icon.paintIcon(c, img.getGraphics(), 0, 0);
        return img;
    }

    public Image getOpenedIcon(int type) {
        Icon icon = a.lookup(Icon.class);
        if (icon == null) {
            return null;
        }
        Component c = new Container();
        Image img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        icon.paintIcon(c, img.getGraphics(), 0, 0);
        return img;
    }

    public HelpCtx getHelpCtx() {
        HelpCtx.Provider p = a.lookup(HelpCtx.Provider.class);
        return p == null ? null : p.getHelpCtx();
    }

    public boolean canRename() {
        return a.lookup(Rename.class) != null;
    }

    public boolean canDestroy() {
        return a.lookup(Delete.class) != null;
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
        class IAE extends IOException {
            public IAE(Throwable cause) {
                if (cause != null) {
                    initCause(cause);
                }
            }

            public String getMessage() {
                return "There is no destroy adaptor"; // NOI18N
            }

            public String getLocalizedMessage() {
                if (getCause() != null) {
                    return getCause().getLocalizedMessage();
                }

                return NbBundle.getMessage(ANode.class, "EXC_NoDestroyAdaptor"); // NOI18N
            }
        }

        Delete delete = a.lookup(Delete.class);
        if (delete == null) {
            throw new IAE(null);
        }

        try {
            delete.delete();
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IAE(ex);
        }
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
            if (isInitialized()) {
                addNotify();
            }
        }


    }
}

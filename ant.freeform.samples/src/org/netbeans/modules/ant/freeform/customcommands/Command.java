/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.ant.freeform.customcommands;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import org.netbeans.spi.project.ui.support.FileSensitiveActions;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.InstanceDataObject;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.actions.Presenter;

/**
 * Persistent version of {@link FileSensitiveActions#fileCommandAction}.
 * @author Jesse Glick
 */
public final class Command implements Serializable, Action, Presenter.Menu, Presenter.Toolbar, Presenter.Popup, ContextAwareAction {
    
    private static final long serialVersionUID = 1L;
    
    private final String command;
    private final String namePattern;
    private final URL icon;
    private transient Action delegate;
    
    Command(String command, String namePattern, URL icon) {
        this.command = command;
        this.namePattern = namePattern;
        this.icon = icon;
    }
    
    private Action getDelegate() {
        if (delegate == null) {
            delegate = FileSensitiveActions.fileCommandAction(command, namePattern, icon != null ? new ImageIcon(icon) : null);
        }
        return delegate;
    }
    
    private static DataFolder folder(String path) {
        FileObject f = Repository.getDefault().getDefaultFileSystem().findResource(path);
        return f != null ? DataFolder.findFolder(f) : null;
    }

    public void create(DataFolder menuFolder, int position) throws IOException {
        DataFolder ap = folder("Actions/Project"); // NOI18N
        if (ap == null) {
            ap = folder("Actions"); // NOI18N
            assert ap != null;
        }
        DataObject instance = InstanceDataObject.create(ap, command, this, null, true);
        String menuName = menuFolder.getName();
        if (menuName.endsWith("Project")) { // NOI18N
            menuName = menuName.substring(0, menuName.length() - "Project".length()); // NOI18N
        }
        DataFolder oda = folder("OptionsDialog/Actions/" + menuName); // NOI18N
        if (oda == null) {
            oda = folder("OptionsDialog/Actions/Build"); // NOI18N
        }
        if (oda != null) {
            instance.createShadow(oda);
        }
        DataObject nue = instance.createShadow(menuFolder);
        List children = new ArrayList(Arrays.asList(menuFolder.getChildren()));
        assert children.contains(nue);
        children.remove(nue);
        children.add(position, nue);
        menuFolder.setOrder((DataObject[]) children.toArray(new DataObject[children.size()]));
    }

    public Object getValue(String key) {
        return getDelegate().getValue(key);
    }

    public void putValue(String key, Object value) {
        getDelegate().putValue(key, value);
    }

    public void setEnabled(boolean b) {
        getDelegate().setEnabled(b);
    }

    public boolean isEnabled() {
        return getDelegate().isEnabled();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        getDelegate().addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        getDelegate().removePropertyChangeListener(listener);
    }

    public void actionPerformed(ActionEvent e) {
        getDelegate().actionPerformed(e);
    }

    public JMenuItem getMenuPresenter() {
        if (getDelegate() instanceof Presenter.Menu) {
            return ((Presenter.Menu) getDelegate()).getMenuPresenter();
        } else {
            return new JMenuItem(this);
        }
    }

    public Component getToolbarPresenter() {
        if (getDelegate() instanceof Presenter.Toolbar) {
            return ((Presenter.Toolbar) getDelegate()).getToolbarPresenter();
        } else {
            return new JButton(this);
        }
    }

    public JMenuItem getPopupPresenter() {
        if (getDelegate() instanceof Presenter.Popup) {
            return ((Presenter.Popup) getDelegate()).getPopupPresenter();
        } else {
            return new JMenuItem(this);
        }
    }
    
    public Action createContextAwareInstance(Lookup actionContext) {
        if (getDelegate() instanceof ContextAwareAction) {
            return ((ContextAwareAction) getDelegate()).createContextAwareInstance(actionContext);
        } else {
            return this;
        }
    }

}

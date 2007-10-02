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
 * Software is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
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
package org.netbeans.modules.bookmarks;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Externalizable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import org.openide.windows.TopComponent;
import org.openide.util.Utilities;
import org.openide.util.NbBundle;
import org.openide.NotifyDescriptor;
import org.openide.DialogDisplayer;

import org.netbeans.api.bookmarks.*;
import org.netbeans.spi.convertor.SimplyConvertible;

/**
 * The default bookmark implementation is created for a TopComponent
 * in order for the user
 * to be able to open the TopComponent with saved content. The bookmark
 * is saved to the system file system by default together with its
 * TopComponent. When the user "invokes" the bookmark the associated
 * TopComponent is opened. In the defualt implementation if the TopComponent
 * is TopComponent.Cloneable a new copy of the TopComponent is opened
 * with each invokation. 
 * <P> When used with NetBeans 3.5
 * the persitence of the Bookmark
 * class is achieved using core/settings module properties convertor - 
 * see methods readProperties, writeProperties.
 * @author David Strupl
 */
public class BookmarkImpl extends AbstractAction
                implements Bookmark, Externalizable, SimplyConvertible, Cloneable {
    
    static final long serialVersionUID = 1L;
    
    /** Name of the property used from readProperties, writeProperties */
    private static final String PROP_TC_NAME = "topComponentName";
    
    /** Name of the property used from readProperties, writeProperties */
    private static final String PROP_NAME = "name";
    
    /** Reference to the TopComponent for which this bookmark is created */
    private TopComponent topComponent;
    
    /** Name of the file where the TopComponent was persisted */
    private String tcFileName;
    
    /** Name of the bookmark */
    private String name;
    
    /** Chache our menu item */
    private JMenuItem menuItem;
    
    /** Default constructor used by the persistence mechanism.
     * This constructor does not fill vital variables, so its
     * usage must be followed by at least calling
     * setTopComponentFileName for the bookmark to be able to
     * create its TopComponent. Or the getTopComponent method
     * has to be overriden not to need the file name.
     */
    public BookmarkImpl() {
    }
    
    /** Creates the bookmark with the associated TopComponent.
     * If you use this constructor the bookmark is able to be
     * saved after the call to setTopComponentFileName.
     */
    public BookmarkImpl(TopComponent tc) {
        this.topComponent = tc;
        name = tc.getDisplayName();
        if (name == null) {
            name = tc.getName();
        }
        putValue(NAME, name);
    }
    
    /**
     * Method implementing interface Presenter.Menu. The default
     * implementation of the menu item uses name and icon from
     * the associated top component.
     */
    public JMenuItem getMenuPresenter() {
        if (menuItem != null) {
            return menuItem;
        }
        // Mantis 242 
        String mName = getName();
        if ((mName != null) && (mName.length() > 50)) {
            mName = mName.substring(0, 49);
        }
        // ----------
        menuItem = new JMenuItem(mName);
        menuItem.addActionListener(new InvokeBookmarkListener());
        TopComponent tc = getTopComponent();
        if (tc != null) {
            Image icon = tc.getIcon();
            if (icon != null) {
                menuItem.setIcon(new ImageIcon(icon));
            }
        }
        return menuItem;
    }
    
    /** Method implementing interface Presenter.Toolbar. The
     * default implementation uses the name and icon from
     * the associated top component.
     */
    public java.awt.Component getToolbarPresenter() {
        JButton jb = new JButton();
        jb.setToolTipText(getName());
        jb.addActionListener(new InvokeBookmarkListener());
        TopComponent tc = getTopComponent();
        Image icon = null;
        if (tc != null) {
            icon = tc.getIcon();
            if (icon != null) {
                jb.setIcon(new ImageIcon(icon));
            }
        }
        if ((icon == null) || (icon.getHeight(null) < 16)) {
            jb.setIcon(new ImageIcon(Utilities.loadImage("org/netbeans/modules/bookmarks/resources/bookmarksToolbarIcon.gif")));
        }
        return jb;
    }

    /**
     * ActionListener for the menu and toolbar presenters.
     * Simply calls invokeBookmark.
     */
    private class InvokeBookmarkListener implements ActionListener {
        public InvokeBookmarkListener() {}
        public void actionPerformed(ActionEvent ae) {
            invoke();
        }
    }
    
    /** Name of the bookmark presented to the user. The default
     * implementation calls TopComponent.getName().
     * @returns name of the bookmark
     */
    public String getName() {
        return name;
    }
    
    public void setName(final String newName) {
        String oldValue = this.name;
        this.name = newName;
        firePropertyChange(PROP_NAME, oldValue, newName);
        if (menuItem != null) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    menuItem.setText(newName);
                }
            });
        }
    }
    
    /**
     * File name (without extension) for saving the bookmark and
     * also for its top component. This might be different from
     * regular name since there might be more bookmarks created
     * for the same top component.
     */
    public void setTopComponentFileName(String tcFileName) {
        this.tcFileName = tcFileName;
    }
    
    /**
     * File name (without extension) for saving the bookmark and
     * also for its top component. This might be different from
     * regular name since there might be more bookmarks created
     * for the same top component.
     */
    public String getTopComponentFileName() {
        return tcFileName;
    }

    /** 
     * Returns the associated top component. The default implementation
     * first checks whether it is able to restore the persisted
     * top component by calling into BookmarkService.loadTopComponent and
     * then tries to clone the result. The effect of this is that if you
     * open the resulting top component a fresh copy with the saved
     * content is opened.
     * @returns top component for which this bookmark was taken or null
     */
    public TopComponent getTopComponent() {
        if (tcFileName != null) {
            topComponent = ((BookmarkServiceImpl)BookmarkService.getDefault()).
                loadTopComponent(tcFileName);
        } 
        if (topComponent instanceof TopComponent.Cloneable) {
            try {
                topComponent = ((TopComponent.Cloneable)topComponent).cloneComponent();
            } catch (Exception x) {
                Logger log = Logger.getLogger(BookmarkImpl.class.getName());
                log.log(Level.FINE, "Problem when trying to clone " + topComponent, x);
            }
        }
        return topComponent;
    }
    
    /**
     * Main action method called when the user selects the bookmark. The
     * default implementation tries to open the top component returned
     * from getTopComponent().<p> This method is called after the user
     * selects the bookmark from the menu or toolbar. So calling this method
     * is contained in the default implementation of the menu and toolbar
     * presenters for the bookmark.
     */
    public void invoke() {
        TopComponent tc = getTopComponent();
        if (tc != null) {
            if (! tc.isOpened()) {
                tc.open();
            }
            
            tc.requestActive();
        } else {
            // warn
            NotifyDescriptor.Message warning = 
                new NotifyDescriptor.Message(
                    NbBundle.getBundle(BookmarkImpl.class).getString("WARN_NO_TC"),
                    NotifyDescriptor.WARNING_MESSAGE);
            DialogDisplayer.getDefault().notify(warning);
        }
    }
    
    /**
     * Implementing the javax.swing.Action interface.
     */
    public void actionPerformed(ActionEvent e) {
        invoke();
    }
    
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        super.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /**
     * Based on name and tcFileName.
     */
    public boolean equals(Object another) {
        boolean res = super.equals(another);
        if ( ! (another instanceof BookmarkImpl)) {
            return false;
        }
        BookmarkImpl theOther = (BookmarkImpl)another;
        if (name != null) {
            if (! name.equals(theOther.getName())) {
                return false;
            }
        } else {
            if (theOther.getName() != null) {
                return false;
            }
        }
        if (tcFileName != null) {
            if (! tcFileName.equals(theOther.getTopComponentFileName())) {
                return false;
            }
        } else {
            if (theOther.getTopComponentFileName() != null) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Combine the hascode from name and tcFileName.
     */
    public int hashCode() {
        int res = 29;
        if (name != null) {
            res += name.hashCode() * 13;
        }
        if (tcFileName != null) {
            res += tcFileName.hashCode() * 7;
        }
        return res;
    }
    
    /**
     * Tries to fullfill the contract of Object.toString().
     * @returns informative string representation of this object.
     */
    public String toString() {
        return "Bookmark [name==" + getName() + ", tcFileName==" + tcFileName + "]";
    }
    
    // readProperties/writeProperties called by XMLPropertiesConvertor
    
    /**
     * Called by XMLPropertiesConvertor to restore the state
     * of this object after calling the default constructor.
     *
     * It can be private = XMLPropertiesConvertor from core/settings
     * module is able to invoke it anyway.
     */
    void readProperties(java.util.Properties p) {
        tcFileName = p.getProperty(PROP_TC_NAME);
        name = p.getProperty(PROP_NAME);
        putValue(NAME, name);
    }
    
    /**
     * XMLPropertiesConvertor calls this method when it wants
     * to persist this object.
     *
     * It can be private = XMLPropertiesConvertor from core/settings
     * module is able to invoke it anyway.
     */
    void writeProperties(java.util.Properties p) {
        p.setProperty(PROP_TC_NAME, tcFileName);
        p.setProperty(PROP_NAME, name);
    }
    
    /**
     * Serialization method - overriden to make sure this
     * class is not serialized.
     */
    public void readExternal(java.io.ObjectInput in) throws IOException, ClassNotFoundException {
        throw new IOException("This class should not be serialized."); // NOI18N
    }
    
    /**
     * Serialization method - overriden to make sure this
     * class is not serialized.
     */
    public void writeExternal(java.io.ObjectOutput out) throws IOException {
        throw new IOException("This class should not be serialized."); // NOI18N
    }
    
    /**
     * Implementing interface SimplyConvertible.
     */
    public void read(java.util.Properties p) {
        readProperties(p);
    }
    
    /**
     * Implementing interface SimplyConvertible.
     */
    public void write(java.util.Properties p) {
        writeProperties(p);
    }
    
    public Object clone() throws CloneNotSupportedException {
        BookmarkImpl res = new BookmarkImpl();
        res.topComponent = topComponent;
        res.tcFileName = tcFileName;
        res.name = name;
        return res;
    }
}

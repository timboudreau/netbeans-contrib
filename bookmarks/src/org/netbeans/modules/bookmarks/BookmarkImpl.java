/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.bookmarks;

import java.beans.PropertyChangeListener;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.AbstractAction;

import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.windows.Mode;
import org.openide.windows.Workspace;
import org.openide.util.actions.Presenter;
import org.openide.util.Utilities;
import org.openide.util.NbBundle;
import org.openide.NotifyDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;

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
public class BookmarkImpl extends AbstractAction implements Bookmark, SimplyConvertible {
    
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
        name = tc.getName();
        putValue(NAME, name);
    }
    
    /**
     * Method implementing interface Presenter.Menu. The default
     * implementation of the menu item uses name and icon from
     * the associated top component.
     */
    public JMenuItem getMenuPresenter() {
        JMenuItem jmi = new JMenuItem(getName());
        jmi.addActionListener(new InvokeBookmarkListener());
        TopComponent tc = getTopComponent();
        if (tc != null) {
            Image icon = tc.getIcon();
            if (icon != null) {
                jmi.setIcon(new ImageIcon(icon));
            }
        }
        return jmi;
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
                ErrorManager.getDefault().annotate(x, "Problem when trying to clone " + topComponent); // NOI18N
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, x);
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
            tc.requestFocus();
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
     */
    public void read(java.util.Properties p) {
        tcFileName = p.getProperty(PROP_TC_NAME);
        name = p.getProperty(PROP_NAME);
        putValue(NAME, name);
    }
    
    /**
     * XMLPropertiesConvertor calls this method when it wants
     * to persist this object.
     */
    public void write(java.util.Properties p) {
        p.setProperty(PROP_TC_NAME, tcFileName);
        p.setProperty(PROP_NAME, name);
    }
    
}

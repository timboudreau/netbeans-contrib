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

import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.windows.Mode;
import org.openide.windows.Workspace;
import org.openide.util.actions.Presenter;
import org.openide.util.Utilities;

import org.netbeans.api.bookmarks.*;

/**
 * The default bookmakr implementatoin is craeated for a TopComponent
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
public class BookmarkImpl implements Bookmark {
    
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
    }
    
    /**
     * Method implementing interface Presenter.Menu. The default
     * implementation of the menu item uses name and icon from
     * the associated top component.
     */
    public JMenuItem getMenuPresenter() {
        JMenuItem jmi = new JMenuItem(getName());
        jmi.addActionListener(new InvokeBookmarkListener());
        Image icon = getTopComponent().getIcon();
        if (icon != null) {
            jmi.setIcon(new ImageIcon(icon));
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
        Image icon = getTopComponent().getIcon();
        if (icon != null) {
            jb.setIcon(new ImageIcon(icon));
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
     * @returns top component for which this bookmark was taken
     */
    public TopComponent getTopComponent() {
        if (tcFileName != null) {
            topComponent = ((BookmarkServiceImpl)BookmarkService.getDefault()).
                loadTopComponent(tcFileName);
        } 
        if (topComponent instanceof TopComponent.Cloneable) {
            topComponent = ((TopComponent.Cloneable)topComponent).cloneComponent();
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
        }
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
    private void readProperties(java.util.Properties p) {
        tcFileName = p.getProperty(PROP_TC_NAME);
        name = p.getProperty(PROP_NAME);
    }
    
    /**
     * XMLPropertiesConvertor calls this method when it wants
     * to persist this object.
     */
    private void writeProperties(java.util.Properties p) {
        p.setProperty(PROP_TC_NAME, tcFileName);
        p.setProperty(PROP_NAME, name);
    }
    
    /**
     * XMLPropertiesConvertor expect this class to be source of
     * property change events.
     */
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
    }
    /**
     * XMLPropertiesConvertor expect this class to be source of
     * property change events.
     */
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
    }
}

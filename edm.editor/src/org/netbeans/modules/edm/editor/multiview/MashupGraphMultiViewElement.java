/*
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License (the License). You may not use this
 * file except in compliance with the License.  You can obtain a copy of the
 *  License at http://www.netbeans.org/cddl.html
 *
 * When distributing Covered Code, include this CDDL Header Notice in each
 * file and include the License. If applicable, add the following below the
 * CDDL Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2006 Sun Microsystems, Inc. All Rights Reserved
 *
 */

package org.netbeans.modules.edm.editor.multiview;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import javax.swing.JScrollPane;

import org.openide.cookies.SaveCookie;
import org.openide.windows.TopComponent;
import org.openide.windows.TopComponentGroup;
import org.openide.windows.WindowManager;
import org.openide.windows.Mode;
import org.openide.util.Lookup;
import org.openide.nodes.Node;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.util.lookup.AbstractLookup;

import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.netbeans.core.api.multiview.MultiViewPerspective;
import org.netbeans.core.api.multiview.MultiViews;
import org.netbeans.core.api.multiview.MultiViewHandler;

import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;
import org.netbeans.modules.edm.editor.dataobject.MashupDataEditorSupport;
import org.netbeans.modules.edm.editor.graph.MashupGraphManager;
import org.netbeans.modules.edm.editor.graph.components.EDMNavigatorHint;
import org.netbeans.modules.edm.editor.graph.components.MashupToolbar;
import org.netbeans.modules.edm.editor.palette.PaletteSupport;

/**
 *
 * @author Jeri Lockhart
 */
public class MashupGraphMultiViewElement extends TopComponent
        implements MultiViewElement {
    
    /**
     *
     */
    private static final long serialVersionUID = -655912409997381426L;
    
    private static final String ACTIVATED_NODES = "activatedNodes";//NOI18N
    
    private MashupDataObject mObj = null;
    
    private transient InstanceContent nodesHack;
    
    private transient MultiViewElementCallback multiViewObserver;
    private transient javax.swing.JLabel errorLabel = new javax.swing.JLabel();
    
    private transient JToolBar mToolbar = null;
    
    private MashupGraphManager manager;
    
    public MashupGraphMultiViewElement() {
        super();
    }
    
    public MashupGraphMultiViewElement(MashupDataObject dObj) {
        this();
        this.mObj = dObj;
        initialize();
    }
    
    private void initialize() {
        setLayout(new BorderLayout());
        initializeLookup();        
        initUI();
    }
    
    private void initializeLookup() {
        associateLookup(createAssociateLookup());
        
        addPropertyChangeListener(new PropertyChangeListener() {
            /**
             * TODO: may not be needed at some point when parenting
             * MultiViewTopComponent delegates properly to its peer's
             * activatedNodes.
             *
             * see http://www.netbeans.org/issues/show_bug.cgi?id=67257
             *
             * note: TopComponent.setActivatedNodes is final
             */
            public void propertyChange(PropertyChangeEvent event) {
                if(event.getPropertyName().equals("activatedNodes")) {
                    nodesHack.set(Arrays.asList(getActivatedNodes()),null);
                }
            }
        });
        setActivatedNodes(new Node[] {getMashupDataObject().getNodeDelegate()});
    }
    
    private Lookup createAssociateLookup() {
        
        //
        // see http://www.netbeans.org/issues/show_bug.cgi?id=67257
        //
        nodesHack = new InstanceContent();
        return new ProxyLookup(new Lookup[] {
            //
            // other than nodesHack what else do we need in the associated
            // lookup?  I think that XmlNavigator needs DataObject
            //
            getMashupDataObject().getLookup(),
            Lookups.singleton(this),
            new AbstractLookup(nodesHack),
            Lookups.fixed(new Object[]{PaletteSupport.createPalette()}),
            Lookups.fixed(new Object[]{new EDMNavigatorHint()})
        });
    }
    
    public CloseOperationState canCloseElement() {
        if ((mObj != null) && (mObj.isModified())) {
            return MultiViewFactory.createUnsafeCloseState("Data object modified", null, null);
        }
        return CloseOperationState.STATE_OK;
    }
    
    private MashupDataObject getMashupDataObject() {
        return mObj;
    }
    
    /**
     * Overwrite when you want to change default persistence type. Default
     * persistence type is PERSISTENCE_ALWAYS.
     * Return value should be constant over a given TC's lifetime.
     *
     * @return one of P_X constants
     * @since 4.20
     */
    @Override
    public int getPersistenceType() {
        return PERSISTENCE_NEVER;
    }
    
    public void setMultiViewCallback(final MultiViewElementCallback callback) {
        multiViewObserver = callback;
    }
    
    @Override
    public void componentHidden() {
        super.componentHidden();
    }
    
    @Override
    public void componentClosed() {
        super.componentClosed();
    }
    
    @Override
    public void componentOpened() {
        super.componentOpened();
    }
    
    @Override
    public void componentActivated() {        
        super.componentActivated();  
        SaveCookie cookie = (SaveCookie) mObj.getCookie(SaveCookie.class);
        if(cookie != null) {
            getMashupDataObject().getMashupDataEditorSupport().synchDocument();
        }
        getMashupDataObject().createNodeDelegate();
    }
    
    @Override
    public void componentDeactivated() {
        SaveCookie cookie = (SaveCookie) mObj.getCookie(SaveCookie.class);
        if(cookie != null) {
            getMashupDataObject().getMashupDataEditorSupport().synchDocument();
        }
        super.componentDeactivated();
    }
    
    @Override
    public void componentShowing() {
        super.componentShowing();
    }
    
    private static Boolean groupVisible = null;
    
    private void updateGroupVisibility(boolean closeGroup) {
        WindowManager wm = WindowManager.getDefault();
        final TopComponentGroup group = wm.findTopComponentGroup("wsdl_ui"); // NOI18N
        if (group == null) {
            return;
        }
        boolean isAspectViewSelected = false;
        Iterator it = wm.getModes().iterator();
        while (it.hasNext()) {
            Mode mode = (Mode) it.next();
            TopComponent selected = mode.getSelectedTopComponent();
            if (selected != null) {
                MultiViewHandler mvh = MultiViews.findMultiViewHandler(selected);
                if (mvh != null) {
                    MultiViewPerspective mvp = mvh.getSelectedPerspective();
                    if (mvp != null) {
                        String id = mvp.preferredID();
                        if (MashupGraphMultiViewDesc.PREFERRED_ID.equals(id)) {
                            isAspectViewSelected = true;
                            break;
                        }
                    }
                }
            }
        }
        //
        if (isAspectViewSelected && !Boolean.TRUE.equals(groupVisible)) {
            group.open();
        } else if (!isAspectViewSelected && !Boolean.FALSE.equals(groupVisible)) {
            group.close();
        }
        //
        groupVisible = isAspectViewSelected ? Boolean.TRUE : Boolean.FALSE;
        
    }
    
    @Override
    protected String preferredID() {
        return "MashupGraphMultiViewElementTC";  //  NOI18N
    }
    
    /**
     * Construct the user interface.
     */
    private void initUI() {
        MashupDataEditorSupport editor = getMashupDataObject().getMashupDataEditorSupport();
        String errorMessage = null;
        try {
            removeAll();
            manager = getMashupDataObject().getGraphManager();
            setLayout(new BorderLayout());
            manager.refreshGraph();
            JScrollPane pane = manager.getPanel();
            add(pane, BorderLayout.CENTER);
            return;
        } catch (Exception ex) {
            errorMessage = ex.getMessage();
        }
        
        // Clear the interface and show the error message.
        removeAll();
        errorLabel.setText("<" + errorMessage + ">");
        errorLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        errorLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        errorLabel.setEnabled(false);
        Color usualWindowBkg = UIManager.getColor("window"); //NOI18N
        errorLabel.setBackground(usualWindowBkg != null ? usualWindowBkg :
            Color.white);
        errorLabel.setOpaque(true);
        add(errorLabel, BorderLayout.CENTER);
    }
    
    private void reload() {
        removeAll();
        manager = getMashupDataObject().getGraphManager();
        manager.refreshGraph();
        setLayout(new BorderLayout());
        add(manager.getPanel(), BorderLayout.CENTER);
        return;
    }
    
    public javax.swing.JComponent getToolbarRepresentation() {
        if (mToolbar == null) {
            try {
                mToolbar = new MashupToolbar(getMashupDataObject()).getToolBar();
                mToolbar.setFloatable(false);
            } catch (Exception e) {
                //wait until the model is loaded
            }
        }
        return mToolbar;
    }
    
    public javax.swing.JComponent getVisualRepresentation() {
        return this;
    }
    
    public TopComponent getComponent() {
        return this;
    }
}
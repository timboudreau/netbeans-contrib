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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.workflow.editor.multiview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.DefaultEditorKit;

import org.netbeans.core.api.multiview.MultiViewHandler;
import org.netbeans.core.api.multiview.MultiViewPerspective;
import org.netbeans.core.api.multiview.MultiViews;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.netbeans.core.spi.multiview.MultiViewFactory;
//import org.netbeans.modules.iep.editor.designer.ZoomManager;
import org.netbeans.modules.workflow.editor.dataloader.WorkflowDataObject;
import org.netbeans.modules.workflow.editor.palette.WorkflowPaletteFactory;
import org.netbeans.modules.workflow.editor.view.design.WorkflowDesignViewPanel;
import org.netbeans.modules.xml.xam.Component;
import org.netbeans.modules.xml.xam.spi.Validator.ResultItem;
import org.netbeans.modules.xml.xam.ui.multiview.ActivatedNodesMediator;
import org.netbeans.modules.xml.xam.ui.multiview.CookieProxyLookup;

import org.openide.actions.FindAction;
import org.openide.awt.UndoRedo;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallbackSystemAction;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.TopComponentGroup;
import org.openide.windows.WindowManager;
import org.netbeans.modules.xml.validation.ShowCookie;
import org.netbeans.modules.xml.validation.ValidateAction;
import org.openide.filesystems.FileUtil;

/**
 * @author radval
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class WorkflowDesignViewMultiViewElement extends TopComponent
        implements MultiViewElement, ExplorerManager.Provider {
    private static final long serialVersionUID = -655912409997381426L;
    private static final String ACTIVATED_NODES = "activatedNodes";//NOI18N
    private ExplorerManager manager;
    private WorkflowDataObject mObj;
    private transient MultiViewElementCallback multiViewObserver;
    private transient javax.swing.JLabel errorLabel = new javax.swing.JLabel();
    private transient JToolBar mToolbar;
    //private GraphView graphComponent;
    
    public WorkflowDesignViewMultiViewElement() {
        super();
    }

    public WorkflowDesignViewMultiViewElement(WorkflowDataObject dObj) {
        super();
        this.mObj = dObj;
        this.mObj.getWorkflowEditorSupport().setPlanDesignMultiviewElement(this);
        initialize();
    }

    private void initialize() {
        manager = new ExplorerManager();
        // Install our own actions.
        CallbackSystemAction globalFindAction = SystemAction.get(FindAction.class);
        Object mapKey = globalFindAction.getActionMapKey();
        Action action = new WSDLFindAction();
        ActionMap map = getActionMap();
        map.put(mapKey, action);
        map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(manager));
        map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(manager));
        map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(manager));
        map.put("delete", ExplorerUtils.actionDelete(manager, false));

        // Define the keyboard shortcuts for the actions.
        InputMap keys = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke key = (KeyStroke) globalFindAction.getValue(Action.ACCELERATOR_KEY);
        if (key == null) {
            key = KeyStroke.getKeyStroke("control F");
        }
        keys.put(key, mapKey);

        //show cookie
        ShowCookie showCookie = new ShowCookie() {
            public void show(ResultItem resultItem) {
                Component component = resultItem.getComponents();
                //graphComponent.showComponent(component);
            }
        };
        
        Node delegate = this.mObj.getNodeDelegate();
       
        CookieProxyLookup cpl = new CookieProxyLookup(new Lookup[] {
            Lookups.fixed(new Object[] {
                // Need the data object registered in the lookup so that the
                // projectui code will close our open editor windows when the
                // project is closed.
            	this.mObj,
            	showCookie,
                // Provides the PrintProvider for printing
                //new DesignViewPrintProvider(),
                // Component palette for the partner view.
            	WorkflowPaletteFactory.getPalette(),
                // This is unusal, not sure why this is here.
                this,
            }),
            // The Node delegate Lookup must be the last one in the list
            // for the CookieProxyLookup to work properly.
            delegate.getLookup(),
        }, delegate);
        
        associateLookup(cpl);
        addPropertyChangeListener(ACTIVATED_NODES, cpl);
        
        setLayout(new BorderLayout());
    }

    
    private void cleanup() {
        try {
            manager.setSelectedNodes(new Node[0]);
        } catch (PropertyVetoException e) {
        }
        
       if (mToolbar != null) mToolbar.removeAll();
        mToolbar = null;
        removeAll();
    }
    public ExplorerManager getExplorerManager() {
    	return manager;
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_NEVER;
    }
    
    public void setMultiViewCallback(final MultiViewElementCallback callback) {
        multiViewObserver = callback;
    }

    public CloseOperationState canCloseElement() {
        // if this is not the last cloned xml editor component, closing is OK
        if (!WorkflowEditorSupport.isLastView(multiViewObserver.getTopComponent())) {
            return CloseOperationState.STATE_OK;
        }
        // return a placeholder state - to be sure our CloseHandler is called
        return MultiViewFactory.createUnsafeCloseState(
                "ID_TEXT_CLOSING", // dummy ID // NOI18N
                MultiViewFactory.NOOP_CLOSE_ACTION,
                MultiViewFactory.NOOP_CLOSE_ACTION);
    }

   
    @Override
    public void componentHidden() {
        super.componentHidden();
        
    }
    
    @Override
    public void componentClosed() {
        super.componentClosed();
        cleanup();
    }
    
    @Override
    public void componentOpened() {
        super.componentOpened();
        initUI();
    }
    
    @Override
    public void componentActivated() {
        super.componentActivated();
        ExplorerUtils.activateActions(manager, true);
        mObj.getWorkflowEditorSupport().syncModel();
        updateGroupVisibility();
    }
    
    @Override
    public void componentDeactivated() {
        ExplorerUtils.activateActions(manager, false);
        super.componentDeactivated();
        updateGroupVisibility();
    }
    
    @Override
    public void componentShowing() {
        super.componentShowing();
        initUI();

        
    }

    @Override
    public void requestActive() {
        super.requestActive();
       
    }
    
    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx(WorkflowDesignViewMultiViewDesc.class);
    }

    private static Boolean groupVisible = null;
    
    private void updateGroupVisibility() {
        WindowManager wm = WindowManager.getDefault();
        final TopComponentGroup group = wm.findTopComponentGroup("wsdl_ui"); // NOI18N
        if (group == null) {
            return; // group not found (should not happen)
        }
        //
        boolean isWSDLViewSelected = false;
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
                        if (WorkflowDesignViewMultiViewDesc.PREFERRED_ID.equals(id)) {
                            isWSDLViewSelected = true;
                            break;
                        }
                    }
                }
            }
        }
        //
        if (isWSDLViewSelected && !Boolean.TRUE.equals(groupVisible)) {
            group.open();
        } else if (!isWSDLViewSelected && !Boolean.FALSE.equals(groupVisible)) {
            group.close();
        }
        //
        groupVisible = isWSDLViewSelected ? Boolean.TRUE : Boolean.FALSE;
        
    }

    @Override
    protected String preferredID() {
        return "WSDLTreeViewMultiViewElementTC";  //  NOI18N
    }

    /**
     * Construct the user interface.
     */
    private void initUI() {
        WorkflowEditorSupport editor = mObj.getWorkflowEditorSupport();
        /*IEPModel iepModel = editor.getModel();
        if (iepModel != null &&
        		iepModel.getState() == IEPModel.State.VALID) {
        	// Construct the standard editor interface.
        	if (graphComponent == null) {
        		graphComponent = new GraphView(iepModel);
        		ZoomManager manager = new ZoomManager(graphComponent.getPlanCanvas());
        		manager.addToolbarActions((JToolBar)getToolbarRepresentation());
        	}
        	removeAll();
        	add(graphComponent, BorderLayout.CENTER);
        	return;
        }*/
        if(true) {
         add(new WorkflowDesignViewPanel(), BorderLayout.CENTER);
         return;
        }
        
        //add(graphComponent, BorderLayout.CENTER);
        
        // Clear the interface and show the error message.
        removeAll();

        String errorMessage = null;

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

    public javax.swing.JComponent getToolbarRepresentation() {
        return new JToolBar();
        /*if (mToolbar == null) {
        	IEPModel model = mObj.getPlanEditorSupport().getModel();
        	if (model != null && model.getState() == IEPModel.State.VALID) {
        		mToolbar = new JToolBar();
        		mToolbar.setFloatable(false);
        		
        		mToolbar.addSeparator();
        		mToolbar.add(new ValidateAction(model));
        		
        		Action overviewAction = new OverviewAction(graphComponent.getPlanCanvas(), model);
        		mToolbar.add(overviewAction);
        		
        		Action toggleOrthoLinkAction = new ToggleOrthogonalLinkAction(graphComponent.getPlanCanvas(), model);
        		mToolbar.add(toggleOrthoLinkAction);
        		
        		Action autoLayoutAction = new AutoLayoutAction(graphComponent.getPlanCanvas(), model);
        		mToolbar.add(autoLayoutAction);
        		mToolbar.addSeparator();
        		
//        		Action toggleScopeAction = new ToggleScopeAction(graphComponent.getPlanCanvas(), model);
//        		mToolbar.add(toggleScopeAction);
//        				
                        addGenerateReportAction(mToolbar);
                        
                        
                        addCustomizeReportAction(mToolbar);
                        mToolbar.addSeparator();
        	}
        }
        return mToolbar;*/
    }
    /*
    private void addGenerateReportAction(JToolBar toolbar) {
        try {
            toolbar.add(new GenerateReportAction(mObj));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        
        
    }
    
    private void addCustomizeReportAction(JToolBar toolbar) {
        CustomizeReportAction action = new CustomizeReportAction();
        toolbar.add(action);
        
    }*/
    
    @Override
    public UndoRedo getUndoRedo() {
        return mObj.getWorkflowEditorSupport().getUndoManager();
    }


    
    public javax.swing.JComponent getVisualRepresentation() {
        return this;
    }   

   /* public GraphView getGraphView() {
    	return this.graphComponent;
    }*/
    
    /**
     * Find action for WSDL editor.
     *
     * @author  Nathan Fiedler
     */
    private class WSDLFindAction extends AbstractAction {
        /** silence compiler warnings */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new instance of WSDLFindAction.
         */
        public WSDLFindAction() {
        }

        public void actionPerformed(ActionEvent event) {
            WorkflowDesignViewMultiViewElement parent =
                    WorkflowDesignViewMultiViewElement.this;
            
        }
    }
}

/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.mount;

import java.awt.BorderLayout;
import javax.swing.ActionMap;
import javax.swing.text.DefaultEditorKit;
import org.openide.ErrorManager;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * @author Petr Hrebejk, Jesse Glick
 */
public class MountTab extends TopComponent 
                        implements ExplorerManager.Provider {
                
    public static final String ID = "mount"; // NOI18N
    
    private static MountTab DEFAULT = null;
                            
    private transient final ExplorerManager manager;
    private transient Node rootNode;
    
    private transient final BeanTreeView btv;
                         
    public MountTab() {
        // See #36315        
        manager = new ExplorerManager();
        
        ActionMap map = getActionMap();
        map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(manager));
        map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(manager));
        map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(manager));
        map.put("delete", ExplorerUtils.actionDelete(manager, true));
        
        setLayout(new java.awt.BorderLayout());
        btv = new BeanTreeView();
        add( btv, BorderLayout.CENTER ); 
        
        associateLookup( ExplorerUtils.createLookup(manager, map) );

        setName("Filesystems");
        // XXX setIcon
        if ( rootNode == null ) {
            // Create the node which lists open projects      
            rootNode = new MountRootNode();
        }
        manager.setRootContext( rootNode );
        
        // Make sure the mount project is opened so that classpaths are scanned:
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                DummyProject.getInstance();
            }
        });
    }
            
    /** Explorer manager implementation 
     */
    public ExplorerManager getExplorerManager() {
        return manager;
    }
    
    public static synchronized MountTab findDefault() {
                       
        MountTab tab = DEFAULT;
        
        if ( tab == null ) {
            //If settings file is correctly defined call of WindowManager.findTopComponent() will
            //call TestComponent00.getDefault() and it will set static field component.
            
            TopComponent tc = WindowManager.getDefault().findTopComponent(ID);
            if (tc != null) {
                if (!(tc instanceof MountTab)) {
                    //This should not happen. Possible only if some other module
                    //defines different settings file with the same name but different class.
                    //Incorrect settings file?
                    IllegalStateException exc = new IllegalStateException
                    ("Incorrect settings file. Unexpected class returned." // NOI18N
                    + " Expected:" + MountTab.class.getName() // NOI18N
                    + " Returned:" + tc.getClass().getName()); // NOI18N
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, exc);
                    //Fallback to accessor reserved for window system.
                    tab = MountTab.getDefault( );
                }
                else {
                    tab = (MountTab) tc;
                }
            } 
            else {
                //This should not happen when settings file is correctly defined in module layer.
                //TestComponent00 cannot be deserialized
                //Fallback to accessor reserved for window system.
                tab = MountTab.getDefault();
            }
        }
        return tab;
    }
    
    /* Singleton accessor reserved for window system ONLY. Used by window system to create
     * ProjectTab instance from settings file when method is given. Use <code>findDefault</code>
     * to get correctly deserialized instance of ProjectTab */
    public static synchronized MountTab getDefault() {
        if ( DEFAULT == null ) {
            DEFAULT = new MountTab();            
        }
        return DEFAULT;        
    }
    
    protected String preferredID () {
        return ID;
    }
    
    public HelpCtx getHelpCtx() {
        return ExplorerUtils.getHelpCtx( manager.getSelectedNodes(),
                                         new HelpCtx( MountTab.class ) );
    }

     
    public boolean requestFocusInWindow() {
        super.requestFocusInWindow();
        return btv.requestFocusInWindow();
    }
    
    // PERSISTENCE
    
    private static final long serialVersionUID = 1L;
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }
    
    // MANAGING ACTIONS
    
    protected void componentActivated() {
        ExplorerUtils.activateActions(manager, true);
    }
    
    protected void componentDeactivated() {
        ExplorerUtils.activateActions(manager, false);
    }
    
}

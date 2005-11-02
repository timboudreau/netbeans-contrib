/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2005 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.tool.actions;

import javax.swing.text.DefaultEditorKit;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallbackSystemAction;

/**
 * This action stub is needed to register the [CTRL]-[A] key as shortcut
 * for select all. There are many similar actions in the package
 * <tt>org.openide.actions</tt>, e.g. {@link org.openide.actions.CopyAction},
 * that allow to bind other shortcuts like [CTRL]-[C] for copyig objects.
 * <p>
 * In order to provide an implementation for this stub, you have to place
 * an action into the {@link javax.swing.ActionMap} of your tool:
 * <p>
 * <pre>
 * ActionMap map = getActionMap(  );
 * map.put( DefaultEditorKit.copyAction, new MyCopyAction( myController ) );
 * map.put( DefaultEditorKit.cutAction, new MyCutAction( myController ) );
 * map.put( DELETE_ACTION, new MyDeleteAction( myController ) );
 * map.put( DefaultEditorKit.pasteAction, new MyPasteAction( myController ) );
 * map.put( DefaultEditorKit.selectAllAction, new MySelectAllAction( myController ) );
 * </pre>
 * <p>
 * See also {@link org.openide.explorer.ExplorerUtils} for an alternative
 * way how to register actins in the {@link javax.swing.ActionMap}.
 * <p>
 * <b>Note</b>: In case you don't use the explorer support for actions, you
 * have to enable/disable the actions in the {@link javax.swing.ActionMap}
 * based on the state of your tool (e.g. copy makes only sense if something
 * is selected). 
 *  
 * @author John Stuwe
 */
public class SelectAllSystemAction extends CallbackSystemAction
{
    //=======================================================================
    // Public methods
    
    /**
     * Provides the key under which implementing actions must be registered
     * in the {@link javax.swing.ActionMap}, so that they will be invoked by
     * this stub.
     * 
     * @return The constant {@link DefaultEditorKit#selectAllAction}.
     */
    public Object getActionMapKey( )
    {
        return DefaultEditorKit.selectAllAction;
    }

    /**
     * The label of this action is picked from a properties file. Usually
     * it is 'Select All' for English language.
     * 
     * @return The label of this action.
     */
    public String getName( )
    {
        return NbBundle.getMessage( SelectAllSystemAction.class, "SelectAll" ); //$NON-NLS-1$
    }

    /**
     * The help context of this action.
     * 
     * @return The help context.
     */
    public HelpCtx getHelpCtx( )
    {
        return new HelpCtx( SelectAllSystemAction.class );
    }
    
    //=======================================================================
    // Protected methods
    
    /**
     * Initializes this action.
     */
    protected void initialize()
    {
        super.initialize();
        setSurviveFocusChange(true);
    }
   
    /**
     * Provides the path to the icon that represents this action.
     * 
     * @return The path to the icon 'select_all.gif'.
     */     
    protected String iconResource( )
    {
        return NbBundle.getMessage( SelectAllSystemAction.class, "SelectAll_icon" ); //$NON-NLS-1$
    }

    /**
     * This action is performed synchronous.
     * 
     * @return Always <tt>false</tt>.
     */
    protected boolean asynchronous()
    {
        return false;
    }

}


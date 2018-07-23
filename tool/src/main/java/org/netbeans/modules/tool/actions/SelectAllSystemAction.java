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
 * Software is Nokia. Portions Copyright 2005 Nokia.
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


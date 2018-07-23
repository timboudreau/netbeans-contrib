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

package org.netbeans.modules.bookmarks;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Properties;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import org.netbeans.api.bookmarks.Bookmark;
import org.netbeans.api.bookmarks.BookmarkProvider;
import org.netbeans.spi.convertor.SimplyConvertible;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Explorer type top component allowing bookmarks customization.
 * This top component is a singleton.
 *
 * @author David Strupl
 */
public class ManageBookmarksTool extends TopComponent implements ExplorerManager.Provider,
        Lookup.Provider, SimplyConvertible, BookmarkProvider {
    
    /** Singleton instance */
    private static ManageBookmarksTool instance;
    
    /** ExplorerManager for the tree view. */
    private ExplorerManager manager;
    
    /** Icon image path.*/
    private static final String MY_ICON = "org/netbeans/modules/bookmarks/resources/add.gif";
    
    /**
     * Name of property used to store this component's name
     */
    private static final String PROP_NAME = "name"; //$NON-NLS-1$
    
    /**
     * Name of property used to store this component's tooltip
     */
    private static final String PROP_TOOLTIP = "tooltip"; //$NON-NLS-1$
    
    BeanTreeView tv = null;
    
    public ManageBookmarksTool() {
        instance = this; // for the case if someone is creating the instance
        // not from the method getInstance (e.g. from winsys storage)
        this.manager = new ExplorerManager();
        ActionMap map = this.getActionMap();
        map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(manager));
        map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(manager));
        map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(manager));
        map.put("delete", ExplorerUtils.actionDelete(manager, true)); // or false
        
        // following line tells the top component which lookup should be associated with it
        associateLookup(ExplorerUtils.createLookup(manager, map));
        setName("ManageBookmarks");
        setDisplayName(NbBundle.getBundle(ManageBookmarksTool.class).getString("ManageBookmarks"));
        setIcon(Utilities.loadImage(MY_ICON, true));
        init();
    }
    
    public void readSettings(java.util.Properties p) {
        // empty - the root node is always the same
    }
    
    public void writeSettings(java.util.Properties p) {
        // empty - the root node is always the same
    }
    
    private void init() {
        setLayout( new BorderLayout() );
        setFocusCycleRoot(true);
        tv = new BeanTreeView();
        
        manager.setRootContext(new BookmarksRootNode());
        
        add(tv, BorderLayout.CENTER );
    }
    
    public ExplorerManager getExplorerManager() {
        return manager;
    }
    // It is good idea to switch all listeners on and off when the
    // component is shown or hidden. In the case of TopComponent use:
    protected void componentActivated() {
        ExplorerUtils.activateActions(manager, true);
        tv.requestFocus();
    }
    protected void componentDeactivated() {
        ExplorerUtils.activateActions(manager, false);
    }
    /**
     * Read object state from the given Properties instance.
     * The method will be called only once by Convertor infrastructure just
     * after the instance was created by default constructor.
     *
     * @param p properties instance with properties stored by the
     * {@link #write(Properties)} method.
     *
     * @throws org.netbeans.api.convertor.ConvertorException can throw this
     *     exception when content of {@link Properties} instance is malformed.
     */
    public final void read( Properties p ) {
        setName( p.getProperty( PROP_NAME ) );
        
        String tooltip = p.getProperty( PROP_TOOLTIP );
        if( tooltip != null ) {
            setToolTipText( tooltip );
        }
        
        readSettings( p );
    }
    
    
    /**
     * Write object state to the given Properties instance.
     * The Convertor infrastructure will take care about persistence of
     * content of {@link Properties} instance. Non-String properties are
     * forbidden. For naming restrictions on property keys see the class {@link Javado.
     *
     * @param p Empty properties instance for the data to be persisted.
     */
    public final void write( Properties p ) {
        String name = getName();
        
        if (name != null) {
            p.setProperty( PROP_NAME, getName() );
        } else {
            throw new IllegalArgumentException( "ManageBookmarksTool does not hava a name?" );
        }
        
        String tooltip = getToolTipText(  );
        if( tooltip != null ) {
            p.setProperty( PROP_TOOLTIP, tooltip );
        }
        
        writeSettings( p );
    }
    /**
     * @inheritdoc
     *
     * This method is not expected to be used. Throws always an
     * {@link IOException}.
     */
    public final void writeExternal( ObjectOutput out )
    throws IOException {
        String message =
                NbBundle.getMessage( ManageBookmarksTool.class, "ManageBookmarksTool.Serialize", this ); //$NON-NLS-1$
        throw new IOException( message );
    }
    
    
    /**
     * @inheritdoc
     *
     * This method is not expected to be used. Throws always an
     * {@link IOException}.
     */
    public final void readExternal( ObjectInput in )
    throws IOException, ClassNotFoundException {
        String message =
                NbBundle.getMessage( ManageBookmarksTool.class, "ManageBookmarksTool.Deserialize", this ); //$NON-NLS-1$
        throw new IOException( message );
    }
    
    
    /**
     * Reads the properties stored for this tool from the virtual file
     * system when the tool gets restored.
     *
     * This is the old XML format implemented in core/settings. Please
     * use/override method {@link #readSettings(Properties)}.
     *
     * @param properties The properties that got serialized.
     *
     * @see org.netbeans.spi.convertor.SimplyConvertible
     */
    public final void readProperties( Properties properties ) {
        read( properties );
    }
    
    
    /**
     * Writes the current settings of this tool to the given properties which
     * will be stored in the virtual file system.
     *
     * This is the old XML format implemented in core/settings. Please
     * use/override the method {@link #writeSettings(Properties)}.
     *
     * @param properties The properties that shall get serialized.
     *
     * @see org.netbeans.spi.convertor.SimplyConvertible
     */
    public final void writeProperties( Properties properties ) {
        write( properties );
    }
    
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }
    
    public static ManageBookmarksTool getInstance() {
        if (instance == null) {
            instance = new ManageBookmarksTool();
        }
        return instance;
    }

    public Bookmark createBookmark() {
        return new ManageBookmarksBookmarkImpl();
    }

    protected String preferredID() {
        return "ManageBookmarksTool"; // NOI18N
    }
}

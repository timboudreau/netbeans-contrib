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
package org.netbeans.modules.tool;

import java.awt.Frame;
import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.netbeans.api.bookmarks.Bookmark;
import org.netbeans.api.bookmarks.BookmarkProvider;
import org.netbeans.spi.convertor.SimplyConvertible;
import org.openide.util.NbBundle;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;


/**
 * Common superclass for all Tools. Provides a single interface to store
 * settings using the Core/Settings module and the newer mechanism of the
 * Registry/Convertor API at the same time. This means all subclasses of
 * this class automatically support to save the desktop layout, bookmarks,
 * and cloning.
 * <p>
 * In order to (de-)serialize properties specific to the subclass, the methods
 * {@link #readSettings(Properties)} and {@link #writeSettings(Properties)}
 * need to be overridden.
 * <p>
 * As this class implements the {@link org.netbeans.spi.convertor.SimplyConvertible}
 * interface, the subclass <b>must</b> be registered in the manifest file to 
 * have any effect. The following is an example for such a manifest entry:
 * <p>
 * <pre>
 * Name: com/example/vwtvie/NetworkView.class
 * NetBeans-Simply-Convertible: {com/example/vwtvie}NetworkView
 * </pre>
 * <p>
 * Please check the doc of {@link org.netbeans.spi.convertor.SimplyConvertible}
 * for more details about the registration.
 * <p>
 * This class supports the differentiation between view- and editor tools.
 * When deciding what kind of the Tool you are going to have, 
 * the main argument should be whether your Tool is a singleton or not. 
 * In the NetBeans terminology singleton tools are called "view". Other tools
 * are called "editor".
 * <p> 
 * All views should be initially docked into the {@link #EXPLORER_MODE}, 
 * {@link #OUTPUT_MODE} or {@link #PROPERTIES_MODE}, while "editor" tools only
 * get docked into the {@link #EDITOR_MODE}.
 * <p>
 * Singleton tools must be configured in layer.xml, so that the window system
 * knows how to create them. This requires to define the singleton instance of 
 * the tool (i.e. the initial tool settings) in the folder 
 * "Windows2/Components" and to associate that instance with a mode in the 
 * folder "Windows2/Modes/&lt;mode name&gt;". Here is the example:
 * <p>
 * <pre>
 *    &lt;folder name="Windows2"&gt;  
 *
 *      &lt;folder name="Modes"&gt;
 *          &lt;folder name="explorer"&gt;
 *              &lt;file name="com_example_tpclie_topology_explorer.wstcref" url="topology_explorer.wstcref"/&gt;
 *          &lt;/folder&gt;
 *      &lt;/folder&gt; &lt-- Modes --&gt;
 *
 *      &lt;folder name="Components"&gt;
 *          &lt;file name="com_example_tpclie_topology_explorer.settings" url="topology_explorer.settings"&gt;
 *             &lt;attr name="viewClass" stringvalue="com.example.explorer.EntityTreeView"/&gt;
 *             &lt;attr name="supportedViews" stringvalue="com.example.explorer.EntityTreeView#Tree,com.example.explorer.EntityTreeTableView#Tree-Table"/&gt;
 *             &lt;attr name="rootNodeName" stringvalue="Topology"/&gt;
 *             &lt;attr name="domainEntityType" stringvalue="TPFTPC_ManagedObject"/&gt;
 *             &lt;attr name="supportedRelations" stringvalue="TPFTPC_ManagedObject#children,TPFTPC_ManagedObject#parentInMrHierarchy,TPFTPC_ManagedObject#parentInSiteHierarchy"/&gt;
 *             &lt;attr name="relation" stringvalue="TPFTPC_ManagedObject#children"/&gt;
 *             &lt;attr name="rootNodePath" stringvalue="TPFTPC_Root"/&gt;
 *           &lt;/file&gt;
 *      &lt;/folder&gt; &lt;-- Components --&gt;
 *   &lt;/folder&gt;
 * </pre>
 * <p>
 * The first entry in the Modes folder defines into which mode the tool 
 * initially shall be docked (the "explorer" mode in this example). The 
 * referenced "topology_explorer.wstcrf" file defines the unique {@link 
 * org.openide.windows.TopComponent} ID (in this case 
 * "com_example_tpclie_topology_explorer"). Furthermore it defines 
 * whether the tool is initially closed or opened.
 * <p>
 * <pre> 
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 *
 * &lt;tc-ref version="2.0"&gt;
 *   &lt;tc-id id="com_example_tpclie_topology_explorer" /&gt;
 *   &lt;state opened="false" /&gt;
 * &lt;/tc-ref&gt;
 * </pre>
 * <p>
 * The "topology_explorer.settings" file contains the settings needed to create 
 * the singleton instance of the tool. It is referenced by the entry in the 
 * Components folder. As the example shows, you can add additional settings
 * in the layer.xml file. In the example this permits to define different
 * types of explorers.
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;!DOCTYPE settings PUBLIC "-//NetBeans//DTD Session settings 1.0//EN" "http://www.netbeans.org/dtds/sessionsettings-1_0.dtd"&gt;
 *
 * &lt;settings version="1.0"&gt;
 *   &lt;instanceof class="org.openide.windows.TopComponent"/&gt;
 *   &lt;instanceof class="com.example.serreg.explorer.DomainEntityExplorer"/&gt;
 *   &lt;instance class="com.example.serreg.explorer.DomainEntityExplorerFactory" method="singleton"/&gt;
 * &lt;/settings&gt;
 * </pre>
 * <p>
 * As the settings file shows, a factory method is needed to create the 
 * singleton tool. Usually this method can be part of the tool itself, but in 
 * complicated cases, as in the example above, it can be defined in a separate 
 * factory.
 * <p>
 * Finally you can access the singleton tool via {@link 
 * WindowManager#findTopComponent(java.lang.String)}, with the {@link 
 * org.openide.windows.TopComponent} ID as parameter.
 * 
 * @author David Strupl, John Stuwe, Vladislav Kublanov
 */
public abstract class AbstractTool
             extends TopComponent
             implements SimplyConvertible,
                                     TopComponent.Cloneable,
                                     BookmarkProvider
{
    //=======================================================================
    // Public constants
    
    /**
     * The explorer mode. Should be used for all navigation tools,
     * especially trees. Use the string "explorer" in the layer.xml
     * configuration to address this mode.
     */
    public static final String EXPLORER_MODE = "explorer"; //$NON-NLS-1$
    
    
    /**
     * The default mode for tools. Should be used for all tools requiring 
     * much screen space, like e.g. the Network View tool. Use the string
     * "editor" in the layer.xml configuration to address this mode.
     */
    public static final String EDITOR_MODE = "editor"; //$NON-NLS-1$
    
    /**
     * The secondary mode for tools. Should be used for all tools requiring 
     * less screen space and user interaction. Use the string "output" in 
     * the layer.xml configuration to address this mode.
     */
    public static final String OUTPUT_MODE = "output"; //$NON-NLS-1$
    
    /**
     * The properties mode. Should be used for all tools displaying
     * detailed properties of selected items in other tools. Use the 
     * string "properties" in the layer.xml configuration to address 
     * this mode.
     */
    public static final String PROPERTIES_MODE = "properties"; //$NON-NLS-1$
    
    
    //=======================================================================
    // Private data members
    
    private static volatile long theToolCounter = System.currentTimeMillis();
    
    /**
     * Name of property used to store this component's name
     */
    private static final String PROP_NAME = "name"; //$NON-NLS-1$

    /**
     * Name of property used to store this component's tooltip
     */
    private static final String PROP_TOOLTIP = "tooltip"; //$NON-NLS-1$


    private static final String PROP_TC_ID = "tcid"; //$NON-NLS-1$

    /**
     * This flag differntiates between views and editors.
     */    
    private String myTopComponentId; 
        
    //=======================================================================
    // Public methods

    /**
     * When Tool is created with this constructor, it is assumed that
     * it is an "editor". Otherwise use {@link #AbstractTool(String)}
     * in order to create a "view" (singleton).
     * <p>
     * The name of the tool will be initialized with the class name of
     * the tool.
     */
    public AbstractTool()
    {
        this( null, null );
    }

    /**
     * This constructor creates a view (singleton) tool with the given
     * top component ID. The ID is used in the window system configuration
     * for identifying the tool (compare with the class description).
     * 
     * @param tcid The top component ID which will be used as name also.
     *        A <tt>null</tt> value will create an editor (non-singleton)
     *        tool without name.
     */
    public AbstractTool( String tcid )
    {
        this( tcid, null );
    }

    /**
     * This constructor creates a view (singleton) tool with the given
     * top component ID. The ID is used in the window system configuration
     * for identifying the tool (compare with the class description).
     * <p>
     * The settings can be used to initialize the tool when it gets created
     * from window system settings.
     * 
     * @param tcid The top component ID which will be used as name also.
     *        A <tt>null</tt> value will create an editor (non-singleton)
     *        tool without name.
     * 
     * @param settings The initial setings of the tool.
     */
    public AbstractTool( String tcid, Properties settings )
    {
        if( tcid != null )
        {
            myTopComponentId = tcid;
            setName(tcid);
        }
        else
        {
            theToolCounter++;
            setName( getClass().getName().replace( '.', '_' )  + '_' + theToolCounter );
        }
        
        if( settings != null )
        {
            readSettings( settings );
        }
    }

    protected String preferredID( )
    {
        String id = myTopComponentId;
        
        if( id == null )
        {
            id = super.preferredID( );
        }
        
        return id;
    }
    

    /**
     * This method checks whether this Tool is a "view".
     *  
     * @return true if it is a "view"
     *             false if it is an "editor".
     */    
    public boolean isSingleton( )
    {
        return myTopComponentId != null;
    }

    /**
     * Opens the tool in the given mode. The tool will be visible
     * and tries to grab focus.
     * 
     * @param mode Mode should be one of the mode constants defined
     *        in this class
     * 
     * @see #EXPLORER_MODE
     * @see #PRIMARY_MODE
     * @see #SECONDARY_MODE
     * @see #PROPERTIES_MODE
     */
    public void openInMode( String mode )
    {
        Mode wsMode = WindowManager.getDefault( ).findMode( mode );
        
        if( wsMode == null || !wsMode.canDock( this ) )
        {
            Object[] args = 
            {
                getDisplayName( ),
                mode
            };
            
            String message =
                NbBundle.getMessage( AbstractTool.class, "AbstractTool.DockingFailed", args ); //$NON-NLS-1$

            throw new IllegalArgumentException(message);
        }
        
        wsMode.dockInto( this );
        
        super.open( );
        requestVisible( );
        requestActive( );
    }
    
    /**
     * Each Tool which pretends to be a "view" should be docked into its default mode,
     * @see TopComponent.getDefaultMode(). 
     * This could be #EXPLORER_MODE, #OUTPUT_MODE or #PROPERTIES_MODE.
      * The "editor" tools are docked initially only into the #EDITOR_MODE.
     * 
     */
    public void open()
    {
        if (isSingleton() == true)
        {
            super.open( );
            requestVisible( );
            requestActive( );
        }
        else
        {
            openInMode( findMode( ) );
        }
    }

    
    
    /**
     * @inheritdoc
     * 
     * @return Always {@link TopComponent#PERSISTENCE_ONLY_OPENED}.
     */
    public int getPersistenceType(  )
    {
        
        int persistence = TopComponent.PERSISTENCE_ONLY_OPENED;
        
        if (isSingleton() == true)
        {
            persistence = TopComponent.PERSISTENCE_ALWAYS;
        }

        return persistence;
    }


    /**
     * @inheritdoc
     * 
     * This method is not expected to be used. Throws always an 
     * {@link IOException}.
     */
    public final void writeExternal( ObjectOutput out )
            throws IOException
    {
        String message =
            NbBundle.getMessage( AbstractTool.class, "AbstractTool.Serialize", this ); //$NON-NLS-1$
        throw new IOException( message );
    }


    /**
     * @inheritdoc
     * 
     * This method is not expected to be used. Throws always an 
     * {@link IOException}.
     */
    public final void readExternal( ObjectInput in )
            throws IOException, ClassNotFoundException
    {
        String message =
            NbBundle.getMessage( AbstractTool.class, "AbstractTool.Deserialize", this ); //$NON-NLS-1$
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
    public final void readProperties( Properties properties )
    {
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
    public final void writeProperties( Properties properties )
    {
        write( properties );
    }


    //=======================================================================
    // SimplyConvertible interface

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
    public final void read( Properties p )
    {
        setName( p.getProperty( PROP_NAME ) );
        myTopComponentId = p.getProperty( PROP_TC_ID );
        
        String tooltip = p.getProperty( PROP_TOOLTIP );
        if( tooltip != null )
        {
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
    public final void write( Properties p )
    {
        String name = getName();
        
        if (name != null)
        {
            p.setProperty( PROP_NAME, getName() );
        }
        else
        {
            String message =
                NbBundle.getMessage( AbstractTool.class, "AbstractTool.ComponentNameNull" ); //$NON-NLS-1$

            throw new IllegalArgumentException( message );
        }
        
        if( myTopComponentId != null )
        {
            p.setProperty( PROP_TC_ID, myTopComponentId );
        }

        String tooltip = getToolTipText(  );
        if( tooltip != null )
        {
            p.setProperty( PROP_TOOLTIP, tooltip );
        }

        writeSettings( p );
    }


    //=======================================================================
    // BookmarkProvider interface

    /**
     * Implementing BookmarkProvider interface. ToolBookmark bound
     * to this AbstractTool is returned.
     */
    public Bookmark createBookmark(  )
    {
        
        String name = getDisplayName(  );
        if( name == null )
        {
            name = getName(  );
        }

        name = ToolBookmark.askUserAboutNewBookmarkName(name);
        if (name == null) {
            return null;
        }
        
        ToolBookmark tb = new ToolBookmark( this, name );

        return tb;
    }


    //=======================================================================
    // TopComponent.Cloneable interface

    /**
     * If you want a better cloning algorithm please feel free
     * to override this method.
     */
    public TopComponent cloneComponent(  )
    {
        AbstractTool clone = null;
        
        //    Cloning is allowed only for "editors"
        if (isSingleton() == false)
        {
            try
            {
                clone = (AbstractTool)getClass(  ).newInstance(  );
                Properties settings = new Properties(  );
                write( settings );
                clone.read( settings );
            }
            catch( InstantiationException ie )
            {
                Logger.getLogger(AbstractTool.class.getName()).log(
                    Level.SEVERE, "", ie);
            }
            catch( IllegalAccessException ia )
            {
                Logger.getLogger(AbstractTool.class.getName()).log(
                    Level.SEVERE, "", ia);
            }
        }
        else
        {
            clone = this;
        }
        
        return clone;
    }


    // =======================================================================
    // Protected methods

    
    /**
     * You can override this method to retrieve tool's state
     * from the persistent storage. This method is called
     * right after the default contstructor and the Properties
     * object contains values stored by method writeSettings.
     *
     * @param p Properties instance containing values stored by
     *        {@link #writeSettings(Properties)}.
     */
    protected void readSettings( Properties p )
    {
    }


    /**
     * You can override this method to store the tool's state
     * to the persistent storage. This method is called
     * whenever the infrastructure thinks that the tool should be saved.
     *
     * Put everything that you need to get stored to the properties
     * object using
     * <tt>p.setProperty(&lt;your key&gt;, &lt;your value&gt;);</tt>
     * <b>Note</b>: Non-String properties are forbidden!
     *
     * @param p Properties instance that will get stored as tool state.
     */
    protected void writeSettings( Properties p )
    {
    }
    
    
    /**
     * Gets the best matching mode for docking the <tt>AbstractTool</tt> 
     * into the desktop. The method checks first if there are already other 
     * instances of the tool docked in the desktop and compares the locations 
     * of those tools. The method tries to find the mode that contains a tool
     * of same implementation class that covers the biggest area on the screen.
     * Second criterium is to place the tool as much as possible in the top
     * left corner of the desktop.
     * <p>
     * If no matching tool was found, the default mode of the tool will be 
     * used.
     * 
     * @return The best matching mode for docking this tool.
     */
    private String findMode( )
    {
        String mode = EDITOR_MODE;

        //Following code makes sense only for editor tools!
        if ( isSingleton() == false )
        {
        
            Point topLeft = new Point(10000,10000);
            int topArea = 0;
            Set openTools = WindowManager.getDefault( ).getRegistry( ).getOpened( );
            Iterator tools = openTools.iterator( );
        
            while( tools.hasNext( ) )
            {
                TopComponent tool = (TopComponent)tools.next( );
                if( tool.getClass( ).equals( this.getClass( ) ) )
                {
                    WindowManager manager = WindowManager.getDefault( );
                    Mode wsMode = manager.findMode( tool );
                    if ((wsMode != null) && (wsMode.getSelectedTopComponent( ) != null)) {
                        //
                        // After restart, only the visible TC
                        // has correct bounds
                        //
                        tool = wsMode.getSelectedTopComponent( );
                    }
                 
                    Point location = tool.getLocation( );
                    int area = tool.getWidth( ) * tool.getHeight( ); 
                    Frame mainWindow = manager.getMainWindow( );
                    location = SwingUtilities.convertPoint( tool, location, mainWindow );
                
                    if ( (area > topArea) ||
                         ( (area == topArea) && 
                           (topLeft.y > location.y) || 
                           ( (topLeft.y == location.y) && 
                             (topLeft.x > location.x)
                       ) ) )
                    {
                        topArea = area;
                        topLeft = location;
                        mode = manager.findMode( tool ).getName( );
                    }
                }
            }
        }
        
        return mode;
    }
    
}


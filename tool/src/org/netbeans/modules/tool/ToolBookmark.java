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
 * Software is Nokia. Portions Copyright 2005 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.tool;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;

import org.netbeans.api.bookmarks.Bookmark;
import org.netbeans.spi.convertor.SimplyConvertible;
import org.openide.ErrorManager;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

/**
 * Implements a default {@link Bookmark} for all {@link AbstractTool}'s.
 *
 * @author John Stuwe
 */
public class ToolBookmark
             implements Bookmark,
                                     SimplyConvertible,
                                     ActionListener,
                                     Cloneable
{
    //=======================================================================
    // Private data members
    
    //
    // The properties needed for the bookmark itself.
    //
    private static final String NAME = "ToolBookmark.Name"; //$NON-NLS-1$
    private static final String ICON = "ToolBookmark.Icon"; //$NON-NLS-1$
    private static final String TOOL_CLASS = "ToolBookmark.ToolClass"; //$NON-NLS-1$
    private static final String MODE = "ToolBookmark.Mode"; //$NON-NLS-1$
    private static final String TC_ID ="ToolBookmark.TopComponentID";  //$NON-NLS-1$

    //
    // Default size for byte[] buffer to convert image data into string
    //
    private static final int BUFFER_SIZE = 1024;

    //
    // Label and icon of the bookmark and 
    // the settings of the bookmarked tool
    //
    private String myName;
    private ImageIcon myIcon;
    private Class myToolClass;
    private String myTopComponentId;
    private String myMode;
    private Properties mySettings;

    //
    // UI components visualizing the bookmark (either in Bookmarks 
    // menu or in the toolbar)
    //
    private JMenuItem myMenuItem;
    private JButton myToolButton;

    //
    // Helper for managing property change events
    //
    private PropertyChangeSupport myPropertySupport;

    //=======================================================================
    // Public methods

    /**
     * Creates a new <tt>ToolBookmark</tt>. This constructor is only
     * availabl to support the {@link SimplyConvertible} interface. A
     * {@link org.netbeans.api.bookmarks.BookmarkProvider} should use
     * the constructor {@link #ToolBookmark(AbstractTool)} to create a
     * new bookmark.
     */
    public ToolBookmark(  )
    {
        mySettings = new Properties(  );
        myPropertySupport = new PropertyChangeSupport( this );
    }


    /**
     * Creates a new <tt>ToolBookmark</tt> that stores the current state
     * of the given tool.
     *
     * @param tool The tool that shall be bookmarked.
     */
    public ToolBookmark( AbstractTool tool )
    {
        this(  );

        myName = tool.getDisplayName(  );
        if( myName == null )
        {
            myName = tool.getName(  );
        }
        
        myIcon = new ImageIcon( tool.getIcon(  ) );
        
        if( tool.isSingleton( ) )
        {
            myTopComponentId = tool.preferredID();
        }
        else
        {
            myToolClass = tool.getClass(  );
            Mode mode = WindowManager.getDefault(  ).findMode( tool );
            myMode = mode.getName(  );
        }

        tool.write( mySettings );
    }

    //=======================================================================
    // ActionListener interface

    /**
     * Needed to catch {@link ActionEvent}'s when the user wants to
     * restore the tool from the bookmark.
     *
     * @param event The event that occurred.
     */
    public void actionPerformed( ActionEvent event )
    {
        invoke(  );
    }


    //=======================================================================
    // SimplyConvertible interface

    /**
     * Reads the settings of the bookmark from the XML file system.
     *
     * @param settings The settings from the XML file system.
     */
    public void read( Properties settings )
    {
        try
        {
            mySettings.clear(  );
            mySettings.putAll( settings );
            mySettings.remove( NAME );
            mySettings.remove( ICON );
            mySettings.remove( TOOL_CLASS );
            mySettings.remove( MODE );
            mySettings.remove( TC_ID );

            myName = settings.getProperty( NAME );

            String icon = settings.getProperty( ICON );
            myIcon = stringToIcon( icon );

            myTopComponentId = settings.getProperty( TC_ID );
            if( myTopComponentId == null )
            {
        
                String className = settings.getProperty( TOOL_CLASS );
                ClassLoader loader =
                    (ClassLoader)Lookup.getDefault(  ).lookup( ClassLoader.class );
                myToolClass = Class.forName( className, true, loader );

                myMode = settings.getProperty( MODE );
            }
        }
        catch( Exception e )
        {
            Object[] args = { myName };
            String message =
                NbBundle.getMessage( ToolBookmark.class, "ToolBookmark.BadSettings", args ); //$NON-NLS-1$

            ErrorManager manager =
                ErrorManager.getDefault(  ).getInstance( "org.netbeans.modules.tool" ); //$NON-NLS-1$
            manager.annotate( e, message );
            manager.notify( ErrorManager.ERROR, e );
        }
    }


    /**
     * Writes the settings of the bookmark to the XML file system.
     *
     * @param settings The settings that will be written to the XML
     *         file system.
     */
    public void write( Properties settings )
    {
        settings.putAll( mySettings );

        settings.setProperty( NAME, myName );

        String icon = iconToString( myIcon );
        settings.setProperty( ICON, icon );

        if( myTopComponentId != null )
        {
            settings.setProperty( TC_ID, myTopComponentId );
        }
        else
        {
            String className = myToolClass.getName(  );
            settings.setProperty( TOOL_CLASS, className );
            settings.setProperty( MODE, myMode );
        }
    }


    //=======================================================================
    // Bookmark interface

    /**
     * Method implementing interface {@link
     * org.openide.util.actions.Presenter.Menu}. Uses name and icon of this
     * bookmark as label and icon of the menu item.
     *
     * @return The created menu item.
     */
    public JMenuItem getMenuPresenter(  )
    {
        if( myMenuItem == null )
        {
            myMenuItem = new JMenuItem( myName );
            myMenuItem.addActionListener( this );

            if( myIcon != null )
            {
                myMenuItem.setIcon( myIcon );
            }
        }

        return myMenuItem;
    }


    /**
     * Method implementing interface {@link
     * org.openide.util.actions.Presenter.Toolbar}. Creates a rollover button
     * that uses name and icon of this bookmark as tooltip and icon.
     *
     * @return The created {@link DFButton}.
     */
    public Component getToolbarPresenter(  )
    {
        if( myToolButton == null )
        {
            myToolButton = new JButton(  );
            myToolButton.setToolTipText( myName );
            myToolButton.addActionListener( this );

            if( myIcon != null )
            {
                myToolButton.setIcon( myIcon );
            }
        }

        return myToolButton;
    }


    /**
     * Provides the icon used for visualizing this bookmark in
     * menus and toolbar.
     *
     * @return The icon of this bookmark.
     */
    public ImageIcon getIcon(  )
    {
        return myIcon;
    }


    /**
     * Sets the icon used for visualizing this bookmark in
     * menus and toolbar.
     *
     * @param icon The icon of this bookmark.
     */
    public void setIcon( ImageIcon icon )
    {
        if( !myIcon.equals( icon ) )
        {
            ImageIcon oldIcon = myIcon;
            myIcon = icon;

            myPropertySupport.firePropertyChange( ICON, oldIcon, myIcon );

            if( myMenuItem != null )
            {
                myMenuItem.setIcon( myIcon );
            }

            if( myToolButton != null )
            {
                myToolButton.setIcon( myIcon );
            }
        }
    }


    /**
     * Provides the label/tooltip used for visualizing this bookmark in
     * menus and toolbar.
     *
     * @return The label/tooltip of this bookmark.
     */
    public String getName(  )
    {
        return myName;
    }


    /**
     * Sets the label/tooltip used for visualizing this bookmark in
     * menus and toolbar.
     *
     * @param name The label/tooltip of this bookmark.
     */
    public void setName( String name )
    {
        if( !myName.equals( name ) )
        {
            String oldName = myName;
            myName = name;

            myPropertySupport.firePropertyChange( NAME, oldName, myName );

            if( myMenuItem != null )
            {
                myMenuItem.setText( myName );
            }

            if( myToolButton != null )
            {
                myToolButton.setToolTipText( myName );
            }
        }
    }


    /**
     * Invokes this bookmark. The stored tool will be created using the {@link
     * SimplyConvertible} interface. That means the default constructor (i.e.
     * the constructor without arguments) is used to create an instance of the
     * tool and then the settings are restored by calling the method {@link
     * SimplyConvertible#read}.
     */
    public void invoke(  )
    {
        // TODO: assert not possible in ant build
        //assert SwingUtilities.isEventDispatchThread( ) : "Bookmarks must be invoked on the AWT event thread."; //$NON-NLS-1$
        
        try
        {
            if( myTopComponentId != null )
            {
                WindowManager manager = WindowManager.getDefault( );
                AbstractTool tool = (AbstractTool)manager.findTopComponent( myTopComponentId );
                tool.read( mySettings );
                   tool.open( );
                tool.requestActive( );
            }
            else
            {
                AbstractTool tool = (AbstractTool)myToolClass.newInstance(  );
                tool.read( mySettings );
                
                Mode mode = WindowManager.getDefault( ).findMode( myMode );
                if( mode == null )
                {
                    tool.open( );
                }
                else
                {
                    tool.openInMode( myMode );
                }
            }
        }
        catch( Exception e )
        {
            Object[] args = { myName };
            String message =
                NbBundle.getMessage( ToolBookmark.class, "ToolBookmark.BadInvocation", args ); //$NON-NLS-1$

            ErrorManager manager =
                ErrorManager.getDefault(  ).getInstance( "org.netbeans.modules.tool" ); //$NON-NLS-1$
            manager.annotate( e, message );
            manager.notify( ErrorManager.ERROR, e );
        }
    }


    /* (non-Javadoc)
     * @see org.netbeans.api.bookmarks.Bookmark#addPropertyChangeListener(PropertyChangeListener)
     */
    public void addPropertyChangeListener( PropertyChangeListener listener )
    {
        myPropertySupport.addPropertyChangeListener( listener );
    }


    /*
     * (non-Javadoc)
     * @see org.netbeans.api.bookmarks.Bookmark#removePropertyChangeListener(PropertyChangeListener)
     */
    public void removePropertyChangeListener( PropertyChangeListener listener )
    {
        myPropertySupport.removePropertyChangeListener( listener );
    }


    //=======================================================================
    // Private methods

    /**
     * Converts the given {@link ImageIcon} into a string.
     *
     * @param icon The icon
     *
     * @return A string containing the icon data in PNG format.
     */
    private static String iconToString( ImageIcon icon )
    {
        String string = null;

        Image image = icon.getImage(  );
        int h = icon.getIconHeight(  );
        int w = icon.getIconWidth(  );

        try
        {
            //
            // Draw the icon into a BufferedImage -> 
            // permits to write it a stream via ImageIO
            //
            BufferedImage buffer =
                new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
            buffer.getGraphics(  ).drawImage( image, 0, 0, w, h,
                                              icon.getImageObserver(  ) );

            //
            // Write image into byte[] buffer
            //
            ByteArrayOutputStream stream =
                new ByteArrayOutputStream( BUFFER_SIZE );
            ImageIO.write( buffer, "png", stream ); //$NON-NLS-1$
            stream.close(  );

            //
            // Convert byte[] buffer into string
            //
            byte[] data = stream.toByteArray(  );
            BigInteger convertor = new BigInteger( data );
            string = convertor.toString(  );
        }
        catch( Exception e )
        {
            ErrorManager manager =
                ErrorManager.getDefault(  ).getInstance( "org.netbeans.modules.tool" ); //$NON-NLS-1$
            manager.notify( ErrorManager.INFORMATIONAL, e );
        }

        return string;
    }


    /**
     * Converts the given {@link String} into an {@link ImageIcon}.
     *
     * @param string A string containing encoded image data in PNG format.
     *
     * @return The icon that was loaded from the string after decoding.
     */
    private static ImageIcon stringToIcon( String string )
    {
        ImageIcon icon = null;

        try
        {
            //
            // Convert string into byte buffer
            //
            BigInteger convertor = new BigInteger( string );
            byte[] data = convertor.toByteArray(  );

            //
            // Read image from byte buffer via ImageIO
            //
            ByteArrayInputStream stream = new ByteArrayInputStream( data );
            Image image = ImageIO.read( stream );
            stream.close(  );
            icon = new ImageIcon( image );
        }
        catch( Exception e )
        {
            ErrorManager manager =
                ErrorManager.getDefault(  ).getInstance( "org.netbeans.modules.tool" ); //$NON-NLS-1$
            manager.notify( ErrorManager.INFORMATIONAL, e );
        }

        return icon;
    }


    /**
     * Just delegate the call to the PropertyChangeSupport.
     */
    public void firePropertyChange( String propertyName, Object oldValue, Object newValue )
    {
        myPropertySupport.firePropertyChange( propertyName, oldValue, newValue );
    }


    /**
     * Support making clones.
     */
    public Object clone(  )
            throws CloneNotSupportedException
    {
        ToolBookmark res = (ToolBookmark)super.clone(  );

        // the clone should use its own menu item and button
        res.myMenuItem = null;
        res.myToolButton = null;

        // also make a fresh support:
        res.myPropertySupport = new PropertyChangeSupport( res );

        return res;
    }
}


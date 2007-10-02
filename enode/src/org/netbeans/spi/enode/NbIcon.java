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
 * Software is Nokia. Portions Copyright 2003-2004 Nokia.
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

package org.netbeans.spi.enode;

import org.netbeans.modules.enode.ScaledIcon;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openide.util.Utilities;



/**
 * This class allows to load icons without raising any exceptions. When the
 * requested icon cannot be loaded, a default icon is rendered in memory and
 * returned instead. The user will detect the (ugly) default icon, so that
 * no further notifications are needed. Any exceptions that occur when loading
 * an icon fails are logged.
 * <p>
 * In case a loaded icon does not match the requested size, the icon will be
 * scaled to proper size automatically.
 *
 * @author John Stuwe
 *
 * @see #ScaledIcon
 */
public class NbIcon extends ImageIcon {
    
    private static final Logger log = Logger.getLogger(NbIcon.class.getName());
    private static boolean LOGGABLE = log.isLoggable(Level.FINE);

    //=======================================================================
    // Public constants
    
    /**
     * Constant for the default icons size 16x16 pixels.
     */
    public static final int SIZE_16x16 = 16;
    
    /**
     * Constant for the default icons size 20x20 pixels.
     */
    public static final int SIZE_20x20 = 20;
    
    /**
     * Constant for the default icons size 32x32 pixels.
     */
    public static final int SIZE_32x32 = 32;
    
    //=======================================================================
    // Private data members
    
    //
    // Default description for unknown icon
    //
    private static final String DESCRIPTION = "Unknown Icon";
    
    //
    // Constants needed for drawing the icon.
    //
    private static final int PREFERRED_SIZE = 16;
    private static final int RECT_X = 4;
    private static final int RECT_Y = 4;
    private static final int RECT_WIDTH = 8;
    private static final int RECT_HEIGHT = 8;
    private static final int CROSS_X = 6;
    private static final int CROSS_Y = 6;
    private static final int CROSS_WIDTH = 4;
    private static final int CROSS_HEIGHT = 4;
    
    //
    // The defaukt icon sizes get cached to safe memory...
    //
    private static ImageIcon theDefault16x16Icon;
    private static ImageIcon theDefault20x20Icon;
    private static ImageIcon theDefault32x32Icon;
    
    //
    // Width and height of the icon.
    //
    private Image myImage = null;
    private int mySize = 0;
    
    //=======================================================================
    // Private constructor
    
    /**
     * Creates a rectangular icon with the given size.
     *
     * @param size The width and height in pixels.
     * @param enabled Boolean flag that indicates if an enabled or disabled
     *        icon shall be drawn.
     */
    private NbIcon( int size ) {
        super(  );
        setDescription( DESCRIPTION );
        
        mySize = size;
        myImage = new BufferedImage( mySize, mySize, BufferedImage.TYPE_INT_ARGB );
        
        Color fill = Color.WHITE;
        Color border = Color.BLACK;
        Color cross = Color.RED;
        
        Graphics g = myImage.getGraphics(  );
        g.setColor( fill );
        g.fillRect( 0, 0, size - 1, size - 1 );
        g.setColor( border );
        g.drawRect( 0, 0, size - 1, size - 1 );
        
        //
        // Add 'button' with  red cross in the upper
        // left corner.
        //
        if( size > PREFERRED_SIZE ) {
            g.draw3DRect( RECT_X, RECT_Y, RECT_WIDTH, RECT_HEIGHT, true );
            g.setColor( cross );
            g.drawLine( CROSS_X, CROSS_Y, CROSS_X + CROSS_WIDTH,
            CROSS_Y + CROSS_HEIGHT );
            g.drawLine( CROSS_X, CROSS_Y + CROSS_HEIGHT, CROSS_X + CROSS_WIDTH,
            CROSS_Y );
        }
        else {
            g.setColor( cross );
            g.drawLine( RECT_X, RECT_Y, size - RECT_X - 1, size - RECT_Y - 1 );
            g.drawLine( size - RECT_X - 1, RECT_Y, RECT_X, size - RECT_Y - 1 );
        }
        
        g.dispose( );
        
        setImage( myImage );
    }
    
    //=======================================================================
    // Public methods
    
    /**
     * Factory method that loads an icon from the given {@link URL}. If the
     * icon cannot be loaded, a default icon with the given size is returned.
     * In case the with or height of the loaded icon does not match the
     * given size the icon will be scaled to fit the requested size.
     *
     * @param url The URL from which the icon shall be loaded.
     * @param size The required width and height of the icon.
     * @param description The description of the icon. Can be used to
     *         identify the icon later.
     */
    public static ImageIcon loadIcon( URL url, int size, String description ) {
        ImageIcon icon = null;
        
        try {
            icon = new ImageIcon( url );
            
            
            if( icon.getIconHeight(  ) != size ||
            icon.getIconWidth(  ) != size ) {
                icon = (ImageIcon)ScaledIcon.create( icon, size );
            }
        }
        catch( Exception e ) {
            log.log(Level.FINE, "", e);
        }
        
        if( icon == null ) {
            icon = unknownIcon( size );
        }
        
        if( description != null ) {
            icon.setDescription( description );
        }
        
        return icon;
    }
    
    /**
     * Factory method that loads an icon from the JAR file path (across
     * module boundaries). If the icon cannot be loaded, a default icon with
     * the given size is returned. In case the with or height of the loaded
     * icon does not match the given size the icon will be scaled to fit the
     * requested size.
     *
     * @param file The JAR path from which the icon shall be loaded.
     * @param size The required width and height of the icon.
     * @param description The description of the icon. Can be used to
     *         identify the icon later.
     */
    public static ImageIcon loadIcon( String file, int size, String description ) {
        ImageIcon icon = null;
        
        try {
            if (file == null) {
                throw new IllegalStateException("Icon with description " + description + " cannot be loaded.");
            }
            Image image = Utilities.loadImage(file, true);
            if (image == null) {
                throw new IOException("File " + file + " cannot be found.");
            }
            icon = new ImageIcon( image );
            
            if( icon.getIconHeight(  ) != size ||
            icon.getIconWidth(  ) != size ) {
                icon = (ImageIcon)ScaledIcon.create( icon, size );
            }
        }
        catch(Exception e) {
            log.log(Level.FINE, "", e);
        }
        
        if( icon == null ) {
            icon = unknownIcon( size );
        }
        
        if( description != null ) {
            icon.setDescription( description );
        }
        
        return icon;
    }
    
    /**
     * Creates a default icon with the given size as width and height.
     * The icon is rendered in a {@link Graphics} context of a {@link
     * BufferedImage} so that no resources have to be loaded (i.e. it
     * should always work).
     *
     * @param size The width and height of the icon.
     */
    public static ImageIcon unknownIcon( int size ) {
        ImageIcon icon = null;
        
        switch( size ) {
            case SIZE_16x16:
                
                if( theDefault16x16Icon == null ) {
                    theDefault16x16Icon = new NbIcon( SIZE_16x16 );
                }
                
                icon = theDefault16x16Icon;
                
                break;
                
            case SIZE_20x20:
                
                if( theDefault20x20Icon == null ) {
                    theDefault20x20Icon = new NbIcon( SIZE_20x20 );
                }
                
                icon = theDefault20x20Icon;
                
                break;
                
            case SIZE_32x32:
                
                if( theDefault32x32Icon == null ) {
                    theDefault32x32Icon = new NbIcon( SIZE_32x32 );
                }
                
                icon = theDefault32x32Icon;
                
                break;
                
            default:
                icon = new NbIcon( size );
        }
        
        return icon;
    }
}


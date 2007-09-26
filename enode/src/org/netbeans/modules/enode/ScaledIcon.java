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

package org.netbeans.modules.enode;

import org.netbeans.spi.enode.NbIcon;
import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;


/**
 * Scales an icon to a given width or height. By default icons are scaled to
 * a width of 20 pixels. Such icons are needed for menu items.
 * <p>
 * The aspect ratio of the icon is kept if the height is less than the width
 * (or if the width is less than the height, depending on which property shall
 * be kept). Otherwise the height is adjusted to the width (or the other way
 * round).
 *
 * @author John Stuwe
 */
public class ScaledIcon extends ImageIcon {
    /**
     * Constructor for creating scaled icons. To create a <tt>ScaledIcon</tt>,
     * use the public static methods in this class.
     *
     * @param image The image that shall be returned.
     *
     * @see #scale(Icon)
     * @see #scale(Icon,int)
     * @see #scale(Icon,int,boolean)
     */
    private ScaledIcon( Image image ) {
        super( image );
    }
    
    /**
     * Scales the icon to a width of 16 pixels. The aspect ratio of the icon is
     * kept if the resulting height would be less than 16 pixels, otherwise the
     * height is truncated to 16 pixels.
     * <p>
     * @param The icon that shall be scaled.
     * <p>
     * @return The scaled icon. This is a new instance of <tt>ScaledIcon</tt>.
     */
    public static Icon create( ImageIcon icon ) {
        return create( icon, NbIcon.SIZE_16x16, true );
    }
    
    
    /**
     * Scales the icon to a given width. The aspect ratio of the icon is kept if
     * the resulting height would be less than the given width, otherwise the
     * height is truncated to the given width.
     * <p>
     * @param The icon that shall be scaled.
     * <p>
     * @param width The width of the scaled icon.
     * <p>
     * @return The scaled icon. This is a new instance of <tt>ScaledIcon</tt>.
     */
    public static Icon create( ImageIcon icon, int width ) {
        return create( icon, width, true );
    }
    
    
    /**
     * Scales the icon to a given width or height. The aspect ratio of the icon
     * is kept if the resulting height (or width) would be less than the given
     * size, otherwise the height (or width) is truncated to the given size.
     * <p>
     * @param The icon that shall be scaled.
     * <p>
     * @param size The width or height of the scaled icon, depending on the flag
     *         <tt>keepWidth</tt>.
     * <p>
     * @param keepWidth If <tt>true</tt>, the width is scaled to the given size,
     *         otherwise the height of the icon is scaled to the given size.
     * <p>
     * @return The scaled icon. This is a new instance of <tt>ScaledIcon</tt>.
     */
    public static Icon create( ImageIcon icon, int size, boolean keepWidth ) {
        return create( icon, size, keepWidth, false );
    }
    
    
    /**
     * Scales the icon to a given width or height. The aspect ratio of the icon
     * is kept if the resulting height (or width) would be less than the given
     * size, otherwise the height (or width) is truncated to the given size.
     * <p>
     * @param The icon that shall be scaled.
     * <p>
     * @param size The width or height of the scaled icon, depending on the flag
     *         <tt>keepWidth</tt>.
     * <p>
     * @param scaleWidth If <tt>true</tt>, the width is scaled to the given size,
     *         otherwise the height of the icon is scaled to the given size.
     * <p>
     * @param magnify If <tt>true</tt>, the icons gets magnified if it is too
     *         small. Otherwise the small icon is centered on a transparent
     *         background.
     * <p>
     * @return The scaled icon. This is a new instance of <tt>ScaledIcon</tt>.
     */
    public static Icon create( ImageIcon icon, int size, boolean scaleWidth,
    boolean magnify ) {
        Icon scaled = icon;
        
        if( icon.getImageLoadStatus(  ) == MediaTracker.COMPLETE ) {
            int width = icon.getIconWidth(  );
            int height = icon.getIconHeight(  );
            
            Image image = icon.getImage(  );
            
            if( magnify || ( width > size ) || ( height > size ) ) {
                image = scaleImage( image, width, height, size, scaleWidth );
            }
            else {
                BufferedImage buffer =
                new BufferedImage( size, size, BufferedImage.TYPE_INT_ARGB );
                
                Graphics g = buffer.getGraphics(  );
                int x = ( size - width ) / 2;
                int y = ( size - height ) / 2;
                g.drawImage( image, x, y, null );
                g.dispose(  );
                
                image = buffer;
            }
            
            scaled = new ScaledIcon( image );
        }
        
        return scaled;
    }
    
    
    /**
     * Paints the scaled icon. To avoid a {@link NullPointerException}, the
     * method uses a {@link MediaTracker} to check if the scaled image is
     * available. If the image is not available nothing will be painted.
     * <p>
     * @param c The component that wants to paint the icon.
     * <p>
     * @param g The current graphics context.
     * <p>
     * @param x The x position for painting the icon.
     * <p>
     * @param y The y position for painting the icon.
     */
    public void paintIcon( Component c, Graphics g, int x, int y ) {
        //
        // Ensure that the scaled image is really available!
        //
        if( getImageLoadStatus(  ) == MediaTracker.COMPLETE ) {
            super.paintIcon( c, g, x, y );
        }
    }
    
    
    /**
     * Scales the image, so that it fits into a square with the given size as
     * width and height.
     *
     * @param image The image that shall be scaled.
     * <p>
     * @param originalWidth The original width of the image.
     * <p>
     * @param originalHeight The original height of the image.
     * <p>
     * @param size The new width and height of the image.
     * <p>
     * @param scaleWidth If <tt>true</tt>, the width is scaled to the given
     *         size, otherwise the height of the icon is scaled to the given
     *         size.
     * <p>
     * @return The scaled image.
     */
    private static Image scaleImage( Image image, int originalWidth, int originalHeight, int size, boolean scaleWidth ) {
        //
        // Keep aspect ratio if possible. If the height exceeds the
        // width, the icon gets distorted to avoid gaps between the
        // menu items.
        //
        int height = -1;
        int width = -1;
        
        if( scaleWidth ) {
            width = size;
            
            if( originalHeight > originalWidth ) {
                height = width;
            }
        }
        else {
            height = size;
            
            if( originalWidth > originalHeight ) {
                width = height;
            }
        }
        
        return image.getScaledInstance( width, height, Image.SCALE_SMOOTH );
    }
}


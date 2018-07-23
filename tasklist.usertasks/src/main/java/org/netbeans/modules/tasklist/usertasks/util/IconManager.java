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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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

package org.netbeans.modules.tasklist.usertasks.util;

import java.io.File;
import org.openide.ErrorManager;

import java.awt.*;
import java.awt.image.*;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


/**
 * Class which wraps functionality for managing icons. This class
 * does nothing on JDK1.3, but on JDK1.4 its subclass (IconManager14)
 * can export icons.
 * <p>
 * @author Tor Norbye
 */
public class IconManager {
    private HashMap written = null;
    private File directory = null;
    private int nextId = 1;

    /**
     * Constructor
     *
     * @param base directory in which icon files are written.
     * Paths returned by getIcon are relative to this directory.
     */
    public IconManager(File base) {
        this.directory = base;
    }    
    
    /**
     * Return the file name of the given icon, relative to
     * the directory that this IconManager was initialized with.
     * If the icon does not yet exist on disk, it is created first.
     * <b>You MUST call setBase before the first call to this method.</b>
     * @param icon The image we want a url for
     * @return A relative filename pointing to the image file
     */
    public String getIcon(Image icon) {
        if (written == null) {
            written = new HashMap(50);
        }
        
        String name = (String)written.get(icon);
        if (name == null) {
            // Invent filename for the icon. Can we use any tricks
            // here, if we pass in the node etc.?
            // For now, just use sequential naming
            BufferedImage image = toBufferedImage(icon);
            File output = null;
            // Find unused name
            while (true) {
                // Using PNG format - gif is probably more common, but
                // a gif writer often is not available in ImageIO
                name = "tasklist-html-" + (nextId++) + ".png"; // NOI18N
                output = new File(directory, name);
                if (!output.exists()) {
                    break;
                }
            }
            try {
                ImageIO.write(image, "png", output); // NOI18N
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            written.put(icon, name);
        }
        return name;
    }

    /**
     * This method is from the The Java Developers Almanac 1.4
     * http://javaalmanac.com/egs/java.awt.image/Image2Buf.html
     * and is copyright by that book's author(s)
     */
    private static BufferedImage toBufferedImage(Image image) {
        // This method returns a buffered image with the contents of an image
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }
    
        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();
    
        // Determine if the image has transparent pixels; for this method's
        // implementation, see e665 Determining If an Image Has Transparent Pixels
        //boolean hasAlpha = hasAlpha(image);
        boolean hasAlpha = true;
    
        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }
    
            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }
    
        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
    
        // Copy image to buffered image
        Graphics g = bimage.createGraphics();
    
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();
    
        return bimage;
    }
}

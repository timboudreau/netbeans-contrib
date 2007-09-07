/*
 * ScreenCapturer.java
 *
 * Created on May 17, 2007, 7:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.j2ee.sun.ws7.util;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Prabushankar.Chinnasamy
 */
public class ScreenCapturer {

    /** Creates a new instance of ScreenCapturer */
    public ScreenCapturer() {
    }
    public void capture(String imageName, String resultDir, Logger logger) {
            /*********Screen Capture*********/
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRectangle = new Rectangle(screenSize);
        Robot robot;

        try {
            robot = new Robot();
            BufferedImage image = robot.createScreenCapture(screenRectangle);
            ImageIO.write(image, "png", new File(resultDir + imageName + ".png"));
            logger.info("Screen captured <a href=" + imageName + ".png>" + imageName + ".png </a>" );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

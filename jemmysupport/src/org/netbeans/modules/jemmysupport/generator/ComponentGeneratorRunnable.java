/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jemmysupport.generator;

/*
 * ComponentGeneratorRunnable.java
 *
 * Created on February 7, 2002, 4:41 PM
 */
import java.util.ArrayList;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.*;
import javax.swing.JLabel;
import javax.swing.JInternalFrame;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.File;
import java.awt.event.ActionEvent;
import java.util.Properties;

import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.util.PNGEncoder;
import java.awt.image.BufferedImage;

/** class observing CTRL-F12 key and launching ComponentGenerator
 * @author <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 * @version 0.1
 */
public class ComponentGeneratorRunnable implements Runnable, AWTEventListener {
    
    String directory;
    String packageName;
    JLabel help;
    Container window;
    ComponentGenerator gen;
    ComponentGeneratorPanel panel;
    boolean screenShot;
    boolean showEditor;
    
    /** Creates new ComponentGeneratorRunnable
     * @param directory String destination directory
     * @param packageName String package name
     * @param panel ComponentGeneratorPanel
     * @param properties CompoenentGenerator configuration properties
     */
    public ComponentGeneratorRunnable(String directory, String packageName, ComponentGeneratorPanel panel, Properties properties, boolean screenShot, boolean showEditor) {
        this.directory = directory;
        this.packageName = packageName;
        help = panel.getHelpLabel();
        gen = new ComponentGenerator(properties);
        this.panel = panel;
        this.screenShot = screenShot;
        this.showEditor = showEditor;
    }

    /** called when event is dispatched
     * @param aWTEvent aWTEvent
     */    
    public void eventDispatched(java.awt.AWTEvent aWTEvent) {
        if ((aWTEvent instanceof KeyEvent)&&(aWTEvent.getID()==KeyEvent.KEY_RELEASED)&&(((KeyEvent)aWTEvent).getKeyCode()==KeyEvent.VK_F12)&&(((KeyEvent)aWTEvent).getModifiers()==KeyEvent.CTRL_MASK)) {
            if (window==null) {
                window=(Container)aWTEvent.getSource();
                while (!((window instanceof Window)||(window instanceof JInternalFrame))) {
                    window = window.getParent();
                }
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }    

    
    /** method implementing Runnable interface
     */    
    public void run() {
        try {
            Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK|AWTEvent.KEY_EVENT_MASK);
            PrintStream out;
            File file;
            boolean write;
            while (!Thread.currentThread().interrupted()) {
                while (window==null) {
                    Thread.currentThread().sleep(100);
                }
                help.setText("Please wait, processing ...");
                try {
                    gen.grabComponents(window, packageName, showEditor);
                    BufferedImage shot=null;
                    if (screenShot) {
                        shot = new Robot().createScreenCapture(new Rectangle(window.getLocationOnScreen(),window.getSize()));
                    }
                    int i=2;
                    String name = gen.getClassName();
                    String index = "";
                    while ((file=new File(directory+"/"+name+index+".java")).exists()){
                        index = String.valueOf(i++);
                    }
                    gen.setClassName(name+index);
                    
                    if ((!showEditor)||(ComponentsEditorPanel.showDialog(gen.getRoot(), gen.getNodes()))) {

                        file=new File(directory+"/"+gen.getClassName()+".java");
                        out=new PrintStream(new FileOutputStream(file));
                        out.println(gen.getComponentCode());
                        out.close();

                        if (screenShot) {
                            new PNGEncoder(new FileOutputStream(directory+"/"+gen.getClassName()+".png")).encode(shot);
                        }

                        help.setText("Finished: "+gen.getClassName());
                    } else {
                        help.setText("Operation canceled.");
                    }
                } catch (Exception e) {
                    help.setText("Exception: "+e.getMessage());
                    e.printStackTrace();
                }
                window=null;
            }
        } catch (InterruptedException ie) {
        } finally {
            Toolkit.getDefaultToolkit().removeAWTEventListener(this);
        }
    }
   
}

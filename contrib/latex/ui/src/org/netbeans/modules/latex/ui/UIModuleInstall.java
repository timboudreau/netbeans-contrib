/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.netbeans.modules.latex.ui.wizards.install.PostInstallIterator;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.modules.ModuleInstall;
import org.openide.windows.WindowManager;


/**
 *
 * @author Jan Lahoda
 */
public class UIModuleInstall extends ModuleInstall {
    
    private static final String KEY_POSTINSTALL_VERSION = "postinstall-version";
    
    private static final int version = 1;
    
    /** Creates a new instance of UIModuleInstall */
    public UIModuleInstall() {
    }

    private File getUserDir() {
        return new File(System.getProperty("netbeans.user"));
    }
    
    private Map readSettings() {
        XMLDecoder dec = null;
        
        try {
            File postInstallFlag = new File(new File(getUserDir(), "var"), ".latex-ui-post-install");
            
            if (!postInstallFlag.canRead())
                return null;
            
            dec = new XMLDecoder(new FileInputStream(postInstallFlag));
            
            Object read = dec.readObject();
            
            if (read instanceof Map)
                return (Map) read;
            
            return null;
        } catch (Exception e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            return null;
        } finally {
            if (dec != null)
                dec.close();
        }
    }
    
    private void setPostInstallDone(Map settings) {
        XMLEncoder enc = null;
        try {
            settings.put(KEY_POSTINSTALL_VERSION, new Integer(version));
            
            for (Iterator i = settings.keySet().iterator(); i.hasNext(); ) {
                Object key = i.next();
                
                if (key != null && key instanceof String && ((String) key).startsWith("temp-"))
                    i.remove();
            }
            
            File postInstallFlag = new File(new File(getUserDir(), "var"), ".latex-ui-post-install");
            
            enc = new XMLEncoder(new FileOutputStream(postInstallFlag));
            
            enc.writeObject(settings);
        } catch (Exception e) {
            ErrorManager.getDefault().notify(e);
        } finally {
            if (enc != null)
                enc.close();
        }
    }
    
    private int getVersion(Map settings) {
        if (settings == null)
            return -1;
        
        Object versionObj = settings.get(KEY_POSTINSTALL_VERSION);
        
        if (versionObj != null && versionObj instanceof Integer) {
            return ((Integer) versionObj).intValue();
        }
        
        return -1;
    }
    
    public void restored() {
        if (!Boolean.getBoolean("netbeans.latex.ignore.postinstall")) {
            Map settings = readSettings();
            
            if (getVersion(settings) < version) {
                //            SwingUtilities.invokeLater(new Runnable() {
                //                public void run() {
                //                    final Frame mainWindow = WindowManager.getDefault().getMainWindow();
                //                    mainWindow.addComponentListener(new ComponentAdapter() {
                //                        public void componentShown(ComponentEvent evt) {
                //                            mainWindow.removeComponentListener(this);
                doPostInstall(settings);
                //                        }
                //                    });
                //                }
                //            });
            }
        }
        super.restored();
    }
    
    private void doPostInstall(Map settings) {
        if (settings == null)
            settings = new HashMap();
        
        WizardDescriptor wd = new WizardDescriptor(new WizardDescriptor.Panel[0], settings);
        
        wd.setPanels(new PostInstallIterator(wd));
        wd.setTitleFormat(new MessageFormat("LaTeX Post Install Configuration"));
        wd.putProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
        
        Dialog d = DialogDisplayer.getDefault().createDialog(wd);
        int    xSize      = 750;
        int    ySize      = 500;
        int    xStartPos  = 0;
        int    yStartPos  = 0;
        int    xTotalSize = 0;
        int    yTotalSize = 0;
        
        Frame mainWindow = SwingUtilities.isEventDispatchThread() ?  WindowManager.getDefault().getMainWindow() : null;
        
        if (mainWindow != null && mainWindow.getSize().getWidth() != 0) {
            xStartPos = mainWindow.getX();
            yStartPos = mainWindow.getY();
            
            Dimension size = mainWindow.getSize();
            
            xTotalSize = (int) size.getWidth();
            yTotalSize = (int) size.getHeight();
        } else {
            Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds();
            
            xStartPos = (int) bounds.getX();
            yStartPos = (int) bounds.getY();
            
            xTotalSize = (int) bounds.getWidth();
            yTotalSize = (int) bounds.getHeight();
        }
        
        int xPos = (xTotalSize - xSize) / 2 + xStartPos;
        int yPos = (yTotalSize - ySize) / 2 + yStartPos;
        
        System.err.println("size = (" + xSize + ", " + ySize + ")");
        System.err.println("tartPos = (" + xStartPos + ", " + yStartPos + ")");
        System.err.println("totalSize = (" + xTotalSize + ", " + yTotalSize + ")");
        System.err.println("pos = (" + xPos + ", " + yPos + ")");
        
        d.setBounds(xPos, yPos, xSize, ySize);
        d.show();
        d.toFront();
        
        setPostInstallDone(settings);
    }
}

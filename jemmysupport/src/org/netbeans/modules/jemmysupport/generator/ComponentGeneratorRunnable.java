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
import java.awt.event.FocusEvent;
import java.util.Properties;

import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.util.PNGEncoder;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import org.netbeans.api.diff.StreamSource;
import org.netbeans.spi.diff.DiffProvider;
import org.netbeans.spi.diff.MergeVisualizer;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.io.ReaderInputStream;

/** class observing CTRL-F12 key and launching ComponentGenerator
 * @author <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 * @version 0.1
 */
public class ComponentGeneratorRunnable implements Runnable, AWTEventListener {
    
    String directory;
    String packageName;
    JLabel help;
    Component window,focused;
    ComponentGenerator gen;
    ComponentGeneratorPanel panel;
    boolean screenShot;
    boolean showEditor;
    boolean merge;
    static final Border focusedBorder=BorderFactory.createLineBorder(Color.red, 1);
    Border lastBorder;
    
    /** Creates new ComponentGeneratorRunnable
     * @param screenShot boolean true when create screen shot
     * @param showEditor boolean true when show components editor
     * @param directory String destination directory
     * @param packageName String package name
     * @param panel ComponentGeneratorPanel
     * @param properties CompoenentGenerator configuration properties */
    public ComponentGeneratorRunnable(String directory, String packageName, ComponentGeneratorPanel panel, Properties properties, boolean screenShot, boolean showEditor, boolean merge) {
        this.directory = directory;
        this.packageName = packageName;
        help = panel.getHelpLabel();

        GeneratorProvider prov = (GeneratorProvider)Lookup.getDefault().lookup(GeneratorProvider.class);
        if (prov==null) {
            gen = new ComponentGenerator(properties);
        } else {
            gen = prov.getInstance(properties);
        }

//        gen=new org.netbeans.modules.testtools.generator.JellyComponentGenerator(properties);
        
        this.panel = panel;
        this.screenShot = screenShot;
        this.showEditor = showEditor;
        this.merge = merge;
    }

    static JComponent getJComponent(Component c) {
        if (c instanceof JComponent) return (JComponent)c;
        if (c instanceof JFrame) return ((JFrame)c).getRootPane();
        if (c instanceof JDialog) return ((JDialog)c).getRootPane();
        return null;
    }
    
    /** called when event is dispatched
     * @param aWTEvent aWTEvent
     */    
    public synchronized void eventDispatched(java.awt.AWTEvent aWTEvent) {
        if (aWTEvent instanceof FocusEvent) {
            if (aWTEvent.getID()==FocusEvent.FOCUS_GAINED) {
                focused=(Component)aWTEvent.getSource();
                while (!gen.isTopComponent(focused)) {
                    focused = focused.getParent();
                }
                JComponent rootPane=getJComponent(focused);
                if (rootPane!=null){
                    lastBorder=rootPane.getBorder();
                    rootPane.setBorder(focusedBorder);
                }
            } else if (aWTEvent.getID()==FocusEvent.FOCUS_LOST) {
                removeFocus();
            }
        } else if ((aWTEvent instanceof KeyEvent)&&(aWTEvent.getID()==KeyEvent.KEY_RELEASED)&&(((KeyEvent)aWTEvent).getKeyCode()==KeyEvent.VK_F12)&&(((KeyEvent)aWTEvent).getModifiers()==KeyEvent.CTRL_MASK)) {
            if (focused!=null) {
                window=focused;
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }    

    void removeFocus() {
        if (focused!=null) {
            JComponent rootPane=getJComponent(focused);
            if (rootPane!=null){
                rootPane.setBorder(lastBorder);
            }
            focused=null;
        }
    }

    static void mergeConflicts(File f, Reader r) throws IOException {
        File tmp = File.createTempFile("merge", "temporary");
        tmp.deleteOnExit();
        tmp.createNewFile();
        InputStream in = null;
        OutputStream out = null;
        try {
            FileUtil.copy(in = new ReaderInputStream(r), out = new FileOutputStream(tmp));
        } finally {
            if (in != null) in.close();
            if (out != null) out.close();
        }
        StreamSource s1=StreamSource.createSource(f.getName(), NbBundle.getMessage(ComponentGeneratorRunnable.class, "LBL_OldComponent"), "text/x-java", f); // NOI18N
        StreamSource s2=StreamSource.createSource(f.getName(), NbBundle.getMessage(ComponentGeneratorRunnable.class, "LBL_NewComponent"), "text/x-java", tmp); // NOI18N
        DiffProvider diff=(DiffProvider)Lookup.getDefault().lookup(DiffProvider.class);
        MergeVisualizer merge=(MergeVisualizer)Lookup.getDefault().lookup(MergeVisualizer.class);
        merge.createView(diff.computeDiff(s1.createReader(), s2.createReader()), s1, s2, s1).show();
    }
    
    /** method implementing Runnable interface
     */    
    public void run() {
        try {
            PrintStream out;
            File file;
            boolean write;
            while (!Thread.currentThread().interrupted()) {
                focused=null;
                Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK|AWTEvent.FOCUS_EVENT_MASK);
                while (window==null) {
                    Thread.currentThread().sleep(100);
                }
                Toolkit.getDefaultToolkit().removeAWTEventListener(this);
                help.setText(NbBundle.getMessage(ComponentGeneratorRunnable.class, "MSG_Processing")); // NOI18N
                removeFocus();
                try {
                    gen.grabComponents((Container)window, packageName, showEditor);
                    BufferedImage shot=null;
                    if (screenShot) {
                        shot = new Robot().createScreenCapture(new Rectangle(window.getLocationOnScreen(),window.getSize()));
                    }
                    if (!merge) {
                        int i=2;
                        String name = gen.getClassName();
                        String index = "";
                        while ((file=new File(directory+"/"+name+index+".java")).exists()) { // NOI18N
                            index = String.valueOf(i++);
                        }
                        gen.setClassName(name+index);
                    }
                    if ((!showEditor)||(ComponentsEditorPanel.showDialog(gen))) {
                        file=new File(directory+"/"+gen.getClassName()+".java"); // NOI18N
                        if (merge && file.exists()) {
                            mergeConflicts(file, new StringReader(gen.getComponentCode()));
                        } else {
                            out=new PrintStream(new FileOutputStream(file));
                            out.println(gen.getComponentCode());
                            out.close();
                        }

                        if (screenShot) {
                            new PNGEncoder(new FileOutputStream(directory+"/"+gen.getClassName()+".png")).encode(shot); // NOI18N
                        }

                        help.setText(NbBundle.getMessage(ComponentGeneratorRunnable.class, "MSG_Finished")+gen.getClassName()); // NOI18N
                    } else {
                        help.setText(NbBundle.getMessage(ComponentGeneratorRunnable.class, "MSG_Canceled")); // NOI18N
                    }
                } catch (Exception e) {
                    help.setText(NbBundle.getMessage(ComponentGeneratorRunnable.class, "MSG_Exception")+e.getMessage()); // NOI18N
                    e.printStackTrace();
                }
                window=null;
            }
        } catch (InterruptedException ie) {
        } finally {
            Toolkit.getDefaultToolkit().removeAWTEventListener(this);
            removeFocus();
        }
    }
   
}

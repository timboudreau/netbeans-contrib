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
import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.util.Properties;

import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.util.PNGEncoder;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import org.netbeans.api.diff.Difference;
import org.netbeans.api.diff.StreamSource;
import org.netbeans.spi.diff.DiffProvider;
import org.netbeans.spi.diff.MergeVisualizer;
import org.netbeans.modules.merge.builtin.visualizer.GraphicalMergeVisualizer;
import org.netbeans.modules.merge.builtin.visualizer.MergeDialogComponent;
import org.netbeans.modules.merge.builtin.visualizer.MergePanel;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

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
        try {
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
        } catch (Exception e) {}
    }    

    void removeFocus() {
        if (focused!=null) try {
            JComponent rootPane=getJComponent(focused);
            if (rootPane!=null){
                rootPane.setBorder(lastBorder);
            }
        } finally {
            focused=null;
        }
    }

    static void mergeConflicts(File f, String s) throws IOException {
        StreamSource s1=new ComponentSource(f);
        StreamSource s2=new ComponentSource(f.getName(), s);
        DiffProvider diff=(DiffProvider)Lookup.getDefault().lookup(DiffProvider.class);
        MergeVisualizer merge=(MergeVisualizer)Lookup.getDefault().lookup(MergeVisualizer.class);
        Component c=merge.createView(diff.computeDiff(s1.createReader(), s2.createReader()), s1, s2, s1);
        if (c instanceof MergeDialogComponent)
            ((MergeDialogComponent)c).getSelectedMergePanel().firePropertyChange(MergePanel.PROP_CAN_BE_SAVED,false,true);
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
                            mergeConflicts(file, gen.getComponentCode());
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
    
    private static class ComponentSource extends StreamSource {
        
        private String content;
        private File file;
        private String name;
        
        ComponentSource(String name, String content) {
            this.file = null;
            this.content = content;
            this.name = name;
        }
        
        ComponentSource(File file) {
            this.file = file;
            this.content = null;
            this.name = file.getName();
        }
        
        public String getName() {
            return name;
        }
        
        public String getTitle() {
            return NbBundle.getMessage(ComponentGeneratorRunnable.class, file==null?"LBL_NewComponent":"LBL_OldComponent"); // NOI18N
        }
        
        public String getMIMEType() {
            return "text/x-java"; // NOI18N
        }
        
        public Reader createReader() throws IOException {
            if (file != null) return new BufferedReader(new FileReader(file));
            return new StringReader(content);
        }
        
        public Writer createWriter(Difference[] conflicts) throws IOException {
            if (file != null) return new BufferedWriter(new FileWriter(file));
            return null;
        }
        
        public void notifyClosed() {
        }
    
    }
  
}

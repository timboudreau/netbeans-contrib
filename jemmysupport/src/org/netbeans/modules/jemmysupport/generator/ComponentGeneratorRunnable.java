/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jemmysupport.generator;

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.io.*;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.Border;
import org.netbeans.api.diff.Difference;
import org.netbeans.api.diff.StreamSource;
import org.netbeans.core.NbMainExplorer;
import org.netbeans.jemmy.util.PNGEncoder;
//import org.netbeans.modules.merge.builtin.visualizer.MergeDialogComponent;
//import org.netbeans.modules.merge.builtin.visualizer.MergePanel;
import org.netbeans.spi.diff.DiffProvider;
import org.netbeans.spi.diff.MergeVisualizer;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;


/** class observing CTRL-F12 key and launching ComponentGenerator
 * @author <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 * @version 0.1
 */
public class ComponentGeneratorRunnable implements Runnable, AWTEventListener {
    
    String packageName;
    DataFolder targetDataFolder;
    JLabel help;
    Component window,focused;
    ComponentGenerator gen;
    ComponentGeneratorPanel panel;
    boolean screenShot;
    boolean showEditor;
    boolean merge;
    // true when Component.getName() should be used to identify component
    boolean useComponentName;
    static final Border focusedBorder=BorderFactory.createLineBorder(Color.red, 1);
    Border lastBorder;
    
    /** Creates new ComponentGeneratorRunnable
     * @param screenShot boolean true when create screen shot
     * @param showEditor boolean true when show components editor
     * @param targetDataFolder target data folder
     * @param packageName String package name
     * @param panel ComponentGeneratorPanel
     * @param properties CompoenentGenerator configuration properties */
    public ComponentGeneratorRunnable(DataFolder targetDataFolder, String packageName, ComponentGeneratorPanel panel, Properties properties, boolean screenShot, boolean showEditor, boolean merge, boolean useComponentName) {
        this.targetDataFolder = targetDataFolder;
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
        this.useComponentName = useComponentName;
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
    public synchronized void eventDispatched(AWTEvent aWTEvent) {
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
            } else if ((aWTEvent instanceof KeyEvent)&&(aWTEvent.getID()==KeyEvent.KEY_RELEASED)&&(((KeyEvent)aWTEvent).getKeyCode()==KeyEvent.VK_F12)&&((((KeyEvent)aWTEvent).getModifiers()&KeyEvent.CTRL_MASK)!=0)) {
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
    
    private static final String oldLabel = NbBundle.getMessage(ComponentGeneratorRunnable.class, "LBL_OldComponent"); // NOI18N
    private static final String newLabel = NbBundle.getMessage(ComponentGeneratorRunnable.class, "LBL_NewComponent"); // NOI18N
    private static final String resultLabel = NbBundle.getMessage(ComponentGeneratorRunnable.class, "LBL_ResultOfMerge"); // NOI18N
    
    void mergeConflicts(File f, ComponentGenerator gen, final FileObject fo) {
        final StreamSource s1 = new ComponentSource(f, oldLabel);
        final StreamSource s2 = new ComponentSource(f.getName(), gen.getComponentCode(), newLabel);
        final StreamSource s3 = new ComponentSource(f, resultLabel);
        final DiffProvider diff = (DiffProvider)Lookup.getDefault().lookup(DiffProvider.class);
        final MergeVisualizer merge = (MergeVisualizer)Lookup.getDefault().lookup(MergeVisualizer.class);
        // must be called from AWT event queue
        Mutex.EVENT.writeAccess(new Runnable() {
            public void run () {
                try {
                    Component c = merge.createView(diff.computeDiff(s1.createReader(), s2.createReader()), s1, s2, s3);
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
        });
        /* MergeDialogComponent is not public anymore, but it should work as before
        if (c instanceof MergeDialogComponent) {
            ((MergeDialogComponent)c).getSelectedMergePanel().firePropertyChange(MergePanel.PROP_CAN_BE_SAVED,false,true);
            // add listener to open result of merging
            ((MergeDialogComponent)c).addVetoableChangeListener(new VetoableChangeListener() {
                public void vetoableChange(PropertyChangeEvent propertyChangeEvent) {
                    if (MergeDialogComponent.PROP_PANEL_SAVE.equals(propertyChangeEvent.getPropertyName())) {
                        // after merged file is saved, open it in editor
                        openInEditor(fo);
                    }
                    else if (MergeDialogComponent.PROP_ALL_CLOSED.equals(propertyChangeEvent.getPropertyName())) {
                        // remove listener when merge dialog is closed
                        ((MergeDialogComponent)propertyChangeEvent.getSource()).removeVetoableChangeListener(this);
                    }
                }
            });
        }
         */
    }

    /** Opens given FileObject in source editor and selects node in explorer. 
     * It is expected that FileObject is java DataObject and has EditorCookie.
     * @param fo FileObject to be opened in source editor. It should be java
     * DataObject
     */
    private void openInEditor(FileObject fo) {
        try {
            DataObject dob = DataObject.find(fo);
            // selects node in projects view
            /* it would be nice to do it. But we are only able to find the right node.
            org.netbeans.api.project.Project p = org.netbeans.api.project.FileOwnerQuery.getOwner(fo);
            if(p != null) {
                org.netbeans.spi.project.ui.LogicalViewProvider lvp = (org.netbeans.spi.project.ui.LogicalViewProvider)p.getLookup().lookup(org.netbeans.spi.project.ui.LogicalViewProvider.class);
                org.openide.nodes.Node node = lvp.findPath(lvp.createLogicalView(), fo);
                System.out.println("NODE="+node);
            }
             */
            // open in editor
            ((EditorCookie)dob.getCookie(EditorCookie.class)).open();
        } catch (Exception e) {
            help.setText(NbBundle.getMessage(ComponentGeneratorRunnable.class, "MSG_Exception")+e.getMessage()); // NOI18N
            ErrorManager.getDefault().notify(e);
        }
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
                // lock used when creating source file
                FileLock lock = null;
                try {
                    gen.grabComponents((Container)window, packageName, showEditor, useComponentName);
                    BufferedImage shot=null;
                    if (screenShot) {
                        shot = new Robot().createScreenCapture(new Rectangle(window.getLocationOnScreen(),window.getSize()));
                    }
                    String directory = FileUtil.toFile(targetDataFolder.getPrimaryFile()).getAbsolutePath();
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
                            FileObject fo = targetDataFolder.getPrimaryFile().getFileObject(gen.getClassName()+".java"); // NOI18N
                            mergeConflicts(file, gen, fo);
                        } else {
                            FileObject fo = targetDataFolder.getPrimaryFile().createData(gen.getClassName()+".java");
                            lock = fo.lock();
                            out = new PrintStream(fo.getOutputStream(lock));
                            // write generated source to a stream
                            out.println(gen.getComponentCode());
                            out.close();
                            lock.releaseLock();
                            // open FileObject representing generated source code
                            // in source editor
                            openInEditor(fo);
                        }
                        if (screenShot) {
                            new PNGEncoder(new FileOutputStream(directory+"/"+gen.getClassName()+".png")).encode(shot); // NOI18N
                            // refresh target data folder
                            targetDataFolder.getPrimaryFile().refresh();
                        }
                        help.setText(NbBundle.getMessage(ComponentGeneratorRunnable.class, "MSG_Finished")+gen.getClassName()); // NOI18N
                    } else {
                        help.setText(NbBundle.getMessage(ComponentGeneratorRunnable.class, "MSG_Canceled")); // NOI18N
                    }
                } catch (Exception e) {
                    help.setText(NbBundle.getMessage(ComponentGeneratorRunnable.class, "MSG_Exception")+e.getMessage()); // NOI18N
                    ErrorManager.getDefault().notify(e);
                } finally {
                    // release lock for sure
                    if(lock != null && lock.isValid()) {
                        lock.releaseLock();
                    }
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
        private String title;
        
        ComponentSource(String name, String content, String title) {
            this.file = null;
            this.content = content;
            this.name = name;
            this.title = title;
        }
        
        ComponentSource(File file, String title) {
            this.file = file;
            this.content = null;
            this.name = file.getName();
            this.title = title;
        }
        
        public String getName() {
            return name;
        }
        
        public String getTitle() {
            return title;
        }
        
        public String getMIMEType() {
            return "text/x-java"; // NOI18N
        }
        
        public Reader createReader() throws IOException {
            if (file != null) return new BufferedReader(new FileReader(file));
            return new StringReader(content);
        }
        
        public Writer createWriter(Difference[] conflicts) throws IOException {
            if (file==null) return null;
            if (conflicts == null || conflicts.length == 0) {
                return new FileWriter(file);
            } else {
                return new MergeConflictFileWriter(file, conflicts);
            }
        }
        
        public void notifyClosed() {
        }
        
    }
    
    private static final String leftLabel = NbBundle.getMessage(ComponentGeneratorRunnable.class, "LBL_Left"); // NOI18N
    private static final String delimiter = NbBundle.getMessage(ComponentGeneratorRunnable.class, "LBL_Delimiter"); // NOI18N
    private static final String rightLabel = NbBundle.getMessage(ComponentGeneratorRunnable.class, "LBL_Right"); // NOI18N
    
    private static class MergeConflictFileWriter extends FileWriter {
        
        private Difference[] conflicts;
        private int lineNumber;
        private int currentConflict;
        
        public MergeConflictFileWriter(File file, Difference[] conflicts) throws IOException {
            super(file);
            this.conflicts = conflicts;
            this.lineNumber = 1;
            this.currentConflict = 0;
            if (lineNumber == conflicts[currentConflict].getFirstStart()) {
                writeConflict(conflicts[currentConflict]);
                currentConflict++;
            }
        }
        
        public void write(String str) throws IOException {
            super.write(str);
            lineNumber += numLines(str);
            if (currentConflict < conflicts.length && lineNumber >= conflicts[currentConflict].getFirstStart()) {
                writeConflict(conflicts[currentConflict]);
                currentConflict++;
            }
        }
        
        private void writeConflict(Difference conflict) throws IOException {
            super.write(leftLabel);
            super.write(conflict.getFirstText());
            super.write(delimiter);
            super.write(conflict.getSecondText());
            super.write(rightLabel);
        }
        
        private static int numLines(String str) {
            int n = 0;
            for (int pos = str.indexOf('\n'); pos >= 0 && pos < str.length(); pos = str.indexOf('\n', pos + 1)) n++;
            return n;
        }
    }
}

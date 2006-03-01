/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.pkgbrowser.explorer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.text.html.HTMLEditorKit;
import org.netbeans.jmi.javamodel.ClassMember;
import org.netbeans.jmi.javamodel.Element;
import org.netbeans.jmi.javamodel.Resource;
import org.netbeans.modules.javacore.internalapi.JavaMetamodel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.propertysheet.PropertySheetView;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;

/**
 * Shows javadoc, properties and source text (if available) in a three way
 * split pane, fetching the Element to display from the selected node of an
 * ancestor ExplorerManager.Provider's ExplorerManager.
 *
 * @author Timothy Boudreau
 */
public class Details extends JPanel implements PropertyChangeListener {
    private final JSplitPane outer = 
            new JSplitPane (JSplitPane.VERTICAL_SPLIT);
    
    private final JSplitPane inner = 
            new JSplitPane (JSplitPane.VERTICAL_SPLIT);
    
    private final JEditorPane javadoc = new JEditorPane(); //NOI18N
    private final JEditorPane source = new JEditorPane("text/x-java", ""); //NOI18N
    private final RequestProcessor rp;
    private Object lock = new Object();
    private RequestProcessor.Task task = null;
    
    public Details(RequestProcessor rp) {
        this.rp = rp;
        setLayout (new BorderLayout());
        add (outer, BorderLayout.CENTER);
        outer.setBottomComponent(inner);
        
        JScrollPane sourceScroll = new JScrollPane(source);
        JScrollPane javadocScroll = new JScrollPane(javadoc);
        
        //sigh...
        Border b = BorderFactory.createEmptyBorder();
        sourceScroll.setViewportBorder(b);
        sourceScroll.setBorder(b);
        javadocScroll.setViewportBorder(b);
        javadocScroll.setBorder(b);
        outer.setBorder (b);
        inner.setBorder (b);
        
        //Avoid an NPE from HTMLEditorKit which can't handle being set on
        //an empty JEditorPane...sigh some more...setting the editor kit
        //will clobber this text anyway...
        javadoc.setText ("<html><body>No content</body></html>"); //NOI18N
        
        //Explicitly set the editor kit or we'll get an editor kit for 
        //editing, not viewing, html
        javadoc.setEditorKit(new HTMLEditorKit());
        
        javadoc.setEditable(false);
        source.setEditable(false);
        
        source.getCaret().setBlinkRate(0);
        
        outer.setTopComponent (sourceScroll);
        inner.setBottomComponent(javadocScroll);
        inner.setTopComponent(new PropertySheetView());
    }

    private ExplorerManager.Provider provider = null;
    public void addNotify() {
        super.addNotify();
        provider = (ExplorerManager.Provider) 
            SwingUtilities.getAncestorOfClass(ExplorerManager.Provider.class, 
            this);
        
        if (provider != null) {
            System.err.println("Now listening to provider");
            provider.getExplorerManager().addPropertyChangeListener(this);
        }
    }
    
    public void removeNotify() {
        super.removeNotify();
        if (provider != null) {
            provider.getExplorerManager().removePropertyChangeListener(this);
            setNode (null);
            provider = null;
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        String nm = evt.getPropertyName();
        if (ExplorerManager.PROP_ROOT_CONTEXT.equals(nm)) {
            setNode (null);
        } else if (ExplorerManager.PROP_SELECTED_NODES.equals(nm)) {
            Node[] n = provider == null ? 
                new Node[0] : provider.getExplorerManager().getSelectedNodes();
            if (n.length == 1) {
                setNode (n[0]);
            } else {
                setNode (null);
            }
        }
    }
    
    private void setNode (Node n) {
        Element el = n == null ? null : (Element) n.getLookup().lookup (
                Element.class);
        
        if (el == null && provider instanceof JComponent) {
            //Try to find an element on the selected class if one can't
            //be found on the selected node
            ExplorerManager.Provider ancProvider = (ExplorerManager.Provider)
                    SwingUtilities.getAncestorOfClass(
                        ExplorerManager.Provider.class,
                        (Component) provider);
            
            if (ancProvider != null) {
                Node[] n1 = ancProvider.getExplorerManager().getSelectedNodes();
                if (n1.length == 1) {
                    el = (Element) n1[0].getLookup().lookup (Element.class);
                }
            }
        }
        setElement (el instanceof ClassMember ? (ClassMember) el : null);
    }
    
    private void setElement (ClassMember el) {
        setData ("", ""); //NOI18N
        if (el != null) {
            synchronized (lock) {
                if (task != null) {
                    task.cancel();
                }
                task = rp.post (new Gatherer(el));
            }
        } else {
            synchronized (lock) {
                if (task != null) {
                    task.cancel();
                }
                task = null;
            }
        }
    }
    
    private void setData (String sourceText, String javadocText) {
        source.setText (sourceText);
        javadoc.setText (javadocText);
    }
    
    private final class Gatherer implements Runnable {
        private final ClassMember member;
        private String javadocText;
        private String sourceText;
        
        Gatherer (ClassMember member) {
            this.member = member;
        }

        private int ct = 0;
        public void run() {
            if (EventQueue.isDispatchThread()) {
                setData (getSourceText(), getJavadocText());
            } else {
                ct++;
                assert ct == 1 : "Task run more than once! " + ct;
                
//                assert javadocText == null : "Run twice"; //NOI18N
//                assert source == null : "Run twice"; //NOI18N
//                if (javadocText != null || sourceText != null) {
//                    System.err.println("HEY! " + javadocText + ":" + sourceText);
//                }
                if (Thread.interrupted()) {
                    return;
                }
                gatherData();
                if (Thread.interrupted()) {
                    return;
                }
                synchronized (lock) {
                    task = null;
                }
                EventQueue.invokeLater(this);
            }
        }
        
        private void gatherData() {
            JavaMetamodel.getManager().waitScanFinished();
            JavaMetamodel.getDefaultRepository().beginTrans(false); 
            try {
                javadocText = member.getJavadocText();
                try {
                    int start = member.getStartOffset();
                    int end = member.getEndOffset();
                    if (start != end) {
                        Resource r = member.getResource();
                        String fullText = r.getSourceText();
                        if (fullText != null) {
                            sourceText = fullText.substring(start, end);
                        }
                    }
                } catch (NullPointerException npe) {
                    //oh how cute, control flow by NPE...
                    //do nothing - this is how member communicates to us that
                    //it has no source associated with it...yippee...
                }
            } finally {
                JavaMetamodel.getDefaultRepository().endTrans();
            }
            System.err.println("MEMBER: " + member + " javadoc " + 
                    javadocText + " source " + sourceText);
        }            
        
        public String getJavadocText() {
            return javadocText == null ? "" : javadocText; //NOI18N
        }
        
        public String getSourceText() {
            return sourceText == null ? "" : sourceText; //NOI18N
        }
    }
}

/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.gui.nb;

import java.awt.BorderLayout;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.latex.gui.Editor;
import org.netbeans.modules.latex.gui.NodeStorage;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.openide.ErrorManager;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Node;
import org.openide.text.NbDocument;
import org.openide.util.Utilities;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

public class VauElementTopComponent extends TopComponent implements PropertyChangeListener {
    private NodeStorage storage;
    private SourcePosition start;
    private SourcePosition end;
    private Editor         editor;
    
    private VauElementTopComponent() {
    }
    
//    public void writeExternal(ObjectOutput oo) throws IOException {
//        super.writeExternal(oo);
//        oo.writeBoolean(closed);
//        oo.writeObject(start);
//    }
//    
//    public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {
//        super.readExternal(oi);
//        
//        if (closed)
//            throw new IOException("The component is closed!");
//        
//        Thread.dumpStack();
//        
//        start = (SourcePosition) oi.readObject();//TODO: this is kind of "hack", it should be generally done much better.
//        
//        
//        StructuralElement d = Model.getDefault().getModel(DataObject.find((FileObject) start.getFile()));
//        
//        Queue q = new Queue();
//        
//        q.put(d);
//        
//        System.err.println("start=" + start);
//        
//        while (!q.empty()) {
//            Element el = (Element) q.pop();
//            
//            System.err.println("el=" + el);
//            
//            if (el instanceof VauElementImpl) {
//                VauElementImpl vel = (VauElementImpl) el;
//                
//                System.err.println("vel=" + vel + ", start=" + vel.getStart());
//                
//                System.err.println("?:" + vel.getStart().getFile().equals(start.getFile()));
//                if (vel.getStart().equals(start)) {
//                    initialize(vel);
//                    
//                    return ;
//                }
//            }
//            
//            if (el instanceof CompoundElement) {
//                CompoundElement cel = (CompoundElement) el;
//                Iterator it = cel.getSubElements().iterator();
//                
//                while (it.hasNext()) {
//                    q.put(it.next());
//                }
//            }
//        }
//        
//        throw new IOException("No appropriate Vaucanson code found for component: " + this);
//    }
    
    private void initialize(VauStructuralElement el) {
        storage = el.getStorage();
        start   = el.getStart();
        end     = el.getEnd();
        editor = new Editor(storage);
        JScrollPane  spane = new JScrollPane(editor);
        
        setLayout(new BorderLayout());
        add(spane, BorderLayout.CENTER);
        
        setName(el.getCaption());
        
        editor.addPropertyChangeListener(this);
        positionToComponent.put(start, this);
        
        setIcon(Utilities.loadImage("org/netbeans/modules/latex/resource/autedit_icon.gif"));
    }
    
    private VauElementTopComponent(VauStructuralElement el) {
        initialize(el);
    }
    
    private static Map positionToComponent = new HashMap();
    
    public static VauElementTopComponent openComponentForElement(VauStructuralElement el) {
        System.err.println("positionToComponent=" + positionToComponent);
        VauElementTopComponent tc = (VauElementTopComponent) positionToComponent.get(el.getStart());
        
        System.err.println("tc=" + tc);
        if (tc == null) {
            tc = new VauElementTopComponent(el);
            tc.open();
        }
        
        System.err.println("done.");
        System.err.println("positionToComponent=" + positionToComponent);
        tc.requestActive();
        return tc;
    }
    
    private boolean closed = false;
    public void componentClosed() {
        System.err.println("positionToComponent=" + positionToComponent);
        positionToComponent.remove(start);
        System.err.println("removed.");
        System.err.println("positionToComponent=" + positionToComponent);
        synchronize();
        unsetGuarded();
        closed = true;
    }
    
    public void componentOpened() {
        setGuarded();
    }
    
    private void setGuarded() {
        try {
            StyledDocument sdoc = (StyledDocument) org.netbeans.modules.latex.model.Utilities.getDefault().openDocument(start.getFile());
            
            NbDocument.markGuarded(sdoc, start.getOffsetValue(), end.getOffsetValue() - start.getOffsetValue());
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    private void unsetGuarded() {
        try {
            StyledDocument sdoc = (StyledDocument) org.netbeans.modules.latex.model.Utilities.getDefault().openDocument(start.getFile());
            
            NbDocument.unmarkGuarded(sdoc, start.getOffsetValue(), end.getOffsetValue() - start.getOffsetValue());
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    public void componentDeactivated() {
        if (!closed)
            synchronize();
    }
    
    
    /*On Windows, common PrintWriter print \n\r as a newline. The NB Document
     *should contain only \n (on all platforms). The method synchronize uses output
     *from a PrintWriter as text that is put into a document, and therefore there
     *is a conflict (the length
     *of the <i>code</i> string with \n\r is not correct). The "temporary" workaround
     *is to overwrite the PrintWriter's println method to print '\n' on all platforms.
     *In the JDK's PrintWriter, each "println(something)" method calls
     *println to print the newline character(s) (described in the Javadoc and verified
     *in the code. It should be compatible in future releases.
     */
    private static class SimpleNewLinePrintWriter extends PrintWriter {
        public SimpleNewLinePrintWriter(Writer w) {
            super(w);
        }
        
        public void println() {
            write('\n');
        }
    }
    
    public void synchronize() {
        StringWriter str = new StringWriter();
        PrintWriter pw = new SimpleNewLinePrintWriter(str);
        
        storage.outputVaucansonSource(pw);
        pw.close();
        final String code = str.toString();
        try {
            final Document doc = org.netbeans.modules.latex.model.Utilities.getDefault().openDocument(start.getFile());
            DataObject od = DataObject.find((FileObject) start.getFile());
            boolean wasModified = od.isModified();
            NbDocument.runAtomic((StyledDocument) doc, new Runnable() {
                public void run() {
                    try {
                        unsetGuarded();
                        System.err.println("start=" + start.getOffsetValue());
                        System.err.println("end=" + end.getOffsetValue());
                        System.err.println("codelen=" + code.length());
                        doc.insertString(start.getOffsetValue() + 1, code, null);
                        System.err.println("start=" + start.getOffsetValue());
                        System.err.println("end=" + end.getOffsetValue());
                        System.err.println("codelen=" + code.length());
                        doc.remove(start.getOffsetValue() + code.length(), end.getOffsetValue() - start.getOffsetValue() - code.length());
                        doc.remove(start.getOffsetValue(), 1);
                        setGuarded();
                    } catch (BadLocationException e) {
                        ErrorManager.getDefault().notify(e);
                    }
                }
            });
            if (!wasModified) {
                SaveCookie c = (SaveCookie) od.getCookie(SaveCookie.class);
                
                if (c != null)
                    c.save();
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
        //            element.synchronize();
    }
    
    public void open() {
        dockIfNeeded();
        
        //            boolean modeVisible = false;
        //            TopComponent[] tcArray = editorMode(realWorkspace).getTopComponents();
        //            for (int i = 0; i < tcArray.length; i++) {
        //                if (tcArray[i].isOpened(realWorkspace)) {
        //                    modeVisible = true;
        //                    break;
        //                }
        //            }
        //            if (!modeVisible) {
        //                openOtherEditors(realWorkspace);
        //            }
        super.open();
    }
    
    /** Dock this top component to editor mode if it is not docked
     * in some mode at this time  */
    private void dockIfNeeded() {
        // dock into editor mode if possible
        Mode ourMode = WindowManager.getDefault().findMode(this);
        
        if (ourMode == null) {
            editorMode().dockInto(this);
        }
    }
    
    private Mode editorMode() {
        //XXX:
        return WindowManager.getDefault().findMode("editor");
//        Mode ourMode = WindowManager.getDefault().findMode(this);
//        if (ourMode == null) {
//            ourMode = WindowManager.getDefault().createMode(
//            CloneableEditorSupport.EDITOR_MODE, getName(),
//            CloneableEditorSupport.class.getResource(
//            "/org/openide/resources/editorMode.gif" // NOI18N
//            )
//            );
//        }
//        return ourMode;
    }
    
    private static Map vauNode2BeanNode = new WeakHashMap();
    private static synchronized BeanNode findNodeForVauNode(Object node) {
        BeanNode bn = (BeanNode) vauNode2BeanNode.get(node);
        
        if (bn == null) {
            try {
            bn = new BeanNode(node);
            vauNode2BeanNode.put(node, bn);
            } catch (IntrospectionException e) {
                IllegalArgumentException exc = new IllegalArgumentException();
                
                ErrorManager.getDefault().annotate(exc, e);
                ErrorManager.getDefault().annotate(exc, e.getMessage());
                
                throw exc;
            }
        }
        
        return bn;
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (Editor.PROP_SELECTION.equals(evt.getPropertyName())) {
            List     selected = editor.getSelection();
            Node[]   result   = new Node[selected.size()];
            
            for (int cntr = 0; cntr < selected.size(); cntr++) {
                result[cntr] = findNodeForVauNode(selected.get(cntr));
                result[cntr].addPropertyChangeListener(this);
            }
            
            setActivatedNodes(result);
        }
    }

    /**
     * Overwrite when you want to change default persistence type. Default
     * persistence type is PERSISTENCE_ALWAYS.
     * Return value should be constant over a given TC's lifetime.
     * @return one of P_X constants
     * @since 4.20
     */
    public int getPersistenceType() {
        return PERSISTENCE_NEVER;
    }
    
}

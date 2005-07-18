/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.errorstripe;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.text.Document;
import org.netbeans.modules.editor.errorstripe.privatespi.Mark;
import org.netbeans.modules.editor.errorstripe.privatespi.MarkProvider;
import org.netbeans.modules.latex.model.Queue;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.structural.Model;
import org.netbeans.modules.latex.model.structural.ModelListener;
import org.netbeans.modules.latex.model.structural.PositionCookie;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.netbeans.modules.latex.model.structural.StructuralNodeFactory;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXStructureMarkProvider extends MarkProvider implements ModelListener {
    
    private Document document;
    private Map/*<StructuralElement, Mark>*/ element2Mark;
    private List currentMarks;
    
    /** Creates a new instance of elementMarkProvider */
    public LaTeXStructureMarkProvider(Document document) {
        this.document = document;
        this.element2Mark = new WeakHashMap();
        this.currentMarks = createMarks();
        
        Model model = Model.getDefault();
        DataObject stream = (DataObject) document.getProperty(Document.StreamDescriptionProperty);
        
        if (stream != null) {
            FileObject file = stream.getPrimaryFile();
            
            model.addModelListener(file, this);
        }
    }

    private synchronized Mark getMarkFor(StructuralElement element) {
        Mark mark = (Mark) element2Mark.get(element);
        
        if (mark == null) {
            mark = new LaTeXStructureMark(element);
            
            element2Mark.put(element, mark);
        }
        
        return mark;
    }

    private List createMarks() {
        List marks = new ArrayList();
        Model model = Model.getDefault();
        DataObject stream = (DataObject) document.getProperty(Document.StreamDescriptionProperty);
        
        if (stream == null)
            return Collections.EMPTY_LIST;
        
        FileObject file = stream.getPrimaryFile();
        StructuralElement root = model.getModel(file);
        Queue q = new Queue();
        
        q.put(root);
        
        while (!q.empty()) {
            StructuralElement element = (StructuralElement) q.pop();
            Node n = StructuralNodeFactory.createNode(element);
            PositionCookie pc = (PositionCookie) n.getCookie(PositionCookie.class);
            SourcePosition position = pc.getPosition();
            
            if (position != null && position.getFile() == file && LaTeXStructureMark.priority2Color.get(new Integer(element.getPriority())) != null) {
                marks.add(getMarkFor(element));
            }
            
            q.putAll(element.getSubElements());
        }
        
        return marks;
    }
    
    public synchronized List/*<Mark>*/ getMarks() {
        return currentMarks;
    }

    public void modelChanged(FileObject mainFile) {
        List old = this.currentMarks;
        
        this.currentMarks = createMarks();
        
        firePropertyChange("marks", old, this.currentMarks);
    }

    
}

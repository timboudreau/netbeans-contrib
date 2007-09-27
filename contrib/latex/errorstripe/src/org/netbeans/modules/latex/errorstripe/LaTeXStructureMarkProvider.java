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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.structural.PositionCookie;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.netbeans.modules.latex.model.structural.StructuralNodeFactory;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;

/**XXX
 *
 * @author Jan Lahoda
 */
public class LaTeXStructureMarkProvider extends MarkProvider  {
    
    private Document document;
    private Map/*<StructuralElement, Mark>*/ element2Mark;
    private List currentMarks;
    
    /** Creates a new instance of elementMarkProvider */
    public LaTeXStructureMarkProvider(Document document) {
        this.document = document;
        this.element2Mark = new WeakHashMap();
        this.currentMarks = createMarks();
        
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
        //XXX:
//        Model model = Model.getDefault();
//        DataObject stream = (DataObject) document.getProperty(Document.StreamDescriptionProperty);
//        
//        if (stream == null)
//            return Collections.EMPTY_LIST;
//        
//        FileObject file = stream.getPrimaryFile();
//        StructuralElement root = model.getModel(file);
//        Queue q = new Queue();
//        
//        q.put(root);
//        
//        while (!q.empty()) {
//            StructuralElement element = (StructuralElement) q.pop();
//            Node n = StructuralNodeFactory.createNode(element);
//            PositionCookie pc = (PositionCookie) n.getCookie(PositionCookie.class);
//            SourcePosition position = pc.getPosition();
//            
//            if (position != null && position.getFile() == file && LaTeXStructureMark.priority2Color.get(new Integer(element.getPriority())) != null) {
//                marks.add(getMarkFor(element));
//            }
//            
//            q.putAll(element.getSubElements());
//        }
        
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

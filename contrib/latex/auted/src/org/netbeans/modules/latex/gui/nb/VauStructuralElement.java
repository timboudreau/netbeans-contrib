/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.gui.nb;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import org.netbeans.modules.latex.gui.NodeStorage;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.DefaultTraverseHandler;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.structural.StructuralElement;

/**
 *
 * @author Jan Lahoda
 */
public class VauStructuralElement extends StructuralElement {
    
    private static final String UNNAMED = "Unnamed automaton";
    private NodeStorage storage;
    private boolean     valid;
    private boolean     addErrors = true;
    private String      caption;
    
    private SourcePosition start;
    private SourcePosition end;
    
    private static Map  store = new HashMap();
    public static VauStructuralElement findElementForPosition(SourcePosition pos) {
        WeakReference ref = (WeakReference) store.get(pos); //TODO:some compacting, or generally better solution.
        
        if (ref == null)
            return null;
        
        VauStructuralElement el = (VauStructuralElement) ref.get();
        
        if (el == null)
            store.remove(pos);
        
        return el;
    }
    
    /** Creates a new instance of VauElementImpl */
    public VauStructuralElement(CommandNode node, Collection errors) {
        Collection iErrors = new ArrayList();
        this.storage = new VauParserImpl().parse(node, iErrors);
        
        if (iErrors.size() != 0) {
            valid = false;
            //XXX: commented out:
//            errors.add(Utilities.getDefault().createError("Error(s) occured during sematic parsing of figure.", node.getStartingPosition()));
            if (addErrors)
                errors.addAll(iErrors);
        } else {
            valid = true;
        }
        start = node.getStartingPosition();
        end = node.getEndingPosition();
        
        findCaption(node);
        store.put(getStart(), new WeakReference(this));
    }
    
    public NodeStorage getStorage() {
        return storage;
    }
    
    public SourcePosition getStart() {
        return start;
    }
    
    public SourcePosition getEnd() {
        return end;
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public String getCaption() {
        return caption;
    }
    
//    public void synchronize() {
//        
//    }
//    
    private void findCaption(CommandNode node) {
        caption = Utilities.getDefault().findCaptionForNode(node);
        
        if (caption == null)
            caption = UNNAMED;
        
//        Node parent = node.getParent();
//        caption = null;
//        
//        while (parent != null) {
//            if (parent instanceof BlockNode) {
//                parent.traverse(new DefaultTraverseHandler() {
//                    public boolean argumentStart(ArgumentNode arg) {
//                        if (arg.getArgument().hasAttribute("#caption")) {
//                            caption = arg.getText().toString();
//                        }
//                        
//                        return false;
//                    }
//                });
//                
//                if (caption != null)
//                    return ;
//            }
//            parent = parent.getParent();
//        }
//        
//        caption = UNNAMED;
    }
    
    public int getPriority() {
        return 50000;
    }
    
}

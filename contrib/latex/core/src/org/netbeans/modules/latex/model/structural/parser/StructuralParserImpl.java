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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
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
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.structural.parser;

import java.io.IOException;
import java.util.*;
import org.netbeans.modules.latex.model.Queue;
import org.netbeans.modules.latex.model.command.*;
import org.netbeans.modules.latex.model.structural.DelegatedParser;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.netbeans.modules.latex.model.structural.label.LabelStructuralElement;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.FolderLookup;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;
import org.openide.util.Lookup.Template;


/**
 *
 * @author Jan Lahoda
 */
public final class StructuralParserImpl {
    
    private Map oldElementsMap;
    private MainStructuralElement mainElement;
    
    /** Creates a new instance of StructuralParser */
    public StructuralParserImpl() {
        oldElementsMap = new HashMap();//MEMORY LEAK!
        mainElement = new MainStructuralElement();
    }
    
    public StructuralElement parse(DocumentNode dn, List<ErrorDescription> errors) {
        List<DelegatedParser> parsers = getDelegatedParsers();
        
        for (DelegatedParser p : parsers) {
            p.reset();
        }
        
        mainElement.clearSubElements();
        mainElement.clearLabels();
        dn.traverse(new ParsingTraverseHandler(mainElement, parsers));
        
        fireSubElementsChanged(mainElement);
        
        for (DelegatedParser p : parsers) {
            errors.addAll(p.getErrors());
        }
        
        for (DelegatedParser p : parsers) {
            p.parsingFinished();
        }
        
        return mainElement;
    }
    
    private void fireSubElementsChanged(StructuralElement el) {
        Queue q = new Queue();
        
        q.put(el);
        
        while (!q.empty()) {
            StructuralElement element = (StructuralElement) q.pop();
            
            element .fireSubElementsChange();
            
            q.putAll(element.getSubElements());
        }
    }
    
    private static synchronized List<DelegatedParser> getDelegatedParsers() {
        FileObject parsersFolder = Repository.getDefault().getDefaultFileSystem().findResource("latex/structural/parsers");
        
        try {
//            System.err.println("parsersFolder = " + parsersFolder );
            DataObject od            = DataObject.find(parsersFolder);
            
//            System.err.println("od = " + od );
            if (od instanceof DataFolder) {
                FolderLookup flookup = new FolderLookup((DataFolder) od);
                
                flookup.run();
                
                Lookup l = flookup.getLookup();
//                System.err.println("l = " + l );
                Result result = l.lookup(new Template(DelegatedParser.class));
                
//                System.err.println("result = " + result );
//                System.err.println(result.allInstances());
                return new ArrayList(result.allInstances());
            }
        } catch (IOException e) {
//            System.err.println("1");
            ErrorManager.getDefault().notify(e);
        }
//        System.err.println("2");
        return Collections.EMPTY_LIST;
    }
    
//    public void addElement(StructuralElement el) {
//    }
//    
    private class ParsingTraverseHandler extends TraverseHandler {
        
        private Stack             elements;
        private List              parsers;
        private MainStructuralElement main;
        
        public ParsingTraverseHandler(MainStructuralElement mainElement, List parsers) {
            elements = new Stack();
            elements.push(mainElement);
            this.main = mainElement;
            
            this.parsers = parsers;
        }
        
        private void addElement(StructuralElement el) {
            while (((StructuralElement) elements.peek()).getPriority() >= el.getPriority())
                elements.pop();
            
            ((StructuralElement) elements.peek()).addSubElement(el);
            elements.push(el);

            if (el instanceof LabelStructuralElement) {
                //                        System.err.println("label...");
                main.addLabel((LabelStructuralElement) el);
            }
        }
        
        private void handleNode(Node node, Attributable attributable) {
//            System.err.println("handleNode(" + node + ", " + attributable + ")");
            for (Iterator i = parsers.iterator(); i.hasNext(); ) {
                DelegatedParser parser = (DelegatedParser) i.next();
                String[]   attributes = parser.getSupportedAttributes();
                boolean    accepts = false;
                
                for (int cntr = 0; cntr < attributes.length; cntr++) {
                    if (attributable.hasAttribute(attributes[cntr])) {
                        accepts = true;
                        break;
                    }
                }
                
                if (!accepts)
                    continue ;
                
                StructuralElement el = null;
                
                Object key = parser.getKey(node);
                
                if (key != null) {
                    StructuralElement oldEl = (StructuralElement) oldElementsMap.get(key);
                    
                    if (oldEl != null) {
                        el = parser.updateElement(node, oldEl);
                        
                        if (el != null) {
                            el.clearSubElements();//This is the only legal place to do this!!!!
                        }
                        
//                        if (el != oldEl) {
//                            System.err.println("A new element created for:");
//                            System.err.println("oldEl = " + oldEl );
//                            System.err.println("el = " + el );
//                            System.err.println("node = " + node );
//                        }
                    } else {
                        el = parser.getElement(node);
                    }
                    
                    if (el != null)
                        oldElementsMap.put(key, el);
                } else {
                    el = parser.getElement(node);
                }
                
                
                if (el != null) {
                    addElement(el);
                }
            }
        }
        
        public void commandEnd(CommandNode node) {
        }
        
        public void argumentEnd(ArgumentNode node) {
        }
        
        public boolean argumentStart(ArgumentNode node) {
            handleNode(node, node.getArgument());
            return true;
        }
        
        public void blockEnd(BlockNode node) {
        }
        
        public boolean blockStart(BlockNode node) {
            if (node.getEnvironment() != null)
                handleNode(node, node.getEnvironment());
            return true;
        }
        
        public boolean commandStart(CommandNode node) {
            handleNode(node, node.getCommand());
            return true;
        }
        
    }
    
}
